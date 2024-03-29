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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.activation.DataHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.client.ui.util.FileDownloader;
import com.clustercontrol.infra.action.GetInfraFileManagerTableDefine;
import com.clustercontrol.infra.util.InfraEndpointWrapper;
import com.clustercontrol.infra.util.InfraFileUtil;
import com.clustercontrol.infra.view.InfraFileManagerView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.infra.InfraFileInfo;


public class DownloadInfraFileAction extends InfraFileManagerBaseAction {
	// ログ
	private static Log m_log = LogFactory.getLog( DownloadInfraFileAction.class );

	/** アクションID */
	public static final String ID = DownloadInfraFileAction.class.getName();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		InfraFileManagerView view = getView(event);
		if (view == null) {
			return null;
		}

		InfraFileInfo info = InfraFileUtil.getSelectedInfraFileInfo(view);
		if (info == null) {
			return null;
		}

		String action = Messages.getString("download");

		FileDialog fd = new FileDialog(window.getShell(), SWT.SAVE);
		String fileName = info.getFileName();
		fd.setFileName(fileName);
		String selectedFilePath = null;
		try{
			selectedFilePath = fd.open();
		}catch( Exception e ){
			m_log.error(e);
 			InfraFileUtil.showFailureDialog(action, e.getMessage());
 			return null;
		}

		// path is null when dialog cancelled
		if( null != selectedFilePath ){
			StructuredSelection selection = null;
			if(view != null && view.getComposite().getTableViewer().getSelection() instanceof StructuredSelection){
				selection = (StructuredSelection) view.getComposite().getTableViewer().getSelection();
			}
			String managerName = null;
			if(selection != null && selection.isEmpty() == false){
				managerName = (String)((ArrayList<?>)selection.getFirstElement()).get(GetInfraFileManagerTableDefine.MANAGER_NAME);
			}
			InfraEndpointWrapper wrapper = InfraEndpointWrapper.getWrapper(managerName);
			FileOutputStream fos = null;
			try {
				DataHandler dh = wrapper.downloadInfraFile(info.getFileId(), fileName);
				fos = new FileOutputStream(new File(selectedFilePath));
				dh.writeTo(fos);
				if( ClusterControlPlugin.isRAP() ){
					FileDownloader.openBrowser(window.getShell(), selectedFilePath, fileName);
				}else{
					InfraFileUtil.showSuccessDialog(action, info.getFileId());
				}
				wrapper.deleteDownloadedInfraFile(fileName);
			} catch (Exception e) {
				m_log.error(e);
				InfraFileUtil.showFailureDialog(action, e.getMessage());
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
					}
				}
			}
		}

		return null;
	}
}
