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

package com.clustercontrol.jobmanagement.composite;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.dialog.JobTreeDialog;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;

/**
 * 参照タブ用のコンポジットクラスです。
 *
 * @version 4.1.0
 * @since 4.1.0
 */
public class ReferComposite extends Composite {

	/** カラム数（ジョブユニットID）。 */
	private static final int WIDTH_JOBUNIT_ID = 3;

	/** カラム数（ジョブID）。 */
	private static final int WIDTH_JOB_ID = 3;

	/** カラム数（参照ボタン）。 */
	private static final int WIDTH_REF_BTN = 2;

	/** 参照先ジョブユニットID テキストボックス */
	private Text m_textJobunitId = null;

	/** 参照先ジョブID テキストボックス */
	private Text m_textJobId = null;

	/** 参照ボタン */
	private Button m_buttonRefer = null;

	/** シェル */
	private Shell m_shell = null;

	/** 参照先ジョブユニットID*/
	private String m_referJobUnitId = null;

	/** 参照先ジョブID*/
	private String m_referJobId = null;

	/** ジョブツリー情報*/
	private JobTreeItem m_jobTreeItem = null;

	/** オーナーロールID */
	private String m_ownerRoleId = null;

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
	public ReferComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		m_shell = this.getShell();
	}

	/**
	 * コンポジットを配置します。
	 */
	private void initialize() {
		//タブのレイアウトは、RowLayout
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 1;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.fill = true;
		this.setLayout(layout);

		// コンポジットのレイアウトは、GridLayout
		GridLayout gridlayout = new GridLayout(8, true);
		gridlayout.marginWidth = 10;
		gridlayout.marginHeight = 10;
		gridlayout.numColumns = 8;

		// 変数として利用されるラベル
		Label label = null;
		// 変数として利用されるグリッドデータ
		GridData gridData = null;

		//ラベル用コンポジット
		Composite referLabelComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "label", referLabelComposite);
		referLabelComposite.setLayout(gridlayout);

		// ラベル（ジョブユニットID）
		label = new Label(referLabelComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobunitid", label);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_JOBUNIT_ID;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("jobunit.id"));

		// ラベル（ジョブID）
		label = new Label(referLabelComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobid", label);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_JOB_ID;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setText(Messages.getString("job.id"));


		//テキストボックス、ボタン用コンポジット
		Composite referInputComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "input", referInputComposite);
		referInputComposite.setLayout(gridlayout);

		// テキストボックス（ジョブユニットID）
		m_textJobunitId = new Text(referInputComposite, SWT.BORDER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "jobunitid", m_textJobunitId);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_JOBUNIT_ID;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		m_textJobunitId.setLayoutData(gridData);
		m_textJobunitId.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		// テキストボックス（ジョブID）
		m_textJobId = new Text(referInputComposite, SWT.BORDER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "jobid", m_textJobId);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_JOB_ID;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		m_textJobId.setLayoutData(gridData);

		// チェックボックス（参照）
		m_buttonRefer = new Button(referInputComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "refer", m_buttonRefer);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_REF_BTN;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		m_buttonRefer.setLayoutData(gridData);
		m_buttonRefer.setText(Messages.getString("refer"));
		m_buttonRefer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// ジョブツリーダイアログ表示
				JobTreeDialog dialog = new JobTreeDialog(m_shell, m_ownerRoleId, m_jobTreeItem, JobConstant.TYPE_REFERJOB);
				if (dialog.open() == IDialogConstants.OK_ID) {
					JobTreeItem selectItem = dialog.getSelectItem().isEmpty() ? null : dialog.getSelectItem().get(0);
					if (selectItem != null && selectItem.getData().getType() != JobConstant.TYPE_COMPOSITE) {
						m_textJobId.setText(selectItem.getData().getId());
						m_textJobunitId.setText(selectItem.getData().getJobunitId());
					} else {
						m_textJobId.setText("");
						m_textJobunitId.setText("");
					}
				}
			}
		});
		reflectReferInfo();
		update();
	}

	/**
	 * 更新処理
	 *
	 */
	@Override
	public void update(){
		// 必須項目を明示
		if("".equals(this.m_textJobunitId.getText())){
			this.m_textJobunitId.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_textJobunitId.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_textJobId.getText())){
			this.m_textJobId.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_textJobId.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * 参照先ジョブユニットIDを返す。<BR>
	 * @return 参照先ジョブユニットID
	 */
	public String getReferJobUnitId() {
		return m_referJobUnitId;
	}
	/**
	 * 参照先ジョブユニットIDを設定する。<BR>
	 * @param referJobUnitId 参照先ジョブユニットID
	 */
	public void setReferJobUnitId(String referJobUnitId) {
		this.m_referJobUnitId = referJobUnitId;
	}
	/**
	 * 参照先ジョブIDを返す。<BR>
	 * @return 参照先ジョブID
	 */
	public String getReferJobId() {
		return m_referJobId;
	}
	/**
	 * 参照先ジョブIDを設定する。<BR>
	 * @param referJobId 参照先ジョブID
	 */
	public void setReferJobId(String referJobId) {
		this.m_referJobId = referJobId;
	}
	/**
	 * ジョブツリーを設定する。<BR>
	 * @param jobTreeItem
	 */
	public void setJobTreeItem(JobTreeItem jobTreeItem) {
		m_jobTreeItem = jobTreeItem;
	}

	/**
	 * 参照ジョブ情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobFileInfo
	 */
	public void reflectReferInfo() {
		if(m_referJobUnitId != null && m_referJobUnitId.length() > 0){
			m_textJobunitId.setText(m_referJobUnitId);
		}
		if(m_referJobId != null && m_referJobId.length() > 0){
			m_textJobId.setText(m_referJobId);
		}
		update();
	}

	/**
	 * コンポジットの情報から、参照ジョブ情報を作成する。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobFileInfo
	 */
	public ValidateResult createReferInfo() {
		ValidateResult result = null;
		// 参照先jobId
		if (m_textJobId.getText() != null
				&& !"".equals(m_textJobId.getText().trim())) {
			this.setReferJobId(m_textJobId.getText());
		}else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.100"));
			return result;
		}
		// 参照先jobunitId
		if (m_textJobunitId.getText() != null
				&& !"".equals(m_textJobunitId.getText().trim())) {
			this.setReferJobUnitId(m_textJobunitId.getText());
		}else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.99"));
			return result;
		}

		return null;
	}

	public void setOwnerRoleId(String ownerRoleId) {
		this.m_ownerRoleId = ownerRoleId;
		this.m_textJobunitId.setText("");
		this.m_textJobId.setText("");
		this.m_referJobUnitId = null;
		this.m_referJobId = null;
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_textJobunitId.setEnabled(enabled);
		m_textJobId.setEnabled(enabled);
		m_buttonRefer.setEnabled(enabled);
	}
}
