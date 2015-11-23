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

/**
 * グラフ表示に使用する定数クラス
 * 
 * @version 1.0.0
 * @since 1.0.0
 */
public class GraphConstant {

	public static final int PLOT_NUM_1 = 12;

	public static final int PLOT_NUM_2 = 24;

	public static final int PLOT_NUM_3 = 100;

	public static final int PLOT_NUM_4 = 200;

	public static final int REALTIME_GRAPH_MAX_PLOT = 200;

	public static final int RANGE_HOUR = 0;

	public static final int RANGE_DAY = 1;

	public static final int RANGE_WEEK = 2;

	public static final int RANGE_MONTH = 3;

	public static final int RANGE_VERTICAL_FREE = 0;

	public static final int RANGE_VERTICAL_FIXED = 1;

	// グラフ種別
	public static final int GRAPH_LINE = 0;		// 折れ線グラフ
	public static final int GRAPH_STACK = 1;	// 積み上げグラフ

	// 表示種別
	public static final int DISPLAY_NODE = 0;		// ノード別表示
	public static final int DISPLAY_ITEMCODE = 1;	// 収集項目別表示
	public static final int DISPLAY_DEVICE = 2;		// デバイス別表示
}
