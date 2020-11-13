package com.dahantc.erp.vo.dsOrder.service.impl;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.SendType;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dsOrder.dao.IDsOrderDao;
import com.dahantc.erp.vo.dsOrder.entity.DsOrder;
import com.dahantc.erp.vo.dsOrder.service.IDsOrderService;
import com.dahantc.erp.vo.user.entity.User;

@Service("dsOrderService")
public class DsOrderServiceImpl implements IDsOrderService {
	private static Logger logger = LogManager.getLogger(DsOrderServiceImpl.class);

	@Autowired
	private IDsOrderDao dsOrderDao;

	@Override
	public DsOrder read(Serializable id) throws ServiceException {
		try {
			return dsOrderDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商订单表失败", e);
			throw new ServiceException("读取电商订单表失败", e);
		}
	}

	@Override
	public boolean save(DsOrder entity) throws ServiceException {
		try {
			return dsOrderDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商订单表失败", e);
			throw new ServiceException("保存电商订单表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsOrder> objs) throws ServiceException {
		try {
			return dsOrderDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsOrderDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商订单表失败", e);
			throw new ServiceException("删除电商订单表失败", e);
		}
	}

	@Override
	public boolean update(DsOrder enterprise) throws ServiceException {
		try {
			return dsOrderDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商订单表失败", e);
			throw new ServiceException("更新电商订单表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商订单表数量失败", e);
			throw new ServiceException("查询电商订单表数量失败", e);
		}
	}

	@Override
	public PageResult<DsOrder> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商订单表分页信息失败", e);
			throw new ServiceException("查询电商订单表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsOrder> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商订单表失败", e);
			throw new ServiceException("查询电商订单表失败", e);
		}
	}

	@Override
	public List<DsOrder> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsOrderDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商订单表失败", e);
			throw new ServiceException("查询电商订单表失败", e);
		}
	}
	
	@Override
	public List<DsOrder> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsOrderDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商订单表失败", e);
			throw new ServiceException("查询电商订单表失败", e);
		}
	}

	@Override
	public String buildOrderNo(User user, Date date, String sendType) throws ServiceException {
		String orderNo = null;
		
		if (StringUtil.isBlank(sendType)) {
			sendType = "2";
		}
		//获取6位时间字符串
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        String dateString = simpleDateFormat.format(date);
        //获取7位工号字符串
        String[] tmp = user.getContacteMail().split("@");
        String mailName = spiltRtoL(tmp[0]);
        if (mailName.length() < 7) {
        	mailName = spiltRtoL(mailName.substring(0, 4));
        	mailName = "021" + mailName;
		}else {
			mailName = spiltRtoL(mailName.substring(0, 7));
		}
        //获取下一个流水号
        SearchFilter filter = new SearchFilter();
        filter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, user.getOssUserId()));
        Date startDate = DateUtil.getCurrentStartDateTime();
        Date endDate = DateUtil.getCurrentEndDateTime();
        filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, startDate));
        filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
        String serialNo = String.valueOf(getCount(filter) + 1); 
        if (serialNo.length()<=1) {
        	serialNo = "0"+serialNo;
		}
        //获取发送方式的数字形式
        if (sendType.equals(SendType.GROUP.getDesc())) {
			
		} else if (sendType.equals(SendType.SINGLE.getDesc())) {
			
		}
        int sendtype = SendType.GROUP.getCode();
        Optional<SendType> sendTypeOpt = SendType.getEnumsByMsg(sendType);
		if (sendTypeOpt.isPresent()) {
			sendtype = sendTypeOpt.get().getCode();
		}
		//获取订单号
        orderNo = dateString + mailName + serialNo + sendtype;
		return orderNo;
	}
	
	   /**
     * @描述 TODO : 将指定的字符串进行倒转
     * @参数 [s]  要倒转的字符串
     * @返回值 java.lang.String 倒转后的字符串
     */
    public static String spiltRtoL(String s) {
        StringBuffer sb = new StringBuffer();
        int length = s.length();
        char[] c = new char[length];
        for (int i = 0; i < length; i++) {
            c[i] = s.charAt(i);
        }
        for (int i = length - 1; i >= 0; i--) {
            sb.append(c[i]);
        }
        return sb.toString();
    }
    
}
