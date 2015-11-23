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

package com.clustercontrol.accesscontrol.view.action;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.accesscontrol.action.GetRoleListTableDefine;
import com.clustercontrol.accesscontrol.composite.RoleListComposite;
import com.clustercontrol.accesscontrol.dialog.RoleDialog;
import com.clustercontrol.accesscontrol.view.RoleListView;
import com.clustercontrol.accesscontrol.view.RoleSettingTreeView;
import com.clustercontrol.util.EndpointManager;

/**
 * アクセス[ロール]ビューの「作成」のアクションクラス
 *
 * @version 5.0.0
 * @since 2.0.0
 */
public class RoleAddAction extends AbstractHandler {
	/** アクションID */
	public static final String ID = RoleAddAction.class.getName();
	/** ビュー */
	private IWorkbenchPart viewPart;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
	}

	/**
	 * アクセス[ロール]ビューの「作成」が押された場合に、<BR>
	 * アクセス[ロールの作成・変更]ダイアログを表示し、ユーザを作成します。
	 * <p>
	 * <ol>
	 * <li>アクセス[ロールの作成・変更]ダイアログを表示します。</li>
	 * <li>アクセス[ロール]ビューを更新します。</li>
	 * </ol>
	 *
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 * @see com.clustercontrol.accesscontrol.dialog.UserDialog
	 * @see com.clustercontrol.accesscontrol.view.UserListView
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// ダイアログを生成
		this.viewPart = HandlerUtil.getActivePart(event);
		RoleListView roleListView = (RoleListView) this.viewPart
				.getAdapter(RoleListView.class);
		String managerName = EndpointManager.getActiveManagerNameList().get(0);

		RoleDialog dialog = new RoleDialog(this.viewPart.getSite().getShell(),
				managerName, null, false);

		// ダイアログにて変更が選択された場合、入力内容をもって登録を行う。
		if (dialog.open() == IDialogConstants.OK_ID) {
			roleListView.update();

			//アクティブページを手に入れる
			IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow( event ).getActivePage();

			//ツリービューを更新する
			IViewPart roleTreeViewPart = page.findView(RoleSettingTreeView.ID);
			if (roleTreeViewPart != null) {
				RoleSettingTreeView treeView = (RoleSettingTreeView) roleTreeViewPart
						.getAdapter(RoleSettingTreeView.class);
				treeView.update();
			}
		}
		return null;
	}
}
