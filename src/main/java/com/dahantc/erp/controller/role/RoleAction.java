package com.dahantc.erp.controller.role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.role.AddRoleReqDto;
import com.dahantc.erp.dto.role.EditRoleReqDto;
import com.dahantc.erp.dto.role.RolePageReqDto;
import com.dahantc.erp.dto.role.RolePageRespDto;
import com.dahantc.erp.enums.DataPermission;
import com.dahantc.erp.enums.DefaultMenuType;
import com.dahantc.erp.enums.MenuGroup;
import com.dahantc.erp.enums.PagePermission;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.menuItem.entity.MenuItem;
import com.dahantc.erp.vo.menuItem.service.IMenuItemService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.roledetail.entity.RoleDetail;
import com.dahantc.erp.vo.roledetail.service.IRoleDetailService;
import com.dahantc.erp.vo.user.entity.User;

@Controller
@RequestMapping("/role")
@Validated
public class RoleAction extends BaseAction {
	private static Logger logger = LogManager.getLogger(RoleAction.class);

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IMenuItemService menuItemService;

	@Autowired
	private IRoleDetailService roleDetailService;

	@RequestMapping("/toQuery")
	public String toQuery() {
		return "/views/role/role";
	}

	@RequestMapping("/toAddRolePage")
	public String toAddRolePage() {
		return "/views/role/addRole";
	}

	@RequestMapping("/toEditRolePage")
	public String toEditRolePage(Model model) {
		String roleid = request.getParameter("roleid");
		if (StringUtils.isNotBlank(roleid)) {
			try {
				Role role = roleService.read(roleid);
				model.addAttribute("roleName", role.getRolename());
				model.addAttribute("roleid", role.getRoleid());
				model.addAttribute("deptIds", role.getDeptIds());
				model.addAttribute("dataPermission", role.getDataPermission());
				return "/views/role/editRole";
			} catch (ServiceException e) {
				logger.error("查询roleid为：" + roleid + "角色失败", e);
			}
		}
		return "";
	}

	// 查询修改页面展示数据
	@RequestMapping("/editRolesData")
	@ResponseBody
	public String editRolesData() {
		String roleid = request.getParameter("roleid");
		JSONObject json = new JSONObject();
		if (StringUtils.isNotBlank(roleid)) {
			List<JSONObject> perMenus = getPerMenus(roleid);
			json.put("msg", "");
			json.put("code", 0);
			json.put("count", perMenus.size());
			json.put("is", true);
			json.put("tip", "操作成功！");
			json.put("data", perMenus);
		}
		return json.toString();
	}

	// 获取所有菜单
	private List<JSONObject> getPerMenus(String roleid) {
		logger.info("查询修改角色所需菜单数据开始");
		long start = System.currentTimeMillis();
		List<JSONObject> result = new ArrayList<>();
		if (StringUtils.isNotBlank(roleid)) {
			try {
				// 根据roleId查询roleDetail，得到角色的菜单
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("roleid", Constants.ROP_EQ, roleid));
				List<RoleDetail> roledetailList = roleDetailService.queryAllBySearchFilter(filter);
				String defaultMenuId = "";
				// 角色的菜单集合
				Set<String> menuIds = new HashSet<String>();
				if (roledetailList != null && !roledetailList.isEmpty()) {
					for (RoleDetail roleDetail : roledetailList) {
						if (roleDetail.getDefalutMenuType() == DefaultMenuType.DEFAULT.ordinal()) {
							defaultMenuId = roleDetail.getMenuid();
						}
						menuIds.add(roleDetail.getMenuid());
					}
				}
				// 查询出所有菜单
				List<MenuItem> allMenus = menuItemService.queryAllBySearchFilter(new SearchFilter());
				if (allMenus != null && !allMenus.isEmpty()) {
					sortData(allMenus);
					for (MenuItem menu : allMenus) {
						Optional<MenuGroup> enumsByCode = MenuGroup.getEnumsByCode(menu.getMenuGroup());
						String menuGroupName = "";
						if (enumsByCode.isPresent()) {
							menuGroupName = enumsByCode.get().getMsg();
						}
						JSONObject json = new JSONObject();
						json.put("id", menu.getMenuid());
						json.put("name", menuGroupName + " -> " + menu.getTitle());
						json.put("sequence", menu.getMenusequence());
						json.put("consoleType", menu.getConsoleType());
						json.put("type", menu.getMenuGroup());
						if (menuIds.contains(menu.getMenuid())) {
							if (menu.getMenuid().equals(defaultMenuId)) {
								json.put("defaultMenu", true);
							} else {
								json.put("defaultMenu", false);
							}
							json.put("status", true);
						} else {
							json.put("status", false);
						}
						result.add(json);
					}
				}
				logger.info("查询修改角色所需菜单数据结束，共耗时：" + (System.currentTimeMillis() - start));
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		return result;
	}

	// 查询添加页面展示数据
	@RequestMapping("/addRolesData")
	@ResponseBody
	public String addRolesData() {
		logger.info("查询添加角色所需菜单数据开始");
		long start = System.currentTimeMillis();
		JSONObject json = new JSONObject();
		List<JSONObject> result = new ArrayList<>();
		try {
			List<MenuItem> allMenus = menuItemService.queryAllBySearchFilter(new SearchFilter());
			sortData(allMenus);
			for (MenuItem menu : allMenus) {
				JSONObject tempjson = new JSONObject();
				tempjson.put("id", menu.getMenuid());
				Optional<MenuGroup> enumsByCode = MenuGroup.getEnumsByCode(menu.getMenuGroup());
				String menuGroupName = "";
				if (enumsByCode.isPresent()) {
					menuGroupName = enumsByCode.get().getMsg();
				}
				tempjson.put("name", menuGroupName + " -> " + menu.getTitle());
				tempjson.put("sequence", menu.getMenusequence());
				tempjson.put("status", false);
				tempjson.put("defaultMenu", false);
				tempjson.put("consoleType", menu.getConsoleType());
				tempjson.put("type", menu.getMenuGroup());
				result.add(tempjson);
			}
			json.put("msg", "");
			json.put("code", 0);
			json.put("count", result.size());
			json.put("tip", "操作成功！");
			json.put("data", result);
			logger.info("查询添加角色所需菜单数据结束，共耗时：" + (System.currentTimeMillis() - start));
		} catch (ServiceException e) {
			logger.error("查询菜单失败", e);
		}
		return json.toString();
	}

	private void sortData(List<MenuItem> menuItemList) {
		Collections.sort(menuItemList, new Comparator<MenuItem>() {
			@Override
			public int compare(MenuItem a, MenuItem b) {
				int result = a.getMenuGroup() - b.getMenuGroup();
				if (result == 0) {
					return a.getMenusequence() - b.getMenusequence();
				} else {
					return result;
				}
			}
		});
	}

	/**
	 * 查询角色信息
	 */
	@PostMapping("/readPages")
	@ResponseBody
	public BaseResponse<PageResult<RolePageRespDto>> list(@RequestBody RolePageReqDto pageReqDto) {
		PageResult<RolePageRespDto> result = new PageResult<RolePageRespDto>();
		List<RolePageRespDto> data = new ArrayList<RolePageRespDto>();
		try {
			SearchFilter filter = new SearchFilter();
			if (StringUtils.isNotBlank(pageReqDto.getRoleName())) {
				filter.getRules().add(new SearchRule("rolename", Constants.ROP_CN, pageReqDto.getRoleName()));
			}
			PageResult<Role> queryByPages = roleService.queryByPages(pageReqDto.getLimit(), pageReqDto.getPage(), filter);
			BeanUtils.copyProperties(queryByPages, result);
			List<Role> _data = queryByPages.getData();

			for (Role role : _data) {
				RolePageRespDto dto = new RolePageRespDto();
				BeanUtils.copyProperties(role, dto);
				dto.setWtime(DateUtil.convert(role.getWtime(), DateUtil.format2));
				data.add(dto);
			}
			result.setData(data);
		} catch (Exception e) {
			logger.error("分页查询角色异常", e);
			return BaseResponse.error("分页查询角色异常");
		}
		return BaseResponse.success(result);
	}

	/**
	 * 添加角色
	 */
	@PostMapping("/add")
	@ResponseBody
	public BaseResponse<String> addRole(@RequestBody @Valid AddRoleReqDto roleReqDto) throws Exception {
		User onlineUser = getOnlineUser();
		return roleService.addRole(roleReqDto, onlineUser.getOssUserId());
	}

	/**
	 * 获取要修改的角色数据
	 */
	@GetMapping("/editData")
	@ResponseBody
	public BaseResponse<?> getEditData(@NotBlank(message = "角色id不能为空") @RequestParam("roleId") String roleId) {
		return BaseResponse.success();
	}

	/**
	 * 角色修改
	 */
	@PostMapping("/edit")
	@ResponseBody
	public BaseResponse<String> editRole(@RequestBody @Valid EditRoleReqDto roleReqDto) throws Exception {
		return roleService.updateRole(roleReqDto);
	}

	/**
	 * 获取角色的页面权限
	 * 
	 * @param type
	 *            工作台类型(多个用,分隔)
	 * @param roleid
	 *            角色id
	 * @return
	 */
	@RequestMapping("/getPagePermission")
	@ResponseBody
	public String getPagePermission(String type, String roleid) {
		JSONArray array = new JSONArray();
		try {
			Map<String, Boolean> rolePermission = new HashMap<>();
			if (StringUtils.isNotBlank(roleid)) {
				Role role = roleService.read(roleid);
				if (role != null) {
					rolePermission = role.getPagePermissionMap(); // 该角色以前的权限
				}
			}
			Set<String> keys = rolePermission.entrySet().stream().filter(Entry::getValue).map(Entry::getKey).collect(Collectors.toSet());
			if (StringUtils.isNotBlank(type)) { // 根据开启的工作台，应该显示的权限
				List<Integer> typeList = Arrays.stream(type.split(",")).map(string -> Integer.parseInt(string.trim())).collect(Collectors.toList());
				PagePermission.getAllByConsoleType(typeList).forEach((consoleType, map) -> {
					if (!CollectionUtils.isEmpty(map)) {
						map.forEach((desc, name) -> {
							JSONObject json = new JSONObject();
							json.put("desc", desc);
							json.put("name", name);
							json.put("consoleType", consoleType);
							json.put("status", keys.contains(desc));
							array.add(json);
						});
					}
				});
			}
			array.sort((o1, o2) -> {
				JSONObject json1 = (JSONObject) o1;
				JSONObject json2 = (JSONObject) o2;
				if (json1.getInteger("consoleType") != json2.getInteger("consoleType")) {
					return json1.getInteger("consoleType").compareTo(json2.getInteger("consoleType"));
				} else {
					return json1.getString("desc").compareTo(json2.getString("desc"));
				}
			});
		} catch (Exception e) {
			logger.error("", e);
		}
		JSONObject result = new JSONObject();
		result.put("msg", "");
		result.put("code", 0);
		result.put("count", array.size());
		result.put("data", array);
		return result.toJSONString();
	}

	/**
	 * 获取数据权限下拉项
	 */
	@RequestMapping("/getDataPermission")
	@ResponseBody
	public String getDataPermission() {
		long _start = System.currentTimeMillis();
		JSONArray types = new JSONArray();
		String[] typeDescs = DataPermission.getDescs();
		for (int i = 0; i < typeDescs.length; i++) {
			JSONObject json = new JSONObject();
			json.put("value", i);
			json.put("name", typeDescs[i]);
			types.add(json);
		}
		logger.info("获取数据权限下拉项成功，耗时：" + (System.currentTimeMillis() - _start));
		return types.toJSONString();
	}
}
