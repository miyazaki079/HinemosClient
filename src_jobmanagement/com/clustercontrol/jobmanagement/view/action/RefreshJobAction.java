/*
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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.clustercontrol.jobmanagement.util.JobEditState;
import com.clustercontrol.jobmanagement.util.JobEditStateUtil;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.jobmanagement.view.JobListView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;
import com.clustercontrol.ws.jobmanagement.JobInfo;

/**
 * ジョブ[一覧]ビューの「更新」を行うクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class RefreshJobAction extends AbstractHandler {

	// ログ
	private static Log m_log = LogFactory.getLog( RefreshJobAction.class );

	/** アクションID */
	public static final String ID = RefreshJobAction.class.getName();
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
	 * ジョブ[一覧]ビューの「更新」が押された場合に、ジョブ[一覧]ビューを更新します。
	 * <p>
	 * <ol>
	 * <li>確認ダイアログを表示します。</li>
	 * <li>ジョブ[一覧]ビューを更新します。</li>
	 * </ol>
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 * @see com.clustercontrol.jobmanagement.view.JobListView#update()
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

		if (viewPart instanceof JobListView) {
			JobListView view = (JobListView)viewPart.getAdapter(JobListView.class);

			// 編集モードから抜ける

			// 確認ダイアログを生成
			String message;
			if(JobEditStateUtil.existEditing()){
				message = Messages.getString("message.job.43") + "\n" +
						Messages.getString("message.job.30");
			}else{
				message = Messages.getString("message.job.30");
			}
			if(MessageDialog.openQuestion(
					null,
					Messages.getString("confirmed"),
					message)) {
				m_log.debug("RefreshJob start " + new Date());

				try {
					for (String managerName : JobEditStateUtil.getManagerList()) {
						JobEditState jobEditState = JobEditStateUtil.getJobEditState(managerName);
						JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
						for (JobInfo info : jobEditState.getLockedJobunitList()) {
							wrapper.releaseEditLock(jobEditState.getEditSession(info));
						}
					}
				} catch (InvalidRole_Exception e) {
					// アクセス権なしの場合、エラーダイアログを表示する
					MessageDialog.openInformation(
							null,
							Messages.getString("message"),
							Messages.getString("message.accesscontrol.16"));
				} catch (Exception e) {
					m_log.warn("updateJobunitUpdateTime() : " + e.getMessage(), e);
					MessageDialog.openError(
							null,
							Messages.getString("failed"),
							Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
				}

				view.update();
				view.getJobTreeComposite().refresh();
				m_log.debug("RefreshJob end " + new Date());
			}
		}
		return null;
	}
}
