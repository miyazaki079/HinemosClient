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

package com.clustercontrol.repository.composite.action;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.repository.view.NodeListView;

/**
 * リポジトリ[ノード]ビューのテーブルビューア用のSelectionChangedListenerクラス<BR>
 * 
 * @version 2.2.0
 * @since 2.2.0
 */
public class NodeListSelectionChangedListener implements ISelectionChangedListener {

	/**
	 * コンストラクタ
	 * 
	 * @param list リポジトリ[ノード]ビュー用のコンポジット
	 */
	public NodeListSelectionChangedListener() {

	}

	/**
	 * 選択変更時に呼び出されます。<BR>
	 * リポジトリ[ノード]ビューのテーブルビューアを選択した際に、<BR>
	 * 選択した行の内容でビューのアクションの有効・無効を設定します。
	 * <P>
	 * <ol>
	 * <li>選択変更イベントから選択行を取得し、選択行からイベントの表示内容を取得します。</li>
	 * <li>取得したイベントからリポジトリ[ノード]ビューのアクションの有効・無効を設定します。</li>
	 * </ol>
	 * 
	 * @param event 選択変更イベント
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		//リポジトリ[ノード]ビューのインスタンスを取得
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(NodeListView.ID);

		//選択アイテムを取得
		StructuredSelection selection = (StructuredSelection) event.getSelection();

		if ( viewPart != null && selection != null) {
			NodeListView view = (NodeListView) viewPart.getAdapter(NodeListView.class);

			//ビューのボタン（アクション）の使用可/不可を設定する
			view.setEnabledAction(selection.size(), event.getSelection());
		}
	}
}
