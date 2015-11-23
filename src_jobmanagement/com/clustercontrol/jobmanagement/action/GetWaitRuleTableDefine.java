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

package com.clustercontrol.jobmanagement.action;

import java.util.ArrayList;

import org.eclipse.swt.SWT;

import com.clustercontrol.bean.TableColumnInfo;
import com.clustercontrol.util.Messages;

/**
 * 待ち条件タブのテーブル定義を取得するクライアント側アクションクラス<BR>
 *
 * @version 2.1.0
 * @since 1.0.0
 */
public class GetWaitRuleTableDefine {

	/** 名前 */
	public static final int JUDGMENT_OBJECT = 0;
	/** ジョブID */
	public static final int JOB_ID = 1;
	/** 値 */
	public static final int START_VALUE = 2;
	/**  セッション開始時の時間（分） */
	public static final int START_MINUTE = 3;

	/** 初期表示時ソートカラム */
	public static final int SORT_COLUMN_INDEX = JUDGMENT_OBJECT;
	/** 初期表示時ソートオーダー (昇順=1, 降順=-1) */
	public static final int SORT_ORDER = 1;

	/**
	 * 待ち条件タブのテーブル定義を作成する
	 * 
	 * @return テーブル定義情報（{@link com.clustercontrol.bean.TableColumnInfo}のリスト）
	 */
	public static ArrayList<TableColumnInfo> get() {
		//テーブル定義配列
		ArrayList<TableColumnInfo> tableDefine = new ArrayList<TableColumnInfo>();

		tableDefine.add(JUDGMENT_OBJECT,
				new TableColumnInfo(Messages.getString("name"), TableColumnInfo.JUDGMENT_OBJECT, 200, SWT.LEFT));
		tableDefine.add(JOB_ID,
				new TableColumnInfo(Messages.getString("job.id"), TableColumnInfo.NONE, 150, SWT.LEFT));
		tableDefine.add(START_VALUE,
				new TableColumnInfo(Messages.getString("value"), TableColumnInfo.WAIT_RULE_VALUE, 70, SWT.LEFT));

		return tableDefine;
	}
}
