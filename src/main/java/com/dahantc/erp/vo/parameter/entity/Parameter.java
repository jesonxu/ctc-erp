package com.dahantc.erp.vo.parameter.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import com.dahantc.erp.enums.ParamType;

/**
 * ema_page_property 实体类 Mon Feb 25 17:26:19 CST 2019 wangyang
 */

@Entity
@Table(name = "erp_parameter")
@DynamicUpdate(true)
public class Parameter implements Serializable {

	private static final long serialVersionUID = 3443945072247365901L;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String entityid;

	/** 参数说明 **/
	@Column(name = "depict", length = 255)
	private String depict;

	/** 参数类别 **/
	@Column(name = "paramtype", columnDefinition = "int default 0")
	private Integer paramType = ParamType.SYSTEM_PARAMETER.ordinal();

	/** 参数名称 **/
	@Column(name = "paramkey", length = 255)
	private String paramkey;
	
	/** 扩展属性 **/
	@Column(name = "extended", length = 1000)
	private String extended;

	/** 参数值 **/
	@Column(name = "paramvalue", length = 255)
	private String paramvalue;

	/** 创建时间 **/
	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public String getEntityid() {
		return entityid;
	}

	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}

	public String getDepict() {
		return depict;
	}

	public void setDepict(String depict) {
		this.depict = depict;
	}

	public String getParamkey() {
		return paramkey;
	}

	public void setParamkey(String paramkey) {
		this.paramkey = paramkey;
	}

	public String getParamvalue() {
		return paramvalue;
	}

	public void setParamvalue(String paramvalue) {
		this.paramvalue = paramvalue;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

	public Integer getParamType() {
		return paramType;
	}

	public void setParamType(int paramType) {
		this.paramType = paramType;
	}

	public String getExtended() {
		return extended;
	}

	public void setExtended(String extended) {
		this.extended = extended;
	}

}
