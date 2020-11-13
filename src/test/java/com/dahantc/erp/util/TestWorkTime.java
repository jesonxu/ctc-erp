package com.dahantc.erp.util;

import com.dahantc.erp.commom.Constants;
import com.dahantc.erp.commom.dao.ServiceException;
import com.dahantc.erp.vo.parameter.entity.Parameter;
import com.dahantc.erp.vo.parameter.service.IParameterService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestWorkTime {


    @Test
    public void test(){
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.format2);
        try {
            Date from = sdf.parse("2020-11-12 8:30:00");
            Date fromM = sdf.parse("2020-11-12 13:00:00");

            Date to = sdf.parse("2020-11-17 11:30:00");
            Date toM = sdf.parse("2020-11-17 18:00:00");

            //上午 - 上午
            BigDecimal leaveDays = getLeaveDays(from, to);
            BigDecimal bigDecimal = leaveDays.setScale(0, BigDecimal.ROUND_DOWN);
            System.out.println("上午->上午："+leaveDays+":"+bigDecimal);

            //上午 - 下午
            leaveDays = getLeaveDays(from, toM);
            bigDecimal = leaveDays.setScale(0, BigDecimal.ROUND_DOWN);
            System.out.println("上午->下午："+leaveDays+":"+bigDecimal);

            //下午 - 上午
            leaveDays = getLeaveDays(fromM, to);
            bigDecimal = leaveDays.setScale(0, BigDecimal.ROUND_DOWN);
            System.out.println("下午->上午："+leaveDays+":"+bigDecimal);

            //下午 - 上午
            leaveDays = getLeaveDays(fromM, toM);
            bigDecimal = leaveDays.setScale(0, BigDecimal.ROUND_DOWN);
            System.out.println("下午->下午："+leaveDays+":"+bigDecimal);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public BigDecimal getLeaveDays(Date startTime, Date endTime) {
        String workTime = Constants.DEFAULT_WORK_TIME;
        // 工作时间字符串
        String[] workTimes = workTime.split(","); // [8:30-11:45, 13:15-18:00]
        String[] amWorkTime = workTimes[0].split("-"); // [8:30, 11:45]
        String[] pmWorkTime = workTimes[1].split("-"); // [13:15, 18:00]
        String[] amWorkStartTime = amWorkTime[0].split(":"); // [8, 30]
        String[] amWorkEndTime = amWorkTime[1].split(":"); // [11, 45]
        String[] pmWorkStartTime = pmWorkTime[0].split(":"); // [13, 15]
        String[] pmWorkEndTime = pmWorkTime[1].split(":"); // [18, 0]

        // 上下班的时分折算成一天的分钟
        int amWorkStart = Integer.parseInt(amWorkStartTime[0]) * 60 + Integer.parseInt(amWorkStartTime[1]);
        int amWorkEnd = Integer.parseInt(amWorkEndTime[0]) * 60 + Integer.parseInt(amWorkEndTime[1]);
        int pmWorkStart = Integer.parseInt(pmWorkStartTime[0]) * 60 + Integer.parseInt(pmWorkStartTime[1]);
        int pmWorkEnd = Integer.parseInt(pmWorkEndTime[0]) * 60 + Integer.parseInt(pmWorkEndTime[1]);
        // 一个工作日的工作分钟数
        int totalWorkTime = amWorkEnd - amWorkStart + pmWorkEnd - pmWorkStart;

        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        // 请假时间的时分折算成一天的分钟
        int leaveTimeStart = start.get(Calendar.HOUR_OF_DAY) * 60 + start.get(Calendar.MINUTE);
        int leaveTimeEnd = end.get(Calendar.HOUR_OF_DAY) * 60 + end.get(Calendar.MINUTE);
        // 请假时间随上下班时间调整
        if (leaveTimeStart <= amWorkStart) {
            leaveTimeStart = amWorkStart;
        }
        if (leaveTimeEnd <= amWorkStart) {
            leaveTimeEnd = amWorkStart;
        }
        if (leaveTimeStart >= pmWorkEnd) {
            leaveTimeStart = pmWorkEnd;
        }
        if (leaveTimeEnd >= pmWorkEnd) {
            leaveTimeEnd = pmWorkEnd;
        }

        BigDecimal workDays = null;
        int diffDays = DateUtil.getDiffDays(DateUtil.getDateStartDateTime(end.getTime()), DateUtil.getDateStartDateTime(start.getTime()));
        int diffMins = leaveTimeEnd - leaveTimeStart;
        if (diffDays == 0 || diffMins >= 0) {
            // 同一工作日，或 跨工作日且结束时间的时分在开始时间的时分之后
            // 如：2020-08-18 08:30:00 - 2020-08-18 18:00:00
            // 2020-08-18 08:30:00 - 2020-08-21 18:00:00
            if (leaveTimeStart <= amWorkEnd && pmWorkStart <= leaveTimeEnd) {
                // __8_|_11__13_|_18__
                diffMins -= pmWorkStart - amWorkEnd;
            } else if (leaveTimeStart <= amWorkEnd && amWorkEnd <= leaveTimeEnd && leaveTimeEnd <= pmWorkStart) {
                // __8_|_11_|_13__18__
                diffMins -= leaveTimeEnd - amWorkEnd;
            } else if (amWorkEnd <= leaveTimeStart && leaveTimeStart <= pmWorkStart && leaveTimeEnd <= pmWorkStart) {
                // __8__11_|_|_13__18__
                diffMins -= leaveTimeEnd - leaveTimeStart;
            } else if (amWorkEnd <= leaveTimeStart && leaveTimeStart <= pmWorkStart && pmWorkStart <= leaveTimeEnd) {
                // __8__11_|_13_|_18__
                diffMins -= pmWorkStart - leaveTimeStart;
            }
            workDays = new BigDecimal(diffDays).add(new BigDecimal(diffMins).divide(new BigDecimal(totalWorkTime), 2, BigDecimal.ROUND_HALF_UP));
        } else if (diffDays > 0 && diffMins < 0) {
            // 跨工作日，且结束时间的时分在开始时间的时分之前
            // 如：2020-08-18 13:15:00 - 2020-08-21 11:45:00
            diffDays--;
            diffMins = 0;
            // 结束时间
            if (amWorkStart <= leaveTimeEnd && leaveTimeEnd <= amWorkEnd) {
                // __8_|_11__13__18__
                diffMins += leaveTimeEnd - amWorkStart;
            } else if (amWorkEnd <= leaveTimeEnd && leaveTimeEnd <= pmWorkStart) {
                // __8__11_|_13__18__
                diffMins += amWorkEnd - amWorkStart;
            } else if (pmWorkStart <= leaveTimeEnd && leaveTimeEnd <= pmWorkEnd) {
                // __8__11__13_|_18__
                diffMins += (amWorkEnd - amWorkStart) + (leaveTimeEnd - pmWorkStart);
            }
            workDays = new BigDecimal(diffDays).add(new BigDecimal(diffMins).divide(new BigDecimal(totalWorkTime), 2, BigDecimal.ROUND_HALF_UP));

            diffMins = 0;
            // 开始时间
            if (amWorkStart <= leaveTimeStart && leaveTimeStart <= amWorkEnd) {
                // __8_|_11__13__18__
                diffMins += (amWorkEnd - leaveTimeStart) + (pmWorkEnd - pmWorkStart);
            } else if (amWorkEnd <= leaveTimeStart && leaveTimeStart <= pmWorkStart) {
                // __8__11_|_13__18__
                diffMins += pmWorkEnd - pmWorkStart;
            } else if (pmWorkStart <= leaveTimeStart && leaveTimeStart <= pmWorkEnd) {
                // __8__11__13_|_18__
                diffMins += pmWorkEnd - leaveTimeStart;
            }
            workDays = workDays.add(new BigDecimal(diffMins).divide(new BigDecimal(totalWorkTime), 2, BigDecimal.ROUND_HALF_UP));
        }
        return workDays;
    }

    public BigDecimal getLeaveDays1(Date startTime, Date endTime) {
        long time = endTime.getTime() - startTime.getTime();

        long days=time/(1000*60*60*24);//天数
        long hours=(time%(1000*60*60*24))/(1000*60*60);//小时数
        return BigDecimal.ZERO;
    }
}