package com.dahantc.erp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页 业务处理
 * 
 * @author 8515
 */
@Controller
public class IndexAction extends BaseAction {

	@RequestMapping("/erp")
	public String monitor() {
		String language = getLanguage();
		request.setAttribute("lang", language);
		return "/views/login/login";
	}
}
