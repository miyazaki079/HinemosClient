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

package com.clustercontrol.jobmanagement.action;

import java.util.Locale;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.bean.StatusConstant;
import com.clustercontrol.jobmanagement.bean.HistoryFilterPropertyConstant;
import com.clustercontrol.jobmanagement.bean.JobTriggerTypeConstant;
import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.Messages;

/**
 * ジョブ[履歴]ビューのフィルタ用プロパティを取得するクライアント側アクションクラス<BR>
 *
 * マネージャにSessionBean経由でアクセスし、フィルタ用プロパティを取得する
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class GetHistoryFilterProperty {

	/**
	 * マネージャにSessionBean経由でアクセスし、履歴フィルタ用プロパティを取得する
	 *
	 * @return 履歴フィルタ用プロパティ
	 *
	 */
	public Property getProperty() {

		Locale locale = Locale.getDefault();

		//マネージャ
		Property m_manager =
				new Property(HistoryFilterPropertyConstant.MANAGER, Messages.getString("facility.manager", locale),
						PropertyDefineConstant.EDITOR_SELECT);
		//開始・再実行日時（自）
		Property m_startFromDate =
				new Property(HistoryFilterPropertyConstant.START_FROM_DATE, Messages.getString("start", locale),
						PropertyDefineConstant.EDITOR_DATETIME);
		//開始・再実行日時（至）
		Property m_startToDate =
				new Property(HistoryFilterPropertyConstant.START_TO_DATE, Messages.getString("end", locale),
						PropertyDefineConstant.EDITOR_DATETIME);
		//終了・中断日時（自）
		Property m_endFromDate =
				new Property(HistoryFilterPropertyConstant.END_FROM_DATE, Messages.getString("start", locale),
						PropertyDefineConstant.EDITOR_DATETIME);
		//終了・中断日時（至）
		Property m_endToDate =
				new Property(HistoryFilterPropertyConstant.END_TO_DATE, Messages.getString("end", locale),
						PropertyDefineConstant.EDITOR_DATETIME);
		//ジョブID
		Property m_jobId =
				new Property(HistoryFilterPropertyConstant.JOB_ID, Messages.getString("job.id", locale),
						PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		//状態
		Property m_status =
				new Property(HistoryFilterPropertyConstant.STATUS, Messages.getString("run.status", locale),
						PropertyDefineConstant.EDITOR_SELECT);
		//実行契機種別
		Property m_triggerType =
				new Property(HistoryFilterPropertyConstant.TRIGGER_TYPE, Messages.getString("trigger.type", locale),
						PropertyDefineConstant.EDITOR_SELECT);
		//実行契機情報
		Property m_triggerInfo =
				new Property(HistoryFilterPropertyConstant.TRIGGER_INFO, Messages.getString("trigger.info", locale),
						PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_128);
		//オーナーロールID
		Property m_ownerRoleId =
				new Property(HistoryFilterPropertyConstant.OWNER_ROLE_ID, Messages.getString("owner.role.id", locale),
						PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);

		//開始・再実行日時
		Property m_startDate =
				new Property(HistoryFilterPropertyConstant.START_DATE, Messages.getString("start.rerun.time", locale),
						PropertyDefineConstant.EDITOR_TEXT);
		//終了・中断日時
		Property m_endDate =
				new Property(HistoryFilterPropertyConstant.END_DATE, Messages.getString("end.suspend.time", locale),
						PropertyDefineConstant.EDITOR_TEXT);
		//実行契機
		Property m_trigger =
				new Property(HistoryFilterPropertyConstant.TRIGGER, Messages.getString("trigger", locale),
						PropertyDefineConstant.EDITOR_TEXT);

		Object[] obj = EndpointManager.getActiveManagerSet().toArray();
		Object[] val = new Object[obj.length + 1];
		val[0] = "";
		for(int i = 0; i<obj.length; i++) {
			val[i + 1] = obj[i];
		}

		Object[][] managerValues = {val, val};
		m_manager.setSelectValues(managerValues);
		m_manager.setValue("");

		//値を初期化（状態）
		Object statusV[] = { "", StatusConstant.STRING_WAIT, StatusConstant.STRING_RESERVING,
				StatusConstant.STRING_RUNNING, StatusConstant.STRING_STOPPING, StatusConstant.STRING_STOP,
				StatusConstant.STRING_END, StatusConstant.STRING_MODIFIED, StatusConstant.STRING_END_CALENDAR,
				StatusConstant.STRING_END_SKIP, StatusConstant.STRING_END_UNMATCH,
				StatusConstant.STRING_END_START_DELAY, StatusConstant.STRING_END_END_DELAY};
		Object statusValues[][] = {statusV, statusV};

		//値を初期化（実行契機種別）
		Object triggerValues[][] = {
				{ "", JobTriggerTypeConstant.STRING_SCHEDULE, JobTriggerTypeConstant.STRING_FILECHECK, JobTriggerTypeConstant.STRING_MANUAL, JobTriggerTypeConstant.STRING_MONITOR},
				{ "", JobTriggerTypeConstant.STRING_SCHEDULE, JobTriggerTypeConstant.STRING_FILECHECK, JobTriggerTypeConstant.STRING_MANUAL, JobTriggerTypeConstant.STRING_MONITOR}
		};

		m_status.setSelectValues(statusValues);
		m_status.setValue("");
		m_triggerType.setSelectValues(triggerValues);
		m_triggerType.setValue("");

		m_startFromDate.setValue("");
		m_startToDate.setValue("");
		m_endFromDate.setValue("");
		m_endToDate.setValue("");
		m_jobId.setValue("");
		m_triggerInfo.setValue("");
		m_ownerRoleId.setValue("");

		m_startDate.setValue("");
		m_endDate.setValue("");
		m_trigger.setValue("");

		//変更の可/不可を設定
		m_manager.setModify(PropertyDefineConstant.MODIFY_OK);
		m_startFromDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_startToDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_endFromDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_endToDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_jobId.setModify(PropertyDefineConstant.MODIFY_OK);
		m_status.setModify(PropertyDefineConstant.MODIFY_OK);
		m_triggerType.setModify(PropertyDefineConstant.MODIFY_OK);
		m_triggerInfo.setModify(PropertyDefineConstant.MODIFY_OK);
		m_ownerRoleId.setModify(PropertyDefineConstant.MODIFY_OK);


		m_startDate.setModify(PropertyDefineConstant.MODIFY_NG);
		m_endDate.setModify(PropertyDefineConstant.MODIFY_NG);
		m_trigger.setModify(PropertyDefineConstant.MODIFY_NG);

		Property property = new Property(null, null, "");

		// 初期表示ツリーを構成。
		property.removeChildren();
		property.addChildren(m_manager);
		property.addChildren(m_startDate);
		property.addChildren(m_endDate);
		property.addChildren(m_jobId);
		property.addChildren(m_status);
		property.addChildren(m_trigger);
		property.addChildren(m_ownerRoleId);

		// 開始・再実行日時
		m_startDate.removeChildren();
		m_startDate.addChildren(m_startFromDate);
		m_startDate.addChildren(m_startToDate);

		// 終了・中断日時
		m_endDate.removeChildren();
		m_endDate.addChildren(m_endFromDate);
		m_endDate.addChildren(m_endToDate);

		// 実行契機
		m_trigger.removeChildren();
		m_trigger.addChildren(m_triggerType);
		m_trigger.addChildren(m_triggerInfo);

		return property;
	}
}
