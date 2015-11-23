/*

 Copyright (C) 2006 NTT DATA Corporation

 This program is free software; you can redistribute it and/or
 Modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation, version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 */

package com.clustercontrol.performance.view.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.performance.view.PerformanceListView;

/**
 * 性能[一覧]ビューのリスト表示をリフレッシュするアクションクラス
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class RefreshPerformanceListAction extends AbstractHandler {
	public static final String ID = RefreshPerformanceListAction.class.getName();

	/**
	 * 性能[一覧]ビューを更新します。
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// 収集中リストのビューを取得します。
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow( event ).getActivePage();

		// 収集中リストのリフレッシュをおこないます。
		PerformanceListView performanceListView = (PerformanceListView) page.findView(PerformanceListView.ID);

		performanceListView.update();
		return null;
	}
}
