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

package com.clustercontrol.infra.composite;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.dialog.ScopeTreeDialog;
import com.clustercontrol.repository.FacilityPath;
import com.clustercontrol.repository.bean.FacilityConstant;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.ws.infra.InfraManagementInfo;
import com.clustercontrol.ws.monitor.MonitorInfo;
import com.clustercontrol.ws.repository.FacilityInfo;
import com.clustercontrol.ws.repository.FacilityTreeItem;
import com.clustercontrol.ClusterControlPlugin;

/**
 * スコープ基本情報コンポジットクラス<BR>
 * <dl>
 *  <dt>コンポジット</dt>
 *  <dd>「スコープ」 テキストボックス</dd>
 *  <dd>「参照」 ボタン</dd>
 * </dl>
 *
 * @version 4.0.0
 * @since 5.0.0
 */
public class InfraScopeComposite extends Composite {

	/** スコープ テキストボックス。 */
	private Text m_scope = null;

	/** 参照 ボタン。 */
	private Button btnRefer = null;

	/** 選択されたスコープのファシリティID。 */
	private String m_facilityId = null;

	/** 未登録ノード スコープを表示するかフラグ*/
	private boolean m_unregistered = false;

	/** オーナーロールID*/
	private String m_ownerRoleId = null;

	/** マネージャ名 */
	private String m_managerName = null;

//	/** 選択されたスコープのファシリティ名*/
//	private String m_facilityName = null;

	/**
	 * インスタンスを返します。
	 * <p>
	 * 初期処理を呼び出し、スコープのコンポジットを配置します。
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 *
	 * @see org.eclipse.swt.SWT
	 * @see #initialize()
	 */
	public InfraScopeComposite(Composite parent, int style) {
		super(parent, style);

		this.initialize();
	}

	/**
	 * コンポジットを配置します。
	 */
	private void initialize() {
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.setLayout(layout);

		// 変数として利用されるグリッドデータ
		GridData gridData = null;

		this.m_scope = new Text(this, SWT.BORDER | SWT.LEFT | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "scope", m_scope);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = GridData.FILL;
		this.m_scope.setLayoutData(gridData);
		this.m_scope.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		});

		// 参照ボタン
		btnRefer = new Button(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "refer", btnRefer);
		btnRefer.setText(Messages.getString("refer"));
		btnRefer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScopeTreeDialog dialog = new ScopeTreeDialog(null, m_managerName, m_ownerRoleId, false, m_unregistered);
				if (dialog.open() == IDialogConstants.OK_ID) {
					FacilityTreeItem item = dialog.getSelectItem();
					FacilityInfo info = item.getData();
					if( !info.getFacilityId().equals(InfraScopeComposite.this.m_facilityId) ){
						InfraScopeComposite.this.m_facilityId = info.getFacilityId();
						if (info.getFacilityType() == FacilityConstant.TYPE_NODE) {
							m_scope.setText(info.getFacilityName());
						} else {
							FacilityPath path = new FacilityPath(ClusterControlPlugin.getDefault().getSeparator());
							m_scope.setText(path.getPath(item));
						}
					}
				}
			}
		});

		update();
	}

	/**
	 * 更新処理
	 */
	@Override
	public void update(){
		super.update();
		// スコープが必須項目であることを明示
		if("".equals((this.m_scope.getText()).trim())){
			this.m_scope.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_scope.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * 引数で指定された情報の値を、各項目に設定します。
	 *
	 * @param info 設定値として用いる情報
	 *
	 * @see com.clustercontrol.infra.composite.MonitorBasicComposite#setInputData(MonitorInfo)
	 */
	public void setInputData(String managerName, InfraManagementInfo info) {
		this.m_managerName = managerName;
		setInputData( info );
	}
	public void setInputData(InfraManagementInfo info) {
		if(info != null){
			this.m_ownerRoleId = info.getOwnerRoleId();
			this.m_facilityId = info.getFacilityId();
			this.m_scope.setText(info.getScope());
		}
		// スコープが必須項目であることを明示
		this.update();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.m_scope.setEnabled(enabled);
		this.btnRefer.setEnabled(enabled);
	}

	public String getFacilityId() {
		return m_facilityId;
	}

	public void setOwnerRoleId(String managerName, String ownerRoleId) {
		this.m_managerName = managerName;
		setOwnerRoleId( ownerRoleId );
	}

	public void setOwnerRoleId(String ownerRoleId) {
		this.m_ownerRoleId = ownerRoleId;
		this.m_scope.setText("");
		this.m_facilityId = null;
	}

	public String getOwnerRoleId(){
		return this.m_ownerRoleId;
	}

	public String getScope(){
		return m_scope.getText();
	}
}
