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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.EndStatusColorConstant;
import com.clustercontrol.bean.EndStatusConstant;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.notify.composite.NotifyIdListComposite;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobInfo;

/**
 * 通知先の指定タブ用のコンポジットクラスです。
 *
 * @version 3.0.0
 * @since 2.0.0
 */
public class NotificationsComposite extends Composite {
	/** 正常重要度用コンボボックス */
	private Combo m_normalPriority = null;
	/** 警告重要度用コンボボックス */
	private Combo m_warningPriority = null;
	/** 異常重要度用コンボボックス */
	private Combo m_abnormalPriority = null;
	/** 開始重要度用コンボボックス */
	private Combo m_startPriority = null;
	/** 通知ID */
	private NotifyIdListComposite m_notifyId = null;
	private JobInfo m_jobInfo = null;

	/** マネージャ名 */
	private String managerName = null;

	/**
	 * コンストラクタ
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 * @param managerName マネージャ名
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public NotificationsComposite(Composite parent, int style, String managerName) {
		super(parent, style);
		this.managerName = managerName;
		initialize();
	}

	/**
	 * コンポジットを構築します。
	 */
	private void initialize() {
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 1;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.fill = true;
		this.setLayout(layout);

		RowLayout rowLayout = null;

		//タイトル
		Composite notificationsComposite1 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "priority", notificationsComposite1);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		rowLayout.marginHeight = 2;
		notificationsComposite1.setLayout(rowLayout);

		Label dummy = new Label(notificationsComposite1, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy", dummy);
		dummy.setLayoutData(new RowData(60, SizeConstant.SIZE_LABEL_HEIGHT));

		Label importanceDegreeTitle = new Label(notificationsComposite1, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "importancedegreetitle", importanceDegreeTitle);
		importanceDegreeTitle.setText(Messages.getString("priority"));
		importanceDegreeTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));

		//開始
		Composite notificationsComposite2 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "notifications", notificationsComposite2);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		rowLayout.marginHeight = 2;
		notificationsComposite2.setLayout(rowLayout);

		Label beginningTitle = new Label(notificationsComposite2, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "beginningtitle", beginningTitle);
		beginningTitle.setText(EndStatusConstant.STRING_BEGINNING + " : ");
		beginningTitle.setLayoutData(new RowData(60,
				SizeConstant.SIZE_LABEL_HEIGHT));

		m_startPriority = new Combo(notificationsComposite2, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "startpriority", m_startPriority);
		m_startPriority.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_startPriority.add(PriorityConstant.STRING_INFO);
		m_startPriority.add(PriorityConstant.STRING_WARNING);
		m_startPriority.add(PriorityConstant.STRING_CRITICAL);
		m_startPriority.add(PriorityConstant.STRING_UNKNOWN);
		m_startPriority.add(PriorityConstant.STRING_NONE);

		//正常
		Composite notificationsComposite3 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "normal", notificationsComposite3);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		rowLayout.marginHeight = 2;
		notificationsComposite3.setLayout(rowLayout);

		Label normalTitle = new Label(notificationsComposite3, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "normaltitle", normalTitle);
		normalTitle.setText(EndStatusConstant.STRING_NORMAL + " : ");
		normalTitle.setLayoutData(new RowData(60,
				SizeConstant.SIZE_LABEL_HEIGHT));
		normalTitle.setBackground(EndStatusColorConstant.COLOR_NORMAL);

		m_normalPriority = new Combo(notificationsComposite3, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "normalpriority", m_normalPriority);
		m_normalPriority.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_normalPriority.add(PriorityConstant.STRING_INFO);
		m_normalPriority.add(PriorityConstant.STRING_WARNING);
		m_normalPriority.add(PriorityConstant.STRING_CRITICAL);
		m_normalPriority.add(PriorityConstant.STRING_UNKNOWN);
		m_normalPriority.add(PriorityConstant.STRING_NONE);

		//警告
		Composite notificationsComposite4 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "warning", notificationsComposite4);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		rowLayout.marginHeight = 2;
		notificationsComposite4.setLayout(rowLayout);

		Label warningTitle = new Label(notificationsComposite4, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "warningtitle", warningTitle);
		warningTitle.setText(EndStatusConstant.STRING_WARNING + " : ");
		warningTitle.setLayoutData(new RowData(60,
				SizeConstant.SIZE_LABEL_HEIGHT));
		warningTitle.setBackground(EndStatusColorConstant.COLOR_WARNING);

		m_warningPriority = new Combo(notificationsComposite4, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "warningpriority", m_warningPriority);
		m_warningPriority.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_warningPriority.add(PriorityConstant.STRING_INFO);
		m_warningPriority.add(PriorityConstant.STRING_WARNING);
		m_warningPriority.add(PriorityConstant.STRING_CRITICAL);
		m_warningPriority.add(PriorityConstant.STRING_UNKNOWN);
		m_warningPriority.add(PriorityConstant.STRING_NONE);

		//異常
		Composite notificationsComposite5 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "abnormal", notificationsComposite5);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		rowLayout.marginHeight = 2;
		notificationsComposite5.setLayout(rowLayout);

		Label abnormalTitle = new Label(notificationsComposite5, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "abnormaltitle", abnormalTitle);
		abnormalTitle.setText(EndStatusConstant.STRING_ABNORMAL + " : ");
		abnormalTitle.setLayoutData(new RowData(60,
				SizeConstant.SIZE_LABEL_HEIGHT));
		abnormalTitle.setBackground(EndStatusColorConstant.COLOR_ABNORMAL);

		m_abnormalPriority = new Combo(notificationsComposite5, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "abnormalpriority", m_abnormalPriority);
		m_abnormalPriority.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_abnormalPriority.add(PriorityConstant.STRING_INFO);
		m_abnormalPriority.add(PriorityConstant.STRING_WARNING);
		m_abnormalPriority.add(PriorityConstant.STRING_CRITICAL);
		m_abnormalPriority.add(PriorityConstant.STRING_UNKNOWN);
		m_abnormalPriority.add(PriorityConstant.STRING_NONE);

		//通知ID
		Composite notificationsComposite6 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "id", notificationsComposite6);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		rowLayout.marginHeight = 2;
		notificationsComposite6.setLayout(rowLayout);

		Label notifyIdTitle = new Label(notificationsComposite6, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "notifyidtitle", notifyIdTitle);
		notifyIdTitle.setText(Messages.getString("notify.id") + " : ");
		notifyIdTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));

		m_notifyId = new NotifyIdListComposite(notificationsComposite6, SWT.CENTER, false);
		m_notifyId.setManagerName(this.managerName);
		WidgetTestUtil.setTestId(this, "notifyidlist", m_notifyId);
	}

	/**
	 * ジョブ通知情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobNotificationsInfo
	 */
	public void reflectNotificationsInfo() {

		// 初期値
		m_normalPriority.select(0);
		m_warningPriority.select(1);
		m_abnormalPriority.select(2);
		m_startPriority.select(0);
		//  m_notifyId.setText("");

		if (this.m_jobInfo != null) {
			if (m_jobInfo.getBeginPriority() != null) {
				setSelectPriority(m_startPriority, m_jobInfo.getBeginPriority());
			}
			if (m_jobInfo.getNormalPriority() != null) {
				setSelectPriority(m_normalPriority, m_jobInfo.getNormalPriority());
			}
			if (m_jobInfo.getWarnPriority() != null) {
				setSelectPriority(m_warningPriority, m_jobInfo.getWarnPriority());
			}
			if (m_jobInfo.getAbnormalPriority() != null) {
				setSelectPriority(m_abnormalPriority,
						m_jobInfo.getAbnormalPriority());
			}

			if (m_jobInfo.getNotifyRelationInfos() != null
					&& m_jobInfo.getNotifyRelationInfos().size() > 0) {
				m_notifyId.setNotify(m_jobInfo.getNotifyRelationInfos());
			}
		}
	}

	/**
	 * ジョブ情報を返します。
	 *
	 * @return ジョブ通知情報のリスト
	 */
	public JobInfo getJobInfo() {
		return m_jobInfo;
	}

	/**
	 * コンポジットの情報から、ジョブ通知情報を作成します。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobNotificationsInfo
	 */
	public ValidateResult createNotificationsInfo() {
		m_jobInfo = new JobInfo();
		m_jobInfo.setBeginPriority(getSelectPriority(m_startPriority));
		m_jobInfo.setNormalPriority(getSelectPriority(m_normalPriority));
		m_jobInfo.setWarnPriority(getSelectPriority(m_warningPriority));
		m_jobInfo.setAbnormalPriority(getSelectPriority(m_abnormalPriority));
		if (m_notifyId.getNotify() != null) {
			m_jobInfo.getNotifyRelationInfos().addAll(m_notifyId.getNotify());
		}
		return null;
	}

	/**
	 * 指定した重要度に該当する重要度用コンボボックスの項目を選択します。
	 *
	 * @param combo 重要度用コンボボックスのインスタンス
	 * @param priority 重要度
	 *
	 * @see com.clustercontrol.bean.PriorityConstant
	 */
	public void setSelectPriority(Combo combo, int priority) {
		String select = "";

		if (priority == PriorityConstant.TYPE_CRITICAL) {
			select = PriorityConstant.STRING_CRITICAL;
		} else if (priority == PriorityConstant.TYPE_WARNING) {
			select = PriorityConstant.STRING_WARNING;
		} else if (priority == PriorityConstant.TYPE_INFO) {
			select = PriorityConstant.STRING_INFO;
		} else if (priority == PriorityConstant.TYPE_UNKNOWN) {
			select = PriorityConstant.STRING_UNKNOWN;
		} else if (priority == PriorityConstant.TYPE_NONE) {
			select = PriorityConstant.STRING_NONE;
		}

		combo.select(0);
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (select.equals(combo.getItem(i))) {
				combo.select(i);
				break;
			}
		}
	}

	/**
	 * 重要度用コンボボックスにて選択している重要度を取得します。
	 *
	 * @param combo 重要度用コンボボックスのインスタンス
	 * @return 重要度
	 *
	 * @see com.clustercontrol.bean.PriorityConstant
	 */
	public int getSelectPriority(Combo combo) {
		String select = combo.getText();

		if (select.equals(PriorityConstant.STRING_CRITICAL)) {
			return PriorityConstant.TYPE_CRITICAL;
		} else if (select.equals(PriorityConstant.STRING_WARNING)) {
			return PriorityConstant.TYPE_WARNING;
		} else if (select.equals(PriorityConstant.STRING_INFO)) {
			return PriorityConstant.TYPE_INFO;
		} else if (select.equals(PriorityConstant.STRING_UNKNOWN)) {
			return PriorityConstant.TYPE_UNKNOWN;
		} else if (select.equals(PriorityConstant.STRING_NONE)) {
			return PriorityConstant.TYPE_NONE;
		}

		return -1;
	}

	public NotifyIdListComposite getNotifyId() {
		return m_notifyId;
	}

	public void setNotifyId(NotifyIdListComposite notifyId) {
		this.m_notifyId = notifyId;
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		// super.setEnabled(enabled); // スクロールバーを動かせるように、ここはコメントアウト
		m_normalPriority.setEnabled(enabled);
		m_warningPriority.setEnabled(enabled);
		m_abnormalPriority.setEnabled(enabled);
		m_startPriority.setEnabled(enabled);
		m_notifyId.setButtonEnabled(enabled);
	}

	public void setJobInfo(JobInfo info) {
		this.m_jobInfo = info;
	}

}
