package com.dahantc.erp.dto.login;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class LoginReqDto implements Serializable {

	private static final long serialVersionUID = -4253543536501244512L;
	@NotEmpty(message = "验证码不能为空")
	private String verifyCode;
	@NotBlank(message = "用户名不能为空")
	private String loginName;
	@NotNull(message = "密码不能为空")
	private String pwdMd5;

	private String randomCode;

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPwdMd5() {
		return pwdMd5;
	}

	public void setPwdMd5(String pwdMd5) {
		this.pwdMd5 = pwdMd5;
	}

	public String getRandomCode() {
		return randomCode;
	}

	public void setRandomCode(String randomCode) {
		this.randomCode = randomCode;
	}

}
