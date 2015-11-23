/*

Copyright (C) 2008 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.notify.mail.action;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;

import com.clustercontrol.notify.mail.util.MailTemplateEndpointWrapper;
import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.mailtemplate.InvalidRole_Exception;
import com.clustercontrol.ws.mailtemplate.MailTemplateInfo;

/**
 * メールテンプレート情報を取得するクライアント側アクションクラス<BR>
 *
 * @version 2.4.0
 * @since 2.4.0
 */
public class GetMailTemplate {

	// ログ
	private static Log m_log = LogFactory.getLog( GetMailTemplate.class );

	/**
	 * メールテンプレート情報一覧を返します。<BR>
	 * マネージャにSessionBean経由でアクセスします。
	 *
	 * @return メールテンプレート情報一覧
	 *
	 */
	public Map<String, List<MailTemplateInfo>> getMailTemplateList() {

		Map<String, List<MailTemplateInfo>> dispDataMap= new ConcurrentHashMap<>();
		Map<String, String> infoMsgs = new ConcurrentHashMap<>();
		Map<String, String> failMsgs = new ConcurrentHashMap<>();
		List<MailTemplateInfo> records = null;
		for (String managerName : EndpointManager.getActiveManagerSet()) {
			try {
				MailTemplateEndpointWrapper wrapper = MailTemplateEndpointWrapper.getWrapper(managerName);
				records = wrapper.getMailTemplateList();
				dispDataMap.put(managerName, records);
			} catch (InvalidRole_Exception e) {
				infoMsgs.put( managerName, Messages.getString("message.accesscontrol.16") );
			} catch (Exception e) {
				m_log.warn("getNotifyListByOwnerRole(), " + e.getMessage(), e);
				failMsgs.put( managerName, Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
			}
		}

		//メッセージ表示
		if( 0 < infoMsgs.size() ){
			showMessageBox( infoMsgs, Messages.getString("message"), false );
		}
		if( 0 < failMsgs.size() ){
			showMessageBox( failMsgs, Messages.getString("failed"), true );
		}
		return dispDataMap;
	}

	/**
	 * オーナーロールIDを条件としてメールテンプレート情報一覧を返します。<BR>
	 * マネージャにSessionBean経由でアクセスします。
	 *
	 * @param managerName
	 * @param ownerRoleId
	 * @return メールテンプレート情報一覧
	 *
	 */
	public List<MailTemplateInfo> getMailTemplateListByOwnerRole(String managerName, String ownerRoleId) {

		List<MailTemplateInfo> records = null;
		try {
			MailTemplateEndpointWrapper wrapper = MailTemplateEndpointWrapper.getWrapper(managerName);
			records = wrapper.getMailTemplateListByOwnerRole(ownerRoleId);
		} catch (InvalidRole_Exception e) {
			MessageDialog.openInformation(null, Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));
		} catch (Exception e) {
			m_log.warn("getMailTemplateListByOwnerRole(), " + e.getMessage(), e);
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}

		return records;
	}

	private void showMessageBox( Map<String, String> msgs, String title, boolean isError ){
		String msg = "";
		for( Map.Entry<String, String> e : msgs.entrySet() ){
			String eol = System.getProperty("line.separator");
			msg += "MANAGER[" + e.getKey() + "] : " + eol + "    " + e.getValue() + eol + eol;
		}
		MessageDialog.open(isError ? MessageDialog.ERROR : MessageDialog.INFORMATION, null, title, msg, SWT.NONE);
	}
}
