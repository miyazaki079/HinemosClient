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

package com.clustercontrol.accesscontrol.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.clustercontrol.accesscontrol.bean.PrivilegeConstant.SystemPrivilegeMode;
import com.clustercontrol.accesscontrol.util.AccessEndpointWrapper;
import com.clustercontrol.accesscontrol.util.SystemPrivilegePropertyUtil;
import com.clustercontrol.dialog.CommonDialog;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.ws.access.InvalidRole_Exception;
import com.clustercontrol.ws.access.InvalidSetting_Exception;
import com.clustercontrol.ws.access.RoleInfo;
import com.clustercontrol.ws.access.SystemPrivilegeInfo;
import com.clustercontrol.ws.access.UnEditableRole_Exception;
import com.clustercontrol.ws.access.UserDuplicate_Exception;

/**
 * アカウント[システム権限設定]ダイアログクラスです。
 *
 * @version 4.0.0
 * @since 2.0.0
 */
public class SystemPrivilegeDialog extends CommonDialog {

	// ログ
	private static Log m_log = LogFactory.getLog( SystemPrivilegeDialog.class );

	/**　マネージャ名 */
	private String managerName = "";

	/** ロールID */
	private String roleId = "";

	/** 全システム権限一覧　リストボックス */
	private List listNotRoleSystemPrivilege = null;

	/** 付与システム権限一覧　リストボックス */
	private List listRoleSystemPrivilege = null;

	/** 権限付与　ボタン */
	private Button buttonRoleSystemPrivilege = null;

	/** 権限解除　ボタン */
	private Button buttonNotRoleSystemPrivilege = null;

	/** ソート（権限）　ボタン */
	private Button buttonSortRole = null;

	/** ソート（機能）　ボタン */
	private Button buttonSortFunction = null;

	/** カラム数 */
	public static final int WIDTH	 = 15;

	/** カラム数（ラベル）。 */
	public static final int WIDTH_LABEL = 4;

	/** カラム数（テキスト）。 */
	public static final int WIDTH_TEXT = 10;

	/** 入力値を保持するオブジェクト。 */
	private RoleInfo inputData = null;

	/**
	 * コンストラクタ
	 *
	 * @param parent 親シェル
	 * @param managerName マネージャ名
	 * @param uid ユーザID
	 */
	public SystemPrivilegeDialog(Shell parent, String managerName, String roleId) {
		super(parent);

		this.managerName = managerName;
		this.roleId = roleId;
	}

	/**
	 * ダイアログの初期サイズを返します。
	 *
	 * @return 初期サイズ
	 *
	 * @see org.eclipse.jface.window.Window#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(600, 600);
	}

	/**
	 * 権限又は機能で、ソートする。
	 */
	public List sortFunctionPrivilege(List sortItem, Boolean sortOrder) {
		String[] items = sortItem.getItems();
		String keyValue = null;
		Map<String, String> keys_Map = new HashMap<String, String>();
		ArrayList<String> item_keys = new ArrayList<String>();
		//itemから、KEYを生成して、テーブルにソート優先順位の指定別に格納
		for (String value : items) {
			//画面表示の文言別に、KEY用のKEY項目を作成する。
			keyValue = SystemPrivilegePropertyUtil.getFunctionPrivilege(value);
			String func = SystemPrivilegePropertyUtil.getSystemPrivilegeInfo(keyValue).getSystemFunction();
			String priv = SystemPrivilegePropertyUtil.getSystemPrivilegeInfo(keyValue).getSystemPrivilege();
			String key = "";
			if (sortOrder) {
				key = func + priv;
			} else {
				key = priv + func;
			}
			item_keys.add(key);
			keys_Map.put(key, value);
		}
		Collections.sort(item_keys);
		sortItem.removeAll();
		//sort済のKEY情報順に、mapから情報をitemに設定する。
		for (String sortKey : item_keys) {
			sortItem.add(keys_Map.get(sortKey));
		}
		return sortItem;
	}

	/**
	 * ダイアログエリアを生成します。
	 *
	 * @param parent 親コンポジット
	 *
	 * @see com.clustercontrol.dialog.CommonDialog#customizeDialog(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void customizeDialog(Composite parent) {
		Shell shell = this.getShell();

		// タイトル
		shell.setText(Messages
				.getString("dialog.accesscontrol.system.privilege.setting"));

		// ロール情報の取得
		RoleInfo info = null;
		try {
			AccessEndpointWrapper wrapper = AccessEndpointWrapper.getWrapper(this.managerName);
			info = wrapper.getRoleInfo(this.roleId);
		} catch (InvalidRole_Exception e) {
			MessageDialog.openInformation(null, Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));
		} catch (Exception e) {
			m_log.warn("customizeDialog(), " + e.getMessage(), e);
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}

		// レイアウト
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.numColumns = WIDTH;
		parent.setLayout(layout);

		/*
		 * ロール名
		 */
		// ラベル
		Label label = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, label);
		GridData gridData = new GridData();
		gridData.horizontalSpan = WIDTH;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("role.name") + " : " + info.getName());

		/*
		 * 全システム権限一覧用コンポジット
		 */
		Composite compositeNotRole = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "all", compositeNotRole);
		layout = new GridLayout(1, true);
		layout.numColumns = 1;
		compositeNotRole.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 6;
		gridData.verticalSpan = 2;
		compositeNotRole.setLayoutData(gridData);

		// 全システム権限一覧 ラベル
		label = new Label(compositeNotRole, SWT.NONE);
		WidgetTestUtil.setTestId(this, "systemprivilegelist", label);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("SystemPrivilegeDialog.not_role_system_privilege"));

		// 全システム権限一覧 リスト
		this.listNotRoleSystemPrivilege = new List(compositeNotRole, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		WidgetTestUtil.setTestId(this, "all", listNotRoleSystemPrivilege);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = this.listNotRoleSystemPrivilege.getItemHeight() * 12;
		this.listNotRoleSystemPrivilege.setLayoutData(gridData);

		/*
		 * 操作ボタン用コンポジット
		 */
		Composite compositeButton = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "button", compositeButton);
		layout = new GridLayout(1, true);
		layout.numColumns = 1;
		compositeButton.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		compositeButton.setLayoutData(gridData);

		// 空
		label = new Label(compositeButton, SWT.NONE);
		WidgetTestUtil.setTestId(this, "blank", label);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);

		// 権限付与ボタン
		@SuppressWarnings("unused")
		Label dummy = new Label(compositeButton, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy", dummy);
		this.buttonRoleSystemPrivilege = this.createButton(compositeButton, Messages.getString("SystemPrivilegeDialog.role_system_privilege_button"));
		WidgetTestUtil.setTestId(this, "assign", buttonRoleSystemPrivilege);
		this.buttonRoleSystemPrivilege.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] items = listNotRoleSystemPrivilege.getSelection();
				for (String item : items) {
					listNotRoleSystemPrivilege.remove(item);
					listRoleSystemPrivilege.add(item);
				}
			}
		});

		// 権限解除ボタン
		dummy = new Label(compositeButton, SWT.NONE);
		WidgetTestUtil.setTestId(this, "notrolesystemprivilege", dummy);
		this.buttonNotRoleSystemPrivilege = this.createButton(compositeButton, Messages.getString("SystemPrivilegeDialog.not_role_system_privilege_button"));
		WidgetTestUtil.setTestId(this, "unassign", buttonNotRoleSystemPrivilege);
		this.buttonNotRoleSystemPrivilege.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] items = listRoleSystemPrivilege.getSelection();
				for (String item : items) {
					listRoleSystemPrivilege.remove(item);
					listNotRoleSystemPrivilege.add(item);
				}
			}
		});

		// ソート（機能）ボタン
		dummy = new Label(compositeButton, SWT.NONE);
		WidgetTestUtil.setTestId(this, "buttonsortfunction", dummy);
		this.buttonSortFunction = this.createButton(compositeButton, Messages.getString("SystemPrivilegeDialog.sort_function_button"));
		WidgetTestUtil.setTestId(this, "sortfunction", buttonSortFunction);
		this.buttonSortFunction.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				listRoleSystemPrivilege = sortFunctionPrivilege(listRoleSystemPrivilege, true);
				listNotRoleSystemPrivilege = sortFunctionPrivilege(listNotRoleSystemPrivilege, true);
			}
		});

		// ソート（権限）ボタン
		dummy = new Label(compositeButton, SWT.NONE);
		WidgetTestUtil.setTestId(this, "buttonsortrole", dummy);
		this.buttonSortRole = this.createButton(compositeButton, Messages.getString("SystemPrivilegeDialog.sort_privilege_button"));
		WidgetTestUtil.setTestId(this, "sortrole", buttonSortRole);
		this.buttonSortRole.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				listRoleSystemPrivilege = sortFunctionPrivilege(listRoleSystemPrivilege, false);
				listNotRoleSystemPrivilege = sortFunctionPrivilege(listNotRoleSystemPrivilege, false);
			}
		});

		// 空
		label = new Label(compositeButton, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, label);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);

		/*
		 * 付与システム権限一覧用コンポジット
		 */
		Composite compositeRole = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, compositeRole);
		layout = new GridLayout(1, true);
		layout.numColumns = 1;
		compositeRole.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 6;
		gridData.verticalSpan = 2;
		compositeRole.setLayoutData(gridData);

		// 付与システム権限一覧 ラベル
		label = new Label(compositeRole, SWT.NONE);
		WidgetTestUtil.setTestId(this, "systemprivilege", label);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("SystemPrivilegeDialog.role_system_privilege"));

		// 付与システム権限一覧 リスト
		this.listRoleSystemPrivilege = new List(compositeRole, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		WidgetTestUtil.setTestId(this, null, listRoleSystemPrivilege);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = this.listRoleSystemPrivilege.getItemHeight() * 12;
		this.listRoleSystemPrivilege.setLayoutData(gridData);

		// サイズを最適化
		// グリッドレイアウトを用いた場合、こうしないと横幅が画面いっぱいになります。
		shell.pack();
		shell.setSize(new Point(550, shell.getSize().y));

		// 画面中央に
		Display display = shell.getDisplay();
		shell.setLocation((display.getBounds().width - shell.getSize().x) / 2,
				(display.getBounds().height - shell.getSize().y) / 2);

		this.setInputData(this.managerName, info);
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
		// 入力値生成
		this.inputData = this.createInputData();

		if (this.inputData != null) {
			return super.validate();
		} else {
			return null;
		}
	}

	/**
	 * 入力値をマネージャに登録します。
	 *
	 * @return true：正常、false：異常
	 *
	 * @see com.clustercontrol.dialog.CommonDialog#action()
	 */
	@Override
	protected boolean action() {
		boolean result = false;

		RoleInfo roleInfo = this.inputData;
		if(roleInfo == null){
			return result;
		}

		try {
			AccessEndpointWrapper wrapper = AccessEndpointWrapper.getWrapper(this.managerName);
			wrapper.replaceSystemPrivilegeRole(this.roleId, roleInfo.getSystemPrivilegeList());
			result = true;

			Object[] arg = {this.managerName};
			// 完了メッセージ
			MessageDialog.openInformation(
					null,
					Messages.getString("successful"),
					Messages.getString("message.accesscontrol.47", arg));

		} catch (UserDuplicate_Exception e) {
			//ロールID取得
			String args[] = { roleInfo.getId() };

			// ロールIDが重複している場合、エラーダイアログを表示する
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.accesscontrol.48", args));

		} catch (InvalidSetting_Exception e) {
			// 「リポジトリ - 参照」が登録するシステム権限に含まれていない
			String args[] = {
					Messages.getString("repository.read", Locale.getDefault())
			};

			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.accesscontrol.51", args));


		} catch (Exception e) {
			String errMessage = "";
			if (e instanceof InvalidRole_Exception) {
				// 権限なし
				MessageDialog.openInformation(null, Messages.getString("message"),
						Messages.getString("message.accesscontrol.16"));
			} else if (e instanceof UnEditableRole_Exception) {
				// システム権限の変更なロールの場合はエラー（ADMINISTRATORS）
				MessageDialog.openInformation(null, Messages.getString("message"),
						Messages.getString("message.accesscontrol.44"));
			} else {
				errMessage = ", " + e.getMessage();
			}
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.accesscontrol.48") + errMessage);

		}


		return result;
	}

	/**
	 * ロール用プロパティを設定します。
	 *
	 * @param roleInfo 設定値として用いるロール情報
	 */
	protected void setInputData(String managerName, RoleInfo roleInfo) {

		this.inputData = roleInfo;

		// 各項目に反映

		java.util.List<String> allSystemPrivilegeList = null;
		java.util.List<SystemPrivilegeInfo> roleSystemPrivilegeKeyList = null;
		// 全システム権限を取得
		allSystemPrivilegeList = SystemPrivilegePropertyUtil.getSystemPrivilegeNameList();
		//TODO 保留！！！この修正では最初の時点でのソートしかできない。以降はSWTのオブジェクトを直接いじっているように見える
		java.util.Collections.sort(allSystemPrivilegeList);
		// 付与システム権限を取得
		try {
			AccessEndpointWrapper wrapper = AccessEndpointWrapper.getWrapper(managerName);
			roleSystemPrivilegeKeyList = wrapper.getSystemPrivilegeInfoListByRoleId(roleId);
		} catch (InvalidRole_Exception e) {
			// 権限なし
			MessageDialog.openInformation(null, Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));

		} catch (Exception e) {
			// 上記以外の例外
			m_log.warn("getOwnUserList(), " + e.getMessage(), e);
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		java.util.List<String> roleSystemPrivilegeValueList
		= getSystemPrivilegeValueList(roleSystemPrivilegeKeyList);
		// リストを振り分ける
		for (String SystemPrivilege : allSystemPrivilegeList) {
			if (roleSystemPrivilegeValueList.contains(SystemPrivilege)) {
				this.listRoleSystemPrivilege.add(SystemPrivilege);
			} else {
				this.listNotRoleSystemPrivilege.add(SystemPrivilege);
			}
		}
	}

	/**
	 * 入力値を設定したロール情報を返します。<BR>
	 * 入力値チェックを行い、不正な場合は<code>null</code>を返します。
	 *
	 * @return ロール情報
	 */
	private RoleInfo createInputData() {
		RoleInfo info = this.inputData;

		// 付与システム権限一覧からシステム権限情報を取得する
		java.util.List<SystemPrivilegeInfo> systemPrivilegeList = this.inputData.getSystemPrivilegeList();
		if (systemPrivilegeList == null) {
			systemPrivilegeList = new ArrayList<SystemPrivilegeInfo>();
		}

		java.util.List<SystemPrivilegeInfo> roleSystemPrivilegeList = info.getSystemPrivilegeList();
		roleSystemPrivilegeList.clear();
		if (this.listRoleSystemPrivilege.getItemCount() > 0) {
			roleSystemPrivilegeList.addAll(getSystemPrivilegeKeyList(Arrays.asList(this.listRoleSystemPrivilege.getItems())));
		}

		return info;
	}

	/**
	 * ＯＫボタンのテキストを返します。
	 *
	 * @return ＯＫボタンのテキスト
	 */
	@Override
	protected String getOkButtonText() {
		return Messages.getString("setup");
	}

	/**
	 * キャンセルボタンのテキストを返します。
	 *
	 * @return キャンセルボタンのテキスト
	 */
	@Override
	protected String getCancelButtonText() {
		return Messages.getString("cancel"); //$NON-NLS-1$
	}

	/**
	 * 共通のボタンを生成します。
	 *
	 * @param parent
	 *            親のコンポジット
	 * @param label
	 *            ボタンのラベル
	 * @return 生成されたボタン
	 */
	private Button createButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, button);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		button.setLayoutData(gridData);

		button.setText(label);

		return button;
	}

	/**
	 * 表示名称リストからシステム権限リストを返す
	 *
	 * @param beforeList 表示名称リスト
	 * @return システム権限リスト
	 */
	private java.util.List<SystemPrivilegeInfo> getSystemPrivilegeKeyList(java.util.List<String> beforeList) {
		java.util.List<SystemPrivilegeInfo> afterList = new ArrayList<SystemPrivilegeInfo>();
		for (String beforeStr : beforeList) {
			String key = SystemPrivilegePropertyUtil.getFunctionPrivilege(beforeStr);
			// 画面変更前の仮対応
			SystemPrivilegeInfo info = new SystemPrivilegeInfo();
			String regexp = "(.+?)(" + SystemPrivilegeMode.READ.name() + ")";
			Pattern pattern = Pattern.compile(regexp);
			Matcher matcher = pattern.matcher(key);
			if (matcher.find()) {
				info.setSystemFunction(matcher.group(1));
				info.setSystemPrivilege(SystemPrivilegeMode.READ.name());
				afterList.add(info);
				continue;
			}
			regexp = "(.+?)(" + SystemPrivilegeMode.MODIFY.name() + ")";
			pattern = Pattern.compile(regexp);
			matcher = pattern.matcher(key);
			if (matcher.find()) {
				info.setSystemFunction(matcher.group(1));
				info.setSystemPrivilege(SystemPrivilegeMode.MODIFY.name());
				afterList.add(info);
				continue;
			}
			regexp = "(.+?)(" + SystemPrivilegeMode.ADD.name() + ")";
			pattern = Pattern.compile(regexp);
			matcher = pattern.matcher(key);
			if (matcher.find()) {
				info.setSystemFunction(matcher.group(1));
				info.setSystemPrivilege(SystemPrivilegeMode.ADD.name());
				afterList.add(info);
				continue;
			}
			regexp = "(.+?)(" + SystemPrivilegeMode.EXEC.name() + ")";
			pattern = Pattern.compile(regexp);
			matcher = pattern.matcher(key);
			if (matcher.find()) {
				info.setSystemFunction(matcher.group(1));
				info.setSystemPrivilege(SystemPrivilegeMode.EXEC.name());
				afterList.add(info);
				continue;
			}
		}
		return afterList;
	}

	/**
	 * システム権限リストから表示名称リストを返す
	 *
	 * @param beforeList システム権限リスト
	 * @return 表示名称リスト
	 */
	private java.util.List<String> getSystemPrivilegeValueList(java.util.List<SystemPrivilegeInfo> beforeList) {
		java.util.List<String> afterList = new ArrayList<String>();
		for (SystemPrivilegeInfo systemPrivilegeInfo : beforeList) {
			afterList.add(SystemPrivilegePropertyUtil.getSystemPrivilegeName(systemPrivilegeInfo.getSystemFunction(), systemPrivilegeInfo.getSystemPrivilege()));
		}
		return afterList;
	}
}
