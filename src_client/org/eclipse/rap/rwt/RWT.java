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

package org.eclipse.rap.rwt;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.service.DummyHttpServletRequest;
import org.eclipse.rap.rwt.service.UISession;

/**
 * RWT for RCP (Dummy class)
 * 
 * @since 5.0.0
 */
public class RWT {

	public final static String MNEMONIC_ACTIVATOR = "dummy";

	/* Create an unique UISession */
	private final static UISession uiSession = new UISession();
	private final static HttpServletRequest servletRequest = new DummyHttpServletRequest();

	public static UISession getUISession(){
		return uiSession;
	}

	public static HttpServletRequest getRequest() {
		return servletRequest;
	}
}
