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

package com.clustercontrol.repository.view;

import java.util.ArrayList;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.bean.TableColumnInfo;
import com.clustercontrol.repository.composite.NodeAttributeComposite;
import com.clustercontrol.repository.composite.NodeListComposite;
import com.clustercontrol.view.CommonViewPart;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * ノード属性ビュークラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class NodeAttributeView extends CommonViewPart {
	public static final String ID = NodeAttributeView.class.getName();

	// ----- instance フィールド ----- //

	/** ノード属性一覧コンポジット */
	private NodeAttributeComposite composite = null;

	private String orgViewName = null;
	// ----- コンストラクタ ----- //

	/**
	 * インスタンスを返します。
	 */
	public NodeAttributeView() {
		super();
	}

	// ----- instance メソッド ----- //

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		this.composite = new NodeAttributeComposite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.composite.setLayoutData(gridData);

		orgViewName = this.getPartName();
		this.update();
	}

	/**
	 * compositeを返します。
	 *
	 * @return composite
	 */
	public NodeAttributeComposite getComposite() {
		return this.composite;
	}

	/**
	 * ノード一覧にて選択されているノードの内容を表示します。
	 */
	public void update() {
		IWorkbench workbench = ClusterControlPlugin.getDefault().getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
				.getActivePage();

		NodeListView view = null;
		if (page != null) {
			IViewPart viewPart = page.findView(NodeListView.ID);
			if (viewPart != null){
				view = (NodeListView)viewPart.getAdapter(NodeListView.class);
			}
		}

		String managerName = null;
		String facilityId = null;
		if (view != null) {
			NodeListComposite composite = view.getComposite();
			CommonTableViewer viewer = composite.getTableViewer();
			StructuredSelection selection = (StructuredSelection) viewer
					.getSelection();

			ArrayList<?> list = (ArrayList<?>) selection.getFirstElement();
			if (list != null) {
				facilityId = (String) list.get(viewer
						.getTableColumnIndex(TableColumnInfo.FACILITY_ID));
				managerName = (String) list.get(viewer
						.getTableColumnIndex(TableColumnInfo.MANAGER_NAME));
				if(managerName == null || managerName.equals("")) {
					return;
				}
				String viewName = orgViewName + "(" + managerName + ")";
				setPartName(viewName);
			}
		}
		this.composite.update(managerName, facilityId);
	}
}
