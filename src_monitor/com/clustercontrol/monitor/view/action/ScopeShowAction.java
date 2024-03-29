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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.view.ScopeListBaseView;

/**
 * スコープ階層ペイン表示切替を行うクライアント側アクションクラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ScopeShowAction extends AbstractHandler {

	private static Log m_log = LogFactory.getLog( ScopeShowAction.class );
	
	private IWorkbenchWindow window;
	/** ビュー */
	private IWorkbenchPart viewPart;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
		this.window = null;
	}

	/**
	 * スコープ階層ペインの表示／非表示を行います。
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == this.window || !isEnabled() ){
			return null;
		}

		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);

		ScopeListBaseView view = (ScopeListBaseView) viewPart
				.getAdapter(ScopeListBaseView.class);

		ICommandService commandService = (ICommandService)window.getService(ICommandService.class);
		String id = this.getClass().getName();
		m_log.trace("execute id=" + id);
		Command command = commandService.getCommand(id);
		boolean isChecked = !HandlerUtil.toggleCommandState(command);

		if (isChecked) {
			view.show();
		} else {
			view.hide();
		}

		view.setFocus();
		return null;
	}
}
