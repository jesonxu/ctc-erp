package com.dahantc.erp.vo.region.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "erp_region")
@DynamicUpdate(true)
public class Region implements Serializable {
	private static final long serialVersionUID = 4802169537008590824L;

	@Id
	@Column(name = "id")
	private Integer id;

	/** 位号，省份，直辖市标识位 */
	@Column(name = "bitwise", columnDefinition = "int default 0")
	private int bitwise;

	/** 归属地级别，0-国家，1-全国，2-省份，3-市 */
	@Column(name = "ilevel", columnDefinition = "int default 0")
	private int ilevel;

	@Column(name = "regionname", length = 100)
	private String regionName;

	/** 上级归属地id **/
	@Column(name = "region_id", length = 10)
	private String regionId;

	/** 国别号 **/
	@Column(name = "countrycode", length = 30)
	private String countryCode;

	/** 归属地或国家的中文拼音 **/
	@Column(name = "pinyin", length = 100)
	private String pinyin;

	@Column(name = "groupRegion", columnDefinition = "int default 0")
	private int groupRegion;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getBitwise() {
		return bitwise;
	}

	public void setBitwise(int bitwise) {
		this.bitwise = bitwise;
	}

	public int getIlevel() {
		return ilevel;
	}

	public void setIlevel(int ilevel) {
		this.ilevel = ilevel;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public int getGroupRegion() {
		return groupRegion;
	}

	public void setGroupRegion(int groupRegion) {
		this.groupRegion = groupRegion;
	}
}
