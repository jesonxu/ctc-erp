package com.dahantc.erp.flowtask.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.SearchFilter;
import com.dahantc.erp.commom.dao.SearchRule;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.enums.AuditResult;
import com.dahantc.erp.enums.EntityStatus;
import com.dahantc.erp.enums.SpecialAttendanceType;
import com.dahantc.erp.flowtask.BaseFlowTask;
import com.dahantc.erp.util.DateUtil;
import com.dahantc.erp.util.StringUtil;
import com.dahantc.erp.vo.flow.entity.ErpFlow;
import com.dahantc.erp.vo.flowEnt.entity.FlowEnt;
import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import com.dahantc.erp.vo.specialAttendance.service.ISpecialAttendanceRecordService;
import com.dahantc.erp.vo.user.entity.User;
import com.dahantc.erp.vo.user.service.IUserService;
import com.dahantc.erp.vo.userLeave.service.IUserLeaveService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service("userTravelFlow")
public class UserTravelFlowService extends BaseFlowTask {
    private static Logger logger = LogManager.getLogger(UserTravelFlowService.class);

    private static final String FLOW_CLASS = Constants.USER_BUSINESS_TRAVEL_FLOW_CLASS;
    private static final String FLOW_NAME = Constants.USER_BUSINESS_TRAVEL_FLOW_NAME;

    @Autowired
    private ISpecialAttendanceRecordService specialAttendanceRecordService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IUserLeaveService userLeaveService;

    @Override
    public String getFlowClass() {
        return FLOW_CLASS;
    }

    @Override
    public String getFlowName() {
        return FLOW_NAME;
    }

    @Override
    public String verifyFlowMsg(ErpFlow erpFlow, String productId, String labelJsonVal) {
        return null;
    }

    @Override
    public String verifyFlowMsg(ErpFlow erpFlow, FlowEnt flowEnt, String labelJsonVal) {
        //校验
        String msg = "";
        User user = null;
        try {
            user = userService.read(flowEnt.getOssUserId());
        } catch (ServiceException e) {
            logger.error("查询流程发起人异常", e);
        }
        if (null == user) {
            msg = "流程发起人不能为空";
            logger.info(msg);
            return msg;
        }
        JSONObject flowMsgJson = JSONObject.parseObject(labelJsonVal);
        String leaveTime = flowMsgJson.getString(Constants.USER_TRAVEL_TIME_KEY);
        if (StringUtil.isBlank(leaveTime)) {
            msg = "出差时间不能为空";
            logger.info(msg);
        }
        return msg;
    }

    @Override
    public boolean flowArchive(ErpFlow erpFlow, FlowEnt flowEnt) {
        return true;
    }

    @Override
    public void flowMsgModify(int auditResult, FlowEnt flowEnt) throws ServiceException {
        flowMsgModify(auditResult,flowEnt,null);
    }

    @Override
    public void flowMsgModify(int auditResult, FlowEnt flowEnt, String changes) throws ServiceException {
        String flowMsg = flowEnt.getFlowMsg();
        SpecialAttendanceRecord leaveLog = null;
        SearchFilter filter = new SearchFilter();
        filter.getRules().add(new SearchRule("flowEntId", Constants.ROP_EQ, flowEnt.getId()));
        try {
            List<SpecialAttendanceRecord> leaveLogList = specialAttendanceRecordService.queryAllBySearchFilter(filter);
            // 流程关联的请假记录
            if (!CollectionUtils.isEmpty(leaveLogList)) {
                leaveLog = leaveLogList.get(0);
            }
        } catch (ServiceException e) {
            logger.error("", e);
        }
        if (AuditResult.CREATED.getCode() == auditResult) {
            // 创建
            if (null == leaveLog) {
                buildLogAndUpdateLeave(flowEnt);
            }
        } else if (AuditResult.CANCLE.getCode() == auditResult || AuditResult.REJECTED.getCode() == auditResult) {
            // 取消、驳回至发起人
            if (null != leaveLog) {
                updateLogAndRestoreLeave(leaveLog);
            }
        } else if (AuditResult.PASS.getCode() == auditResult) {
            // 通过、驳回至非发起人节点
            if (null != leaveLog) {
                updateLogAndUpdateLeave(leaveLog, flowMsg);
            }
        }
    }

    /**
     * 创建流程
     * @param flowEnt
     */
    private SpecialAttendanceRecord buildLogAndUpdateLeave(FlowEnt flowEnt) {
        SpecialAttendanceRecord leaveLog = null;
        boolean result = false;
        try {
            User user = userService.read(flowEnt.getOssUserId());
            JSONObject flowMsgJson = JSONObject.parseObject(flowEnt.getFlowMsg());
            //出差地点
            String leaveTypeStr = flowMsgJson.getString(Constants.USER_TRAVEL_PLACE_KEY);
            //出差时间
            String leaveTime = flowMsgJson.getString(Constants.USER_TRAVEL_TIME_KEY);
            String[] leaveTimes = null;
            if (leaveTime.contains("{")) {
                JSONObject leaveInfo = JSON.parseObject(leaveTime);
                leaveTimes = leaveInfo.getString("datetime").split(" - ");
            } else {
                leaveTimes = leaveTime.split(" - ");
            }
            Date leaveTimeStart = DateUtil.convert(leaveTimes[0], DateUtil.format2);
            Date leaveTimeEnd = DateUtil.convert(leaveTimes[1], DateUtil.format2);
            BigDecimal days = userLeaveService.getLeaveDays(leaveTimeStart, leaveTimeEnd);

            //创建流程
            leaveLog = new SpecialAttendanceRecord();
            leaveLog.setSpecialAttendanceType(SpecialAttendanceType.BusinessTravel.ordinal());
            leaveLog.setOssUserId(user.getOssUserId());
            leaveLog.setDeptId(user.getDeptId());
            leaveLog.setStartTime(leaveTimeStart);
            leaveLog.setEndTime(leaveTimeEnd);
            leaveLog.setDays(days);
            leaveLog.setFlowEntId(flowEnt.getId());
            leaveLog.setWtime(flowEnt.getWtime());

            result = specialAttendanceRecordService.save(leaveLog);
            logger.info("保存出差申请流程"+(result?"成功":"失败"));
        } catch (ServiceException e) {
            logger.error("",e);
        }
        return leaveLog;
    }

    /**
     * 取消、驳回至发起人
     * @param leaveLog
     */
    private void updateLogAndRestoreLeave(SpecialAttendanceRecord leaveLog){
        boolean result;
        try {
            leaveLog.setValid(EntityStatus.DELETED.ordinal());
            result = specialAttendanceRecordService.update(leaveLog);
            logger.info("更新出差记录" + (result ? "成功" : "失败"));
        } catch (ServiceException e) {
            logger.error("",e);
        }
    }

    /**
     * 通过、驳回至非发起人节点
     * @param leaveLog
     * @param flowMsg
     */
    private void updateLogAndUpdateLeave(SpecialAttendanceRecord leaveLog,String flowMsg){
        boolean result;
        try {
            //leaveLog.setValid(EntityStatus.NORMAL.ordinal());
            JSONObject flowMsgJson = JSONObject.parseObject(flowMsg);
            String leaveTypeStr = flowMsgJson.getString(Constants.USER_TRAVEL_PLACE_KEY);
            String leaveTime = flowMsgJson.getString(Constants.USER_TRAVEL_TIME_KEY);
            String[] leaveTimes = null;
            if (leaveTime.contains("{")) {
                JSONObject labelValue = JSON.parseObject(leaveTime);
                leaveTimes = labelValue.getString("datetime").split(" - ");
            } else {
                leaveTimes = leaveTime.split(" - ");
            }
            //格式化日期
            Date leaveTimeStart = DateUtil.convert(leaveTimes[0], DateUtil.format2);
            Date leaveTimeEnd = DateUtil.convert(leaveTimes[1], DateUtil.format2);
            // 出差天数
            BigDecimal days = userLeaveService.getLeaveDays(leaveTimeStart, leaveTimeEnd);

            // 流程信息与请假记录进行比较
            if (days.compareTo(leaveLog.getDays()) != 0) {
                leaveLog.setDays(days);
            }
            if (leaveTimeStart.getTime() != leaveLog.getStartTime().getTime() || leaveTimeEnd.getTime() != leaveLog.getEndTime().getTime()) {
                leaveLog.setStartTime(new Timestamp(leaveTimeStart.getTime()));
                leaveLog.setEndTime(new Timestamp(leaveTimeEnd.getTime()));
            }
            result = specialAttendanceRecordService.update(leaveLog);
            logger.info("更新出差记录" + (result ? "成功" : "失败"));
        } catch (ServiceException e) {
            logger.error("",e);
        }
    }

}
