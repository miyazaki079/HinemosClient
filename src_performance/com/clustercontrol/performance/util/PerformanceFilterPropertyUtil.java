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
package com.clustercontrol.performance.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.performance.bean.PerformanceFilterConstant;
import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.PropertyUtil;
import com.clustercontrol.ws.collector.PerformanceFilterInfo;

/**
 * 監視[一覧]のフィルタダイアログに関するutilityクラス
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class PerformanceFilterPropertyUtil {


	public static PerformanceFilterInfo property2dto(Property property){
		PerformanceFilterInfo info = new PerformanceFilterInfo();

		String monitorId = null;
		String monitorTypeId = null;
		String description = null;
		Timestamp latestFromDate = null;
		Timestamp latestToDate = null;
		Timestamp oldestFromDate = null;
		Timestamp oldestToDate = null;

		ArrayList<?> values = null;

		// 監視ID
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.MONITOR_ID);
		if (!"".equals(values.get(0))) {
			monitorId = (String) values.get(0);
			info.setMonitorId(monitorId);
		}

		// プラグインID
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.MONITOR_TYPE_ID);
		if (!"".equals(values.get(0))) {
			monitorTypeId = (String) values.get(0);
			info.setMonitorTypeId(monitorTypeId);
		}

		// 説明
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.DESCRIPTION);
		if (!"".equals(values.get(0))) {
			description = (String) values.get(0);
			info.setDescription(description);
		}

		// 最新収集時刻(From)
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.LATEST_FROM_DATE);
		if (values.get(0) instanceof Date) {
			latestFromDate = new Timestamp(((Date) values.get(0))
					.getTime());
			latestFromDate.setNanos(999999999);
			info.setLatestFromDate(latestFromDate.getTime());
		}

		// 最新収集時刻(To)
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.LATEST_TO_DATE);
		if (values.get(0) instanceof Date) {
			latestToDate = new Timestamp(((Date) values.get(0))
					.getTime());
			latestToDate.setNanos(999999999);
			info.setLatestToDate(latestToDate.getTime());
		}

		// 最古収集時刻(From)
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.OLDEST_FROM_DATE);
		if (values.get(0) instanceof Date) {
			oldestFromDate = new Timestamp(((Date) values.get(0))
					.getTime());
			oldestFromDate.setNanos(999999999);
			info.setOldestFromDate(oldestFromDate.getTime());
		}

		// 最古収集時刻(To)
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.OLDEST_TO_DATE);
		if (values.get(0) instanceof Date) {
			oldestToDate = new Timestamp(((Date) values.get(0))
					.getTime());
			oldestToDate.setNanos(999999999);
			info.setOldestToDate(oldestToDate.getTime());
		}

		return info;
	}

	/**
	 * 性能[一覧]フィルタ用プロパティを取得します。<BR>
	 *
	 * @param locale ロケール情報
	 * @return 性能[一覧]フィルタ用プロパティ
	 *
	 * @see com.clustercontrol.bean.Property
	 * @see com.clustercontrol.bean.PropertyDefineConstant
	 * @see com.clustercontrol.bean.PriorityConstant
	 * @see com.clustercontrol.bean.FacilityTargetConstant
	 */
	public static Property getProperty(Locale locale) {

		//マネージャ
		Property manager =
				new Property(PerformanceFilterConstant.MANAGER, Messages.getString("facility.manager", locale), PropertyDefineConstant.EDITOR_SELECT);
		//監視項目ID
		Property monitorId =
				new Property(PerformanceFilterConstant.MONITOR_ID, Messages.getString("monitor.id", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		//プラグインID
		Property monitorTypeId =
				new Property(PerformanceFilterConstant.MONITOR_TYPE_ID, Messages.getString("plugin.id", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		//説明
		Property description =
				new Property(PerformanceFilterConstant.DESCRIPTION, Messages.getString("description", locale), PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		//最古収集日時(FROM)
		Property oldestFromDate =
				new Property(PerformanceFilterConstant.OLDEST_FROM_DATE, Messages.getString("start", locale), PropertyDefineConstant.EDITOR_DATETIME);
		//最古収集日時(TO)
		Property oldestToDate =
				new Property(PerformanceFilterConstant.OLDEST_TO_DATE, Messages.getString("end", locale), PropertyDefineConstant.EDITOR_DATETIME);
		//最新収集日時(FROM)
		Property latestFromDate =
				new Property(PerformanceFilterConstant.LATEST_FROM_DATE, Messages.getString("start", locale), PropertyDefineConstant.EDITOR_DATETIME);
		//最新収集日時(TO)
		Property latestToDate =
				new Property(PerformanceFilterConstant.LATEST_TO_DATE, Messages.getString("end", locale), PropertyDefineConstant.EDITOR_DATETIME);

		//最古収集日時
		Property oldestDate =
				new Property(PerformanceFilterConstant.LATEST_DATE, Messages.getString("collection.oldest.date", locale), PropertyDefineConstant.EDITOR_TEXT);
		Property latestDate =
				new Property(PerformanceFilterConstant.LATEST_DATE, Messages.getString("collection.latest.date", locale), PropertyDefineConstant.EDITOR_TEXT);

		Object[] obj = EndpointManager.getActiveManagerSet().toArray();
		Object[] val = new Object[obj.length + 1];
		val[0] = "";
		for(int i = 0; i<obj.length; i++) {
			val[i + 1] = obj[i];
		}

		Object[][] managerValues = {val, val};
		manager.setSelectValues(managerValues);
		manager.setValue("");

		//値を初期化
		monitorId.setValue("");
		monitorTypeId.setValue("");
		description.setValue("");

		oldestFromDate.setValue("");
		oldestToDate.setValue("");
		latestFromDate.setValue("");
		latestToDate.setValue("");

		oldestDate.setValue("");
		latestDate.setValue("");

		//修正可否を設定
		manager.setModify(PropertyDefineConstant.MODIFY_OK);
		monitorId.setModify(PropertyDefineConstant.MODIFY_OK);
		monitorTypeId.setModify(PropertyDefineConstant.MODIFY_OK);
		description.setModify(PropertyDefineConstant.MODIFY_OK);
		oldestFromDate.setModify(PropertyDefineConstant.MODIFY_OK);
		oldestToDate.setModify(PropertyDefineConstant.MODIFY_OK);
		latestFromDate.setModify(PropertyDefineConstant.MODIFY_OK);
		latestToDate.setModify(PropertyDefineConstant.MODIFY_OK);

		oldestDate.setModify(PropertyDefineConstant.MODIFY_NG);
		latestDate.setModify(PropertyDefineConstant.MODIFY_NG);

		Property property = new Property(null, null, "");

		// 初期表示ツリーを構成。
		property.removeChildren();
		property.addChildren(manager);
		property.addChildren(monitorId);
		property.addChildren(monitorTypeId);
		property.addChildren(description);
		property.addChildren(oldestDate);
		property.addChildren(latestDate);


		// 最古収集日時
		oldestDate.removeChildren();
		oldestDate.addChildren(oldestFromDate);
		oldestDate.addChildren(oldestToDate);

		// 最新収集日時
		latestDate.removeChildren();
		latestDate.addChildren(latestFromDate);
		latestDate.addChildren(latestToDate);

		return property;
	}

	public static String getManagerName(Property property) {
		String manager = null;
		ArrayList<?> values = null;

		// 監視ID
		values = PropertyUtil.getPropertyValue(property,
				PerformanceFilterConstant.MANAGER);
		if (!"".equals(values.get(0))) {
			manager = (String) values.get(0);
		}

		return manager;
	}
}
