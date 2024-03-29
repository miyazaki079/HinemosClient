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

package com.clustercontrol.jobmanagement.viewer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.clustercontrol.bean.JobImageConstant;
import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.util.JobEditStateUtil;
import com.clustercontrol.jobmanagement.util.JobTreeItemUtil;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobInfo;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;

/**
 * ジョブツリー用コンポジットのツリービューア用のLabelProviderクラスです。
 *
 * @version 2.0.0
 * @since 1.0.0
 */
public class JobTreeLabelProvider extends LabelProvider {
	private boolean printEditable = false; // [編集モード]と表示する場合はtrue

	public JobTreeLabelProvider() {
		super();
		this.printEditable = false;
	}

	public JobTreeLabelProvider(boolean printEditable) {
		super();
		this.printEditable = printEditable;
	}
	/**
	 * ジョブツリーアイテムから表示名を作成し返します。
	 *
	 * @param ジョブツリーアイテム
	 * @return 表示名
	 *
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		JobTreeItem item = (JobTreeItem) element;
		JobInfo info = item.getData();
		int type = info.getType();

		String editable = "";
		if (printEditable && type == JobConstant.TYPE_JOBUNIT ){
			String managerName = JobTreeItemUtil.getManagerName(item);
			if( JobEditStateUtil.getJobEditState(managerName).isLockedJobunitId(info.getJobunitId())) {
				editable = " ["+Messages.getString("edit.mode") + "]";
			}
		}

		if (type == JobConstant.TYPE_COMPOSITE) {
			return info.getName();
		} else if (type == JobConstant.TYPE_MANAGER) {
			return Messages.getString("facility.manager") + " (" + info.getName() + ")";
		} else {
			return info.getName() + " (" + info.getId() + ")" + editable;
		}
	}

	/**
	 * ジョブツリーアイテムのジョブ種別に該当するイメージを返します。
	 *
	 * @param ジョブツリーアイテム
	 * @return イメージ
	 *
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		JobTreeItem item = (JobTreeItem) element;
		int type = item.getData().getType();
		return JobImageConstant.typeToImage(type);
	}
}
