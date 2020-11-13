package com.dahantc.erp.util;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahantc.erp.vo.specialAttendance.entity.SpecialAttendanceRecord;
import org.springframework.beans.BeanUtils;

/**
 * @author : 8523
 * @date : 2020/10/23 16:11
 */
public class TimeListUtil {

	/**
	 * 一次合并2个有交集的时间段为一个范围更大的时间段
	 * 
	 * @param timeList
	 *            可能重叠的时间段列表
	 * @return 不重叠的时间段列表
	 */
	public static List<SpecialAttendanceRecord> combineTimeList(List<SpecialAttendanceRecord> timeList) {
		// 检查存在交集
		Map<String, Integer> union = checkUnion(timeList);

		if (union != null) {
			Integer i = union.get("i");
			Integer j = union.get("j");

			if (i.intValue() == j.intValue()) {// 这条记录是多余的
				timeList.remove(i.intValue());
			} else {
				SpecialAttendanceRecord time1 = timeList.get(i);
				SpecialAttendanceRecord time2 = timeList.get(j);
				// 时间段合并成并集
				combineTime(time1, time2, timeList);
			}

			// 递归调用
			combineTimeList(timeList);
		}
		return timeList;
	}

	/**
	 * 合并2个有重叠的时间段为一个大的时间段
	 * 
	 * @param time1
	 *            时间段1
	 * @param time2
	 *            时间段2
	 * @param timeList
	 *            时间段列表
	 */
	private static void combineTime(SpecialAttendanceRecord time1, SpecialAttendanceRecord time2, List<SpecialAttendanceRecord> timeList) {
		Date startTime1 = time1.getStartTime();
		Date endTime1 = time1.getEndTime();

		Date startTime2 = time2.getStartTime();
		Date endTime2 = time2.getStartTime();

		Date start;
		Date end;
		if (startTime1.before(startTime2)) {
			start = startTime1;
		} else {
			start = startTime2;
		}

		if (endTime1.after(endTime2)) {
			end = endTime1;
		} else {
			end = endTime2;
		}
		// 有交集的2个时间段合并为一个大的时间段，加入列表中，把原来的2个移出列表
		SpecialAttendanceRecord combinedTime = new SpecialAttendanceRecord();
		BeanUtils.copyProperties(time1, combinedTime);
		combinedTime.setStartTime(start);
		combinedTime.setEndTime(end);
		timeList.remove(time1);
		timeList.remove(time2);
		timeList.add(combinedTime);
	}

	/**
	 * 获取一组不重叠的时间段，可以区分不同类型
	 * 
	 * @param timeList
	 *            可能重叠的时间段列表
	 * @return 不重叠的时间段列表
	 */
	public static List<SpecialAttendanceRecord> splitTimeList(List<SpecialAttendanceRecord> timeList) {
		// 检查存在交集
		Map<String, Integer> union = checkUnion(timeList);

		if (union != null) {
			Integer i = union.get("i");
			Integer j = union.get("j");
			if (i.intValue() == j.intValue()) {// 这条记录是多余的
				timeList.remove(i.intValue());
			} else {
				SpecialAttendanceRecord time1 = timeList.get(i);
				SpecialAttendanceRecord time2 = timeList.get(j);

				boolean new1 = true;
				if (time1.getWtime() != null && time2.getWtime() != null) {
					new1 = time1.getWtime().after(time2.getWtime());
				}

				if (new1) {
					splitTime(time1, time2, timeList);
				} else {
					splitTime(time2, time1, timeList);
				}

			}
			// 递归调用
			splitTimeList(timeList);
		}
		return timeList;
	}

	/**
	 * 合并2个不同类型有重叠的时间段，按类型划分成不重叠的时间段
	 * 
	 * @param time1
	 *            较新的记录
	 * @param time2
	 *            旧的记录
	 * @param timeList
	 *            存放时间段的列表
	 */
	private static void splitTime(SpecialAttendanceRecord time1, SpecialAttendanceRecord time2, List<SpecialAttendanceRecord> timeList) {
		Date startTime1 = time1.getStartTime();
		Date endTime1 = time1.getEndTime();

		Date startTime2 = time2.getStartTime();
		Date endTime2 = time2.getEndTime();
		if (!startTime1.after(startTime2) && !endTime1.before(endTime2)) {
			// 1. ---------------
			// 2. ----------
			// 取1的全部，去掉2
			timeList.remove(time2);
		} else if (!startTime1.before(startTime2) && !endTime1.after(endTime2)) {
			// 1. ----------
			// 2. ---------------
			// 交集部分用1，其他部分用2
			SpecialAttendanceRecord splitTime = new SpecialAttendanceRecord();
			BeanUtils.copyProperties(time2, splitTime);
			splitTime.setStartTime(startTime2);
			splitTime.setEndTime(startTime1);
			timeList.add(splitTime);

			SpecialAttendanceRecord splitTime2 = new SpecialAttendanceRecord();
			BeanUtils.copyProperties(time2, splitTime2);
			splitTime2.setStartTime(endTime1);
			splitTime2.setEndTime(endTime2);
			timeList.add(splitTime2);

			timeList.remove(time2);
		} else if (!startTime1.before(startTime2) && !endTime1.before(endTime2)) {
			// 1. ----------
			// 2. --------
			SpecialAttendanceRecord splitTime = new SpecialAttendanceRecord();
			BeanUtils.copyProperties(time2, splitTime);
			splitTime.setStartTime(startTime2);
			splitTime.setEndTime(startTime1);
			timeList.add(splitTime);

			timeList.remove(time2);
		} else if (!startTime1.after(startTime2) && !endTime1.after(endTime2)) {
			// 1. --------
			// 2. ----------
			SpecialAttendanceRecord splitTime = new SpecialAttendanceRecord();
			BeanUtils.copyProperties(time2, splitTime);
			splitTime.setStartTime(endTime1);
			splitTime.setEndTime(endTime2);
			timeList.add(splitTime);

			timeList.remove(time2);
		}
	}

	/**
	 * 求有交集的2个时间段在列表的序号，若返回null说明没有交集
	 *
	 * @param timeList
	 *            时间段列表
	 * @return 序号
	 */
	private static Map<String, Integer> checkUnion(List<SpecialAttendanceRecord> timeList) {
		// 长度为1无需判断
		if (timeList == null || timeList.size() <= 1) {
			return null;
		}
		for (int i = 0; i < timeList.size() - 1; i++) {
			Date start = timeList.get(i).getStartTime();
			Date tempStart = start;
			Date end = timeList.get(i).getEndTime();
			Date tempEnd = end;
			for (int j = i + 1; j < timeList.size(); j++) {

				Date start2 = timeList.get(j).getStartTime();
				Date end2 = timeList.get(j).getEndTime();

				if (start.before(start2)) {
					start = start2;
				}
				if (end.after(end2)) {
					end = end2;
				}
				if (start.before(end)) {
					Map<String, Integer> map = new HashMap<String, Integer>();
					map.put("i", i);
					map.put("j", j);
					return map;
				} else {
					start = tempStart;
					end = tempEnd;
				}
			}
		}
		return null;
	}
}
