package com.dahantc.erp.controller.balanceinterest;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.balanceaccount.BalanceInterestDto;
import com.dahantc.erp.dto.balanceaccount.BalanceInterestParam;
import com.dahantc.erp.dto.balanceaccount.InterestDetailDto;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.balanceinterest.service.IBalanceInterestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/balanceInterest")
public class BalanceInterestAction extends BaseAction {

    public static final Logger logger = LoggerFactory.getLogger(BalanceInterestAction.class);

    @Autowired
    private IBalanceInterestService balanceInterService;

    @RequestMapping("/toBalanceInterestManager")
    public String toBalanceInterestManager() {
        return "/views/manageConsole/balanceInterest";
    }


    @ResponseBody
    @RequestMapping("/getBalanceInterest")
    public PageResult<BalanceInterestDto> getBalanceInterest(BalanceInterestParam param) {
        OnlineUser onlineUser = getOnlineUserAndOnther();
        if (null == onlineUser) {
            return PageResult.empty("请先登录");
        }
        if (param.getCurrentPage() == null) {
            return PageResult.empty("请求参数错误");
        }
        if (param.getPageSize() == null) {
            return PageResult.empty("请求参数错误");
        }
        return balanceInterService.queryBalanceInterestByPage(onlineUser, param);
    }


    @ResponseBody
    @PostMapping("/getDetail")
    public BaseResponse<List<InterestDetailDto>> getDetail(String month, String companyId) {
        OnlineUser onlineUser = getOnlineUserAndOnther();
        if (null == onlineUser) {
            return BaseResponse.error("请先登录");
        }
        if (StringUtil.isBlank(companyId)) {
            return BaseResponse.error("请求参数错误");
        }
        Date monthTime = DateUtil.convert(month, DateUtil.format4);
        if (monthTime == null) {
            return BaseResponse.error("请求参数错误");
        }
        List<InterestDetailDto> interestDetails = balanceInterService.queryInterestDetail(companyId, monthTime, onlineUser);
        if (interestDetails == null || interestDetails.isEmpty()) {
            return BaseResponse.success("暂无数据");
        }
        return BaseResponse.success(interestDetails);
    }
}
