package com.dahantc.erp.dto.customer;

import com.dahantc.erp.vo.bankAccount.entity.BankAccount;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.invoice.entity.InvoiceInformation;
import com.dahantc.erp.vo.supplierContactLog.entity.SupplierContactLog;
import com.dahantc.erp.vo.supplierContacts.entity.SupplierContacts;

import java.io.Serializable;
import java.util.List;

/**
 * 客户的所有信息
 *
 * @author 8520
 */
public class CustomerAllDto extends CustomerDetailRspDto implements Serializable {

    private static final long serialVersionUID = 4338919012079130262L;

    private CustomerDetailRspDto customerDetail;

    /**
     * 客户的发票信息
     */
    private List<InvoiceInformation> invoices;

    /**
     * 银行信息
     */
    private List<BankAccount> bankAccounts;
    /**
     * 联系人
     */
    private List<SupplierContacts> contacts;
    /**
     * 联系日志
     */
    private List<SupplierContactLog> contactLogs;

    public CustomerAllDto(Customer cus) {
       this.customerDetail = new CustomerDetailRspDto(cus);
    }

    public List<InvoiceInformation> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceInformation> invoices) {
        this.invoices = invoices;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public List<SupplierContacts> getContacts() {
        return contacts;
    }

    public void setContacts(List<SupplierContacts> contacts) {
        this.contacts = contacts;
    }

    public List<SupplierContactLog> getContactLogs() {
        return contactLogs;
    }

    public void setContactLogs(List<SupplierContactLog> contactLogs) {
        this.contactLogs = contactLogs;
    }

    public CustomerDetailRspDto getCustomerDetail() {
        return customerDetail;
    }

    public void setCustomerDetail(CustomerDetailRspDto customerDetail) {
        this.customerDetail = customerDetail;
    }
}
