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

package com.clustercontrol.calendar.view.action;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import com.clustercontrol.calendar.util.CalendarEndpointWrapper;
import com.clustercontrol.calendar.view.CalendarListView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.UIManager;
import com.clustercontrol.ws.calendar.InvalidRole_Exception;

/**
 * カレンダの削除を行うクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 2.0.0
 */
public class CalendarDeleteAction extends AbstractHandler implements IElementUpdater{
	public static final String ID = CalendarDeleteAction.class.getName();

	private IWorkbenchWindow window;
	private IWorkbenchPart viewPart;

	/**
	 * Handler execution
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == this.window || !isEnabled() ){
			return null;
		}

		this.viewPart = HandlerUtil.getActivePart(event);

		// カレンダ一覧より、選択されているカレンダIDを取得
		CalendarListView view = (CalendarListView) this.viewPart
				.getAdapter(CalendarListView.class);

		Map<String, List<String>> map = view.getSelectedItem();

		String[] args = new String[1];
		String msg = null;

		int i = 0;
		String calId = null;
		for(Map.Entry<String, List<String>> entry : map.entrySet()) {
			for(String id : entry.getValue()) {
				calId = id;
				i++;
			}
		}

		if (i > 0) {
			// 確認ダイアログにて変更が選択された場合、削除処理を行う。
			if(i == 1) {
				msg = "message.calendar.7";
				args[0] = calId;
			} else {
				msg = "message.calendar.71";
				args[0] = (new Integer(i)).toString();
			}

			Map<String, String> errorMsgs = new ConcurrentHashMap<>();
			StringBuffer messageArg = new StringBuffer();
			i = 0;

			if (MessageDialog.openConfirm(
					null,
					Messages.getString("confirmed"),
					Messages.getString(msg, args))) {
				for(Map.Entry<String, List<String>> entry : map.entrySet()) {
					String managerName = entry.getKey();
					CalendarEndpointWrapper wrapper = CalendarEndpointWrapper.getWrapper(managerName);
					if(i > 0) {
						messageArg.append(", ");
					}
					messageArg.append(managerName);
					try {
						wrapper.deleteCalendar(entry.getValue());
					} catch (Exception e) {
						if (e instanceof InvalidRole_Exception) {
							// 権限なし
							errorMsgs.put(managerName, Messages.getString("message.accesscontrol.16"));
						} else {
							errorMsgs.put(managerName, Messages.getString("message.calendar.6") +
									", " + e.getMessage());
						}
					}
					i++;
				}

				//メッセージ表示
				if( 0 < errorMsgs.size() ){
					UIManager.showMessageBox(errorMsgs, true);
				} else {
					args[0] = messageArg.toString();
					// 成功報告ダイアログを生成
					MessageDialog.openInformation(
							null,
							Messages.getString("successful"),
							Messages.getString("message.calendar.5", args));
				}

				// ビューを更新
				view.update();
			}
		}

		return null;
	}

	/**
	 * Dispose
	 */
	@Override
	public void dispose(){
		this.viewPart = null;
		this.window = null;
	}

	/**
	 * Update handler status
	 */
	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// page may not start at state restoring
		if( null != window ){
			IWorkbenchPage page = window.getActivePage();
			if( null != page ){
				IWorkbenchPart part = page.getActivePart();

				if( part instanceof CalendarListView  ){
					// Enable button when 1 item is selected
					this.setBaseEnabled( 0 < ((CalendarListView) part).getSelectedNum() );
				}else{
					this.setBaseEnabled( false );
				}
			} else {
				this.setBaseEnabled(false);
			}
		}
	}
}
