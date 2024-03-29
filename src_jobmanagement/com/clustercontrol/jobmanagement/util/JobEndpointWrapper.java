package com.clustercontrol.jobmanagement.util;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.EndpointUnit;
import com.clustercontrol.util.EndpointUnit.EndpointSetting;
import com.clustercontrol.ws.jobmanagement.FacilityNotFound_Exception;
import com.clustercontrol.ws.jobmanagement.HinemosUnknown_Exception;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;
import com.clustercontrol.ws.jobmanagement.InvalidSetting_Exception;
import com.clustercontrol.ws.jobmanagement.InvalidUserPass_Exception;
import com.clustercontrol.ws.jobmanagement.JobEndpoint;
import com.clustercontrol.ws.jobmanagement.JobEndpointService;
import com.clustercontrol.ws.jobmanagement.JobFileCheck;
import com.clustercontrol.ws.jobmanagement.JobForwardFile;
import com.clustercontrol.ws.jobmanagement.JobHistoryFilter;
import com.clustercontrol.ws.jobmanagement.JobHistoryList;
import com.clustercontrol.ws.jobmanagement.JobInfo;
import com.clustercontrol.ws.jobmanagement.JobInfoNotFound_Exception;
import com.clustercontrol.ws.jobmanagement.JobInvalid_Exception;
import com.clustercontrol.ws.jobmanagement.JobKick;
import com.clustercontrol.ws.jobmanagement.JobKickDuplicate_Exception;
import com.clustercontrol.ws.jobmanagement.JobMasterNotFound_Exception;
import com.clustercontrol.ws.jobmanagement.JobNodeDetail;
import com.clustercontrol.ws.jobmanagement.JobOperationInfo;
import com.clustercontrol.ws.jobmanagement.JobPlan;
import com.clustercontrol.ws.jobmanagement.JobPlanFilter;
import com.clustercontrol.ws.jobmanagement.JobSchedule;
import com.clustercontrol.ws.jobmanagement.JobSessionDuplicate_Exception;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;
import com.clustercontrol.ws.jobmanagement.JobTriggerInfo;
import com.clustercontrol.ws.jobmanagement.NotifyNotFound_Exception;
import com.clustercontrol.ws.jobmanagement.OtherUserGetLock_Exception;
import com.clustercontrol.ws.jobmanagement.OutputBasicInfo;
import com.clustercontrol.ws.jobmanagement.UpdateTimeNotLatest_Exception;
import com.clustercontrol.ws.jobmanagement.UserNotFound_Exception;
import com.sun.xml.internal.ws.client.ClientTransportException;

/**
 * Hinemosマネージャとの通信をするクラス。
 * HAのような複数マネージャ対応のため、このクラスを実装する。
 *
 * Hinemosマネージャと通信できない場合は、WebServiceExceptionがthrowされる。
 * WebServiceExeptionが出力された場合は、もう一台のマネージャと通信する。
 */
public class JobEndpointWrapper {

	// ログ
	private static Log m_log = LogFactory.getLog( JobEndpointWrapper.class );

	private EndpointUnit endpointUnit;

	public JobEndpointWrapper(EndpointUnit endpointUnit) {
		this.endpointUnit = endpointUnit;
	}

	public static JobEndpointWrapper getWrapper(String managerName) {
		return new JobEndpointWrapper(EndpointManager.get(managerName));
	}

	public EndpointUnit getEndpointUnit() {
		return this.endpointUnit;
	}

	public static List<EndpointSetting<JobEndpoint>> getJobEndpoint(EndpointUnit endpointUnit) {
		return endpointUnit.getEndpoint(JobEndpointService.class, JobEndpoint.class);
	}

	public void addSchedule(JobSchedule jobSchedule)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, InvalidSetting_Exception, JobKickDuplicate_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.addSchedule(jobSchedule);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("addSchedule(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void addFileCheck(JobFileCheck jobFileCheck)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, InvalidSetting_Exception, JobKickDuplicate_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.addFileCheck(jobFileCheck);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("addFileCheck(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void modifySchedule(JobSchedule jobSchedule)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, InvalidSetting_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.modifySchedule(jobSchedule);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("modifySchedule(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void modifyFileCheck(JobFileCheck jobFileCheck)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, InvalidSetting_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.modifyFileCheck(jobFileCheck);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("modifyFileCheck(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void deleteSchedule(List<String> scheduleIdList)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.deleteSchedule(scheduleIdList);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("deleteSchedule(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void deleteFileCheck(List<String> scheduleIdList)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.deleteFileCheck(scheduleIdList);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("deleteFileCheck(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public JobSchedule getJobSchedule(String jobKickId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getJobSchedule(jobKickId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getJobSchedule(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public JobFileCheck getJobFileCheck(String jobKickId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getJobFileCheck(jobKickId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getJobFileCheck(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<JobKick> getJobKickList()
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getJobKickList();
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getScheduleList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}
	public void setJobKickStatus(String scheduleId, boolean validFlag)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, InvalidSetting_Exception, JobInfoNotFound_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.setJobKickStatus(scheduleId, validFlag);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("setJobKickStatus(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<JobForwardFile> getForwardFileList(String sessionId, String jobunitId, String jobId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getForwardFileList(sessionId, jobunitId, jobId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getForwardFileList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public JobTreeItem getJobDetailList(String sessionId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				JobTreeItem item = endpoint.getJobDetailList(sessionId);
				setTreeParent(item);
				return item;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getJobDetailList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<String> getAvailableStartOperation(String sessionId, String jobunitId, String jobId, String facilityId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = (JobEndpoint) endpointSetting.getEndpoint();
				return endpoint.getAvailableStartOperation(sessionId, jobunitId, jobId, facilityId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getAvailableStartOperation(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<String> getAvailableStopOperation(String sessionId, String jobunitId, String jobId, String facilityId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = (JobEndpoint) endpointSetting.getEndpoint();
				return endpoint.getAvailableStopOperation(sessionId, jobunitId, jobId, facilityId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getAvailableStopOperation(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public JobTreeItem getJobTree(String ownerRoleId, boolean treeOnly)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobMasterNotFound_Exception, NotifyNotFound_Exception, UserNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				JobTreeItem item = endpoint.getJobTree(ownerRoleId, treeOnly);
				setTreeParent(item);
				m_log.info("getJobTree role=" + ownerRoleId);
				return item;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getJobTree(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	// ジョブマップからも利用します。
	public void setTreeParent(JobTreeItem item) {
		List<JobTreeItem> children = item.getChildren();
		for (JobTreeItem child : children) {
			child.setParent(item);
			setTreeParent(child);
		}
	}

	public List<JobNodeDetail> getNodeDetailList(String sessionId, String jobunitId, String jobId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getNodeDetailList(sessionId, jobunitId, jobId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getNodeDetailList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}



	public JobTreeItem getSessionJobInfo(String sessionId, String jobunitId, String jobId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception, NotifyNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getSessionJobInfo(sessionId, jobunitId, jobId);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getSessionJobInfo(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public JobHistoryList getJobHistoryList(JobHistoryFilter filter, int histories)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getJobHistoryList(filter, histories);
			} catch (WebServiceException e) {
				wse = e;
				if (e instanceof ClientTransportException) {
					m_log.warn("getJobHistoryList(), " + e.getMessage());
				} else {
					m_log.warn("getJobHistoryList(), " + e.getMessage());
				}
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public List<JobPlan> getPlanList(JobPlanFilter filter, int plans)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getPlanList(filter, plans);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getPlanList(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void operationJob(JobOperationInfo info)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = (JobEndpoint) endpointSetting.getEndpoint();
				endpoint.operationJob(info);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("operationJob(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public Long registerJobunit(JobTreeItem jobunit)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInvalid_Exception, JobMasterNotFound_Exception, InvalidSetting_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			JobTreeItem top = null; // TOPを保存しておく
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				removeTreeParent(jobunit);
				top = jobunit.getParent();
				jobunit.setParent(null);
				Long lastUpdateTime = endpoint.registerJobunit(jobunit);
				return lastUpdateTime;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("registerJob(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			} finally {
				jobunit.setParent(top);
				setTreeParent(jobunit);
			}
		}
		throw wse;
	}

	public void deleteJobunit(String jobunitId)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInvalid_Exception, JobMasterNotFound_Exception, InvalidSetting_Exception, NotifyNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.deleteJobunit(jobunitId);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("registerJobunit(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	private static void removeTreeParent(JobTreeItem item) {
		List<JobTreeItem> children = item.getChildren();
		for (JobTreeItem child : children) {
			child.setParent(null);
			removeTreeParent(child);
		}
	}

	public void runJob(String jobunitId, String jobId, OutputBasicInfo info, JobTriggerInfo triggerInfo)
			throws FacilityNotFound_Exception, HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobInfoNotFound_Exception, JobMasterNotFound_Exception, JobSessionDuplicate_Exception, InvalidSetting_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = (JobEndpoint) endpointSetting.getEndpoint();
				endpoint.runJob(jobunitId, jobId, info, triggerInfo);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("runJob(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public JobInfo getJobFull(JobInfo jobInfo)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, JobMasterNotFound_Exception, NotifyNotFound_Exception, UserNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getJobFull(jobInfo);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getJobFull(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}
	
	
	public List<JobInfo> getJobFullList(List<JobInfo> jobList)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, NotifyNotFound_Exception, UserNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getJobFullList(jobList);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getJobFull(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}
	
	public List<Long> getUpdateTimeList(List<String> jobunitIdList) throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				List<Long> updateTimeList = endpoint.getUpdateTimeList(jobunitIdList);
				return updateTimeList;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getJobunitUpdateTime(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public Integer getEditLock(String jobunitId, Long updateTime, boolean forceFlag)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, OtherUserGetLock_Exception, UpdateTimeNotLatest_Exception, JobInvalid_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				return endpoint.getEditLock(jobunitId, updateTime, forceFlag);
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("getEditLock(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void checkEditLock(String jobunitId, Integer editSession)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception, OtherUserGetLock_Exception, UpdateTimeNotLatest_Exception, JobInvalid_Exception, JobMasterNotFound_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.checkEditLock(jobunitId, editSession);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("checkEditLock(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}

	public void releaseEditLock(Integer editSession)
			throws HinemosUnknown_Exception, InvalidRole_Exception, InvalidUserPass_Exception {
		WebServiceException wse = null;
		for (EndpointSetting<JobEndpoint> endpointSetting : getJobEndpoint(endpointUnit)) {
			try {
				JobEndpoint endpoint = endpointSetting.getEndpoint();
				endpoint.releaseEditLock(editSession);
				return;
			} catch (WebServiceException e) {
				wse = e;
				m_log.warn("releaseEditLock(), " + e.getMessage());
				endpointUnit.changeEndpoint();
			}
		}
		throw wse;
	}
}
