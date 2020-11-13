/**
 * 
 */
package com.dahantc.erp.commom.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 搜索过滤器
 * 
 * @author 8531
 */
public class SearchFilter implements Serializable {
	private static final Logger logger = LogManager.getLogger(SearchFilter.class);
	private static final long serialVersionUID = 611489428669824516L;

	/** 多字段查询时分组类型，主要是AND或者OR */

	private String groupOp = "AND";

	private String select = "";

	private String groupBy = "";

	/** 多字段查询时候，查询条件的集合 */
	private List<SearchRule> rules = new ArrayList<SearchRule>();

	/** or条件集合 */
	private List<SearchRule[]> orRules = new ArrayList<SearchRule[]>();

	/** or条件集合，单元or条件支持多条件组合，sql查询暂不支持，支持ES查询 */
	private List<List<SearchRule[]>> orsRules = new ArrayList<List<SearchRule[]>>();

	/** 多字段排序 */
	private List<SearchOrder> orders = new ArrayList<SearchOrder>();

	public void release() {
		try {
			rules.clear();
			rules = null;
			orRules.clear();
			orRules = null;
			orders.clear();
			orders = null;
			orsRules.clear();
			orsRules = null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 获取查询条件的集合
	 * 
	 * @return List<{@link SearchRule}>
	 */
	public List<SearchRule> getRules() {
		return rules;
	}

	/**
	 * 设置查询条件的集合
	 * 
	 * @param rules
	 *            List<{@link SearchRule}>
	 */
	public void setRules(List<SearchRule> rules) {
		this.rules = rules;
	}

	/**
	 * 获取排序集合
	 * 
	 * @return List<{@link SearchOrder}>
	 */
	public List<SearchOrder> getOrders() {
		return orders;
	}

	/**
	 * 设置排序集合
	 * 
	 * @param orders
	 *            List<{@link SearchOrder}>
	 */
	public void setOrders(List<SearchOrder> orders) {
		this.orders = orders;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getGroupOp() {
		return groupOp;
	}

	public void setGroupOp(String groupOp) {
		this.groupOp = groupOp;
	}

	public List<SearchRule[]> getOrRules() {
		return orRules;
	}

	public void setOrRules(List<SearchRule[]> orRules) {
		this.orRules = orRules;
	}

	public List<List<SearchRule[]>> getOrsRules() {
		return orsRules;
	}

	public void setOrsRules(List<List<SearchRule[]>> orsRules) {
		this.orsRules = orsRules;
	}

	public String getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

}