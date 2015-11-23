package com.clustercontrol.performance.util;

import java.util.ArrayList;
import java.util.List;

import com.clustercontrol.ws.collector.CollectedDataSet;
import com.clustercontrol.ws.collector.CollectorItemInfo;
import com.clustercontrol.ws.collector.FacilityIdItemCodeInfo;
import com.clustercontrol.ws.collector.CollectedDataInfo;
import com.clustercontrol.ws.collector.CollectedDataList;

public class CollectedDataSetUtil {

	/**
	 * 指定のファシリティIDと収集項目IDをキーに収集済み性能値情報のリストを取得します。
	 * 
	 * @param facilityID ファシリティID
	 * @param itemCode 収集項目コード
	 * @return 性能値データのリスト
	 */
	public static List<CollectedDataInfo> getCollectedDataList(CollectedDataSet dataSet, String facilityId, CollectorItemInfo key){
		FacilityIdItemCodeInfo keyInfo = new FacilityIdItemCodeInfo();
		keyInfo.setFacilityId(facilityId);
		keyInfo.setItemInfo(key);
		CollectedDataList dataList = null;
		for(CollectedDataSet.DataMap.Entry entry : dataSet.getDataMap().getEntry()) {
			//			if (keyInfo.equals(entry.getKey())) {
			if (compareKenInfo(keyInfo,entry.getKey())) {
				dataList = entry.getValue();
				break;
			}
		}
		if(dataList != null){
			return dataList.getList();
		} else {
			// 空のリストを生成して返す
			return new ArrayList<CollectedDataInfo>();
		}
	}

	private static boolean compareKenInfo(FacilityIdItemCodeInfo key1, FacilityIdItemCodeInfo key2){

		// facilityId
		if((key1.getFacilityId() == null && key2.getFacilityId() != null)){
			return false;
		}
		if(key1.getFacilityId() != null && !key1.getFacilityId().equals(key2.getFacilityId())){
			return false;
		}

		// CollectorItemCode
		if((key1.getItemInfo() == null && key2.getItemInfo() != null)){
			return false;
		}

		// CollectorItemCode.collectorId
		if((key1.getItemInfo().getCollectorId() == null && key2.getItemInfo().getCollectorId() != null)){
			return false;
		}
		if(key1.getItemInfo().getCollectorId() != null && !key1.getItemInfo().getCollectorId().equals(key2.getItemInfo().getCollectorId())){
			return false;
		}

		// CollectorItemCode.displayName
		if((key1.getItemInfo().getDisplayName() == null && key2.getItemInfo().getDisplayName() != null)){
			return false;
		}
		if(key1.getItemInfo().getDisplayName() != null && !key1.getItemInfo().getDisplayName().equals(key2.getItemInfo().getDisplayName())){
			return false;
		}

		// CollectorItemCode.itemCode
		if((key1.getItemInfo().getItemCode() == null && key2.getItemInfo().getItemCode() != null)){
			return false;
		}
		if(key1.getItemInfo().getItemCode() != null && !key1.getItemInfo().getItemCode().equals(key2.getItemInfo().getItemCode())){
			return false;
		}

		return true;
	}

	/**
	 * 指定のファシリティIDと収集項目IDをキーに収集済み性能値データのリストを登録します。
	 * 
	 * @param facilityID ファシリティID
	 * @param itemCode 収集項目コード
	 * @param data 性能値データのリスト
	 */
	public static void setCollectedDataList(
			CollectedDataSet dataSet,
			String facilityId,
			CollectorItemInfo itemInfo,
			List<CollectedDataInfo> dataList){

		FacilityIdItemCodeInfo keyInfo = new FacilityIdItemCodeInfo();
		keyInfo.setFacilityId(facilityId);
		keyInfo.setItemInfo(itemInfo);
		CollectedDataSet.DataMap.Entry entry = new CollectedDataSet.DataMap.Entry();
		entry.setKey(keyInfo);
		CollectedDataList list = new CollectedDataList();
		if (dataList != null) {
			list.getList().addAll(dataList);
		}
		entry.setValue(list);
		dataSet.getDataMap().getEntry().add(entry);
	}

}
