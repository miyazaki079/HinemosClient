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

package com.clustercontrol.performance.action;

import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.swt.SWT;

import com.clustercontrol.bean.TableColumnInfo;
import com.clustercontrol.util.Messages;

/**
 * 性能一覧テーブル定義情報を取得するクライアント側アクションクラス<BR>
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class GetPerformanceListTableDefine {

	/** マネージャ名 */
	public static final int MANAGER_NAME = 0;

	/**
	 * 状態
	 */
	public static final int STATUS = 1;

	/** 監視項目ID */
	public static final int MONITOR_ID = 2;

	/** プラグインID */
	public static final int MONITOR_TYPE_ID = 3;

	/** 説明 */
	public static final int DESCRIPTION = 4;

	/**
	 * ファシリティ
	 *
	 * ファシリティ名(ファシリティID)の形式で表示する
	 *
	 */
	public static final int FACILITY = 5;

	/** 収集間隔 */
	public static final int RUN_INTERVAL = 6;

	/**
	 * 蓄積されている収集データのうちもっとも古いものの日付時刻
	 */
	public static final int OLDEST_DATE = 7;

	/**
	 * 蓄積されている収集データのうちもっとも新しいものの日付時刻
	 */
	public static final int LATEST_DATE = 8;

	/** ダミー**/
	public static final int DUMMY = 9;

	/** 初期表示時ソートカラム */
	public static final int SORT_COLUMN_INDEX1 = MANAGER_NAME;
	public static final int SORT_COLUMN_INDEX2 = MONITOR_ID;

	/** 初期表示時ソートオーダー */
	public static final int SORT_ORDER = 1;


	/**
	 *性能一覧のテーブル定義情報を返します。
	 *
	 * @return 性能一覧テーブル定義情報
	 */
	public static ArrayList<TableColumnInfo> get() {
		// テーブル情報定義配列
		ArrayList<TableColumnInfo> tableDefine = new ArrayList<TableColumnInfo>();
		Locale locale = Locale.getDefault();

		// マネージャ名
		tableDefine.add(MANAGER_NAME,
				new TableColumnInfo(Messages.getString("facility.manager", locale), TableColumnInfo.NONE, 100, SWT.LEFT));

		// 状態
		tableDefine.add(STATUS,
				new TableColumnInfo(Messages.getString("run.status", locale), TableColumnInfo.COLLECT_STATUS, 70, SWT.LEFT));

		// 監視項目ID
		tableDefine.add(MONITOR_ID,
				new TableColumnInfo(Messages.getString("monitor.id", locale), TableColumnInfo.NONE, 100, SWT.LEFT));

		// 監視項目ID
		tableDefine.add(MONITOR_TYPE_ID,
				new TableColumnInfo(Messages.getString("plugin.id", locale), TableColumnInfo.NONE, 100, SWT.LEFT));

		// 説明
		tableDefine.add(DESCRIPTION,
				new TableColumnInfo(Messages.getString("description", locale), TableColumnInfo.NONE, 200, SWT.LEFT));

		// ファシリティ
		tableDefine.add(FACILITY,
				new TableColumnInfo(Messages.getString("facility.name", locale), TableColumnInfo.FACILITY, 200, SWT.LEFT));

		// 収集間隔
		tableDefine.add(RUN_INTERVAL,
				new TableColumnInfo(Messages.getString("run.interval", locale), TableColumnInfo.NONE, 40, SWT.LEFT));

		// 最古収集日時
		tableDefine.add(OLDEST_DATE,
				new TableColumnInfo(Messages.getString("collection.oldest.date", locale), TableColumnInfo.NONE, 140, SWT.LEFT));

		// 最新収集日時
		tableDefine.add(LATEST_DATE,
				new TableColumnInfo(Messages.getString("collection.latest.date", locale), TableColumnInfo.NONE, 140, SWT.LEFT));

		tableDefine.add(DUMMY,
				new TableColumnInfo("", TableColumnInfo.DUMMY, 200, SWT.LEFT));

		return tableDefine;
	}
}
