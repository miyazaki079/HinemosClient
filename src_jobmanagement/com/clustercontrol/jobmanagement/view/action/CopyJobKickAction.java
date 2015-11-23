/*

Copyright (C) 2013 NTT DATA Corporation

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
 * ジョブ[実行契機]ビューの「コピー」のクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 4.1.0
 */
public class CopyJobKickAction extends AbstractHandler implements IElementUpdater {
	/** アクションID */
	public static final String ID = CopyJobKickAction.class.getName();
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
	 * ジョブ[実行契機]ビューの「コピー」が押された場合に、スケジュールを作成します。
	 * <p>
	 * <ol>
	 * <li>ジョブ[実行契機]ビューから選択されたスケジュール、</li>
	 * <li>または、ファイルチェックを取得します。</li>
	 * <li>ジョブ[実行契機の作成・変更]ダイアログを表示します。</li>
	 * <li>ジョブ[実行契機]ビューをコピーします。</li>
	 * </ol>
	 *
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
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
		this.viewPart = HandlerUtil.getActivePart(event);

		if (viewPart instanceof JobKickListView) {
			JobKickListView view = (JobKickListView) viewPart
					.getAdapter(JobKickListView.class);
			JobKickListComposite composite = view.getComposite();

			ArrayList<?> itemList = composite.getSelectItem();
			if (itemList instanceof ArrayList) {
				ArrayList<?> item = (ArrayList<?>)itemList.get(0);
				String id = view.getSelectedIdList().get(0);
				String managerName = (String) item.get(GetJobKickTableDefine.MANAGER_NAME);
				String type = (String) item.get(GetJobKickTableDefine.TYPE);
				int TypeNum = JobTriggerTypeConstant.stringToType(type);

				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow( event );
				if(TypeNum == JobTriggerTypeConstant.TYPE_SCHEDULE){
					ScheduleDialog dialog = new ScheduleDialog(window.getShell(), managerName, id,PropertyDefineConstant.MODE_COPY);
					dialog.open();
				}
				else if(TypeNum == JobTriggerTypeConstant.TYPE_FILECHECK){
					FileCheckDialog dialog = new FileCheckDialog(window.getShell(), managerName, id,PropertyDefineConstant.MODE_COPY);
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
