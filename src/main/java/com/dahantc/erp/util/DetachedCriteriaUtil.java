/**
 * 
 */
package com.dahantc.erp.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchOrder;
import com.dahantc.erp.commom.dao.SearchRule;

/**
 * @author 8533
 * 
 */
public class DetachedCriteriaUtil {

	public static DetachedCriteria addSearchFilter(DetachedCriteria detachedCriteria, SearchFilter filter, List<Order> orderList) throws BaseException {
		List<String> list = new ArrayList<>();
		if (filter != null) {
			// 解析普通条件
			for (SearchRule rule : filter.getRules()) {
				Criterion criterion = analysisRule(rule, list, detachedCriteria);
				if (criterion != null) {
					detachedCriteria.add(criterion);
				}
			}

			// 解析or条件
			for (SearchRule[] orRule : filter.getOrRules()) {
				if (orRule != null && orRule.length > 0) {
					Criterion[] criterions = new Criterion[orRule.length];
					for (int i = 0; i < orRule.length; i++) {
						criterions[i] = analysisRule(orRule[i], list, detachedCriteria);
					}
					detachedCriteria.add(Restrictions.or(criterions));
				}
			}
			if (null != filter.getOrders() && !filter.getOrders().isEmpty()) {
				for (SearchOrder searchOrder : filter.getOrders()) {
					if (orderList != null) {
						orderList.add(analysisOrder(detachedCriteria, searchOrder, list));
					} else {
						detachedCriteria.addOrder(analysisOrder(detachedCriteria, searchOrder, list));
					}
				}
			}

			ProjectionList projections = Projections.projectionList();
			if (StringUtils.isNotEmpty(filter.getSelect())) {
				String[] selects = filter.getSelect().split(",");
				for (String select : selects) {
					projections.add(Projections.property(select));
				}
			}
			if (projections.getLength() > 0) {
				detachedCriteria.setProjection(projections);
			}
		}
		return detachedCriteria;
	}

	public static Order analysisOrder(DetachedCriteria detachedCriteria, SearchOrder searchOrder, List<String> list) {
		Order order = null;
		if (searchOrder != null) {
			if (StringUtils.isNotBlank(searchOrder.getOrderField())) {
				if (searchOrder.getOrderBy().equals("asc")) {// 升序
					if (searchOrder.getOrderField().indexOf(".") != -1) {
						String[] alias = searchOrder.getOrderField().split("\\.");
						String tempAlia = "";
						int i = alias.length;
						for (String alia : alias) {
							i--;
							if (!tempAlia.equals("")) {
								tempAlia = tempAlia + "." + alia;
							} else {
								tempAlia = alia;
							}
							if (i > 0) {
								if (!list.contains(tempAlia)) {
									detachedCriteria.createAlias(tempAlia, alia);
								}
								list.add(tempAlia);
								tempAlia = alia;
							}
						}
						order = Order.asc(tempAlia);
					} else {
						order = Order.asc(searchOrder.getOrderField());
					}
				} else if (searchOrder.getOrderBy().equals("desc")) {// 降序
					if (searchOrder.getOrderField().indexOf(".") != -1) {
						String[] alias = searchOrder.getOrderField().split("\\.");
						String tempAlia = "";
						int i = alias.length;
						for (String alia : alias) {
							i--;
							if (!tempAlia.equals("")) {
								tempAlia = tempAlia + "." + alia;
							} else {
								tempAlia = alia;
							}
							if (i > 0) {
								if (!list.contains(tempAlia)) {
									detachedCriteria.createAlias(tempAlia, alia);
								}
								list.add(tempAlia);
								tempAlia = alia;
							}
						}
						order = Order.desc(tempAlia);
					} else {
						order = Order.desc(searchOrder.getOrderField());
					}
				}
			}
		}
		return order;
	}

	private static Criterion analysisRule(SearchRule rule, List<String> list, DetachedCriteria detachedCriteria) {
		Criterion criterion = null;
		if (rule.getData() == null) {// 查询内容为空
			if (rule.getOp().equals("eq")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.isNull(tempAlia);
				} else {
					criterion = Restrictions.isNull(rule.getField());
				}
			} else if (rule.getOp().equals("ne")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.isNotNull(tempAlia);
				} else {
					criterion = Restrictions.isNotNull(rule.getField());
				}
			}
		} else if (rule.getData().equals("")) {// 查询内容为空
			if (rule.getOp().equals("eq")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.eq(tempAlia, "");
				} else {
					criterion = Restrictions.eq(rule.getField(), "");
				}
			} else if (rule.getOp().equals("ne")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.ne(tempAlia, "");
				} else {
					criterion = Restrictions.ne(rule.getField(), "");
				}
			}
		} else if (!rule.getData().equals("")) {// 查询内容不为空
			if (rule.getOp().equals("cn")) {// 包含
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.like(tempAlia, "%" + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%");
				} else {
					criterion = Restrictions.like(rule.getField(), "%" + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%");
				}
			} else if (rule.getOp().equals("nlcn")) {// 非左包含
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.not(Restrictions.like(tempAlia, (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%"));
				} else {
					criterion = Restrictions
							.not(Restrictions.like(rule.getField(), (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%"));
				}
			} else if (rule.getOp().equals("lcn")) { // 左包含
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.like(tempAlia, (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%");
				} else {
					criterion = Restrictions.like(rule.getField(), (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%");
				}
			} else if (rule.getOp().equals("rcn")) { // 右包含
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.like(tempAlia, "%" + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\"));
				} else {
					criterion = Restrictions.like(rule.getField(), "%" + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\"));
				}
			} else if (rule.getOp().equals("CN")) {// 后 包含
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.like(tempAlia, (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%");
				} else {
					criterion = Restrictions.like(rule.getField(), (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%");
				}
			} else if (rule.getOp().equals("eq")) {// 精确匹配
				if (rule.getField().indexOf(".") != -1) {// 查询字段
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.eq(tempAlia, rule.getData());
				} else {
					criterion = Restrictions.eq(rule.getField(), rule.getData());
				}
			} else if (rule.getOp().equals("ne")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.ne(tempAlia, rule.getData());
				} else {
					criterion = Restrictions.ne(rule.getField(), rule.getData());
				}
			} else if (rule.getOp().equals("le")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.le(tempAlia, rule.getData());
				} else {
					criterion = Restrictions.le(rule.getField(), rule.getData());
				}
			} else if (rule.getOp().equals("ge")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.ge(tempAlia, rule.getData());
				} else {
					criterion = Restrictions.ge(rule.getField(), rule.getData());
				}
			} else if (rule.getOp().equals("lt")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.lt(tempAlia, rule.getData());
				} else {
					criterion = Restrictions.lt(rule.getField(), rule.getData());
				}
			} else if (rule.getOp().equals("gt")) {
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.gt(tempAlia, rule.getData());
				} else {
					criterion = Restrictions.gt(rule.getField(), rule.getData());
				}
			} else if (rule.getOp().equals("in")) {// in
				Object a = rule.getData();
				Object[] b = null;
				if (a instanceof List) {
					b = ((List<?>) a).toArray();
				} else if (a instanceof Object[]) {
					b = (Object[]) a;
				}
				if (rule.getField().indexOf(".") != -1) {
					String[] alias = rule.getField().split("\\.");
					String tempAlia = "";
					int i = alias.length;
					for (String alia : alias) {
						i--;
						if (!tempAlia.equals("")) {
							tempAlia = tempAlia + "." + alia;
						} else {
							tempAlia = alia;
						}
						if (i > 0) {
							if (!list.contains(tempAlia)) {
								detachedCriteria.createAlias(tempAlia, alia);
							}
							list.add(tempAlia);
							tempAlia = alia;
						}
					}
					criterion = Restrictions.in(tempAlia, b);
				} else {
					criterion = Restrictions.in(rule.getField(), b);
				}
			}
		}
		return criterion;
	}

	public static String getWhereHQL(SearchFilter filter, String alia, String dbname) {
		StringBuffer buf = new StringBuffer();
		List<String> condition = null;
		if (filter != null) {
			alia = (alia != null) ? alia + "." : "";
			if (!filter.getRules().isEmpty()) {
				condition = new ArrayList<String>();
				for (SearchRule rule : filter.getRules()) {
					String conditionStr = getWherHQLStr(rule, alia, dbname);
					if (StringUtils.isNotBlank(conditionStr)) {
						condition.add(conditionStr);
					}
				}
				if (condition != null && !condition.isEmpty()) {
					for (int i = 0; i < condition.size(); i++) {
						buf.append(condition.get(i));
						if (i < condition.size() - 1) {
							buf.append(" and ");
						}
					}
				}
			}

			if (!filter.getOrRules().isEmpty()) {
				for (SearchRule[] orRule : filter.getOrRules()) {
					if (orRule != null && orRule.length > 0) {
						if (buf.length() > 0) {
							buf.append(" and ");
						}
						buf.append(" (");
						for (int i = 0; i < orRule.length; i++) {
							if (i != 0) {
								buf.append(" or ");
							}
							buf.append(getWherHQLStr(orRule[i], alia, dbname));
						}
						buf.append(") ");
					}
				}
			}
		}
		return buf.toString();
	}

	private static String getWherHQLStr(SearchRule rule, String alia, String dbname) {
		String condition = null;
		if (rule.getData() == null) {
			if (rule.getOp().equals("eq")) {
				condition = alia + rule.getField() + " is null";
			} else if (rule.getOp().equals("ne")) {
				condition = alia + rule.getField() + " != null";
			}
		} else if (rule.getData().equals("")) {// 查询内容为空
			if (rule.getOp().equals("eq")) {
				condition = alia + rule.getField() + " = " + "\'\'";
			} else if (rule.getOp().equals("ne")) {
				condition = alia + rule.getField() + " != " + "\'\'";
			}
		} else if (!rule.getData().equals("")) {// 查询内容不为空
			if (rule.getOp().equals("cn")) {// 包含
				condition = alia + rule.getField() + " like " + "'%" + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%'";
			} else if (rule.getOp().equals("lcn")) { // 左包含
				condition = alia + rule.getField() + " like " + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%";
			} else if (rule.getOp().equals("nlcn")) { // 非左包含
				condition = alia + rule.getField() + "not like " + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%";
			} else if (rule.getOp().equals("rcn")) { // 右包含
				condition = alia + rule.getField() + " like " + "%" + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\");
			} else if (rule.getOp().equals("CN")) {// 后 包含
				condition = alia + rule.getField() + " like " + (rule.getData().toString()).replace("%", "\\%").replace("\\", "\\\\") + "%";
			} else if (rule.getOp().equals("eq")) {// 精确匹配
				condition = alia + rule.getField() + " = " + getConvertData(rule.getData(), dbname);
			} else if (rule.getOp().equals("ne")) {
				condition = alia + rule.getField() + " != " + getConvertData(rule.getData(), dbname);
			} else if (rule.getOp().equals("le")) {
				condition = alia + rule.getField() + " <= " + getConvertData(rule.getData(), dbname);
			} else if (rule.getOp().equals("ge")) {
				condition = alia + rule.getField() + " >= " + getConvertData(rule.getData(), dbname);
			} else if (rule.getOp().equals("lt")) {
				condition = alia + rule.getField() + " < " + getConvertData(rule.getData(), dbname);
			} else if (rule.getOp().equals("gt")) {
				condition = alia + rule.getField() + " > " + getConvertData(rule.getData(), dbname);
			} else if (rule.getOp().equals("in")) {// in
				Object a = rule.getData();
				Object[] b = null;
				if (a instanceof List) {
					b = ((List<?>) a).toArray();
				} else if (a instanceof Object[]) {
					b = (Object[]) a;
				}
				condition = alia + rule.getField() + " in " + getConvertData(b);
			}
		}
		return condition;
	}

	private static String getConvertData(Object[] data) {
		String bst = "( ";
		if (data != null && data.length > 0) {
			String temp = "";
			for (int i = 0; i < data.length; i++) {
				temp += data[i].toString();
				if (i < data.length - 1) {
					temp += ",";
				}
			}
			bst += temp;
		}
		bst += " ) ";
		return bst;
	}

	private static String getConvertData(Object data, String dbname) {
		String fieldData = null;
		if (data instanceof Date) {
			Date date = (Date) data;
			String dateStr = DateUtil.transFormString(date, "yyyy-MM-dd HH:mm:ss");
			if (StringUtils.equals("Oracle", dbname)) {
				return " to_date( '" + dateStr + "','YYYY-MM-DD HH24:MI:SS')";
			} else {
				return "'" + dateStr + "'";
			}
		} else if (data instanceof String) {
			fieldData = "'" + data.toString() + "'";
		} else if (data instanceof Number) {
			fieldData = data.toString();
		} else {
			fieldData = "'" + data.toString() + "'";
		}

		return fieldData;
	}

	/**
	 * 
	 * 方法描述：
	 * 
	 * @param filter
	 * @param alia
	 * @param dbname
	 * @return 第一个值为hql，第二个值为绑定参数集合。
	 * @author: 8527
	 * @date: 2016年1月23日 下午1:57:17
	 */
	public static Object[] getHQLByFilter(SearchFilter filter, String alia, String dbname) {
		Object[] result = new Object[2];
		List<Object> list = new ArrayList<Object>();
		List<String> condition = null;
		StringBuffer buf = new StringBuffer();
		if (filter != null) {
			alia = (alia != null) ? alia + "." : "";
			if (!filter.getRules().isEmpty()) {
				condition = new ArrayList<String>();
				for (SearchRule rule : filter.getRules()) {
					String conditionStr = getHqlStrByFilter(rule, alia, list);
					if (StringUtils.isNotBlank(conditionStr)) {
						condition.add(conditionStr);
					}
				}
				if (condition != null && !condition.isEmpty()) {
					for (int i = 0; i < condition.size(); i++) {
						buf.append(condition.get(i));
						if (i < condition.size() - 1) {
							buf.append(" and ");
						}
					}
				}
			}

			if (!filter.getOrRules().isEmpty()) {
				for (SearchRule[] orRule : filter.getOrRules()) {
					if (orRule != null && orRule.length > 0) {
						if (buf.length() > 0) {
							buf.append(" and ");
						}
						buf.append(" (");
						for (int i = 0; i < orRule.length; i++) {
							if (i != 0) {
								buf.append(" or ");
							}
							buf.append(getHqlStrByFilter(orRule[i], alia, list));
						}
						buf.append(") ");
					}
				}
			}
		}
		result[0] = buf.toString();
		result[1] = list.toArray();
		return result;
	}

	private static String getHqlStrByFilter(SearchRule rule, String alia, List<Object> list) {
		String condition = null;
		if (rule.getData() == null) {
			if (rule.getOp().equals("eq")) {
				condition = alia + rule.getField() + " is null";
			} else if (rule.getOp().equals("ne")) {
				condition = alia + rule.getField() + " != null";
			}
		} else {// 查询内容不为空
			Object data = rule.getData();
			if (rule.getOp().equals("cn")) {// 包含
				condition = alia + rule.getField() + " like ?";
				data = "%" + data.toString() + "%";
				list.add(data);
			} else if (rule.getOp().equals("lcn")) { // 左包含
				condition = alia + rule.getField() + " like ?";
				data = data.toString() + "%";
				list.add(data);
			} else if (rule.getOp().equals("rcn")) { // 右包含
				condition = alia + rule.getField() + " like ?";
				data = "%" + data.toString();
				list.add(data);
			} else if (rule.getOp().equals("nlcn")) { // 非左包含
				condition = alia + rule.getField() + "not like ?";
				data = data.toString() + "%";
				list.add(data);
			} else if (rule.getOp().equals("CN")) {// 后 包含
				condition = alia + rule.getField() + " like ?";
				data = "%" + data.toString();
				list.add(data);
			} else if (rule.getOp().equals("eq")) {// 精确匹配
				condition = alia + rule.getField() + " = ?";
				list.add(data);
			} else if (rule.getOp().equals("ne")) {
				condition = alia + rule.getField() + " != ?";
				list.add(data);
			} else if (rule.getOp().equals("le")) {
				condition = alia + rule.getField() + " <= ?";
				list.add(data);
			} else if (rule.getOp().equals("ge")) {
				condition = alia + rule.getField() + " >= ?";
				list.add(data);
			} else if (rule.getOp().equals("lt")) {
				condition = alia + rule.getField() + " < ?";
				list.add(data);
			} else if (rule.getOp().equals("gt")) {
				condition = alia + rule.getField() + " > ?";
				list.add(data);
			} else if (rule.getOp().equals("in")) {// in
				StringBuffer sb = new StringBuffer();
				sb.append(alia).append(rule.getField()).append(" in (");
				Object a = rule.getData();
				Object[] ins = null;
				if (a instanceof List) {
					ins = ((List<?>) a).toArray();
				} else if (a instanceof Object[]) {
					ins = (Object[]) a;
				}
				for (int i = 0; i < ins.length; i++) {
					if (i == 0) {
						sb.append("?");
					} else {
						sb.append(",?");
					}
					list.add(ins[i]);
				}
				sb.append(")");
				condition = sb.toString();
			}
		}
		return condition;
	}
}
