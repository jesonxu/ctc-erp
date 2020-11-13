package com.dahantc.erp.commom;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.dahantc.erp.flowtask.BaseFlowTask;

@Component("flowTaskManager")
public class FlowTaskManager implements Serializable {

	private static final long serialVersionUID = -1264387784436511555L;

	/**
	 * 流程任务
	 */
	private Map<String, BaseFlowTask> flowTasks = new ConcurrentHashMap<String, BaseFlowTask>();

	public BaseFlowTask getFlowTasks(String taskId) {
		return flowTasks.get(taskId);
	}

	public Map<String, BaseFlowTask> getFlowTasks() {
		return flowTasks;
	}

	public void setFlowTasks(Map<String, BaseFlowTask> flowTasks) {
		this.flowTasks = flowTasks;
	}
}
