/*

 Copyright (C) 2014 NTT DATA Corporation

 This program is free software; you can redistribute it and/or
 Modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation, version 2.

 This program is distributed in the hope that it will be
 useful, but WITHOUT ANY WARRANTY; without even the implied
 warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.http.viewer;

import com.clustercontrol.bean.ValidConstant;
import com.clustercontrol.monitor.bean.HttpStatusConstant;
import com.clustercontrol.monitor.run.composite.ITableItemCompositeDefine;
import com.clustercontrol.monitor.run.viewer.CommonTableLabelProvider;
import com.clustercontrol.ws.monitor.Pattern;

/**
 * 文字列監視の判定情報一覧のラベルプロバイダークラス<BR>
 * 
 * @version 5.0.0
 * @since 5.0.0
 */
public class PatternTableLabelProvider extends CommonTableLabelProvider<Pattern> {

	public PatternTableLabelProvider(ITableItemCompositeDefine<Pattern> define) {
		super(define);
	}

	/**
	 * カラム文字列を返します。
	 * 
	 * @since 2.1.0
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {

		if (element instanceof Pattern) {
			Pattern Pattern = (Pattern) element;
			if (columnIndex == GetPageTableDefine.ORDER_NO) {
				return String.valueOf(indexOf(Pattern) + 1);
			} else if (columnIndex == GetPatternTableDefine.PROCESS_TYPE) {
				return HttpStatusConstant.typeToString(Pattern.getProcessType());
			} else if (columnIndex == GetPatternTableDefine.PATTERN_STRING) {
				if (Pattern.getPattern() != null) {
					return Pattern.getPattern();
				}
			} else if (columnIndex == GetPatternTableDefine.DESCRIPTION) {
				if (Pattern.getDescription() != null) {
					return Pattern.getDescription();
				}
			} else if (columnIndex == GetPatternTableDefine.VALID_FLG) {
				return ValidConstant.typeToString(ValidConstant.booleanToType(Pattern.isValidFlg()));
			}
		}
		return "";
	}
}
