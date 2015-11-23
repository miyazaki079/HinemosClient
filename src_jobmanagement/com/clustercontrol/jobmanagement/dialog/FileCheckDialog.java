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

package com.clustercontrol.jobmanagement.dialog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
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

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.ValidConstant;
import com.clustercontrol.calendar.composite.CalendarIdListComposite;
import com.clustercontrol.composite.ManagerListComposite;
import com.clustercontrol.composite.RoleIdListComposite;
import com.clustercontrol.composite.RoleIdListComposite.Mode;
import com.clustercontrol.composite.action.StringVerifyListener;
import com.clustercontrol.dialog.CommonDialog;
import com.clustercontrol.dialog.ScopeTreeDialog;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.action.GetJobKick;
import com.clustercontrol.jobmanagement.bean.FileCheckConstant;
import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;
import com.clustercontrol.ws.jobmanagement.InvalidSetting_Exception;
import com.clustercontrol.ws.jobmanagement.InvalidUserPass_Exception;
import com.clustercontrol.ws.jobmanagement.JobFileCheck;
import com.clustercontrol.ws.jobmanagement.JobKickDuplicate_Exception;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;
import com.clustercontrol.ws.repository.FacilityInfo;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * ジョブ[ファイルチェックの作成・変更]ダイアログクラスです。
 *
 * @version 4.1.0
 * @since 4.1.0
 */
public class FileCheckDialog extends CommonDialog {

	// ログ
	private static Log m_log = LogFactory.getLog( FileCheckDialog.class );
	/** 実行契機[ファイルチェック]情報*/
	private JobFileCheck m_jobFileCheck ;
	/**
	 * ダイアログの最背面レイヤのカラム数
	 * 最背面のレイヤのカラム数のみを変更するとレイアウトがくずれるため、
	 * グループ化されているレイヤは全てこれにあわせる
	 */
	private final int DIALOG_WIDTH = 12;
	/*
	 * 基本情報設定
	 */
	/** ファイルチェックID用テキスト */
	private Text txtJobKickId = null;
	/** ファイルチェック名用テキスト */
	private Text txtJobKickName = null;
	/** ジョブID用テキスト */
	private Text txtJobId = null;
	/** ジョブ名用テキスト */
	private Text txtJobName = null;
	/** カレンダID用コンボボックス */
	private CalendarIdListComposite cmpCalendarId = null;
	/** ジョブ参照用ボタン */
	private Button btnJobSelect = null;
	/*
	 * ファイルチェック設定
	 */
	/** スコープ用テキスト */
	private Text txtScope = null;
	/** スコープ参照用ボタン */
	private Button btnScopeSelect = null;
	/** 選択されたスコープのファシリティID。 */
	private String m_facilityId = null;
	/** ファイルパス */
	private Text txtDirectory = null;
	/** ファイル名 */
	private Text txtFileName = null;
	/** チェック種別 - 作成 */
	private Button btnTypeCreate = null;
	/** チェック種別 - 削除 */
	private Button btnTypeDelete = null;
	/** チェック種別 - 変更 */
	private Button btnTypeModify = null;
	/** チェック種別 - 変更 - タイムスタンプ*/
	private Button btnTypeTimeStamp = null;
	/** チェック種別 - 変更 - ファイルサイズ*/
	private Button btnTypeFileSize = null;
	/*
	 * 有効・無効
	 */
	/** 有効用ラジオボタン */
	private Button btnValid = null;
	/** 無効用ラジオボタン */
	private Button btnInvalid = null;

	/** オーナーロールID用テキスト */
	private RoleIdListComposite cmpOwnerRoleId = null;

	/** マネージャ名コンボボックス用コンポジット */
	private ManagerListComposite m_managerComposite = null;

	/** シェル */
	private Shell m_shell = null;

	/**
	 * 作成 or 変更情報
	 * 作成時 false
	 * 変更時 true
	 **/
	//	private boolean m_mode = false;
	/**
	 * 作成：MODE_ADD = 0;
	 * 変更：MODE_MODIFY = 1;
	 * 複製：MODE_COPY = 3;
	 * */
	private int mode = PropertyDefineConstant.MODE_ADD;

	/** 所属ジョブユニットのジョブID */
	private String m_jobunitId = null;

	/** 実行契機ID*/
	private String m_JobKickId = null;

	/** マネージャ名 */
	private String managerName = null;

	/**
	 * コンストラクタ
	 * 変更時、コピー時
	 * @param parent
	 * @param managerName
	 * @param id
	 */
	public FileCheckDialog(Shell parent, String managerName, String id, int mode){
		super(parent);
		this.m_JobKickId = id;
		this.mode = mode;
		this.managerName = managerName;
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
				Messages.getString("dialog.job.add.modify.filecheck"));
		/**
		 * レイアウト設定
		 * ダイアログ内のベースとなるレイアウトが全てを変更
		 */
		GridLayout baseLayout = new GridLayout(1, true);
		baseLayout.marginWidth = 10;
		baseLayout.marginHeight = 10;
		baseLayout.numColumns = DIALOG_WIDTH;
		//一番下のレイヤー
		parent.setLayout(baseLayout);

		GridData gridData= null;

		// マネージャ
		Composite fileCheckComposite = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, fileCheckComposite);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns =12;
		fileCheckComposite.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 12;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		fileCheckComposite.setLayoutData(gridData);

		Label labelManager = new Label(fileCheckComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "manager", labelManager);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelManager.setLayoutData(gridData);
		labelManager.setText(Messages.getString("facility.manager") + " : ");
		if(this.mode == PropertyDefineConstant.MODE_MODIFY){
			this.m_managerComposite = new ManagerListComposite(fileCheckComposite, SWT.NONE, false);
		} else {
			this.m_managerComposite = new ManagerListComposite(fileCheckComposite, SWT.NONE, true);
		}
		WidgetTestUtil.setTestId(this, "managerComposite", this.m_managerComposite);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.m_managerComposite.setLayoutData(gridData);

		if(this.managerName != null) {
			this.m_managerComposite.setText(this.managerName);
		}
		if(this.mode != PropertyDefineConstant.MODE_MODIFY) {
			this.m_managerComposite.getComboManagerName().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String managerName = m_managerComposite.getText();
					cmpOwnerRoleId.createRoleIdList(managerName);
					updateCalendarId();
				}
			});
		}

		/*
		 * ファイルチェックID
		 */
		//ラベル
		Label labelScheduleId = new Label(fileCheckComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "scheduleid", labelScheduleId);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelScheduleId.setText(Messages.getString("jobkick.id") + " : ");
		labelScheduleId.setLayoutData(gridData);

		/*
		 * テキスト
		 */
		txtJobKickId = new Text(fileCheckComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobkickid", txtJobKickId);
		txtJobKickId.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.txtJobKickId.setLayoutData(gridData);
		this.txtJobKickId.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		/*
		 * ファイルチェック名
		 */
		//ラベル
		Label labelScheduleName = new Label(fileCheckComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "jobkickname", labelScheduleName);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelScheduleName.setText(Messages.getString("jobkick.name") + " : ");
		labelScheduleName.setLayoutData(gridData);
		//テキスト
		this.txtJobKickName = new Text(fileCheckComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobkickname", txtJobKickName);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.txtJobKickName.setLayoutData(gridData);
		this.txtJobKickName.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		this.txtJobKickName.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		/*
		 * オーナーロールID
		 */
		Label labelRoleId = new Label(fileCheckComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "roleid", labelRoleId);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelRoleId.setLayoutData(gridData);
		labelRoleId.setText(Messages.getString("owner.role.id") + " : ");
		if (this.mode == PropertyDefineConstant.MODE_ADD
				|| this.mode == PropertyDefineConstant.MODE_COPY) {
			this.cmpOwnerRoleId = new RoleIdListComposite(fileCheckComposite,
					SWT.NONE, this.managerName, true, Mode.OWNER_ROLE);
			this.cmpOwnerRoleId.getComboRoleId().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateCalendarId();
				}
			});
		} else {
			this.cmpOwnerRoleId = new RoleIdListComposite(fileCheckComposite,
					SWT.NONE, this.managerName, false, Mode.OWNER_ROLE);
		}
		WidgetTestUtil.setTestId(this, "cmpownerownerroleid", cmpOwnerRoleId);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmpOwnerRoleId.setLayoutData(gridData);

		/*
		 * ジョブID
		 */
		//ラベル
		Label labelJobId = new Label(fileCheckComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "jobid", labelJobId);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelJobId.setText(Messages.getString("job.id") + " : ");
		labelJobId.setLayoutData(gridData);
		//テキスト
		this.txtJobId = new Text(fileCheckComposite, SWT.READ_ONLY | SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobid", txtJobId);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.txtJobId.setLayoutData(gridData);
		this.txtJobId.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		//ボタン
		btnJobSelect = new Button(fileCheckComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobselect", btnJobSelect);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnJobSelect.setText(Messages.getString("refer"));
		btnJobSelect.setLayoutData(gridData);
		btnJobSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JobTreeDialog dialog = new JobTreeDialog(m_shell, m_managerComposite.getText(), cmpOwnerRoleId.getText(), true);
				if (dialog.open() == IDialogConstants.OK_ID) {
					JobTreeItem selectItem = dialog.getSelectItem().get(0);
					if (selectItem.getData().getType() != JobConstant.TYPE_COMPOSITE) {
						txtJobId.setText(selectItem.getData().getId());
						txtJobName.setText(selectItem.getData().getName());
						setJobunitId(selectItem.getData().getJobunitId());
					} else {
						txtJobId.setText("");
						txtJobName.setText("");
						setJobunitId(null);
					}
				}
			}
		});
		/*
		 * ジョブ名
		 */
		//ラベル
		Label labelJobName = new Label(fileCheckComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "jobname", labelJobName);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelJobName.setText(Messages.getString("job.name") + " : ");
		labelJobName.setLayoutData(gridData);
		//テキスト
		txtJobName = new Text(fileCheckComposite, SWT.BORDER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "jobname", txtJobName);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		txtJobName.setLayoutData(gridData);
		/*
		 * カレンダID
		 */
		//ラベル
		Label labelCalendarId = new Label(fileCheckComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "calendarid", labelCalendarId);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelCalendarId.setText(Messages.getString("calendar.id") + " : ");
		labelCalendarId.setLayoutData(gridData);
		//コンボボックス
		cmpCalendarId = new CalendarIdListComposite(fileCheckComposite, SWT.NONE, false);
		WidgetTestUtil.setTestId(this, "calendaridlist", cmpCalendarId);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmpCalendarId.setLayoutData(gridData);

		//ファイルチェック設定グループ作成
		createFileCheck(parent);

		Group groupValidOrInvalid = new Group(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "validorinvalid", groupValidOrInvalid);
		layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns =6;
		groupValidOrInvalid.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 12;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupValidOrInvalid.setLayoutData(gridData);
		groupValidOrInvalid.setText(Messages.getString("valid") + "/"
				+ Messages.getString("invalid"));

		//有効ボタン
		btnValid = new Button(groupValidOrInvalid, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "valid", btnValid);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnValid.setText(ValidConstant.STRING_VALID);
		btnValid.setLayoutData(gridData);
		btnValid.setSelection(true);
		//無効ボタン
		btnInvalid = new Button(groupValidOrInvalid, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "invalid", btnInvalid);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnInvalid.setText(ValidConstant.STRING_INVALID);
		btnInvalid.setLayoutData(gridData);

		// ダイアログを調整
		this.adjustDialog();
		//スケジュール情報反映
		reflectJobFileCheck();
		update();
	}
	/**
	 * ファイルチェック設定グループ作成
	 * @param groupFileCheck
	 */
	private void createFileCheck(Composite parent){
		/**
		 * ファイルチェック設定グループ
		 */
		Group groupFileCheck = new Group(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "filecheck", groupFileCheck);
		// 変数として利用されるラベル
		Label label = null;
		// 変数として利用されるグリッドデータ
		GridData gridData = null;
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.numColumns = DIALOG_WIDTH;
		groupFileCheck.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = DIALOG_WIDTH;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupFileCheck.setLayoutData(gridData);
		groupFileCheck.setText(Messages.getString("file.check.setting"));
		/*
		 * スコープ
		 */
		// ラベル
		label = new Label(groupFileCheck, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scope", label);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("scope") + " : ");

		// テキスト
		txtScope =  new Text(groupFileCheck, SWT.READ_ONLY | SWT.BORDER);
		WidgetTestUtil.setTestId(this, "scope", txtScope);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		txtScope.setLayoutData(gridData);
		txtScope.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		// 参照ボタン
		btnScopeSelect = new Button(groupFileCheck, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scopeselect", btnScopeSelect);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnScopeSelect.setLayoutData(gridData);
		btnScopeSelect.setText(Messages.getString("refer"));
		btnScopeSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScopeTreeDialog dialog = new ScopeTreeDialog(m_shell, m_managerComposite.getText(), cmpOwnerRoleId.getText());
				if (dialog.open() == IDialogConstants.OK_ID) {
					FacilityTreeItem selectItem = dialog.getSelectItem();
					FacilityInfo info = selectItem.getData();
					m_facilityId = info.getFacilityId();
					txtScope.setText(m_facilityId);
					update();
				}
			}
		});
		/*
		 * ディレクトリ
		 */
		// ラベル
		label = new Label(groupFileCheck, SWT.NONE);
		WidgetTestUtil.setTestId(this, "filecheckdirectory", label);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("file.check.directory") + " : ");
		// テキスト
		txtDirectory = new Text(groupFileCheck, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "directory", txtDirectory);
		txtDirectory.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_1024));
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		txtDirectory.setLayoutData(gridData);
		txtDirectory.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		/*
		 * ファイル名
		 */
		// ラベル
		label = new Label(groupFileCheck, SWT.NONE);
		WidgetTestUtil.setTestId(this, "filename", label);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("file.name") + "(" + Messages.getString("regex") + ") : ");
		// テキスト
		txtFileName = new Text(groupFileCheck, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "filename", txtFileName);
		txtFileName.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		txtFileName.setLayoutData(gridData);
		txtFileName.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		/*
		 * チェック種別
		 */
		Group groupCheck = new Group(groupFileCheck, SWT.NONE);
		WidgetTestUtil.setTestId(this, "check", groupCheck);
		// 変数として利用されるグリッドデータ
		layout = new GridLayout(1, true);
		layout.numColumns = 4;
		groupCheck.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = DIALOG_WIDTH;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupCheck.setText(Messages.getString("file.check.type")+ " : ");
		groupCheck.setLayoutData(gridData);
		//作成
		btnTypeCreate = new Button(groupCheck, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "typecreate", btnTypeCreate);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 4;
		btnTypeCreate.setText(Messages.getString("create"));
		btnTypeCreate.setLayoutData(gridData);
		btnTypeCreate.setSelection(true);
		btnTypeCreate.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
				}
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		//削除
		btnTypeDelete = new Button(groupCheck, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "typedelete", btnTypeDelete);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 4;
		btnTypeDelete.setText(Messages.getString("delete"));
		btnTypeDelete.setLayoutData(gridData);
		btnTypeDelete.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
				}
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		//変更
		btnTypeModify = new Button(groupCheck, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "typemodify", btnTypeModify);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 4;
		btnTypeModify.setText(Messages.getString("modify"));
		btnTypeModify.setLayoutData(gridData);
		btnTypeModify.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
				}
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Composite fileCheckCreateComposite = new Composite(groupCheck, SWT.NONE);
		WidgetTestUtil.setTestId(this, "create", fileCheckCreateComposite);
		layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns =12;
		fileCheckCreateComposite.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 12;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		fileCheckCreateComposite.setLayoutData(gridData);
		//空白ラベル
		Label spaceLabel = new Label(fileCheckCreateComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "space", spaceLabel);
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		spaceLabel.setLayoutData(gridData);

		//変更 - タイムスタンプ
		btnTypeTimeStamp = new Button(fileCheckCreateComposite, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "typetimestamp", btnTypeTimeStamp);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 5;
		btnTypeTimeStamp.setText(Messages.getString("file.check.type.modify.timestamp"));
		btnTypeTimeStamp.setLayoutData(gridData);
		btnTypeTimeStamp.setSelection(true);
		btnTypeTimeStamp.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
				}
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		//変更 - ファイルサイズ
		btnTypeFileSize = new Button(fileCheckCreateComposite, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "typefilesize", btnTypeFileSize);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 5;
		btnTypeFileSize.setText(Messages.getString("file.check.type.modify.file.size"));
		btnTypeFileSize.setLayoutData(gridData);
		btnTypeFileSize.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
				}
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
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
		m_shell.setSize(new Point(500, m_shell.getSize().y));

		// 画面中央に配置
		Display display = m_shell.getDisplay();
		m_shell.setLocation((display.getBounds().width - m_shell.getSize().x) / 2,
				(display.getBounds().height - m_shell.getSize().y) / 2);
	}
	/**
	 * 更新処理
	 *
	 */
	public void update(){
		/*
		 *  必須項目を明示
		 */
		//実行契機ID
		if("".equals(this.txtJobKickId.getText())){
			this.txtJobKickId.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtJobKickId.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		//実行契機名
		if("".equals(this.txtJobKickName.getText())){
			this.txtJobKickName.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtJobKickName.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		//ジョブID
		if("".equals(this.txtJobId.getText())){
			this.txtJobId.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtJobId.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		//スコープ
		if("".equals(this.txtScope.getText())){
			this.txtScope.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtScope.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		//ファイルパス
		if("".equals(this.txtDirectory.getText())){
			this.txtDirectory.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtDirectory.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		//ファイル名
		if("".equals(this.txtFileName.getText())){
			this.txtFileName.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtFileName.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		//
		if(btnTypeCreate.getSelection() || btnTypeDelete.getSelection()){
			btnTypeTimeStamp.setEnabled(false);
			btnTypeFileSize.setEnabled(false);
		} else {
			btnTypeTimeStamp.setEnabled(true);
			btnTypeFileSize.setEnabled(true);
		}
	}

	/**
	 * ダイアログにスケジュール情報を反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.ScheduleTableDefine
	 */
	private void reflectJobFileCheck() {
		JobFileCheck jobFileCheck = null;
		//マネージャより実行契機IDと一致するスケジュールを取得する
		//if(this.jobKickId!= null){
		//変更時、コピー時
		if(mode == PropertyDefineConstant.MODE_MODIFY
				|| mode == PropertyDefineConstant.MODE_COPY){
			jobFileCheck = GetJobKick.getJobFileCheck(this.managerName, this.m_JobKickId);
		}else {
			jobFileCheck = new JobFileCheck();
		}
		m_jobFileCheck = jobFileCheck;

		// オーナーロールID設定
		if (jobFileCheck != null && jobFileCheck.getOwnerRoleId() != null) {
			this.cmpOwnerRoleId.setText(jobFileCheck.getOwnerRoleId());
		}
		// 他CompositeへのオーナーロールIDの設定
		this.cmpCalendarId.createCalIdCombo(this.managerName, cmpOwnerRoleId.getText());

		//実行契機IDを設定
		if(jobFileCheck.getId() != null){
			txtJobKickId.setText(jobFileCheck.getId());
			this.m_JobKickId = jobFileCheck.getId();
			if(mode == PropertyDefineConstant.MODE_MODIFY){
				//ファイルチェック変更時、ファイルチェックIDは変更不可とする
				txtJobKickId.setEnabled(false);
			}
		}
		//実行契機名を設定
		if(jobFileCheck.getName() != null){
			txtJobKickName.setText(jobFileCheck.getName());
		}
		//ジョブIDを設定
		if(jobFileCheck.getJobId() != null){
			txtJobId.setText(jobFileCheck.getJobId());
		}
		//ジョブ名を設定
		if(jobFileCheck.getJobName() != null){
			txtJobName.setText(jobFileCheck.getJobName());
		}
		//ジョブユニットIDを設定
		if(jobFileCheck.getJobunitId() != null){
			String jobunitId = jobFileCheck.getJobunitId();
			this.setJobunitId(jobunitId);
		}
		//カレンダIDを設定
		if (jobFileCheck.getCalendarId() != null) {
			this.cmpCalendarId.setText(jobFileCheck.getCalendarId());
		}

		//スコープを設定
		if(jobFileCheck.getFacilityId() != null){
			txtScope.setText(jobFileCheck.getFacilityId());
		}
		//ファイルパスを設定
		if(jobFileCheck.getDirectory() != null){
			txtDirectory.setText(jobFileCheck.getDirectory());
		}

		//ファイル名を設定
		if(jobFileCheck.getFileName() != null){
			txtFileName.setText(jobFileCheck.getFileName());
		}

		//ファイルチェック種別を設定
		if(jobFileCheck.getEventType() != null){
			switch(jobFileCheck.getEventType()){
			//作成の場合
			case FileCheckConstant.TYPE_CREATE :
				btnTypeCreate.setSelection(true);
				btnTypeDelete.setSelection(false);
				btnTypeModify.setSelection(false);
				break;
				//削除の場合
			case FileCheckConstant.TYPE_DELETE :
				btnTypeCreate.setSelection(false);
				btnTypeDelete.setSelection(true);
				btnTypeModify.setSelection(false);
				break;
				//変更の場合
			case FileCheckConstant.TYPE_MODIFY :
				btnTypeCreate.setSelection(false);
				btnTypeDelete.setSelection(false);
				btnTypeModify.setSelection(true);
				if(jobFileCheck.getModifyType() == null){
					break;
				}
				//変更 - タイムスタンプの場合
				if(jobFileCheck.getModifyType()
						== FileCheckConstant.TYPE_MODIFY_TIMESTAMP){
					btnTypeTimeStamp.setSelection(true);
					btnTypeFileSize.setSelection(false);
				}
				//変更 - ファイルサイズの場合
				else {
					btnTypeTimeStamp.setSelection(false);
					btnTypeFileSize.setSelection(true);
				}
			}
		}
		if(jobFileCheck.getValid() != null){
			//有効/無効設定
			Integer effective = jobFileCheck.getValid();
			if (effective == ValidConstant.TYPE_VALID) {
				btnValid.setSelection(true);
				btnInvalid.setSelection(false);
			} else {
				btnValid.setSelection(false);
				btnInvalid.setSelection(true);
			}
		}
		// 必須入力項目を可視化
		this.update();
	}

	/**
	 * ダイアログの情報からスケジュール情報を作成します。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.GetJobKickTableDefine
	 */
	private ValidateResult createJobFileCheck() {
		ValidateResult result = null;

		m_jobFileCheck = new JobFileCheck();

		//ジョブユニットID取得
		if (getJobunitId() != null) {
			m_jobFileCheck.setJobunitId(getJobunitId());
		}

		//オーナーロールID
		if (cmpOwnerRoleId.getText().length() > 0) {
			m_jobFileCheck.setOwnerRoleId(cmpOwnerRoleId.getText());
		}

		//実行契機ID取得
		if (txtJobKickId.getText().length() > 0) {
			m_jobFileCheck.setId(txtJobKickId.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.88"));
			return result;
		}

		//実行契機名取得
		if (txtJobKickName.getText().length() > 0) {
			m_jobFileCheck.setName(txtJobKickName.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.89"));
			return result;
		}

		//ジョブID取得
		if (txtJobId.getText().length() > 0) {
			m_jobFileCheck.setJobId(txtJobId.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.22"));
			return result;
		}

		//ジョブ名取得
		if (txtJobName.getText().length() > 0) {
			m_jobFileCheck.setJobName(txtJobName.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.23"));
			return result;
		}

		//カレンダID
		if (cmpCalendarId.getText().length() > 0) {
			m_jobFileCheck.setCalendarId(cmpCalendarId.getText());
		}
		else{
			m_jobFileCheck.setCalendarId("");
		}
		//スコープ
		if(txtScope.getText().length() > 0){
			m_jobFileCheck.setFacilityId(txtScope.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.hinemos.3"));
			return result;
		}
		//ディレクトリ
		if(txtDirectory.getText().length() > 0){
			m_jobFileCheck.setDirectory(txtDirectory.getText());
		}else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.92"));
			return result;
		}
		//ファイル名
		if(txtFileName.getText().length() > 0){
			m_jobFileCheck.setFileName(txtFileName.getText());
		}else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.90"));
			return result;
		}

		//作成の場合
		if(btnTypeCreate.getSelection()){
			m_jobFileCheck.setEventType(FileCheckConstant.TYPE_CREATE);
		}
		//削除の場合
		else if (btnTypeDelete.getSelection()){
			m_jobFileCheck.setEventType(FileCheckConstant.TYPE_DELETE);
		}
		//変更の場合
		//m_modify.getSelection() = true
		else {
			m_jobFileCheck.setEventType(FileCheckConstant.TYPE_MODIFY);
			//変更 - タイムスタンプの場合
			if(btnTypeTimeStamp.getSelection()){
				m_jobFileCheck.setModifyType(FileCheckConstant.TYPE_MODIFY_TIMESTAMP);
			}
			//変更 - ファイルサイズの場合
			else {
				m_jobFileCheck.setModifyType(FileCheckConstant.TYPE_MODIFY_FILESIZE);
			}
		}
		//有効/無効取得
		if (btnValid.getSelection()) {
			m_jobFileCheck.setValid(ValidConstant.TYPE_VALID);
		} else {
			m_jobFileCheck.setValid(ValidConstant.TYPE_INVALID);
		}
		return result;
	}

	/**
	 * ＯＫボタンテキスト取得
	 *
	 * @return ＯＫボタンのテキスト
	 */
	@Override
	protected String getOkButtonText() {
		return Messages.getString("register");
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
	 * 入力値を保持したプロパティを返します。<BR>
	 * プロパティオブジェクトのコピーを返します。
	 *
	 * @return プロパティ
	 *
	 * @see com.clustercontrol.util.PropertyUtil#copy(Property)
	 */
	@Override
	protected ValidateResult validate() {
		ValidateResult result = null;
		result = createJobFileCheck();
		if (result != null) {
			return result;
		}
		return null;
	}

	/**
	 * 所属ジョブユニットのジョブIDを返します。<BR>
	 * @return 所属ジョブユニットのジョブID
	 */
	public String getJobunitId() {
		return m_jobunitId;
	}

	/**
	 * 所属ジョブユニットのジョブIDを設定します。<BR>
	 * @param jobunitId 所属ジョブユニットのジョブID
	 */
	public void setJobunitId(String jobunitId) {
		m_jobunitId = jobunitId;
	}

	@Override
	protected boolean action() {
		boolean result = false;
		try {
			String managerName = this.m_managerComposite.getText();
			//変更時
			JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
			if(mode == PropertyDefineConstant.MODE_MODIFY){
				wrapper.modifyFileCheck(m_jobFileCheck);
				Object[] arg = {managerName};
				MessageDialog.openInformation(null, Messages.getString("successful"),
						Messages.getString("message.job.77",  arg));
			}
			//作成時、コピー時
			else {
				wrapper.addFileCheck(m_jobFileCheck);
				Object[] arg = {managerName};
				MessageDialog.openInformation(null, Messages.getString("successful"),
						Messages.getString("message.job.79", arg));
			}
			result = true;
		} catch (InvalidRole_Exception e) {
			// アクセス権なしの場合、エラーダイアログを表示する
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));
		} catch (JobKickDuplicate_Exception e) {
			String[] args = {m_jobFileCheck.getId()};
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.job.83",args) + " " + e.getMessage());
		} catch (InvalidUserPass_Exception e) {
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.job.74") + ", " + e.getMessage());
		} catch (InvalidSetting_Exception e) {
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.job.74") + ", " + e.getMessage());
		} catch (Exception e) {
			m_log.warn("action(), " + e.getMessage(), e);
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		return result;
	}

	private void updateCalendarId() {
		cmpCalendarId.createCalIdCombo(this.m_managerComposite.getText(), cmpOwnerRoleId.getText());
		txtJobId.setText("");
		txtJobName.setText("");
		setJobunitId(null);
		m_facilityId = "";
		txtScope.setText(m_facilityId);
	}
}
