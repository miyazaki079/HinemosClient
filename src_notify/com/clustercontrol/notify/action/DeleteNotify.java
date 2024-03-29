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

package com.clustercontrol.notify.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.notify.util.NotifyEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.notify.InvalidRole_Exception;
import com.clustercontrol.ws.notify.NotifyCheckIdResultInfo;

/**
 * 通知情報を削除するクライアント側アクションクラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class DeleteNotify {

	// ログ
	private static Log m_log = LogFactory.getLog( DeleteNotify.class );

	/**
	 * 通知情報を削除します。<BR>
	 * マネージャにSessionBean経由でアクセスします。
	 *
	 * @param managerName マネージャ名
	 * @param notifyIdList 削除対象の通知IDリスト
	 * @return 削除に成功した場合、<code> true </code>
	 *
	 */
	public boolean delete(String managerName, List<String> notifyIdList) {

		boolean result = false;
		String msg = null;
		String[] args = new String[2];

		if (notifyIdList.isEmpty()) {
			return result;
		}

		if (notifyIdList.size() == 1) {
			args[0] = notifyIdList.get(0);
			args[1] = managerName;
			msg = "message.notify.5";
		} else {
			args[0] = (new Integer(notifyIdList.size()).toString());
			msg = "message.notify.52";
		}

		try {
			NotifyEndpointWrapper wrapper = NotifyEndpointWrapper.getWrapper(managerName);
			result = wrapper.deleteNotify(notifyIdList);

			MessageDialog.openInformation(
					null,
					Messages.getString("successful"),
					Messages.getString(msg, args));

		} catch (InvalidRole_Exception e) {
			MessageDialog.openInformation(null, Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));

		} catch (Exception e) {
			m_log.warn("delete(), " + e.getMessage(), e);
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		return result;
	}

	public int useCheck (String managerName, List<String> notifyIds){

		int result = Window.OK;
		List<NotifyCheckIdResultInfo> retList = null;
		List<String> notifyGroupIdList = null;

		try {
			NotifyEndpointWrapper wrapper = NotifyEndpointWrapper.getWrapper(managerName);
			retList = wrapper.checkNotifyId(notifyIds);

			for (NotifyCheckIdResultInfo checkResult : retList) {
				notifyGroupIdList = checkResult.getNotifyGroupIdList();

				if(notifyGroupIdList.size() != 0){
					String message = null;
					String notifyGroupId = null;
					String[] strings = null;
					String moduleName = "";
					ArrayList<String> checkList = new ArrayList<String>();	// 同一監視内で、同じ監視項目IDを表示しないようチェックするリスト（文字列監視用）
					String id = this.toString();

					Iterator<String> itr = notifyGroupIdList.iterator();

					String[] args = { checkResult.getNotifyId() };
					MultiStatus mStatus = new MultiStatus(this.toString(), IStatus.OK, Messages.getString("message.notify.26", args), null);
					IStatus status = null;

					while(itr.hasNext()){
						notifyGroupId = itr.next();
						strings = notifyGroupId.split("-");

						// HinemosModuleConstantに定義されている場合
						if(HinemosModuleConstant.isExist(strings[0]) &&
								!strings[0].equals(HinemosModuleConstant.JOB_SESSION)){	// JobSessionIDを除外

							// 監視の種別を出力（監視の種類が同じである場合は、出力しない）
							if(!moduleName.equals(strings[0])) {

								// 最初の一回目を除外する
								if(!moduleName.equals("")){
									status = new Status(IStatus.INFO, id, IStatus.OK, "", null);
									mStatus.add(status);
								}

								moduleName = strings[0];
								message = "[" + HinemosModuleConstant.nameToString(moduleName) + "]";
								checkList.clear();

								status = new Status(IStatus.INFO, id, IStatus.OK, message, null);
								mStatus.add(status);
							}

							// 監視項目IDを出力
							String monitorId = strings[1];
							for(int i = 2; i < strings.length - 1 ; i++){	// stringsの最後には、文字列監視用のindexが含まれているため除外
								monitorId = monitorId + "-" + strings[i];
							}

							if(!checkList.contains(monitorId)){
								checkList.add(monitorId);

								//	        			message = "  " + strings[1];
								message = "  " + monitorId;
								status = new Status(IStatus.INFO, id, IStatus.OK, message, null);
								mStatus.add(status);
							}
						}

					}

					// どの監視機能で使用されているかをダイアログで表示する
					result = ErrorDialog.openError(
							null,
							Messages.getString("info"),
							null,
							mStatus);

					continue;
				}
			}
		} catch (Exception e) {
			m_log.warn("useCheck(), " + e.getMessage(), e);
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}

		return result;
	}

}
