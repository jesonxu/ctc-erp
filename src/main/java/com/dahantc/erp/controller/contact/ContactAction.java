package com.dahantc.erp.controller.contact;


import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.contact.AddContactDto;
import com.dahantc.erp.vo.customer.entity.Customer;
import com.dahantc.erp.vo.customer.service.ICustomerService;
import com.dahantc.erp.vo.supplier.entity.Supplier;
import com.dahantc.erp.vo.supplier.service.ISupplierService;
import com.dahantc.erp.vo.supplierContacts.service.ISupplierContactsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/contact")
public class ContactAction extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ContactAction.class);

    private ISupplierContactsService supplierContactsService;

    private ISupplierService supplierService;

    private ICustomerService customerService;

    /**
     * 添加客户联系人
     */
    @ResponseBody
    @RequestMapping(value = "/addCustomerContact")
    public BaseResponse<Boolean> addCustomerContact(AddContactDto contactDto) {
        try {
            Customer customer = customerService.read(contactDto.getEntityId());
            if (customer == null) {
                return BaseResponse.error("请求参数错误");
            }
            return supplierContactsService.addContact(contactDto);
        } catch (Exception e) {
            logger.error("添加客户的联系人时错误", e);
            logger.error("添加失败的数据" + contactDto.toString());
        }
        return BaseResponse.error("添加客户联系失败");
    }

    /**
     * 添加供应商联系人
     */
    @ResponseBody
    @RequestMapping(value = "/addSupplierContact")
    public BaseResponse<Boolean> addSupplierContact(AddContactDto contactDto) {
        try {
            Supplier supplier = supplierService.read(contactDto.getEntityId());
            if (supplier == null) {
                return BaseResponse.error("请求参数错误");
            }
            return supplierContactsService.addContact(contactDto);
        } catch (Exception e) {
            logger.error("添加客户的联系人时错误", e);
            logger.error("添加失败的数据" + contactDto.toString());
        }
        return BaseResponse.error("添加客户联系失败");
    }

    @Autowired
    public void setSupplierContactsService(ISupplierContactsService supplierContactsService) {
        this.supplierContactsService = supplierContactsService;
    }

    @Autowired
    public void setSupplierService(ISupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Autowired
    public void setCustomerService(ICustomerService customerService) {
        this.customerService = customerService;
    }
}
