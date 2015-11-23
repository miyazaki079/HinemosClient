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
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.calendar.composite.CalendarIdListComposite;
import com.clustercontrol.composite.action.NumberVerifyListener;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobWaitRuleInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 制御タブ用のコンポジットクラスです。
 *
 * @version 2.1.0
 * @since 2.1.0
 */
public class ControlComposite extends Composite {
	/** 保留用チェックボタン */
	private Button m_waitCondition = null;
	/** スキップ用チェックボタン */
	private Button m_skipCondition = null;
	/** スキップ用グループ */
	private Group CtrlskipEndConditionGroup = null;
	/** スキップ終了状態用テキスト */
	private Combo m_skipEndStatus = null;
	/** スキップ終了値用テキスト */
	private Text m_skipEndValue = null;
	/** カレンダ用チェックボタン */
	private Button m_calendarCondition = null;
	/** カレンダ用グループ */
	private Group m_calendarConditionGroup = null;
	/** カレンダID用コンボボックス */
	private CalendarIdListComposite m_calendarId = null;
	/** カレンダ終了状態用テキスト */
	private Combo m_calendarEndStatus = null;
	/** カレンダ終了値用テキスト */
	private Text m_calendarEndValue = null;
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
	public ControlComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	/**
	 * コンポジットを配置します。
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

		//カレンダ
		Composite ctrlCalComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "cal", ctrlCalComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		ctrlCalComposite.setLayout(rowLayout);

		m_calendarCondition = new Button(ctrlCalComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "calconditioncheck", m_calendarCondition);
		m_calendarCondition.setText(Messages.getString("calendar"));
		m_calendarCondition.setLayoutData(new RowData(220,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_calendarCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_calendarConditionGroup.setEnabled(true);
					m_calendarId.setEnabled(true);
					m_calendarEndStatus.setEnabled(true);
					m_calendarEndValue.setEnabled(true);
				} else {
					m_calendarConditionGroup.setEnabled(false);
					m_calendarId.setEnabled(false);
					m_calendarEndStatus.setEnabled(false);
					m_calendarEndValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		m_calendarConditionGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "calcondition", m_calendarConditionGroup);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		m_calendarConditionGroup.setLayout(rowLayout);

		Composite ctrlCalConditionComposite= new Composite(m_calendarConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "calcondition", ctrlCalConditionComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		rowLayout.marginHeight = 2;
		ctrlCalConditionComposite.setLayout(rowLayout);
		Label calendarIdTitle = new Label(ctrlCalConditionComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "calendarid", calendarIdTitle);
		calendarIdTitle.setText(Messages.getString("calendar.id") + " : ");
		calendarIdTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_calendarId = new CalendarIdListComposite(ctrlCalConditionComposite, SWT.NONE, false);
		WidgetTestUtil.setTestId(this, "idlist", m_calendarId);
		m_calendarId.setEnabled(false);

		// カレンダ終了状態
		Composite ctrlCalEndComposite= new Composite(m_calendarConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "calend", ctrlCalEndComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		ctrlCalEndComposite.setLayout(rowLayout);
		Label calendarEndStatusTitle = new Label(ctrlCalEndComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "endstatus", calendarEndStatusTitle);
		calendarEndStatusTitle.setText(Messages.getString("end.status") + " : ");
		calendarEndStatusTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_calendarEndStatus = new Combo(ctrlCalEndComposite, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "calendstatus", m_calendarEndStatus);
		m_calendarEndStatus.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_calendarEndStatus.add(EndStatusConstant.STRING_NORMAL);
		m_calendarEndStatus.add(EndStatusConstant.STRING_WARNING);
		m_calendarEndStatus.add(EndStatusConstant.STRING_ABNORMAL);

		// カレンダ終了値
		Composite ctrlCalEndValueComposite = new Composite(m_calendarConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "calendvalue", ctrlCalEndValueComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		ctrlCalEndValueComposite.setLayout(rowLayout);
		Label calendarEndValueTitle = new Label(ctrlCalEndValueComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "endvalue", calendarEndValueTitle);
		calendarEndValueTitle.setText(Messages.getString("end.value") + " : ");
		calendarEndValueTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_calendarEndValue = new Text(ctrlCalEndValueComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "calendvalue", m_calendarEndValue);
		m_calendarEndValue.setLayoutData(new RowData(100, SizeConstant.SIZE_TEXT_HEIGHT));
		m_calendarEndValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_calendarEndValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);

		//保留
		Composite ctrlReserveComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "reserve", ctrlReserveComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		ctrlReserveComposite.setLayout(rowLayout);

		Label dummy4 = new Label(ctrlReserveComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dymmy4", dummy4);
		dummy4.setLayoutData(new RowData(190, SizeConstant.SIZE_LABEL_HEIGHT));
		m_waitCondition = new Button(ctrlReserveComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "waitcondition", m_waitCondition);
		m_waitCondition.setText(Messages.getString("reserve"));
		m_waitCondition.setLayoutData(new RowData(200,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_waitCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_skipCondition.setSelection(false);
					CtrlskipEndConditionGroup.setEnabled(false);
					m_skipEndStatus.setEnabled(false);
					m_skipEndValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		//スキップ
		Composite ctrlSkipComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "skip", ctrlSkipComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		ctrlSkipComposite.setLayout(rowLayout);

		m_skipCondition = new Button(ctrlSkipComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "skipconditioncheck", m_skipCondition);
		m_skipCondition.setText(Messages.getString("skip"));
		m_skipCondition.setLayoutData(new RowData(200,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_skipCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					CtrlskipEndConditionGroup.setEnabled(true);
					m_skipEndStatus.setEnabled(true);
					m_skipEndValue.setEnabled(true);
					m_waitCondition.setSelection(false);
				} else {
					CtrlskipEndConditionGroup.setEnabled(false);
					m_skipEndStatus.setEnabled(false);
					m_skipEndValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		CtrlskipEndConditionGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "skipend", CtrlskipEndConditionGroup);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		CtrlskipEndConditionGroup.setLayout(rowLayout);

		// スキップ終了状態
		Composite ctrlSkipEndStatusComposite = new Composite(CtrlskipEndConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "skipend", ctrlSkipEndStatusComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		ctrlSkipEndStatusComposite.setLayout(rowLayout);
		Label skipEndStatusTitle = new Label(ctrlSkipEndStatusComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "endstatus", skipEndStatusTitle);
		skipEndStatusTitle.setText(Messages.getString("end.status") + " : ");
		skipEndStatusTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_skipEndStatus = new Combo(ctrlSkipEndStatusComposite, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "skipendstatus", m_skipEndStatus);
		m_skipEndStatus.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_skipEndStatus.add(EndStatusConstant.STRING_NORMAL);
		m_skipEndStatus.add(EndStatusConstant.STRING_WARNING);
		m_skipEndStatus.add(EndStatusConstant.STRING_ABNORMAL);

		// スキップ終了値
		Composite ctrlSkipEndValueComposite = new Composite(CtrlskipEndConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "skipendvalue", ctrlSkipEndValueComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		ctrlSkipEndValueComposite.setLayout(rowLayout);
		Label skipEndValueTitle = new Label(ctrlSkipEndValueComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "endvalue", skipEndValueTitle);
		skipEndValueTitle.setText(Messages.getString("end.value") + " : ");
		skipEndValueTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_skipEndValue = new Text(ctrlSkipEndValueComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "skipendvalue", m_skipEndValue);
		m_skipEndValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		m_skipEndValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_skipEndValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);
	}

	/**
	 * 更新処理
	 *
	 */
	@Override
	public void update(){
		// 必須項目を明示
		if (m_calendarCondition.getSelection() && "".equals(this.m_calendarEndValue.getText())){
			this.m_calendarEndValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_calendarEndValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if (m_skipCondition.getSelection() && "".equals(this.m_skipEndValue.getText())){
			this.m_skipEndValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_skipEndValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * ジョブ待ち条件情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobWaitRuleInfo
	 */
	public void reflectWaitRuleInfo() {
		if (m_waitRule != null) {
			//カレンダ
			m_calendarCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule
					.getCalendar()));

			//カレンダID
			if (m_waitRule.getCalendarId() != null &&
					m_waitRule.getCalendarId().length() > 0) {
				m_calendarId.setText(m_waitRule.getCalendarId());
			}

			//カレンダの終了状態
			setSelectEndStatus(m_calendarEndStatus, m_waitRule.getCalendarEndStatus());
			//カレンダ未実行時の終了値
			m_calendarEndValue.setText(String.valueOf(m_waitRule.getCalendarEndValue()));
			//保留
			m_waitCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule.getSuspend()));
			//スキップ
			m_skipCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule.getSkip()));
			//スキップ終了値
			m_skipEndValue.setText(String.valueOf(m_waitRule.getSkipEndValue()));
		}

		//カレンダ
		if (m_calendarCondition.getSelection()) {
			m_calendarConditionGroup.setEnabled(true);
			m_calendarId.setEnabled(true);
			m_calendarEndStatus.setEnabled(true);
			m_calendarEndValue.setEnabled(true);
		} else {
			m_calendarConditionGroup.setEnabled(false);
			m_calendarId.setEnabled(false);
			m_calendarEndStatus.setEnabled(false);
			m_calendarEndValue.setEnabled(false);
		}

		//保留
		if (m_waitCondition.getSelection()) {
			m_skipCondition.setSelection(false);
		}

		//スキップ
		if (m_skipCondition.getSelection()) {
			CtrlskipEndConditionGroup.setEnabled(true);
			m_skipEndStatus.setEnabled(true);
			m_skipEndValue.setEnabled(true);
			m_waitCondition.setSelection(false);
		} else {
			CtrlskipEndConditionGroup.setEnabled(false);
			m_skipEndStatus.setEnabled(false);
			m_skipEndValue.setEnabled(false);
		}

		//スキップの終了状態
		setSelectEndStatus(m_skipEndStatus, m_waitRule.getSkipEndStatus());
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
	 * コンポジットの情報から、ジョブ待ち条件情報を作成する。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobWaitRuleInfo
	 */
	public ValidateResult createWaitRuleInfo() {
		ValidateResult result = null;

		//カレンダ
		m_waitRule.setCalendar(YesNoConstant.booleanToType(m_calendarCondition
				.getSelection()));

		//カレンダID
		if (m_calendarId.getText().length() == 0){
			m_waitRule.setCalendarId(null);
		}
		if (m_calendarId.getText().length() > 0) {
			m_waitRule.setCalendarId(m_calendarId.getText());
		}
		else{
			if (m_waitRule.getCalendar() == YesNoConstant.TYPE_YES) {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.calendar.22"));
				return result;
			}
		}

		//カレンダ未実行時の終了値取得
		try {
			m_waitRule.setCalendarEndStatus(getSelectEndStatus(m_calendarEndStatus));
			m_waitRule.setCalendarEndValue(Integer.parseInt(m_calendarEndValue.getText()));
		} catch (NumberFormatException e) {
			if (m_waitRule.getCalendar() == YesNoConstant.TYPE_YES) {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.21"));
				return result;
			}
		}

		//保留
		m_waitRule.setSuspend(YesNoConstant.booleanToType(m_waitCondition
				.getSelection()));

		//スキップ
		m_waitRule.setSkip(YesNoConstant.booleanToType(m_skipCondition
				.getSelection()));

		//スキップ終了値取得
		try {
			m_waitRule.setSkipEndStatus(getSelectEndStatus(m_skipEndStatus));
			m_waitRule.setSkipEndValue(Integer.parseInt(m_skipEndValue.getText()));
		} catch (NumberFormatException e) {
			if (m_waitRule.getSkip() == YesNoConstant.TYPE_YES) {
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
	 *終了状態用コンボボックスにて選択している項目を取得します。
	 *
	 */
	private int getSelectEndStatus(Combo combo) {
		String select = combo.getText();
		return EndStatusConstant.stringToType(select);
	}

	/**
	 * 指定した重要度に該当する終了状態用コンボボックスの項目を選択します。
	 *
	 */
	private void setSelectEndStatus(Combo combo, int status) {
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

	public CalendarIdListComposite getCalendarId() {
		return m_calendarId;
	}

	public void setCalendarId(CalendarIdListComposite calendarId) {
		this.m_calendarId = calendarId;
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_waitCondition.setEnabled(enabled);
		m_skipCondition.setEnabled(enabled);
		m_skipEndStatus.setEnabled(enabled);
		m_skipEndValue.setEnabled(enabled);
		m_calendarCondition.setEnabled(enabled);
		m_calendarId.setEnabled(enabled);
		m_calendarEndStatus.setEnabled(enabled);
		m_calendarEndValue.setEnabled(enabled);
	}


}
