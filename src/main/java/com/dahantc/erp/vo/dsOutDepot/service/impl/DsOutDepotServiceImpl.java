package com.dahantc.erp.vo.dsOutDepot.service.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.dsOutDepot.DsSaveOutDepotDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dsOutDepot.dao.IDsOutDepotDao;
import com.dahantc.erp.vo.dsOutDepot.entity.DsOutDepot;
import com.dahantc.erp.vo.dsOutDepot.service.IDsOutDepotService;
import com.dahantc.erp.vo.dsOutDepotDetail.entity.DsOutDepotDetail;
import com.dahantc.erp.vo.dsOutDepotDetail.service.IDsOutDepotDetailService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Service("dsOutDepotService")
public class DsOutDepotServiceImpl implements IDsOutDepotService {
	private static Logger logger = LogManager.getLogger(DsOutDepotServiceImpl.class);

	@Autowired
	private IDsOutDepotDao dsOutDepotDao;

	@Autowired
	private IDsOutDepotDetailService dsOutDepotDetailService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ICustomerService customerService;
	
	@Override
	public DsOutDepot read(Serializable id) throws ServiceException {
		try {
			return dsOutDepotDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商出库表失败", e);
			throw new ServiceException("读取电商出库表失败", e);
		}
	}

	@Override
	public boolean save(DsOutDepot entity) throws ServiceException {
		try {
			return dsOutDepotDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商出库表失败", e);
			throw new ServiceException("保存电商出库表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsOutDepot> objs) throws ServiceException {
		try {
			return dsOutDepotDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsOutDepotDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商出库表失败", e);
			throw new ServiceException("删除电商出库表失败", e);
		}
	}

	@Override
	public boolean update(DsOutDepot enterprise) throws ServiceException {
		try {
			return dsOutDepotDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商出库表失败", e);
			throw new ServiceException("更新电商出库表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商出库表数量失败", e);
			throw new ServiceException("查询电商出库表数量失败", e);
		}
	}

	@Override
	public PageResult<DsOutDepot> queryByPages(int pageSize, int currentPage, SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商出库表分页信息失败", e);
			throw new ServiceException("查询电商出库表分页信息失败", e);
		}
	}
	
	@Override
	public List<DsOutDepot> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商出库表失败", e);
			throw new ServiceException("查询电商出库表失败", e);
		}
	}

	@Override
	public List<DsOutDepot> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsOutDepotDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商出库表失败", e);
			throw new ServiceException("查询电商出库表失败", e);
		}
	}
	
	@Override
	public List<DsOutDepot> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsOutDepotDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商出库表失败", e);
			throw new ServiceException("查询电商出库表失败", e);
		}
	}
	
	/**
	 * 保存产品基本信息
	 * 
	 * @param creatorid
	 */
	@Override
	public BaseResponse<String> saveDsOutDepot(DsSaveOutDepotDto dto, User user) throws ServiceException {
		long start = System.currentTimeMillis();
		// 判断是否是添加
		boolean isCreate = StringUtils.isBlank(dto.getId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.format1);
		Date date = new Date();
		try {
			DsOutDepot dsOutDepot = new DsOutDepot();
			if (!isCreate) {
				dsOutDepot = this.read(dto.getId());
				if (dsOutDepot == null) {
					logger.info("要修改的入库信息不存在，Id：" + dto.getId());
					return BaseResponse.error("要修改的入库信息不存在");
				}
				dsOutDepot.setUpdateId(user.getOssUserId());
				dsOutDepot.setUpdateName(user.getRealName());
				dsOutDepot.setUpdateTime(date);
			} else {
				dsOutDepot.setCreaterId(user.getOssUserId());
				dsOutDepot.setCreaterName(user.getRealName());
				dsOutDepot.setWtime(date);
				dsOutDepot.setOutDepotCode(bulidDsDepotCode(user));
			}
			dsOutDepot.setCustomerId(dto.getCustomerId());
			Customer outDepotCustomer = customerService.read(dto.getCustomerId());
			if (outDepotCustomer != null) {
				dsOutDepot.setCustomerName(outDepotCustomer.getCompanyName());
			}
			Date outTime = simpleDateFormat.parse(dto.getOutTime());
			dsOutDepot.setOutTime(outTime);
			dsOutDepot.setIsDelete(1);;
			dsOutDepot.setOtherCost(dto.getOtherCost());
			dsOutDepot.setRemark(dto.getRemark());
			dsOutDepot.setVerifyStatus(dto.getVerifyStatus());
			dsOutDepot.setOutDepotPersonId(dto.getUserId());
			User outDepotUser = userService.read(dto.getUserId());
			if (outDepotUser != null) {
				dsOutDepot.setOutDepotPersonName(outDepotUser.getRealName());
			}
			List<DsOutDepotDetail> dsOutDepotDetials = null;
			if (StringUtil.isNotBlank(dto.getDsOutDepotDetials())) {
				dsOutDepotDetials = JSON.parseArray(StringEscapeUtils.unescapeHtml4(dto.getDsOutDepotDetials()), DsOutDepotDetail.class);
			}
			if (CollectionUtils.isEmpty(dsOutDepotDetials)) {
				logger.error("出库详情不存在，添加失败");
				return BaseResponse.error("出库详情不存在，添加失败");
			}
			BigDecimal outDepotTotal = BigDecimal.ZERO;
			boolean result = false;
			for (DsOutDepotDetail dsOutDepotDetial : dsOutDepotDetials) {
				BigDecimal depotCost = dsOutDepotDetial.getPrice().multiply(new BigDecimal(dsOutDepotDetial.getAmount()));
				outDepotTotal = outDepotTotal.add(depotCost);
				if (dsOutDepotDetial.getValidTime() != null) {
					String validTime = simpleDateFormat.format(dsOutDepotDetial.getValidTime());
					Date changeValidTime = simpleDateFormat.parse(validTime);
					dsOutDepotDetial.setValidTime(changeValidTime);
				}
				dsOutDepotDetial.setTotal(depotCost);
				dsOutDepotDetial.setIsDelete(1);
			}
			outDepotTotal = outDepotTotal.add(dsOutDepot.getOtherCost());
			dsOutDepot.setOutDepotTotal(outDepotTotal);
			if (isCreate) {
				result = save(dsOutDepot);
			} else {
				result = update(dsOutDepot);
			}
			if (!result) {
				return BaseResponse.error("保存失败");
			}
			if (isCreate) {
				String productName = "";
				for (DsOutDepotDetail dsOutDepotDetial : dsOutDepotDetials) {
					dsOutDepotDetial.setOutDepotId(dsOutDepot.getId());
					productName = productName + "," + dsOutDepotDetial.getProductName();
				}
				productName = productName.substring(0, productName.length() -1);
				dsOutDepot.setProductName(productName);
				result = dsOutDepotDetailService.saveByBatch(dsOutDepotDetials);
				update(dsOutDepot);
			} else {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("depotHeadId", Constants.ROP_EQ, dsOutDepot.getId()));
				List<DsOutDepotDetail> deleteDsOutDepotDetails = dsOutDepotDetailService.queryAllBySearchFilter(filter);
				for (DsOutDepotDetail dsOutDepotDetail : deleteDsOutDepotDetails) {
					dsOutDepotDetailService.delete(dsOutDepotDetail.getId());
				}
				String productName = "";
				for (DsOutDepotDetail dsOutDepotDetail : deleteDsOutDepotDetails) {
					productName = productName + "," + dsOutDepotDetail.getProductName();
				}
				productName = productName.substring(0, productName.length() -1);
				dsOutDepot.setProductName(productName);
				update(dsOutDepot);
				result = dsOutDepotDetailService.saveByBatch(dsOutDepotDetials);
				if (!result) {
					return BaseResponse.error("保存失败");
				}
			}
			if (!result) {
				return BaseResponse.error("保存失败");
			}
		} catch (Exception e) {
			logger.error("保存失败", e);
			return BaseResponse.error("保存失败");
		}
		logger.info("保存产品信息成功,共耗时:" + (System.currentTimeMillis() - start));
		return BaseResponse.success("保存成功");
	}

	@Override
	public BaseResponse<String> auditDsOutDepot(String id, User user) throws ServiceException {
		DsOutDepot dsOutDepot = this.read(id);
		dsOutDepot.setVerifyStatus(1);
		dsOutDepot.setAuditId(user.getOssUserId());
		dsOutDepot.setAuditName(user.getRealName());
		boolean result = save(dsOutDepot);
		if (!result) {
			return BaseResponse.error("审核失败");
		} 
		return BaseResponse.success("审核完成");
	}

	@Override
	public BaseResponse<String> deleteDsOutDepot(String id, User user) throws ServiceException {
		boolean result = false;
		DsOutDepot dsOutDepot = this.read(id);
		dsOutDepot.setIsDelete(0);
		dsOutDepot.setAuditId(user.getOssUserId());
		dsOutDepot.setAuditName(user.getRealName());
		result = save(dsOutDepot);
		if (!result) {
			return BaseResponse.error("删除失败");
		}
		SearchFilter dsDepotItemFilter = new SearchFilter();
		dsDepotItemFilter.getRules().add(new SearchRule("outDepotId", Constants.ROP_EQ, dsOutDepot.getId()));
		List<DsOutDepotDetail> dsOutDepotDetails = dsOutDepotDetailService.queryAllBySearchFilter(dsDepotItemFilter);
		for (DsOutDepotDetail dsOutDepotDetail : dsOutDepotDetails) {
			dsOutDepotDetail.setIsDelete(0);
		}
		result = dsOutDepotDetailService.saveByBatch(dsOutDepotDetails);
		if (!result) {
			return BaseResponse.error("删除失败");
		}
		return BaseResponse.success("删除成功");
	}
	
	public String bulidDsDepotCode(User user) throws ServiceException {
		Date date = new Date();
		String dsDepotCode = null;
		//获取6位时间字符串
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        String dateString = simpleDateFormat.format(date);
        //获取下一个流水号
        SearchFilter filter = new SearchFilter();
        Date startDate = DateUtil.getThisYearFirst();
        Date endDate = DateUtil.getCurrYearLast();
        filter.getRules().add(new SearchRule("createrId", Constants.ROP_EQ, user.getOssUserId()));
        filter.getRules().add(new SearchRule("wtime", Constants.ROP_GT, startDate));
        filter.getRules().add(new SearchRule("wtime", Constants.ROP_LT, endDate));
        String serialNo = String.valueOf(getCount(filter) + 1); 
        String[] tmp = user.getContacteMail().split("@");
        String mailName = spiltRtoL(tmp[0]);
        if (mailName.length() < 7) {
        	mailName = spiltRtoL(mailName.substring(0, 4));
		}else {
			mailName = spiltRtoL(mailName.substring(3, 7));
		}
        if (serialNo.length()==1) {
        	serialNo = "00"+serialNo;
		}else if (serialNo.length()==2) {
        	serialNo = "0"+serialNo;
		}
		//获取订单号
        dsDepotCode = dateString + mailName + serialNo;
		return dsDepotCode;
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
