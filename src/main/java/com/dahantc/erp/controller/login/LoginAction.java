package com.dahantc.erp.controller.login;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.AuthenticationException;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.VerifyUtil;
import com.dahantc.erp.commom.WeixinMessage;
import com.dahantc.erp.commom.WeixinParam;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.login.LoginReqDto;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.LogType;
import com.dahantc.erp.enums.SearchType;
import com.dahantc.erp.util.SendSmsMsgUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.menuItem.entity.MenuItem;
import com.dahantc.erp.vo.menuItem.service.IMenuItemService;
import com.dahantc.erp.vo.role.entity.Role;
import com.dahantc.erp.vo.role.service.IRoleService;
import com.dahantc.erp.vo.roledetail.entity.RoleDetail;
import com.dahantc.erp.vo.roledetail.service.IRoleDetailService;
import com.dahantc.erp.vo.rolerelation.entity.RoleRelation;
import com.dahantc.erp.vo.rolerelation.service.IRoleRelationService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * @Description: 登录控制器
 * @author 8515
 * @date 2019年3月18日
 * @version V1.0
 */
@Controller
@RequestMapping(value = "/login")
public class LoginAction extends BaseAction {
	private static final Logger logger = LogManager.getLogger(LoginAction.class);
	private static String[] numLetters = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };

	@Autowired
	private IUserService userService;

	@Autowired
	private IMenuItemService menuItemService;

	@Autowired
	private IRoleDetailService roleDetailService;

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IRoleRelationService roleRelationService;

	@Autowired
	private Environment ev;
	/**
	 * 操作描述
	 */
	private String msg;

	@Value("${isTestMode}")
	private String isTestMode;

	/**
	 * 用户认证
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/login")
	@ResponseBody
	public BaseResponse<String> login(@Valid LoginReqDto loginReqDto) throws Exception {

		String verifyCode = loginReqDto.getVerifyCode();
		String loginName = loginReqDto.getLoginName();
		String passWord = loginReqDto.getPwdMd5();

		try {
			// 校验验证码
			HttpSession session = request.getSession();
			if (session != null) {
				if (!"1".equals(isTestMode)) {
					Object sessionVerify = session.getAttribute(VerifyUtil.RANDOMCODEKEY);
					if (sessionVerify == null) {
						msg = "登录过期";
						return BaseResponse.error(msg);
					}
					if (verifyCode == null || ("").equals(verifyCode) || !String.valueOf(sessionVerify).equalsIgnoreCase(verifyCode)) {
						msg = "验证码错误";
						return BaseResponse.error(msg);
					}
				}
				// 校验登录用户
				User user = userService.login(loginName, passWord);
				logger.info("来自[ " + getIp() + " ]，使用 [ " + loginName + " ]登陆");

				if (user != null) {
					session.setAttribute(Constants.SESSION_KEY, user);
				}
				msg = "登入成功";
				return BaseResponse.success(msg);
			} else {
				msg = "验证码已过期";
				return BaseResponse.error(msg);
			}
		} catch (AuthenticationException e) {
			msg = e.getMessage();
			logger.error("来自[ " + getIp() + " ]，使用 [ " + loginName + " : " + passWord + " ]，" + msg);
			return BaseResponse.error(msg);
		} catch (Exception e) {
			msg = "登录失败，账号或密码错误";
			logger.error("来自[ " + request.getRemoteAddr() + " ]，使用 [ " + loginName + " : " + passWord + " ]，登录异常", e);
			return BaseResponse.error(msg);
		}
	}

	/**
	 * 用户认证
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/loginWithRandomCode")
	@ResponseBody
	public BaseResponse<String> loginWithRandomCode(@Valid LoginReqDto loginReqDto) throws Exception {

		String verifyCode = loginReqDto.getVerifyCode();
		String loginName = loginReqDto.getLoginName();
		String passWord = loginReqDto.getPwdMd5();
		String randomCode = loginReqDto.getRandomCode();

		try {
			// 校验验证码
			HttpSession session = request.getSession();
			if (session != null) {
				if (!"1".equals(isTestMode)) {
					Object sessionVerify = session.getAttribute(VerifyUtil.RANDOMCODEKEY);
					if (sessionVerify == null) {
						msg = "登录过期";
						return BaseResponse.error(msg);
					}
					if (verifyCode == null || ("").equals(verifyCode) || !String.valueOf(sessionVerify).equalsIgnoreCase(verifyCode)) {
						msg = "验证码错误";
						return BaseResponse.error(msg);
					}
				}

				// 校验登录用户
				User user = userService.loginWithRandomCode(loginName, passWord, randomCode, "1".equals(isTestMode));
				logger.info("来自[ " + getIp() + " ]，使用 [ " + loginName + " ]登陆");

				if (user != null) {
					session.setAttribute(Constants.SESSION_KEY, user);
				}
				msg = "登入成功";
				return BaseResponse.success(msg);
			} else {
				msg = "验证码已过期";
				return BaseResponse.error(msg);
			}
		} catch (AuthenticationException e) {
			msg = e.getMessage();
			logger.error("来自[ " + getIp() + " ]，使用 [ " + loginName + " : " + passWord + " ]，" + msg);
			return BaseResponse.error(msg);
		} catch (Exception e) {
			msg = "登录失败，账号或密码错误";
			logger.error("来自[ " + request.getRemoteAddr() + " ]，使用 [ " + loginName + " : " + passWord + " ]，登录异常", e);
			return BaseResponse.error(msg);
		}
	}

	@PostMapping("/getPhoneCode")
	@ResponseBody
	public BaseResponse<String> getPhoneCode(@Valid LoginReqDto loginReqDto) throws Exception {
		String verifyCode = loginReqDto.getVerifyCode();
		String loginName = loginReqDto.getLoginName();
		String passWord = loginReqDto.getPwdMd5();

		try {
			// 校验验证码
			HttpSession session = request.getSession();
			if (session != null) {
				Object sessionVerify = session.getAttribute(VerifyUtil.RANDOMCODEKEY);
				if (sessionVerify == null) {
					msg = "登录过期";
					return BaseResponse.error(msg);
				}
				if (verifyCode == null || ("").equals(verifyCode) || !String.valueOf(sessionVerify).equalsIgnoreCase(verifyCode)) {
					msg = "验证码错误";
					return BaseResponse.error(msg);
				}
				// 校验登录用户
				User user = userService.login(loginName, passWord);
				logger.info("来自[ " + getIp() + " ]，使用 [ " + loginName + " ]登陆");

				if (user != null) {
					logger.info("账号密码验证码校验成功，开始获取手机验证码");
					if (null != VerifyUtil.timeMap.get(user.getLoginName())
							&& System.currentTimeMillis() - VerifyUtil.timeMap.get(user.getLoginName()) < 60 * 1000) {
						msg = "距上次获取手机验证码未满1分钟，请稍后再试";
						return BaseResponse.error(msg);
					}

					StringBuffer _sb = new StringBuffer();
					Random _random = new Random(System.currentTimeMillis());
					for (int i = 0; i < 6; i++) {
						_sb.append(numLetters[_random.nextInt(numLetters.length)]);
					}
					String _randomCode = _sb.toString();
					boolean result = SendSmsMsgUtil.sendRandomCodeMsg(user.getContactMobile(), _randomCode);
					if (result) {
						VerifyUtil.randomCodeMap.put(user.getLoginName(), _randomCode);
						VerifyUtil.timeMap.put(user.getLoginName(), System.currentTimeMillis());
						logger.info("验证码:" + _randomCode + "，获取成功");
						msg = "验证码发送成功，3分钟有效";
						return BaseResponse.success(msg);
					} else {
						msg = "验证码发送失败，请稍后重试";
						return BaseResponse.error(msg);
					}
				} else {
					msg = "账号不存在或密码错误";
					return BaseResponse.error(msg);
				}
			} else {
				msg = "验证码已过期";
				return BaseResponse.error(msg);
			}
		} catch (AuthenticationException e) {
			msg = e.getMessage();
			logger.error("来自[ " + getIp() + " ]，使用 [ " + loginName + " : " + passWord + " ]，" + msg);
			return BaseResponse.error(msg);
		} catch (Exception e) {
			msg = "登录失败，账号或密码错误";
			logger.error("来自[ " + request.getRemoteAddr() + " ]，使用 [ " + loginName + " : " + passWord + " ]，登录异常", e);
			return BaseResponse.error(msg);
		}
	}

	/**
	 * 获取企业微信相关参数
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/wxparam")
	@ResponseBody
	public BaseResponse<WeixinParam> wxparam() throws Exception {
		return BaseResponse.success(WeixinMessage.initwxParam(ev));
	}

	/**
	 * 企业微信扫码登录
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/wxLogin", produces = "application/text; charset=utf-8")
	public String wxLogin() throws Exception {
		HttpSession session = request.getSession();
		String code = request.getParameter("code");
		String type = request.getParameter("type");
		User user = null;
		if (StringUtils.isNotBlank(code)) {
			user = getUserByWxCode(code);
		}
		if (user != null) {
			session.setAttribute(Constants.SESSION_KEY, user);
			if (StringUtil.isNotBlank(type) && "1".equals(type)){
				return "redirect:/mobile/toIndex";
			} else if ("2".equals(type)){
				return "redirect:/personalCenter/toMyMessagePage";
			} else {
				return "redirect:/login/toWelcomePage";
			}
		} else {
			return "redirect:/views/login/login";
		}
	}
	
	
	/**
	 * 微信入口,该地址可以配置在企业微信中,从企业微信中点击菜单访问该接口
	 * 作用: 根据企业微信corpid得到获取code的url
	 * @return
	 */
	@RequestMapping(value = "/entry")
	public String weixinEntry(HttpServletRequest request, HttpServletResponse response){
		
		WeixinParam weixinParam = WeixinMessage.initwxParam(ev);
		logger.info("weixinEntry corpid: {}, redirectURI: {}, ", weixinParam.getCorpid(), weixinParam.getCorpsecret());
		
		// 重定向的地址,需要是一个可以信任的域名,不然提示域名不可信
		String redirect_uri = "";
		try {
			// redirect_uri
			redirect_uri = URLEncoder.encode(weixinParam.getRedirect_uri(), "UTF-8") + "?type=1";
		} catch (UnsupportedEncodingException e) {
			logger.info("weixinEntry redirect_uri error: {}", e);
		}
		// 微信认证地址,该地址会获取一个code,并且该地址只能在微信客户端中打开
		String oauthUrl = WeixinMessage.QY_WEIXIN_OAUTH_URL
				.replace("CORPID", weixinParam.getCorpid())
				.replace("REDIRECT_URI", redirect_uri);
		// 需要把该地址复制到微信客户端中打开链接, 微信会根据redirect_uri参数自动跳转地址
		logger.info("weixinEntry oauthUrl: {}", oauthUrl);	
		// 重定向,该地址一定要在微信客户端里才能打开
		return "redirect:" + oauthUrl;
	}

	/**
	 * 根据企业微信回传过来的code，获取账户。
	 * 
	 * @param code
	 * @param
	 * @return user
	 */
	private User getUserByWxCode(String code) {
		try {
			String wx_userInfo = WeixinMessage.getUserID(code);
			if (StringUtils.isBlank(wx_userInfo)) {
				logger.info(" code:" + code + "查询不到对应的企业微信用户信息！");
				return null;
			}
			JSONObject tokenJson = JSONObject.parseObject(wx_userInfo);
			if (null == tokenJson) {
				logger.info(" wx_userInfo:" + wx_userInfo + "转换json失败");
				return null;
			}
			int errcode = (int) tokenJson.get("errcode");
			String userId = (String) tokenJson.get("UserId");
			if (0 != errcode || StringUtils.isBlank(userId)) {
				logger.info(" errcode:" + errcode + "userId:" + userId + ",企业微信用户信息非法！");
				return null;
			}
			logger.info("使用企业微信ID登录：" + userId);

			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, userId));
			List<User> userList = userService.queryAllBySearchFilter(filter);
			if (userList == null || userList.isEmpty()) {
				logger.info(" userId:" + userId + ",在erp系统中没有查到对应用户信息！开始同步");
				return syncUserByWx(userId);
			}
			for (User user : userList) {
				if (EntityStatus.NORMAL.ordinal() == user.getStatus() && EntityStatus.NORMAL.ordinal() == user.getUstate()) {
					return user;
				}
			}
			logger.info("userId:" + userId + ",企业微信用户信息非法！");
		} catch (Exception e) {
			logger.error("用户使用企业微信登录异常：", e);
		}
		return null;
	}

	@RequestMapping(value = "/toWelcomePage", produces = "application/text; charset=utf-8")
	public String toWelcomePage() throws Exception {
		String language = getLanguage();
		request.setAttribute("lang", language);
		User user = getOnlineUser();
		if (user != null) {
			List<Role> roleList = new ArrayList<>();
			String roleId = request.getParameter("roleId");

			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
			List<RoleRelation> roles = roleRelationService.queryAllBySearchFilter(filter);

			for (RoleRelation relation : roles) {
				Role role = roleService.read(relation.getRoleId());
				if (relation.getRoleId().equals(roleId)) {
					roleList.add(0, role);
				} else {
					roleList.add(role);
				}
			}
			if (roleList.isEmpty()) {
				cleanSession();
				return "/views/login/login";
			}
			request.setAttribute("realName", user.getRealName());
			request.setAttribute("loginName", user.getLoginName());
			request.setAttribute("menus", getPerMenus(roleList.get(0).getRoleid()));
			request.setAttribute("roles", roleList);
			request.setAttribute("searchTypes", getSearchType());
			request.getSession().setAttribute(Constants.ROLEID_KEY, roleList.get(0).getRoleid());
			saveLog("ProductAction.login", User.class.getName(), "账号：" + user.getLoginName() + "，登陆成功", LogType.OperationLog.ordinal());
			return "/index";
		} else {
			return "/views/login/login";
		}

	}

	private JSONArray getSearchType() {
		JSONArray result = new JSONArray();
		for (SearchType searchType : SearchType.values()) {
			JSONObject json = new JSONObject();
			json.put("value", searchType.getCode());
			json.put("desc", searchType.getDesc());
			result.add(json);
		}
		return result;
	}

	private List<MenuItem> getPerMenus(String roleId) {

		List<MenuItem> menuList = new ArrayList<MenuItem>();
		try {
			// 根据roleId查询roleDetailList
			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("roleid", Constants.ROP_EQ, roleId));
			List<RoleDetail> roleDetailList = roleDetailService.queryAllBySearchFilter(filter);
			MenuItem defaultMenu = null;
			for (RoleDetail roledetail : roleDetailList) {
				MenuItem menu = menuItemService.read(roledetail.getMenuid());
				if (roledetail.getDefalutMenuType() == 0) {
					menuList.add(menu);
				} else {
					defaultMenu = menu;
				}
			}
			menuList.sort((o1, o2) -> {
				int result = o1.getMenuGroup() - o2.getMenuGroup();
				if (result == 0) {
					return o1.getMenusequence() - o2.getMenusequence();
				} else {
					return result;
				}
			});
			if (null != defaultMenu) {
				menuList.add(0, defaultMenu);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return menuList;
	}

	/**
	 * 生成验证码
	 */
	@GetMapping(value = "/getVerify")
	public ModelAndView getVerify(HttpServletRequest request, HttpServletResponse response) {
		try {

			HttpSession session = request.getSession();
			// 利用图片工具生成图片
			// 第一个参数是生成的验证码，第二个参数是生成的图片
			Object[] objs = VerifyUtil.createImage();
			// 将验证码存入Session
			session.removeAttribute(VerifyUtil.RANDOMCODEKEY);
			session.setAttribute(VerifyUtil.RANDOMCODEKEY, objs[0]);
			// 将图片输出给浏览器
			BufferedImage image = (BufferedImage) objs[1];
			response.setContentType("image/png");
			// 设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			// 将内存中的图片通过流动形式输出到客户端
			ImageIO.write(image, "png", response.getOutputStream());

		} catch (Exception e) {
			logger.error("获取验证码失败", e);
		}
		return null;
	}

	public String getIsTestMode() {
		return isTestMode;
	}

	public void setIsTestMode(String isTestMode) {
		this.isTestMode = isTestMode;
	}

	/**
	 * 同步企业微信账号到erp系统
	 * 
	 * @param userId
	 * @return
	 * @throws ServiceException
	 */
	private User syncUserByWx(String userId) throws ServiceException {
		String userInfo = WeixinMessage.getUserInfo(userId);
		if (StringUtils.isNotBlank(userInfo)) {
			JSONObject userInfoJsonObject = JSONObject.parseObject(userInfo);
			if (null != userInfoJsonObject) {
				String userid = userInfoJsonObject.getString("userid");
				String name = userInfoJsonObject.getString("name");
				String mobile = userInfoJsonObject.getString("mobile");
				String email = userInfoJsonObject.getString("email");
				JSONArray departmentJsonArray = userInfoJsonObject.getJSONArray("department");
				if (null != departmentJsonArray && departmentJsonArray.size() > 0 && StringUtils.isNotBlank(departmentJsonArray.getString(0))) {
					User user = new User();
					user.setOssUserId(userid);
					// 部门发生变化
					user.setRealName(name);
					user.setLoginName(mobile);
					user.setContactMobile(mobile);
					user.setContacteMail(email);
					user.setStatus(1);
					user.setUstate(1);
					user.setDeptId(departmentJsonArray.getString(0));
					userService.save(user);
					return user;
				} else {
					logger.info(" userId:" + userId + ",同步企业微信中部门信息不正确。");
				}
			} else {
				logger.info(" userId:" + userId + ",同步企业微信中信息不正确。");
			}
		} else {
			logger.info(" userId:" + userId + ",在企业微信中不存在。");
		}
		return null;
	}

	@RequestMapping("/faq")
	public String faq() {
		return "/views/faq/faq";
	}
}
