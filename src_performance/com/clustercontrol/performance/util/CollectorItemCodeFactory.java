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

package com.clustercontrol.performance.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.rap.rwt.SingletonUtil;

import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.performance.action.RecordController;
import com.clustercontrol.performance.bean.GraphConstant;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.collector.CollectorItemInfo;
import com.clustercontrol.ws.collector.CollectorItemTreeItem;

/**
 * 収集項目コードの情報を生成するファクトリクラス
 *
 * @version 4.0.0
 * @since 1.0.0
 */
public class CollectorItemCodeFactory {
	private static Log log = LogFactory.getLog(CollectorItemCodeFactory.class);

	private Map<String, CollectorItemTreeItem> m_codeTable = null; // 収集項目コードがキー

	/**
	 * コンストラクター
	 *
	 */
	private CollectorItemCodeFactory() {
	}

	private static CollectorItemCodeFactory getInstance(){
		return SingletonUtil.getSessionInstance( CollectorItemCodeFactory.class );
	}

	/**
	 * 初期化を行います。
	 *
	 */
	private static void init(String managerName) {
		if (getInstance().m_codeTable != null) {
			return;
		}
		getInstance().m_codeTable = new RecordController().getItemCodeTreeMap(managerName);
	}

	/**
	 * 指定収集項目コードの項目名を返します。
	 *
	 * @param itemCode
	 * @return 指定収集項目コードの項目名
	 */
	public static String getItemName(String managerName, String itemCode) {
		init(managerName);
		if(getInstance().m_codeTable.get(itemCode).getItemCodeData() != null){
			return getInstance().m_codeTable.get(itemCode).getItemCodeData().getItemName();
		}
		else {
			return null;
		}
	}

	/**
	 * 指定収集項目コードの項目名をデバイス名も含んだ形で返します。
	 *
	 * @param itemCode 収集項目コード
	 * @param displayName リポジトリ表示名
	 * @return 指定収集項目コードの項目名
	 */
	public static String getFullItemName(String managerName, String itemCode, String displayName) {
		log.debug("getFullItemName() itemCode = " + itemCode + ", displayName = " + displayName);
		init(managerName);
		
		String name;
		if (displayName == null || displayName.equals(Messages.getString("none")) || displayName.equals("")) {
			name = getItemName(managerName, itemCode);
		} else {
			name = getItemName(managerName, itemCode) + "[" + displayName + "]";
		}

		log.trace("getFullItemName() name = " + name);
		return name;
	}

	/**
	 * 指定収集項目コードの項目名をデバイス名も含んだ形で返します。
	 *
	 * @param collectorItemInfo 収集項目情報
	 * @return 指定収集項目コードの項目名
	 */
	public static String getFullItemName(String managerName, CollectorItemInfo collectorItemInfo) {
		init(managerName);

		String itemCode = collectorItemInfo.getItemCode();

		// デバイス別の収集を行う項目か否かで出力を変更
		if(CollectorItemCodeFactory.isDeviceSupport(managerName, itemCode)){
			// デバイス名も含めた項目名を返す
			return getFullItemName(
					managerName,
					collectorItemInfo.getItemCode(),
					collectorItemInfo.getDisplayName()
					);
		} else {
			return getItemName(managerName, itemCode);
		}
	}

	/**
	 * 指定収集項目コードの性能値の単位を返します。
	 *
	 * @return 性能値の単位
	 */
	public static String getMeasure(String managerName, String itemCode) {
		init(managerName);
		if(getInstance().m_codeTable.get(itemCode).getItemCodeData() != null){
			return getInstance().m_codeTable.get(itemCode).getItemCodeData().getMeasure();
		}
		else{
			return null;
		}
	}

	/**
	 * 性能値の値が百分率か否かを返します。
	 * 積み上げ面グラフ（ノード別）の場合のみ、100を超える事がある。
	 * それ以外は、itemCodeから判別。
	 *
	 * @return true 百分率 false 百分率ではない
	 */
	public static boolean isRangeFixed(String managerName, String itemCode, int graphType) {
		log.debug("isRangeFixed() itemCode = " + itemCode + ", graphType = " + graphType);
		boolean isFixed = false;
		init(managerName);

		if (getInstance().m_codeTable.get(itemCode) == null) {
			isFixed = false;
		} else {
			boolean rangeFixed = false;
			if(getInstance().m_codeTable.get(itemCode).getItemCodeData() != null){
				rangeFixed = getInstance().m_codeTable.get(itemCode).getItemCodeData().getGraphRange() == YesNoConstant.TYPE_YES;
			}
			if (rangeFixed && graphType != GraphConstant.GRAPH_STACK){
				isFixed = true;
			}
			else{
				isFixed = false;
			}
		}

		log.debug("isRangeFixed() isFixed = " + isFixed);
		return isFixed;
	}

	/**
	 * デバイス別の性能値か否かを返します。
	 *
	 * @return true デバイス別の性能値、false デバイス別の性能値ではない
	 */
	public static boolean isDeviceSupport(String managerName, String itemCode) {
		init(managerName);
		if(getInstance().m_codeTable.get(itemCode).getItemCodeData() != null){
			return getInstance().m_codeTable.get(itemCode).getItemCodeData().getDeviceSupport() == YesNoConstant.TYPE_YES;
		}
		else{
			return false;
		}
	}

	/**
	 * 指定コードの内訳グラフを表示するのに必要な項目のコードをリストで返す。
	 *
	 * @param itemCode
	 * @return 項目コードのリスト
	 */
	public static List<String> getSubItemCode(String managerName, String itemCode) {
		init(managerName);

		ArrayList<String> itemCodeList = new ArrayList<String>();

		// 指定の収集コードに対応する収集項目ツリーの要素を取得
		CollectorItemTreeItem treeItem = getInstance().m_codeTable.get(itemCode);

		// 子項目を取得
		String subItemCode = null;
		for(CollectorItemTreeItem item : treeItem.getChildren()){
			if(item.getItemCodeData() != null){
				subItemCode = item.getItemCodeData().getItemCode();
			}
			itemCodeList.add(subItemCode);
		}

		return itemCodeList;
	}
}
