/*

Copyright (C) since 2010 NTT DATA Corporation

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
 * 性能[一覧]ビューのフィルタ条件の定数クラス
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class PerformanceFilterConstant {

	/** マネージャ */
	public static final String MANAGER = "manager";
	/** 監視ID。 */
	public static final String MONITOR_ID = "monitorId";
	/** 監視種別ID。 */
	public static final String MONITOR_TYPE_ID = "monitortypeId";
	/** 説明。 */
	public static final String DESCRIPTION = "description";
	/** 最古収集日時（開始）。 */
	public static final String OLDEST_FROM_DATE = "oldestFromDate";
	/** 最古収集日時（終了）。 */
	public static final String OLDEST_TO_DATE = "oldestToDate";
	/** 最新収集日時（開始）。 */
	public static final String LATEST_FROM_DATE = "latestFromDate";
	/** 最新収集日時（終了）。 */
	public static final String LATEST_TO_DATE = "latestToDate";

	/** 最古収集日時。 */
	public static final String OLDEST_DATE = "oldestDate";
	/** 最新収集日時。 */
	public static final String LATEST_DATE = "latestDate";

}
