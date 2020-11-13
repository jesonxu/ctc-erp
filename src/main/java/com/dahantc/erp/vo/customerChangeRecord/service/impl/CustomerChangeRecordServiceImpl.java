package com.dahantc.erp.vo.customerChangeRecord.service.impl;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.BaseException;
import com.dahantc.erp.commom.dao.DaoException;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.dto.customerChangeRecord.ChangeCountDto;
import com.dahantc.erp.enums.CustomerChangeType;
import com.dahantc.erp.enums.CustomerTypeValue;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.base.IBaseDao;
import com.dahantc.erp.vo.customerChangeRecord.dao.ICustomerChangeRecordDao;
import com.dahantc.erp.vo.customerChangeRecord.entity.CustomerChangeRecord;
import com.dahantc.erp.vo.customerChangeRecord.service.ICustomerChangeRecordService;
import com.dahantc.erp.vo.customerChangeRecord.vo.CustomerChangeVo;
import com.dahantc.erp.vo.customerType.entity.CustomerType;
import com.dahantc.erp.vo.customerType.service.ICustomerTypeService;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户变更记录Service
 *
 * @author 8520
 */
@Service(value = "customerChangeRecordService")
public class CustomerChangeRecordServiceImpl implements ICustomerChangeRecordService {

    private static Logger logger = LogManager.getLogger(CustomerChangeRecordServiceImpl.class);

    @Autowired
    private IDepartmentService departmentService;

    @Autowired
    private ICustomerChangeRecordDao customerChangeRecordDao;

    @Autowired
    private IBaseDao baseDao;

    @Autowired
    private ICustomerTypeService customerTypeService;

    @Override
    public CustomerChangeRecord read(Serializable id) throws ServiceException {
        try {
            return customerChangeRecordDao.read(id);
        } catch (Exception e) {
            logger.error("读取客户变更记录失败", e);
            throw new ServiceException("读取客户变更记录失败", e);
        }
    }

    @Override
    public boolean save(CustomerChangeRecord entity) throws ServiceException {
        try {
            return customerChangeRecordDao.save(entity);
        } catch (DaoException e) {
            throw new ServiceException("保存客户变更记录失败", e);
        }
    }

    @Override
    public boolean delete(Serializable id) throws ServiceException {
        try {
            return customerChangeRecordDao.delete(id);
        } catch (DaoException e) {
            throw new ServiceException("删除客户变更记录失败", e);
        }
    }

    @Override
    public boolean update(CustomerChangeRecord enterprise) throws ServiceException {
        try {
            return customerChangeRecordDao.update(enterprise);
        } catch (DaoException e) {
            throw new ServiceException("更新客户变更记录失败", e);
        }
    }

    @Override
    public int getCount(SearchFilter filter) throws ServiceException {
        try {
            return customerChangeRecordDao.getCountByCriteria(filter);
        } catch (DaoException e) {
            throw new ServiceException("查询客户变更记录数量失败", e);
        }
    }

    @Override
    public PageResult<CustomerChangeRecord> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
        try {
            return customerChangeRecordDao.findByPages(pageSize, currentPage, filter);
        } catch (Exception e) {
            logger.error("分页查询客户变更记录信息失败", e);
            throw new ServiceException("分页查询客户变更记录信息失败", e);
        }
    }

    @Override
    public List<CustomerChangeRecord> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
        try {
            return customerChangeRecordDao.findByFilter(size, start, filter);
        } catch (DaoException e) {
            logger.error("查询客户变更记录信息失败", e);
            throw new ServiceException("查询客户变更记录信息失败", e);
        }
    }

    @Override
    public List<CustomerChangeRecord> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
        try {
            return customerChangeRecordDao.queryAllBySearchFilter(filter);
        } catch (DaoException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    @Override
    public List<CustomerChangeRecord> findByHql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
        try {
            return customerChangeRecordDao.findByHql(hql, params, maxCount);
        } catch (DaoException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("HQL查询客户变更记录失败", e);
        }
    }

    @Override
    public boolean saveByBatch(List<CustomerChangeRecord> objs) throws ServiceException {
        try {
            return customerChangeRecordDao.saveByBatch(objs);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException(e);
        }
    }

    /**
     * 查询客户变更统计
     *
     * @param user    当前登录用户
     * @param deptIds 部门
     * @param userIds  用户
     * @param month   月份
     * @return 统计信息
     */
    @Override
    public List<CustomerChangeVo> queryCustomerChangeInfo(OnlineUser user, List<String> deptIds, List<String> userIds , Date month) {
        // 销售客户统计数据
        List<CustomerChangeVo> customerChangeList = queryCustomerCount(user, deptIds, userIds, month);
        if (customerChangeList == null || customerChangeList.isEmpty()) {
            return null;
        }
        // 记录变更统计数据
        Map<String, List<ChangeCountDto>> statisticsMap = queryChangeStatistics(customerChangeList.stream()
                .map(CustomerChangeVo::getUserId).collect(Collectors.toList()), month);
        if (statisticsMap != null && !statisticsMap.isEmpty()) {
            customerChangeList.forEach(change -> {
                List<ChangeCountDto> changeCounts = statisticsMap.get(change.getUserId());
                if (changeCounts != null && !changeCounts.isEmpty()) {
                    // 从 合同客户 降级数量（1 -> X）
					int downFromContractCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.CONTRACTED.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownFromContractCount(downFromContractCount);

                    // 升级到 合同客户数量(X -> 1)
                    int upToContractCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.CONTRACTED.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpToContractCount(upToContractCount);

                    // 升级到 测试客户数量(X -> 2)
                    int upToTestCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.TESTING.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpToTestCount(upToTestCount);

                    // 降级到 测试客户数量(X -> 2)
                    int downToTestCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.TESTING.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownToTestCount(downToTestCount);

                    // 从 测试客户 降级数量（2 -> X）
                    int downFromTestCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.TESTING.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownFromTestCount(downFromTestCount);

                    // 从 测试客户 升级数量（2 -> X）
                    int upFromTestCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.TESTING.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpFromTestCount(upFromTestCount);

                    // 升级到 意向客户数量(X -> 3)
                    int upToIntentionCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.INTENTION.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpToIntentionCount(upToIntentionCount);

                    // 降级到 意向客户数量(X -> 3)
                    int downToIntentionCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.INTENTION.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownToIntentionCount(downToIntentionCount);

                    // 从 意向客户 降级数量（3 -> X）
                    int downFromIntentionCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.INTENTION.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownFromTestCount(downFromIntentionCount);

                    // 从 意向客户 升级数量（3 -> X）
                    int upFromIntentionCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.INTENTION.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpFromTestCount(upFromIntentionCount);

                    // 升级到 沉默客户数量(X -> 3)
                    int upToSilenceCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.SILENCE.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpToSilenceCount(upToSilenceCount);

                    // 降级到 沉默客户数量(X -> 3)
                    int downToSilenceCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.SILENCE.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownToSilenceCount(downToSilenceCount);

                    // 从 沉默客户 降级数量（3 -> X）
                    int downFromSilenceCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.SILENCE.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownFromSilenceCount(downFromSilenceCount);

                    // 从 沉默客户 升级数量（3 -> X）
                    int upFromSilenceCount = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.SILENCE.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpFromSilenceCount(upFromSilenceCount);

                    // 降到公共池 ->t5
                    int downToPublic = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.DOWNGRADE.ordinal() && statistic.getTo() == CustomerTypeValue.PUBLIC.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setDownToPublic(downToPublic);

                    // 从公共池升上来 f5->
                    int upFromPublic = changeCounts.stream().filter(statistic -> statistic.getChangeType() == CustomerChangeType.UPGRADE.ordinal() && statistic.getFrom() == CustomerTypeValue.PUBLIC.getCode())
                            .mapToInt(ChangeCountDto::getCount).sum();
                    change.setUpFromPublic(upFromPublic);
                }
            });
            statisticsMap.clear();
        }
        customerChangeList.sort((Comparator.comparing(CustomerChangeVo::getDeptId)));
        return customerChangeList;
    }

    /**
     * 查询 客户的数量
     *
     * @param user    登录用户
     * @param deptIds 查询部门
     * @param userIds 查询用户
     * @param month   月份
     * @return 客户的统计数量
     */
    private List<CustomerChangeVo> queryCustomerCount(OnlineUser user, List<String> deptIds, List<String> userIds, Date month) {
        // 查询所有销售信息（分开查询）
        StringBuilder userHql = new StringBuilder("select c.ossuserid,c.deptid,u.realname,c.customertypeid,count(1) as total," +
                " sum(c.wtime <:endTime and c.wtime >= :startTime) as t " +
                " from erp_customer c left join erp_user as u on c.ossuserid = u.ossuserid  where c.customertypeid is not null ");
        Map<String, Object> userParam = new HashMap<>();
        userParam.put("startTime", month);
        userParam.put("endTime", DateUtil.getNextMonthFirst(month));
        // 部门信息
        List<String> deptIdList = departmentService.getDeptIdsByPermission(user);
        if (deptIds != null && deptIdList != null) {
            deptIdList.retainAll(deptIds);
        }
        if (!CollectionUtils.isEmpty(deptIdList) || !CollectionUtils.isEmpty(userIds)) {
            if (!CollectionUtils.isEmpty(userIds)) {
                // 精确筛选用户
                userHql.append(" and c.ossuserid in(:userIds) ");
                userParam.put("userIds", userIds);
            }
            if (!CollectionUtils.isEmpty(deptIds)) {
                // 部门
                userHql.append(" and c.deptid in(:deptIds)");
                userParam.put("deptIds", deptIdList);
            }
        } else {
            // 查看自己
            userHql.append(" and c.ossuserid =:userId ");
            userParam.put("userId", user.getUser().getOssUserId());
        }
        userHql.append(" group by c.ossuserid,c.customertypeid order by c.deptid,u.realname ");
        List<Object> userInfoList = null;
        try {
            userInfoList = baseDao.selectSQL(userHql.toString(), userParam);
        } catch (BaseException e) {
            logger.error("查询用户异常");
        }
        if (userInfoList == null || userInfoList.isEmpty()) {
            return null;
        }
        // 客户类型
        List<CustomerType> customerTypes = customerTypeService.findAllCustomerType();
        // 类型id -> 类型值
        Map<String, Integer> typeIdMapValue = customerTypes.stream().collect(Collectors.toMap(CustomerType::getCustomerTypeId, CustomerType::getCustomerTypeValue, (o, n) -> n));
        customerTypes.clear();
        // 用来存储销售客户信息
        Map<String, CustomerChangeVo> saleCustomerChangeInfo = new HashMap<>();
        userInfoList.forEach(row -> {
            if (row.getClass().isArray()) {
                Object[] userInfo = (Object[]) row;
                if (userInfo.length >= 6) {
                    // 部门名称 后面单独查询
                    //  c.ossUserId,c.deptId,u.realName,c.customerTypeId,count(1) as total
                    String saleUserId = String.valueOf(userInfo[0]);
                    CustomerChangeVo change = saleCustomerChangeInfo.get(saleUserId);
                    if (change == null) {
                        change = new CustomerChangeVo();
                        change.setUserId(String.valueOf(userInfo[0]));
                        change.setDeptId(String.valueOf(userInfo[1]));
                        change.setUserName(String.valueOf(userInfo[2]));
                    }
                    // HQL已经判断空了
                    String customerTypeId = String.valueOf(userInfo[3]);
                    int count = 0;
                    if (userInfo[4] != null && userInfo[4] instanceof Number) {
                        count = ((Number) userInfo[4]).intValue();
                    }
                    if (count > 0) {
                        Integer type = typeIdMapValue.get(customerTypeId);
                        if (type != null) {
                            if (CustomerTypeValue.CONTRACTED.getCode() == type) {
                                change.setContractTotal(count);
                            } else if (CustomerTypeValue.TESTING.getCode() == type) {
                                change.setTestTotal(count);
                            } else if (CustomerTypeValue.INTENTION.getCode() == type) {
                                change.setIntentionTotal(count);
                            } else if (CustomerTypeValue.SILENCE.getCode() == type) {
                                change.setSilenceTotal(count);
                            }
                        }
                    }
                    if (userInfo[5] != null && userInfo[5] instanceof Number) {
                        int monthCount = ((Number) userInfo[5]).intValue();
                        change.setMonthChangeCount(change.getMonthChangeCount() + monthCount);
                    }
                    saleCustomerChangeInfo.put(saleUserId, change);
                }
            }
        });
        userInfoList.clear();
        if (saleCustomerChangeInfo.isEmpty()) {
            return null;
        }
        List<CustomerChangeVo> customerChangeList = new ArrayList<>(saleCustomerChangeInfo.values());
        // 获取部门名称
        Map<String, String> deptNames = departmentService.queryDeptName(customerChangeList.stream().map(CustomerChangeVo::getDeptId).collect(Collectors.toList()));
        customerChangeList.forEach(change -> change.setDeptName(deptNames.get(change.getDeptId())));
        deptNames.clear();
        // 统计数据
        return customerChangeList;
    }

    /**
     * 查询销售的客户变更统计信息
     *
     * @param saleIds 销售ID
     * @param month   月份
     * @return 时间内 客户变更统计信息
     */
    private Map<String, List<ChangeCountDto>> queryChangeStatistics(List<String> saleIds, Date month) {
        // 查询变更记录信息
        Map<String, Object> changeParam = new HashMap<>();
        changeParam.put("saleIds", saleIds);
        changeParam.put("startTime", month);
        changeParam.put("endTime", DateUtil.getNextMonthFirst(month));
        // 统计变更记录SQL
        String changeCountSql = "select r.ossuserid, r.origincustomertype, r.nowcustomertype, changetype , " +
                " count(1) as total from erp_customer_change_record r where r.origincustomertype != r.nowcustomertype " +
                " and r.ossuserid in(:saleIds) and r.changetime>=:startTime and r.changetime <:endTime group by r.ossuserid, r.origincustomertype, r.nowcustomertype, r.changetype ";
        List<Object> changeCountList = null;
        try {
            changeCountList = baseDao.selectSQL(changeCountSql, changeParam);
        } catch (BaseException e) {
            logger.error("通过SQL{}查询变更统计数据异常{}", changeCountSql, e);
        }
        if (changeCountList == null || changeCountList.isEmpty()) {
            return null;
        }
        // 组装数据
        Map<String, List<ChangeCountDto>> statisticsMap = new HashMap<>();
        changeCountList.forEach(row -> {
            ChangeCountDto statisticsDto = new ChangeCountDto();
            if (statisticsDto.setObjectInfo(row)) {
                List<ChangeCountDto> statisticsList = statisticsMap.get(statisticsDto.getUserId());
                if (statisticsList == null) {
                    statisticsList = new ArrayList<>();
                }
                statisticsList.add(statisticsDto);
                statisticsMap.put(statisticsDto.getUserId(),statisticsList);
            }
        });
        return statisticsMap;
    }
}
