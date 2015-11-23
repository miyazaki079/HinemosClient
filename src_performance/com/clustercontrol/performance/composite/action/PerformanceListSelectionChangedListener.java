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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.performance.action.GetPerformanceListTableDefine;
import com.clustercontrol.performance.view.PerformanceListView;

/**
 * 性能[一覧]ビューのテーブルビューア用のSelectionChangedListenerです。
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class PerformanceListSelectionChangedListener implements ISelectionChangedListener {

	/**
	 * コンストラクタ
	 *
	 * @param list 性能[一覧]ビュー用のコンポジット
	 */
	public PerformanceListSelectionChangedListener() {

	}

	/**
	 * 選択変更時に呼び出されます。<BR>
	 * 性能[一覧]ビューのテーブルビューアを選択した際に、<BR>
	 * 選択した行の内容でビューのアクションの有効・無効を設定します。
	 * <P>
	 * <ol>
	 * <li>選択変更イベントから選択行を取得し、選択行からイベントの表示内容を取得します。</li>
	 * <li>取得したイベントから性能[一覧]ビューのアクションの有効・無効を設定します。</li>
	 * </ol>
	 *
	 * @param event 選択変更イベント
	 *
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		ArrayList<?> list = null;

		//性能[一覧]ビューのインスタンスを取得
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(PerformanceListView.ID);

		//選択アイテムを取得
		StructuredSelection selection = (StructuredSelection) event.getSelection();

		if ( viewPart != null && selection != null) {
			PerformanceListView view = (PerformanceListView) viewPart.getAdapter(PerformanceListView.class);

			list = (ArrayList<?>) selection.getFirstElement();

			if (list != null) {
				//性能[一覧]ビューのボタン（アクション）の使用可/不可を設定する
				view.setEnabledAction(((Integer) list.get(GetPerformanceListTableDefine.STATUS)).intValue(), selection);
			} else {
				//性能[一覧]ビューのボタン（アクション）を使用不可に設定する
				view.initButton();
			}
		}
	}
}
