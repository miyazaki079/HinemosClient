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

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.infra.dialog.ModuleTypeDialog;
import com.clustercontrol.infra.view.InfraManagementView;
import com.clustercontrol.infra.view.InfraModuleView;


public class AddInfraModuleAction extends AbstractHandler  implements IElementUpdater {

	/** アクションID */
	public static final String ID = AddInfraModuleAction.class.getName();

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

		this.viewPart = HandlerUtil.getActivePart(event);

		InfraModuleView view = null;
		if(viewPart instanceof InfraModuleView){
			view = (InfraModuleView) viewPart.getAdapter(InfraModuleView.class);
		}

		if(view != null){
			// モジュールタイプダイアログを開く
			String managementId = view.getComposite().getManagementId();
			String managerName = view.getComposite().getManagerName();
			ModuleTypeDialog dialog = new ModuleTypeDialog(this.viewPart.getSite().getShell(), managerName, managementId, view);
			dialog.open();

			// ビューの更新
			view.update(managerName, view.getComposite().getManagementId());
		}
		return null;
	}

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
		this.window = null;
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
					if(view.getComposite().getManagementId() != null){
						enable = true;
					}
				} else if (part instanceof InfraManagementView){
					InfraManagementView view = (InfraManagementView) part.getAdapter(InfraManagementView.class);
					StructuredSelection selection = (StructuredSelection) view.getComposite().getTableViewer().getSelection();
					if(selection.size() == 1){
						enable = true;
					}
				}
				this.setBaseEnabled(enable);
			}
		}
	}

}
