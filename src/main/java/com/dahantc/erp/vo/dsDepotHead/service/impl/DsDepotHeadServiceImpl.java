package com.dahantc.erp.vo.dsDepotHead.service.impl;

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
import com.dahantc.erp.dto.dsDepot.DsSaveDepotDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.dsDepotHead.dao.IDsDepotHeadDao;
import com.dahantc.erp.vo.dsDepotHead.entity.DsDepotHead;
import com.dahantc.erp.vo.dsDepotHead.service.IDsDepotHeadService;
import com.dahantc.erp.vo.dsDepotItem.entity.DsDepotItem;
import com.dahantc.erp.vo.dsDepotItem.service.IDsDepotItemService;
import com.dahantc.erp.vo.user.entity.User;

@Service("dsDepotHeadService")
public class DsDepotHeadServiceImpl implements IDsDepotHeadService {
	private static Logger logger = LogManager.getLogger(DsDepotHeadServiceImpl.class);

	@Autowired
	private IDsDepotHeadDao dsDepotHeadDao;

	@Autowired
	private IDsDepotItemService dsDepotItemService;

//	@Autowired
//	private IDianShangProductService dianShangProductService;

	@Override
	public DsDepotHead read(Serializable id) throws ServiceException {
		try {
			return dsDepotHeadDao.read(id);
		} catch (Exception e) {
			logger.error("读取电商入库表失败", e);
			throw new ServiceException("读取电商入库表失败", e);
		}
	}

	@Override
	public boolean save(DsDepotHead entity) throws ServiceException {
		try {
			return dsDepotHeadDao.save(entity);
		} catch (Exception e) {
			logger.error("保存电商入库表失败", e);
			throw new ServiceException("保存电商入库表失败", e);
		}
	}

	@Override
	public boolean saveByBatch(List<DsDepotHead> objs) throws ServiceException {
		try {
			return dsDepotHeadDao.saveByBatch(objs);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(Serializable id) throws ServiceException {
		try {
			return dsDepotHeadDao.delete(id);
		} catch (Exception e) {
			logger.error("删除电商入库表失败", e);
			throw new ServiceException("删除电商入库表失败", e);
		}
	}

	@Override
	public boolean update(DsDepotHead enterprise) throws ServiceException {
		try {
			return dsDepotHeadDao.update(enterprise);
		} catch (Exception e) {
			logger.error("更新电商入库表失败", e);
			throw new ServiceException("更新电商入库表失败", e);
		}
	}

	@Override
	public int getCount(SearchFilter filter) throws ServiceException {
		try {
			return dsDepotHeadDao.getCountByCriteria(filter);
		} catch (Exception e) {
			logger.error("查询电商入库表数量失败", e);
			throw new ServiceException("查询电商入库表数量失败", e);
		}
	}

	@Override
	public PageResult<DsDepotHead> queryByPages(int pageSize, int currentPage, SearchFilter filter)
			throws ServiceException {
		try {
			return dsDepotHeadDao.findByPages(pageSize, currentPage, filter);
		} catch (Exception e) {
			logger.error("查询电商入库表分页信息失败", e);
			throw new ServiceException("查询电商入库表分页信息失败", e);
		}
	}

	@Override
	public List<DsDepotHead> findByFilter(int size, int start, SearchFilter filter) throws ServiceException {
		try {
			return dsDepotHeadDao.findByFilter(size, start, filter);
		} catch (Exception e) {
			logger.error("查询电商入库表失败", e);
			throw new ServiceException("查询电商入库表失败", e);
		}
	}

	@Override
	public List<DsDepotHead> queryAllBySearchFilter(SearchFilter filter) throws ServiceException {
		try {
			return dsDepotHeadDao.queryAllBySearchFilter(filter);
		} catch (Exception e) {
			logger.error("查询电商入库表失败", e);
			throw new ServiceException("查询电商入库表失败", e);
		}
	}

	@Override
	public List<DsDepotHead> findByhql(String hql, Map<String, Object> params, int maxCount) throws ServiceException {
		try {
			return dsDepotHeadDao.findByhql(hql, params, maxCount);
		} catch (Exception e) {
			logger.error("查询电商入库表失败", e);
			throw new ServiceException("查询电商入库表失败", e);
		}
	}

	/**
	 * 保存产品基本信息
	 * 
	 * @param creatorid
	 */
	@Override
	public BaseResponse<String> saveDepotHead(DsSaveDepotDto dto, User user) throws ServiceException {
		long start = System.currentTimeMillis();
		// 判断是否是添加
		boolean isCreate = StringUtils.isBlank(dto.getId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.format1);
		Date date = new Date();
		try {
			DsDepotHead dsDepotHead = new DsDepotHead();
			if (!isCreate) {
				dsDepotHead = this.read(dto.getId());
				if (dsDepotHead == null) {
					logger.info("要修改的入库信息不存在，Id：" + dto.getId());
					return BaseResponse.error("要修改的入库信息不存在");
				}
				dsDepotHead.setUpdateId(user.getOssUserId());
				dsDepotHead.setUpdateName(user.getRealName());
				dsDepotHead.setUpdateTime(date);
			} else {
				dsDepotHead.setCreaterId(user.getOssUserId());
				dsDepotHead.setCreaterName(user.getRealName());
				dsDepotHead.setWtime(date);
				dsDepotHead.setDepotCode(bulidDsDepotCode(user));
			}
			dsDepotHead.setSupplierId(dto.getSupplierId());
			dsDepotHead.setSupplierName(dto.getSupplierName());
			Date buyTime = simpleDateFormat.parse(dto.getBuyTime());
			dsDepotHead.setBuyTime(buyTime);
			dsDepotHead.setIsDelete(1);;
			dsDepotHead.setOtherCost(dto.getOtherCost());
			dsDepotHead.setRemark(dto.getRemark());
			dsDepotHead.setVerifyStatus(0);
			List<DsDepotItem> dsDepotItems = null;
			if (StringUtil.isNotBlank(dto.getDsDepotItems())) {
				dsDepotItems = JSON.parseArray(StringEscapeUtils.unescapeHtml4(dto.getDsDepotItems()), DsDepotItem.class);
			}
			if (CollectionUtils.isEmpty(dsDepotItems)) {
				logger.error("入库详情不存在，添加失败");
				return BaseResponse.error("入库详情不存在，添加失败");
			}
			BigDecimal depotCosts = BigDecimal.ZERO;
			boolean result = false;
			for (DsDepotItem dsDepotItem : dsDepotItems) {
				BigDecimal depotCost = dsDepotItem.getPrice().multiply(new BigDecimal(dsDepotItem.getAmount()));
				depotCosts = depotCosts.add(depotCost);
				dsDepotItem.setTotal(depotCost);
				dsDepotItem.setIsDelete(1);
			}
			depotCosts = depotCosts.add(dsDepotHead.getOtherCost());
			dsDepotHead.setDepotCost(depotCosts);
			if (isCreate) {
				result = save(dsDepotHead);
			} else {
				result = update(dsDepotHead);
			}
			if (!result) {
				return BaseResponse.error("保存失败");
			}
			if (isCreate) {
				String productName = "";
				for (DsDepotItem dsDepotItem : dsDepotItems) {
					dsDepotItem.setDepotHeadId(dsDepotHead.getId());
					productName = productName + "," + dsDepotItem.getProductName();
				}
				productName = productName.substring(0, productName.length() -1);
				dsDepotHead.setProductName(productName);
				result = dsDepotItemService.saveByBatch(dsDepotItems);
				update(dsDepotHead);
			} else {
				SearchFilter filter = new SearchFilter();
				filter.getRules().add(new SearchRule("depotHeadId", Constants.ROP_EQ, dsDepotHead.getId()));
				List<DsDepotItem> deleteDsDepotItems = dsDepotItemService.queryAllBySearchFilter(filter);
				for (DsDepotItem dsDepotItem : deleteDsDepotItems) {
					dsDepotItemService.delete(dsDepotItem.getId());
				}
				String productName = "";
				for (DsDepotItem dsDepotItem : dsDepotItems) {
					productName = productName + "," + dsDepotItem.getProductName();
				}
				productName = productName.substring(0, productName.length() -1);
				dsDepotHead.setProductName(productName);
				update(dsDepotHead);
				result = dsDepotItemService.saveByBatch(dsDepotItems);
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
	public BaseResponse<String> auditDepotHead(String id, User user) throws ServiceException {
		DsDepotHead dsDepotHead = this.read(id);
		dsDepotHead.setVerifyStatus(1);
		dsDepotHead.setAuditId(user.getOssUserId());
		dsDepotHead.setAuditName(user.getRealName());
		boolean result = save(dsDepotHead);
		if (!result) {
			return BaseResponse.error("审核失败");
		} 
//		else {
//			SearchFilter filter = new SearchFilter();
//			filter.getRules().add(new SearchRule("depotheadid", Constants.ROP_EQ, user.getOssUserId()));
//			List<DsDepotItem> dsDepotItems = dsDepotItemService.queryAllBySearchFilter(filter);
//			for (DsDepotItem dsDepotItem : dsDepotItems) {
//				DianShangProduct dianShangProduct = dianShangProductService.read(dsDepotItem.getProductId());
//				dianShangProduct.setStock(dianShangProduct.getStock() + dsDepotItem.getAmount());
//				dianShangProductService.save(dianShangProduct);
//			}
//			return BaseResponse.success("审核完成");
//		}
		return BaseResponse.success("审核完成");
	}

	@Override
	public BaseResponse<String> deleteDepotHead(String id, User user) throws ServiceException {
		boolean result = false;
		DsDepotHead dsDepotHead = this.read(id);
		dsDepotHead.setIsDelete(0);
		dsDepotHead.setAuditId(user.getOssUserId());
		dsDepotHead.setAuditName(user.getRealName());
		result = save(dsDepotHead);
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
