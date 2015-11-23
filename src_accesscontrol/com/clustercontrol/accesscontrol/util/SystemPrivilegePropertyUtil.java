/*

Copyright (C) 2009 NTT DATA Corporation

This program is free software;
			you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY;
			without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */
package com.clustercontrol.accesscontrol.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.clustercontrol.accesscontrol.bean.FunctionConstant;
import com.clustercontrol.accesscontrol.bean.PrivilegeConstant.SystemPrivilegeMode;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.access.SystemPrivilegeInfo;

/**
 * システム権限の表示文字列とシステム権限テーブル値を保持するユーティリティクラスです。
 * 
 * @version 4.0.0
 */
public class SystemPrivilegePropertyUtil {
	/** システム権限マップ（システム権限、表示文字列） */
	private static Map<String, SystemPrivilegeInfo> m_systemPrivilegeMap = new ConcurrentHashMap<String, SystemPrivilegeInfo>();

	private static void createSystemPrivilegeMap(){
		Locale locale = Locale.getDefault();
		if (m_systemPrivilegeMap == null || m_systemPrivilegeMap.size() == 0) {
			SystemPrivilegeInfo info = null;
			//リポジトリ - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPOSITORY);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("repository.read", locale), info);
			//リポジトリ - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPOSITORY);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("repository.modify", locale), info);
			//リポジトリ - 作成

			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPOSITORY);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("repository.create", locale), info);
			//リポジトリ - 実行
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPOSITORY);
			info.setSystemPrivilege(SystemPrivilegeMode.EXEC.name());
			m_systemPrivilegeMap.put(Messages.getString("repository.execute", locale), info);

			//アカウント - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.ACCESSCONTROL);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("accesscontrol.read", locale), info);
			//アカウント - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.ACCESSCONTROL);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("accesscontrol.modify", locale), info);
			//アカウント - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.ACCESSCONTROL);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("accesscontrol.create", locale), info);

			//監視 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MONITOR_RESULT);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("monitor.result.read", locale), info);
			//監視 - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MONITOR_RESULT);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("monitor.result.modify", locale), info);

			//監視設定 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MONITOR_SETTING);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("monitor.setting.read", locale), info);
			//監視設定 - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MONITOR_SETTING);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("monitor.setting.modify", locale), info);
			//監視設定 - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MONITOR_SETTING);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("monitor.setting.create", locale), info);

			//ジョブ管理 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.JOBMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("jobmanagement.read", locale), info);
			//ジョブ - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.JOBMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("jobmanagement.modify", locale), info);
			//ジョブ - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.JOBMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("jobmanagement.create", locale), info);
			//ジョブ - 実行
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.JOBMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.EXEC.name());
			m_systemPrivilegeMap.put(Messages.getString("jobmanagement.execute", locale), info);

			//環境構築 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.INFRA);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("infra.read", locale), info);
			//環境構築 - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.INFRA);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("infra.modify", locale), info);
			//環境構築 - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.INFRA);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("infra.create", locale), info);
			//環境構築 - 実行
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.INFRA);
			info.setSystemPrivilege(SystemPrivilegeMode.EXEC.name());
			m_systemPrivilegeMap.put(Messages.getString("infra.execute", locale), info);

			//性能管理 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.PERFORMANCE);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("performance.read", locale), info);
			//性能管理 - 実行
			// なし

			//カレンダ - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.CALENDAR);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("calendar.read", locale), info);
			//カレンダ - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.CALENDAR);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("calendar.modify", locale), info);
			//カレンダ - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.CALENDAR);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("calendar.create", locale), info);

			//通知 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.NOTIFY);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("notify.id.read", locale), info);
			//通知 - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.NOTIFY);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("notify.id.modify", locale), info);
			//通知 - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.NOTIFY);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("notify.id.create", locale), info);

			//履歴情報削除 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MAINTENANCE);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("maintenance.read", locale), info);
			//履歴情報削除 - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MAINTENANCE);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("maintenance.modify", locale), info);
			//履歴情報削除 - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.MAINTENANCE);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("maintenance.create", locale), info);

			//クラウド管理 - 参照
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.CLOUDMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("cloud.management.read", locale), info);
			//クラウド管理 - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.CLOUDMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("cloud.management.modify", locale), info);
			//クラウド管理 - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.CLOUDMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("cloud.management.create", locale), info);
			//クラウド管理 - 実行
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.CLOUDMANAGEMENT);
			info.setSystemPrivilege(SystemPrivilegeMode.EXEC.name());
			m_systemPrivilegeMap.put(Messages.getString("cloud.management.execute", locale), info);

			//レポーティング - 参照 
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPORTING);
			info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
			m_systemPrivilegeMap.put(Messages.getString("reporting.read", locale), info);
			//レポーティング - 更新
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPORTING);
			info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
			m_systemPrivilegeMap.put(Messages.getString("reporting.modify", locale), info);
			//レポーティング - 作成
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPORTING);
			info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
			m_systemPrivilegeMap.put(Messages.getString("reporting.create", locale), info);
			//レポーティング - 実行
			info = new SystemPrivilegeInfo();
			info.setSystemFunction(FunctionConstant.REPORTING);
			info.setSystemPrivilege(SystemPrivilegeMode.EXEC.name());
			m_systemPrivilegeMap.put(Messages.getString("reporting.execute", locale), info);
		}

		return;
	}

	/**
	 * 
	 * @return Messages.getString("repository.read", locale) の形のリスト
	 */
	public static List<String> getSystemPrivilegeNameList() {
		createSystemPrivilegeMap();
		return new ArrayList<String>(m_systemPrivilegeMap.keySet());
	}

	/**
	 * 
	 * @param systemFunction
	 * @param systemPrivilege
	 * @return Messages.getString("repository.read", locale) の形
	 */
	public static String getSystemPrivilegeName(String systemFunction, String systemPrivilege){
		createSystemPrivilegeMap();
		for (String key : m_systemPrivilegeMap.keySet()) {
			SystemPrivilegeInfo info = m_systemPrivilegeMap.get(key);
			if (info.getSystemFunction().equals(systemFunction) &&
					info.getSystemPrivilege().equals(systemPrivilege)) {
				return key;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param Messages.getString("repository.read", locale) の形
	 * @return FunctionConstant.REPOSITORY + SystemPrivilegeMode.READ.name() の形
	 */
	public static String getFunctionPrivilege(String value){
		createSystemPrivilegeMap();
		SystemPrivilegeInfo info = m_systemPrivilegeMap.get(value);
		if (info == null) {
			return null;
		}
		return info.getSystemFunction() + info.getSystemPrivilege();
	}

	/**
	 * 
	 * @param FunctionConstant.REPOSITORY + SystemPrivilegeMode.READ.name() の形
	 * @return SystemPrivilegeInfo
	 */
	public static SystemPrivilegeInfo getSystemPrivilegeInfo(String s) {
		createSystemPrivilegeMap();
		for (String key : m_systemPrivilegeMap.keySet()) {
			SystemPrivilegeInfo info = m_systemPrivilegeMap.get(key);
			if (s.equals(info.getSystemFunction() + info.getSystemPrivilege())) {
				return info;
			}
		}
		return null;
	}
}
