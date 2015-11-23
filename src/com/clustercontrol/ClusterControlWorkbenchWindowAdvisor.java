/**********************************************************************
 * Copyright (C) 2014 NTT DATA Corporation
 * This program is free software; you can redistribute it and/or
 * Modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2.
 * 
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *********************************************************************/

package com.clustercontrol;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.clustercontrol.startup.ui.StartUpPerspective;
import com.clustercontrol.util.LoginManager;

/**
 * 
 * WorkbenchWindowAdvisorクラスを継承するクラス<BR>
 * RCPのWorkbenchWindowの設定などを行います。
 * 
 * @version 5.0.0
 * @since 2.0.0
 */
public class ClusterControlWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ClusterControlWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ClusterControlActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		// Initialize window size according to browser size
		Rectangle bounds = Display.getCurrent().getBounds();
		Rectangle rect = ClusterControlPlugin.WINDOW_INIT_SIZE;
		if(bounds.width < rect.width ){
			rect.width = bounds.width;
		}
		if(bounds.height < rect.height ){
			rect.height = bounds.height;
		}
		configurer.setInitialSize(new Point(rect.width, rect.height));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowPerspectiveBar(true);

		if( ClusterControlPlugin.isRAP() ){
			// Remove the title bar and buttons
			configurer.setShellStyle(SWT.NONE);

			// Add the following to prevent overflow auto-hiding on perspective bar
			IPreferenceStore prefStore = PlatformUI.getPreferenceStore();
			prefStore.setDefault( IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR, IWorkbenchPreferenceConstants.TOP_LEFT );
		}
	}

	@Override
	public void postWindowCreate(){
		super.postWindowCreate();

		// 起動時にログインダイアログを表示する。
		// パースペクティブが何もない時に必ずこのルートを通る。
		LoginManager.login();

		// Clear stored perspective layouts if login failed or cancelled
		if( !LoginManager.isLogin() ){
			IWorkbenchWindow window = getWindowConfigurer().getWindow();
			IWorkbenchPage page = window.getActivePage();
			IPerspectiveRegistry pr = window.getWorkbench().getPerspectiveRegistry();

			page.closeAllPerspectives( false, false );
			page.setPerspective( pr.findPerspectiveWithId( StartUpPerspective.ID ) );
			page.resetPerspective();
		}
	}

	@Override
	public void postWindowOpen(){
		super.postWindowOpen();

		// Web Client starts with maximized window
		if( ClusterControlPlugin.isRAP() ){
			IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
			configurer.getWindow().getShell().setMaximized(true);
		}
	}

}
