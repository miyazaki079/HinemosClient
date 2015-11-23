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

package com.clustercontrol.monitor.view.action;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.monitor.dialog.MonitorTypeDialog;
import com.clustercontrol.monitor.view.MonitorListView;

/**
 * 監視[一覧]ビューの作成アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class MonitorAddAction extends AbstractHandler {

	/** アクションID */
	public static final String ID = MonitorAddAction.class.getName();

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
	 * 監視種別一覧ダイアログを表示します
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// ビューの取得
		this.viewPart = HandlerUtil.getActivePart(event);
		MonitorListView view = (MonitorListView) this.viewPart.getAdapter(MonitorListView.class);

		// 監視種別ダイアログを開く
		MonitorTypeDialog dialog = new MonitorTypeDialog(this.viewPart.getSite().getShell(), view);
		dialog.open();

		// ビューの更新
		view.update();

		return null;
	}
}
