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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.infra.dialog.InfraFileDialog;
import com.clustercontrol.infra.util.InfraFileUtil;
import com.clustercontrol.infra.view.InfraFileManagerView;


public class ModifyInfraFileAction extends InfraFileManagerBaseAction {

	/** アクションID */
	public static final String ID = ModifyInfraFileAction.class.getName();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InfraFileManagerView view = getView(event);
		if (view == null) {
			return null;
		}

		//アップロードダイアログを開く
		InfraFileDialog dialog = new InfraFileDialog(
				this.viewPart.getSite().getShell(),
				InfraFileUtil.getManagerName(view),
				PropertyDefineConstant.MODE_MODIFY,
				InfraFileUtil.getSelectedInfraFileInfo(view));
		dialog.open();

		// ビューの更新
		view.update();

		return null;
	}
}
