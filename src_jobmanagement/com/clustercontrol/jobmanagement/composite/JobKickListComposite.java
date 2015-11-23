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

package com.clustercontrol.jobmanagement.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.UIManager;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.DayOfWeekConstant;
import com.clustercontrol.bean.ScheduleConstant;
import com.clustercontrol.jobmanagement.action.GetJobKickTableDefine;
import com.clustercontrol.jobmanagement.bean.JobTriggerTypeConstant;
import com.clustercontrol.jobmanagement.composite.action.JobKickDoubleClickListener;
import com.clustercontrol.jobmanagement.composite.action.JobKickSelectionChangedListener;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;
import com.clustercontrol.ws.jobmanagement.JobFileCheck;
import com.clustercontrol.ws.jobmanagement.JobKick;
import com.clustercontrol.ws.jobmanagement.JobSchedule;

/**
 * ジョブ[実行契機]ビュー用のコンポジットクラスです。
 *
 * @version 4.1.0
 * @since 1.0.0
 */
public class JobKickListComposite extends Composite {

	// ログ
	private static Log m_log = LogFactory.getLog( JobKickListComposite.class );

	/** テーブルビューア */
	private CommonTableViewer m_viewer = null;
	/** 選択アイテム */
	private ArrayList<ArrayList<?>> m_selectItem = null;

	/**
	 * コンストラクタ
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public JobKickListComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * コンポジットを構築します。
	 */
	private void initialize() {
		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Table table = new Table(this, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		WidgetTestUtil.setTestId(this, null, table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 1;
		table.setLayoutData(gridData);

		m_viewer = new CommonTableViewer(table);
		m_viewer.createTableColumn(GetJobKickTableDefine.get(),
				GetJobKickTableDefine.SORT_COLUMN_INDEX1,
				GetJobKickTableDefine.SORT_COLUMN_INDEX2,
				GetJobKickTableDefine.SORT_ORDER);

		m_viewer
		.addSelectionChangedListener(new JobKickSelectionChangedListener(
				this));
		// ダブルクリックリスナの追加
		m_viewer.addDoubleClickListener(new JobKickDoubleClickListener(this));
	}

	/**
	 * テーブルビューアーを更新します。<BR>
	 * ジョブ[実行契機]一覧情報を取得し、共通テーブルビューアーにセットします。
	 * <p>
	 * <ol>
	 * <li>スケジュール一覧情報を取得します。</li>
	 * <li>共通テーブルビューアーにジョブ[実行契機]一覧情報をセットします。</li>
	 * </ol>
	 *
	 * @see #getJobKick()
	 */
	@Override
	public void update() {
		m_log.trace("update()");
		ArrayList<Object> listInput = new ArrayList<Object>();
		for (Map.Entry<String, List<JobKick>> set : getJobKick().entrySet()) {
			for(JobKick jobKick : set.getValue()) {
				ArrayList<Object> a = new ArrayList<Object>();
				a.add(set.getKey());
				a.add(JobTriggerTypeConstant.typeToString(jobKick.getType()));
				a.add(jobKick.getId());
				a.add(jobKick.getName());
				a.add(jobKick.getJobId());
				a.add(jobKick.getJobName());
				a.add(jobKick.getJobunitId());
				a.add(new JobKickDetail(jobKick));
				a.add(jobKick.getCalendarId());
				a.add(jobKick.getValid());
				a.add(jobKick.getOwnerRoleId());
				a.add(jobKick.getCreateUser());
				if(jobKick.getCreateTime() == null){
					a.add(null);
				}
				else{
					a.add(new Date(jobKick.getCreateTime()));
				}
				a.add(jobKick.getUpdateUser());
				if(jobKick.getUpdateTime() == null){
					a.add(null);
				}
				else{
					a.add(new Date(jobKick.getUpdateTime()));
				}
				listInput.add(a);
			}
		}
		m_viewer.setInput(listInput);
	}
	
	private class JobKickDetail implements Comparable<JobKickDetail> {
		JobKick jobKick;
		private JobKickDetail(JobKick jobKick) {
			this.jobKick = jobKick;
		}

		@Override
		public int compareTo(JobKickDetail other) {
			int ret = 0;
			ret = this.jobKick.getType() - other.jobKick.getType();
			if (ret != 0) {
				return ret;
			}
			if(this.jobKick instanceof JobFileCheck &&
					other.jobKick instanceof JobFileCheck) {
				JobFileCheck fileCheck1 = (JobFileCheck)this.jobKick;
				JobFileCheck fileCheck2 = (JobFileCheck)other.jobKick;
				
				return fileCheck1.getFileName().compareTo(fileCheck2.getFileName());
			}
			if(this.jobKick instanceof JobSchedule &&
					other.jobKick instanceof JobSchedule) {
				JobSchedule schedule1 = (JobSchedule)this.jobKick;
				JobSchedule schedule2 = (JobSchedule)other.jobKick;
				
				ret = schedule1.getScheduleType() - schedule2.getScheduleType();
				if (ret != 0) {
					return ret;
				}
				
				if (schedule1.getScheduleType() == ScheduleConstant.TYPE_DAY) {
					Integer hour1 = schedule1.getHour();
					if (hour1 == null) { hour1 = 99; }
					Integer hour2 = schedule2.getHour();
					if (hour2 == null) { hour2 = 99; }
					ret = hour1 - hour2;
					if (ret != 0) {
						return ret;
					}
					
					Integer minute1 = schedule1.getMinute();
					if (minute1 == null) { minute1 = 99; }
					Integer minute2 = schedule2.getMinute();
					if (minute2 == null) { minute2 = 99; }
					ret = minute1 - minute2;
					return ret;
				} else if (schedule1.getScheduleType() == ScheduleConstant.TYPE_WEEK) {
					Integer week1 = schedule1.getWeek();
					Integer week2 = schedule2.getWeek();
					ret = week1 - week2;
					if (ret != 0) {
						return ret;
					}
					
					Integer hour1 = schedule1.getHour();
					if (hour1 == null) { hour1 = 99; }
					Integer hour2 = schedule2.getHour();
					if (hour2 == null) { hour2 = 99; }
					ret = hour1 - hour2;
					if (ret != 0) {
						return ret;
					}
					
					Integer minute1 = schedule1.getMinute();
					if (minute1 == null) { minute1 = 99; }
					Integer minute2 = schedule2.getMinute();
					if (minute2 == null) { minute2 = 99; }
					ret = minute1 - minute2;
					return ret;
				} else if (schedule1.getScheduleType() == ScheduleConstant.TYPE_REPEAT) {
					Integer x1 = schedule1.getEveryXminutes();
					Integer x2 = schedule2.getEveryXminutes();
					ret = x1 - x2;
					if (ret != 0) {
						return ret;
					}
					
					x1 = schedule1.getFromXminutes();
					x2 = schedule2.getFromXminutes();
					ret = x1 - x2;
					return ret;
				}
			}
			
			m_log.warn("compareTo warn");
			return 1;
		}
		
		@Override
		public String toString() {
			if (jobKick instanceof JobSchedule) {
				JobSchedule schedule = (JobSchedule) jobKick;
				String ret = "";
				if (schedule.getScheduleType() == ScheduleConstant.TYPE_DAY) {
					if (schedule.getHour() == null) {
						ret += "*";
					} else {
						ret += String.format("%02d", schedule.getHour());
					}
					ret += ":" + String.format("%02d", schedule.getMinute());
				} else if (schedule.getScheduleType() == ScheduleConstant.TYPE_WEEK) {
					ret += DayOfWeekConstant.typeToString(schedule.getWeek()) + " ";
					if (schedule.getHour() == null) {
						ret += "*";
					} else {
						ret += String.format("%02d", schedule.getHour());
					}
					ret += ":" + String.format("%02d", schedule.getMinute());
				} else if (schedule.getScheduleType() == ScheduleConstant.TYPE_REPEAT) {
					ret += String.format("%02d", schedule.getFromXminutes()) +
							Messages.getString("schedule.min.start.time") +
							String.format("%02d", schedule.getEveryXminutes()) +
							Messages.getString("schedule.min.execution.interval");
				}
				return ret;
			} else if (jobKick instanceof JobFileCheck) {
				JobFileCheck fileCheck = (JobFileCheck) jobKick;
				return fileCheck.getFileName();
			} else {
				m_log.warn("unknown class " + jobKick.getClass().getName());
			}
			return "";
		}
	}

	/**
	 * 実行契機一覧情報を取得します。
	 *
	 * @return 実行契機一覧情報
	 *
	 * @see com.clustercontrol.jobmanagement.action.GetSchedule#getSchedule()
	 */
	private Map<String, List<JobKick>> getJobKick() {
		Map<String, List<JobKick>> dispDataMap= new ConcurrentHashMap<String, List<JobKick>>();
		Map<String, String> errorMsgs = new ConcurrentHashMap<>();

		//実行契機情報取得
		for (String managerName : EndpointManager.getActiveManagerNameList()){
			try {
				JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
				dispDataMap.put(managerName, wrapper.getJobKickList());
			} catch (InvalidRole_Exception e) {
				errorMsgs.put( managerName, Messages.getString("message.accesscontrol.16") );
			} catch (Exception e) {
				m_log.warn("JobSearchTask(), " + e.getMessage(), e);
				errorMsgs.put(managerName,
						Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
			}
		}

		//メッセージ表示
		if( 0 < errorMsgs.size() ){
			UIManager.showMessageBox(errorMsgs, true);
		}

		return dispDataMap;
	}

	/**
	 * このコンポジットが利用するテーブルビューアを返します。
	 *
	 * @return テーブルビューア
	 */
	public TableViewer getTableViewer() {
		return m_viewer;
	}

	/**
	 * このコンポジットが利用するテーブルを返します。
	 *
	 * @return テーブル
	 */
	public Table getTable() {
		return m_viewer.getTable();
	}

	/**
	 * 選択アイテムを返します。
	 *
	 * @return 選択アイテム
	 */
	public ArrayList<ArrayList<?>> getSelectItem() {
		return m_selectItem;
	}

	/**
	 * 選択アイテムを設定します。
	 *
	 * @param selectItem 選択アイテム
	 */
	public void setSelectItem(ArrayList<ArrayList<?>> selectItem) {
		this.m_selectItem = selectItem;
	}
}
