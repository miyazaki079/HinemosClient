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

package com.clustercontrol.performance.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.StatusConstant;
import com.clustercontrol.performance.action.GetPerformanceListTableDefine;
import com.clustercontrol.performance.composite.action.PerformanceListDoubleClickListener;
import com.clustercontrol.performance.util.CollectorEndpointWrapper;
import com.clustercontrol.performance.util.PerformanceFilterPropertyUtil;
import com.clustercontrol.util.EndpointManager;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.UIManager;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.collector.HinemosUnknown_Exception;
import com.clustercontrol.ws.collector.InvalidRole_Exception;
import com.clustercontrol.ws.collector.PerformanceFilterInfo;
import com.clustercontrol.ws.collector.PerformanceListInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 性能一覧のコンポジットクラス<BR>
 *
 * 性能一覧部分のテーブルのコンポジット
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class PerformanceListComposite extends Composite{

	// ログ
	private static Log m_log = LogFactory.getLog( PerformanceListComposite.class );

	// ----- instance フィールド ----- //

	/** テーブルビューア */
	private CommonTableViewer tableViewer = null;

	/** 表示内容ラベル */
	private Label statuslabel = null;

	/** 合計ラベル */
	private Label totalLabel = null;

	/** 検索条件 */
	private Property condition = null;

	// ----- コンストラクタ ----- //

	/**
	 * コンストラクタ
	 *
	 * @param parent
	 *            親のコンポジット
	 * @param style
	 *            スタイル
	 */
	public PerformanceListComposite(Composite parent, int style) {
		super(parent, style);

		// 初期化
		initialize();
	}


	/**
	 * コンポジットの初期化
	 */
	private void initialize() {

		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		// 表示内容ラベルの作成
		this.statuslabel = new Label(this, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "status", statuslabel);
		this.statuslabel.setText("");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.statuslabel.setLayoutData(gridData);

		// テーブルの作成
		Table performanceListTable = new Table(this, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		WidgetTestUtil.setTestId(this, null, performanceListTable);
		performanceListTable.setHeaderVisible(true);
		performanceListTable.setLinesVisible(true);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		performanceListTable.setLayoutData(gridData);

		// テーブルビューアの作成
		this.tableViewer = new CommonTableViewer(performanceListTable);
		this.tableViewer.createTableColumn(GetPerformanceListTableDefine.get(),
				GetPerformanceListTableDefine.SORT_COLUMN_INDEX1,
				GetPerformanceListTableDefine.SORT_COLUMN_INDEX2,
				GetPerformanceListTableDefine.SORT_ORDER);

		for (int i = 0; i < performanceListTable.getColumnCount(); i++){
			performanceListTable.getColumn(i).setMoveable(true);
		}

		// ダブルクリックリスナの追加
		this.tableViewer.addDoubleClickListener(new PerformanceListDoubleClickListener(this));

		// 合計ラベルの作成
		this.totalLabel = new Label(this, SWT.RIGHT);
		WidgetTestUtil.setTestId(this, "totallabel", totalLabel);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.totalLabel.setLayoutData(gridData);

	}


	/**
	 * コンポジットを更新します。
	 * <p>
	 *
	 * 検索条件が事前に設定されている場合、その条件にヒットする一覧を 表示します <br>
	 * 検索条件が設定されていない場合は、全ての設定を表示します。
	 */
	@Override
	public void update() {
		long start = System.currentTimeMillis();
		Map<String, List<PerformanceListInfo>> dispDataMap= new ConcurrentHashMap<String, List<PerformanceListInfo>>();
		Map<String, String> errorMsgs = new ConcurrentHashMap<>();

		String conditionManager = null;
		if(condition != null) {
			conditionManager = PerformanceFilterPropertyUtil.getManagerName(condition);
		}

		if(conditionManager == null || conditionManager.equals("")) {
			if (this.condition == null) {
				this.statuslabel.setText("");
				for(String managerName : EndpointManager.getActiveManagerSet()) {
					getPerformanceList(managerName, dispDataMap, errorMsgs);
				}
			} else {
				this.statuslabel.setText(Messages.getString("filtered.list"));
				PerformanceFilterInfo info = PerformanceFilterPropertyUtil.property2dto(this.condition);
				for(String managerName : EndpointManager.getActiveManagerSet()) {
					getPerformanceListWithCondition(managerName, dispDataMap, errorMsgs, info);
				}
			}
		} else {
			getPerformanceList(conditionManager, dispDataMap, errorMsgs);
		}

		//メッセージ表示
		if( 0 < errorMsgs.size() ){
			UIManager.showMessageBox(errorMsgs, true);
		}

		// MonitorInfo を tableViewer にセットするための詰め替え

		ArrayList<Object> listInput = new ArrayList<Object>();
		for(Map.Entry<String, List<PerformanceListInfo>> map : dispDataMap.entrySet()) {
			for (PerformanceListInfo perfListInfo : map.getValue()) {
				ArrayList<Object> a = new ArrayList<Object>();
				a.add(map.getKey());
				a.add(perfListInfo.getCollectorFlg());
				a.add(perfListInfo.getMonitorId());
				a.add(perfListInfo.getMonitorTypeId());
				a.add(perfListInfo.getDescription());
				a.add(perfListInfo.getScopeText());
				if(perfListInfo.getRunInterval() == null || perfListInfo.getRunInterval() == 0){
					a.add("-");
				}else{
					a.add(perfListInfo.getRunInterval() / 60 + Messages.getString("minute"));
				}
				if(perfListInfo.getOldestDate() == null){
					a.add(null);
				}
				else{
					a.add(new Date(perfListInfo.getOldestDate()));
				}
				if(perfListInfo.getLatestDate() == null){
					a.add(null);
				}
				else{
					a.add(new Date(perfListInfo.getLatestDate()));
				}
				a.add(null);
				listInput.add(a);
			}
		}

		// テーブル更新
		this.tableViewer.setInput(listInput);

		// 合計欄更新
		String[] args = { String.valueOf(listInput.size()) };
		String message = null;
		if (this.condition == null) {
			message = Messages.getString("records", args);
		} else {
			message = Messages.getString("filtered.records", args);
		}
		this.totalLabel.setText(message);

		m_log.debug(String.format("update: %dms", System.currentTimeMillis() - start));
	}

	/**
	 * tableViewerを返します。
	 *
	 * @return tableViewer
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
	 * コンポジットを更新します。
	 * <p>
	 *
	 * 検索条件が事前に設定されている場合、その条件にヒットする設定の一覧を 表示します <br>
	 * 検索条件が設定されていない場合は、全設定を表示します。
	 */
	public void update(Property condition) {
		this.condition = condition;

		this.update();
	}

	/**
	 * 現在選択されている監視IDを返します。
	 *
	 * @return 現在選択されている収集の監視ID
	 */
	public String getSelectedMonitorId() {
		Table table = tableViewer.getTable();
		WidgetTestUtil.setTestId(this, null, table);

		int selectionIndex = table.getSelectionIndex();

		// 何も選択されていない場合はnullを返す
		if(selectionIndex < 0){
			return null;
		}

		return table.getItem(selectionIndex).getText(GetPerformanceListTableDefine.MONITOR_ID);
	}

	/**
	 * 現在選択されている監視IDの監視種別IDを返します。
	 *
	 * @return 現在選択されている収集の監視IDの監視種別ID
	 */
	public String getSelectedMonitorTypeId() {
		Table table = tableViewer.getTable();
		WidgetTestUtil.setTestId(this, null, table);

		int selectionIndex = table.getSelectionIndex();

		// 何も選択されていない場合はnullを返す
		if(selectionIndex < 0){
			return null;
		}

		return table.getItem(selectionIndex).getText(GetPerformanceListTableDefine.MONITOR_TYPE_ID);
	}

	public String getSelectedManagerName() {
		Table table = tableViewer.getTable();
		WidgetTestUtil.setTestId(this, null, table);

		int selectionIndex = table.getSelectionIndex();

		// 何も選択されていない場合はnullを返す
		if(selectionIndex < 0){
			return null;
		}

		return table.getItem(selectionIndex).getText(GetPerformanceListTableDefine.MANAGER_NAME);
	}

	/**
	 * 現在選択されている収集の実行状態を返します。
	 *
	 * @return 現在選択されている収集の収集ID
	 */
	public int getRunStatus() {
		Table table = tableViewer.getTable();
		WidgetTestUtil.setTestId(this, null, table);
		String statusStr = table.getItem(table.getSelectionIndex()).getText(0);
		return StatusConstant.stringToType(statusStr);
	}

	private void getPerformanceList(String managerName,
			Map<String, List<PerformanceListInfo>> dispDataMap,
			Map<String, String> errorMsgs) {
		List<PerformanceListInfo> list = null;

		try{
			CollectorEndpointWrapper wrapper = CollectorEndpointWrapper.getWrapper(managerName);
			list = wrapper.getPerformanceList();
		} catch (InvalidRole_Exception e) {
			// アクセス権なしの場合、エラーダイアログを表示する
			errorMsgs.put( managerName, Messages.getString("message.accesscontrol.16") );
		} catch (HinemosUnknown_Exception e) {
			errorMsgs.put( managerName, Messages.getString("message.monitor.67") );
		} catch (Exception e) {
			m_log.warn("update() getPerformanceList, " + e.getMessage(), e);
			errorMsgs.put( managerName, Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		// list のNULLチェック
		if (list == null) {
			list = new ArrayList<PerformanceListInfo>();
		}
		dispDataMap.put(managerName, list);
	}

	private void getPerformanceListWithCondition(String managerName,
			Map<String, List<PerformanceListInfo>> dispDataMap,
			Map<String, String> errorMsgs, PerformanceFilterInfo info) {
		List<PerformanceListInfo> list = null;

		try{
			CollectorEndpointWrapper wrapper = CollectorEndpointWrapper.getWrapper(managerName);
			list = wrapper.getPerformanceList(info);
		} catch (InvalidRole_Exception e) {
			// アクセス権なしの場合、エラーダイアログを表示する
			errorMsgs.put( managerName, Messages.getString("message.accesscontrol.16") );
		} catch (HinemosUnknown_Exception e) {
			errorMsgs.put( managerName, Messages.getString("message.monitor.67") );
		} catch (Exception e) {
			m_log.warn("update() getPerformanceList, " + e.getMessage(), e);
			errorMsgs.put( managerName, Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		// list のNULLチェック
		if (list == null) {
			list = new ArrayList<PerformanceListInfo>();
		}
		dispDataMap.put(managerName, list);
	}
}
