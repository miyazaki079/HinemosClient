/*

Copyright (C) 2006 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.systemlog.action;

import java.util.List;

import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.monitor.action.DeleteInterface;
import com.clustercontrol.monitor.util.MonitorSettingEndpointWrapper;

/**
 * システムログ監視情報を削除するクラスです。
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class DeleteSystemlog implements DeleteInterface {

	/**
	 * システムログ監視情報を削除します。
	 *
	 * @param managerName マネージャ名
	 * @param monitorIdList 監視項目IDリスト
	 * @return 削除に成功した場合、true
	 */
	@Override
	public boolean delete(String managerName, List<String> monitorIdList) throws Exception{
		MonitorSettingEndpointWrapper wrapper = MonitorSettingEndpointWrapper.getWrapper(managerName);
		return wrapper.deleteMonitor(monitorIdList, HinemosModuleConstant.MONITOR_SYSTEMLOG);
	}
}
