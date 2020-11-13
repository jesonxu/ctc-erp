package com.dahantc.erp.commom.listener;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.FlowTaskManager;
import com.dahantc.erp.flowtask.service.*;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * 后台任务监听器
 *
 * @author 8515
 */
@WebListener
public class TaskListener implements ServletContextListener {

	private static final Logger logger = LogManager.getLogger(TaskListener.class);

	@Autowired
	private IParameterService parameterService;

	@Autowired
	private PaymentFlowService paymentFlowService;

	@Autowired
	private BillPaymentFlowService billPaymentFlowService;

	@Autowired
	private AdjustPriceService adjustPriceService;

	@Autowired
	private FlowTaskManager flowTaskManager;

	@Autowired
	private CommonFlowTask commonFlowTask;

	@Autowired
	private ContractFlowService contractFlowService;

	@Autowired
	private BillFlowService billFlowService;

	@Autowired
	private RemunerationFlowService remunerationFlowService;

	@Autowired
	private InterAdjustPriceService interAdjustPriceService;

	@Autowired
	private CustomerBillFlowService customerBillFlowService;

	@Autowired
	private BillReceivablesFlowService billReceivablesFlowService;

	@Autowired
	private InvoiceFlowService invoiceFlowService;

	@Autowired
	private BillWriteOffFlowService billWriteOffFlowService;

	@Autowired
	private DsOrderFlowService dsOrderFlowService;

	@Autowired
	private DsPurchaseFlowService dsPurchaseFlowService;

	@Autowired
	private DsSupplierFlowService dsSupplierFlowService;

	@Autowired
	private PaymentPeriodService paymentPeriodService;

	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private ApplyCustomerFlowService applyCustomerFlowService;

	@Autowired
	private CheckBillFlowService checkBillFlowService;

	@Autowired
	private UserLeaveFlowService userLeaveFlowService;

	@Autowired
	private UserOvertimeFlowService userOvertimeFlowService;

	@Autowired
	private UserOutsideFlowService userOutsideFlowService;

	@Autowired
	private UserTravelFlowService userTravelFlowService;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {

			try {
				// 缓存资源路径信息
				Parameter resource = parameterService.readOneByProperty("paramkey", "resource");
				if (resource != null) {
					/** 缓存资源路径信息 */
					Constants.RESOURCE = resource.getParamvalue();
				}
				Constants.DATA_ANALYSIS_CALL_BACK_IP = parameterService.getSysParam("data_analysis_call_back_ip");
			} catch (Exception e) {
				logger.error("", e);
			}

			flowTaskManager.getFlowTasks().put(paymentFlowService.getFlowClass(), paymentFlowService);
			flowTaskManager.getFlowTasks().put(adjustPriceService.getFlowClass(), adjustPriceService);
			flowTaskManager.getFlowTasks().put(interAdjustPriceService.getFlowClass(), interAdjustPriceService);
			flowTaskManager.getFlowTasks().put(commonFlowTask.getFlowClass(), commonFlowTask);
			flowTaskManager.getFlowTasks().put(billFlowService.getFlowClass(), billFlowService);
			flowTaskManager.getFlowTasks().put(billPaymentFlowService.getFlowClass(), billPaymentFlowService);
			flowTaskManager.getFlowTasks().put(remunerationFlowService.getFlowClass(), remunerationFlowService);
			flowTaskManager.getFlowTasks().put(customerBillFlowService.getFlowClass(), customerBillFlowService);
			flowTaskManager.getFlowTasks().put(billReceivablesFlowService.getFlowClass(), billReceivablesFlowService);
			flowTaskManager.getFlowTasks().put(invoiceFlowService.getFlowClass(), invoiceFlowService);
			flowTaskManager.getFlowTasks().put(billWriteOffFlowService.getFlowClass(), billWriteOffFlowService);
			flowTaskManager.getFlowTasks().put(dsOrderFlowService.getFlowClass(), dsOrderFlowService);
			flowTaskManager.getFlowTasks().put(dsPurchaseFlowService.getFlowClass(), dsPurchaseFlowService);
			flowTaskManager.getFlowTasks().put(contractFlowService.getFlowClass(), contractFlowService);
			flowTaskManager.getFlowTasks().put(dsSupplierFlowService.getFlowClass(), dsSupplierFlowService);
			flowTaskManager.getFlowTasks().put(paymentPeriodService.getFlowClass(), paymentPeriodService);
			flowTaskManager.getFlowTasks().put(accountFlowService.getFlowClass(), accountFlowService);
			flowTaskManager.getFlowTasks().put(applyCustomerFlowService.getFlowClass(), applyCustomerFlowService);
			flowTaskManager.getFlowTasks().put(checkBillFlowService.getFlowClass(), checkBillFlowService);
			flowTaskManager.getFlowTasks().put(userLeaveFlowService.getFlowClass(), userLeaveFlowService);
			flowTaskManager.getFlowTasks().put(userOvertimeFlowService.getFlowClass(), userOvertimeFlowService);
			flowTaskManager.getFlowTasks().put(userOutsideFlowService.getFlowClass(), userOutsideFlowService);
			flowTaskManager.getFlowTasks().put(userTravelFlowService.getFlowClass(),userTravelFlowService);
		} catch (Exception e) {
			logger.error("后台任务监听器启动异常：", e);
		}
	}

}
