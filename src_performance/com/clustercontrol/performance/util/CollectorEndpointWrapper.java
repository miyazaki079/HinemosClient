package com.clustercontrol.performance.util;

import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.clustercontrol.repository.util.RepositoryEndpointWrapper;
import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.EndpointUnit;
import com.clustercontrol.util.EndpointUnit.EndpointSetting;
import com.clustercontrol.ws.collector.CollectedDataSet;
import com.clustercontrol.ws.collector.CollectorEndpoint;
import com.clustercontrol.ws.collector.CollectorEndpointService;
import com.clustercontrol.ws.collector.CollectorItemInfo;
import com.clustercontrol.ws.collector.HashMapInfo;
import com.clustercontrol.ws.collector.HinemosUnknown_Exception;
import com.clustercontrol.ws.collector.InvalidRole_Exception;
import com.clustercontrol.ws.collector.InvalidUserPass_Exception;
import com.clustercontrol.ws.collector.PerformanceDataSettings;
import com.clustercontrol.ws.collector.PerformanceFilterInfo;
import com.clustercontrol.ws.collector.PerformanceListInfo;

/**
 * Hinemosマネージャとの通信をするクラス。
 * HAのような複数マネージャ対応のため、このクラスを実装する。
 *
 * Hinemosマネージャと通信できない場合は、WebServiceExceptionがthrowされる。
 * WebServiceExeptionが出力された場合は、もう一台のマネージャと通信する。
 */
public class CollectorEndpointWrapper {

	// ログ
	private static Log m_log = LogFactory.getLog( CollectorEndpointWrapper.class );

	private EndpointUnit endpointUnit;

	public CollectorEndpointWrapper(EndpointUnit endpointUnit) {
		this.endpointUnit = endpointUnit;
	}

	public static CollectorEndpointWrapper getWrapper(String managerName) {
		return new CollectorEndpointWrapper(EndpointManager.get(managerName));
	}

	private static List<EndpointSetting<CollectorEndpoint>> getCollectorEndpoint(EndpointUnit endpointUnit) {
		return endpointUnit.getEndpoint(CollectorEndpointService.class, CollectorEndpoint.class);
	}

	public PerformanceDataSettings getPerformanceGraphInfo(String monitorId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = endpointSetting.getEndpoint();
				PerformanceDataSettings settings = endpoint.getPerformanceGraphInfo(monitorId);
				RepositoryEndpointWrapper.setTreeParent(settings.getFacilityTreeItem());
				return settings;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getPerformanceGraphInfo(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public CollectedDataSet getRecordCollectedDataFromIdList(
			List<String> facilityIdList,
			List<CollectorItemInfo> itemInfoList,
			Long startDate,
			Long endDate)
					throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getRecordCollectedDataFromIdList(facilityIdList, itemInfoList, startDate, endDate);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getRecordCollectedDataFromIdList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<CollectorItemInfo> getAvailableCollectorItemList(String facilityId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = (CollectorEndpoint) endpointSetting.getEndpoint();
				return endpoint.getAvailableCollectorItemList(facilityId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getAvailableCollectorItemList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<PerformanceListInfo> getPerformanceList()
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getPerformanceList();
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getPerformanceList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<PerformanceListInfo> getPerformanceList(PerformanceFilterInfo condition)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getPerformanceListByCondition(condition);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getPerformanceList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public HashMapInfo getItemCodeMap()
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = (CollectorEndpoint) endpointSetting.getEndpoint();
				return endpoint.getItemCodeMap();
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getItemCodeMap(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<String> createPerfFile(String monitorId, String facilityId, boolean header, boolean archive)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = (CollectorEndpoint) endpointSetting.getEndpoint();
				return endpoint.createPerfFile(monitorId, facilityId, header, archive);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("createPerformanceFile(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public DataHandler downloadPerfFile(String fileName)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = (CollectorEndpoint) endpointSetting.getEndpoint();
				return endpoint.downloadPerfFile(fileName);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("downloadPerformanceFile(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void deletePerfFile(ArrayList<String> fileNameList)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<CollectorEndpoint> endpointSetting : getCollectorEndpoint(endpointUnit)) {
			try {
				CollectorEndpoint endpoint = (CollectorEndpoint) endpointSetting.getEndpoint();
				endpoint.deletePerfFile(fileNameList);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("deletePerformanceFile(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

}
