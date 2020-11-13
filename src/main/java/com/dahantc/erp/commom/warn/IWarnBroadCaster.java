package com.dahantc.erp.commom.warn;

public interface IWarnBroadCaster {

	public boolean addListener(IWarnListener inListener);

	public boolean removeListener(IWarnListener inListener);

	public boolean warn(WarnMessage inWarnMessage);
}
