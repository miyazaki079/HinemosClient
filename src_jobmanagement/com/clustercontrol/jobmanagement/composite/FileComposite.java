/*

//Copyright (C) 2006 NTT DATA Corporation

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
import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.composite.action.StringVerifyListener;
import com.clustercontrol.dialog.ScopeTreeDialog;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.bean.ProcessingMethodConstant;
import com.clustercontrol.repository.FacilityPath;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobFileInfo;
import com.clustercontrol.ws.repository.FacilityInfo;
import com.clustercontrol.ws.repository.FacilityTreeItem;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * ファイル転送タブ用のコンポジットクラスです。
 *
 * @version 4.0.0
 * @since 2.0.0
 */
public class FileComposite extends Composite {
	/** 転送スコープ */
	private Text m_srcScope = null;
	/** 転送ファイル */
	private Text m_srcFile = null;

	/** 受信スコープ */
	private Text m_destScope = null;
	/** 受信ディレクトリ */
	private Text m_destDirectory = null;

	/** エージェント実行ユーザ用ラジオボタン */
	private Button m_agentUser = null;
	/** ユーザを指定する用ラジオボタン  */
	private Button m_specifyUser = null;
	/** 実効ユーザ */
	private Text m_user = null;
	/** 転送スコープ参照ボタン */
	private Button m_srcScopeSelect = null;
	/** 受信スコープ参照ボタン */
	private Button m_destScopeSelect = null;
	/** 全てのノードで受信用ラジオボタン */
	private Button m_allNode = null;
	/** 1ノードで受信用ラジオボタン */
	private Button m_oneNode = null;
	/** ファイル転送時圧縮用チェックボタン */
	private Button m_compressionCondition = null;
	/** 転送ファイルチェック用チェックボタン */
	private Button m_checkFileCondition = null;
	/** 転送ファシリティID */
	private String m_srcFacilityId = null;
	/** 転送ファシリティパス */
	private String m_srcFacilityPath = null;
	/** 受信ファシリティID */
	private String m_destFacilityId = null;
	/** 受信ファシリティパス */
	private String m_destFacilityPath = null;
	/** ジョブファイル転送情報 */
	private JobFileInfo m_jobFileInfo = null;
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
	public FileComposite(Composite parent, int style) {
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

		//転送元用グループ
		Group fileTransferFromgroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "transferfrom", fileTransferFromgroup);
		fileTransferFromgroup.setText(Messages.getString("forward.source"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		fileTransferFromgroup.setLayout(rowLayout);

		//転送元スコープ用コンポジット
		Composite fileComposite1 = new Composite(fileTransferFromgroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "srcscope", fileComposite1);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		fileComposite1.setLayout(rowLayout);

		Label srcScopeTitle = new Label(fileComposite1, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scopetitle", srcScopeTitle);
		srcScopeTitle.setText(Messages.getString("scope") + " : ");
		srcScopeTitle.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));

		this.m_srcScope = new Text(fileComposite1, SWT.BORDER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "srcscope", m_srcScope);
		this.m_srcScope.setLayoutData(new RowData(200, SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_srcScope.setText("");
		this.m_srcScope.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		m_srcScopeSelect = new Button(fileComposite1, SWT.NONE);
		WidgetTestUtil.setTestId(this, "srcscopeselect", m_srcScopeSelect);
		m_srcScopeSelect.setText(Messages.getString("refer"));
		m_srcScopeSelect.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_srcScopeSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScopeTreeDialog dialog = new ScopeTreeDialog(m_shell, m_managerName, m_ownerRoleId);
				// ノードのみ選択可能とする。
				dialog.setSelectNodeOnly(true);
				if (dialog.open() == IDialogConstants.OK_ID) {
					FacilityTreeItem selectItem = dialog.getSelectItem();
					FacilityInfo info = selectItem.getData();
					FacilityPath path = new FacilityPath(
							ClusterControlPlugin.getDefault()
							.getSeparator());
					m_srcFacilityPath = path.getPath(selectItem);
					m_srcFacilityId = info.getFacilityId();
					m_srcScope.setText(m_srcFacilityPath);
				}
			}
		});

		//転送ファイル用コンポジット
		Composite fileComposite2 = new Composite(fileTransferFromgroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "file", fileComposite2);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		fileComposite2.setLayout(rowLayout);

		Label fileTitle = new Label(fileComposite2, SWT.NONE);
		WidgetTestUtil.setTestId(this, "filetitle", fileTitle);
		fileTitle.setText(Messages.getString("file") + " : ");
		fileTitle.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));

		this.m_srcFile = new Text(fileComposite2, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "srcfile", m_srcFile);
		this.m_srcFile.setLayoutData(new RowData(200, SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_srcFile.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_4096));
		this.m_srcFile.setText("");
		this.m_srcFile.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		//転送先用グループ
		Group fileTransferTogroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "filetransferto", fileTransferTogroup);
		fileTransferTogroup.setText(Messages.getString("forward.destination"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		fileTransferTogroup.setLayout(rowLayout);

		//転送先スコープ用コンポジット
		Composite fileComposite3 = new Composite(fileTransferTogroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "destscope", fileComposite3);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		fileComposite3.setLayout(rowLayout);

		Label destScopeTitle = new Label(fileComposite3, SWT.NONE);
		WidgetTestUtil.setTestId(this, "destscopetitle", destScopeTitle);
		destScopeTitle.setText(Messages.getString("scope") + " : ");
		destScopeTitle.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));

		this.m_destScope = new Text(fileComposite3, SWT.BORDER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "destscope", m_destScope);
		this.m_destScope.setLayoutData(new RowData(200, SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_destScope.setText("");
		this.m_destScope.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		m_destScopeSelect = new Button(fileComposite3, SWT.NONE);
		WidgetTestUtil.setTestId(this, "destscopeselect", m_destScopeSelect);
		m_destScopeSelect.setText(Messages.getString("refer"));
		m_destScopeSelect.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_destScopeSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScopeTreeDialog dialog = new ScopeTreeDialog(m_shell, m_managerName, m_ownerRoleId);
				if (dialog.open() == IDialogConstants.OK_ID) {
					FacilityTreeItem selectItem = dialog.getSelectItem();
					FacilityInfo info = selectItem.getData();
					FacilityPath path = new FacilityPath(
							ClusterControlPlugin.getDefault()
							.getSeparator());
					m_destFacilityPath = path.getPath(selectItem);
					m_destFacilityId = info.getFacilityId();
					m_destScope.setText(m_destFacilityPath);
				}
			}
		});

		//処理方式グループ
		Group methodGroup = new Group(fileTransferTogroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "method", methodGroup);
		methodGroup.setText(Messages.getString("process.method"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		methodGroup.setLayout(rowLayout);

		m_allNode = new Button(methodGroup, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "allnode", m_allNode);
		m_allNode.setText(Messages.getString("forward.all.nodes"));
		m_allNode.setLayoutData(
				new RowData(220, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_oneNode = new Button(methodGroup, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "onenode", m_oneNode);
		m_oneNode.setText(Messages.getString("forward.one.node"));
		m_oneNode.setLayoutData(
				new RowData(250, SizeConstant.SIZE_BUTTON_HEIGHT));

		//転送先ディレクトリ用コンポジット
		Composite fileComposite4 = new Composite(fileTransferTogroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "directory", fileComposite4);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		fileComposite4.setLayout(rowLayout);

		Label forwardDirTitle = new Label(fileComposite4, SWT.NONE);
		WidgetTestUtil.setTestId(this, "forwarddirtitle", forwardDirTitle);
		forwardDirTitle.setText(Messages.getString("directory") + " : ");
		forwardDirTitle.setLayoutData(new RowData(100,
				SizeConstant.SIZE_LABEL_HEIGHT));

		this.m_destDirectory = new Text(fileComposite4, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "destdirectory", m_destDirectory);
		this.m_destDirectory.setLayoutData(new RowData(200,
				SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_destDirectory.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_4096));
		this.m_destDirectory.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		//ファイル圧縮用コンポジット
		Composite fileComposite5 = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "compression", fileComposite5);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		fileComposite5.setLayout(rowLayout);

		m_compressionCondition = new Button(fileComposite5, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "compressioncondition", m_compressionCondition);
		m_compressionCondition.setText(Messages.getString("forward.compression.file"));
		m_compressionCondition.setLayoutData(new RowData(220,
				SizeConstant.SIZE_BUTTON_HEIGHT));

		m_checkFileCondition = new Button(fileComposite5, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "checkfile", m_checkFileCondition);
		m_checkFileCondition.setText(Messages.getString("forward.file.check"));
		m_checkFileCondition.setLayoutData(new RowData(250,
				SizeConstant.SIZE_BUTTON_HEIGHT));

		Group fileEffectiveUserGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "effectiveuser", fileEffectiveUserGroup);
		fileEffectiveUserGroup.setText(Messages.getString("effective.user"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 10;
		rowLayout.marginHeight = 10;
		fileEffectiveUserGroup.setLayout(rowLayout);

		Composite fileComposite6 = new Composite(fileEffectiveUserGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "agentuser", fileComposite6);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		fileComposite6.setLayout(rowLayout);

		m_agentUser = new Button(fileComposite6, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "agentuser", m_agentUser);
		m_agentUser.setText(Messages.getString("agent.user"));
		m_agentUser.setLayoutData(
				new RowData(350, SizeConstant.SIZE_BUTTON_HEIGHT));
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

		Composite fileComposite7 = new Composite(fileEffectiveUserGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "specifyuser", fileComposite7);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		fileComposite7.setLayout(rowLayout);

		m_specifyUser = new Button(fileComposite7, SWT.RADIO);
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

		this.m_user = new Text(fileComposite7, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "user", m_user);
		this.m_user.setLayoutData(new RowData(300, SizeConstant.SIZE_TEXT_HEIGHT));
		this.m_user.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		this.m_user.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

	}

	/**
	 * 更新処理
	 *
	 */
	@Override
	public void update(){
		// 必須項目を明示
		if("".equals(this.m_srcScope.getText())){
			this.m_srcScope.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_srcScope.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_srcFile.getText())){
			this.m_srcFile.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_srcFile.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(m_specifyUser.getSelection() && "".equals(this.m_user.getText())){
			this.m_user.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_user.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_destScope.getText())){
			this.m_destScope.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_destScope.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.m_destDirectory.getText())){
			this.m_destDirectory.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_destDirectory.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * ジョブファイル転送情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobFileInfo
	 */
	public void reflectFileInfo() {

		// 初期値
		m_srcScope.setText("");
		m_srcFile.setText("");
		m_destScope.setText("");
		m_destDirectory.setText("");
		m_allNode.setSelection(true);
		m_compressionCondition.setSelection(false);
		m_checkFileCondition.setSelection(false);
		m_agentUser.setSelection(true);
		m_specifyUser.setSelection(false);
		m_user.setText("");
		m_user.setEnabled(false);

		if (m_jobFileInfo != null) {
			//転送元スコープ設定
			m_srcFacilityPath = m_jobFileInfo.getSrcScope();
			m_srcFacilityId = m_jobFileInfo.getSrcFacilityID();
			if (m_srcFacilityPath != null && m_srcFacilityPath.length() > 0) {
				m_srcScope.setText(m_srcFacilityPath);
			}
			//転送元ファイル設定
			if (m_jobFileInfo.getSrcFile() != null
					&& m_jobFileInfo.getSrcFile().length() > 0) {
				m_srcFile.setText(m_jobFileInfo.getSrcFile());
			}
			//転送先スコープ設定
			m_destFacilityPath = m_jobFileInfo.getDestScope();
			m_destFacilityId = m_jobFileInfo.getDestFacilityID();
			if (m_destFacilityPath != null && m_destFacilityPath.length() > 0) {
				m_destScope.setText(m_destFacilityPath);
			}
			//転送先ファイル設定
			if (m_jobFileInfo.getDestDirectory() != null
					&& m_jobFileInfo.getDestDirectory().length() > 0) {
				m_destDirectory.setText(m_jobFileInfo.getDestDirectory());
			}
			//処理方法設定
			if (m_jobFileInfo.getProcessingMethod() == ProcessingMethodConstant.TYPE_ALL_NODE) {
				m_allNode.setSelection(true);
				m_oneNode.setSelection(false);
			} else {
				m_allNode.setSelection(false);
				m_oneNode.setSelection(true);
			}
			//ファイル圧縮
			m_compressionCondition.setSelection(
					YesNoConstant.typeToBoolean(m_jobFileInfo.getCompressionFlg()));
			//整合性チェック
			m_checkFileCondition.setSelection(
					YesNoConstant.typeToBoolean(m_jobFileInfo.getCheckFlg()));
			//ユーザー設定
			if (m_jobFileInfo.getSpecifyUser() == YesNoConstant.TYPE_YES) {
				m_specifyUser.setSelection(true);
				m_agentUser.setSelection(false);
				m_user.setEnabled(true);
			} else {
				m_specifyUser.setSelection(false);
				m_agentUser.setSelection(true);
				m_user.setEnabled(false);
			}
			if (m_jobFileInfo.getUser() != null && m_jobFileInfo.getUser().length() > 0) {
				m_user.setText(m_jobFileInfo.getUser());
			}
		}
	}

	/**
	 * ジョブファイル転送情報を設定します。
	 *
	 * @param jobFileInfo ジョブファイル転送情報
	 */
	public void setFileInfo(JobFileInfo jobFileInfo) {
		m_jobFileInfo = jobFileInfo;
	}

	/**
	 * ジョブファイル転送情報を返します。
	 *
	 * @return ジョブファイル転送情報
	 */
	public JobFileInfo getFileInfo() {
		return m_jobFileInfo;
	}

	/**
	 * コンポジットの情報から、ジョブファイル転送情報を作成する。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobFileInfo
	 */
	public ValidateResult createFileInfo() {
		ValidateResult result = null;

		//ファイル転送情報クラスのインスタンスを作成・取得
		m_jobFileInfo = new JobFileInfo();

		//転送元スコープ取得
		if (m_srcFacilityId != null && m_srcFacilityId.length() > 0) {
			m_jobFileInfo.setSrcFacilityID(m_srcFacilityId);
			m_jobFileInfo.setSrcScope(m_srcFacilityPath);
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("forward.source") +
					Messages.getString("message.hinemos.3"));
			return result;
		}

		//転送元ファイル
		if (m_srcFile.getText().length() > 0) {
			m_jobFileInfo.setSrcFile(m_srcFile.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("forward.source") +
					Messages.getString("message.job.45"));
			return result;
		}

		//転送元作業ディレクトリ
		m_jobFileInfo.setSrcWorkDir("");

		//転送先スコープ取得
		if (m_destFacilityId != null && m_destFacilityId.length() > 0) {
			m_jobFileInfo.setDestFacilityID(m_destFacilityId);
			m_jobFileInfo.setDestScope(m_destFacilityPath);
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("forward.destination") +
					Messages.getString("message.hinemos.3"));
			return result;
		}

		//転送先ディレクトリ
		if (m_destDirectory.getText().length() > 0) {
			m_jobFileInfo.setDestDirectory(m_destDirectory.getText());

			// 転送先ディレクトリが指定されていない場合
		} else if (m_destDirectory.getText().length() <= 0) {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("forward.destination") +
					Messages.getString("message.job.46"));
			return result;

		}


		//転送先作業ディレクトリ
		m_jobFileInfo.setDestWorkDir("");

		//処理方法取得
		if (m_allNode.getSelection()) {
			m_jobFileInfo.setProcessingMethod(
					ProcessingMethodConstant.TYPE_ALL_NODE);
		} else {
			m_jobFileInfo.setProcessingMethod(
					ProcessingMethodConstant.TYPE_RETRY);
		}

		//ファイル圧縮
		m_jobFileInfo.setCompressionFlg(
				YesNoConstant.booleanToType(m_compressionCondition.getSelection()));

		//整合性チェック
		m_jobFileInfo.setCheckFlg(
				YesNoConstant.booleanToType(m_checkFileCondition.getSelection()));

		//ユーザー取得
		if (m_agentUser.getSelection()) {
			m_jobFileInfo.setSpecifyUser(YesNoConstant.TYPE_NO);
		} else {
			if (m_user.getText().length() > 0) {
				m_jobFileInfo.setSpecifyUser(YesNoConstant.TYPE_YES);
				m_jobFileInfo.setUser(m_user.getText());
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
		this.m_destScope.setText("");
		this.m_destFacilityId = null;
		this.m_srcScope.setText("");
		this.m_srcFacilityId = null;
	}

	public void setManagerName(String managerName) {
		this.m_managerName = managerName;
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		m_srcScope.setEnabled(enabled);
		m_srcFile.setEnabled(enabled);
		m_destScope.setEnabled(enabled);
		m_destDirectory.setEnabled(enabled);
		m_agentUser.setEnabled(enabled);
		m_specifyUser.setEnabled(enabled);
		m_user.setEnabled(enabled);
		m_srcScopeSelect.setEnabled(enabled);
		m_destScopeSelect.setEnabled(enabled);
		m_allNode.setEnabled(enabled);
		m_oneNode.setEnabled(enabled);
		m_compressionCondition.setEnabled(enabled);
		m_checkFileCondition.setEnabled(enabled);
	}
}
