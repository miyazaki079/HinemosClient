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

package com.clustercontrol.accesscontrol.composite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.accesscontrol.bean.RoleSettingTreeConstant;
import com.clustercontrol.accesscontrol.dialog.RoleSettingDialog;
import com.clustercontrol.accesscontrol.util.AccessEndpointWrapper;
import com.clustercontrol.accesscontrol.view.RoleSettingTreeView;
import com.clustercontrol.accesscontrol.viewer.RoleSettingTreeContentProvider;
import com.clustercontrol.accesscontrol.viewer.RoleSettingTreeLabelProvider;
import com.clustercontrol.accesscontrol.viewer.RoleSettingTreeViewer;
import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.UIManager;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.ws.access.InvalidRole_Exception;
import com.clustercontrol.ws.access.RoleInfo;
import com.clustercontrol.ws.accesscontrol.RoleTreeItem;

/**
 * ロールツリー用のコンポジットクラスです。
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class RoleSettingTreeComposite extends Composite {

	// ログ
	private static Log m_log = LogFactory.getLog( RoleSettingTreeComposite.class );

	/** ツリービューア */
	private RoleSettingTreeViewer m_viewer = null;
	/** ツリービュ */
	private RoleSettingTreeView m_view = null;
	/** 選択ジョブツリーアイテム */
	private RoleTreeItem m_selectItem = null;

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
	public RoleSettingTreeComposite(RoleSettingTreeView view , Composite parent, int style) {
		super(parent, style);
		this.m_view = view;
		initialize();
	}

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
	public RoleSettingTreeComposite(Composite parent, int style) {
		super(parent, style);

		initialize();
	}

	/**
	 * コンストラクタ
	 *
	 * @param parent 親コンポジット
	 * @param style スタイル
	 * @param selectItem 選択したノード
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public RoleSettingTreeComposite(Composite parent, int style, RoleTreeItem selectItem) {
		super(parent, style);

		m_selectItem = selectItem;
		initialize();
	}

	/**
	 * コンポジットを構築します。
	 */
	private void initialize() {
		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Tree tree = new Tree(this, SWT.SINGLE | SWT.BORDER);
		WidgetTestUtil.setTestId(this, null, tree);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tree.setLayoutData(gridData);

		m_viewer = new RoleSettingTreeViewer(tree);
		m_viewer.setContentProvider(new RoleSettingTreeContentProvider());
		m_viewer.setLabelProvider(new RoleSettingTreeLabelProvider());

		// 選択アイテム取得イベント定義
		m_viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();
				m_selectItem = (RoleTreeItem) selection.getFirstElement();
			}
		});

		// ダブルクリックしたらジョブを開く
		m_viewer.addDoubleClickListener(
				new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent event) {
						StructuredSelection selection = (StructuredSelection) event.getSelection();
						RoleTreeItem item = (RoleTreeItem) selection.getFirstElement();
						Object data = item.getData();
						if (data instanceof RoleInfo
								&& !((RoleInfo)data).getId().equals(RoleSettingTreeConstant.ROOT_ID)
								&& !((RoleInfo)data).getId().equals(RoleSettingTreeConstant.MANAGER)) {
							RoleTreeItem manager = RoleSettingTreeView.getManager(item);
							RoleInfo info = (RoleInfo)manager.getData();
							String managerName = info.getName();
							RoleSettingDialog dialog = new RoleSettingDialog(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
									managerName,
									((RoleInfo)data).getId());
							//ダイアログ表示
							if (dialog.open() == IDialogConstants.OK_ID) {
								m_view.update();
							}
						} else {
							return;
						}
					}
				});

		update();
	}

	/**
	 * このコンポジットが利用するツリービューアを返します。
	 *
	 * @return ツリービューア
	 */
	public RoleSettingTreeViewer getTreeViewer() {
		return m_viewer;
	}

	/**
	 * このコンポジットが利用するツリーを返します。
	 *
	 * @return ツリー
	 */
	public Tree getTree() {
		return m_viewer.getTree();
	}

	/**
	 * ツリービューアーを更新します。<BR>
	 * ツリー情報を取得し、ツリービューアーにセットします。
	 * <p>
	 * <ol>
	 * <li>ロールツリー情報を取得します。</li>
	 * <li>ツリービューアーにロールツリー情報をセットします。</li>
	 * </ol>
	 *
	 */
	@Override
	public void update() {
		RoleTreeItem tree = null;

		Map<String, RoleTreeItem> dispDataMap= new ConcurrentHashMap<String, RoleTreeItem>();
		Map<String, String> errorMsgs = new ConcurrentHashMap<>();

		//　ロール一覧情報取得
		for(String managerName : EndpointManager.getActiveManagerSet()) {
			try {
				AccessEndpointWrapper wrapper = AccessEndpointWrapper.getWrapper(managerName);
				tree = wrapper.getRoleTree();
				dispDataMap.put(managerName, tree);
			} catch (InvalidRole_Exception e) {
				// アクセス権なしの場合、エラーダイアログを表示する
				errorMsgs.put( managerName, Messages.getString("message.accesscontrol.16") );
			} catch (Exception e) {
				m_log.warn("update() getJobTree, " + e.getMessage(), e);
				errorMsgs.put( managerName, Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
			}

		}

		//メッセージ表示
		if( 0 < errorMsgs.size() ){
			UIManager.showMessageBox(errorMsgs, true);
		}

		m_selectItem = null;

		//ツリーの再構築
		RoleTreeItem newTree = new RoleTreeItem();
		RoleTreeItem roleTree = new RoleTreeItem();

		for(Map.Entry<String, RoleTreeItem> map : dispDataMap.entrySet()) {
			RoleTreeItem orgTree = map.getValue();
			//トップ
			if(newTree.getData() == null) {
				newTree.setData(orgTree.getData());
			}

			//"ロール"
			if(newTree.getChildren().isEmpty() == false) {
				roleTree = newTree.getChildren().get(0);
			} else {
				roleTree.setData(orgTree.getChildren().get(0).getData());
			}

			//マネージャ
			RoleInfo role = new RoleInfo();
			role.setId(RoleSettingTreeConstant.MANAGER);
			role.setName(map.getKey());
			RoleTreeItem managerTree = new RoleTreeItem();
			managerTree.setData(role);

			//詳細設定
			for(RoleTreeItem t : orgTree.getChildren().get(0).getChildren()) {
				managerTree.getChildren().add(t);
				t.setParent(managerTree);
			}
			roleTree.getChildren().add(managerTree);
			managerTree.setParent(roleTree);

			if(newTree.getChildren().isEmpty()) {
				newTree.getChildren().add(roleTree);
			}
		}
		m_viewer.setInput(newTree);
		m_viewer.expandToLevel(2);
	}

	/**
	 * 選択ロールツリーアイテムを返します。
	 *
	 * @return ロールツリーアイテム
	 */
	public RoleTreeItem getSelectItem() {
		return m_selectItem;
	}

	/**
	 * 選択ロールツリーアイテムを設定
	 *
	 * @param item ロールツリーアイテム
	 */
	public void setSelectItem(RoleTreeItem item) {
		m_selectItem = item;
	}
}
