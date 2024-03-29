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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.repository.action.GetScopeListTableDefine;
import com.clustercontrol.repository.composite.ScopeListComposite;
import com.clustercontrol.repository.view.ScopeListView;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * リポジトリ[スコープ]ビューのテーブルビューア用のSelectionChangedListenerクラス<BR>
 * 
 * @version 2.2.0
 * @since 2.2.0
 */
public class ScopeListSelectionChangedListener implements ISelectionChangedListener {
	/** リポジトリ[スコープ]ビュー用のコンポジット */
	private ScopeListComposite m_list;

	/**
	 * コンストラクタ
	 * 
	 * @param list リポジトリ[スコープ]ビュー用のコンポジット
	 */
	public ScopeListSelectionChangedListener(ScopeListComposite list) {
		m_list = list;
	}

	/**
	 * 選択変更時に呼び出されます。<BR>
	 * リポジトリ[スコープ]ビューのテーブルビューアを選択した際に、選択した行の内容でビューのアクションの有効・無効を設定します。
	 * <P>
	 * <ol>
	 * <li>選択変更イベントから選択行を取得し、選択行からファシリティIDを取得します。</li>
	 * <li>リポジトリ[スコープ]ビュー用のコンポジットからファシリティツリーアイテムを取得します。</li>
	 * <li>取得したファシリティツリーアイテムから、ファシリティIDが一致するファシリティツリーアイテムを取得します。</li>
	 * <li>リポジトリ[スコープ]ビュー用のコンポジットに、ファシリティIDが一致するファシリティツリーアイテムを設定します。</li>
	 * <li>リポジトリ[スコープ]ビューのアクションの有効・無効を設定します。</li>
	 * </ol>
	 * 
	 * @param event 選択変更イベント
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		FacilityTreeItem selectFacilityTreeItem = null;
		ArrayList<?> item = null;

		if (((StructuredSelection) event.getSelection()).getFirstElement() != null) {
			//選択アイテムを取得
			item = (ArrayList<?>) ((StructuredSelection) event.getSelection()).getFirstElement();
		}

		//リポジトリ[スコープ]ビューのインスタンスを取得
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(ScopeListView.ID);

		if(viewPart != null) {
			ScopeListView view = (ScopeListView) viewPart.getAdapter(ScopeListView.class);

			// Set last focus
			Composite composite = view.getListComposite();
			if( composite instanceof ScopeListComposite && ((ScopeListComposite)composite).getTable().isFocusControl() ){
				view.setLastFocusComposite( composite );
			}

			if(item instanceof ArrayList){
				String facilityId = (String) item.get(GetScopeListTableDefine.FACILITY_ID);

				if (m_list.getFacilityTreeItem() instanceof FacilityTreeItem) {
					List<FacilityTreeItem> items = m_list.getFacilityTreeItem().getChildren();

					for(int i = 0; i < items.size(); i++){
						if(facilityId.equals(items.get(i).getData().getFacilityId())){
							selectFacilityTreeItem = items.get(i);
							break;
						}
					}
				}
			}

			if (selectFacilityTreeItem instanceof FacilityTreeItem) {
				//選択ツリーアイテムを設定
				m_list.setSelectFacilityTreeItem(selectFacilityTreeItem);

				//ビューのアクションの有効/無効を設定
				view.setEnabledAction(selectFacilityTreeItem.getData().isBuiltInFlg(),selectFacilityTreeItem.getData().getFacilityType(), event.getSelection(), selectFacilityTreeItem.getData().isNotReferFlg());
			} else {
				//選択ツリーアイテムを設定
				m_list.setSelectFacilityTreeItem(null);
			}
		}
	}
}
