/*

Copyright (C) 2013 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.accesscontrol.etc.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.accesscontrol.view.SystemPrivilegeListView;

/**
 * アカウント[システム権限]ビューを表示するアクションクラスです。
 * 
 * @version 5.0.0
 * @since 4.1.0
 */
public class SystemPrivilegePropertyAction extends AbstractHandler {

	/**
	 * 終了する際に呼ばれます。
	 * 
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	@Override
	public void dispose() {

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//アクティブページを手に入れる
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow( event ).getActivePage();

		//ビューを表示する
		try {
			page.showView(SystemPrivilegeListView.ID);
			IViewPart viewPart = page.findView(SystemPrivilegeListView.ID);
			SystemPrivilegeListView view = (SystemPrivilegeListView) viewPart
					.getAdapter(SystemPrivilegeListView.class);
			view.setFocus();
		} catch (PartInitException e) {
		}
		return null;
	}

}
