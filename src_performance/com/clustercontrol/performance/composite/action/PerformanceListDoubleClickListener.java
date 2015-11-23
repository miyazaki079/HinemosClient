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

package com.clustercontrol.performance.composite.action;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.performance.action.GetPerformanceDataSettings;
import com.clustercontrol.performance.action.GetPerformanceListTableDefine;
import com.clustercontrol.performance.composite.PerformanceListComposite;
import com.clustercontrol.performance.view.PerformanceDataSettingCache;
import com.clustercontrol.performance.view.PerformanceGraphView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.collector.PerformanceDataSettings;

/**
 * 性能[一覧]ビュー用のテーブルビューア用のDoubleClickListenerクラスです。
 *
 * @version 4.1.0
 */
public class PerformanceListDoubleClickListener implements IDoubleClickListener {

	private static Log m_log = LogFactory.getLog( PerformanceListDoubleClickListener.class );

	/**
	 * コンストラクタ
	 *
	 * @param composite 性能[一覧]ビュー用のコンポジット
	 */
	public PerformanceListDoubleClickListener(PerformanceListComposite composite) {
	}

	/**
	 * ダブルクリック時に呼び出されます。<BR>
	 * 性能[一覧]ビューのテーブルビューアをダブルクリックした際に、選択した行の内容をダイアログで表示します。
	 * <P>
	 * <ol>
	 * <li>イベントから選択行を取得し、選択行からモニターIDを取得します。</li>
	 * <li>モニターIDからノード情報を取得し、ダイアログで表示します。</li>
	 * </ol>
	 *
	 * @param event イベント
	 *
	 * @see com.clustercontrol.repository.dialog.NodeCreateDialog
	 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
	 */
	@Override
	public void doubleClick(DoubleClickEvent event) {
		long start = System.currentTimeMillis();
		String managerName = null;
		String monitorId = null;

		//モニターIDを取得
		if (((StructuredSelection) event.getSelection()).getFirstElement() != null) {
			ArrayList<?> info = (ArrayList<?>) ((StructuredSelection) event
					.getSelection()).getFirstElement();
			managerName = (String) info.get(GetPerformanceListTableDefine.MANAGER_NAME);
			monitorId = (String) info.get(GetPerformanceListTableDefine.MONITOR_ID);
		}

		if (monitorId != null) {
			PerformanceDataSettings perfDataSettings = GetPerformanceDataSettings
					.getPerformanceGraphInfo(managerName, monitorId);

			// グラフのヘッダ情報を取得成功
			if(perfDataSettings != null){
				// 収集データがなければViewを開かずに終了する
				if(perfDataSettings.getOldestDate() == null || perfDataSettings.getOldestDate() == 0){
					MessageDialog.openError(
							null,
							Messages.getString("failed"),
							Messages.getString("message.collection.6"));
					return;
				}
				PerformanceDataSettingCache.set(managerName, monitorId, perfDataSettings);

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				try {
					managerName = managerName.replace(":", "@");
					PerformanceGraphView graphView = (PerformanceGraphView) page.showView(
							PerformanceGraphView.ID, managerName + "," + monitorId,
							IWorkbenchPage.VIEW_ACTIVATE);
					graphView.setFocus();
					graphView.update(); // This is necessary for RAP(Browser embedded chart)
				} catch (PartInitException e) {
					MessageDialog.openError(
							null,
							Messages.getString("failed"),
							Messages.getString("message.collection.9"));
					return;
				}
			}
			// グラフのヘッダ情報を取得失敗
			else{
				MessageDialog.openError(
						null,
						Messages.getString("failed"),
						Messages.getString("message.collection.9"));

			}
		}

		m_log.debug(String.format("doubleClick: %dms", System.currentTimeMillis() - start));
	}

}
