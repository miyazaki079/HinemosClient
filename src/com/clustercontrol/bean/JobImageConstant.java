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
import com.clustercontrol.jobmanagement.bean.JobConstant;

/**
 * ジョブイメージ定数のクラス<BR>
 *
 * @version 4.1.0
 * @since 1.0.0
 */
public class JobImageConstant extends JobConstant {
	private static Image jobUnit = null;

	private static Image jobUnitUnref = null;

	private static Image jobNet = null;

	private static Image job = null;

	private static Image fileJob = null;

	private static Image referJob = null;

	private static Image manager = null;

	/**
	 * 種別からImageに変換します。<BR>
	 *
	 * @param type
	 * @return
	 */
	public static Image typeToImage(int type) {
		ImageRegistry registry = ClusterControlPlugin.getDefault()
				.getImageRegistry();

		if (type == TYPE_JOBUNIT || type == TYPE_COMPOSITE) {
			if (jobUnit == null)
				jobUnit = registry.getDescriptor(
						ClusterControlPlugin.IMG_JOBUNIT).createImage();
			return jobUnit;
		} else if (type == TYPE_JOBUNIT_UNREFERABLE) {
			if (jobUnitUnref == null)
				jobUnitUnref = registry.getDescriptor(
						ClusterControlPlugin.IMG_JOBUNIT_UNREFERABLE).createImage();
			return jobUnitUnref;
		} else if (type == TYPE_JOBNET) {
			if (jobNet == null)
				jobNet = registry.getDescriptor(
						ClusterControlPlugin.IMG_JOBNET).createImage();
			return jobNet;
		} else if (type == TYPE_JOB) {
			if (job == null)
				job = registry.getDescriptor(
						ClusterControlPlugin.IMG_JOB).createImage();
			return job;
		} else if (type == TYPE_FILEJOB) {
			if (fileJob == null)
				fileJob = registry.getDescriptor(
						ClusterControlPlugin.IMG_FILEJOB).createImage();
			return fileJob;
		} else if (type == TYPE_REFERJOB) {
			if (referJob == null)
				referJob = registry.getDescriptor(
						ClusterControlPlugin.IMG_REFERJOB).createImage();
			return referJob;
		} else if (type == TYPE_MANAGER) {
			if (manager == null)
				manager = registry.getDescriptor(
						ClusterControlPlugin.IMG_CONSOLE).createImage();
			return manager;
		}

		return null;
	}
}
