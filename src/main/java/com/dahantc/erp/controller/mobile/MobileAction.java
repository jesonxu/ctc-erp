package com.dahantc.erp.controller.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.businessCard.BusinessCardInfo;
import com.dahantc.erp.util.businessCard.ScanBusinessCardUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.customer.CustomerInfoDto;
import com.dahantc.erp.dto.role.RoleInfoDto;
import com.dahantc.erp.enums.LogType;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * 移动端action(主要处理首页)
 *
 * @author 8520
 */
@Controller
@RequestMapping(path = "/mobile")
public class MobileAction extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(MobileAction.class);

	/**
	 * 客户
	 */
	private ICustomerService customerService;

	/**
	 * 供应商
	 */
	private ISupplierService supplierService;
	/**
	 * 名片扫描工具类
	 */
	private ScanBusinessCardUtil scanBusinessCardUtil;

	/**
	 * 访问移动端的首页（登录进来的首页）
	 */
	@RequestMapping(path = "/toIndex")
	public String toIndex(@RequestParam(name = "userRoleId", required = false) String roleId) {
		String language = getLanguage();
		request.setAttribute("lang", language);
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return "/views/login/login";
			}
			User user = onlineUser.getUser();
			if (user != null) {
				if (StringUtil.isBlank(roleId)) {
					roleId = onlineUser.getRoleId();
				}
				List<Role> roles = roleService.findUserAllRole(user.getOssUserId());
				if (roles == null || roles.isEmpty()) {
					cleanSession();
					return "/views/login/login";
				}
				String finalRoleId = roleId;
				List<Role> nowRole = roles.stream().filter(role -> role.getRoleid().equals(finalRoleId)).collect(Collectors.toList());
				if (!nowRole.isEmpty()) {
					roles.removeAll(nowRole);
					roles.addAll(0, nowRole);
				}
				request.setAttribute("realName", user.getRealName());
				request.setAttribute("loginName", user.getLoginName());
				request.getSession().setAttribute(Constants.ROLEID_KEY, roles.get(0).getRoleid());
				saveLog("MobileAction.login", User.class.getName(), "账号：" + user.getLoginName() + "，登陆成功", LogType.OperationLog.ordinal());
				return "/mobile/index";
			}
		} catch (Exception e) {
			logger.error("进入移动端首页失败", e);
		}
		return "/views/login/login";
	}

	/**
	 * 获取角色下拉框
	 */
	@ResponseBody
	@RequestMapping("/getUserRole")
	public BaseResponse<List<RoleInfoDto>> getSelectRole() {
		long start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("请先登录");
			}
			User user = onlineUser.getUser();
			List<Role> roleList = roleService.findUserAllRole(user.getOssUserId());
			if (roleList == null || roleList.isEmpty()) {
				return BaseResponse.error("用户没有角色");
			}
			String userRoleId = onlineUser.getRoleId();
			List<RoleInfoDto> roleInfoList = new ArrayList<>(roleList.size());
			for (int index = 0; index < roleList.size(); index++) {
				Role role = roleList.get(index);
				int isNow = 0;
				if (StringUtil.isBlank(userRoleId) && index == 0) {
					isNow = 1;
				} else if (StringUtil.isNotBlank(userRoleId) && userRoleId.equals(role.getRoleid())) {
					isNow = 1;
				}
				roleInfoList.add(new RoleInfoDto(role, isNow));
			}
			logger.info("获取用户角色成功，耗时：" + (System.currentTimeMillis() - start));
			return BaseResponse.success(roleInfoList);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return BaseResponse.error("用户角色获取异常");
	}

	/**
	 * 改变用户的角色
	 */
	@ResponseBody
	@RequestMapping("/changeUserRole")
	public BaseResponse<Boolean> changeUserRole(@RequestParam(required = false) String roleId) {
		long start = System.currentTimeMillis();
		try {
			if (StringUtil.isBlank(roleId)) {
				return BaseResponse.error("请求参数错误");
			}
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("请先登录");
			}
			User user = onlineUser.getUser();
			List<Role> roleList = roleService.findUserAllRole(user.getOssUserId());
			if (roleList == null || roleList.isEmpty()) {
				return BaseResponse.error("用户没有角色");
			}
			for (Role role : roleList) {
				if (roleId.equals(role.getRoleid())) {
					request.getSession().setAttribute(Constants.ROLEID_KEY, roleId);
					saveLog("MobileAction.changeUserRole", User.class.getName(), "账号：" + user.getLoginName() + "，切换角色成功", LogType.UserLog.ordinal());
					logger.info("用户切换角色成功，耗时：" + (System.currentTimeMillis() - start));
					return BaseResponse.success(true);
				}
			}
			logger.info("切换角色失败，耗时：" + (System.currentTimeMillis() - start));
			return BaseResponse.error("没有用户需要切换的角色");
		} catch (Exception e) {
			logger.error("用户切换角色异常", e);
		}
		return BaseResponse.error("切换角色异常");
	}

	/**
	 * 根据客户ID查询客户信息
	 */
	@ResponseBody
	@RequestMapping("/getEntityInfo")
	public BaseResponse<CustomerInfoDto> getEntityInfo(@RequestParam(required = false) String entityId) {
		long start = System.currentTimeMillis();
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (onlineUser == null) {
				return BaseResponse.error("请先登录");
			}
			if (StringUtil.isBlank(entityId)) {
				return BaseResponse.error("请求参数错误");
			}
			Customer customer = customerService.read(entityId);
			if (customer != null) {
				CustomerInfoDto info = new CustomerInfoDto();
				info.setCompanyId(customer.getCustomerId());
				info.setCompanyName(customer.getCompanyName());
				return BaseResponse.success(info);
			}
			Supplier supplier = supplierService.read(entityId);
			if (supplier != null) {
				CustomerInfoDto info = new CustomerInfoDto();
				info.setCompanyId(supplier.getSupplierId());
				info.setCompanyName(supplier.getCompanyName());
				return BaseResponse.success(info);
			}
			logger.info("根据ID查询主体信息，耗时：" + (System.currentTimeMillis() - start));
			return BaseResponse.error("没有查找到相关信息");
		} catch (Exception e) {
			logger.error("主体查询异常", e);
		}
		return BaseResponse.error("信息查询异常");
	}

	/**
	 * 加载用户的流程页面
	 *
	 * @return 流程页面
	 */
	@RequestMapping(path = "/toFlowList")
	public String toFlowList() {
		return "/mobile/flow/flowList/flowList";
	}

	@RequestMapping(path = "/toConsole")
	public String toConsole() {
		return "/mobile/console/index";
	}

	@RequestMapping(path = "/toAddreport")
	public String toAddreport(@RequestParam Integer reportType) {
		request.setAttribute("reportType", reportType);
		return "/mobile/report/addReport";
	}

	@RequestMapping(path = "/toReport")
	public String toReport() {
		request.setAttribute("userName", getOnlineUser().getRealName());
		request.setAttribute("userId", getOnlineUser().getOssUserId());
		return "/mobile/report/reportList/reportList";
	}

	/**
	 * 跳转到流程申请列表
	 */
	@RequestMapping(path = "/toApplyFlowList")
	public String toApplyFlowList() {
		return "/mobile/flow/applyFlow/applyFlowList";
	}

	/**
	 * 跳转到流程申请流程页面
	 */
	@RequestMapping(path = "/toApplyFlowDetail")
	public String toApplyFlowDetail() {
		return "/mobile/flow/applyFlow/applyFlowDetail";
	}

	/**
	 * 跳转到主体（客户、供应商）申请页面
	 */
	@RequestMapping(path = "/toEntityChoose")
	public String toEntityChoose() {
		return "/mobile/flow/applyFlow/entityChoose";
	}

	/**
	 * 跳转到产品（客户、供应商）申请页面
	 */
	@RequestMapping(path = "/toProductChoose")
	public String toProductChoose() {
		return "/mobile/flow/applyFlow/productChoose";
	}

	/**
	 * 跳转到用户添加页面
	 */
	@RequestMapping(path = "/toAddCustomer")
	public String toAddUser() {
		if (getOnlineUserAndOnther() == null) {
			return "/views/login/login";
		}
		return "/mobile/customer/addCustomer";
	}

	/**
	 * 跳转到用户列表界面
	 */
	@RequestMapping(path = "/toCustomerList")
	public String toUserList() {
		if (getOnlineUserAndOnther() == null) {
			return "/views/login/login";
		}
		return "/mobile/customer/customerList";
	}

	/**
	 * 跳转到用户列表界面
	 */
	@RequestMapping(path = "/toCustomerDetail")
	public String toCustomerDetail() {
		if (getOnlineUserAndOnther() == null) {
			return "/views/login/login";
		}
		return "/mobile/customer/customerDetail";
	}

	/**
	 * 识别名片 （一次只允许一张图片进行识别）
	 */
	@ResponseBody
	@PostMapping(path = "/scanBusinessCard")
	public BaseResponse<BusinessCardInfo> scanBusinessCard(MultipartFile file) {
		if (getOnlineUserAndOnther() == null) {
			return BaseResponse.error("请先登录");
		}
		if (file == null) {
			return BaseResponse.error("请求参数错误");
		}
		String docFileName = file.getOriginalFilename();
		if (StringUtil.isBlank(docFileName)) {
			return BaseResponse.error("请求参数错误");
		}
		try {
			byte[] uploadFileBytes = file.getBytes();
			// 文件类型
			String ext = docFileName.substring(docFileName.lastIndexOf("."));
			List<String> supportFileTypes = new ArrayList<>(Arrays.asList(".jpg", ".jpeg", ".png"));
			if (!supportFileTypes.contains(ext.toLowerCase())) {
				return BaseResponse.error("名片文件类型不支持");
			}
			String datePath = DateUtil.convert(new Date(), "yyyyMMdd");
			// 文件夹路径
			String fileFolderPaht = Constants.RESOURCE + File.separator + "businessCard" + File.separator + datePath;
			// 保存文件的文件夹路径
			File destFolder = new File(fileFolderPaht);
			if (!destFolder.exists()) {
				if (!destFolder.mkdirs()) {
					return BaseResponse.error("名片文件保存失败");
				}
			}
			// 文件保存的路径
			String fileStorePath = fileFolderPaht + File.separator + UUID.randomUUID().toString().replace("-", "") + ext;
			File storeFile = new File(fileStorePath);
			file.transferTo(storeFile);
			logger.info("上传的文件大小：" + file.getSize() + "bit");
			BaseResponse<BusinessCardInfo> cardInfoRes = scanBusinessCardUtil.scanBusinessCard(uploadFileBytes);
			if (cardInfoRes.getCode() == 200) {
				cardInfoRes.getData().setFilePath(fileStorePath);
			}
			return cardInfoRes;
		} catch (Exception e) {
			logger.error("扫描名片文件异常", e);
		}
		return BaseResponse.error("名片识别错误");
	}

	/**
	 * 查看名片
	 */
	@ResponseBody
	@GetMapping(path = "/viewBusinessCard",
			produces = {
					MediaType.IMAGE_JPEG_VALUE,
					MediaType.IMAGE_PNG_VALUE,
					MediaType.IMAGE_PNG_VALUE
			})
	public byte[] viewBusinessCard(String filePath) {
		File file = new File(filePath);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			byte[] bytes = new byte[inputStream.available()];
			int size = inputStream.read(bytes, 0, inputStream.available());
			logger.info("读取的文件大小" + size);
			return bytes;
		} catch (Exception e) {
			logger.error("读取文件异常", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error("关闭文件读取流错误", e);
			}
		}
		return null;
	}

	@RequestMapping( value = "/matchCustomer")
	public String matchCustomer() {
		return "/mobile/customer/matchCustomer";
	}

	/**
	 * 添加客户联系人
	 */
	@RequestMapping(value = "/toAddCustomerContact")
	public String toAddCustomerContact() {
		return "/mobile/contact/addCustomerContact";
	}

	/**
	 * 添加供应商联系人
	 */
	@RequestMapping(value = "/toAddSupplierContact")
	public String toAddSupplierContact() {
		return "/mobile/contact/addSupplierContact";
	}

	@Autowired
	public void setCustomerService(ICustomerService customerService) {
		this.customerService = customerService;
	}

	@Autowired
	public void setSupplierService(ISupplierService supplierService) {
		this.supplierService = supplierService;
	}

	@Autowired
	public void setScanBusinessCardUtil(ScanBusinessCardUtil scanBusinessCardUtil) {
		this.scanBusinessCardUtil = scanBusinessCardUtil;
	}
}
