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

package com.clustercontrol.snmp.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;

import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.monitor.bean.MonitorTypeMstConstant;
import com.clustercontrol.monitor.util.MonitorSettingEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.monitor.InvalidRole_Exception;
import com.clustercontrol.ws.monitor.MonitorInfo;

/**
 * SNMP監視を取得するクライアント側アクションクラス<BR>
 *
 * @version 2.1.0
 * @since 2.0.0
 */
public class GetSnmp {

	// ログ
	private static Log m_log = LogFactory.getLog( GetSnmp.class );
	/** マネージャ名 */

	/**
	 * SNMP監視情報を返します。
	 *
	 * @param monitorId 監視項目ID
	 * @param monitorType 監視判定タイプ
	 * @return SNMP監視情報
	 */
	public MonitorInfo getSnmp(String managerName, String monitorId, int monitorType) {

		MonitorInfo info = null;
		try {
			MonitorSettingEndpointWrapper wrapper = MonitorSettingEndpointWrapper.getWrapper(managerName);
			if (monitorType == MonitorTypeMstConstant.MONITOR_TYPE_NUMERIC) {
				info = wrapper.getMonitor(monitorId, HinemosModuleConstant.MONITOR_SNMP_N);
			} else {
				info = wrapper.getMonitor(monitorId, HinemosModuleConstant.MONITOR_SNMP_S);
			}

		} catch (InvalidRole_Exception e) {
			// アクセス権なしの場合、エラーダイアログを表示する
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));

		} catch (Exception e) {
			// 上記以外の例外
			m_log.warn("getSnmp(), " + e.getMessage(), e);
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}

		return info;
	}
}
