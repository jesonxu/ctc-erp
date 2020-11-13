package com.dahantc.erp.commom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.dahantc.erp.commom.warn.IWarnBroadCaster;
import com.dahantc.erp.commom.warn.IWarnListener;
import com.dahantc.erp.commom.warn.WarnMessage;

public class CTCThread extends Thread implements IWarnBroadCaster {

	private boolean isRun = false;
	private List<IWarnListener> listenerList = new LinkedList<IWarnListener>();
	private static CTCThread instance = null;

	/**
	 * 得到一个CTCThread实例
	 * 
	 * @return CTCThread
	 */
	public static CTCThread getInstance() {
		if (instance == null) {
			instance = new CTCThread();
		}
		return instance;
	}

	/**
	 * 让线程开始运行
	 */
	public void startRun() {
		startWork();
		this.start();
	}

	/**
	 * 让线程停止运行
	 */
	public void stopRun() {
		try {
			this.stopService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopService() {
		int i = 0;
		try {
			do {
				if (i >= 1) {
					System.out.println("线程：" + this.getName() + "哎呀呀!!打不死的小强,我再杀...");
				} else {
					System.out.println("线程：" + this.getName() + "正在停止...");
				}
				this.setRun(false);
				this.interrupt();
				this.join();
				i++;
			} while (this.isAlive());
			if (i >= 2) {
				System.out.println("线程：" + this.getName() + "终于使出了六脉神剑,灭了这该死的小强...");
			} else {
				System.out.println("线程：" + this.getName() + "已经停止...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	/**
	 * 让线程开始工作
	 */
	public void startWork() {
		setRun(true);
	}

	/**
	 * 让线程停止工作
	 */
	public void stopWork() {
		setRun(false);
	}

	public boolean isRun() {
		return isRun;
	}

	public void setRun(boolean isRun) {
		this.isRun = isRun;
	}

	public String getTaskName() {
		return "CTCThread";
	}

	public boolean addListener(IWarnListener inListener) {
		boolean _bRet = false;
		if (inListener != null) {
			if (!listenerList.contains(inListener)) {
				listenerList.add(inListener);
				_bRet = true;
			}

		}
		return _bRet;
	}

	public boolean removeListener(IWarnListener inListener) {
		boolean _bRet = false;
		if (inListener != null) {
			_bRet = listenerList.remove(inListener);
		}
		return _bRet;
	}

	public boolean warn(WarnMessage inWarnMessage) {
		boolean _bRet = true;
		Iterator<IWarnListener> _it = listenerList.iterator();
		while (_it.hasNext()) {
			IWarnListener _listener = (IWarnListener) _it.next();
			_listener.listenTo(inWarnMessage);
		}

		return _bRet;
	}

}
