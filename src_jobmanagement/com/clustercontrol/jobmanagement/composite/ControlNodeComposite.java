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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.EndStatusConstant;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.bean.StatusConstant;
import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.composite.action.NumberVerifyListener;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobCommandInfo;
import com.clustercontrol.ws.jobmanagement.JobFileInfo;
import com.clustercontrol.ws.jobmanagement.JobWaitRuleInfo;

/**
 *制御(ノード)タブ用のコンポジットクラスです。
 *
 * @version 2.1.0
 * @since 2.1.0
 */
public class ControlNodeComposite extends Composite {

	/** 待機用ラジオボタン */
	private Button m_waitCondition = null;
	/** 停止用ラジオボタン */
	private Button m_endCondition = null;
	/** 停止時の終了値用テキスト */
	private Text m_endValue = null;

	/**  通知用チェックボタン */
	private Button m_notifyCondition = null;
	/**  通知重要度用コンボボックス */
	private Combo m_notifyPriority = null;

	/** ジョブ待ち条件情報 */
	private JobWaitRuleInfo m_waitRule = null;
	/** ジョブコマンド情報 */
	private JobCommandInfo m_jobCommand = null;
	/** ジョブファイル転送情報 */
	private JobFileInfo m_jobFile = null;

	private Composite topComposite = null;

	/** コマンド実行失敗時に終了する用チェックボタン */
	private Button m_messageRetryEndCondition = null;
	/** 終了値用テキスト */
	private Text m_messageRetryEndValue = null;
	/** コマンド実行失敗時に終了する用グループ */
	private Group m_messageRetryEndConditionGroup = null;
	/** リトライ回数用テキスト */
	private Text m_messageRetry;

	/** ジョブが正常終了するまで繰り返す用チェックボタン */
	private Button m_commandRetryCondition = null;
	/** ジョブが正常終了するまで繰り返す用グループ */
	private Group m_commandRetryConditionGroup = null;
	/** エラーリトライ回数用テキスト */
	private Text m_commandRetry;
	
	private boolean m_isFileJob = false;

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
	public ControlNodeComposite(Composite parent, int style, boolean isFileJob) {
		super(parent, style);
		initialize(isFileJob);
	}

	/**
	 * コンポジットを配置します。
	 */
	private void initialize(boolean isFileJob) {
		m_isFileJob = isFileJob;
		
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 1;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.fill = true;
		this.setLayout(layout);

		Composite composite = null;
		RowLayout rowLayout = null;

		topComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "top", topComposite);
		//topComposite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeTopComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		topComposite.setLayout(rowLayout);

		//多重度が上限に達した時の挙動
		Group group = new Group(topComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, group);
		//group.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeGroup");
		group.setText(Messages.getString("job.multiplicity.action"));
		group.setLayout(rowLayout);

		//通知
		composite = new Composite(group, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);
		//composite.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeNotifyComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		composite.setLayout(rowLayout);
		m_notifyCondition = new Button(composite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, null, m_notifyCondition);
		//m_notifyCondition.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeNotifyConditionCheck");
		m_notifyCondition.setText(Messages.getString("notify") + " : ");
		m_notifyCondition.setLayoutData(new RowData(150, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_notifyCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_notifyPriority.setEnabled(true);
				} else {
					m_notifyPriority.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		//通知の重要度
		m_notifyPriority = new Combo(composite, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "notifypriority", m_notifyPriority);
		//m_notifyPriority.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeNotifyPriorityCombo");
		m_notifyPriority.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_notifyPriority.add(PriorityConstant.STRING_INFO);
		m_notifyPriority.add(PriorityConstant.STRING_WARNING);
		m_notifyPriority.add(PriorityConstant.STRING_CRITICAL);
		m_notifyPriority.add(PriorityConstant.STRING_UNKNOWN);

		//判定対象一覧
		Group actionGroup = new Group(group, SWT.NONE);
		//actionGroup.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeActionGroup");
		actionGroup.setText(Messages.getString("operations"));
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 10;
		gridLayout.marginHeight = 10;
		gridLayout.numColumns = 5;
		actionGroup.setLayout(gridLayout);

		// grid
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalSpan = 1;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.horizontalSpan = 4;

		// 待機
		m_waitCondition = new Button(actionGroup, SWT.RADIO);
		//m_waitCondition.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeWaitConditionRadio");
		m_waitCondition.setText(Messages.getString("wait"));
		m_waitCondition.setLayoutData(gridData1);
		m_waitCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setMultiplicityOperation(StatusConstant.TYPE_WAIT);
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		Label spaceLabel = null;
		spaceLabel = new Label(actionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "spacelabel", spaceLabel);
		spaceLabel.setLayoutData(gridData2);

		// 終了
		m_endCondition = new Button(actionGroup, SWT.RADIO);
		//m_endCondition.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeEndConditionRadio");
		m_endCondition.setText(Messages.getString("end"));
		m_endCondition.setLayoutData(gridData1);
		m_endCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setMultiplicityOperation(StatusConstant.TYPE_END);
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});

		Group endGroup = new Group(actionGroup, SWT.LEFT);
		//endGroup.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "controlNodeCompositeEndGroup");
		endGroup.setLayoutData(gridData2);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 3;
		rowLayout.spacing = 1;
		endGroup.setLayout(rowLayout);
		Label endLabel = new Label(endGroup, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "endvalue", endLabel);
		endLabel.setText(Messages.getString("end.value") + " : ");
		endLabel.setLayoutData(new RowData(80, SizeConstant.SIZE_LABEL_HEIGHT));
		m_endValue = new Text(endGroup, SWT.BORDER);
		WidgetTestUtil.setTestId(this, null, m_endValue);
		m_endValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		m_endValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_endValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);

		m_messageRetryEndCondition = new Button(topComposite, SWT.CHECK);
		//m_errorCondition.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "commandCompositeErrorConditionCheck");
		m_messageRetryEndCondition.setText(Messages.getString("command.error.ended"));
		m_messageRetryEndCondition.setLayoutData(new RowData(300,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_messageRetryEndCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					if (!m_isFileJob) {
						m_messageRetryEndValue.setEnabled(true);
					}
				} else {
					m_messageRetryEndValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		m_messageRetryEndConditionGroup = new Group(this, SWT.NONE);
		//m_errorEndConditionGroup.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "commandCompositeErrorEndConditionGroup");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		rowLayout.spacing = 10;
		m_messageRetryEndConditionGroup.setLayout(rowLayout);

		Composite commandComposite9 = new Composite(m_messageRetryEndConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "command9", commandComposite9);
		//commandComposite9.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "commandCompositeRetryComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite9.setLayout(rowLayout);
		Label messageRetryTitle = new Label(commandComposite9, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "jobretries", messageRetryTitle);
		messageRetryTitle.setText(Messages.getString("job.retries") + " : ");
		messageRetryTitle.setLayoutData(new RowData(90,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_messageRetry= new Text(commandComposite9, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "messageretry", m_messageRetry);
		m_messageRetry.setLayoutData(new RowData(100, SizeConstant.SIZE_TEXT_HEIGHT));
		m_messageRetry.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_messageRetry.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);

		Composite commandComposite10 = new Composite(m_messageRetryEndConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "errorendvalue", commandComposite10);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite10.setLayout(rowLayout);
		Label skipEndValueTitle = new Label(commandComposite10, SWT.RIGHT);
		WidgetTestUtil.setTestId(this, "skipendvalue", skipEndValueTitle);
		skipEndValueTitle.setText(Messages.getString("end.value") + " : ");
		skipEndValueTitle.setLayoutData(new RowData(70,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_messageRetryEndValue = new Text(commandComposite10, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "errorendvalue", m_messageRetryEndValue);
		m_messageRetryEndValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		m_messageRetryEndValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_messageRetryEndValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);
		
		if (m_isFileJob) {
			m_messageRetryEndValue.setEnabled(false);
		}

		//リトライ
		m_commandRetryCondition = new Button(this, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "retryconditioncheck", m_commandRetryCondition);
		m_commandRetryCondition.setText(Messages.getString("command.error.retry"));
		m_commandRetryCondition.setLayoutData(new RowData(300,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_commandRetryCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_commandRetryConditionGroup.setEnabled(true);
					m_commandRetry.setEnabled(true);
				} else {
					m_commandRetryConditionGroup.setEnabled(false);
					m_commandRetry.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		m_commandRetryConditionGroup = new Group(this, SWT.NONE);
		//m_retryConditionGroup.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "commandCompositeRetryConditionGroup");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		rowLayout.spacing = 10;
		m_commandRetryConditionGroup.setLayout(rowLayout);

		Composite commandComposite11 = new Composite(m_commandRetryConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "command11", commandComposite11);
		//commandComposite11.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "commandCompositeRetryComposite");
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		commandComposite11.setLayout(rowLayout);
		messageRetryTitle = new Label(commandComposite11, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "jobretries", messageRetryTitle);
		messageRetryTitle.setText(Messages.getString("job.retries") + " : ");
		messageRetryTitle.setLayoutData(new RowData(90,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_commandRetry= new Text(commandComposite11, SWT.BORDER);
		WidgetTestUtil.setTestId(this, null, m_commandRetry);
		//m_errorMessageRetry.setData(ClusterControlPlugin.CUSTOM_WIDGET_ID, "commandCompositeMessageRetryText");
		m_commandRetry.setLayoutData(new RowData(100, SizeConstant.SIZE_TEXT_HEIGHT));
		m_commandRetry.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_commandRetry.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);
	}

	@Override
	public void update() {
		// 必須項目を明示
		if(m_endCondition.getSelection() && "".equals(this.m_endValue.getText())){
			this.m_endValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_endValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(m_messageRetryEndCondition.getSelection() && "".equals(m_messageRetryEndValue.getText())) {
			this.m_messageRetryEndValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_messageRetryEndValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_messageRetry.getText())){
			this.m_messageRetry.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_messageRetry.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(m_commandRetryCondition.getSelection() && "".equals(this.m_commandRetry.getText())){
			this.m_commandRetry.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_commandRetry.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * 制御(ノード)情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobWaitRuleInfo
	 * @see com.clustercontrol.jobmanagement.bean.JobCommandInfo
	 */
	public void reflectControlNodeInfo() {
		if (m_waitRule != null) {
			//通知
			m_notifyCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule.getMultiplicityNotify()));

			//通知の重要度
			setSelectPriority(m_notifyPriority, m_waitRule.getMultiplicityNotifyPriority());
			setMultiplicityOperation(m_waitRule.getMultiplicityOperation());

			m_endValue.setText(String.valueOf(m_waitRule.getMultiplicityEndValue()));
		}

		m_messageRetry.setText("10");
		m_messageRetryEndCondition.setSelection(true);
		m_messageRetryEndValue.setText(String
				.valueOf(EndStatusConstant.INITIAL_VALUE_ABNORMAL));
		m_commandRetry.setText("10");
		m_commandRetryCondition.setSelection(false);

		if (m_jobCommand != null) {
			//リトライ回数
			m_messageRetry.setText(String.valueOf(m_jobCommand.getMessageRetry()));

			//エラー時終了
			m_messageRetryEndCondition.setSelection(YesNoConstant.typeToBoolean(m_jobCommand
					.getMessageRetryEndFlg()));

			//エラー時終了値
			m_messageRetryEndValue.setText(String.valueOf(m_jobCommand.getMessageRetryEndValue()));

			//エラー時リトライ
			m_commandRetryCondition.setSelection(YesNoConstant.typeToBoolean(m_jobCommand
					.getCommandRetryFlg()));

			//エラー時のリトライ回数
			m_commandRetry.setText(String.valueOf(m_jobCommand.getCommandRetry()));
		} else if (m_jobFile != null) {
			//リトライ回数
			m_messageRetry.setText(String.valueOf(m_jobFile.getMessageRetry()));

			//エラー時終了
			m_messageRetryEndCondition.setSelection(YesNoConstant.typeToBoolean(m_jobFile.getMessageRetryEndFlg()));

			//エラー時終了値
			m_messageRetryEndValue.setText(String.valueOf(m_jobFile.getMessageRetryEndValue()));

			//エラー時リトライ
			m_commandRetryCondition.setSelection(YesNoConstant.typeToBoolean(m_jobFile.getCommandRetryFlg()));

			//エラー時のリトライ回数
			m_commandRetry.setText(String.valueOf(m_jobFile.getCommandRetry()));
		}

		//エラー時終了
		if (m_messageRetryEndCondition.getSelection()) {
			m_messageRetryEndValue.setEnabled(false);
			if (!m_isFileJob) {
				m_messageRetryEndValue.setEnabled(true);
			}
			m_messageRetry.setEnabled(true);
		} else {
			m_messageRetryEndValue.setEnabled(false);
			m_messageRetry.setEnabled(false);
		}

		//正常終了するまで繰り返す
		if (m_commandRetryCondition.getSelection()) {
			m_commandRetry.setEnabled(true);
		} else {
			m_commandRetry.setEnabled(false);
		}

	}

	private void setMultiplicityOperation(Integer status) {
		m_waitCondition.setSelection(false);
		m_endCondition.setSelection(false);

		//wait,end,running
		if (status == StatusConstant.TYPE_END) {
			m_endCondition.setSelection(true);
			m_endValue.setEnabled(true);
		} else if (status == StatusConstant.TYPE_RUNNING) {
			m_endValue.setEnabled(false);
		} else {
			m_waitCondition.setSelection(true);
			m_endValue.setEnabled(false);
		}
	}

	/**
	 * ジョブ待ち条件情報を設定します。
	 *
	 * @param start ジョブ待ち条件情報
	 */
	public void setWaitRuleInfo(JobWaitRuleInfo start) {
		m_waitRule = start;
	}

	/**
	 * ジョブ待ち条件情報を返します。
	 *
	 * @return ジョブ待ち条件情報
	 */
	public JobWaitRuleInfo getWaitRuleInfo() {
		return m_waitRule;
	}

	/**
	 * ジョブコマンド情報を設定する。
	 *
	 * @param jobCommand ジョブコマンド情報
	 */
	public void setCommandInfo(JobCommandInfo jobCommand) {
		m_jobCommand = jobCommand;
	}
	
	/**
	 * ジョブファイル転送情報を設定する。
	 *
	 * @param jobFile ジョブファイル転送情報
	 */
	public void setFileInfo(JobFileInfo jobFile) {
		m_jobFile = jobFile;
	}

	/**
	 * ジョブコマンド情報を返す。
	 *
	 * @return ジョブコマンド情報
	 */
	public JobCommandInfo getCommandInfo() {
		return m_jobCommand;
	}
	
	/**
	 * ジョブファイル転送情報を返す。
	 * 
	 * @return ジョブファイル転送情報
	 */
	public JobFileInfo getFileInfo() {
		return m_jobFile;
	}

	/**
	 * コンポジットの情報から、ジョブ待ち条件情報を作成する。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobWaitRuleInfo
	 */
	public ValidateResult createWaitRuleInfo() {

		//条件関係取得
		if (m_waitCondition.getSelection()) {
			m_waitRule.setMultiplicityOperation(StatusConstant.TYPE_WAIT);
		} else if (m_endCondition.getSelection()){
			m_waitRule.setMultiplicityOperation(StatusConstant.TYPE_END);
		}

		//通知
		m_waitRule.setMultiplicityNotify(YesNoConstant.booleanToType(m_notifyCondition
				.getSelection()));

		//通知の重要度
		m_waitRule.setMultiplicityNotifyPriority(getSelectPriority(m_notifyPriority));

		//終了値
		try {
			m_waitRule.setMultiplicityEndValue(
					Integer.parseInt(m_endValue.getText()));
		} catch (NumberFormatException e) {
			if (m_waitRule.getMultiplicityOperation() == StatusConstant.TYPE_END) {
				ValidateResult result = null;
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.21"));
				return result;
			}
		}

		return null;
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

		if (m_jobCommand == null) {
			m_jobCommand = new JobCommandInfo();
		}

		//リトライ回数
		try {
			if (m_messageRetry.getText().length() > 0) {
				m_jobCommand.setMessageRetry(Integer.parseInt(m_messageRetry.getText()));
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.87"));
				return result;
			}
		} catch(NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.87"));
			return result;
		}

		//エラー時終了
		m_jobCommand.setMessageRetryEndFlg(YesNoConstant.booleanToType(m_messageRetryEndCondition
				.getSelection()));

		//エラー時終了値取得
		try {
			if (m_messageRetryEndValue.getText().length() > 0) {
				m_jobCommand.setMessageRetryEndValue(Integer.parseInt(m_messageRetryEndValue
						.getText()));
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.21"));
				return result;
			}
		} catch (NumberFormatException e) {
			if (m_jobCommand.getMessageRetryEndValue() == YesNoConstant.TYPE_YES) {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.21"));
				return result;
			}
		}

		//エラーリトライ回数
		try {
			if (m_commandRetry.getText().length() > 0) {
				m_jobCommand.setCommandRetry(Integer.parseInt(m_commandRetry.getText()));
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.87"));
				return result;
			}
		} catch(NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.87"));
			return result;
		}

		//エラー時のリトライ
		m_jobCommand.setCommandRetryFlg(YesNoConstant.booleanToType(m_commandRetryCondition
				.getSelection()));

		return null;
	}
	
	/**
	 * コンポジットの情報から、ジョブコマンド情報を作成する。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobFileInfo
	 */
	public ValidateResult createFileInfo() {
		ValidateResult result = null;

		if (m_jobFile == null) {
			m_jobFile = new JobFileInfo();
		}

		//リトライ回数
		try {
			if (m_messageRetry.getText().length() > 0) {
				m_jobFile.setMessageRetry(Integer.parseInt(m_messageRetry.getText()));
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.87"));
				return result;
			}
		} catch(NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.87"));
			return result;
		}

		//エラー時終了
		m_jobFile.setMessageRetryEndFlg(YesNoConstant.booleanToType(m_messageRetryEndCondition
				.getSelection()));

		//エラー時終了値取得
		try {
			if (m_messageRetryEndValue.getText().length() > 0) {
				m_jobFile.setMessageRetryEndValue(Integer.parseInt(m_messageRetryEndValue
						.getText()));
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.21"));
				return result;
			}
		} catch (NumberFormatException e) {
			if (m_jobFile.getMessageRetryEndValue() == YesNoConstant.TYPE_YES) {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.21"));
				return result;
			}
		}

		//エラーリトライ回数
		try {
			if (m_commandRetry.getText().length() > 0) {
				m_jobFile.setCommandRetry(Integer.parseInt(m_commandRetry.getText()));
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.87"));
				return result;
			}
		} catch(NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.87"));
			return result;
		}

		//エラー時のリトライ
		m_jobFile.setCommandRetryFlg(YesNoConstant.booleanToType(m_commandRetryCondition
				.getSelection()));

		return null;
	}


		/**
	 * 指定した重要度に該当する終了遅延通知重要度用コンボボックスの項目を選択します。
	 *
	 * @param combo 終了遅延通知重要度用コンボボックスのインスタンス
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
	 * 終了遅延通知重要度用コンボボックスにて選択している重要度を取得します。
	 *
	 * @param combo 終了遅延通知重要度用コンボボックスのインスタンス
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

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_waitCondition.setEnabled(enabled);
		m_endCondition.setEnabled(enabled);
		m_endValue.setEnabled(m_endCondition.getSelection() && enabled);
		m_notifyCondition.setEnabled(enabled);
		m_notifyPriority.setEnabled(m_notifyCondition.getSelection() && enabled);
		m_messageRetryEndCondition.setEnabled(enabled);
		m_messageRetry.setEnabled(enabled);
		m_messageRetryEndValue.setEnabled(!m_isFileJob && m_messageRetryEndCondition.getSelection() && enabled);
		m_commandRetryCondition.setEnabled(enabled);
		m_commandRetry.setEnabled(m_commandRetryCondition.getSelection() && enabled);
	}

}
