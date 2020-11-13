package com.dahantc.erp.commom;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据对象
 * 
 * @author 8515
 */
public class PageResult<T> implements Serializable {

	private static final long serialVersionUID = -5545326937396319559L;

	/** 状态码 */
	private int code = 0;

	/** 提示文本 */
	private String msg;

	/** 当前分页页码 */
	private int currentPage;

	/** 数据分页总数 */
	private int totalPages;
	
	/** 数据记录总数 */
	private long count;

	/** 当前页数据行集合 */
	private List<T> data;

	public PageResult() {
		super();
	}

	@SuppressWarnings("unchecked")
	public PageResult(List<?> data, long count) {
		super();
		this.count = count;
		this.data = (List<T>) data;
	}
	
	public PageResult(List<T> inRows, int inCurrentPage, int inTotalPages, long inTotalRecords) {
		this.setData(inRows);
		this.setCurrentPage(inCurrentPage);
		this.setTotalPages(inTotalPages);
		this.setCount(inTotalRecords);
	}

	/**
	 * 直接返回空的对象
	 * @param messgae 提示消息
	 * @param <T> 类型
	 * @return 空
	 */
	public static <T> PageResult<T> empty(String messgae) {
		PageResult<T> result = new PageResult<>(null, 0);
		result.setMsg(messgae);
		return result;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public void release() {
		if (this.data != null) {
			this.data.clear();
			this.data = null;
		}
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	
}
