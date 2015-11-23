/**********************************************************************
 * Copyright (C) 2006 NTT DATA Corporation
 * This program is free software; you can redistribute it and/or
 * Modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *********************************************************************/

package com.clustercontrol.performance.view;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.rap.rwt.SingletonUtil;

import com.clustercontrol.performance.action.GetPerformanceDataSettings;
import com.clustercontrol.ws.collector.PerformanceDataSettings;

/**
 * グラフ描画/性能実績データのエクスポートに必要なヘッダ情報<BR>
 *
 * @since 5.0.0
 */
public class PerformanceDataSettingCache{
	private ConcurrentHashMap<String, HashMap<String, PerformanceDataSettings>> settingMap = new ConcurrentHashMap<String, HashMap<String, PerformanceDataSettings>>();

	/** Private constructor */
	private PerformanceDataSettingCache(){}

	/** Instance getter */
	private static PerformanceDataSettingCache getInstance(){
		return SingletonUtil.getSessionInstance( PerformanceDataSettingCache.class );
	}

	/**
	 * 取得済みのグラフヘッダ情報を取得する
	 *
	 * @param managerName
	 * @param monitorId
	 * @return
	 */
	public static PerformanceDataSettings get(String managerName, String monitorId){
		PerformanceDataSettings perfDataSettings = null;
		HashMap<String, PerformanceDataSettings> map = getInstance().settingMap.get(managerName);
		if( null != map && null != monitorId ) {
			perfDataSettings = map.get(monitorId);
		}
		if( null == perfDataSettings ){
			perfDataSettings = GetPerformanceDataSettings.getPerformanceGraphInfo(managerName, monitorId);
		}
		return perfDataSettings;
	}

	/**
	 * 取得済みのグラフヘッダ情報を設定する
	 *
	 * @param managerName
	 * @param monitorId
	 * @param perfDataSettings
	 */
	public static void set(String managerName, String monitorId, PerformanceDataSettings perfDataSettings ){
		HashMap<String, PerformanceDataSettings> map = new HashMap<String, PerformanceDataSettings>();
		map.put(monitorId, perfDataSettings);
		getInstance().settingMap.put(managerName, map);
	}
}
