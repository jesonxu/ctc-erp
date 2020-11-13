package com.dahantc.erp.dto;

public abstract class BaseDto {
	// 默认查询第一页
	protected int page = 1;
	// 默认查询前10条
	protected int pageSize = 10;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
