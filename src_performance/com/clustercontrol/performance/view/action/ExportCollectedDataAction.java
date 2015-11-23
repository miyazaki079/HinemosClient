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

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.performance.bean.PerformanceStatusConstant;
import com.clustercontrol.performance.dialog.ExportDialog;
import com.clustercontrol.performance.view.PerformanceListView;
import com.clustercontrol.util.Messages;

/**
 * 収集済みデータのエクスポートを行うアクションクラス
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class ExportCollectedDataAction extends AbstractHandler implements IElementUpdater {
	public static final String ID = ExportCollectedDataAction.class.getName();

	/** ビュー */
	private IWorkbenchPart viewPart;
	private IWorkbenchWindow window;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.window = null;
		this.viewPart = null;
	}

	/**
	 * 性能[一覧]ビューで選択されている性能値をエクスポートします。
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == window || !isEnabled() ){
			return null;
		}

		// 選択アイテムの取得
		viewPart = HandlerUtil.getActivePart(event);

		// 呼び出し元のViewを持ってきます。
		IWorkbenchPage page = window.getActivePage();
		PerformanceListView listView = (PerformanceListView) page.findView(PerformanceListView.ID);

		// テーブルで選ばれている監視のIDを取得します。
		String managerName = listView.getSelectedManagerName();
		String monitorId = listView.getSelectedMonitorId();
		String monitorTypeId = listView.getSelectedMonitorTypeId();

		if (monitorId != null) {
			// マネージャからプロパティ情報を収集する
			ExportDialog exportDialog = new ExportDialog(this.viewPart.getSite().getShell(), managerName, monitorId, monitorTypeId);
			exportDialog.open();
		} else {

			String msg = Messages.getString("performance.not.selected");
			MessageDialog.openError(null, Messages.getString("error"),
					msg);
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// page may not start at state restoring
		if( null != window ){
			IWorkbenchPage page = window.getActivePage();
			if( null != page ){
				IWorkbenchPart part = page.getActivePart();

				boolean editEnable = false;
				if(part instanceof PerformanceListView){
					// Enable button when 1 item is selected
					PerformanceListView view = (PerformanceListView)part;
					if(view.getStatus() == PerformanceStatusConstant.TYPE_RUNNING ||
							view.getStatus() == PerformanceStatusConstant.TYPE_STOP){
						editEnable = true;
					}
				}
				this.setBaseEnabled( editEnable );
			} else {
				this.setBaseEnabled(false);
			}
		}
	}
}
