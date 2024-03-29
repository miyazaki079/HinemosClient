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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.accesscontrol.dialog.UserDialog;
import com.clustercontrol.accesscontrol.view.RoleSettingTreeView;
import com.clustercontrol.accesscontrol.view.UserListView;
import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.ws.access.UserInfo;

/**
 * アクセス[ユーザ]ビューの「作成」のアクションクラス
 *
 * @version 5.0.0
 * @since 2.0.0
 */
public class UserAddAction extends AbstractHandler {
	/** アクションID */
	public static final String ID = UserAddAction.class.getName();
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
	 * アクセス[ユーザ]ビューの「作成」が押された場合に、<BR>
	 * アクセス[ユーザの作成・変更]ダイアログを表示し、ユーザを作成します。
	 * <p>
	 * <ol>
	 * <li>アクセス[ユーザの作成・変更]ダイアログを表示します。</li>
	 * <li>アクセス[ユーザ]ビューを更新します。</li>
	 * </ol>
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 * @see com.clustercontrol.accesscontrol.dialog.UserDialog
	 * @see com.clustercontrol.accesscontrol.view.UserListView
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);

		String managerName = EndpointManager.getActiveManagerNameList().get(0);
		
		// ダイアログを生成
		UserDialog dialog = new UserDialog(this.viewPart.getSite()
				.getShell(), managerName, new UserInfo(), false);

		// ダイアログにて変更が選択された場合、入力内容をもって登録を行う。
		if (dialog.open() == IDialogConstants.OK_ID) {
			UserListView userListView = (UserListView) this.viewPart
					.getAdapter(UserListView.class);
			userListView.update();

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
