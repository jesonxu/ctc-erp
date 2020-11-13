package com.dahantc.erp.vo.contractIncrease.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "erp_contract_increase")
@DynamicUpdate(true)
public class ContractIncrease {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private int id;

	private Timestamp wtime = new Timestamp(System.currentTimeMillis());

	public int getId() {
		return id;
	}

	public Timestamp getWtime() {
		return wtime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setWtime(Timestamp wtime) {
		this.wtime = wtime;
	}

}
