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

package com.clustercontrol.performance.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;

import com.clustercontrol.performance.util.CollectorEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.collector.InvalidRole_Exception;
import com.clustercontrol.ws.collector.PerformanceDataSettings;

/**
 * 性能グラフをのヘッダ情報の取得<BR>
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class GetPerformanceDataSettings {
	private static Log log = LogFactory.getLog(GetPerformanceDataSettings.class);

	/**
	 * 指定した監視IDのグラフ描画のためのヘッダ情報(PerformanceGraphInfo)を取得する
	 *
	 * @param monitorId
	 * @return
	 */
	public static PerformanceDataSettings getPerformanceGraphInfo(String managerName, String monitorId) {

		PerformanceDataSettings ret = null;

		try {
			CollectorEndpointWrapper wrapper = CollectorEndpointWrapper.getWrapper(managerName);
			ret = wrapper.getPerformanceGraphInfo(monitorId);

		} catch (InvalidRole_Exception e) {
			// アクセス権なしの場合、エラーダイアログを表示する
			MessageDialog.openInformation(null, Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));
		} catch (Exception e) {
			log.error("getPerformanceGraphInfo() monitorId = " + monitorId, e);
		}

		return ret;
	}
}
