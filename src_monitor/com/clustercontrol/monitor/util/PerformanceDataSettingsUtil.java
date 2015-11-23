package com.clustercontrol.monitor.util;

import com.clustercontrol.ws.collector.PerformanceDataSettings;

/**
 * PerformanceDataSettingsのためのユーティリティクラスです。
 * @since 0.1
 */
public class PerformanceDataSettingsUtil {

	/**
	 * 指定した収集項目ID(ItemCode)に該当する収集項目名を取得する
	 * @param itemCode
	 * @return
	 */
	public static String getItemName(PerformanceDataSettings item, String itemCode) {
		String itemName = null;
		PerformanceDataSettings.ItemNameMap itemNameMap = item.getItemNameMap();
		if (itemNameMap != null){
			for(PerformanceDataSettings.ItemNameMap.Entry entry : itemNameMap.getEntry()) {
				if (itemCode.equals(entry.getKey())) {
					itemName = entry.getValue();
					break;
				}
			}
		}
		return itemName;
	}

}
