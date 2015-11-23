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

package com.clustercontrol.monitor.run.dialog;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.clustercontrol.bean.TableColumnInfo;
import com.clustercontrol.monitor.run.action.GetStringFilterTableDefine;
import com.clustercontrol.monitor.run.bean.MonitorTypeConstant;
import com.clustercontrol.monitor.run.composite.StringValueInfoComposite;
import com.clustercontrol.ws.monitor.MonitorInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 文字列系監視設定共通ダイアログクラス<BR>
 *
 */
public class CommonMonitorStringDialog extends CommonMonitorDialog {

	/** 文字列監視判定情報 */
	protected StringValueInfoComposite m_stringValueInfo = null;

	/** メッセージにデフォルトを入れるフラグ */
	protected boolean logLineFlag = false;

	// ----- コンストラクタ ----- //

	/**
	 * 作成用ダイアログのインスタンスを返します。
	 *
	 * @param parent
	 *            親のシェルオブジェクト
	 * @param managerName
	 *            マネージャ名
	 */
	public CommonMonitorStringDialog(Shell parent, String managerName) {
		super(parent, managerName);
	}

	// ----- instance メソッド ----- //

	/**
	 * ダイアログエリアを生成します。
	 *
	 * @param parent 親のインスタンス
	 */
	@Override
	protected void customizeDialog(Composite parent) {

		super.customizeDialog(parent);

		// 変数として利用されるグリッドデータ
		GridData gridData = null;

		// 文字列判定定義情報
		ArrayList<TableColumnInfo> tableDefine = GetStringFilterTableDefine.get();
		this.m_stringValueInfo = new StringValueInfoComposite(groupDetermine, SWT.NONE, tableDefine, logLineFlag);
		WidgetTestUtil.setTestId(this, null, m_stringValueInfo);
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 220;
		m_stringValueInfo.setLayoutData(gridData);

	}

	/**
	 * 監視エリアを有効/無効化します。
	 *
	 */
	@Override
	protected void setMonitorEnabled(boolean enabled){
		super.setMonitorEnabled(enabled);
		m_stringValueInfo.setEnabled(enabled);
	}

	/**
	 * 入力値を用いて通知情報を生成します。
	 *
	 * @return 入力値を保持した通知情報
	 */
	@Override
	protected MonitorInfo createInputData() {
		super.createInputData();
		if(validateResult != null){
			return null;
		}

		// 監視種別を文字列に設定する
		monitorInfo.setMonitorType(MonitorTypeConstant.TYPE_STRING);

		return monitorInfo;
	}

}
