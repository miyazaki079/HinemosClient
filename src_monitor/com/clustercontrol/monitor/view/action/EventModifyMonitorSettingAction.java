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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.monitor.composite.EventListComposite;
import com.clustercontrol.monitor.view.EventView;
import com.clustercontrol.view.ScopeListBaseView;

/**
 * 監視[イベント]ビューの監視設定変更ダイアログ表示アクションを行うアクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class EventModifyMonitorSettingAction extends AbstractHandler implements IElementUpdater {

	/** プラグインIDの末尾文字（数値） */
	private static final String SUFFIX_PLUGIN_ID_NUM = "_N";

	/** マネージャの位置 */
	private static final int POS_MANAER_NAME = 0;

	/** 監視種別の位置 */
	private static final int POS_PLUGIN_ID = 4;

	/** 監視項目IDの位置 */
	private static final int POS_MONITOR_ID = 5;

	/** アクションID */
	public static final String ID = EventModifyMonitorSettingAction.class.getName();

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
	 * 監視[イベント]ビューの選択されたアイテムの変更用ダイアログを表示します。
	 * <p>
	 * <ol>
	 * <li>監視[イベント]ビューで、選択されているアイテムを取得します。</li>
	 * <li>取得したイベント情報に応じた変更用ダイアログを表示します。 </li>
	 * </ol>
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 * @see com.clustercontrol.monitor.view.EventView
	 * @see com.clustercontrol.monitor.view.EventView#update()
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

		String managerName = "";
		String pluginId = "";
		String monitorId = "";
		int monitorType = 0;

		// 選択アイテムを取得します。
		ScopeListBaseView view = (EventView) this.viewPart.getAdapter(EventView.class);
		EventListComposite composite = (EventListComposite) view.getListComposite();
		WidgetTestUtil.setTestId(this, null, composite);
		StructuredSelection  selection = (StructuredSelection) composite.getTableViewer().getSelection();

		List<?> list = (ArrayList<?>) selection.getFirstElement();

		if (list != null) {
			managerName = (String) list.get(POS_MANAER_NAME);
			pluginId = (String) list.get(POS_PLUGIN_ID);
			monitorId = (String) list.get(POS_MONITOR_ID);
			if (pluginId.endsWith(SUFFIX_PLUGIN_ID_NUM)) {
				monitorType = 1;
			}

			if(pluginId != null && monitorId != null){
				// ダイアログ名を取得
				MonitorModifyAction mmAction = new MonitorModifyAction();
				// ダイアログにて変更が選択された場合、入力内容をもって登録を行う。
				if (mmAction.dialogOpen(composite.getShell(), managerName, pluginId, monitorId,monitorType) == IDialogConstants.OK_ID) {
					composite.update();
				}
			}
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
				if(part instanceof EventView){
					// Enable button when 1 item is selected
					EventView view = (EventView)part;

					if(HinemosModuleConstant.JOB.equals(view.getPluginId()) ||
							HinemosModuleConstant.SYSYTEM.equals(view.getPluginId()) ||
							HinemosModuleConstant.SYSYTEM_MAINTENANCE.equals(view.getPluginId()) ||
							HinemosModuleConstant.SYSYTEM_SELFCHECK.equals(view.getPluginId())) {
						editEnable = false;
					} else if(view.getPluginId() != null && HinemosModuleConstant.isExist(view.getPluginId())) {
						editEnable = true;
					}
				}
				this.setBaseEnabled(editEnable);
			} else {
				this.setBaseEnabled(false);
			}
		}
	}
}
