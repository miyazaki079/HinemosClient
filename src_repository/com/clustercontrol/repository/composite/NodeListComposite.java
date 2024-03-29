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

package com.clustercontrol.repository.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.Property;
import com.clustercontrol.jobmanagement.util.JobPropertyUtil;
import com.clustercontrol.repository.action.GetNodeList;
import com.clustercontrol.repository.action.GetNodeListTableDefine;
import com.clustercontrol.repository.composite.action.NodeDoubleClickListener;
import com.clustercontrol.repository.view.NodeAttributeView;
import com.clustercontrol.repository.view.NodeScopeView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.repository.NodeInfo;

/**
 * ノード一覧コンポジットクラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class NodeListComposite extends Composite {

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
	 * インスタンスを返します。
	 *
	 * @param parent
	 *            親のコンポジット
	 * @param style
	 *            スタイル
	 */
	public NodeListComposite(Composite parent, int style) {
		super(parent, style);

		this.initialize();
	}

	// ----- instance メソッド ----- //

	/**
	 * コンポジットを生成・構築します。
	 */
	private void initialize() {
		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		this.statuslabel = new Label(this, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "statuslabel", statuslabel);
		this.statuslabel.setText("");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.statuslabel.setLayoutData(gridData);

		Table table = new Table(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		WidgetTestUtil.setTestId( this, null, table );
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		table.setLayoutData(gridData);

		// テーブルビューアの作成
		this.tableViewer = new CommonTableViewer(table);
		this.tableViewer.createTableColumn(GetNodeListTableDefine.get(),
				GetNodeListTableDefine.SORT_COLUMN_INDEX1,
				GetNodeListTableDefine.SORT_COLUMN_INDEX2,
				GetNodeListTableDefine.SORT_ORDER);

		for (int i = 0; i < table.getColumnCount(); i++){
			table.getColumn(i).setMoveable(true);
		}
		this.tableViewer
		.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// 各ビューの更新

				IWorkbench workbench = ClusterControlPlugin.getDefault()
						.getWorkbench();
				IWorkbenchPage page = workbench
						.getActiveWorkbenchWindow().getActivePage();

				NodeScopeView scopeView = (NodeScopeView) page
						.findView(NodeScopeView.ID);
				if (scopeView != null) {
					scopeView.update();
				}
				NodeAttributeView attributeView = (NodeAttributeView) page
						.findView(NodeAttributeView.ID);
				if (attributeView != null) {
					attributeView.update();
				}
			}
		});

		// ダブルクリックリスナの追加
		this.tableViewer.addDoubleClickListener(new NodeDoubleClickListener(this));

		this.totalLabel = new Label(this, SWT.RIGHT);
		WidgetTestUtil.setTestId(this, "totallabel", totalLabel);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.totalLabel.setLayoutData(gridData);

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
	 * 検索条件が事前に設定されている場合、その条件にヒットするノードの一覧を 表示します <br>
	 * 検索条件が設定されていない場合は、全ノードを表示します。
	 */
	@Override
	public void update() {
		// データ取得
		Map<String, List<NodeInfo>> dispDataMap = null;
		ArrayList<Object> listInput = new ArrayList<Object>();

		if (this.condition == null) {
			this.statuslabel.setText("");
			dispDataMap = new GetNodeList().getAll();
		} else {
			this.statuslabel.setText(Messages.getString("filtered.list"));

			String conditionManager = null;
			if(this.condition != null) {
				conditionManager = JobPropertyUtil.getManagerName(this.condition);
			}

			if(conditionManager == null || conditionManager.equals("")) {
				dispDataMap = new GetNodeList().get(this.condition);
			} else {
				List<NodeInfo> list = new GetNodeList().get(conditionManager, this.condition);
				dispDataMap = new ConcurrentHashMap<String, List<NodeInfo>>();
				dispDataMap.put(conditionManager, list);
			}
		}

		int cnt = 0;
		for(Map.Entry<String, List<NodeInfo>> entrySet : dispDataMap.entrySet()) {
			List<NodeInfo> list = entrySet.getValue();

			if(list == null){
				list = new ArrayList<NodeInfo>();
			}

			for (NodeInfo node : list) {
				ArrayList<Object> a = new ArrayList<Object>();
				a.add(entrySet.getKey());
				a.add(node.getFacilityId());
				a.add(node.getFacilityName());
				a.add(node.getPlatformFamily());
				if (node.getIpAddressVersion() == 6) {
					a.add(new Ip(node.getIpAddressV6(), 6));
				} else {
					a.add(new Ip(node.getIpAddressV4(), 4));
				}
				a.add(node.getDescription());
				a.add(node.getOwnerRoleId());
				a.add(null);
				listInput.add(a);
				cnt++;
			}
		}
		// テーブル更新
		this.tableViewer.setInput(listInput);

		// 合計欄更新
		String[] args = { new Integer(cnt).toString() };
		String message = null;
		if (this.condition == null) {
			message = Messages.getString("records", args);
		} else {
			message = Messages.getString("filtered.records", args);
		}
		this.totalLabel.setText(message);
	}

	private class Ip implements Comparable<Ip>{
		int version = 6;
		String ip = "";
		private Ip (String ip, int version) {
			if (version == 4) {
				this.version = 4;
			}
			this.ip = ip;
		}
		
		@Override
		public int compareTo(Ip o) {
			
			if (this.version != o.version) {
				return this.version - o.version;
			}
			if (this.version == 4 && o.version == 4) {
				long v1 = 0;
				for (String octet : this.ip.split("\\.")){
					v1 *= 256;
					try {
						int i = Integer.parseInt(octet);
						v1 += i;
					} catch (Exception e) {
					}
				}
				long v2 = 0;
				for (String octet : o.ip.split("\\.")){
					v2 *= 256;
					try {
						v2 += Integer.parseInt(octet);
					} catch (Exception e) {
					}
				}
				long diff = v1 - v2;
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}
			}
			
			return this.ip.compareTo(o.ip);
		}
		
		@Override
		public String toString() {
			return ip;
		}
	}
	
	/**
	 * 検索条件にヒットしたノードの一覧を表示します。
	 * <p>
	 *
	 * conditionがnullの場合、全ノードを表示します。
	 *
	 * @param condition
	 *            検索条件
	 */
	public void update(Property condition) {
		this.condition = condition;

		this.update();
	}
}
