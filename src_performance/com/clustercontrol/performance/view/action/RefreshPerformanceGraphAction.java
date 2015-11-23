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

package com.clustercontrol.performance.view.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.performance.view.PerformanceGraphView;

/**
 * 性能グラフビューの更新アクション<BR>
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class RefreshPerformanceGraphAction extends AbstractHandler {
	private static Log log = LogFactory.getLog(RefreshPerformanceGraphAction.class);

	public static final String ID = RefreshPerformanceGraphAction.class.getName();

	/**
	 * 更新アクション
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow( event ).getActivePage();

		// 同じクラスのViewを複数作るため、IDで特定不可
		// アクティブなviewを取得する
		String viewTitle = page.getActivePart().getTitle();
		IViewReference[] refs = page.getViewReferences();
		for (IViewReference ref : refs) {

			if (ref.getView(false) instanceof PerformanceGraphView) {
				PerformanceGraphView view = (PerformanceGraphView) ref.getView(false);
				if (view.getTitle().equals(viewTitle)) {
					log.debug("run() viewTitle = " + viewTitle);
					view.update();
				}
			}
		}
		return null;
	}
}
