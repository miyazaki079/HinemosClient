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

package com.clustercontrol.jobmanagement.composite.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.jobmanagement.action.GetJobTableDefine;
import com.clustercontrol.jobmanagement.composite.JobListComposite;
import com.clustercontrol.jobmanagement.view.JobListView;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;

/**
 * ジョブ[一覧]ビューのテーブルビューア用のSelectionChangedListenerです。
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class JobListSelectionChangedListener implements ISelectionChangedListener {
	/** ジョブ[一覧]ビュー用のコンポジット */
	private JobListComposite m_list;

	/**
	 * コンストラクタ
	 *
	 * @param list ジョブ[一覧]ビュー用のコンポジット
	 */
	public JobListSelectionChangedListener(JobListComposite list) {
		m_list = list;
	}

	/**
	 * 選択変更時に呼び出されます。<BR>
	 * ジョブ[一覧]ビューのテーブルビューアを選択した際に、選択した行の内容でビューのアクションの有効・無効を設定します。
	 * <P>
	 * <ol>
	 * <li>選択変更イベントから選択行を取得し、選択行からジョブIDを取得します。</li>
	 * <li>ジョブ[一覧]ビュー用のコンポジットからジョブツリーアイテムを取得します。</li>
	 * <li>取得したジョブツリーアイテムから、ジョブIDが一致するジョブツリーアイテムを取得します。</li>
	 * <li>ジョブ[一覧]ビュー用のコンポジットに、ジョブIDが一致するジョブツリーアイテムを設定します。</li>
	 * <li>ジョブ[一覧]ビューのアクションの有効・無効を設定します。</li>
	 * </ol>
	 *
	 * @param event 選択変更イベント
	 *
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		//ジョブ[登録]ビューのインスタンスを取得
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart viewPart = page.findView(JobListView.ID);

		//選択アイテムを取得
		StructuredSelection selection = (StructuredSelection) event.getSelection();

		if ( viewPart != null && selection != null ){
			JobListView view = (JobListView) viewPart.getAdapter(JobListView.class);

			// Set last focus
			Composite composite = view.getJobListComposite();
			if( composite instanceof JobListComposite && ((JobListComposite)composite).getTable().isFocusControl() ){
				view.setLastFocusComposite( composite );
			}

			JobTreeItem selectJobTreeItem = null;

			List<?> list = selection.toList();
			List<JobTreeItem> itemList = new ArrayList<JobTreeItem>();
			for(Object obj : list) {
				if (obj instanceof ArrayList) {
					ArrayList<?> item = (ArrayList<?>)obj;
					String jobId = (String) item.get(GetJobTableDefine.JOB_ID);

					if (m_list.getJobTreeItem() instanceof JobTreeItem) {
						List<JobTreeItem> items = m_list.getJobTreeItem().getChildren();

						for (int i = 0; i < items.size(); i++) {
							if (jobId.equals(items.get(i).getData().getId())) {
								selectJobTreeItem = items.get(i);
								break;
							}
						}
						itemList.add(selectJobTreeItem);
					}
				}
			}

			view.setEnabledAction(selectJobTreeItem, itemList, false);
		}
	}
}
