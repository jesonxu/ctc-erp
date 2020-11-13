package com.dahantc.erp.controller.receivableaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.receivableaccount.ReceivableAccountDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.dept.entity.Department;
import com.dahantc.erp.vo.dept.service.IDepartmentService;
import com.dahantc.erp.vo.invoice.service.IInvoiceService;
import com.dahantc.erp.vo.invoice.service.InvoiceExtendDto;
import com.dahantc.erp.vo.productBills.service.IProductBillsService;
import com.dahantc.erp.dto.bill.ProductBillsExtendDto;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;

@Controller
@RequestMapping("/receivableAccount")
public class ReceivableAccountAction extends BaseAction {

	public static final Logger logger = LoggerFactory.getLogger(ReceivableAccountAction.class);

	@Autowired
	private IProductBillsService productBillsService;

	@Autowired
	private IDepartmentService departmentService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IInvoiceService invoiceService;

	@Autowired
	private ICustomerService customerService;

	@SuppressWarnings("deprecation")
	@ResponseBody
	@PostMapping("/getAllReceivableAccount")
	public BaseResponse<Object> getAllReceivableAccount(@RequestParam(required = false) String userId, @RequestParam(required = false) String deptId,
			@RequestParam(required = false) String queryDate, @RequestParam(required = false) String settleType) {
		// 用户id 部门 id 查询年份
		try {
			OnlineUser onlineUser = getOnlineUserAndOnther();
			if (null == onlineUser) {
				return BaseResponse.noLogin("请先登录");
			}

			long _start = System.currentTimeMillis();
			logger.info("查询应收账款开始。。。");

			Date yearDate = DateUtil.convert(DateUtil.convert(new Date(), DateUtil.format11), DateUtil.format11);
			if (StringUtils.isNotBlank(queryDate)) {
				yearDate = DateUtil.convert(queryDate, DateUtil.format11);
			}

			List<ReceivableAccountDto> resultList = new ArrayList<>();
			List<String> deptIdList = null;

			SearchFilter userFilter = new SearchFilter();
			if (!StringUtils.isAllBlank(userId, deptId)) {
				if (StringUtils.isNoneBlank(userId, deptId)) {
					userFilter.getOrRules().add(new SearchRule[] { new SearchRule("ossUserId", Constants.ROP_IN, userId.split(",")),
							new SearchRule("deptId", Constants.ROP_IN, deptId.split(",")) });
				} else {
					if (StringUtils.isNotBlank(userId)) {
						userFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_IN, userId.split(",")));
					} else {
						userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptId.split(",")));
					}
				}
			} else {
				deptIdList = departmentService.getDeptIdsByPermission(onlineUser);
				if (!CollectionUtils.isEmpty(deptIdList)) {
					userFilter.getRules().add(new SearchRule("deptId", Constants.ROP_IN, deptIdList));
				} else {
					userFilter.getRules().add(new SearchRule("ossUserId", Constants.ROP_EQ, onlineUser.getUser().getOssUserId()));
				}
			}

			List<User> userList = userService.queryAllBySearchFilter(userFilter);
			List<String> userIds = new ArrayList<>();
			List<String> deptIds = new ArrayList<>();
			userIds = userList.stream().map(User::getOssUserId).collect(Collectors.toList());
			deptIds = userList.stream().map(User::getDeptId).collect(Collectors.toList());

			if (CollectionUtils.isEmpty(userIds)) {
				return BaseResponse.error("未查询到数据");
			}

			List<ProductBillsExtendDto> billDtoList = queryCompleteYearBills(yearDate, userIds, settleType);
			if (billDtoList == null) {
				billDtoList = new ArrayList<>();
			}
			if (yearDate.getYear() == new Date().getYear()) {
				List<ProductBillsExtendDto> currDtoList = queryCurrMonthBills(userIds, settleType);
				if (!CollectionUtils.isEmpty(currDtoList)) {
					billDtoList.addAll(currDtoList);
				}
			}

			List<InvoiceExtendDto> invoiceDtoList = queryInvoices(yearDate, userIds, settleType);

			SearchFilter filter = new SearchFilter();
			filter.getRules().add(new SearchRule("ossuserId", Constants.ROP_IN, userIds));
			List<Customer> customerList = customerService.queryAllBySearchFilter(filter);

			if (!(CollectionUtils.isEmpty(billDtoList) && CollectionUtils.isEmpty(invoiceDtoList) && CollectionUtils.isEmpty(customerList))) {
				filter.getRules().clear();
				filter.getRules().add(new SearchRule("deptid", Constants.ROP_IN, deptIds));
				Map<String, String> cacheDeptMap = departmentService.queryAllBySearchFilter(filter).stream()
						.collect(Collectors.toMap(Department::getDeptid, Department::getDeptname, (v1, v2) -> v2));

				Map<String, String> cacheUserMap = userList.stream().collect(Collectors.toMap(User::getOssUserId, User::getRealName, (v1, v2) -> v2));

				Map<String, String> cacheCustomerMap = customerList.stream()
						.collect(Collectors.toMap(Customer::getCustomerId, Customer::getCompanyName, (v1, v2) -> v2));

				Map<String, ReceivableAccountDto> dtoMap = new HashMap<>();
				buildReceivableAccountDto(dtoMap, billDtoList, invoiceDtoList, cacheDeptMap, cacheUserMap, cacheCustomerMap);
				resultList = dtoMap.values().stream().map(dto -> {
					BigDecimal receivable = BigDecimal.ZERO;
					for (BigDecimal value : dto.getReceivables()) {
						if (value != null) {
							receivable = receivable.add(value);
						}
					}
					BigDecimal received = BigDecimal.ZERO;
					for (BigDecimal value : dto.getReceiveds()) {
						if (value != null) {
							received = received.add(value);
						}
					}
					BigDecimal invoiced = BigDecimal.ZERO;
					for (BigDecimal value : dto.getInvoiceds()) {
						if (value != null) {
							invoiced = invoiced.add(value);
						}
					}
					dto.setNotInvoice(receivable.subtract(invoiced));
					dto.setNotReceive(receivable.subtract(received));
					dto.setInvoicedNotReceive(invoiced.subtract(received));
					return dto;
				}).filter(ReceivableAccountDto::isShow).collect(Collectors.toList());
			}
			// 排序
			if (!CollectionUtils.isEmpty(resultList)) {
				resultList.sort((dto1, dto2) -> {
					if (StringUtils.isAllBlank(dto1.getDeptName(), dto2.getDeptName()) || StringUtils.equals(dto1.getDeptName(), dto2.getDeptName())) {
						if (StringUtils.isAllBlank(dto1.getSaleName(), dto2.getSaleName()) || StringUtils.equals(dto1.getSaleName(), dto2.getSaleName())) {
							if (StringUtils.isAllBlank(dto1.getCustomerName(), dto2.getCustomerName())
									|| StringUtils.equals(dto1.getCustomerName(), dto2.getCustomerName())) {
								return 0;
							}
							String customerName1 = "";
							String customerName2 = "";
							if (StringUtils.isNotBlank(dto1.getCustomerName())) {
								customerName1 = dto1.getCustomerName();
							}
							if (StringUtils.isNotBlank(dto2.getCustomerName())) {
								customerName2 = dto2.getCustomerName();
							}
							return customerName1.compareTo(customerName2);
						}
						String saleName1 = "";
						String saleName2 = "";
						if (StringUtils.isNotBlank(dto1.getSaleName())) {
							saleName1 = dto1.getSaleName();
						}
						if (StringUtils.isNotBlank(dto2.getSaleName())) {
							saleName2 = dto2.getSaleName();
						}
						return saleName1.compareTo(saleName2);
					}
					String deptName1 = "";
					String deptName2 = "";
					if (StringUtils.isNotBlank(dto1.getDeptName())) {
						deptName1 = dto1.getDeptName();
					}
					if (StringUtils.isNotBlank(dto2.getDeptName())) {
						deptName2 = dto2.getDeptName();
					}
					return deptName1.compareTo(deptName2);
				});
			}
			logger.info("查询应收账款耗时:[" + (System.currentTimeMillis() - _start) + "]毫秒");
			return BaseResponse.success(resultList);
		} catch (

		Exception e) {
			logger.error("", e);
		}
		return BaseResponse.error("未查询到数据");
	}

	// 查询当月账单(映射表)
	private List<ProductBillsExtendDto> queryCurrMonthBills(List<String> userIds, String settleType) {
		return productBillsService.queryCurrMonthBills(userIds, settleType);
	}

	// 账单信息(除了本月)
	private List<ProductBillsExtendDto> queryCompleteYearBills(Date yearDate, List<String> userIds, String settleType) {
		return productBillsService.queryCompleteYearBills(yearDate, userIds, settleType);
	}

	// 开票信息
	private List<InvoiceExtendDto> queryInvoices(Date yearDate, List<String> userIds, String settleType) {
		return invoiceService.queryInvoices(yearDate, userIds, settleType);
	}

	@SuppressWarnings("deprecation")
	private void buildReceivableAccountDto(Map<String, ReceivableAccountDto> dtoMap, List<ProductBillsExtendDto> billDtoList,
			List<InvoiceExtendDto> invoiceDtoList, Map<String, String> cacheDeptMap, Map<String, String> cacheUserMap, Map<String, String> cacheCustomerMap) {
		billDtoList.forEach(billDto -> {
			ReceivableAccountDto dto = null;
			if (!dtoMap.containsKey(billDto.getEntityId())) {
				dto = new ReceivableAccountDto();
				dtoMap.put(billDto.getEntityId(), dto);
				dto.setCustomerId(billDto.getEntityId());
				dto.setDeptName(cacheDeptMap.get(billDto.getDeptId()));
				dto.setSaleName(cacheUserMap.get(billDto.getOssUserId()));
				dto.setCustomerName(cacheCustomerMap.get(billDto.getEntityId()));
			} else {
				dto = dtoMap.get(billDto.getEntityId());
			}
			if (dto.getReceivables()[billDto.getWtime().getMonth()] == null) {
				dto.getReceivables()[billDto.getWtime().getMonth()] = BigDecimal.ZERO;
			}
			dto.getReceivables()[billDto.getWtime().getMonth()] = dto.getReceivables()[billDto.getWtime().getMonth()]
					.add(billDto.getReceivables() == null ? BigDecimal.ZERO : billDto.getReceivables());
			if (dto.getReceiveds()[billDto.getWtime().getMonth()] == null) {
				dto.getReceiveds()[billDto.getWtime().getMonth()] = BigDecimal.ZERO;
			}
			dto.getReceiveds()[billDto.getWtime().getMonth()] = dto.getReceiveds()[billDto.getWtime().getMonth()]
					.add(billDto.getActualReceivables() == null ? BigDecimal.ZERO : billDto.getActualReceivables());
			if (dto.getInvoiceds()[billDto.getWtime().getMonth()] == null) {
				dto.getInvoiceds()[billDto.getWtime().getMonth()] = BigDecimal.ZERO;
			}
			dto.getInvoiceds()[billDto.getWtime().getMonth()] = dto.getInvoiceds()[billDto.getWtime().getMonth()]
					.add(billDto.getActualInvoiceAmount() == null ? BigDecimal.ZERO : billDto.getActualInvoiceAmount());
		});
		invoiceDtoList.forEach(invoiceDto -> {
			ReceivableAccountDto dto = null;
			if (!dtoMap.containsKey(invoiceDto.getEntityId())) {
				dto = new ReceivableAccountDto();
				dtoMap.put(invoiceDto.getEntityId(), dto);
				dto.setCustomerId(invoiceDto.getEntityId());
				dto.setDeptName(cacheDeptMap.get(invoiceDto.getDeptId()));
				dto.setSaleName(cacheUserMap.get(invoiceDto.getOssUserId()));
				dto.setCustomerName(cacheCustomerMap.get(invoiceDto.getEntityId()));
			} else {
				dto = dtoMap.get(invoiceDto.getEntityId());
			}
			if (dto.getInvoiceds()[invoiceDto.getWtime().getMonth()] == null) {
				dto.getInvoiceds()[invoiceDto.getWtime().getMonth()] = BigDecimal.ZERO;
			}
			dto.getInvoiceds()[invoiceDto.getWtime().getMonth()] = dto.getInvoiceds()[invoiceDto.getWtime().getMonth()]
					.add(invoiceDto.getReceivables() == null ? BigDecimal.ZERO : invoiceDto.getReceivables());
		});
	}

	@RequestMapping("/toReceivableAccount")
	public String toReceivableAccount() {
		List<String> deptIdList = departmentService.getDeptIdsByPermission(getOnlineUserAndOnther());
		boolean isManager = CollectionUtils.isEmpty(deptIdList) ? false : true;
		request.setAttribute("isManager", isManager);
		return "/views/manageConsole/receivableAccountSheet";
	}

}
