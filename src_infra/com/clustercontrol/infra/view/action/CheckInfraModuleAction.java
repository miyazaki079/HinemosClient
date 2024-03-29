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
import org.eclipse.jface.dialogs.IDialogConstants;
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
import com.clustercontrol.infra.dialog.RunDialog;
import com.clustercontrol.infra.util.InfraEndpointWrapper;
import com.clustercontrol.infra.util.AccessUtil;
import com.clustercontrol.infra.util.ModuleUtil;
import com.clustercontrol.infra.view.InfraModuleView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.infra.AccessInfo;
import com.clustercontrol.ws.infra.FacilityNotFound_Exception;
import com.clustercontrol.ws.infra.HinemosUnknown_Exception;
import com.clustercontrol.ws.infra.InfraManagementInfo;
import com.clustercontrol.ws.infra.InfraManagementNotFound_Exception;
import com.clustercontrol.ws.infra.InfraModuleNotFound_Exception;
import com.clustercontrol.ws.infra.InvalidRole_Exception;
import com.clustercontrol.ws.infra.InvalidSetting_Exception;
import com.clustercontrol.ws.infra.InvalidUserPass_Exception;
import com.clustercontrol.ws.infra.ModuleResult;
import com.clustercontrol.ws.infra.NotifyNotFound_Exception;
import com.clustercontrol.ws.infra.SessionNotFound_Exception;

public class CheckInfraModuleAction extends AbstractHandler implements IElementUpdater {
	// ログ
	private static Log m_log = LogFactory.getLog( CheckInfraModuleAction.class );

	/** アクションID */
	public static final String ID = CheckInfraModuleAction.class.getName();

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

		InfraModuleView view = null;
		if(viewPart instanceof InfraModuleView){
			view = (InfraModuleView) viewPart.getAdapter(InfraModuleView.class);
		}
		if(view == null) {
			return null;
		}

		StructuredSelection selection = null;
		if(view != null && view.getComposite().getTableViewer().getSelection() instanceof StructuredSelection){
			selection = (StructuredSelection) view.getComposite().getTableViewer().getSelection();
		}

		InfraManagementInfo management = null;
		String managerName = view.getComposite().getManagerName();

		try {
			InfraEndpointWrapper wrapper = InfraEndpointWrapper.getWrapper(managerName);
			management = wrapper.getInfraManagement(view.getComposite().getManagementId());
		} catch (HinemosUnknown_Exception | InvalidRole_Exception | InvalidUserPass_Exception | NotifyNotFound_Exception | InfraManagementNotFound_Exception e1) {
			m_log.error("execute() : " + e1.getClass().getName() + ", " + e1.getMessage());
			MessageDialog.openError(null, Messages.getString("failed"),
					Messages.getString("message.infra.action.result", new Object[]{Messages.getString("infra.module.id"),
							Messages.getString("infra.module.check"), Messages.getString("failed"), e1.getMessage()}));
			return null;
		}

		if(selection == null || management == null){
			return null;
		}

		String moduleId = (String) ((ArrayList<?>)selection.getFirstElement()).get(GetInfraModuleTableDefine.MODULE_ID);


		boolean allRun = false;
		RunDialog dialog = new RunDialog(null, 
				Messages.getString("message.infra.confirm.action",
						new Object[]{Messages.getString("infra.module.id"),
						Messages.getString("infra.module.check"), moduleId}));
		if (dialog.open() == IDialogConstants.CANCEL_ID) {
			return null;
		}
		allRun = dialog.isAllRun();

		List<AccessInfo> accessInfoList = AccessUtil.getAccessInfoList(
				viewPart.getSite().getShell(), management.getFacilityId(), management.getOwnerRoleId(), managerName, view.isUseNodeProp());
		// ユーザ、パスワード、ポートの入力画面でキャンセルをクリックすると、nullが返ってくる。
		// その場合は、処理中断。
		if (accessInfoList == null) {
			return null;
		}
		List<String> moduleIdList = new ArrayList<String>();
		moduleIdList.add(moduleId);
		try {
			InfraEndpointWrapper wrapper = InfraEndpointWrapper.getWrapper(managerName);
			String sessionId = wrapper.createSession(management.getManagementId(), moduleIdList, accessInfoList);
			while (true) {
				ModuleResult moduleResult = wrapper.checkInfraModule(sessionId, !allRun);
				if (!allRun && !ModuleUtil.displayResult(moduleResult.getModuleId(), moduleResult)) {
					break;
				}
				if(!moduleResult.isHasNext()) {
					break;
				}
			}
			MessageDialog.openInformation(null, Messages.getString("message"), Messages.getString("message.infra.management.check.end"));
			wrapper.deleteSession(sessionId);
		} catch (HinemosUnknown_Exception | InvalidRole_Exception | InvalidUserPass_Exception |
				InfraManagementNotFound_Exception | InfraModuleNotFound_Exception | SessionNotFound_Exception | FacilityNotFound_Exception | InvalidSetting_Exception e1) {
			m_log.error("execute() :  " + e1.getClass().getName() + ", " + e1.getMessage());
			MessageDialog.openError(null, Messages.getString("failed"), Messages.getString("message.infra.action.result", new Object[]{Messages.getString("infra.module.id"), Messages.getString("infra.module.run"), Messages.getString("failed"), e1.getMessage()}));
			return null;
		}
		view.update(view.getComposite().getManagerName(), view.getComposite().getManagementId());
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
