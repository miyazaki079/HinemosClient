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

package com.clustercontrol.jobmanagement.dialog;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.accesscontrol.bean.RoleIdConstant;
import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.composite.RoleIdListComposite;
import com.clustercontrol.composite.RoleIdListComposite.Mode;
import com.clustercontrol.composite.action.StringVerifyListener;
import com.clustercontrol.dialog.CommonDialog;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.fault.JobInvalid;
import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.composite.CommandComposite;
import com.clustercontrol.jobmanagement.composite.ControlComposite;
import com.clustercontrol.jobmanagement.composite.ControlNodeComposite;
import com.clustercontrol.jobmanagement.composite.EndDelayComposite;
import com.clustercontrol.jobmanagement.composite.EndStatusComposite;
import com.clustercontrol.jobmanagement.composite.FileComposite;
import com.clustercontrol.jobmanagement.composite.JobTreeComposite;
import com.clustercontrol.jobmanagement.composite.NotificationsComposite;
import com.clustercontrol.jobmanagement.composite.ParameterComposite;
import com.clustercontrol.jobmanagement.composite.ReferComposite;
import com.clustercontrol.jobmanagement.composite.StartDelayComposite;
import com.clustercontrol.jobmanagement.composite.WaitRuleComposite;
import com.clustercontrol.jobmanagement.util.JobEditState;
import com.clustercontrol.jobmanagement.util.JobEditStateUtil;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.jobmanagement.util.JobPropertyUtil;
import com.clustercontrol.jobmanagement.util.JobTreeItemUtil;
import com.clustercontrol.jobmanagement.util.JobUtil;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.ws.jobmanagement.JobEndStatusInfo;
import com.clustercontrol.ws.jobmanagement.JobInfo;
import com.clustercontrol.ws.jobmanagement.JobObjectInfo;
import com.clustercontrol.ws.jobmanagement.JobParameterInfo;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;
import com.clustercontrol.ws.jobmanagement.JobWaitRuleInfo;
import com.clustercontrol.ws.jobmanagement.OtherUserGetLock_Exception;

/**
 * ジョブ[ジョブの作成・変更]ダイアログクラスです。
 *
 * @version 4.0.0
 * @since 1.0.0
 */
public class JobDialog extends CommonDialog {
	// ログ
	private static Log m_log = LogFactory.getLog( JobDialog.class );

	/** ジョブID用テキスト */
	private Text m_jobIdText = null;
	/** ジョブ名用テキスト */
	private Text m_jobNameText = null;
	/** 説明テキスト */
	private Text m_jobAnnotationText = null;
	/** 待ち条件タブ用コンポジット */
	private WaitRuleComposite m_startComposite = null;
	/** 制御タブ用コンポジット */
	private ControlComposite m_controlComposite = null;
	/** 終了状態タブ用コンポジット */
	private EndStatusComposite m_endComposite = null;
	/** コマンドタブ用コンポジット */
	private CommandComposite m_executeComposite = null;
	/** ファイル転送タブ用コンポジット */
	private FileComposite m_fileComposite = null;
	/** 通知先の指定タブ用コンポジット */
	private NotificationsComposite m_messageComposite = null;
	/** 開始遅延タブ用コンポジット */
	private StartDelayComposite m_startDelayComposite = null;
	/** 終了遅延タブ用コンポジット */
	private EndDelayComposite m_endDelayComposite = null;
	/** 制御(ノード)用コンポジット */
	private ControlNodeComposite m_controlNodeComposite = null;
	/** ジョブ変数タブ用コンポジット */
	private ParameterComposite m_parameterComposite = null;
	/** 参照タブ用コンポジット */
	private ReferComposite m_referComposite = null;

	/** ジョブツリーアイテム */
	private JobTreeItem m_jobTreeItem = null;
	/** タブフォルダー */
	private TabFolder m_tabFolder = null;
	/** シェル */
	private Shell m_shell = null;
	/** 読み取り専用フラグ */
	private boolean m_readOnly = false;

	/** オーナーロールID用テキスト */
	private RoleIdListComposite m_ownerRoleId = null;

	/** マネージャ名 */
	private String m_managerName = null;

	private Button m_editButton;

	private JobTreeComposite m_jobTreeComposite = null;

	/**
	 * コンストラクタ
	 *
	 * @param parent 親シェル
	 * @param readOnly 読み取り専用フラグ true：変更不可、false：変更可
	 */
	public JobDialog(Shell parent, String managerName, boolean readOnly) {
		super(parent);
		this.m_managerName = managerName;
		m_readOnly = readOnly;
		this.m_jobTreeComposite = null;
	}

	public JobDialog(JobTreeComposite jobTreeComposite, Shell parent, String managerName, boolean readOnly) {
		this(parent, managerName, readOnly);
		this.m_jobTreeComposite = jobTreeComposite;
	}
	
	/**
	 * ダイアログエリアを生成します。
	 * <P>
	 * ジョブ種別により、表示するタブを切り替えます。
	 *
	 * @param parent 親コンポジット
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobInfo
	 * @see com.clustercontrol.bean.JobConstant
	 */
	@Override
	protected void customizeDialog(Composite parent) {
		m_shell = this.getShell();

		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 0;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.marginBottom = 0;
		layout.fill = true;
		parent.setLayout(layout);

		RowLayout rowLayout = null;

		JobInfo info = m_jobTreeItem.getData();
		// 本メソッドで詳細情報を取得するため、setJobFull実行
		JobPropertyUtil.setJobFull(m_managerName, info);

		if (info instanceof JobInfo) {
			if (info.getType() == JobConstant.TYPE_JOBUNIT) {
				parent.getShell().setText(
						Messages.getString("dialog.job.create.modify.jobunit"));
			} else if (info.getType() == JobConstant.TYPE_JOBNET) {
				parent.getShell().setText(
						Messages.getString("dialog.job.create.modify.jobnet"));
			} else if (info.getType() == JobConstant.TYPE_JOB) {
				parent.getShell().setText(
						Messages.getString("dialog.job.create.modify.job"));
			} else if (info.getType() == JobConstant.TYPE_FILEJOB) {
				parent.getShell().setText(
						Messages.getString("dialog.job.create.modify.forward.file.job"));
			} else if (info.getType() == JobConstant.TYPE_REFERJOB){
				parent.getShell().setText(
						Messages.getString("dialog.job.create.modify.refer.job"));
			}
		}
		boolean initFlag = true;
		if (info.getId() != null && info.getId().length() > 0) {
			initFlag = false;
		}

		// ジョブID
		Composite jobIdComposite = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, jobIdComposite);
		//jobIdComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogIdComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		jobIdComposite.setLayout(rowLayout);
		Label jobIdTitle = new Label(jobIdComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobid", jobIdTitle);
		jobIdTitle.setText(Messages.getString("job.id") + " : ");
		jobIdTitle
		.setLayoutData(new RowData(120, SizeConstant.SIZE_LABEL_HEIGHT));
		this.m_jobIdText = new Text(jobIdComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobid", m_jobIdText);
		this.m_jobIdText.setLayoutData(new RowData(200,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_jobIdText.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		this.m_jobIdText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		this.m_editButton = new Button(jobIdComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "edit", m_editButton);
		m_editButton.setText(Messages.getString("edit"));
		m_editButton.setEnabled(false);
		m_editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				super.widgetSelected(event);

				JobTreeItem jobunitItem = JobUtil.getTopJobUnitTreeItem(m_jobTreeItem);
				String jobunitId = jobunitItem.getData().getJobunitId();

				JobEditState JobEditState = JobEditStateUtil.getJobEditState( m_managerName );
				// 編集モードに入る
				Long updateTime = JobEditState.getJobunitUpdateTime(jobunitId);
				Integer result = null;
				try {
					result =JobUtil.getEditLock(m_managerName, jobunitId, updateTime, false);
				} catch (OtherUserGetLock_Exception e) {
					// 他のユーザがロックを取得している
					String message = e.getMessage();
					if (MessageDialog.openQuestion(
							null,
							Messages.getString("confirmed"),
							message)) {
						try {
							result = JobUtil.getEditLock(m_managerName, jobunitId, updateTime, true);
						} catch (Exception e1) {
							// ここには絶対にこないはず
							m_log.error("run() : logical error");
						}
					}
				}

				if (result != null) {
					// ロックを取得した
					m_log.debug("run() : get editLock(jobunitId="+jobunitId+")");
					JobEditState.addLockedJobunit(jobunitItem.getData(), JobTreeItemUtil.clone(jobunitItem, null), result);
					if (m_jobTreeComposite != null) {
						m_jobTreeComposite.refresh(jobunitItem.getParent());
					}

					//ダイアログの更新
					m_readOnly = false;
					updateWidgets();
				} else {
					// ロックの取得に失敗した
					m_log.debug("run() : cannot get editLock(jobunitId="+jobunitId+")");
				}
			}
		});

		// ジョブ名
		Composite jobNameComposite = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobname", jobNameComposite);
		//jobNameComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogNameComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		jobNameComposite.setLayout(rowLayout);
		Label jobNameTitle = new Label(jobNameComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobname", jobNameTitle);
		jobNameTitle.setText(Messages.getString("job.name") + " : ");
		jobNameTitle.setLayoutData(new RowData(120,
				SizeConstant.SIZE_LABEL_HEIGHT));
		this.m_jobNameText = new Text(jobNameComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "name", m_jobNameText);
		this.m_jobNameText.setLayoutData(new RowData(200,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_jobNameText.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		this.m_jobNameText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		// 説明
		Composite jobDescriptionComposite = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobdescription", jobDescriptionComposite);
		//jobDescriptionComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogDescriptionComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		jobDescriptionComposite.setLayout(rowLayout);
		Label jobAnnotationTitle = new Label(jobDescriptionComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobannotationtitle", jobAnnotationTitle);
		jobAnnotationTitle.setText(Messages.getString("description") + " : ");
		jobAnnotationTitle.setLayoutData(new RowData(120,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_jobAnnotationText = new Text(jobDescriptionComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "annotation", m_jobAnnotationText);
		m_jobAnnotationText.setLayoutData(new RowData(200,
				SizeConstant.SIZE_TEXT_HEIGHT));
		m_jobAnnotationText.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_256));

		// オーナーロールID
		Composite jobRoleIdComposite = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobroleid", jobRoleIdComposite);
		//jobRoleIdComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogRoleIdComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		jobRoleIdComposite.setLayout(rowLayout);
		Label ownerRoleIdTitle = new Label(jobRoleIdComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "ownerroleid", ownerRoleIdTitle);
		ownerRoleIdTitle.setText(Messages.getString("owner.role.id") + " : ");
		ownerRoleIdTitle.setLayoutData(new RowData(120,
				SizeConstant.SIZE_LABEL_HEIGHT));
		// 新規登録、コピー時のみ変更可能
		// 新規登録、コピーの判定はJobInfo.createTimeで行う。
		if (info.getType() == JobConstant.TYPE_JOBUNIT && info.getCreateTime() == null) {
			this.m_ownerRoleId = new RoleIdListComposite(jobRoleIdComposite,
					SWT.NONE, this.m_managerName, true, Mode.OWNER_ROLE);
			this.m_ownerRoleId.getComboRoleId().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					m_messageComposite.getNotifyId().setOwnerRoleId(m_ownerRoleId.getText(), false);
				}
			});
		} else {
			this.m_ownerRoleId = new RoleIdListComposite(jobRoleIdComposite,
					SWT.NONE, this.m_managerName, false, Mode.OWNER_ROLE);
		}
		//this.m_ownerRoleId.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogOwnerRoleIdListComposite");
		this.m_ownerRoleId.setLayoutData(new RowData(200, SizeConstant.SIZE_COMBO_HEIGHT + 5));

		m_tabFolder = new TabFolder(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, m_tabFolder);
		//m_tabFolder.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogTabFolder");

		if (info instanceof JobInfo) {
			if (info.getType() == JobConstant.TYPE_JOBNET) {
				//待ち条件
				m_startComposite = new WaitRuleComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "start", m_startComposite);
				//m_startComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartComposite");
				TabItem tabItem1 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem1", tabItem1);
				//tabItem1.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogWaitRuleTabItem");
				tabItem1.setText(Messages.getString("wait.rule"));
				tabItem1.setControl(m_startComposite);

				//制御
				m_controlComposite = new ControlComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "control", m_controlComposite);
				//m_controlComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlComposite");
				TabItem tabItem2 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem2", tabItem2);
				//tabItem2.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlTabItem");
				tabItem2.setText(Messages.getString("control.job"));
				tabItem2.setControl(m_controlComposite);

				//開始遅延
				m_startDelayComposite = new StartDelayComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "startdelay", m_startDelayComposite);
				//m_startDelayComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartDelayComposite");
				TabItem tabItem3 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem3", tabItem3);
				//tabItem3.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartDelayTabItem");
				tabItem3.setText(Messages.getString("start.delay"));
				tabItem3.setControl(m_startDelayComposite);

				//終了遅延
				m_endDelayComposite = new EndDelayComposite(m_tabFolder, SWT.NONE, false);
				WidgetTestUtil.setTestId(this, "enddelay", m_endDelayComposite);
				//m_endDelayComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndDelayComposite");
				TabItem tabItem4 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem4", tabItem4);
				//tabItem4.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndDelayTabItem");
				tabItem4.setText(Messages.getString("end.delay"));
				tabItem4.setControl(m_endDelayComposite);
			}
			else if (info.getType() == JobConstant.TYPE_JOB) {
				//待ち条件
				m_startComposite = new WaitRuleComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "start", m_startComposite);
				//m_startComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartComposite");
				TabItem tabItem1 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem1", tabItem1);
				//tabItem1.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogWaitRuleTabItem");
				tabItem1.setText(Messages.getString("wait.rule"));
				tabItem1.setControl(m_startComposite);

				//制御(ジョブ)
				m_controlComposite = new ControlComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "control", m_controlComposite);
				//m_controlComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlComposite");
				TabItem tabItem2 = new TabItem(m_tabFolder, SWT.NONE);
				//tabItem2.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlTabItem");
				tabItem2.setText(Messages.getString("control.job"));
				tabItem2.setControl(m_controlComposite);

				//制御(ノード)
				m_controlNodeComposite = new ControlNodeComposite(m_tabFolder, SWT.NONE, false);
				WidgetTestUtil.setTestId(this, "controlnode", m_controlNodeComposite);
				//m_controlNodeComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlNodeComposite");
				TabItem tabItem3 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem3", tabItem3);
				//tabItem3.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlNodeTabItem");
				tabItem3.setText(Messages.getString("control.node"));
				tabItem3.setControl(m_controlNodeComposite);

				//コマンド
				m_executeComposite = new CommandComposite(m_tabFolder, SWT.NONE);
				m_executeComposite.setManagerName(m_managerName);
				WidgetTestUtil.setTestId(this, "execute", m_executeComposite);
				//m_executeComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogExecuteComposite");
				TabItem tabItem4 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem4", tabItem4);
				//tabItem4.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogCommandTabItem");
				tabItem4.setText(Messages.getString("command"));
				tabItem4.setControl(m_executeComposite);

				//開始遅延
				m_startDelayComposite = new StartDelayComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "start", m_startDelayComposite);
				//m_startDelayComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartDelayComposite");
				TabItem tabItem5 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem5", tabItem5);
				//tabItem5.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartDelayTabItem");
				tabItem5.setText(Messages.getString("start.delay"));
				tabItem5.setControl(m_startDelayComposite);

				//終了遅延
				m_endDelayComposite = new EndDelayComposite(m_tabFolder, SWT.NONE, false);
				WidgetTestUtil.setTestId(this, "end", m_endDelayComposite);
				//m_endDelayComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndDelayComposite");
				TabItem tabItem6 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem6", tabItem6);
				//tabItem6.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndDelayTabItem");
				tabItem6.setText(Messages.getString("end.delay"));
				tabItem6.setControl(m_endDelayComposite);

			}
			else if (info.getType() == JobConstant.TYPE_FILEJOB) {
				//待ち条件
				m_startComposite = new WaitRuleComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "start", m_startComposite);
				//m_startComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartComposite");
				TabItem tabItem1 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem1", tabItem1);
				//tabItem1.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogWaitRuleTabItem");
				tabItem1.setText(Messages.getString("wait.rule"));
				tabItem1.setControl(m_startComposite);

				//制御（ジョブ）
				m_controlComposite = new ControlComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "control", m_controlComposite);
				//m_controlComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlComposite");
				TabItem tabItem2 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabItem2", tabItem2);
				//tabItem2.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogControlTabItem");
				tabItem2.setText(Messages.getString("control.job"));
				tabItem2.setControl(m_controlComposite);
				
				//制御(ノード)
				m_controlNodeComposite = new ControlNodeComposite(m_tabFolder, SWT.NONE, true);
				WidgetTestUtil.setTestId(this, "controlnode", m_controlNodeComposite);
				//m_controlNodeComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogMultiplicityComposite");
				TabItem tabItem6 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabItem2", tabItem2);
				//tabItem6.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogMultiplicityTabItem");
				tabItem6.setText(Messages.getString("control.node"));
				tabItem6.setControl(m_controlNodeComposite);

				//ファイル転送
				m_fileComposite = new FileComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "file", m_fileComposite);
				//m_fileComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogFileComposite");
				TabItem tabItem3 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabItem2", tabItem2);
				//tabItem3.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogFileTabItem");
				tabItem3.setText(Messages.getString("forward.file"));
				tabItem3.setControl(m_fileComposite);

				//開始遅延
				m_startDelayComposite = new StartDelayComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "start", m_startDelayComposite);
				//m_startDelayComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartDelayComposite");
				TabItem tabItem4 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabItem2", tabItem2);
				//tabItem4.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartDelayTabItem");
				tabItem4.setText(Messages.getString("start.delay"));
				tabItem4.setControl(m_startDelayComposite);

				//終了遅延
				m_endDelayComposite = new EndDelayComposite(m_tabFolder, SWT.NONE, true);
				WidgetTestUtil.setTestId(this, "end", m_endDelayComposite);
				//m_endDelayComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndDelayComposite");
				TabItem tabItem5 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabItem2", tabItem2);
				//tabItem5.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndDelayTabItem");
				tabItem5.setText(Messages.getString("end.delay"));
				tabItem5.setControl(m_endDelayComposite);
			}
			//参照ジョブ
			else if(info.getType() == JobConstant.TYPE_REFERJOB){
				//待ち条件
				m_startComposite = new WaitRuleComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "start", m_startComposite);
				//m_startComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogStartComposite");
				TabItem tabItem1 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem1", tabItem1);
				//tabItem1.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogWaitRuleTabItem");
				tabItem1.setText(Messages.getString("wait.rule"));
				tabItem1.setControl(m_startComposite);

				//参照
				m_referComposite = new ReferComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "refer", m_referComposite);
				//m_referComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogReferComposite");
				TabItem tabItem2 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabyitem2", tabItem2);
				//tabItem2.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogReferTabItem");
				tabItem2.setText(Messages.getString("refer"));
				tabItem2.setControl(m_referComposite);
			}
			//参照ジョブ以外では使用する
			if (info.getType() != JobConstant.TYPE_REFERJOB) {
				//終了状態
				m_endComposite = new EndStatusComposite(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "end", m_endComposite);
				//m_endComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndComposite");
				TabItem tabItem7 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem7", tabItem7);
				//tabItem7.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogEndStatusTabItem");
				tabItem7.setText(Messages.getString("end.status"));
				tabItem7.setControl(m_endComposite);

				//通知先の指定
				m_messageComposite = new NotificationsComposite(m_tabFolder, SWT.NONE, m_managerName);
				WidgetTestUtil.setTestId(this, "message", m_messageComposite);
				//m_messageComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogNotifyComposite");
				TabItem tabItem8 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem8", tabItem8);
				//tabItem8.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogNotifiyTabItem");
				tabItem8.setText(Messages.getString("notifications"));
				tabItem8.setControl(m_messageComposite);
			}

			if (info.getType() == JobConstant.TYPE_JOBUNIT ) {
				//ジョブパラメータ
				m_parameterComposite = new ParameterComposite(m_tabFolder, SWT.NONE, initFlag);
				WidgetTestUtil.setTestId(this, "parameter", m_parameterComposite);
				//m_parameterComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogParameterComposite");
				TabItem tabItem9 = new TabItem(m_tabFolder, SWT.NONE);
				WidgetTestUtil.setTestId(this, "tabitem9", tabItem9);
				//tabItem9.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "jobDialogParameterTabItem");
				tabItem9.setText(Messages.getString("job.parameter"));
				tabItem9.setControl(m_parameterComposite);
			}
		}

		m_tabFolder.setSelection(0);

		// 画面中央に
		Display display = m_shell.getDisplay();
		m_shell.setLocation(
				(display.getBounds().width - m_shell.getSize().x) / 2, (display
						.getBounds().height - m_shell.getSize().y) / 2);

		//ジョブ情報反映
		reflectJobInfo(info);

		updateWidgets();
	}

	private void updateWidgets() {
		JobInfo info = m_jobTreeItem.getData();
		if (m_jobTreeItem.getParent() == null) {
			// ジョブ履歴の場合はparentがnullになるので、編集モードにできないようにする
			m_editButton.setEnabled(false);
		} else {
			m_editButton.setEnabled(m_readOnly);
		}
		
		if (info.getType() == JobConstant.TYPE_JOBUNIT && info.getCreateTime() != null) {
			// すでにマネージャに登録してあるジョブユニットはIDを変更できない
			m_jobIdText.setEnabled(false);
		} else {
			// それ以外の場合は編集モードの有無で判定する
			m_jobIdText.setEnabled(!m_readOnly);
		}
		
		m_jobNameText.setEnabled(!m_readOnly);
		m_jobAnnotationText.setEnabled(!m_readOnly);
		if (m_startComposite != null)
			m_startComposite.setEnabled(!m_readOnly);
		if (m_controlComposite != null)
			m_controlComposite.setEnabled(!m_readOnly);
		if (m_executeComposite != null)
			m_executeComposite.setEnabled(!m_readOnly);
		if (m_fileComposite != null)
			m_fileComposite.setEnabled(!m_readOnly);
		if (m_startDelayComposite != null)
			m_startDelayComposite.setEnabled(!m_readOnly);
		if (m_endDelayComposite != null)
			m_endDelayComposite.setEnabled(!m_readOnly);
		if (m_controlNodeComposite != null)
			m_controlNodeComposite.setEnabled(!m_readOnly);

		if (info.getType() != JobConstant.TYPE_REFERJOB) {
			m_endComposite.setEnabled(!m_readOnly);
			m_messageComposite.setEnabled(!m_readOnly);
		} else {
			if (m_referComposite != null) {
				m_referComposite.setEnabled(!m_readOnly);
			}
		}

		if (m_parameterComposite != null)
			m_parameterComposite.setEnabled(!m_readOnly);
	}

	/**
	 * 更新処理
	 *
	 */
	public void update(){
		// 必須項目を明示
		if("".equals(this.m_jobIdText.getText())){
			this.m_jobIdText.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_jobIdText.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_jobNameText.getText())){
			this.m_jobNameText.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_jobNameText.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * ＯＫボタンテキスト取得
	 *
	 * @return ＯＫボタンのテキスト
	 * @since 1.0.0
	 */
	@Override
	protected String getOkButtonText() {
		return Messages.getString("ok");
	}

	/**
	 * キャンセルボタンテキスト取得
	 *
	 * @return キャンセルボタンのテキスト
	 * @since 1.0.0
	 */
	@Override
	protected String getCancelButtonText() {
		return Messages.getString("cancel");
	}

	/**
	 * ジョブ情報をダイアログ及び各タブのコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobInfo
	 * @see com.clustercontrol.bean.JobConstant
	 */
	private void reflectJobInfo(JobInfo info) {
		if (info instanceof JobInfo) {
			//ジョブID設定
			String jobId = info.getId();
			if (jobId != null) {
				m_jobIdText.setText(jobId);
			} else {
				m_jobIdText.setText("");
			}

			//ジョブ名設定
			if (info.getName() != null) {
				m_jobNameText.setText(info.getName());
			} else {
				m_jobNameText.setText("");
			}
			//注釈設定
			if (info.getDescription() != null) {
				m_jobAnnotationText.setText(info.getDescription());
			} else {
				m_jobAnnotationText.setText("");
			}

			// オーナーロール取得
			if (info.getOwnerRoleId() != null) {
				this.m_ownerRoleId.setText(info.getOwnerRoleId());
			} else {
				if (info.getType() == JobConstant.TYPE_JOBUNIT) {
					this.m_ownerRoleId.setText(RoleIdConstant.ALL_USERS);
				} else {
					JobTreeItem parentItem = m_jobTreeItem.getParent();
					if (parentItem != null) {
						JobInfo parentInfo = parentItem.getData();
						this.m_ownerRoleId.setText(parentInfo.getOwnerRoleId());
					}
				}
			}

			//参照タブ以外
			if( info.getType() != JobConstant.TYPE_REFERJOB ){
				this.m_messageComposite.getNotifyId().setOwnerRoleId(m_ownerRoleId.getText(), false);
			}

			JobWaitRuleInfo jobWaitRuleInfo = info.getWaitRule();
			if (jobWaitRuleInfo == null) {
				jobWaitRuleInfo = JobTreeItemUtil.getNewJobWaitRuleInfo();
			}

			//タブ内のコンポジットにジョブ情報を反映
			if (info.getType() == JobConstant.TYPE_JOBNET) {
				//開始待ち条件
				m_startComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_startComposite.setJobTreeItem(m_jobTreeItem);
				m_startComposite.reflectWaitRuleInfo();

				//制御
				m_controlComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_controlComposite.getCalendarId().createCalIdCombo(this.m_managerName, this.m_ownerRoleId.getText());
				m_controlComposite.reflectWaitRuleInfo();

				//開始遅延
				m_startDelayComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_startDelayComposite.reflectWaitRuleInfo();

				//終了遅延
				m_endDelayComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_endDelayComposite.reflectWaitRuleInfo();
			}
			else if (info.getType() == JobConstant.TYPE_JOB) {
				//開始待ち条件
				m_startComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_startComposite.setJobTreeItem(m_jobTreeItem);
				m_startComposite.reflectWaitRuleInfo();

				//制御
				m_controlComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_controlComposite.getCalendarId().createCalIdCombo(this.m_managerName, this.m_ownerRoleId.getText());
				m_controlComposite.reflectWaitRuleInfo();

				//実行内容
				m_executeComposite.setCommandInfo(info.getCommand());
				m_executeComposite.setOwnerRoleId(this.m_ownerRoleId.getText());
				m_executeComposite.reflectCommandInfo();

				//開始遅延
				m_startDelayComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_startDelayComposite.reflectWaitRuleInfo();

				//終了遅延
				m_endDelayComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_endDelayComposite.reflectWaitRuleInfo();

				//制御(ノード）
				m_controlNodeComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_controlNodeComposite.setCommandInfo(info.getCommand());
				m_controlNodeComposite.reflectControlNodeInfo();
			}
			else if (info.getType() == JobConstant.TYPE_FILEJOB) {
				//開始待ち条件
				m_startComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_startComposite.setJobTreeItem(m_jobTreeItem);
				m_startComposite.reflectWaitRuleInfo();

				//制御
				m_controlComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_controlComposite.getCalendarId().createCalIdCombo(this.m_managerName, this.m_ownerRoleId.getText());
				m_controlComposite.reflectWaitRuleInfo();

				//ファイル転送
				m_fileComposite.setFileInfo(info.getFile());
				m_fileComposite.setOwnerRoleId(this.m_ownerRoleId.getText());
				m_fileComposite.setManagerName(this.m_managerName);
				m_fileComposite.reflectFileInfo();

				//開始遅延
				m_startDelayComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_startDelayComposite.reflectWaitRuleInfo();

				//終了遅延
				m_endDelayComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_endDelayComposite.reflectWaitRuleInfo();

				//制御(ノード)
				m_controlNodeComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_controlNodeComposite.setFileInfo(info.getFile());
				m_controlNodeComposite.reflectControlNodeInfo();
			}
			//参照ジョブ
			else if(info.getType() == JobConstant.TYPE_REFERJOB){
				//開始待ち条件
				m_startComposite.setWaitRuleInfo(jobWaitRuleInfo);
				m_startComposite.setJobTreeItem(m_jobTreeItem);
				m_startComposite.reflectWaitRuleInfo();
				//参照ジョブ
				m_referComposite.setReferJobUnitId(info.getReferJobUnitId());
				m_referComposite.setReferJobId(info.getReferJobId());
				m_referComposite.setJobTreeItem(m_jobTreeItem);
				m_referComposite.reflectReferInfo();
			}
			//参照タブ以外で使用する
			if (info.getType() != JobConstant.TYPE_REFERJOB) {
				//終了状態の定義
				m_endComposite.setEndInfo(info.getEndStatus());
				m_endComposite.reflectEndInfo();

				//メッセージの指定
				m_messageComposite.setJobInfo(info);
				m_messageComposite.getNotifyId().setOwnerRoleId(this.m_ownerRoleId.getText(), false);
				m_messageComposite.reflectNotificationsInfo();
			}

			if (info.getType() == JobConstant.TYPE_JOBUNIT) {

				//ジョブパラメータ
				m_parameterComposite.setParamInfo(info.getParam());
				m_parameterComposite.reflectParamInfo();
			}
		}
	}

	/**
	 * 入力値チェックをします。
	 *
	 * @return 検証結果
	 *
	 * @see com.clustercontrol.dialog.CommonDialog#validate()
	 */
	@Override
	protected ValidateResult validate() {
		ValidateResult result = null;

		result = createJobInfo();
		if (result != null) {
			return result;
		}

		JobInfo info = m_jobTreeItem.getData();
		if (info instanceof JobInfo) {
			if (info.getType() == JobConstant.TYPE_JOBNET) {
				//開始待ち条件
				result = m_startComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//制御
				result = m_controlComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//開始遅延
				result = m_startDelayComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//終了遅延
				result = m_endDelayComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}
			} else if (info.getType() == JobConstant.TYPE_JOB) {
				//開始待ち条件
				result = m_startComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//制御
				result = m_controlComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//実行内容
				result = m_executeComposite.createCommandInfo();
				if (result != null) {
					return result;
				}

				//開始遅延
				result = m_startDelayComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//終了遅延
				result = m_endDelayComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//多重度
				result = m_controlNodeComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//リトライ情報
				result = m_controlNodeComposite.createCommandInfo();
				if (result != null) {
					return result;
				}
			} else if (info.getType() == JobConstant.TYPE_FILEJOB) {
				//開始待ち条件
				result = m_startComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//制御
				result = m_controlComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//ファイル転送
				result = m_fileComposite.createFileInfo();
				if (result != null) {
					return result;
				}

				//開始遅延
				result = m_startDelayComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//終了遅延
				result = m_endDelayComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}

				//多重度
				result = m_controlNodeComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}
				
				//リトライ回数
				result = m_controlNodeComposite.createFileInfo();
				if (result != null) {
					return result;
				}
			}
			//参照ジョブ
			else if(info.getType() == JobConstant.TYPE_REFERJOB){
				//開始待ち条件
				result = m_startComposite.createWaitRuleInfo();
				if (result != null) {
					return result;
				}
				//参照ジョブ
				result = m_referComposite.createReferInfo();
				if(result != null){
					return result;
				}
			}
			//参照ジョブ以外で使用する
			if(info.getType() != JobConstant.TYPE_REFERJOB){
				//終了状態の定義
				result = m_endComposite.createEndInfo();
				if (result != null) {
					return result;
				}

				//メッセージの指定
				result = m_messageComposite.createNotificationsInfo();
				if (result != null) {
					return result;
				}
			}

			if (info.getType() == JobConstant.TYPE_JOBUNIT) {
				//ジョブパラメータ
				result = m_parameterComposite.createParamInfo();
				if (result != null) {
					return result;
				}
			}

			if (m_startComposite != null)
				info.setWaitRule(
						m_startComposite.getWaitRuleInfo());
			if (m_controlComposite != null)
				info.setWaitRule(
						m_controlComposite.getWaitRuleInfo());
			if (m_executeComposite != null)
				info.setCommand(
						m_executeComposite.getCommandInfo());
			if (m_fileComposite != null)
				info.setFile(
						m_fileComposite.getFileInfo());
			if (m_endComposite != null) {
				List<JobEndStatusInfo> jobEndStatusInfoList = info.getEndStatus();
				jobEndStatusInfoList.clear();
				if (m_endComposite.getEndInfo() != null) {
					jobEndStatusInfoList.addAll(m_endComposite.getEndInfo());
				}
			} if (m_startDelayComposite != null)
				info.setWaitRule(
						m_startDelayComposite.getWaitRuleInfo());
			if (m_endDelayComposite != null)
				info.setWaitRule(
						m_endDelayComposite.getWaitRuleInfo());
			if (m_controlNodeComposite != null) {
				info.setWaitRule(
						m_controlNodeComposite.getWaitRuleInfo());
				//リトライ情報のセット
				if (m_controlNodeComposite.getCommandInfo() != null) {
					info.getCommand().setMessageRetryEndFlg(m_controlNodeComposite.getCommandInfo().getMessageRetryEndFlg());
					info.getCommand().setMessageRetryEndValue(m_controlNodeComposite.getCommandInfo().getMessageRetryEndValue());
					info.getCommand().setMessageRetry(m_controlNodeComposite.getCommandInfo().getMessageRetry());
					info.getCommand().setCommandRetryFlg(m_controlNodeComposite.getCommandInfo().getCommandRetryFlg());
					info.getCommand().setCommandRetry(m_controlNodeComposite.getCommandInfo().getCommandRetry());
				}
				if (m_controlNodeComposite.getFileInfo() != null) {
					info.getFile().setMessageRetryEndFlg(m_controlNodeComposite.getFileInfo().getMessageRetryEndFlg());
					info.getFile().setMessageRetryEndValue(m_controlNodeComposite.getFileInfo().getMessageRetryEndValue());
					info.getFile().setMessageRetry(m_controlNodeComposite.getFileInfo().getMessageRetry());
					info.getFile().setCommandRetryFlg(m_controlNodeComposite.getFileInfo().getCommandRetryFlg());
					info.getFile().setCommandRetry(m_controlNodeComposite.getFileInfo().getCommandRetry());
				}
			}
			if (m_messageComposite != null){
				JobInfo messageJobInfo = m_messageComposite.getJobInfo();
				info.setBeginPriority(messageJobInfo.getBeginPriority());
				info.setNormalPriority(messageJobInfo.getNormalPriority());
				info.setWarnPriority(messageJobInfo.getWarnPriority());
				info.setAbnormalPriority(messageJobInfo.getAbnormalPriority());

				if (messageJobInfo.getNotifyRelationInfos() != null) {
					info.getNotifyRelationInfos().clear();
					info.getNotifyRelationInfos().addAll(messageJobInfo.getNotifyRelationInfos());
				}
			}

			if (m_parameterComposite != null){
				List<JobParameterInfo> jobParameterInfoinfoList = info.getParam();
				jobParameterInfoinfoList.clear();
				if (m_parameterComposite.getParamInfo() != null) {
					jobParameterInfoinfoList.addAll(m_parameterComposite.getParamInfo());
				}
			}

			//参照ジョブ
			if(m_referComposite != null){
				if(m_referComposite.getReferJobUnitId() != null){
					info.setReferJobUnitId(m_referComposite.getReferJobUnitId());
				}
				if(m_referComposite.getReferJobId() != null){
					info.setReferJobId(m_referComposite.getReferJobId());
				}
			}

			info.setPropertyFull(true);
		}

		return null;
	}

	/**
	 * ダイアログの情報から、ジョブ情報を作成します。
	 *
	 * @return 入力値の検証結果
	 */
	private ValidateResult createJobInfo() {
		ValidateResult result = null;

		JobInfo info = m_jobTreeItem.getData();
		String oldJobId = info.getId();
		String oldJobunitId;

		// ジョブユニットIDの重複チェック(ジョブを編集しているときだけチェックする）
		if (!m_readOnly && info.getType() == JobConstant.TYPE_JOBUNIT) {
			// ジョブユニットIDの重複チェック
			oldJobunitId = info.getJobunitId();
			info.setJobunitId(m_jobIdText.getText());
			try {
				JobUtil.findDuplicateJobunitId(m_jobTreeItem.getParent().getParent());
			} catch (JobInvalid e) {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				String[] args1 = { m_jobIdText.getText() };
				result.setMessage(Messages.getString("message.job.64", args1));
				return result;
			} finally {
				info.setJobunitId(oldJobunitId);
			}
			// ジョブユニットIDの文字制約のチェック
			if(!m_jobIdText.getText().matches("^[A-Za-z0-9-_]+$")){
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				String[] args1 = { m_jobIdText.getText(), Messages.getString("job.id")};
				result.setMessage(Messages.getString("message.common.6", args1));

				info.setJobunitId(oldJobunitId);
				return result;
			}

			JobEditState JobEditState = JobEditStateUtil.getJobEditState( m_managerName );
			if( JobEditState.getEditSession(m_jobTreeItem.getData()) == null ){
				// 新規ジョブユニット作成の場合
				Integer editSession = null;

				try {
					editSession =JobUtil.getEditLock(m_managerName, m_jobIdText.getText(), null, false);
				} catch (OtherUserGetLock_Exception e) {
					// 他のユーザがロックを取得している
					String message = e.getMessage();
					if (MessageDialog.openQuestion(
							null,
							Messages.getString("confirmed"),
							message)) {
						try {
							editSession = JobUtil.getEditLock(m_managerName, m_jobIdText.getText(), null, true);
						} catch (Exception e1) {
							// ここには絶対にこないはず
							m_log.error("run() : logical error");
						}
					}
				}
				if (editSession == null) {
					result = new ValidateResult();
					result.setValid(false);
					result.setID(Messages.getString("message.hinemos.1"));
					String[] args1 = { m_jobIdText.getText() };
					result.setMessage(Messages.getString("message.job.105", args1));
					return result;
				}
				JobEditState.addLockedJobunit(info, null, editSession);
			} else if (!m_jobIdText.getText().equals(oldJobunitId)) {
				// ジョブユニットID変更の場合
				Integer oldEditSession = JobEditState.getEditSession(info);
				Integer editSession = null;
				try {
					editSession =JobUtil.getEditLock(m_managerName, m_jobIdText.getText(), null, false);
				} catch (OtherUserGetLock_Exception e) {
					// 他のユーザがロックを取得している
					String message = e.getMessage();
					if (MessageDialog.openQuestion(
							null,
							Messages.getString("confirmed"),
							message)) {
						try {
							editSession = JobUtil.getEditLock(m_managerName, m_jobIdText.getText(), null, true);
						} catch (Exception e1) {
							// ここには絶対にこないはず
							m_log.error("run() : logical error");
						}
					}
				}
				if (editSession == null) {
					result = new ValidateResult();
					result.setValid(false);
					result.setID(Messages.getString("message.hinemos.1"));
					String[] args1 = { m_jobIdText.getText() };
					result.setMessage(Messages.getString("message.job.105", args1));
					return result;
				}
				JobEditState.addLockedJobunit(info, null, editSession);
				try {
					JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(m_managerName);
					wrapper.releaseEditLock(oldEditSession);
				} catch (Exception e) {
					result = new ValidateResult();
					result.setValid(false);
					result.setID(Messages.getString("message.hinemos.1"));
					String[] args1 = { m_jobIdText.getText() };
					result.setMessage(Messages.getString("message.job.105", args1));
					return result;
				}
			}
		}

		//ジョブID取得
		if (m_jobIdText.getText().length() > 0) {
			String oldId = info.getId();
			info.setId("");
			//ジョブIDの重複チェック（所属するジョブユニット配下のみ）
			JobTreeItem unit = JobUtil.getTopJobUnitTreeItem(m_jobTreeItem);
			if(unit != null && JobUtil.findJobId(m_jobIdText.getText(), unit)){
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				String[] args1 = { m_jobIdText.getText() };
				result.setMessage(Messages.getString("message.job.42", args1));

				info.setId(oldId);
				return result;
			}
			// ジョブIDの文字制約のチェック
			if(!m_jobIdText.getText().matches("^[A-Za-z0-9-_]+$")){
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				String[] args1 = { m_jobIdText.getText(), Messages.getString("job.id")};
				result.setMessage(Messages.getString("message.common.6", args1));

				info.setId(oldId);
				return result;
			}
			info.setId(m_jobIdText.getText());

			// ジョブユニットの場合はジョブユニットIDをセットする。
			if (info.getType() == JobConstant.TYPE_JOBUNIT) {
				info.setJobunitId(m_jobIdText.getText());
			}


		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.22"));
			return result;
		}

		//ジョブ名取得
		if (m_jobNameText.getText().length() > 0) {
			info.setName(m_jobNameText.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.23"));
			return result;
		}

		//注釈取得
		if (m_jobAnnotationText.getText().length() > 0) {
			info.setDescription(m_jobAnnotationText.getText());
		} else {
			info.setDescription("");
		}

		//オーナーロールID取得
		String newOwnerRoleId = m_ownerRoleId.getText();
		if (newOwnerRoleId.length() > 0) {
			if (!newOwnerRoleId.equals(info.getOwnerRoleId())) {
				changeOwnerRoleId(m_jobTreeItem, newOwnerRoleId);
			}
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("owner.role.id"));
			return result;
		}

		//このジョブを参照するほかのジョブの待ち条件を更新
		if (!oldJobId.equals(info.getId()) && info.getType() != JobConstant.TYPE_JOBUNIT) {
			List<JobTreeItem> siblings = m_jobTreeItem.getParent().getChildren();
			for (JobTreeItem sibling : siblings) {
				if (sibling == m_jobTreeItem) {
					continue;
				}

				JobInfo siblingJobInfo = sibling.getData();
				if (siblingJobInfo.getWaitRule() == null) {
					continue;
				}
				
				for (JobObjectInfo siblingWaitJobObjectInfo : siblingJobInfo.getWaitRule().getObject()) {
					if (oldJobId.equals(siblingWaitJobObjectInfo.getJobId())) {
						siblingWaitJobObjectInfo.setJobId(info.getId());
					}
				}
			}
		}
		
		//この参照ジョブのジョブIDを更新
		if (!oldJobId.equals(info.getId()) && info.getType() != JobConstant.TYPE_JOBUNIT) {
			//所属するjobunitを探す
			JobTreeItem treeItem = m_jobTreeItem;
			while (treeItem.getData().getType() != JobConstant.TYPE_JOBUNIT) {
				treeItem = treeItem.getParent();
			}
			
			//すべての参照ジョブに対して、ループさせる
			updateReferJob(treeItem, oldJobId, info.getId());
		}
		
		return null;
	}

	private void updateReferJob(JobTreeItem treeItem, String oldJobId, String newJobId) {
		JobInfo info = treeItem.getData();
		if (info.getType() == JobConstant.TYPE_REFERJOB) {
			if (oldJobId.equals(info.getReferJobId())) {
				info.setReferJobId(newJobId);
			}
		}
		
		for (JobTreeItem childTreeItem : treeItem.getChildren()) {
			updateReferJob(childTreeItem, oldJobId, newJobId);
		}
	}

	/**
	 * オーナーロールIDを変更する。<BR>
	 *
	 * @param item 反映するJobTreeItem
	 * @param ownerRoleId オーナーロールID
	 */
	private static void changeOwnerRoleId(JobTreeItem item, String ownerRoleId) {

		// 直下のJobTreeItemのOwnerRoleIdを変更する
		List<JobTreeItem> children = item.getChildren();
		if (children != null && children.size() > 0) {
			Iterator<JobTreeItem> iter = children.iterator();
			while(iter.hasNext()) {
				JobTreeItem child = iter.next();
				changeOwnerRoleId(child, ownerRoleId);
			}
		}
		JobInfo info = item.getData();
		info.setOwnerRoleId(ownerRoleId);

		m_log.debug("changeOwnerRoleId() "
				+ " jobunitId = " + info.getJobunitId()
				+ " jobId = " + info.getId()
				+ " ownerRoleId = " + info.getOwnerRoleId());
	}

	/**
	 * ジョブツリーアイテムを返します。
	 *
	 * @return ジョブツリーアイテム
	 */
	public JobTreeItem getJobTreeItem() {
		return m_jobTreeItem;
	}

	/**
	 * ジョブツリーアイテムを設定します。
	 *
	 * @param jobTreeItem ジョブツリーアイテム
	 */
	public void setJobTreeItem(JobTreeItem jobTreeItem) {
		this.m_jobTreeItem = jobTreeItem;
	}
}
