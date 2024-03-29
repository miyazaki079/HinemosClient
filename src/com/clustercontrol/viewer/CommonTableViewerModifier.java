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

package com.clustercontrol.viewer;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ICellModifier;

import com.clustercontrol.bean.TableColumnInfo;

/**
 * CommonTableViewerクラス用のModifierクラス<BR>
 * 
 * @version 2.2.0
 * @since 2.2.0
 */
public class CommonTableViewerModifier implements ICellModifier {
	private CommonTableViewer m_viewer;

	/**
	 * コンストラクタ
	 * 
	 * @param viewer
	 */
	public CommonTableViewerModifier(CommonTableViewer viewer) {
		this.m_viewer = viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public Object getValue(Object element, String property) {

		Integer index = Integer.valueOf(property).intValue();

		ArrayList<?> list = (ArrayList<?>) element;
		Object item = list.get(index);

		ArrayList<TableColumnInfo> tableColumnList = m_viewer.getTableColumnList();

		TableColumnInfo tableColumn = tableColumnList.get(index);

		if(tableColumn.getType() == TableColumnInfo.TEXT_DIALOG) {
			//上記以外のデータタイプの処理
			return item;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void modify(Object element, String property, Object value) {

	}
}
