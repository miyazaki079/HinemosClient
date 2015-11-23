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

package com.clustercontrol.http.action;

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
 * HTTP監視設定情報を取得するクライアント側アクションクラス<BR>
 *
 * @version 2.1.0
 * @since 2.1.0
 */
public class GetHttp {

	// ログ
	private static Log m_log = LogFactory.getLog( GetHttp.class );

	/**
	 * HTTP監視情報をマネージャから取得します。<BR>
	 *
	 * @param monitorId 監視項目ID
	 * @param monitorType 監視判定タイプ
	 * @return HTTP監視情報
	 */
	public MonitorInfo getHttp(String managerName, String monitorId, int monitorType) {

		MonitorInfo info = null;

		try {
			MonitorSettingEndpointWrapper wrapper = MonitorSettingEndpointWrapper.getWrapper(managerName);
			if (monitorType == MonitorTypeMstConstant.MONITOR_TYPE_NUMERIC) {
				info = wrapper.getMonitor(monitorId, HinemosModuleConstant.MONITOR_HTTP_N);
			} else if (monitorType == MonitorTypeMstConstant.MONITOR_TYPE_STRING) {
				info = wrapper.getMonitor(monitorId, HinemosModuleConstant.MONITOR_HTTP_S);
			} else {
				info = wrapper.getMonitor(monitorId, HinemosModuleConstant.MONITOR_HTTP_SCENARIO);
			}

		} catch (InvalidRole_Exception e) {
			// アクセス権なしの場合、エラーダイアログを表示する
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));

		} catch (Exception e) {
			m_log.warn("getHttp() getMonitor, " + e.getMessage(), e);
			// 上記以外の例外
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}

		return info;
	}
}
