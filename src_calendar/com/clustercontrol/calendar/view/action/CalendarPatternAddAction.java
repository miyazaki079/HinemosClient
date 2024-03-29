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

package com.clustercontrol.calendar.view.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.calendar.dialog.CalendarPatternDialog;
import com.clustercontrol.calendar.view.CalendarPatternView;
import com.clustercontrol.util.EndpointManager;

/**
 * カレンダ[カレンダパターン]の作成・変更ダイアログによる、
 * カレンダパターン登録を行うクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 4.1.0
 */
public class CalendarPatternAddAction extends AbstractHandler {
	public static final String ID = CalendarPatternAddAction.class.getName();

	private IWorkbenchWindow window;
	private IWorkbenchPart viewPart;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
		this.window = null;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == this.window || !isEnabled() ){
			return null;
		}

		String managerName = EndpointManager.getActiveManagerNameList().get(0);
		
		this.viewPart = HandlerUtil.getActivePart(event);
		// ダイアログを生成
		CalendarPatternDialog dialog = new CalendarPatternDialog(this.viewPart.getSite().getShell(),
				managerName, null, PropertyDefineConstant.MODE_ADD);

		// ダイアログにて変更が選択された場合、入力内容をもって登録を行う。
		dialog.open();

		CalendarPatternView view = (CalendarPatternView) this.viewPart
				.getAdapter(CalendarPatternView.class);
		view.update();
		return null;
	}
}
