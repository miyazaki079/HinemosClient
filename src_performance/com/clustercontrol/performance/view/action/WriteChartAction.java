/**********************************************************************
 * Copyright (C) 2006 NTT DATA Corporation
 * This program is free software; you can redistribute it and/or
 * Modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *********************************************************************/
package com.clustercontrol.performance.view.action;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jfree.chart.ChartUtilities;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.client.ui.util.FileDownloader;
import com.clustercontrol.performance.view.PerformanceGraphView;
import com.clustercontrol.util.Messages;

public class WriteChartAction extends AbstractHandler {
	// ログ
	private static Log m_log = LogFactory.getLog(WriteChartAction.class);

	public static final String ID = WriteChartAction.class.getName();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow( event );
		IWorkbenchPage page = window.getActivePage();

		// 同じクラスのViewを複数作るため、IDで特定不可
		// アクティブなviewを取得する
		String viewTitle = page.getActivePart().getTitle();
		IViewReference[] refs = page.getViewReferences();
		for (IViewReference ref : refs) {

			if (! (ref.getView(false) instanceof PerformanceGraphView)){
				continue;
			}
			PerformanceGraphView view = (PerformanceGraphView) ref.getView(false);
			if (! view.getTitle().equals(viewTitle)){
				continue;
			}
			// シェルを取得
			Shell shell = window.getShell();

			// ファイルダイアログを開く
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			String[] extensions = { ".png" };
			dialog.setFilterExtensions(extensions);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String defaultFileName = viewTitle + '-'
					+ sdf.format(new Date(System.currentTimeMillis()));
			dialog.setFileName(defaultFileName);

			String filePath = dialog.open();
			if (null != filePath && !filePath.trim().isEmpty()) {
				FileOutputStream out = null;
				boolean success = false;
				try {
					out = new FileOutputStream(filePath);
					ChartUtilities.writeChartAsPNG(out, view.getChart(), 640, 480);
					// Start download file
					if (ClusterControlPlugin.isRAP()) {
						success = FileDownloader.openBrowser(shell, filePath,
								defaultFileName + extensions[0]);
					}else{
						success = true;
					}
				} catch (FileNotFoundException e) {
					m_log.debug(e);
				} catch (IOException e) {
					m_log.debug(e);
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							m_log.debug(e);
						}
					}
					if (ClusterControlPlugin.isRAP()) {
						FileDownloader.cleanup( filePath );
					}
				}

				if (success) {
					MessageDialog.openInformation(null, Messages.getString("successful"),
							Messages.getString("performance.write.chart.success"));
				} else {
					MessageDialog.openError(null, Messages.getString("failed"),
							Messages.getString("performance.write.chart.cancel"));
				}
			}
		}
		return null;
	}
}
