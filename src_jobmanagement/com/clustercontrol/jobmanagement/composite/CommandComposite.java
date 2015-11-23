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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.composite.action.StringVerifyListener;
import com.clustercontrol.dialog.ScopeTreeDialog;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.bean.CommandStopTypeConstant;
import com.clustercontrol.jobmanagement.bean.ProcessingMethodConstant;
import com.clustercontrol.jobmanagement.bean.SystemParameterConstant;
import com.clustercontrol.repository.FacilityPath;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobCommandInfo;
import com.clustercontrol.ws.repository.FacilityInfo;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * コマンドタブ用のコンポジットクラスです。
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandComposite extends Composite {
	/** スコープ用テキスト */
	private Text m_scope = null;
	/** 起動コマンド用テキスト */
	private Text m_startCommand = null;
	/** 停止コマンド用ラジオボタン */
	private Button m_executeStopCommand;
	/** 停止コマンド用テキスト */
	private Text m_stopCommand = null;
	/** プロセス終了用ラジオボタン */
	private Button m_destroyProcess;
	/** エージェントと同じユーザ用ラジオボタン */
	private Button m_agentUser = null;
	/** ユーザを指定する用ラジオボタン */
	private Button m_specifyUser = null;
	/** 実効ユーザ用テキスト */
	private Text m_user = null;
	/** ジョブ変数用ラジオボタン */
	private Button m_scopeJobParam = null;
	/** 固定値用ラジオボタン */
	private Button m_scopeFixedValue = null;
	/** スコープ参照用ボタン */
	private Button m_scopeSelect = null;
	/** 全てのノードで実行用ラジオボタン */
	private Button m_allNode = null;
	/** 正常終了するまでノードを順次リトライ用ラジオボタン */
	private Button m_retry = null;
	/** ファシリティID */
	private String m_facilityId = null;
	/** スコープ */
	private String m_facilityPath = null;
	/** ジョブコマンド情報 */
	private JobCommandInfo m_execute = null;
	/** シェル */
	private Shell m_shell = null;
	/** オーナーロールID */
	private String m_ownerRoleId = null;
	/** マネージャ名 */
	private String m_managerName = null;

	/**
	 * コンストラクタ
	 *
	 * @param parent 親コンポジット
	 * @param style スタイル
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public CommandComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		m_shell = this.getShell();
	}

	/**
	 * コンポジットを配置します。
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
		Label label = null;

		// スコープ
		Group cmdScopeGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scope", cmdScopeGroup);
		cmdScopeGroup.setText(Messages.getString("scope"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		cmdScopeGroup.setLayout(rowLayout);

		Composite commandComposite1 = new Composite(cmdScopeGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "command1", commandComposite1);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite1.setLayout(rowLayout);
		m_scopeJobParam = new Button(commandComposite1, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "scopejob", m_scopeJobParam);
		m_scopeJobParam.setText(Messages.getString("job.parameter") + " : ");
		m_scopeJobParam.setLayoutData(
				new RowData(120, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_scopeJobParam.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_scopeFixedValue.setSelection(false);
					m_scopeSelect.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		label = new Label(commandComposite1, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "commandcomposite1", label);
		label.setText(
				SystemParameterConstant.getParamText(SystemParameterConstant.FACILITY_ID));
		label.setLayoutData(
				new RowData(100, SizeConstant.SIZE_LABEL_HEIGHT));

		Composite commandComposite2 = new Composite(cmdScopeGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "commandcomposite2", commandComposite2);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite2.setLayout(rowLayout);

		m_scopeFixedValue = new Button(commandComposite2, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "scopefieldvalue", m_scopeFixedValue);
		m_scopeFixedValue.setText(Messages.getString("fixed.value") + " : ");
		m_scopeFixedValue.setLayoutData(new RowData(120,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_scopeFixedValue.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_scopeJobParam.setSelection(false);
					m_scopeSelect.setEnabled(true);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		this.m_scope = new Text(commandComposite2, SWT.BORDER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "scope", m_scope);
		this.m_scope.setLayoutData(new RowData(200, SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_scope.setText("");
		this.m_scope.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		m_scopeSelect = new Button(commandComposite2, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scopeselect", m_scopeSelect);
		m_scopeSelect.setText(Messages.getString("refer"));
		m_scopeSelect.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_scopeSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScopeTreeDialog dialog = new ScopeTreeDialog(m_shell, m_managerName, m_ownerRoleId);
				if (dialog.open() == IDialogConstants.OK_ID) {
					FacilityTreeItem selectItem = dialog.getSelectItem();
					FacilityInfo info = selectItem.getData();
					FacilityPath path = new FacilityPath(
							ClusterControlPlugin.getDefault()
							.getSeparator());
					m_facilityPath = path.getPath(selectItem);
					m_facilityId = info.getFacilityId();
					m_scope.setText(m_facilityPath);
					update();
				}
			}
		});

		// スコープ処理
		Group cmdScopeProcGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scopeproc", cmdScopeProcGroup);
		cmdScopeProcGroup.setText(Messages.getString("scope.process"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		cmdScopeProcGroup.setLayout(rowLayout);

		m_allNode = new Button(cmdScopeProcGroup, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "allnode", m_allNode);
		m_allNode.setText(Messages.getString("scope.process.all.nodes"));
		m_allNode.setLayoutData(
				new RowData(200,SizeConstant.SIZE_BUTTON_HEIGHT));
		m_retry = new Button(cmdScopeProcGroup, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "retry", m_retry);
		m_retry.setText(Messages.getString("scope.process.retry.nodes"));
		m_retry.setLayoutData(
				new RowData(300, SizeConstant.SIZE_BUTTON_HEIGHT));

		Composite commandComposite3 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "start", commandComposite3);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite3.setLayout(rowLayout);

		label = new Label(commandComposite3, SWT.NONE);
		WidgetTestUtil.setTestId(this, "startcommand", label);
		label.setText(Messages.getString("start.command") + " : ");
		label.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));

		// 起動コマンド
		this.m_startCommand = new Text(commandComposite3, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "start", m_startCommand);
		this.m_startCommand.setLayoutData(new RowData(400,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_startCommand.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_1024));
		this.m_startCommand.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		// 停止
		Group cmdStopGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "stop", cmdStopGroup);
		cmdStopGroup.setText(Messages.getString("stop"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		cmdStopGroup.setLayout(rowLayout);

		Composite commandComposite4 = new Composite(cmdStopGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "destroyprocess", commandComposite4);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite4.setLayout(rowLayout);

		m_destroyProcess = new Button(commandComposite4, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "destroyprocess", m_destroyProcess);
		m_destroyProcess.setText(Messages.getString("shutdown.process"));
		m_destroyProcess.setLayoutData(
				new RowData(190, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_destroyProcess.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_executeStopCommand.setSelection(false);
					m_stopCommand.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Composite commandComposite5 = new Composite(cmdStopGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "stop", commandComposite5);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite5.setLayout(rowLayout);

		m_executeStopCommand = new Button(commandComposite5, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "executestop", m_executeStopCommand);
		m_executeStopCommand.setText(Messages.getString("stop.command"));
		m_executeStopCommand.setLayoutData(
				new RowData(120,SizeConstant.SIZE_BUTTON_HEIGHT));
		m_executeStopCommand.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_destroyProcess.setSelection(false);
					m_stopCommand.setEnabled(true);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		this.m_stopCommand = new Text(commandComposite5, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "stop", m_stopCommand);
		this.m_stopCommand.setLayoutData(new RowData(160,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_stopCommand.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_1024));
		this.m_stopCommand.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		// 実効ユーザ
		Group cmdExeUserGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "cmdexeuser", cmdExeUserGroup);
		cmdExeUserGroup.setText(Messages.getString("effective.user"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		cmdExeUserGroup.setLayout(rowLayout);

		Composite commandComposite6 = new Composite(cmdExeUserGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "agentuser", commandComposite6);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite6.setLayout(rowLayout);

		m_agentUser = new Button(commandComposite6, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "agentuser", m_agentUser);
		m_agentUser.setText(Messages.getString("agent.user"));
		m_agentUser.setLayoutData(
				new RowData(190, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_agentUser.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_specifyUser.setSelection(false);
					m_user.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		Composite commandComposite7 = new Composite(cmdExeUserGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "specifyuser", commandComposite7);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite7.setLayout(rowLayout);

		m_specifyUser = new Button(commandComposite7, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "specifyuser", m_specifyUser);
		m_specifyUser.setText(Messages.getString("specified.user"));
		m_specifyUser.setLayoutData(
				new RowData(120, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_specifyUser.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_agentUser.setSelection(false);
					m_user.setEnabled(true);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		this.m_user = new Text(commandComposite7, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "user", m_user);
		this.m_user.setLayoutData(new RowData(160, SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_user.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		this.m_user.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		//エラー時終了
		Composite commandComposite8 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "errorconfition", commandComposite8);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		commandComposite8.setLayout(rowLayout);

	}

	/**
	 * 更新処理
	 *
	 */
	@Override
	public void update(){
		// 必須項目を明示
		if(m_scopeFixedValue.getSelection() && "".equals(this.m_scope.getText())){
			this.m_scope.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_scope.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_startCommand.getText())){
			this.m_startCommand.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_startCommand.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(m_executeStopCommand.getSelection() && "".equals(this.m_stopCommand.getText())){
			this.m_stopCommand.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_stopCommand.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(m_specifyUser.getSelection() && "".equals(this.m_user.getText())){
			this.m_user.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_user.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * ジョブコマンド情報をコンポジットに反映する。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobCommandInfo
	 */
	public void reflectCommandInfo() {

		// 初期値
		m_scopeJobParam.setSelection(false);
		m_scopeFixedValue.setSelection(true);
		m_scope.setText("");
		m_allNode.setSelection(true);
		m_startCommand.setText("");
		m_executeStopCommand.setSelection(false);
		m_destroyProcess.setSelection(true);
		m_stopCommand.setText("");
		m_stopCommand.setEnabled(false);
		m_agentUser.setSelection(true);
		m_specifyUser.setSelection(false);
		m_user.setText("");
		m_user.setEnabled(false);

		if (m_execute != null) {
			//スコープ設定
			m_facilityPath = m_execute.getScope();
			m_facilityId = m_execute.getFacilityID();
			if(SystemParameterConstant.isParam(
					m_facilityId,
					SystemParameterConstant.FACILITY_ID)){
				//ファシリティIDがジョブ変数の場合
				m_facilityId = "";
				m_facilityPath = "";
				m_scope.setText(m_facilityPath);
				m_scopeJobParam.setSelection(true);
				m_scopeFixedValue.setSelection(false);
			}
			else{
				if (m_facilityPath != null && m_facilityPath.length() > 0) {
					m_scope.setText(m_facilityPath);
				}
				m_scopeJobParam.setSelection(false);
				m_scopeFixedValue.setSelection(true);
			}
			//処理方法設定
			if (m_execute.getProcessingMethod() == ProcessingMethodConstant.TYPE_ALL_NODE) {
				m_allNode.setSelection(true);
				m_retry.setSelection(false);
			} else {
				m_allNode.setSelection(false);
				m_retry.setSelection(true);
			}
			//起動コマンド設定
			if (m_execute.getStartCommand() != null
					&& m_execute.getStartCommand().length() > 0) {
				m_startCommand.setText(m_execute.getStartCommand());
			}
			//停止コマンド設定
			if (m_execute.getStopType() == CommandStopTypeConstant.DESTROY_PROCESS) {
				m_destroyProcess.setSelection(true);
				m_executeStopCommand.setSelection(false);
			} else {
				m_destroyProcess.setSelection(false);
				m_executeStopCommand.setSelection(true);
				m_stopCommand.setEnabled(true);
			}
			if (m_execute.getStopCommand() != null
					&& m_execute.getStopCommand().length() > 0) {
				m_stopCommand.setText(m_execute.getStopCommand());
			}
			//ユーザー設定
			if (m_execute.getSpecifyUser() == YesNoConstant.TYPE_YES) {
				m_specifyUser.setSelection(true);
				m_agentUser.setSelection(false);
				m_user.setEnabled(true);
			} else {
				m_specifyUser.setSelection(false);
				m_agentUser.setSelection(true);
				m_user.setEnabled(false);
			}
			if (m_execute.getUser() != null && m_execute.getUser().length() > 0) {
				m_user.setText(m_execute.getUser());
			}

		}

		//スコープ
		if (m_scopeJobParam.getSelection()) {
			m_scopeSelect.setEnabled(false);
		} else {
			m_scopeSelect.setEnabled(true);
		}
	}

	/**
	 * ジョブコマンド情報を設定する。
	 *
	 * @param execute ジョブコマンド情報
	 */
	public void setCommandInfo(JobCommandInfo execute) {
		m_execute = execute;
	}

	/**
	 * ジョブコマンド情報を返す。
	 *
	 * @return ジョブコマンド情報
	 */
	public JobCommandInfo getCommandInfo() {
		return m_execute;
	}

	/**
	 * コンポジットの情報から、ジョブコマンド情報を作成する。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobCommandInfo
	 */
	public ValidateResult createCommandInfo() {
		ValidateResult result = null;

		//実行内容情報クラスのインスタンスを作成・取得
		m_execute = new JobCommandInfo();

		//スコープ取得
		if(m_scopeJobParam.getSelection()){
			//ジョブ変数の場合
			m_execute.setFacilityID(
					SystemParameterConstant.getParamText(SystemParameterConstant.FACILITY_ID));
			m_execute.setScope("");
		}
		else{
			//固定値の場合
			if (m_facilityId != null && m_facilityId.length() > 0){
				m_execute.setFacilityID(m_facilityId);
				m_execute.setScope(m_facilityPath);
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.hinemos.3"));
				return result;
			}
		}

		//処理方法取得
		if (m_allNode.getSelection()) {
			m_execute
			.setProcessingMethod(ProcessingMethodConstant.TYPE_ALL_NODE);
		} else {
			m_execute.setProcessingMethod(ProcessingMethodConstant.TYPE_RETRY);
		}

		//起動コマンド取得
		if (m_startCommand.getText().length() > 0) {
			m_execute.setStartCommand(m_startCommand.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.3"));
			return result;
		}

		//停止コマンド取得
		if (m_destroyProcess.getSelection()) {
			m_execute.setStopType(CommandStopTypeConstant.DESTROY_PROCESS);
		} else {
			if (m_stopCommand.getText().length() > 0) {
				m_execute.setStopType(CommandStopTypeConstant.EXECUTE_COMMAND);
				m_execute.setStopCommand(m_stopCommand.getText());
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.4"));
				return result;
			}
		}

		//ユーザー取得
		if (m_agentUser.getSelection()) {
			m_execute.setSpecifyUser(YesNoConstant.TYPE_NO);
		} else {
			if (m_user.getText().length() > 0) {
				m_execute.setSpecifyUser(YesNoConstant.TYPE_YES);
				m_execute.setUser(m_user.getText());
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.5"));
				return result;
			}
		}

		return null;
	}

	public void setOwnerRoleId(String ownerRoleId) {
		this.m_ownerRoleId = ownerRoleId;
		this.m_scope.setText("");
		this.m_facilityId = null;
	}

	/**
	 * @return the m_managerName
	 */
	public String getManagerName() {
		return m_managerName;
	}

	/**
	 * @param m_managerName the m_managerName to set
	 */
	public void setManagerName(String m_managerName) {
		this.m_managerName = m_managerName;
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_scope.setEnabled(enabled);
		m_startCommand.setEnabled(enabled);
		m_executeStopCommand.setEnabled(enabled);
		m_stopCommand.setEditable(enabled);
		m_destroyProcess.setEnabled(enabled);
		m_agentUser.setEnabled(enabled);
		m_specifyUser.setEnabled(enabled);
		m_user.setEnabled(enabled);
		m_scopeJobParam.setEnabled(enabled);
		m_scopeFixedValue.setEnabled(enabled);
		m_scopeSelect.setEnabled(enabled);
		m_allNode.setEnabled(enabled);
		m_retry.setEnabled(enabled);
	}
}
