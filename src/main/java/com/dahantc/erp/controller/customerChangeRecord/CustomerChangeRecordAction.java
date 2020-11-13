package com.dahantc.erp.controller.customerChangeRecord;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.customerChangeRecord.service.ICustomerChangeRecordService;
import com.dahantc.erp.vo.customerChangeRecord.vo.CustomerChangeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 客户变更（统计）
 *
 * @author 8520
 */
@Controller
@RequestMapping(path = "/customerChangeRecord")
public class CustomerChangeRecordAction extends BaseAction {

    private ICustomerChangeRecordService customerChangeRecordService;

    @RequestMapping(path = "/toCustomerChangeTable")
    public String toCustomerChangeTable() {
        return "/views/manageConsole/customerChange";
    }

    /**
     * 客户变更统计表
     *
     * @return 客户变更统计信息
     */
    @ResponseBody
    @RequestMapping(path = "/getCustomerChangeTable")
    public PageResult<CustomerChangeVo> getCustomerChangeTable(String deptId, String userId, String yearMonth) {
        OnlineUser user = getOnlineUserAndOnther();
        if (user == null || StringUtil.isBlank(user.getRoleId())) {
            return PageResult.empty("请先登录");
        }
        if (StringUtil.isBlank(yearMonth)) {
            return PageResult.empty("请求参数错误");
        }
        List<String> deptIdList = null;
        if (StringUtil.isNotBlank(deptId)) {
            deptIdList = new ArrayList<>(Arrays.asList(deptId.split(",")));
        }
        List<String> userIds = null;
        if (StringUtil.isNotBlank(userId)) {
            userIds = new ArrayList<>(Arrays.asList(userId.split(",")));
        }
        // 查询的月份
        Date month = DateUtil.convert(yearMonth, DateUtil.format4);
        List<CustomerChangeVo> changeList = customerChangeRecordService.queryCustomerChangeInfo(user,deptIdList, userIds, month);
        if (changeList == null || changeList.isEmpty()) {
            return PageResult.empty("暂无数据");
        }
        return new PageResult<>(changeList, 1, 1, changeList.size());
    }

    @Autowired
    public void setCustomerChangeRecordService(ICustomerChangeRecordService customerChangeRecordService) {
        this.customerChangeRecordService = customerChangeRecordService;
    }
}
