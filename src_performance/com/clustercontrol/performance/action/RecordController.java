/*

 Copyright (C) 2006 NTT DATA Corporation

 This program is free software; you can redistribute it and/or
 Modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation, version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 */

package com.clustercontrol.performance.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.clustercontrol.performance.util.CollectorEndpointWrapper;
import com.clustercontrol.ws.collector.CollectedDataSet;
import com.clustercontrol.ws.collector.CollectorItemInfo;
import com.clustercontrol.ws.collector.CollectorItemTreeItem;
import com.clustercontrol.ws.collector.HashMapInfo;
import com.clustercontrol.ws.collector.HashMapInfo.Map2;
import com.clustercontrol.ws.collector.HashMapInfo.Map2.Entry;
import com.clustercontrol.ws.collector.InvalidRole_Exception;

/**
 * 収集した性能情報を取得を行うアクションクラス
 *
 * @version 4.0.0
 * @since 1.0.0
 *
 */
public class RecordController {
	private static Log log = LogFactory.getLog(RecordController.class);

	private int errorCount = 1;

	/**
	 * コンストラクタ
	 */
	public RecordController() {
	}

	/**
	 * 収集した性能値を取得します。
	 *
	 * @return CollectedDataSet
	 */
	public CollectedDataSet getRecordCollectedData(
			String managerName,
			ArrayList<String> facilityIdList,
			ArrayList<CollectorItemInfo> itemInfoList,
			Date startDate,
			Date endDate) {
		log.debug("getRecordCollectedData() startDate = " + startDate + ", endDate = " + endDate);

		CollectedDataSet dataSet = null;
		try {
			CollectorEndpointWrapper wrapper = CollectorEndpointWrapper.getWrapper(managerName);
			dataSet = wrapper.getRecordCollectedDataFromIdList(
					facilityIdList, itemInfoList, startDate.getTime(), endDate.getTime());
		} catch (InvalidRole_Exception e) {
			log.error("getRecordCollectedData()",e);
		} catch (Exception e) {
			log.error("getRecordCollectedData()",e);
		}
		return dataSet;
	}

	/**
	 * 収集項目コード情報を取得します。
	 *
	 * @param ファシリティID
	 * @return デバイス情報セット
	 */
	public Map<String, CollectorItemTreeItem> getItemCodeTreeMap(String managerName) {
		log.debug("getItemCodeTreeMap() : managerName=" + managerName);
		CollectorItemTreeItem treeItem = null;
		for (int i = 0; i <= this.errorCount; i++) {
			try {
				//TODO:見直しする
				//            	return bean.getItemCodeMap();
				CollectorEndpointWrapper wrapper = CollectorEndpointWrapper.getWrapper(managerName);
				HashMapInfo hashMapInfo = wrapper.getItemCodeMap();
				Map2 map2 = hashMapInfo.getMap2();
				Map<String, CollectorItemTreeItem> rtnMap = new ConcurrentHashMap<String, CollectorItemTreeItem>();
				for(Entry entry : map2.getEntry()) {
					log.trace("entry : key=" + entry.getKey());
					treeItem = entry.getValue();
					setTreeParent(treeItem);
					rtnMap.put(entry.getKey(), treeItem);
				}
				log.debug("getItemCodeTreeMap() : size=" + rtnMap.size());
				return rtnMap;
			} catch (InvalidRole_Exception e) {
				log.error("getItemCodeTreeMap()", e);
			} catch (Exception e){
				log.error("getItemCodeTreeMap()", e);
			}
		}

		return null;
	}

	private static void setTreeParent(CollectorItemTreeItem item) {
		List<CollectorItemTreeItem> children = item.getChildren();
		for (CollectorItemTreeItem child : children) {
			child.setParent(item);
			setTreeParent(child);
		}
	}

}
