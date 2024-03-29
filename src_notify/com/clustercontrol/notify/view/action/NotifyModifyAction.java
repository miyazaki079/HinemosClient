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

package com.clustercontrol.notify.view.action;

import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.monitor.action.NotifyTableDefineNoCheckBox;
import com.clustercontrol.notify.bean.NotifyTypeConstant;
import com.clustercontrol.notify.composite.NotifyListComposite;
import com.clustercontrol.notify.dialog.NotifyBasicCreateDialog;
import com.clustercontrol.notify.dialog.NotifyCommandCreateDialog;
import com.clustercontrol.notify.dialog.NotifyEventCreateDialog;
import com.clustercontrol.notify.dialog.NotifyJobCreateDialog;
import com.clustercontrol.notify.dialog.NotifyLogEscalateCreateDialog;
import com.clustercontrol.notify.dialog.NotifyMailCreateDialog;
import com.clustercontrol.notify.dialog.NotifyStatusCreateDialog;
import com.clustercontrol.notify.view.NotifyListView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 通知[一覧]ビューの編集アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class NotifyModifyAction extends AbstractHandler implements IElementUpdater {

	/** アクションID */
	public static final String ID = NotifyModifyAction.class.getName();

	/** ビュー */
	private IWorkbenchPart viewPart;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
	}

	public int openDialog(Shell shell, String managerName, String notifyId, int notifyType) {
		NotifyBasicCreateDialog dialog = null;
		boolean updateFlg = true;
		if (notifyId == null) {
			updateFlg = false;
		}
		if (NotifyTypeConstant.TYPE_STATUS == notifyType) {
			dialog = new NotifyStatusCreateDialog(shell, managerName, notifyId, updateFlg);
		} else if (NotifyTypeConstant.TYPE_EVENT == notifyType) {
			dialog = new NotifyEventCreateDialog(shell, managerName, notifyId, updateFlg);
		} else if (NotifyTypeConstant.TYPE_MAIL == notifyType) {
			dialog = new NotifyMailCreateDialog(shell, managerName, notifyId, updateFlg);
		} else if (NotifyTypeConstant.TYPE_JOB == notifyType) {
			dialog = new NotifyJobCreateDialog(shell, managerName, notifyId, updateFlg);
		} else if (NotifyTypeConstant.TYPE_LOG_ESCALATE == notifyType) {
			dialog = new NotifyLogEscalateCreateDialog(shell, managerName, notifyId, updateFlg);
		} else if (NotifyTypeConstant.TYPE_COMMAND == notifyType) {
			dialog = new NotifyCommandCreateDialog(shell, managerName, notifyId, updateFlg);
		}
		return dialog.open();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);
		NotifyListView view = (NotifyListView) this.viewPart.getAdapter(NotifyListView.class);

		NotifyListComposite composite = (NotifyListComposite) view.getListComposite();
		WidgetTestUtil.setTestId(this, null, composite);
		StructuredSelection selection = (StructuredSelection) composite.getTableViewer().getSelection();

		List<?> list = (List<?>) selection.getFirstElement();
		String managerName = null;
		String notifyId = null;
		Integer notifyType = null;
		if(list != null && list.size() > 0){
			managerName = (String) list.get(NotifyTableDefineNoCheckBox.MANAGER_NAME);
			notifyId = (String) list.get(NotifyTableDefineNoCheckBox.NOTIFY_ID);
			notifyType = (Integer) list.get(NotifyTableDefineNoCheckBox.NOTIFY_TYPE);
		}

		Table table = composite.getTableViewer().getTable();
		WidgetTestUtil.setTestId(this, null, table);

		// 選択アイテムがある場合に、編集ダイアログを表示する
		if(notifyId != null && notifyType != null){
			Shell shell = view.getListComposite().getShell();
			if (openDialog(shell, managerName, notifyId, notifyType) == IDialogConstants.OK_ID) {
				int selectIndex = table.getSelectionIndex();
				composite.update();
				table.setSelection(selectIndex);
			}
		}else{
			MessageDialog.openWarning(
					null,
					Messages.getString("warning"),
					Messages.getString("message.notify.8"));
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if( null != window ){
			IWorkbenchPage page = window.getActivePage();
			if( null != page ){
				IWorkbenchPart part = page.getActivePart();

				boolean editEnable = false;
				if(part instanceof NotifyListView){
					// Enable button when 1 item is selected
					NotifyListView view = (NotifyListView)part;

					if(view.getSelectedNum() == 1) {
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
