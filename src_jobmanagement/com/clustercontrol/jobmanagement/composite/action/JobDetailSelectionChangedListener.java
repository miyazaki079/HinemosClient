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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.jobmanagement.composite.DetailComposite;
import com.clustercontrol.jobmanagement.view.ForwardFileView;
import com.clustercontrol.jobmanagement.view.JobDetailView;
import com.clustercontrol.jobmanagement.view.JobNodeDetailView;
import com.clustercontrol.ws.jobmanagement.JobInfo;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;

/**
 * ジョブ[ジョブ詳細]ビューのテーブルビューアのSelectionChangedListenerです。
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class JobDetailSelectionChangedListener implements ISelectionChangedListener {
	/** ジョブ[ジョブ詳細]ビュー用のコンポジット */
	private DetailComposite m_composite;

	/**
	 * コンストラクタ
	 *
	 * @param composite ジョブ[ジョブ詳細]ビュー用のコンポジット
	 */
	public JobDetailSelectionChangedListener(DetailComposite composite) {
		m_composite = composite;
	}

	/**
	 * 選択変更時に呼び出されます。<BR>
	 * ジョブ[ジョブ詳細]ビューを選択した際に、<BR>
	 * 選択した行の内容でジョブ[ノード詳細]ビューとジョブ[ファイル転送]ビューを更新します。
	 * <P>
	 * <ol>
	 * <li>選択変更イベントから選択行を取得し、選択行からジョブIDを取得します。</li>
	 * <li>ジョブIDをジョブ[ジョブ詳細]ビュー用のコンポジットに設定します。</li>
	 * <li>ジョブ[ジョブ詳細]ビュー用のコンポジットからセッションIDを取得します。</li>
	 * <li>ジョブ[ノード詳細]ビューを更新します。</li>
	 * <li>ジョブ[ファイル転送]ビューを更新します。</li>
	 * </ol>
	 *
	 * @param event 選択変更イベント
	 *
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		String managerName = null;
		String sessionId = null;
		String jobunitId = null;
		String jobId = null;

		//ジョブIDを取得
		if (((StructuredSelection) event.getSelection()).getFirstElement() != null) {
			JobTreeItem item = (JobTreeItem) ((StructuredSelection) event
					.getSelection()).getFirstElement();
			JobInfo info = item.getData();
			jobunitId = info.getJobunitId();
			jobId = info.getId();

			//ジョブユニットIDを設定
			m_composite.setJobunitId(jobunitId);
			//ジョブIDを設定
			m_composite.setJobId(jobId);
		}

		managerName = m_composite.getManagerName();
		//セッションIDを設定
		sessionId = m_composite.getSessionId();

		//アクティブページを手に入れる
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		//ビューを更新する
		IViewPart viewPart = page.findView(JobNodeDetailView.ID);
		if (viewPart != null) {
			JobNodeDetailView view = (JobNodeDetailView) viewPart
					.getAdapter(JobNodeDetailView.class);
			view.update(managerName, sessionId, jobunitId, jobId);
		}

		//ビューを更新する
		viewPart = page.findView(ForwardFileView.ID);
		if (viewPart != null) {
			ForwardFileView view = (ForwardFileView) viewPart
					.getAdapter(ForwardFileView.class);
			view.update(managerName, sessionId, jobunitId, jobId);
		}

		//ジョブ[ジョブ詳細]ビューのボタン（アクション）の使用可/不可を設定する
		viewPart = page.findView(JobDetailView.ID);
		if (viewPart != null && event.getSelection() != null) {
			JobDetailView view = (JobDetailView) viewPart.getAdapter(JobDetailView.class);
			view.setEnabledAction(((StructuredSelection)event.getSelection()).size(), event.getSelection());
		}
	}
}

