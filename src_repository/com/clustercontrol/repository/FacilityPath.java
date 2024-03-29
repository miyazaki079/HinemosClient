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

package com.clustercontrol.repository;

import com.clustercontrol.repository.bean.FacilityConstant;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * スコープ情報をツリー構造化するためのクラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class FacilityPath {
	private String separator;

	public FacilityPath(String separator) {
		this.separator = separator;
	}

	/**
	 * ツリーアイテムの親子関係を表現するパス文字列を取得します。<BR>
	 * <p>
	 *
	 * 例）getPath() ⇒ "料金システム>顧客管理>WEB"
	 *
	 * @param separator
	 *            セパレーター
	 * @return パス文字列
	 */
	public String getPath(FacilityTreeItem item) {

		if (item == null) {
			return "";
		}

		// トップ("スコープ")の場合は、文字を出力しません。
		if (item.getData().getFacilityType() == FacilityConstant.TYPE_COMPOSITE) {
			return "";
		}

		StringBuffer buffer = new StringBuffer();

		buffer.append(item.getData().getFacilityName());

		// スコープの場合は、最後に">"を追加
		if (item.getData().getFacilityType() == FacilityConstant.TYPE_SCOPE) {
			FacilityTreeItem parent = item.getParent();
			while (parent != null
					&& parent.getData().getFacilityType() != FacilityConstant.TYPE_COMPOSITE
					&& parent.getData().getFacilityType() != FacilityConstant.TYPE_MANAGER) {

				buffer.insert(0, separator);
				buffer.insert(0, parent.getData().getFacilityName());
				parent = parent.getParent();
			}
			buffer.append(separator);
		}

		return buffer.toString();
	}

	/**
	 * セパレータを取得します。<BR>
	 *
	 * @return Returns the separator.
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * セパレータを設定します。<BR>
	 * @param separator
	 *            The separator to set.
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}
}
