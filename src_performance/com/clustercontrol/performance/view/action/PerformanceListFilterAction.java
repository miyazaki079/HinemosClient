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
import com.clustercontrol.performance.dialog.PerformanceListFilterDialog;
import com.clustercontrol.performance.view.PerformanceListView;

/**
 * 性能[一覧]ビューをフィルタ表示するアクションクラス
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class PerformanceListFilterAction extends AbstractHandler {
	public static final String ID = PerformanceListFilterAction.class.getName();

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

		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);

		PerformanceListView view = (PerformanceListView) this.viewPart.getAdapter(PerformanceListView.class);

		ICommandService commandService = (ICommandService)window.getService(ICommandService.class);
		Command command = commandService.getCommand(ID);
		boolean isChecked = !HandlerUtil.toggleCommandState(command);

		if (isChecked) {
			// ダイアログを生成
			PerformanceListFilterDialog dialog = new PerformanceListFilterDialog(this.viewPart
					.getSite().getShell());

			// ダイアログにて検索が選択された場合、検索結果をビューに表示
			if (dialog.open() == IDialogConstants.OK_ID) {

				Property condition = dialog.getInputData();

				view.setCondition(condition);
				view.update();
			} else {
				State state = command.getState(RegistryToggleState.STATE_ID);
				state.setValue(false);
			}
		} else {
			// 検索条件クリア
			view.setCondition(null);
			view.update();
		}
		return null;
	}
}
