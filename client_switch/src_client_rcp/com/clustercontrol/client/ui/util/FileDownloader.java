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

package com.clustercontrol.client.ui.util;

import org.eclipse.swt.widgets.Composite;

/**
 * File download with Browser widget (Dummy class)
 * 
 * @version 5.0.0
 * @since 5.0.0
 */
public class FileDownloader {
	public static boolean openBrowser( Composite parent, String path, String filename ){
		// Do nothing
		return false;
	}
	public static void cleanup( String path ){
		// Do nothing
	}
}
