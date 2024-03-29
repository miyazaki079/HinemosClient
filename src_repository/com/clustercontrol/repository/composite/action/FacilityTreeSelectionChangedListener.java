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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.composite.FacilityTreeComposite;
import com.clustercontrol.repository.bean.FacilityConstant;
import com.clustercontrol.repository.view.ScopeListView;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * リポジトリ[スコープ]ビューのツリービューア用のSelectionChangedListenerクラス<BR>
 *
 * @version 2.2.0
 * @since 2.2.0
 */
public class FacilityTreeSelectionChangedListener implements ISelectionChangedListener {

	// ログ
	private static Log m_log = LogFactory.getLog( FacilityTreeSelectionChangedListener.class );

	/**
	 * コンストラクタ
	 */
	public FacilityTreeSelectionChangedListener() {

	}

	/**
	 * 選択変更時に呼び出されます。<BR>
	 * リポジトリ[スコープ]ビューのツリービューアを選択した際に、<BR>
	 * 選択したアイテムの内容でリポジトリ[スコープ]ビューのアクションの有効・無効を設定します。
	 * <P>
	 * <ol>
	 * <li>選択変更イベントからファシリティツリーアイテムを取得します。</li>
	 * <li>リポジトリ[スコープ]ビューのアクションの有効・無効を設定します。</li>
	 * </ol>
	 *
	 * @param event 選択変更イベント
	 *
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		FacilityTreeItem selectItem = null;

		if (((StructuredSelection) event.getSelection()).getFirstElement() != null) {
			//選択アイテムを取得
			selectItem = (FacilityTreeItem) ((StructuredSelection) event.getSelection()).getFirstElement();
		}

		// リポジトリ[登録]ビューのインスタンスを取得
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(ScopeListView.ID);

		if (viewPart != null && selectItem instanceof FacilityTreeItem) {
			ScopeListView view = (ScopeListView) viewPart.getAdapter(ScopeListView.class);

			// Set last focus
			Composite composite = view.getScopeTreeComposite();
			if( composite instanceof FacilityTreeComposite && ((FacilityTreeComposite)composite).getTree().isFocusControl() ){
				view.setLastFocusComposite( composite );
			}

			TreeSelection selection = (TreeSelection)event.getSelection();
			boolean builtin = false;
			if (selectItem.getData().getFacilityType() != FacilityConstant.TYPE_MANAGER) {
				builtin = isBuiltin((List<?>)selection.toList());
			}

			// ビューのアクションの有効/無効を設定
			view.setEnabledAction(builtin, selectItem.getData().getFacilityType(), event.getSelection(), selectItem.getData().isNotReferFlg());
		}

	}

	private boolean isBuiltin(List<?> treeList) {
		boolean ret = false;
			for(Object obj : treeList) {
				if (obj instanceof FacilityTreeItem == false) {
					continue;
				}
				FacilityTreeItem tree = (FacilityTreeItem)obj;
				m_log.debug("facilityId:" + tree.getData().getFacilityId());
				ret = ret || tree.getData().isBuiltInFlg();
				if (ret == false && tree.getChildren().isEmpty() == false) {
					ret = isBuiltin(tree.getChildren());
				}
				if (ret) {
					return ret;
				}
			}
		return ret;
	}
}
