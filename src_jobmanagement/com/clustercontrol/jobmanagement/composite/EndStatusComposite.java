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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.EndStatusColorConstant;
import com.clustercontrol.bean.EndStatusConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.composite.action.NumberVerifyListener;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobEndStatusInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 終了状態タブ用のコンポジットクラスです。
 *
 * @version 4.0.0
 * @since 1.0.0
 */
public class EndStatusComposite extends Composite {
	/** 正常終了値 */
	private Text m_normalValue = null;
	/** 正常終了値範囲(開始) */
	private Text m_normalStartRange = null;
	/** 正常終了値範囲(終了) */
	private Text m_normalEndRange = null;
	/** 警告終了値 */
	private Text m_warningValue = null;
	/** 警告終了値範囲(開始) */
	private Text m_warningStartRange = null;
	/** 警告終了値範囲(終了) */
	private Text m_warningEndRange = null;
	/** 異常終了値 */
	private Text m_abnormalValue = null;
	/** ジョブ終了値情報のリスト */
	private List<JobEndStatusInfo> m_end = null;

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
	public EndStatusComposite(Composite parent, int style) {
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
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.fill = true;
		this.setLayout(layout);

		RowLayout rowLayout = null;

		Composite endStatusComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy", endStatusComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		endStatusComposite.setLayout(rowLayout);

		Label dummy1 = new Label(endStatusComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dymmy1", dummy1);
		dummy1.setLayoutData(new RowData(60, SizeConstant.SIZE_LABEL_HEIGHT));

		Label endValueTitle = new Label(endStatusComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "endvalue", endValueTitle);
		endValueTitle.setText(Messages.getString("end.value"));
		endValueTitle.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));

		Label rangeTitle = new Label(endStatusComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "rangeendvalue", rangeTitle);
		rangeTitle.setText(Messages.getString("range.end.value"));
		rangeTitle.setLayoutData(new RowData(240,
				SizeConstant.SIZE_LABEL_HEIGHT));

		//正常
		Composite endStatusNormComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "normal", endStatusNormComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		endStatusNormComposite.setLayout(rowLayout);

		Label normalTitle = new Label(endStatusNormComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "normaltitle", normalTitle);
		normalTitle.setText(EndStatusConstant.STRING_NORMAL + " : ");
		normalTitle.setLayoutData(new RowData(60,
				SizeConstant.SIZE_LABEL_HEIGHT));
		normalTitle.setBackground(EndStatusColorConstant.COLOR_NORMAL);

		this.m_normalValue = new Text(endStatusNormComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "normalvalue", m_normalValue);
		this.m_normalValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_normalValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		this.m_normalValue.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		this.m_normalStartRange = new Text(endStatusNormComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "startrange", m_normalStartRange);
		this.m_normalStartRange.setText("");
		this.m_normalStartRange.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_normalStartRange.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		this.m_normalStartRange.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label normalTo = new Label(endStatusNormComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "normalto", normalTo);
		normalTo.setText("-");
		normalTo.setLayoutData(new RowData(20, SizeConstant.SIZE_LABEL_HEIGHT));

		this.m_normalEndRange = new Text(endStatusNormComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "endrange", m_normalEndRange);
		this.m_normalEndRange.setText("");
		this.m_normalEndRange.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_normalEndRange.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		this.m_normalEndRange.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		//警告
		Composite endStatusWarnComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "warn", endStatusWarnComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		endStatusWarnComposite.setLayout(rowLayout);

		Label warningTitle = new Label(endStatusWarnComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "warningtitle", warningTitle);
		warningTitle.setText(EndStatusConstant.STRING_WARNING + " : ");
		warningTitle.setLayoutData(new RowData(60,
				SizeConstant.SIZE_LABEL_HEIGHT));
		warningTitle.setBackground(EndStatusColorConstant.COLOR_WARNING);

		this.m_warningValue = new Text(endStatusWarnComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "warningvalue", m_warningValue);
		this.m_warningValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_warningValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		this.m_warningValue.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		this.m_warningStartRange = new Text(endStatusWarnComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "warningstartrange", m_warningStartRange);
		this.m_warningStartRange.setText("");
		this.m_warningStartRange.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_warningStartRange.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		this.m_warningStartRange.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label warningTo = new Label(endStatusWarnComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "warningto", warningTo);
		warningTo.setText("-");
		warningTo
		.setLayoutData(new RowData(20, SizeConstant.SIZE_LABEL_HEIGHT));

		this.m_warningEndRange = new Text(endStatusWarnComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "warningendrange", m_warningEndRange);
		this.m_warningEndRange.setText("");
		this.m_warningEndRange.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_warningEndRange.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		this.m_warningEndRange.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		//異常
		Composite endStatusAbnormalComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "abnormal", endStatusAbnormalComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		endStatusAbnormalComposite.setLayout(rowLayout);

		Label abnormalTitle = new Label(endStatusAbnormalComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "abnormaltitle", abnormalTitle);
		abnormalTitle.setText(EndStatusConstant.STRING_ABNORMAL + " : ");
		abnormalTitle.setLayoutData(new RowData(60,
				SizeConstant.SIZE_LABEL_HEIGHT));
		abnormalTitle.setBackground(EndStatusColorConstant.COLOR_ABNORMAL);

		this.m_abnormalValue = new Text(endStatusAbnormalComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "abnormalvalue", m_abnormalValue);
		this.m_abnormalValue.setLayoutData(new RowData(100,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_abnormalValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		this.m_abnormalValue.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label abnormalMessage = new Label(endStatusAbnormalComposite, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "abnormalmessage", abnormalMessage);
		abnormalMessage.setText(Messages.getString("other"));
		abnormalMessage.setLayoutData(new RowData(240,
				SizeConstant.SIZE_LABEL_HEIGHT));
	}

	/**
	 * 更新処理
	 *
	 */
	@Override
	public void update(){
		// 必須項目を明示
		if("".equals(this.m_normalValue.getText())){
			this.m_normalValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_normalValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_normalStartRange.getText())){
			this.m_normalStartRange.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_normalStartRange.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_normalEndRange.getText())){
			this.m_normalEndRange.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_normalEndRange.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_warningValue.getText())){
			this.m_warningValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_warningValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_warningStartRange.getText())){
			this.m_warningStartRange.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_warningStartRange.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_warningEndRange.getText())){
			this.m_warningEndRange.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_warningEndRange.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_abnormalValue.getText())){
			this.m_abnormalValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_abnormalValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * ジョブ終了状態情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobEndStatusInfo
	 */
	public void reflectEndInfo() {

		// 初期値
		m_normalValue.setText(String
				.valueOf(EndStatusConstant.INITIAL_VALUE_NORMAL));
		m_normalStartRange.setText("0");
		m_normalEndRange.setText("0");
		m_warningValue.setText(String
				.valueOf(EndStatusConstant.INITIAL_VALUE_WARNING));
		m_warningStartRange.setText("1");
		m_warningEndRange.setText("1");
		m_abnormalValue.setText(String
				.valueOf(EndStatusConstant.INITIAL_VALUE_ABNORMAL));

		if (m_end != null) {
			JobEndStatusInfo infoNormal = null;
			JobEndStatusInfo infoWarning = null;
			JobEndStatusInfo infoAbnormal = null;

			for (int i = 0; i < m_end.size(); i++) {
				if (m_end.get(i).getType() == EndStatusConstant.TYPE_NORMAL) {
					infoNormal = m_end.get(i);
				} else if (m_end.get(i).getType() == EndStatusConstant.TYPE_WARNING) {
					infoWarning = m_end.get(i);
				} else if (m_end.get(i).getType() == EndStatusConstant.TYPE_ABNORMAL) {
					infoAbnormal = m_end.get(i);
				}
			}

			//正常
			if (infoNormal instanceof JobEndStatusInfo) {
				//終了値設定
				m_normalValue.setText(String.valueOf(infoNormal.getValue()));
				//終了値範囲の開始値設定
				m_normalStartRange.setText(String.valueOf(infoNormal
						.getStartRangeValue()));
				//終了値範囲の終了値設定
				m_normalEndRange.setText(String.valueOf(infoNormal
						.getEndRangeValue()));
			}

			//警告
			if (infoWarning instanceof JobEndStatusInfo) {
				//終了値設定
				m_warningValue.setText(String.valueOf(infoWarning.getValue()));
				//終了値範囲の開始値設定
				m_warningStartRange.setText(String.valueOf(infoWarning
						.getStartRangeValue()));
				//終了値範囲の終了値設定
				m_warningEndRange.setText(String.valueOf(infoWarning
						.getEndRangeValue()));
			}

			//異常
			if (infoAbnormal instanceof JobEndStatusInfo) {
				//終了値設定
				m_abnormalValue
				.setText(String.valueOf(infoAbnormal.getValue()));
			}
		}
	}

	/**
	 * ジョブ終了状態情報を設定します。
	 *
	 * @param end ジョブ終了状態情報のリスト
	 */
	public void setEndInfo(List<JobEndStatusInfo> end) {
		m_end = end;
	}

	/**
	 * ジョブ終了状態情報を返します。
	 *
	 * @return ジョブ終了状態情報のリスト
	 */
	public List<JobEndStatusInfo> getEndInfo() {
		return m_end;
	}

	/**
	 * コンポジットの情報から、ジョブ終了状態情報を作成する。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobEndStatusInfo
	 */
	public ValidateResult createEndInfo() {
		ValidateResult result = null;

		JobEndStatusInfo infoNormal = null;
		JobEndStatusInfo infoWarning = null;
		JobEndStatusInfo infoAbnormal = null;

		//終了状態定義情報クラスのインスタンスを作成・取得
		m_end = new ArrayList<JobEndStatusInfo>();
		infoNormal = new JobEndStatusInfo();
		infoNormal.setType(EndStatusConstant.TYPE_NORMAL);
		m_end.add(infoNormal);
		infoWarning = new JobEndStatusInfo();
		infoWarning.setType(EndStatusConstant.TYPE_WARNING);
		m_end.add(infoWarning);
		infoAbnormal = new JobEndStatusInfo();
		infoAbnormal.setType(EndStatusConstant.TYPE_ABNORMAL);
		m_end.add(infoAbnormal);

		try {
			//正常時の終了値取得
			infoNormal.setValue(Integer.parseInt(m_normalValue.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.7"));
			return result;
		}
		try {
			//正常時の終了値範囲の開始値取得
			infoNormal.setStartRangeValue(Integer.parseInt(m_normalStartRange
					.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.8"));
			return result;
		}
		try {
			//正常時の終了値範囲の終了値取得
			infoNormal.setEndRangeValue(Integer.parseInt(m_normalEndRange
					.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.9"));
			return result;
		}

		try {
			//警告時の終了値取得
			infoWarning.setValue(Integer.parseInt(m_warningValue.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.10"));
			return result;
		}
		try {
			//警告時の終了値範囲の開始値取得
			infoWarning.setStartRangeValue(Integer.parseInt(m_warningStartRange
					.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.11"));
			return result;
		}
		try {
			//警告時の終了値範囲の終了値取得
			infoWarning.setEndRangeValue(Integer.parseInt(m_warningEndRange
					.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.12"));
			return result;
		}

		try {
			//異常時の終了値取得
			infoAbnormal.setValue(Integer.parseInt(m_abnormalValue.getText()));
		} catch (NumberFormatException e) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.13"));
			return result;
		}

		//正常時の終了値範囲チェック
		if (infoNormal.getStartRangeValue() > infoNormal.getEndRangeValue()) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.14"));
			return result;
		}

		//警告時の終了値範囲チェック
		if (infoWarning.getStartRangeValue() > infoWarning.getEndRangeValue()) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.15"));
			return result;
		}

		return null;
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_normalValue.setEnabled(enabled);
		m_normalStartRange.setEnabled(enabled);
		m_normalEndRange.setEnabled(enabled);
		m_warningValue.setEnabled(enabled);
		m_warningStartRange.setEnabled(enabled);
		m_warningEndRange.setEnabled(enabled);
		m_abnormalValue.setEnabled(enabled);
	}
}
