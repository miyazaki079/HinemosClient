/*

Copyright (C) 2013 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.jobmanagement.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;

import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobFileCheck;
import com.clustercontrol.ws.jobmanagement.JobKick;
import com.clustercontrol.ws.jobmanagement.JobSchedule;

/**
 * ジョブ[実行契機]情報[スケジュール&ファイルチェック]
 * を取得するクライアント側アクションクラス<BR>
 *
 * @version 4.1.0
 * @since 4.1.0
 */
public class GetJobKick {

	// ログ
	private static Log m_log = LogFactory.getLog( GetJobKick.class );

	/**
	 * 実行契機[スケジュール]情報を返します。<BR>
	 *
	 * @param managerName マネージャ名
	 * @param scheduleId 実行契機ID
	 * @return 実行契機[スケジュール]情報
	 */
	public static JobSchedule getJobSchedule(String managerName, String scheduleId) {
		JobSchedule jobSchedule  = null;
		try {
			JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
			jobSchedule  = wrapper.getJobSchedule(scheduleId);
		} catch (Exception e) {
			// 上記以外の例外
			m_log.warn("getJobSchedule(), " + e.getMessage(), e);
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		return jobSchedule ;
	}

	/**
	 * 実行契機[ファイルチェック]情報を返します。<BR>
	 *
	 * @param scheduleId 実行契機ID
	 * @return 実行契機[ファイルチェック]情報
	 */
	public static JobFileCheck getJobFileCheck(String managerName, String scheduleId) {
		JobFileCheck jobFileCheck = null;
		try {
			JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
			jobFileCheck = wrapper.getJobFileCheck(scheduleId);
		} catch (Exception e) {
			// 上記以外の例外
			m_log.warn("getJobFileCheck(), " + e.getMessage(), e);
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		return jobFileCheck ;
	}
	/**
	 * IDと一致する実行契機[スケジュール]リストを返します<BR>
	 * @param managerName
	 * @param jobKickIdList
	 * @return
	 */
	public static List<ArrayList<String>> getJobKick(String managerName, List<String> jobKickIdList){
		List<JobKick> jobKickList = new ArrayList<JobKick>();
		try {
			JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
			jobKickList  =wrapper.getJobKickList();
		} catch (Exception e) {
			// 上記以外の例外
			m_log.warn("getJobFileCheck(), " + e.getMessage(), e);
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		//IDと一致するものを探索
		List<ArrayList<String>> idList = new ArrayList<ArrayList<String>>();
		ArrayList<String> scheduleList = new ArrayList<String>();
		ArrayList<String> fileList = new ArrayList<String>();
		for(String id : jobKickIdList) {
			for(JobKick jobKick : jobKickList){
				if(jobKick.getId().equals(id) == false) {
					continue;
				}
				if(jobKick instanceof JobSchedule) {
					scheduleList.add(id);
				} else {
					fileList.add(id);
				}
			}
		}
		idList.add(scheduleList);
		idList.add(fileList);

		return idList;
	}
}
