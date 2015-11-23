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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.EndStatusConstant;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.composite.action.NumberVerifyListener;
import com.clustercontrol.composite.action.PositiveNumberVerifyListener;
import com.clustercontrol.composite.action.TimeVerifyListener;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.bean.ConditionTypeConstant;
import com.clustercontrol.jobmanagement.bean.OperationConstant;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.TimeTo48hConverter;
import com.clustercontrol.ws.jobmanagement.JobWaitRuleInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 開始遅延タブ用のコンポジットクラスです。
 *
 * @version 2.1.0
 * @since 2.1.0
 */
public class StartDelayComposite extends Composite {
	/** 開始遅延セッション開始後の時間用チェックボタン */
	private Button m_sessionCondition = null;
	/** 開始遅延セッション開始後の時間の値用テキスト*/
	private Text m_sessionValue = null;
	/** 開始遅延時刻用チェックボタン */
	private Button m_timeCondition = null;
	/** 開始遅延時刻の値用テキスト */
	private Text m_timeValue = null;
	/** 開始遅延判定対象の条件関係 AND用ラジオボタン */
	private Button m_andCondition = null;
	/** 開始遅延判定対象の条件関係 OR用ラジオボタン */
	private Button m_orCondition = null;
	/**  開始遅延通知用チェックボタン */
	private Button m_notifyCondition = null;
	/**  開始遅延通知重要度用コンボボックス */
	private Combo m_notifyPriority = null;
	/** 開始遅延操作用チェックボタン */
	private Button m_operationCondition = null;
	/** 開始遅延操作用グループ */
	private Group m_operationConditionGroup = null;
	/** 開始遅延操作種別用コンボボックス */
	private Combo m_operationType = null;
	/** 開始遅延操作終了状態用テキスト */
	private Combo m_operationStatus = null;
	/** 開始遅延操作終了値用テキスト */
	private Text m_operationValue = null;
	/** 開始遅延用チェックボタン */
	private Button m_startDelayCondition = null;
	/** 開始遅延用グループ */
	private Group m_startDelayConditionGroup = null;
	/** ジョブ待ち条件情報 */
	private JobWaitRuleInfo m_waitRule = null;

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
	public StartDelayComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * コンポジットを構築します。
	 */
	private void initialize() {
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 1;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.fill = true;
		this.setLayout(layout);

		RowLayout rowLayout = null;
		Label label = null;

		//開始遅延
		Composite startDelayStartComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "start", startDelayStartComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		startDelayStartComposite.setLayout(rowLayout);

		m_startDelayCondition = new Button(startDelayStartComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "conditioncheck", m_startDelayCondition);
		m_startDelayCondition.setText(Messages.getString("start.delay"));
		m_startDelayCondition.setLayoutData(new RowData(200,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_startDelayCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				//開始遅延コンポジットのオブジェクトの使用不可を設定
				setStartDelayEnabled(check.getSelection());
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		m_startDelayConditionGroup = new Group(startDelayStartComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "condition", m_startDelayConditionGroup);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		m_startDelayConditionGroup.setLayout(rowLayout);

		//判定対象一覧
		Group startDelayJudgementGroup = new Group(m_startDelayConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "objectlist", startDelayJudgementGroup);
		startDelayJudgementGroup.setText(Messages.getString("object.list"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		startDelayJudgementGroup.setLayout(rowLayout);

		//セッション開始後の時間
		Composite startDelayAfterSessionComposite= new Composite(startDelayJudgementGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "aftersession", startDelayAfterSessionComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayAfterSessionComposite.setLayout(rowLayout);
		m_sessionCondition = new Button(startDelayAfterSessionComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "sessionconditioncheck", m_sessionCondition);
		m_sessionCondition.setText(Messages.getString("time.after.session.start") + " : ");
		m_sessionCondition.setLayoutData(new RowData(220,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_sessionCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_sessionValue.setEnabled(true);
				} else {
					m_sessionValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		//セッション開始後の時間の値
		m_sessionValue = new Text(startDelayAfterSessionComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "sessionvalue", m_sessionValue);
		m_sessionValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		m_sessionValue.addVerifyListener(
				new PositiveNumberVerifyListener(0, DataRangeConstant.SMALLINT_HIGH));
		m_sessionValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);

		//時刻
		Composite startDelayTimeComposite= new Composite(startDelayJudgementGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "timecomposite", startDelayTimeComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayTimeComposite.setLayout(rowLayout);
		m_timeCondition = new Button(startDelayTimeComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "timeconditioncheck", m_timeCondition);
		m_timeCondition.setText(Messages.getString("wait.rule.time.example") + " : ");
		m_timeCondition.setLayoutData(new RowData(220,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_timeCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_timeValue.setEnabled(true);
				} else {
					m_timeValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		//時刻の値
		m_timeValue = new Text(startDelayTimeComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "timevalue", m_timeValue);
		m_timeValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		m_timeValue.addVerifyListener(new TimeVerifyListener());
		m_timeValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);
		//判定対象の条件関係
		Group startDelayBetweenConditionGroup = new Group(m_startDelayConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "betweencondition", startDelayBetweenConditionGroup);
		startDelayBetweenConditionGroup.setText(Messages.getString("condition.between.objects"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		startDelayBetweenConditionGroup.setLayout(rowLayout);

		Composite startDelayConditionComposite= new Composite(startDelayBetweenConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "conditioncomposite", startDelayConditionComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayConditionComposite.setLayout(rowLayout);
		m_andCondition = new Button(startDelayConditionComposite, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "andcondition", m_andCondition);
		m_andCondition.setText(Messages.getString("and"));
		m_andCondition.setLayoutData(new RowData(100,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_orCondition = new Button(startDelayConditionComposite, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "orcondition", m_orCondition);
		m_orCondition.setText(Messages.getString("or"));
		m_orCondition.setLayoutData(new RowData(100,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		label = new Label(startDelayConditionComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "startdelaycondition1", label);
		label.setLayoutData(new RowData(77, SizeConstant.SIZE_LABEL_HEIGHT));

		Composite startDelayDummyComposite= new Composite(m_startDelayConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy", startDelayDummyComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayDummyComposite.setLayout(rowLayout);
		label = new Label(startDelayDummyComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "startdelaycondition2", label);
		label.setLayoutData(new RowData(5, SizeConstant.SIZE_LABEL_HEIGHT));

		//通知
		Composite startDelayNotificationComposite= new Composite(m_startDelayConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "notification", startDelayNotificationComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayNotificationComposite.setLayout(rowLayout);
		m_notifyCondition = new Button(startDelayNotificationComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "notifycondition", m_notifyCondition);
		m_notifyCondition.setText(Messages.getString("notify.attribute") + " : ");
		m_notifyCondition.setLayoutData(new RowData(100,
				SizeConstant.SIZE_BUTTON_HEIGHT));
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
		m_notifyPriority = new Combo(startDelayNotificationComposite, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "notifypriority", m_notifyPriority);
		m_notifyPriority.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_notifyPriority.add(PriorityConstant.STRING_INFO);
		m_notifyPriority.add(PriorityConstant.STRING_WARNING);
		m_notifyPriority.add(PriorityConstant.STRING_CRITICAL);
		m_notifyPriority.add(PriorityConstant.STRING_UNKNOWN);

		//操作
		Composite startDelayOptComposite= new Composite(m_startDelayConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "opt", startDelayOptComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayOptComposite.setLayout(rowLayout);
		m_operationCondition = new Button(startDelayOptComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "optcondition", m_operationCondition);
		m_operationCondition.setText(Messages.getString("operations"));
		m_operationCondition.setLayoutData(new RowData(220,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_operationCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_operationType.setEnabled(true);

					int type = getSelectOperationName(m_operationType);
					if (type == OperationConstant.TYPE_STOP_SKIP) {
						m_operationStatus.setEnabled(true);
						m_operationValue.setEnabled(true);
					} else if(type == OperationConstant.TYPE_STOP_WAIT){
						m_operationStatus.setEnabled(false);
						m_operationValue.setEnabled(false);
					}
					m_operationConditionGroup.setEnabled(true);
				} else {
					m_operationType.setEnabled(false);
					m_operationStatus.setEnabled(false);
					m_operationValue.setEnabled(false);
					m_operationConditionGroup.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		m_operationConditionGroup = new Group(m_startDelayConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "optcondition", m_operationConditionGroup);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		m_operationConditionGroup.setLayout(rowLayout);

		//操作の名前
		Composite startDelayOptNameComposite= new Composite(m_operationConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "optname", startDelayOptNameComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayOptNameComposite.setLayout(rowLayout);
		label = new Label(startDelayOptNameComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "name", label);
		label.setText(Messages.getString("name") + " : ");
		label.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_operationType = new Combo(startDelayOptNameComposite, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "opttype", m_operationType);
		m_operationType.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_operationType.add(OperationConstant.STRING_STOP_SKIP);
		m_operationType.add(OperationConstant.STRING_STOP_WAIT);
		m_operationType.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo check = (Combo) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				int type = getSelectOperationName(check);
				if (type == OperationConstant.TYPE_STOP_SKIP) {
					m_operationStatus.setEnabled(true);
					m_operationValue.setEnabled(true);
				} else if(type == OperationConstant.TYPE_STOP_WAIT){
					m_operationStatus.setEnabled(false);
					m_operationValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		//操作の終了状態
		Composite startDelayOptEndStatusComposite= new Composite(m_operationConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "optendstatus", startDelayOptEndStatusComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayOptEndStatusComposite.setLayout(rowLayout);
		label = new Label(startDelayOptEndStatusComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "endstatus", label);
		label.setText(Messages.getString("end.status") + " : ");
		label.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_operationStatus = new Combo(startDelayOptEndStatusComposite, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "optstatus", m_operationStatus);
		m_operationStatus.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_operationStatus.add(EndStatusConstant.STRING_NORMAL);
		m_operationStatus.add(EndStatusConstant.STRING_WARNING);
		m_operationStatus.add(EndStatusConstant.STRING_ABNORMAL);

		//操作の終了値
		Composite startDelayOptEndValueComposite= new Composite(m_operationConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "optendvalue", startDelayOptEndValueComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		startDelayOptEndValueComposite.setLayout(rowLayout);
		label = new Label(startDelayOptEndValueComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "endvalue", label);
		label.setText(Messages.getString("end.value") + " : ");
		label.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_operationValue = new Text(startDelayOptEndValueComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "optvalue", m_operationValue);
		m_operationValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		m_operationValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_operationValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);
		label = new Label(startDelayOptEndValueComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, label);
		label.setLayoutData(new RowData(70, SizeConstant.SIZE_LABEL_HEIGHT));
	}

	@Override
	public void update() {
		// 必須項目を明示
		if(m_startDelayCondition.getSelection() && m_sessionCondition.getSelection() &&
				"".equals(this.m_sessionValue.getText())){
			this.m_sessionValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_sessionValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(m_startDelayCondition.getSelection() && m_timeCondition.getSelection() &&
				"".equals(this.m_timeValue.getText())){
			this.m_timeValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_timeValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(m_startDelayCondition.getSelection() && m_operationCondition.getSelection() &&
				getSelectOperationName(m_operationType) == OperationConstant.TYPE_STOP_SKIP &&
				"".equals(this.m_operationValue.getText())){
			this.m_operationValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_operationValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * ジョブ待ち条件情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobWaitRuleInfo
	 */
	public void reflectWaitRuleInfo() {
		if (m_waitRule != null) {
			//開始遅延
			m_startDelayCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule
					.getStartDelay()));

			//セッション開始後の時間
			m_sessionCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule.getStartDelaySession()));

			//セッション開始後の時間の値
			m_sessionValue.setText(String.valueOf(m_waitRule.getStartDelaySessionValue()));

			//時刻
			m_timeCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule.getStartDelayTime()));

			//時刻の値
			if (m_waitRule.getStartDelayTimeValue() != null) {
				m_timeValue.setText(TimeTo48hConverter.dateTo48hms(new Date(m_waitRule.getStartDelayTimeValue())));
			}
			else{
				m_timeValue.setText("");
			}

			//条件関係設定
			if (m_waitRule.getStartDelayConditionType() == ConditionTypeConstant.TYPE_AND) {
				m_andCondition.setSelection(true);
				m_orCondition.setSelection(false);
			} else {
				m_andCondition.setSelection(false);
				m_orCondition.setSelection(true);
			}

			//通知
			m_notifyCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule.getStartDelayNotify()));

			//通知の重要度
			setSelectPriority(m_notifyPriority,
					m_waitRule.getStartDelayNotifyPriority());

			//操作
			m_operationCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule.getStartDelayOperation()));

			//操作の名前
			setSelectOperationName(m_operationType,
					m_waitRule.getStartDelayOperationType());

			//操作の終了状態
			setSelectOperationEndStatus(m_operationStatus,
					m_waitRule.getStartDelayOperationEndStatus());

			//操作の終了値
			m_operationValue.setText(String.valueOf(m_waitRule.getStartDelayOperationEndValue()));
		}

		//開始遅延コンポジットのオブジェクトの使用不可を設定
		setStartDelayEnabled(m_startDelayCondition.getSelection());
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
	 * コンポジットの情報から、ジョブ待ち条件情報を作成します。
	 *
	 * @return 入力値の検証結果
	 */
	public ValidateResult createWaitRuleInfo() {
		ValidateResult result = null;

		//開始遅延
		m_waitRule.setStartDelay(YesNoConstant.booleanToType(m_startDelayCondition
				.getSelection()));

		//セッション開始後の時間
		m_waitRule.setStartDelaySession(YesNoConstant.booleanToType(m_sessionCondition
				.getSelection()));

		//セッション開始後の時間の値
		try {
			m_waitRule.setStartDelaySessionValue(
					Integer.parseInt(m_sessionValue.getText()));
		} catch (NumberFormatException e) {
			if (m_waitRule.getStartDelaySession() == YesNoConstant.TYPE_YES) {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.52"));
				return result;
			}
		}

		//時刻
		m_waitRule.setStartDelayTime(YesNoConstant.booleanToType(m_timeCondition
				.getSelection()));

		//時刻の値
		if (m_waitRule.getStartDelayTime() == YesNoConstant.TYPE_YES) {
			boolean check = false;
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			Date from = null;
			Date to = null;
			Date date = null;
			try {
				from = formatter.parse("00:00:00");
				to = formatter.parse("48:00:00");
				date = formatter.parse(m_timeValue.getText());
				check = true;
			} catch (ParseException e) {
				formatter = new SimpleDateFormat("HH:mm");
				try {
					date = formatter.parse(m_timeValue.getText());
					check = true;
				} catch (ParseException e1) {
				}
			}

			if(check){
				//範囲チェック
				if(date.before(from) || date.after(to)){
					String[] args = { formatter.format(from), TimeTo48hConverter.dateTo48hms(to) };
					result = new ValidateResult();
					result.setValid(false);
					result.setID(Messages.getString("message.hinemos.1"));
					result.setMessage(Messages.getString("message.hinemos.8", args));
					return result;
				}

				m_waitRule.setStartDelayTimeValue(date.getTime());
			}
			else{
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.hinemos.6"));
				return result;
			}
		}

		//条件関係取得
		if (m_andCondition.getSelection()) {
			m_waitRule.setStartDelayConditionType(ConditionTypeConstant.TYPE_AND);
		} else {
			m_waitRule.setStartDelayConditionType(ConditionTypeConstant.TYPE_OR);
		}

		//通知
		m_waitRule.setStartDelayNotify(YesNoConstant.booleanToType(m_notifyCondition
				.getSelection()));

		//通知の重要度
		m_waitRule.setStartDelayNotifyPriority(getSelectPriority(m_notifyPriority));

		//操作
		m_waitRule.setStartDelayOperation(YesNoConstant.booleanToType(m_operationCondition
				.getSelection()));

		//操作の名前
		m_waitRule.setStartDelayOperationType(getSelectOperationName(m_operationType));

		//操作の終了値
		try {
			// 開始遅延が有効であり、操作がスキップの場合のみ、入力されている終了値を確認する
			m_waitRule.setStartDelayOperationEndStatus(getSelectOperationEndStatus(m_operationStatus));
			m_waitRule.setStartDelayOperationEndValue(Integer.parseInt(m_operationValue.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.21"));
			return result;
		}

		return null;
	}

	/**
	 * コンポジットに配置したオブジェクトの使用可・使用不可を設定します。
	 *
	 * @param enabled true：開始遅延を使用する、false：開始遅延を使用しない
	 */
	private void setStartDelayEnabled(boolean enabled) {
		if (enabled) {
			m_startDelayConditionGroup.setEnabled(true);

			//セッション開始後の時間
			m_sessionCondition.setEnabled(true);
			if(m_sessionCondition.getSelection())
				m_sessionValue.setEnabled(true);
			else
				m_sessionValue.setEnabled(false);

			//時刻
			m_timeCondition.setEnabled(true);
			if(m_timeCondition.getSelection())
				m_timeValue.setEnabled(true);
			else
				m_timeValue.setEnabled(false);

			//判定条件
			m_andCondition.setEnabled(true);
			m_orCondition.setEnabled(true);

			//通知
			m_notifyCondition.setEnabled(true);
			if(m_notifyCondition.getSelection())
				m_notifyPriority.setEnabled(true);
			else
				m_notifyPriority.setEnabled(false);

			//操作
			m_operationConditionGroup.setEnabled(true);
			m_operationCondition.setEnabled(true);
			if(m_operationCondition.getSelection()){
				m_operationType.setEnabled(true);
				m_operationStatus.setEnabled(false);
				m_operationValue.setEnabled(false);

				int type = getSelectOperationName(m_operationType);
				if (type == OperationConstant.TYPE_STOP_SKIP) {
					m_operationStatus.setEnabled(true);
					m_operationValue.setEnabled(true);
				} else if(type == OperationConstant.TYPE_STOP_WAIT){
					m_operationStatus.setEnabled(false);
					m_operationValue.setEnabled(false);
				}
			}
			else{
				m_operationType.setEnabled(false);
				m_operationStatus.setEnabled(false);
				m_operationValue.setEnabled(false);
			}
		} else {
			m_startDelayConditionGroup.setEnabled(false);

			//セッション開始後の時間
			m_sessionCondition.setEnabled(false);
			m_sessionValue.setEnabled(false);

			//時刻
			m_timeCondition.setEnabled(false);
			m_timeValue.setEnabled(false);

			//判定条件
			m_andCondition.setEnabled(false);
			m_orCondition.setEnabled(false);

			//通知
			m_notifyCondition.setEnabled(false);
			m_notifyPriority.setEnabled(false);

			//操作
			m_operationCondition.setEnabled(false);
			m_operationType.setEnabled(false);
			m_operationStatus.setEnabled(false);
			m_operationValue.setEnabled(false);
			m_operationConditionGroup.setEnabled(false);
		}
	}

	/**
	 * 指定した重要度に該当する開始遅延通知重要度用コンボボックスの項目を選択します。
	 *
	 * @param combo 開始遅延通知重要度用コンボボックスのインスタンス
	 * @param priority 重要度
	 *
	 * @see com.clustercontrol.bean.PriorityConstant
	 */
	private void setSelectPriority(Combo combo, int priority) {
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
	 * 開始遅延通知重要度用コンボボックスにて選択している重要度を取得します。
	 *
	 * @param combo 開始遅延通知重要度用コンボボックスのインスタンス
	 * @return 重要度
	 *
	 * @see com.clustercontrol.bean.PriorityConstant
	 */
	private int getSelectPriority(Combo combo) {
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
	 * 指定したジョブ操作種別に該当する開始遅延操作用コンボボックスの項目を選択します。
	 *
	 * @param combo コンボボックスのインスタンス
	 * @param operation ジョブ操作種別
	 *
	 * @see com.clustercontrol.jobmanagement.bean.OperationConstant
	 */
	private void setSelectOperationName(Combo combo, int operation) {
		String select = "";

		if (operation == OperationConstant.TYPE_STOP_SKIP) {
			select = OperationConstant.STRING_STOP_SKIP;
		} else if (operation == OperationConstant.TYPE_STOP_WAIT) {
			select = OperationConstant.STRING_STOP_WAIT;
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
	 * 開始遅延操作用コンボボックスにて選択しているジョブ操作種別を取得します。
	 *
	 * @param combo コンボボックスのインスタンス
	 * @return ジョブ操作種別
	 *
	 * @see com.clustercontrol.jobmanagement.bean.OperationConstant
	 */
	private int getSelectOperationName(Combo combo) {
		String select = combo.getText();

		if (select.equals(OperationConstant.STRING_STOP_SKIP)) {
			return OperationConstant.TYPE_STOP_SKIP;
		} else if (select.equals(OperationConstant.STRING_STOP_WAIT)) {
			return OperationConstant.TYPE_STOP_WAIT;
		}

		return -1;
	}

	/**
	 * 指定した重要度に該当する開始遅延終了状態用コンボボックスの項目を選択します。
	 *
	 */
	private void setSelectOperationEndStatus(Combo combo, int status) {
		String select = "";

		select = EndStatusConstant.typeToString(status);

		combo.select(0);
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (select.equals(combo.getItem(i))) {
				combo.select(i);
				break;
			}
		}
	}

	/**
	 * 開始遅延通知終了状態用コンボボックスにて選択している項目を取得します。
	 *
	 */
	private int getSelectOperationEndStatus(Combo combo) {
		String select = combo.getText();
		return EndStatusConstant.stringToType(select);
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_sessionCondition.setEnabled(enabled);
		m_sessionValue.setEnabled(enabled);
		m_timeCondition.setEnabled(enabled);
		m_timeValue.setEnabled(enabled);
		m_andCondition.setEnabled(enabled);
		m_orCondition.setEnabled(enabled);
		m_notifyCondition.setEnabled(enabled);
		m_notifyPriority.setEnabled(enabled);
		m_operationCondition.setEnabled(enabled);
		m_operationType.setEnabled(enabled);
		m_operationStatus.setEnabled(enabled);
		m_operationValue.setEnabled(enabled);
		m_startDelayCondition.setEnabled(enabled);
	}
}
