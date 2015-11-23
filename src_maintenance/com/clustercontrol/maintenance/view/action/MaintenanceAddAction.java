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

package com.clustercontrol.maintenance.view.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.maintenance.dialog.MaintenanceDialog;
import com.clustercontrol.maintenance.view.MaintenanceListView;
import com.clustercontrol.util.EndpointManager;

/**
 * メンテナンス[一覧]ビューの作成アクションクラス<BR>
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class MaintenanceAddAction extends AbstractHandler {

	/** アクションID */
	public static final String ID = MaintenanceAddAction.class.getName();

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
	 * メンテナンス一覧ダイアログを表示します
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.viewPart = HandlerUtil.getActivePart(event);
		
		String managerName = EndpointManager.getActiveManagerNameList().get(0);
		MaintenanceDialog dialog = new MaintenanceDialog(this.viewPart.getSite().getShell(),
				managerName, null, PropertyDefineConstant.MODE_ADD);
		dialog.open();
		// ビューの更新
		MaintenanceListView view = (MaintenanceListView) this.viewPart.getAdapter(MaintenanceListView.class);
		view.update();
		return null;
	}

}
