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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.jobmanagement.action.GetJobKickTableDefine;
import com.clustercontrol.jobmanagement.bean.JobTriggerTypeConstant;
import com.clustercontrol.jobmanagement.composite.JobKickListComposite;
import com.clustercontrol.jobmanagement.dialog.FileCheckDialog;
import com.clustercontrol.jobmanagement.dialog.ScheduleDialog;
import com.clustercontrol.jobmanagement.view.JobKickListView;

/**
 * ジョブ[実行契機]ビューの「変更」のクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class ModifyJobKickAction extends AbstractHandler implements IElementUpdater{
	/** アクションID */
	public static final String ID = ModifyJobKickAction.class.getName();
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
	 * ジョブ[実行契機]ビューの「変更」が押された場合に、スケジュールを作成します。
	 * <p>
	 * <ol>
	 * <li>ジョブ[実行契機]ビューから選択されたスケジュール、</li>
	 * <li>または、ファイルチェックを取得します。</li>
	 * <li>ジョブ[実行契機の作成・変更]ダイアログを表示します。</li>
	 * <li>ジョブ[実行契機]ビューを更新します。</li>
	 * </ol>
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 * @see com.clustercontrol.jobmanagement.dialog.ScheduleDialog
	 * @see com.clustercontrol.jobmanagement.dialog.FileCheckDialog
	 * @see com.clustercontrol.jobmanagement.view.JobKickListView
	 * @see com.clustercontrol.jobmanagement.composite.JobKickListComposite
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == this.window || !isEnabled() ){
			return null;
		}

		// 選択アイテムの取得
		this.viewPart =  this.window.getActivePage().getActivePart();

		if (viewPart instanceof JobKickListView) {
			JobKickListView view = (JobKickListView) viewPart
					.getAdapter(JobKickListView.class);
			JobKickListComposite composite = view.getComposite();

			ArrayList<?> item = composite.getSelectItem();
			if (item instanceof ArrayList) {
				String id = view.getSelectedIdList().get(0);
				List<?> list = (ArrayList<?>)item.get(0);
				String managerName = (String) list.get(GetJobKickTableDefine.MANAGER_NAME);
				String type = (String) list.get(GetJobKickTableDefine.TYPE);
				int TypeNum = JobTriggerTypeConstant.stringToType(type);
				if(TypeNum == JobTriggerTypeConstant.TYPE_SCHEDULE){
					ScheduleDialog dialog = new ScheduleDialog(this.window.getShell(), managerName, id, PropertyDefineConstant.MODE_MODIFY);
					dialog.open();
				}
				else if(TypeNum == JobTriggerTypeConstant.TYPE_FILECHECK){
					FileCheckDialog dialog = new FileCheckDialog(this.window.getShell(), managerName, id, PropertyDefineConstant.MODE_MODIFY);
					dialog.open();
				}
			}
			view.update();
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
				if( part instanceof JobKickListView  ){
					// Enable button when 1 item is selected
					JobKickListView view = (JobKickListView)part;
					if(view.getSelectedNum() == 1) {
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
