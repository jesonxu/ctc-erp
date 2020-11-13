package com.dahantc.erp.vo.financialoperatereport.entity;

public class MainCustResult<T> {
	
	private T content;
	
	private int count;

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public MainCustResult(T content, int count) {
		super();
		this.content = content;
		this.count = count;
	}

}
