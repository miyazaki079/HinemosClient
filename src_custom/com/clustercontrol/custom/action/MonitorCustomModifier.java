/*

Copyright (C) 2011 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.custom.action;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;

import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.monitor.action.DeleteInterface;
import com.clustercontrol.monitor.util.MonitorSettingEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.monitor.MonitorNotFound_Exception;

/**
 * マネージャに対して、コマンド監視設定更新を要求するクラス<br/>
 *
 * @since 4.0
 */
public class MonitorCustomModifier implements DeleteInterface {

	/**
	 * 既存のコマンド監視設定を削除する。<br/>
	 *
	 * @param monitorId 削除対象とするコマンド監視の監視項目ID
	 * @return 削除できた場合はtrue, その他はfalse
	 */
	@Override
	public boolean delete(String managerName, List<String> monitorIdList) throws Exception{

		boolean result = false;
		MonitorSettingEndpointWrapper wrapper = MonitorSettingEndpointWrapper.getWrapper(managerName);
		try {
			result = wrapper.deleteMonitor(monitorIdList, HinemosModuleConstant.MONITOR_CUSTOM);
		} catch (MonitorNotFound_Exception e) {
			// 該当の監視項目IDが存在しなかった場合
			String[] msgArgs = new String[0];
			for (String monitorId : monitorIdList) {
				if(msgArgs[0] != null) {
					msgArgs[0] = msgArgs[0] + ", ";
				}
				msgArgs[0] = msgArgs[0] + monitorId;
			}
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.monitor.notfound", msgArgs));
		}

		return result;
	}
}
