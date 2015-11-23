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

package com.clustercontrol.notify.mail.view.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.notify.mail.view.MailTemplateListView;

/**
 * メールテンプレート[一覧]ビューの更新アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class MailTemplateRefreshAction extends AbstractHandler {

	/** アクションID */
	public static final String ID = MailTemplateRefreshAction.class.getName();

	/** ビュー */
	private IWorkbenchPart viewPart;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// ビューの更新
		this.viewPart = HandlerUtil.getActivePart(event);
		MailTemplateListView view = (MailTemplateListView) this.viewPart.getAdapter(MailTemplateListView.class);
		view.update();
		return null;
	}
}
