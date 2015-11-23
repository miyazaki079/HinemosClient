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

package com.clustercontrol.jobmanagement.view.action;

import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.accesscontrol.dialog.ObjectPrivilegeEditDialog;
import com.clustercontrol.accesscontrol.dialog.ObjectPrivilegeListDialog;
import com.clustercontrol.accesscontrol.util.ObjectBean;
import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.view.JobListView;
import com.clustercontrol.view.ObjectPrivilegeTargetListView;
import com.clustercontrol.view.action.ObjectPrivilegeAction;

/**
 * ジョブの作成・変更ダイアログによる、ジョブのオブジェクト権限設定を行うクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 4.1.0
 */
public class JobObjectPrivilegeAction extends ObjectPrivilegeAction {

	public static final String ID = JobObjectPrivilegeAction.class.getName();

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

		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);
		ObjectPrivilegeTargetListView view = (ObjectPrivilegeTargetListView) this.viewPart.getAdapter(listViewClass);

		List<ObjectBean> objectBeans = view.getSelectedObjectBeans();
		if (objectBeans != null && objectBeans.size() > 0) {
			if (objectBeans.size() == 1) {
				// ダイアログを生成
				ObjectPrivilegeListDialog dialog = new ObjectPrivilegeListDialog(
						this.viewPart.getSite().getShell(),objectBeans.get(0).getManagerName(),
						objectBeans.get(0).getObjectId(), objectBeans.get(0).getObjectType(), view.getSelectedOwnerRoleId());
				// ダイアログにて変更が選択された場合、入力内容をもって登録を行う。
				if (dialog.open() == IDialogConstants.OK_ID) {
					view.update();
				}
			} else {
				// ダイアログを生成
				ObjectPrivilegeEditDialog dialog = new ObjectPrivilegeEditDialog(
						this.viewPart.getSite().getShell(),
						objectBeans,
						null,
						null);
				// ダイアログにて変更が選択された場合、入力内容をもって登録を行う。
				if (dialog.open() == IDialogConstants.OK_ID) {
					// ジョブの場合は、登録されていない場合があるためupdate()を実行しない。
					// view.update();
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
				if(part instanceof JobListView){
					// Enable button when 1 item is selected
					JobListView view = (JobListView)part;

					int size = view.getJobTreeComposite().getSelectItemList().size();
					if(size == 1) {
						if(view.getDataType() == JobConstant.TYPE_JOBUNIT){
							editEnable = !view.getEditEnable();
						}
					}
				}
				this.setBaseEnabled(editEnable);
			}
		}
	}

}
