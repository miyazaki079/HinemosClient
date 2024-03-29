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

package com.clustercontrol.accesscontrol.composite.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.accesscontrol.bean.RoleSettingTreeConstant;
import com.clustercontrol.accesscontrol.composite.RoleSettingTreeComposite;
import com.clustercontrol.accesscontrol.view.RoleSettingTreeView;
import com.clustercontrol.accesscontrol.view.SystemPrivilegeListView;
import com.clustercontrol.ws.access.RoleInfo;
import com.clustercontrol.ws.accesscontrol.RoleTreeItem;

/**
 * ロールツリービューア用のSelectionChangedListenerです。
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class RoleSettingTreeSelectionChangedListener implements ISelectionChangedListener {

	private static Log m_log = LogFactory.getLog( RoleSettingTreeSelectionChangedListener.class );
	
	/** ロール設定ビューのツリービューア用のコンポジット */
	private RoleSettingTreeComposite m_tree;


	/**
	 * コンストラクタ
	 *
	 * @param tree ロール設定ビューのツリービューア用のコンポジット
	 */
	public RoleSettingTreeSelectionChangedListener(RoleSettingTreeComposite tree) {
		m_tree = tree;
	}

	/**
	 * 選択変更時に呼び出されます。<BR>
	 * ロール設定ツリービューのツリービューアを選択した際に、<BR>
	 * 選択したアイテムの内容でアクションの有効・無効を設定します。
	 *
	 * @param event 選択変更イベント
	 *
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		RoleTreeItem selectItem = null;

		// ロール設定ツリービューのインスタンスを取得
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(RoleSettingTreeView.ID);

		// 選択アイテムを取得
		StructuredSelection selection = (StructuredSelection) event.getSelection();

		if (viewPart != null && selection != null) {
			// 選択アイテムの情報を取得
			selectItem = (RoleTreeItem) selection.getFirstElement();
			RoleSettingTreeView view = (RoleSettingTreeView) viewPart.getAdapter(RoleSettingTreeView.class);

			//選択ツリーアイテムを設定
			m_tree.setSelectItem(selectItem);

			//ビューのアクションの有効/無効を設定
			//アイテムが選択されていない場合（ロールが追加された場合や選択中のロールががなくなった場合など）は強制的に無効化
			if (selectItem != null) {
				view.setEnabledAction(selectItem.getData(), selection);
			} else {
				view.setEnabledAction((Object)null, selection);
			}

			// システム権限ビューを表示する
			IViewPart systemPrivilegeViewPart = page.findView(SystemPrivilegeListView.ID);
			if (systemPrivilegeViewPart != null) {
				SystemPrivilegeListView systemPrivilegeListView
				= (SystemPrivilegeListView) systemPrivilegeViewPart.getAdapter(SystemPrivilegeListView.class);

				String managerName = getManager(selectItem);
				m_log.debug("selectionChanged managerName=" + managerName + ", selectItem=" + selectItem);
				if (managerName != null) {
					systemPrivilegeListView.update(managerName, selectItem.getData());
				} else {
					systemPrivilegeListView.update(null, null);
				}
			}
		}

	}
	
	private static String getManager(RoleTreeItem item) {
		if(item == null) {
			return null;
		}

		RoleTreeItem parent = item.getParent();
		RoleInfo role = null;
		if(parent == null || parent.getData() == null) {
			return null;
		} else if(parent.getData() instanceof RoleInfo) {
			role = (RoleInfo)(parent.getData());
		} else {
			return null;
		}
		if (role.getId() == RoleSettingTreeConstant.MANAGER) {
			return role.getName();
		}

		return getManager(item.getParent());
	}
}
