package com.dahantc.erp.controller.bankAccount;

import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.enums.InvoiceType;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.bankAccount.service.IBankAccountService;
import com.dahantc.erp.vo.bankAccount.vo.BankInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 平台的银行信息
 *
 * @author 8520
 */
@RestController
@RequestMapping("/bankInfo")
public class BankAccountAction extends BaseAction {

    private IBankAccountService bankAccountService;

    /**
     * 根据类型查询 银行信息
     *
     * @param basicId 客户/供应商id（可为空 只允许在查自己银行信息的时候为空）
     * @param type    类型
     * @return 银行信息
     * @see com.dahantc.erp.enums.InvoiceType
     */
    @PostMapping("/queryBankInfoByType")
    public BaseResponse<List<BankInfoVo>> queryBankInfoByType(String basicId, Integer type) {
        if (type == null) {
            return BaseResponse.error("请求错误");
        }
        if (type != InvoiceType.SelfBank.ordinal()) {
            if (StringUtil.isBlank(basicId)) {
                return BaseResponse.error("请求错误");
            }
        }
        List<BankInfoVo> bankInfoVoList = bankAccountService.queryBankAccountByType(basicId, type);
        return BaseResponse.success(bankInfoVoList);
    }

    @Autowired
    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }
}
