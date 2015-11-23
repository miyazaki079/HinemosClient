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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.performance.action.GetPerformanceDataSettings;
import com.clustercontrol.performance.bean.PerformanceStatusConstant;
import com.clustercontrol.performance.view.PerformanceDataSettingCache;
import com.clustercontrol.performance.view.PerformanceGraphView;
import com.clustercontrol.performance.view.PerformanceListView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.collector.PerformanceDataSettings;

/**
 * 性能[一覧]ビューから、性能[グラフ]ビューを追加するアクションクラス
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class AddGraphAction extends AbstractHandler implements IElementUpdater {

	// ログ
	private static Log m_log = LogFactory.getLog( AddGraphAction.class );

	public static final String ID = AddGraphAction.class.getName();

	private IWorkbenchWindow window;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.window = null;
	}

	/**
	 * 性能[グラフ]ビューを追加します。
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

		// 収集中リストのビューを取得します。
		IWorkbenchPage page = this.window.getActivePage();

		PerformanceListView listView = (PerformanceListView) page.findView(PerformanceListView.ID);

		String monitorId = listView.getSelectedMonitorId();
		String managerName = listView.getSelectedManagerName();

		if (monitorId != null) {
			PerformanceDataSettings perfDataSettings = GetPerformanceDataSettings.getPerformanceGraphInfo(managerName, monitorId);

			// グラフのヘッダ情報を取得成功
			if(perfDataSettings != null){
				// 収集データがなければViewを開かずに終了する
				if(perfDataSettings.getOldestDate() == null || perfDataSettings.getOldestDate() == 0){
					MessageDialog.openError(
							null,
							Messages.getString("failed"),
							Messages.getString("message.collection.6"));
					return null;
				}
				PerformanceDataSettingCache.set(managerName, monitorId, perfDataSettings);

				try {
					//セカンダリーIDにコロンがあるとエラーになるので置き換え
					managerName = managerName.replace(":", "@");
					PerformanceGraphView graphView = (PerformanceGraphView) page.showView(
							PerformanceGraphView.ID, managerName + "," + monitorId,
							IWorkbenchPage.VIEW_ACTIVATE);
					graphView.setFocus();
					graphView.update(); // This is necessary for RAP(Browser embedded chart)
				} catch (PartInitException e) {
					m_log.warn("run(), " + e.getMessage(), e);

					MessageDialog.openError(
							null,
							Messages.getString("failed"),
							Messages.getString("message.collection.9"));
					return null;
				}
			}
			// グラフのヘッダ情報を取得失敗
			else{
				MessageDialog.openError(
						null,
						Messages.getString("failed"),
						Messages.getString("message.collection.9"));
			}

		} else {
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.collection.10"));

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
			}
		}
	}
}
