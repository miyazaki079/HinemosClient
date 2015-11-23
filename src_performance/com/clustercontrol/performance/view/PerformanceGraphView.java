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
package com.clustercontrol.performance.view;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.performance.composite.PerformanceGraphComposite;
import com.clustercontrol.util.Messages;
import com.clustercontrol.view.ScopeListBaseView;
import com.clustercontrol.ws.collector.PerformanceDataSettings;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * 性能[グラフ]ビュー
 *
 * ビュー名は性能[監視ID]として表示する
 *
 * @version 5.0.0
 * @since 4.0.0
 *
 */
public class PerformanceGraphView extends ScopeListBaseView {
	private static final Log log = LogFactory.getLog(PerformanceGraphView.class);

	public static final String ID = PerformanceGraphView.class.getName();

	private String managerName;
	private String monitorId;	// ビュータイトルに表示する監視ID
	private PerformanceGraphComposite graphComposite;

	/**
	 * デフォルトコンストラクタ
	 */
	public PerformanceGraphView() {
		// ノードを含め、INTERNAL, UNREGISTEREDを含めず, TOPICを受信しないスコープツリーとする
		super(false, false, false, false);

		// Sashのサイズ変更
		setSash(20);
	}

	/**
	 * コンポーネントの作成(初期起動)
	 */
	@Override
	protected Composite createListContents(Composite parent) {
		log.debug("createListContents()");

		super.getScopeTreeComposite().setScopeTree(null);

		// セカンダリIDとして登録した監視IDを元に、ビュー名をセット
		String[] secIds = this.getViewSite().getSecondaryId().split(",");
		this.managerName = secIds[0].replace("@", ":");
		this.monitorId = secIds[1];
		String args[] = {this.monitorId};
		this.setPartName(Messages.getString("view.performance.graph", args));

		// マネージャから情報を取得
		PerformanceDataSettings perfDataSettings = PerformanceDataSettingCache.get(this.managerName, this.monitorId );

		// FacilityTree情報をセット
		super.getScopeTreeComposite().setScopeTree(perfDataSettings.getFacilityTreeItem());

		// ビューのレイアウト設定
		parent.setLayout(new GridLayout());

		// ビューに張るコンポジットの初期化
		this.graphComposite = new PerformanceGraphComposite(this.managerName, perfDataSettings, parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, graphComposite);
		this.graphComposite.setLayoutData( new GridData( GridData.FILL_BOTH) );

		FacilityTreeItem item = perfDataSettings.getFacilityTreeItem();
		doSelectTreeItem(item.getChildren().get(0));

		// 完了
		return this.graphComposite;
	}


	/**
	 * ビュー更新
	 */
	@Override
	public void update() {
		long start = System.currentTimeMillis();

		log.debug("update()");
		this.graphComposite.update();

		log.debug(String.format("update: %dms", System.currentTimeMillis() - start));
	}

	/**
	 * ファシリティツリーを選択した場合にコールされるメソッド
	 */
	@Override
	protected void doSelectTreeItem(FacilityTreeItem item) {
		log.debug("doSelectTreeItem()");
		// ファシリティの更新によるグラフの更新
		this.graphComposite.updateGraph(item);
	}

	/**
	 * ビューの自動更新開始
	 */
	@Override
	public void startAutoReload() {
		log.debug("startAutoReload() title = " + this.getTitle());
		super.startAutoReload();
	}

	/**
	 * ビューの自動更新停止
	 */
	@Override
	public void stopAutoReload() {
		log.debug("stopAutoReload() title = " + this.getTitle());
		super.stopAutoReload();
	}

	/**
	 * ビューの自動更新間隔の設定
	 */
	@Override
	public void setInterval(int interval) {
		log.debug("setInterval() interval = " + interval);
		super.setInterval(interval);
	}

	/**
	 * チャートの取得
	 */
	public JFreeChart getChart() {
		log.debug("getChart()");
		return this.graphComposite.getChart();
	}

	public void setFocus() {
	}
}
