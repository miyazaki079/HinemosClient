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

package com.clustercontrol.performance.bean;

import com.clustercontrol.util.Messages;

/**
 * 実績収集の状態の定数クラス
 *
 * @version 4.0.0
 * @since 4.0.0
 *
 */
public class PerformanceStatusConstant {

	/** 初期状態 */
	public static final int TYPE_INIT = -1;

	/** 収集中(状態の種別) */
	public static final int TYPE_RUNNING = 1;

	/** 停止中(状態の種別) */
	public static final int TYPE_STOP = 0;

	/** 収集中(文字列) */
	public static final String STRING_RUNNING = Messages.getString("collection.running");

	/** 停止中(文字列) */
	public static final String STRING_STOP = Messages.getString("collection.stop");


	public static int stringToType(String type) {
		if (type.equals(STRING_RUNNING)) {
			return TYPE_RUNNING;
		} else if (type.equals(STRING_STOP)) {
			return TYPE_STOP;
		}
		return -1;
	}

	public static String typeToString(int type) {
		if (type == TYPE_RUNNING) {
			return STRING_RUNNING;
		} else if (type == TYPE_STOP) {
			return STRING_STOP;
		}
		return "";
	}
}
