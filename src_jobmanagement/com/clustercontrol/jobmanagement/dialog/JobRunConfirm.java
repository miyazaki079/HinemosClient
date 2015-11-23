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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.dialog.CommonDialog;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobTriggerInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * ジョブ実行確認ダイアログクラスです。
 *
 * @version 4.0.0
 * @since 1.0.0
 */
public class JobRunConfirm extends CommonDialog {

	/** ダイアログのタイトル */
	private String m_title = "";
	private String m_MessageText = "";

	/** ジョブ変数情報 */
	private JobTriggerInfo m_trigger = null;

	/** ジョブの待ち条件（時刻）有効無効チェックボタン */
	private Button btnJobWaitTime = null;
	/** ジョブの待ち条件（セッション開始後）有効無効チェックボタン */
	private Button btnJobWaitMinute = null;
	/** ジョブ起動コマンド置換有無　チェックボタン */
	private Button btnJobCommand = null;
	/** ジョブ起動コマンド置換有無　置き換え文字列 */
	private Text textJobCommandText = null;

	/** シェル */
	private Shell m_shell = null;

	/**
	 * コンストラクタ
	 * 作成時
	 * @param parent 親シェル
	 */
	public JobRunConfirm(Shell parent) {
		super(parent);

	}

	/**
	 * ダイアログエリアを生成します。
	 *
	 * @param parent 親コンポジット
	 */
	@Override
	protected void customizeDialog(Composite parent) {
		m_shell = this.getShell();

		parent.getShell().setText(
				Messages.getString("confirmed"));
		/**
		 * レイアウト設定
		 * ダイアログ内のベースとなるレイアウトが全てを変更
		 */
		GridLayout baseLayout = new GridLayout(1, true);
		baseLayout.marginWidth = 5;
		baseLayout.marginHeight = 5;
		baseLayout.numColumns = 10;
		//一番下のレイヤー
		parent.setLayout(baseLayout);

		Composite composite = null;
		GridData gridData= null;

		/*
		 * テスト実行モード選択ダイアログ
		 */
		composite = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns =10;
		composite.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gridData);

		//メッセージ
		Label labelInfomation = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "infomation", labelInfomation);
		gridData = new GridData();
		gridData.horizontalSpan = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelInfomation.setText(m_MessageText);
		labelInfomation.setLayoutData(gridData);

		Group runJobGroup = new Group(composite, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, runJobGroup);
		layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns =6;
		runJobGroup.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		runJobGroup.setText(Messages.getString("message.job.118"));
		runJobGroup.setLayoutData(gridData);

		Composite selectionComposite = new Composite(runJobGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "selection", selectionComposite);
		GridLayout selectionLayout = new GridLayout(1, true);
		selectionLayout.marginWidth = 5;
		selectionLayout.marginHeight = 5;
		selectionLayout.numColumns =8;
		selectionComposite.setLayout(selectionLayout);
		gridData = new GridData();
		gridData.horizontalSpan = 7;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		selectionComposite.setLayoutData(gridData);

		//ジョブ待ち条件（時刻）
		btnJobWaitTime = new Button(selectionComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "jobwaittime", btnJobWaitTime);
		gridData = new GridData();
		gridData.horizontalSpan = 7;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnJobWaitTime.setText(Messages.getString("message.job.119"));
		btnJobWaitTime.setLayoutData(gridData);
		btnJobWaitTime.setSelection(false);

		//ジョブ待ち条件（ジョブセッション開始後の時間）
		btnJobWaitMinute = new Button(selectionComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "jobwaitminute", btnJobWaitMinute);
		gridData = new GridData();
		gridData.horizontalSpan = 7;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnJobWaitMinute.setText(Messages.getString("message.job.120"));
		btnJobWaitMinute.setLayoutData(gridData);
		btnJobWaitMinute.setSelection(false);

		//起動コマンド置換
		btnJobCommand = new Button(selectionComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "jobcommand", btnJobCommand);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnJobCommand.setText(Messages.getString("message.job.121"));
		btnJobCommand.setLayoutData(gridData);
		btnJobCommand.setSelection(false);
		btnJobCommand.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					textJobCommandText.setEnabled(true);
				} else {
					textJobCommandText.setEnabled(false);
				}
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// テキスト
		textJobCommandText = new Text(selectionComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobcommand", textJobCommandText);
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		textJobCommandText.setLayoutData(gridData);
		textJobCommandText.setEnabled(false);
		this.textJobCommandText.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		// ダイアログを調整
		this.adjustDialog();
	}

	/**
	 * ダイアログエリアを調整します。
	 *
	 */
	private void adjustDialog(){
		// サイズを最適化
		// グリッドレイアウトを用いた場合、こうしないと横幅が画面いっぱいになります。
		m_shell.pack();
		m_shell.setSize(new Point(650, m_shell.getSize().y));

		// 画面中央に配置
		Display display = m_shell.getDisplay();
		m_shell.setLocation((display.getBounds().width - m_shell.getSize().x) / 2,
				(display.getBounds().height - m_shell.getSize().y) / 2);
	}
	/**
	 * 更新処理
	 *
	 */
	private void update(){
		if (btnJobCommand.getSelection()) {
			if("".equals(this.textJobCommandText.getText())){
				this.textJobCommandText.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
			}else{
				this.textJobCommandText.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
			}
		} else {
			this.textJobCommandText.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}
	/**
	 * ダイアログタイトルを返します。
	 *
	 * @return ダイアログタイトル
	 */
	public String getTitleText() {
		return m_title;
	}

	/**
	 * ダイアログタイトルを設定します。
	 *
	 * @param title ダイアログタイトル
	 */
	public void setTitleText(String title) {
		m_title = title;
	}

	/**
	 * メッセージを返します。
	 *
	 * @return メッセージ
	 */
	public String getMessageText() {
		return m_MessageText;
	}

	/**
	 * メッセージを設定します。
	 *
	 * @param messagetext メッセージ
	 */
	public void setMessageText(String messagetext) {
		m_MessageText = messagetext;
	}

	/**
	 * ＯＫボタンテキスト取得
	 *
	 * @return ＯＫボタンのテキスト
	 */
	@Override
	protected String getOkButtonText() {
		return Messages.getString("run");
	}

	/**
	 * キャンセルボタンテキスト取得
	 *
	 * @return キャンセルボタンのテキスト
	 */
	@Override
	protected String getCancelButtonText() {
		return Messages.getString("cancel");
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
		setTriggerInfo();
		//起動コマンド取得
		if (btnJobCommand.getSelection()) {
			if (textJobCommandText.getText().length() > 0) {
			} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.3"));
			}
		}
		return result;
	}
	/**
	 * 入力データ更新処理
	 *
	 */
	private void setTriggerInfo(){
		this.m_trigger = new JobTriggerInfo();

		//条件関係取得
		if (btnJobWaitTime.getSelection()) {
			m_trigger.setJobWaitTime(true);
		} else {
			m_trigger.setJobWaitTime(false);
		}
		if (btnJobWaitMinute.getSelection()) {
			m_trigger.setJobWaitMinute(true);
		} else {
			m_trigger.setJobWaitMinute(false);
		}
		if (btnJobCommand.getSelection()) {
			m_trigger.setJobCommand(true);
		} else {
			m_trigger.setJobCommand(false);
		}
		if (textJobCommandText.getText().length() > 0) {
			m_trigger.setJobCommandText(textJobCommandText.getText());
		} else{
			m_trigger.setJobCommandText("");
		}
	}
	/**
	 * 入力情報を設定します。
	 *
	 * @param info 入力情報
	 */
	public void setInputData(JobTriggerInfo info) {
		m_trigger = info;
	}

	/**
	 * 入力情報を返します。
	 *
	 * @return 入力情報
	 */
	public JobTriggerInfo getInputData() {
		return m_trigger;
	}
}
