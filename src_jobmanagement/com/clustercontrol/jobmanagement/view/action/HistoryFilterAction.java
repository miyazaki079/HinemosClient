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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.clustercontrol.bean.Property;
import com.clustercontrol.jobmanagement.dialog.HistoryFilterDialog;
import com.clustercontrol.jobmanagement.view.JobHistoryView;

/**
 * ジョブ[履歴]ビューの「フィルタ処理」のクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class HistoryFilterAction extends AbstractHandler {
	/** アクションID */
	public static final String ID = HistoryFilterAction.class.getName();
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
	 * ジョブ[履歴]ビューの「フィルタ処理」が押された場合に、フィルタ条件に該当する履歴一覧情報を表示します。
	 * <p>
	 * <ol>
	 * <li>ジョブ[履歴フィルタ処理]ダイアログを表示します。</li>
	 * <li>ジョブ[履歴]ビューにフィルタ条件を設定します。</li>
	 * <li>ジョブ[履歴]ビューを更新します。</li>
	 * </ol>
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 * @see com.clustercontrol.jobmanagement.view.JobHistoryView
	 * @see com.clustercontrol.jobmanagement.dialog.HistoryFilterDialog
	 * @see com.clustercontrol.bean.Property
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);

		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);

		ICommandService commandService = (ICommandService)window.getService(ICommandService.class);
		Command command = commandService.getCommand(ID);
		boolean isChecked = !HandlerUtil.toggleCommandState(command);

		if (isChecked) { // ボタンが押された場合

			HistoryFilterDialog dialog = new HistoryFilterDialog(HandlerUtil.getActiveWorkbenchWindow( event ).getShell());

			JobHistoryView view = (JobHistoryView) viewPart
					.getAdapter(JobHistoryView.class);

			//ジョブ履歴フィルタダイアログを表示
			if (dialog.open() == IDialogConstants.OK_ID) {
				// ジョブ履歴ビューをフィルタモードで表示
				Property property = dialog.getInputData();
				view.setFilterCondition(property);
				view.update();
			} else {
				State state = command.getState(RegistryToggleState.STATE_ID);
				state.setValue(false);
			}
		} else { // ボタンが戻った場合
			// ジョブ履歴ビューを通常モードで表示
			JobHistoryView view = (JobHistoryView) viewPart
					.getAdapter(JobHistoryView.class);
			view.setFilterCondition(null);
			view.update();
		}
		return null;
	}
}
