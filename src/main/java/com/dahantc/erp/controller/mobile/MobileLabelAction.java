package com.dahantc.erp.controller.mobile;

import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用于移动端的特殊标签 跳转、特有数据加载入口
 *
 * @author 8520
 */
@Controller
@RequestMapping("/mobileLabel")
public class MobileLabelAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(MobileLabelAction.class);

    @Autowired
    private ICustomerService customerService;

    /**
     * 客户账号详情页面
     */
    @RequestMapping(path = "/customerAccountDetail")
    public String customerAccountDetail() {
        return "/mobile/common/details/customerAccountDetail";
    }

    /**
     * 供应商账号详情页面
     */
    @RequestMapping(path = "/supplierAccountDetail")
    public String supplierAccountDetail() {
        return "/mobile/common/details/supplierAccountDetail";
    }

    /**
     * 供应商账单详情页面
     */
    @RequestMapping(path = "/invoiceDetail")
    public String invoiceDetail() {
        return "/mobile/common/details/invoiceDetail";
    }

    /**
     * 供应商账单详情页面(多选)
     */
    @RequestMapping(path = "/invoiceDetailMulti")
    public String invoiceDetailMulti() {
        return "/mobile/common/details/invoiceDetailMulti";
    }

    /**
     * 发票按钮跳转到选择页面
     */
    @RequestMapping(path = "/invoiceItemDetail")
    public String invoiceItemDetail() {
        return "/mobile/common/details/invoiceItemDetail";
    }

    /**
     * 账单发票详情
     */
    @RequestMapping(path = "/billInvoiceDetail")
    public String billInvoiceDetail() {
        return "/mobile/common/details/billInvoiceDetail";
    }

    /**
     * 未对账账单详情页面
     */
    @RequestMapping(path = "/uncheckedBillDetail")
    public String uncheckedBillDetail() {
        return "/mobile/common/details/uncheckedBillDetail";
    }

    /**
     * 未对账账单详情
     */
    @RequestMapping(path = "/unWriteOffBillDetail")
    public String unWriteOffBillDetail() {
        return "/mobile/common/details/unWriteOffBillDetail";
    }

    /**
     * 未销账收款详情
     */
    @RequestMapping(path = "/unWriteOffReceiptDetail")
    public String unWriteOffReceiptDetail() {
        return "/mobile/common/details/unWriteOffReceiptDetail";
    }

    /**
     * 跳转生成账单页面
     */
    @RequestMapping("/toBuildBillDetail")
    public String toBuildBillDetail() {
        return "/mobile/common/details/buildBillPage";
    }

    /**
     * 跳转编辑账单页面
     */
    @RequestMapping("/toEditBillPage")
    public String toEditBillPage() {
        return "/mobile/common/details/editBillPage";
    }
}
