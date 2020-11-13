package com.dahantc.erp.controller.flow;

import com.dahantc.erp.commom.OnlineUser;
import com.dahantc.erp.commom.PageResult;
import com.dahantc.erp.controller.BaseAction;
import com.dahantc.erp.controller.BaseResponse;
import com.dahantc.erp.dto.flow.ErpFlowUI;
import com.dahantc.erp.dto.flow.FlowDetail;
import com.dahantc.erp.dto.flow.FlowEntForMobileDto;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flow.service.IErpFlowService;
import com.dahantc.erp.vo.flowEnt.service.IFlowEntService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程查询（移动端）
 *
 * @author 8520
 */
@Controller
@RequestMapping(value = "/flowForMobile")
public class FlowForMobileAction extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(FlowForMobileAction.class);

    private IFlowEntService flowEntService;


    private IErpFlowService flowService;

    /**
     * 移动端 分页查询流程信息
     *
     * @param page          当前页
     * @param pageSize      页大小
     * @param searchMonth   查询月份
     * @param searchContent 搜索内容
     * @return 流程信息
     */
    @ResponseBody
    @RequestMapping(value = "/readFlowEnt")
    public PageResult<FlowEntForMobileDto> readFlowEnt(Integer page, Integer pageSize, @RequestParam(value = "month", required = false) String searchMonth,
                                                       @RequestParam(value = "content", required = false) String searchContent) {
        OnlineUser user = getOnlineUserAndOnther();
        if (user == null) {
            return PageResult.empty("请登录后操作");
        }
        return flowEntService.queryPageForMobile(user, page, pageSize, searchMonth, searchContent);
    }

    /**
     * 按条件获取能发起的流程
     */
    @RequestMapping("/getApplyFlow")
    @ResponseBody
    public BaseResponse<Map<Integer, List<ErpFlowUI>>> getApplyFlow() {
        OnlineUser onlineUser = getOnlineUserAndOnther();
        if (onlineUser == null) {
            return BaseResponse.error("请先登录");
        }
        List<ErpFlow> userFlowList = flowService.queryUserAllFlow(onlineUser);
        if (userFlowList == null || userFlowList.isEmpty()) {
            return BaseResponse.success("暂无数据");
        }
        Map<Integer, List<ErpFlowUI>> userFlowInfos = userFlowList.stream().map(flow -> {
            ErpFlowUI flowInfo = new ErpFlowUI();
            flowInfo.setFlowId(flow.getFlowId());
            flowInfo.setFlowName(flow.getFlowName());
            flowInfo.setBindType(flow.getBindType());
            flowInfo.setFlowClass(flow.getFlowClass());
            flowInfo.setFlowType(flow.getFlowType());
            return flowInfo;
        }).collect(Collectors.groupingBy(ErpFlowUI::getFlowType));
        return BaseResponse.success(userFlowInfos);
    }

    /**
     * 根据流程id 查询流程信息
     **/
    @ResponseBody
    @PostMapping("/getFlowDetail/{flowId}")
    public BaseResponse<FlowDetail> getFlowDetail(@PathVariable String flowId) {
        if (StringUtil.isBlank(flowId)) {
            return BaseResponse.error("请求参数错误");
        }
        FlowDetail flowDetail = flowService.queryFlowDetailById(flowId);
        if (flowDetail == null) {
            return BaseResponse.error("未能查询到流程信息");
        }
        return BaseResponse.success(flowDetail);
    }

    @Autowired
    public void setFlowEntService(IFlowEntService flowEntService) {
        this.flowEntService = flowEntService;
    }

    @Autowired
    public void setFlowService(IErpFlowService erpFlowService) {
        this.flowService = erpFlowService;
    }
}
