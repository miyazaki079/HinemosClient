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
package com.clustercontrol.monitor.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.monitor.bean.StatusFilterConstant;
import com.clustercontrol.repository.bean.FacilityTargetConstant;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.PropertyUtil;
import com.clustercontrol.ws.monitor.StatusFilterInfo;

/**
 * ステータス情報のDTOとプロパティを相互変換するためのユーティリティクラスです。
 * @since 0.1
 */
public class StatusFilterPropertyUtil {

	/**
	 * プロパティをステータス情報フィルタDTOに変換するメソッドです。
	 * 
	 * @param property
	 * @return ステータス情報検索条件
	 */
	public static StatusFilterInfo property2dto(Property property){
		StatusFilterInfo info = new StatusFilterInfo();


		Timestamp outputFromDate = null;
		Timestamp outputToDate = null;
		Timestamp generationFromDate = null;
		Timestamp generationToDate = null;
		String monitorId = null;
		String monitorDetailId = null;
		String facilityType = null;
		String application = null;
		String message = null;;
		String ownerRoleId = null;

		ArrayList<?> values = null;

		//重要度取得
		values = PropertyUtil.getPropertyValue(property,
				StatusFilterConstant.PRIORITY_CRITICAL);
		if (!"".equals(values.get(0))) {
			if ((Boolean)values.get(0)) {
				info.getPriorityList().add(PriorityConstant.TYPE_CRITICAL);
			}
		}
		values = PropertyUtil.getPropertyValue(property,
				StatusFilterConstant.PRIORITY_WARNING);
		if (!"".equals(values.get(0))) {
			if ((Boolean)values.get(0)) {
				info.getPriorityList().add(PriorityConstant.TYPE_WARNING);
			}
		}
		values = PropertyUtil.getPropertyValue(property,
				StatusFilterConstant.PRIORITY_INFO);
		if (!"".equals(values.get(0))) {
			if ((Boolean)values.get(0)) {
				info.getPriorityList().add(PriorityConstant.TYPE_INFO);
			}
		}
		values = PropertyUtil.getPropertyValue(property,
				StatusFilterConstant.PRIORITY_UNKNOWN);
		if (!"".equals(values.get(0))) {
			if ((Boolean)values.get(0)) {
				info.getPriorityList().add(PriorityConstant.TYPE_UNKNOWN);
			}
		}

		//更新日時（自）取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.OUTPUT_FROM_DATE);
		if(values.get(0) instanceof Date){
			outputFromDate = new Timestamp(((Date)values.get(0)).getTime());
			outputFromDate.setNanos(0);
			info.setOutputDateFrom(outputFromDate.getTime());
		}

		//更新日時（至）取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.OUTPUT_TO_DATE);
		if(values.get(0) instanceof Date){
			outputToDate = new Timestamp(((Date)values.get(0)).getTime());
			outputToDate.setNanos(999999999);
			info.setOutputDateTo(outputToDate.getTime());
		}

		//出力日時（自）取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.GENERATION_FROM_DATE);
		if(values.get(0) instanceof Date){
			generationFromDate = new Timestamp(((Date)values.get(0)).getTime());
			generationFromDate.setNanos(0);
			info.setGenerationDateFrom(generationFromDate.getTime());
		}

		//出力日時（至）取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.GENERATION_TO_DATE);
		if(values.get(0) instanceof Date){
			generationToDate = new Timestamp(((Date)values.get(0)).getTime());
			generationToDate.setNanos(999999999);
			info.setGenerationDateTo(generationToDate.getTime());
		}

		//監視項目ID取得
		values = PropertyUtil.getPropertyValue(property,
				StatusFilterConstant.MONITOR_ID);
		if (!"".equals(values.get(0))) {
			monitorId = (String) values.get(0);
			info.setMonitorId(monitorId);
		}

		//監視詳細取得
		values = PropertyUtil.getPropertyValue(property,
				StatusFilterConstant.MONITOR_DETAIL_ID);
		if (!"".equals(values.get(0))) {
			monitorDetailId = (String) values.get(0);
			info.setMonitorDetailId(monitorDetailId);
		}

		//対象ファシリティ種別取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.FACILITY_TYPE);
		if(!"".equals(values.get(0))){
			facilityType = (String)values.get(0);
			info.setFacilityType(facilityType);
		}

		//アプリケーション取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.APPLICATION);
		if(!"".equals(values.get(0))){
			application = (String)values.get(0);
			info.setApplication(application);
		}

		//メッセージ取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.MESSAGE);
		if(!"".equals(values.get(0))){
			message = (String)values.get(0);
			info.setMessage(message);
		}

		//オーナーロールID取得
		values = PropertyUtil.getPropertyValue(property, StatusFilterConstant.OWNER_ROLE_ID);
		if(!"".equals(values.get(0))){
			ownerRoleId = (String)values.get(0);
			info.setOwnerRoleId(ownerRoleId);
		}

		return info;
	}

	/**
	 * ステータス情報フィルタ用プロパティを返します。<BR>
	 * <p>
	 * <ol>
	 *  <li>フィルタ項目毎にID, 名前, 処理定数（{@link com.clustercontrol.bean.PropertyDefineConstant}）を指定し、
	 *      プロパティ（{@link com.clustercontrol.bean.Property}）を生成します。</li>
	 *  <li>各フィルタ項目のプロパティをツリー状に定義します。</li>
	 * </ol>
	 * 
	 * <p>プロパティに定義するフィルタ条件は、下記の通りです。
	 * <p>
	 * <ul>
	 *  <li>プロパティ（親。ダミー）</li>
	 *  <ul>
	 *   <li>重要度（子。テキスト）</li>
	 *   <ul>
	 *    <li>危険（孫。チェックボックス）</li>
	 *    <li>警告（孫。チェックボックス）</li>
	 *    <li>情報（孫。チェックボックス）</li>
	 *    <li>不明（孫。チェックボックス）</li>
	 *   </ul>
	 *   <li>最終変更日時（子。テキスト）</li>
	 *   <ul>
	 *    <li>開始（孫。日時ダイアログ）</li>
	 *    <li>終了（孫。日時ダイアログ）</li>
	 *   </ul>
	 *   <li>出力日時（子。テキスト）</li>
	 *   <ul>
	 *    <li>開始（孫。日時ダイアログ）</li>
	 *    <li>終了（孫。日時ダイアログ）</li>
	 *   </ul>
	 *   <li>監視項目ID（子。テキスト）</li>
	 *   <li>監視詳細（子。テキスト）</li>
	 *   <li>ファシリティのターゲット（子。コンボボックス）</li>
	 *   <li>アプリケーション（子。テキスト）</li>
	 *   <li>メッセージ（子。テキスト）</li>
	 *  </ul>
	 * </ul>
	 * 
	 * @param locale ロケール情報
	 * @return ステータス情報フィルタ用プロパティ
	 * 
	 * @see com.clustercontrol.bean.Property
	 * @see com.clustercontrol.bean.PropertyDefineConstant
	 * @see com.clustercontrol.bean.FacilityTargetConstant
	 */
	public static Property getProperty(Locale locale) {

		//重要度
		Property m_priority =
				new Property(StatusFilterConstant.PRIORITY, Messages.getString("priority", locale), PropertyDefineConstant.EDITOR_TEXT);
		//重要度（危険）
		Property m_priorityCritical =
				new Property(StatusFilterConstant.PRIORITY_CRITICAL, Messages.getString("critical", locale), PropertyDefineConstant.EDITOR_BOOL);
		//重要度(警告)
		Property m_priorityWarning =
				new Property(StatusFilterConstant.PRIORITY_WARNING, Messages.getString("warning", locale), PropertyDefineConstant.EDITOR_BOOL);
		//重要度(情報)
		Property m_priorityInfo =
				new Property(StatusFilterConstant.PRIORITY_INFO, Messages.getString("info", locale), PropertyDefineConstant.EDITOR_BOOL);
		//重要度(不明)
		Property m_priorityUnknown =
				new Property(StatusFilterConstant.PRIORITY_UNKNOWN, Messages.getString("unknown", locale), PropertyDefineConstant.EDITOR_BOOL);
		//更新日時（自）
		Property m_outputFromDate =
				new Property(StatusFilterConstant.OUTPUT_FROM_DATE, Messages.getString("start", locale), PropertyDefineConstant.EDITOR_DATETIME);
		//更新日時（至）
		Property m_outputToDate =
				new Property(StatusFilterConstant.OUTPUT_TO_DATE, Messages.getString("end", locale), PropertyDefineConstant.EDITOR_DATETIME);
		//出力日時（自）
		Property m_generationFromDate =
				new Property(StatusFilterConstant.GENERATION_FROM_DATE, Messages.getString("start", locale), PropertyDefineConstant.EDITOR_DATETIME);
		//出力日時（至）
		Property m_generationToDate =
				new Property(StatusFilterConstant.GENERATION_TO_DATE, Messages.getString("end", locale), PropertyDefineConstant.EDITOR_DATETIME);
		//監視項目ID
		Property m_monitorId =
				new Property(StatusFilterConstant.MONITOR_ID, Messages.getString("monitor.id", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		//監視項目詳細
		Property m_monitorDetailId =
				new Property(StatusFilterConstant.MONITOR_DETAIL_ID, Messages.getString("monitor.detail.id", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_1024);
		//対象ファシリティ種別
		Property m_facilityType =
				new Property(StatusFilterConstant.FACILITY_TYPE, Messages.getString("facility.target", locale), PropertyDefineConstant.EDITOR_SELECT);
		//アプリケーション
		Property m_application =
				new Property(StatusFilterConstant.APPLICATION, Messages.getString("application", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		//メッセージ
		Property m_message =
				new Property(StatusFilterConstant.MESSAGE, Messages.getString("message", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_256);
		//オーナーロールID
		Property m_ownerRoleId =
				new Property(StatusFilterConstant.OWNER_ROLE_ID, Messages.getString("owner.role.id", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		//更新日時
		Property m_outputDate =
				new Property(StatusFilterConstant.OUTPUT_DATE, Messages.getString("update.time", locale), PropertyDefineConstant.EDITOR_TEXT);
		//出力日時
		Property m_generationDate =
				new Property(StatusFilterConstant.GENERATION_DATE, Messages.getString("output.time", locale), PropertyDefineConstant.EDITOR_TEXT);

		//値を初期化

		m_priority.setValue("");
		m_priorityCritical.setValue(new Boolean(true));
		m_priorityWarning.setValue(new Boolean(true));
		m_priorityInfo.setValue(new Boolean(true));
		m_priorityUnknown.setValue(new Boolean(true));

		m_outputFromDate.setValue("");
		m_outputToDate.setValue("");
		m_generationFromDate.setValue("");
		m_generationToDate.setValue("");

		m_monitorId.setValue("");
		m_monitorDetailId.setValue("");

		Object facilityTypeValues[][] = {
				{ FacilityTargetConstant.STRING_ALL, FacilityTargetConstant.STRING_BENEATH},
				{ FacilityTargetConstant.STRING_ALL, FacilityTargetConstant.STRING_BENEATH}};

		m_facilityType.setSelectValues(facilityTypeValues);
		m_facilityType.setValue(FacilityTargetConstant.STRING_ALL);

		m_application.setValue("");
		m_message.setValue("");
		m_ownerRoleId.setValue("");

		m_outputDate.setValue("");
		m_generationDate.setValue("");

		//変更の可/不可を設定
		m_priority.setModify(PropertyDefineConstant.MODIFY_NG);
		m_priorityCritical.setModify(PropertyDefineConstant.MODIFY_OK);
		m_priorityWarning.setModify(PropertyDefineConstant.MODIFY_OK);
		m_priorityInfo.setModify(PropertyDefineConstant.MODIFY_OK);
		m_priorityUnknown.setModify(PropertyDefineConstant.MODIFY_OK);
		m_outputFromDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_outputToDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_generationFromDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_generationToDate.setModify(PropertyDefineConstant.MODIFY_OK);
		m_monitorId.setModify(PropertyDefineConstant.MODIFY_OK);
		m_monitorDetailId.setModify(PropertyDefineConstant.MODIFY_OK);
		m_facilityType.setModify(PropertyDefineConstant.MODIFY_OK);
		m_application.setModify(PropertyDefineConstant.MODIFY_OK);
		m_message.setModify(PropertyDefineConstant.MODIFY_OK);
		m_ownerRoleId.setModify(PropertyDefineConstant.MODIFY_OK);

		m_outputDate.setModify(PropertyDefineConstant.MODIFY_NG);
		m_generationDate.setModify(PropertyDefineConstant.MODIFY_NG);

		Property property = new Property(null, null, "");

		// 初期表示ツリーを構成。
		property.removeChildren();
		property.addChildren(m_priority);
		property.addChildren(m_outputDate);
		property.addChildren(m_generationDate);
		property.addChildren(m_monitorId);
		property.addChildren(m_monitorDetailId);
		property.addChildren(m_facilityType);
		property.addChildren(m_application);
		property.addChildren(m_message);
		property.addChildren(m_ownerRoleId);

		// 重要度
		m_priority.removeChildren();
		m_priority.addChildren(m_priorityCritical);
		m_priority.addChildren(m_priorityWarning);
		m_priority.addChildren(m_priorityInfo);
		m_priority.addChildren(m_priorityUnknown);

		// 更新日時
		m_outputDate.removeChildren();
		m_outputDate.addChildren(m_outputFromDate);
		m_outputDate.addChildren(m_outputToDate);

		// 出力日時
		m_generationDate.removeChildren();
		m_generationDate.addChildren(m_generationFromDate);
		m_generationDate.addChildren(m_generationToDate);

		return property;
	}
}
