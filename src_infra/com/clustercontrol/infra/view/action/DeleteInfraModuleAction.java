/*

Copyright (C) 2014 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.infra.view.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.clustercontrol.infra.action.GetInfraModuleTableDefine;
import com.clustercontrol.infra.util.InfraEndpointWrapper;
import com.clustercontrol.infra.view.InfraModuleView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.infra.HinemosUnknown_Exception;
import com.clustercontrol.ws.infra.InfraManagementDuplicate_Exception;
import com.clustercontrol.ws.infra.InfraManagementInfo;
import com.clustercontrol.ws.infra.InfraManagementNotFound_Exception;
import com.clustercontrol.ws.infra.InfraModuleInfo;
import com.clustercontrol.ws.infra.InvalidRole_Exception;
import com.clustercontrol.ws.infra.InvalidSetting_Exception;
import com.clustercontrol.ws.infra.InvalidUserPass_Exception;
import com.clustercontrol.ws.infra.NotifyDuplicate_Exception;
import com.clustercontrol.ws.infra.NotifyNotFound_Exception;

public class DeleteInfraModuleAction extends AbstractHandler implements IElementUpdater {
	// ログ
	private static Log m_log = LogFactory.getLog( DeleteInfraModuleAction.class );

	/** アクションID */
	public static final String ID = DeleteInfraModuleAction.class.getName();

	private IWorkbenchWindow window;
	/** ビュー */
	private IWorkbenchPart viewPart;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.window = HandlerUtil.getActiveWorkbenchWindow(event);
		// In case this action has been disposed
		if( null == this.window || !isEnabled() ){
			return null;
		}

		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);

		if (viewPart instanceof InfraModuleView) {
			InfraModuleView view = (InfraModuleView) viewPart.getAdapter(InfraModuleView.class);

			StructuredSelection selection = null;
			if(view.getComposite().getTableViewer().getSelection() instanceof StructuredSelection){
				selection = (StructuredSelection) view.getComposite().getTableViewer().getSelection();
			}

			List<String> moduleIds = new ArrayList<String>();
			StringBuffer strModuleIds = new StringBuffer();
			String tmpModuleId = null;
			if(selection != null){
				for(Object object: selection.toList()){
					tmpModuleId = (String) ((ArrayList<?>)object).get(GetInfraModuleTableDefine.MODULE_ID);
					moduleIds.add(tmpModuleId);
					strModuleIds.append(tmpModuleId + ", ");
				}
				strModuleIds.setLength(strModuleIds.length() - 2);
			}

			InfraManagementInfo info = null;
			try {
				InfraEndpointWrapper wrapper = InfraEndpointWrapper
						.getWrapper(view.getComposite().getManagerName());
				info = wrapper.getInfraManagement(view.getComposite().getManagementId());
			} catch (HinemosUnknown_Exception | InvalidRole_Exception | InvalidUserPass_Exception | NotifyNotFound_Exception | InfraManagementNotFound_Exception e) {
				m_log.debug("execute getInfraManagement, " + e.getMessage());
			}

			List<InfraModuleInfo> tmpDeleteList = new ArrayList<InfraModuleInfo>();
			if(info != null && info.getModuleList() != null){
				for(String moduleId: moduleIds){
					for(InfraModuleInfo module: info.getModuleList()){
						if(module.getModuleId().equals(moduleId)){
							tmpDeleteList.add(module);
							break;
						}
					}
				}

				info.getModuleList().removeAll(tmpDeleteList);

				if (MessageDialog.openConfirm(
						null,
						Messages.getString("confirmed"),
						Messages.getString("message.infra.confirm.action", new Object[]{Messages.getString("infra.module"), Messages.getString("delete"), strModuleIds})))
				{
					try {
						InfraEndpointWrapper wrapper = InfraEndpointWrapper
								.getWrapper(view.getComposite().getManagerName());
						wrapper.modifyInfraManagement(info);
						MessageDialog.openInformation(null, Messages
								.getString("successful"), Messages.getString(
								"message.infra.action.result",
								new Object[] {
										Messages.getString("infra.module"),
										Messages.getString("delete") + "(" + view.getComposite().getManagerName() +")",
										Messages.getString("successful"),
										strModuleIds }));
					} catch (HinemosUnknown_Exception | InvalidRole_Exception | InvalidUserPass_Exception | InvalidSetting_Exception | NotifyDuplicate_Exception | NotifyNotFound_Exception | InfraManagementNotFound_Exception | InfraManagementDuplicate_Exception e) {
						m_log.debug("execute modifyInfraManagement, " + e.getMessage());
						MessageDialog.openError(
								null,
								Messages.getString("failed"),
								Messages.getString("message.infra.action.result", new Object[]{Messages.getString("infra.modlue"), Messages.getString("delete"), Messages.getString("failed"), e.getMessage()}));
					}

					view.update(view.getComposite().getManagerName(), info.getManagementId());
				}

			}
		}
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		boolean enable = false;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// page may not start at state restoring
		if( null != window ){
			IWorkbenchPage page = window.getActivePage();
			if( null != page ){
				IWorkbenchPart part = page.getActivePart();

				if(part instanceof InfraModuleView){
					InfraModuleView view = (InfraModuleView) part.getAdapter(InfraModuleView.class);
					// Enable button when 1 item is selected
					StructuredSelection selection = null;
					if(view.getComposite().getTableViewer().getSelection() instanceof StructuredSelection){
						selection = (StructuredSelection) view.getComposite().getTableViewer().getSelection();
					}
					if(selection != null && selection.size() > 0){
						enable = true;
					}
				}
				this.setBaseEnabled(enable);
			} else {
				this.setBaseEnabled(false);
			}
		}
	}

	@Override
	public void dispose() {
		this.viewPart = null;
		this.window = null;
	}

}
