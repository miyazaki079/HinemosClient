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

package com.clustercontrol.monitor.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.monitor.action.GetStatusListTableDefine;
import com.clustercontrol.monitor.dialog.StatusInfoDialog;
import com.clustercontrol.monitor.util.ConvertListUtil;
import com.clustercontrol.monitor.util.StatusFilterPropertyUtil;
import com.clustercontrol.monitor.util.StatusSearchRunUtil;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.PropertyUtil;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.monitor.StatusFilterInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * ステータス情報一覧のコンポジットクラス<BR>
 *
 *ステータス情報一覧部分のテーブルのコンポジット
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class StatusListComposite extends Composite {
	/** テーブルビューア */
	private CommonTableViewer tableViewer = null;

	/** 危険ラベル */
	private Label criticalLabel = null;

	/** 警告ラベル */
	private Label warningLabel = null;

	/** 通知ラベル */
	private Label infoLabel = null;

	/** 不明ラベル */
	private Label unknownLabel = null;

	/** 合計ラベル */
	private Label totalLabel = null;

	private Shell m_shell = null;

	/**
	 * インスタンスを返します。
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public StatusListComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		m_shell = this.getShell();
	}

	/**
	 * コンポジットを配置します。
	 */
	private void initialize() {
		GridLayout layout = new GridLayout(5, true);
		this.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Table table = new Table(this, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		WidgetTestUtil.setTestId(this, null, table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 5;
		table.setLayoutData(gridData);

		// ステータス作成
		// 危険
		this.criticalLabel = new Label(this, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "criticallabel", criticalLabel);
		this.criticalLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		this.criticalLabel.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_RED));

		// 警告
		this.warningLabel = new Label(this, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "warninglabel", warningLabel);
		this.warningLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		this.warningLabel.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_YELLOW));

		// 通知
		this.infoLabel = new Label(this, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "infolabel", infoLabel);
		this.infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		this.infoLabel.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_GREEN));

		// 不明
		this.unknownLabel = new Label(this, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "unknownlabel", unknownLabel);
		this.unknownLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		this.unknownLabel.setBackground(new Color(null, 128, 192, 255));

		// 合計
		this.totalLabel = new Label(this, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "totallabel", totalLabel);
		this.totalLabel.setLayoutData( new GridData(SWT.FILL, SWT.NONE, true, false) );

		// テーブルビューアの作成
		this.tableViewer = new CommonTableViewer(table);
		this.tableViewer.createTableColumn(GetStatusListTableDefine.getStatusListTableDefine(),
				GetStatusListTableDefine.SORT_COLUMN_INDEX1,
				GetStatusListTableDefine.SORT_COLUMN_INDEX2,
				GetStatusListTableDefine.SORT_ORDER);

		for (int i = 0; i < table.getColumnCount(); i++){
			table.getColumn(i).setMoveable(true);
		}

		this.tableViewer.addDoubleClickListener(new IDoubleClickListener(){
			@Override
			public void doubleClick(DoubleClickEvent event) {
				// 選択アイテムを取得する
				List<?> list = (List<?>) ((StructuredSelection)event.getSelection()).getFirstElement();
				StatusInfoDialog dialog = new StatusInfoDialog(m_shell, list);
				dialog.open();
			}
		});
	}

	/**
	 * このコンポジットが利用する共通テーブルビューアーを返します。
	 *
	 * @return 共通テーブルビューアー
	 */
	public CommonTableViewer getTableViewer() {
		return this.tableViewer;
	}

	/**
	 * このコンポジットが利用するテーブルを返します。
	 *
	 * @return テーブル
	 */
	public Table getTable() {
		return this.tableViewer.getTable();
	}

	/**
	 * ビューを更新します。<BR>
	 * 引数で指定された条件に一致するステータス情報一覧を取得し、共通テーブルビューアーにセットします。
	 * <p>
	 * <ol>
	 * <li>引数で指定された条件に一致するステータス情報一覧を取得します。</li>
	 * <li>共通テーブルビューアーにステータス情報一覧をセットします。</li>
	 * </ol>
	 *
	 * @param facilityId 表示対象の親ファシリティID
	 * @param condition 検索条件
	 * @param managerList マネージャ名リスト
	 *
	 * @see #updateStatus(ArrayList)
	 */
	public void update(String facilityId, Property condition, List<String> managerList) {
		super.update();

		/** 表示用リスト */
		Map<String, ArrayList<ArrayList<Object>>> dispDataMap= new ConcurrentHashMap<>();

		if(condition == null) {
			dispDataMap = getStatusList(facilityId, managerList);
		} else {
			dispDataMap = getStatusListByCondition(facilityId, condition, managerList);
		}

		ArrayList<ArrayList<Object>> statusList = ConvertListUtil.statusInfoData2List(dispDataMap);
		this.updateStatus(statusList);
		tableViewer.setInput(statusList);
	}

	private Map<String, ArrayList<ArrayList<Object>>> getStatusList(String facilityId, List<String> managerList) {
		Map<String, ArrayList<ArrayList<Object>>> map = new ConcurrentHashMap<>();
		StatusSearchRunUtil util = new StatusSearchRunUtil();
		map = util.searchInfo(managerList, facilityId, null);
		return map;
	}

	private Map<String, ArrayList<ArrayList<Object>>> getStatusListByCondition(String facilityId,
			Property condition, List<String> managerList) {

		Map<String, ArrayList<ArrayList<Object>>> map = new ConcurrentHashMap<>();
		PropertyUtil.deletePropertyDefine(condition);
		StatusFilterInfo filter = StatusFilterPropertyUtil.property2dto(condition);
		StatusSearchRunUtil util = new StatusSearchRunUtil();
		map = util.searchInfo(managerList, facilityId, filter);

		return map;
	}

	/**
	 * ステータスラベルを更新します。<BR>
	 * 引数で指定されたステータス情報一覧より、重要度ごとの件数，全件数を取得し、
	 * ステータスラベルを更新します。
	 *
	 * @param map ステータス情報一覧
	 */
	private void updateStatus( ArrayList<ArrayList<Object>> list ){
		int[] status = new int[4];
		for(ArrayList<Object> data : list ){
			int value = ((Integer) data.get(GetStatusListTableDefine.PRIORITY)).intValue();
			switch (value) {
			case PriorityConstant.TYPE_CRITICAL:
				status[0]++;
				break;
			case PriorityConstant.TYPE_WARNING:
				status[1]++;
				break;
			case PriorityConstant.TYPE_INFO:
				status[2]++;
				break;
			case PriorityConstant.TYPE_UNKNOWN:
				status[3]++;
				break;
			}
		}

		// ラベル更新
		this.criticalLabel.setText(String.valueOf(status[0]));
		this.warningLabel.setText(String.valueOf(status[1]));
		this.infoLabel.setText(String.valueOf(status[2]));
		this.unknownLabel.setText(String.valueOf(status[3]));
		int total = status[0] + status[1] + status[2] + status[3];
		this.totalLabel.setText(Messages.getString("filtered.records", new Object[]{total}));
	}

}
