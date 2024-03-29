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

import com.clustercontrol.jobmanagement.action.GetJobKickTableDefine;
import com.clustercontrol.jobmanagement.composite.JobKickListComposite;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.jobmanagement.view.JobKickListView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;

/**
 * ジョブ[スケジュール]ビューの「無効」のクライアント側アクションクラス<BR>
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class DisableJobKickAction extends AbstractHandler implements IElementUpdater {

	// ログ
	private static Log m_log = LogFactory.getLog( DisableJobKickAction.class );

	/** アクションID */
	public static final String ID = DisableJobKickAction.class.getName();
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
	 * メイン処理
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

		// 選択アイテムの取得
		JobKickListView view = (JobKickListView) this.viewPart.getAdapter(JobKickListView.class);

		JobKickListComposite composite = view.getComposite();
		StructuredSelection selection = (StructuredSelection) composite.getTableViewer().getSelection();

		Object [] objs = selection.toArray();

		// 1つも選択されていない場合
		if(objs.length == 0){
			MessageDialog.openConfirm(
					null,
					Messages.getString("confirmed"),
					Messages.getString("message.job.24"));
			return null;
		}


		// 1つ以上選択されている場合
		String scheduleId = null;
		String[] args;
		Map<String, List<String>> map = new ConcurrentHashMap<String, List<String>>();
		StringBuffer targetList = new StringBuffer();//表示対象のリスト(ターゲット)
		StringBuffer successList = new StringBuffer();//表示対象のリスト(成功)
		StringBuffer failureList = new StringBuffer();//表示対象のリスト(失敗)

		for (int i = 0; i < objs.length; i++) {
			String managerName = (String) ((ArrayList<?>)objs[i]).get(GetJobKickTableDefine.MANAGER_NAME);
			if(map.get(managerName) == null) {
				map.put(managerName, new ArrayList<String>());
			}
		}

		for (int i = 0; i < objs.length; i++) {
			String managerName = (String) ((ArrayList<?>)objs[i]).get(GetJobKickTableDefine.MANAGER_NAME);
			scheduleId = (String) ((ArrayList<?>)objs[i]).get(GetJobKickTableDefine.SCHE_ID);
			map.get(managerName).add(scheduleId);
			targetList.append(scheduleId + "\n");
		}

		// 実行確認(NG→終了)
		args = new String[]{ targetList.toString() } ;
		if (!MessageDialog.openConfirm(
				null,
				Messages.getString("confirmed"),
				Messages.getString("message.job.71", args))) {
			return null;
		}

		boolean hasRole = true;
		// 実行
		for(Map.Entry<String, List<String>> entry : map.entrySet()) {
			String managerName = entry.getKey();
			JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
			for(String targetId : entry.getValue()){
				scheduleId = targetId;
				try{
					wrapper.setJobKickStatus(scheduleId, false);
					successList.append(targetId + "(" + managerName + ")" + "\n");
				} catch (InvalidRole_Exception e) {
					failureList.append(targetId + "\n");
					m_log.warn("run() setScheduleStatus scheduleId=" + scheduleId + ", " + e.getMessage(), e);
					hasRole = false;
				}catch (Exception e) {
					failureList.append(targetId + "\n");
					m_log.warn("run() setScheduleStatus scheduleId=" + scheduleId + ", " + e.getMessage(), e);
				}
			}
		}

		if (!hasRole) {
			// 権限がない場合にはエラーメッセージを表示する
			MessageDialog.openInformation(null, Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));
		}

		// 成功ダイアログ
		if(successList.length() != 0){
			args = new String[]{ successList.toString() } ;
			MessageDialog.openInformation(
					null,
					Messages.getString("successful"),
					Messages.getString("message.job.72", args));
		}

		// 失敗ダイアログ
		if(failureList.length() != 0){
			args = new String[]{ failureList.toString() } ;
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.job.73", args));
		}

		// ビューコンポジット更新
		composite.update();
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
