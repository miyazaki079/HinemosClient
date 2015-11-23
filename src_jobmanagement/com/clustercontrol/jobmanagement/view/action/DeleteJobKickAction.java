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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.jobmanagement.action.GetJobKick;
import com.clustercontrol.jobmanagement.action.GetJobKickTableDefine;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.jobmanagement.view.JobKickListView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.UIManager;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;

/**
 * ジョブ[実行契機]ビューの「削除」のクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class DeleteJobKickAction extends AbstractHandler implements IElementUpdater {

	// ログ
	private static Log m_log = LogFactory.getLog( DeleteJobKickAction.class );

	/** アクションID */
	public static final String ID = DeleteJobKickAction.class.getName();
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
	 * ジョブ[実行契機]ビューの「削除」が押された場合に、
	 * スケジュールまたは、ファイルチェックを削除します。
	 * <p>
	 * <ol>
	 * <li>ジョブ[実行契機]ビューから選択されたスケジュールを取得します。</li>
	 * <li>削除の確認ダイアログを表示します。</li>
	 * <li>スケジュールを削除します。</li>
	 * <li>ジョブ[スケジュール]ビューを更新します。</li>
	 * </ol>
	 *
	 * @see org.eclipse.core.commands.IHandler#execute
	 * @see com.clustercontrol.jobmanagement.view.JobKickListView
	 * @see com.clustercontrol.jobmanagement.composite.JobKickListComposite
	 * @see com.clustercontrol.jobmanagement.action.DeleteSchedule#deleteSchedule(String)
	 * @see com.clustercontrol.jobmanagement.action.DeleteFileCheck#deleteFileCheck(String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == this.window || !isEnabled() ){
			return null;
		}

		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);

		if (viewPart instanceof JobKickListView) {
			JobKickListView view = (JobKickListView) viewPart
					.getAdapter(JobKickListView.class);

			StructuredSelection selection = (StructuredSelection) view
					.getComposite().getTableViewer().getSelection();

			List<?> list = (List<?>)selection.toList();

			Map<String, List<String>> scheIdMap = new ConcurrentHashMap<String, List<String>>();
			Map<String, List<List<String>>[]> jobKickIdMap = new ConcurrentHashMap<String, List<List<String>>[]>();

			for(Object obj : list) {
				List<?> objList = (List<?>)obj;
				String managerName = (String) objList.get(GetJobKickTableDefine.MANAGER_NAME);
				if(scheIdMap.get(managerName) == null) {
					scheIdMap.put(managerName, new ArrayList<String>());
				}
				if(jobKickIdMap.get(managerName) == null) {
					jobKickIdMap.put(managerName, new ArrayList[2]);
					jobKickIdMap.get(managerName)[0] = new ArrayList<List<String>>();
					jobKickIdMap.get(managerName)[1] = new ArrayList<List<String>>();
				}
			}

			for(Object obj : list) {
				List<?> objList = (List<?>)obj;
				String managerName = (String) objList.get(GetJobKickTableDefine.MANAGER_NAME);
				String id = (String) objList.get(GetJobKickTableDefine.SCHE_ID);
				scheIdMap.get(managerName).add(id);
			}

			String scheId = null;
			String fileId = null;
			int scheSize = 0;
			int fileCheckSize = 0;
			for(Map.Entry<String, List<String>> entry : scheIdMap.entrySet()) {
				String managerName = entry.getKey();
				List<String> jobKickIdList = entry.getValue();
				List<ArrayList<String>> jobKickList = GetJobKick.getJobKick(managerName, jobKickIdList);

				List<String> scheduleList = jobKickList.get(0);
				List<String> filecheckList = jobKickList.get(1);

				jobKickIdMap.get(managerName)[0].add(scheduleList);
				jobKickIdMap.get(managerName)[1].add(filecheckList);
				scheId = scheduleList.isEmpty() ? null : scheduleList.get(0);
				fileId = filecheckList.isEmpty() ? null : filecheckList.get(0);
				scheSize += scheduleList.size();
				fileCheckSize += filecheckList.size();
			}

			//実行契機[スケジュール]を選択した場合
			String message = null;
			if(jobKickIdMap.isEmpty()){
				return null;
			} else if(scheSize == 1) {
				message = Messages.getString("schedule") + "["
						+ scheId + "]"
						+ Messages.getString("message.job.2");
			} else if(scheSize > 1){
				Object[] args = {scheSize, Messages.getString("schedule")};
				message = Messages.getString("message.job.123", args);
			}

			if(scheSize > 0) {
				if (MessageDialog.openQuestion(
						null,
						Messages.getString("confirmed"),
						message) == false) {
					return null;
				}

				Map<String, String> errorMsgs = new ConcurrentHashMap<>();

				StringBuffer messageArg = new StringBuffer();
				int i = 0;
				for(Map.Entry<String, List<List<String>>[]> entry : jobKickIdMap.entrySet()) {
					String managerName = entry.getKey();

					if(i > 0) {
						messageArg.append(", ");
					}
					messageArg.append(managerName);

					JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
					for(List<String> scheduleList : entry.getValue()[0]) {
						try {
							wrapper.deleteSchedule(scheduleList);
						} catch (InvalidRole_Exception e) {
							errorMsgs.put(managerName, Messages.getString("message.accesscontrol.16"));
						} catch (Exception e) {
							m_log.warn("run(), " + e.getMessage(), e);
							errorMsgs.put(managerName, Messages.getString("message.hinemos.failure.unexpected") + e.getMessage());
						}
					}
					i++;
				}

				//メッセージ表示
				if( 0 < errorMsgs.size() ){
					UIManager.showMessageBox(errorMsgs, true);
				} else {
					Object[] arg = {messageArg.toString()};
					// 完了メッセージ
					MessageDialog.openInformation(null, Messages.getString("successful"),
							Messages.getString("message.job.75", arg));
				}
			}

			//実行契機[スケジュール]を選択した場合
			if(fileCheckSize == 1) {
				message = Messages.getString("file.check") + "["
						+ fileId + "]"
						+ Messages.getString("message.job.2");
			} else if(fileCheckSize > 1){
				Object[] args = {fileCheckSize, Messages.getString("file.check")};
				message = Messages.getString("message.job.123", args);
			}

			if(fileCheckSize > 0) {
				if (MessageDialog.openQuestion(
						null,
						Messages.getString("confirmed"),
						message) == false) {
					return null;
				}

				Map<String, String> errorMsgs = new ConcurrentHashMap<>();
				StringBuffer messageArg = new StringBuffer();
				int i = 0;
				for(Map.Entry<String, List<List<String>>[]> entry : jobKickIdMap.entrySet()) {
					String managerName = entry.getKey();

					if(i > 0) {
						messageArg.append(", ");
					}
					messageArg.append(managerName);

					JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
					for(List<String> filecheckList : entry.getValue()[1]) {
						//実行契機[ファイルチェック]を選択した場合
						try {
							wrapper.deleteFileCheck(filecheckList);
						} catch (InvalidRole_Exception e) {
							errorMsgs.put(managerName, Messages.getString("message.accesscontrol.16"));
						} catch (Exception e) {
							m_log.warn("run(), " + e.getMessage(), e);
							errorMsgs.put(managerName, Messages.getString("message.hinemos.failure.unexpected") + e.getMessage());
						}
					}
					i++;
				}

				//メッセージ表示
				if( 0 < errorMsgs.size() ){
					UIManager.showMessageBox(errorMsgs, true);
				} else {
					Object[] arg = {messageArg.toString()};
					MessageDialog.openInformation(null, Messages.getString("successful"),
							Messages.getString("message.job.75", arg));
				}
			}

			view.update();
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
				if( part instanceof JobKickListView  ){
					// Enable button when 1 item is selected
					JobKickListView view = (JobKickListView)part;
					if(view.getSelectedNum() > 0) {
						editEnable = true;
					}
				}
				this.setBaseEnabled( editEnable );
			} else {
				this.setBaseEnabled(false);
			}
		}
	}
}
