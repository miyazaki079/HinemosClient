/*

Copyright (C) 2006 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.bean;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.repository.bean.FacilityConstant;

/**
 * ファシリティのイメージの定数クラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class FacilityImageConstant extends FacilityConstant {
	private static Image composite = null;

	private static Image scope = null;

	private static Image scopeInvalid = null;

	private static Image node = null;

	private static Image nodeInvalid = null;

	/**
	 * 種別から文字列に変換します。<BR>
	 *
	 * @param type
	 * @return
	 */
	public static Image typeToImage(int type) {
		ImageRegistry registry = ClusterControlPlugin.getDefault()
				.getImageRegistry();

		if (type == TYPE_COMPOSITE) {
			if (composite == null)
				composite = registry.getDescriptor(
						ClusterControlPlugin.IMG_CONSOLE).createImage();
			return composite;
		} else if (type == TYPE_SCOPE) {
			if (scope == null)
				scope = registry
				.getDescriptor(ClusterControlPlugin.IMG_SCOPE)
				.createImage();
			return scope;
		} else if (type == TYPE_SCOPE_INVALID) {
			if (scopeInvalid == null)
				scopeInvalid = registry
				.getDescriptor(ClusterControlPlugin.IMG_SCOPE_INVALID)
				.createImage();
			return scopeInvalid;
		} else if (type == TYPE_NODE) {
			if (node == null)
				node = registry.getDescriptor(ClusterControlPlugin.IMG_NODE)
				.createImage();
			return node;
		} else if (type == TYPE_NODE_INVALID) {
			if (nodeInvalid == null)
				nodeInvalid = registry.getDescriptor(ClusterControlPlugin.IMG_NODE_INVALID)
				.createImage();
			return nodeInvalid;
		}
		return null;
	}
}
