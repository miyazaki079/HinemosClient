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

package com.clustercontrol.repository.view.action;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.repository.dialog.NodeCreateDialog;
import com.clustercontrol.repository.view.NodeListView;
import com.clustercontrol.util.EndpointManager;

/**
 * ノードの作成・変更ダイアログによる、ノード登録を行うクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class NodeAddAction extends AbstractHandler {
	public static final String ID = NodeAddAction.class.getName();

	//	 ----- instance フィールド ----- //

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
	 * @see org.eclipse.core.commands.IHandler#execute
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.viewPart = HandlerUtil.getActivePart(event);

		// ノード一覧より、選択されているノードのファシリティIDを取得
		NodeListView view = (NodeListView) this.viewPart
				.getAdapter(NodeListView.class);

		String managerName = EndpointManager.getActiveManagerNameList().get(0);

		// ダイアログを生成
		NodeCreateDialog dialog = new NodeCreateDialog(this.viewPart.getSite().getShell(), managerName, null, false);

		// ダイアログにて変更が選択された場合、入力内容をもって登録を行う。
		if( dialog.open() == IDialogConstants.OK_ID ){
			view = (NodeListView) this.viewPart.getAdapter( NodeListView.class );
			view.update();
		}
		return null;
	}
}
