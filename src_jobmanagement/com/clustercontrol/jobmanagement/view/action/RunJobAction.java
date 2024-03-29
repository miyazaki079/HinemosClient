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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.bean.JobTriggerTypeConstant;
import com.clustercontrol.jobmanagement.dialog.JobRunConfirm;
import com.clustercontrol.jobmanagement.util.JobEditState;
import com.clustercontrol.jobmanagement.util.JobEditStateUtil;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.jobmanagement.util.JobTreeItemUtil;
import com.clustercontrol.jobmanagement.view.JobListView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;
import com.clustercontrol.ws.jobmanagement.JobTriggerInfo;

/**
 * ジョブ[一覧]ビューの「実行」のクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class RunJobAction extends AbstractHandler implements IElementUpdater {

	// ログ
	private static Log m_log = LogFactory.getLog( RunJobAction.class );

	private JobTriggerInfo m_trigger = new JobTriggerInfo();

	/** アクションID */
	public static final String ID = RunJobAction.class.getName();
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
	 * ジョブ[一覧]ビューの「登録」が押された場合に、ジョブを登録します。
	 * <p>
	 * <ol>
	 * <li>ジョブ[一覧]ビューから選択されたジョブツリーアイテムを取得します。</li>
	 * <li>実行の確認ダイアログを表示します。</li>
	 * <li>ジョブを実行します。</li>
	 * </ol>
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 * @see com.clustercontrol.jobmanagement.view.JobListView
	 * @see com.clustercontrol.jobmanagement.action.RunJob#runJob(String)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		JobTreeItem item = null;

		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == this.window || !isEnabled() ){
			return null;
		}

		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);

		if (viewPart instanceof JobListView) {
			JobListView view = (JobListView) viewPart.getAdapter(JobListView.class);
			item = view.getSelectJobTreeItemList().get(0);

			if (item instanceof JobTreeItem) {
				JobTreeItem manager = JobTreeItemUtil.getManager(item);
				String managerName = manager.getData().getName();
				JobEditState jobEditState = JobEditStateUtil.getJobEditState( managerName );
				if(jobEditState.isEditing()){
					// 確認ダイアログを生成
					MessageDialog.openWarning(
							null,
							Messages.getString("confirmed"),
							Messages.getString("message.job.43") + "\n" +
									Messages.getString("message.job.44"));
					return null;
				}

				// 確認ダイアログを生成
				Object[] args1 = { item.getData().getName(), item.getData().getId(), item.getData().getJobunitId() };
				//ダイアログ表示
				JobRunConfirm dialog = new JobRunConfirm(null);
				dialog.setMessageText(Messages.getString(Messages.getString("message.job.32"), args1));
				if (dialog.open() == IDialogConstants.OK_ID) {
					m_trigger = dialog.getInputData();
					//ジョブ実行
					try {
						// 実行契機情報を登録
						JobTriggerInfo triggerInfo = new JobTriggerInfo();
						triggerInfo.setTriggerType(JobTriggerTypeConstant.TYPE_MANUAL);
						triggerInfo.setJobWaitTime(m_trigger.isJobWaitTime());
						triggerInfo.setJobWaitMinute(m_trigger.isJobWaitMinute());
						triggerInfo.setJobCommand(m_trigger.isJobCommand());
						triggerInfo.setJobCommandText(m_trigger.getJobCommandText());

						JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
						wrapper.runJob(
								item.getData().getJobunitId(),
								item.getData().getId(),
								null,
								triggerInfo);
					} catch (InvalidRole_Exception e) {
						MessageDialog.openInformation(null, Messages.getString("message"),
								Messages.getString("message.accesscontrol.16"));
					} catch (Exception e) {
						m_log.warn("run(), " + e.getMessage(), e);
						MessageDialog.openError(
								null,
								Messages.getString("failed"),
								Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
					}
				}
			}
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
				if(part instanceof JobListView){
					// Enable button when 1 item is selected
					JobListView view = (JobListView)part;
					int size = view.getJobTreeComposite().getSelectItemList().size();
					if(size == 1) {
						if(view.getDataType() == JobConstant.TYPE_JOBUNIT ||
								view.getDataType() == JobConstant.TYPE_JOBNET ||
								view.getDataType() == JobConstant.TYPE_JOB ||
								view.getDataType() == JobConstant.TYPE_FILEJOB ||
								view.getDataType() == JobConstant.TYPE_REFERJOB){
							editEnable = true;
						}
					}
				}
				this.setBaseEnabled(editEnable);
			} else {
				this.setBaseEnabled(false);
			}
		}
	}
}
