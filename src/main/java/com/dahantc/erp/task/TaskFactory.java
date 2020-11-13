package com.dahantc.erp.task;

import java.io.Serializable;
import java.lang.Thread.State;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dahantc.erp.commom.CTCThread;
import com.dahantc.erp.task.base.BaseTask;

/**
 * 任务工厂类
 * 
 */
@Component("taskFactory")
public class TaskFactory implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 59259259082020598L;

	private static final Logger logger = LogManager.getLogger(TaskFactory.class);

	/** 互斥锁 */
	private final ReentrantLock lock = new ReentrantLock();

	/** 运营端后台任务 */
	private Map<String, BaseTask> realTasks = new ConcurrentHashMap<String, BaseTask>();

	/** 任务状态 */
	private Map<String, Boolean> timerTasks = new ConcurrentHashMap<String, Boolean>();

	public void init() {
		
	}

	/**
	 * 根据指定的任务名称创建任务实例
	 * 
	 * @param taskName
	 * @return
	 */
	private BaseTask createTask(String taskName) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@PreDestroy
	protected void release() {
		Set<String> keys = realTasks.keySet();
		for (String key : keys) {
			BaseTask task = realTasks.remove(key);
			try {
				task.setRun(false);
				task.stopRun();
				task.join(3000);
				if (task.getState() != State.TERMINATED) {
					task.stop();
				}
				task = null;
			} catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * 获取指定名称的任务
	 * 
	 * @param taskName
	 * @return
	 */
	public CTCThread getTask(String taskName) {
		BaseTask task = null;
		if (realTasks.containsKey(taskName)) {
			task = realTasks.get(taskName);
		}
		return task;
	}

	public void startTask(String taskName) {
		lock.lock();
		try {
			BaseTask task = null;
			if (StringUtils.isNotBlank(taskName)) {
				if (realTasks.containsKey(taskName)) {
					task = realTasks.get(taskName);
				}
				if (task != null && !task.isRun()) {
					task.startRun();
					task.startWork();
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		} finally {
			lock.unlock();
		}
	}

	public void stopTask(String taskName) {
		lock.lock();
		try {
			BaseTask task = null;
			if (StringUtils.isNotBlank(taskName)) {
				if (realTasks.containsKey(taskName)) {
					task = realTasks.remove(taskName);
					realTasks.put(taskName, createTask(taskName));
				}
				if (task != null) {
					task.setRun(false);
					task.stopRun();
					task = null;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		} finally {
			lock.unlock();
		}
	}

	@SuppressWarnings("deprecation")
	public void stopAll() {
		Set<String> keys = realTasks.keySet();
		for (String key : keys) {
			BaseTask task = realTasks.remove(key);
			try {
				task.setRun(false);
				task.stopRun();
				if (task.getState() != State.TERMINATED) {
					task.stop();
				}
				task = null;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		init();
	}

	/**
	 * 获取任务状态
	 * 
	 * @param timerName
	 * @return
	 */
	public boolean getTimerTaskStatus(String timerName) {
		if (!timerTasks.containsKey(timerName)) {
			timerTasks.put(timerName, false);
		}
		return timerTasks.get(timerName);
	}

	/**
	 * 更新任务状态
	 * 
	 * @param timerName
	 */
	public void updateTimerStatus(String timerName, boolean status) {
		if (timerTasks.containsKey(timerName)) {
			timerTasks.put(timerName, status);
		}
	}

	/**
	 * 启动所有线程
	 */
	public void startAll() {
		Set<String> keys = realTasks.keySet();
		for (String key : keys) {
			BaseTask task = realTasks.get(key);
			if (!task.isRun()) {
				task.startRun();
			}
		}
	}

	/**
	 * @return the realTasks
	 */
	public Map<String, BaseTask> getRealTasks() {
		return realTasks;
	}

}
