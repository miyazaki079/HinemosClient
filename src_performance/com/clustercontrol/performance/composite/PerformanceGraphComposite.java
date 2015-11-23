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

import java.awt.Color;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.monitor.util.PerformanceDataSettingsUtil;
import com.clustercontrol.performance.action.RecordController;
import com.clustercontrol.performance.bean.GraphConstant;
import com.clustercontrol.performance.preference.PerformancePreferencePage;
import com.clustercontrol.performance.util.CollectedDataSetUtil;
import com.clustercontrol.performance.util.CollectorItemCodeFactory;
import com.clustercontrol.performance.view.PerformanceGraphView;
import com.clustercontrol.repository.bean.FacilityConstant;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.collector.CollectedDataInfo;
import com.clustercontrol.ws.collector.CollectedDataSet;
import com.clustercontrol.ws.collector.CollectorItemInfo;
import com.clustercontrol.ws.collector.CollectorItemParentInfo;
import com.clustercontrol.ws.collector.PerformanceDataSettings;
import com.clustercontrol.ws.repository.FacilityTreeItem;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 性能グラフを表示するコンポジットクラス<BR>
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class PerformanceGraphComposite extends Composite {
	private static Log m_log = LogFactory.getLog(PerformanceGraphComposite.class);

	// グラフ種別
	private final int INDEX_GRAPH_LINE = 0;		// 折れ線グラフ
	private final int INDEX_GRAPH_STACK = 1;	// 積み上げグラフ
	private final String STRING_GRAPH_LINE = Messages.getString("collection.graph.line");
	private final String STRING_GRAPH_STACK = Messages.getString("collection.graph.area");

	// 表示種別
	private final int INDEX_DISPLAY_NODE = 0;		// ノード別
	private final int INDEX_DISPLAY_ITEMCODE = 1;	// 収集項目別
	private final int INDEX_DISPLAY_DEVICE = 2;		// デバイス別
	private final String STRING_DISPLAY_NODE = Messages.getString("collection.display.node");
	private final String STRING_DISPLAY_ITEMCODE = Messages.getString("collection.display.itemcode");
	private final String STRING_DISPLAY_DEVICE = Messages.getString("collection.display.device");

	// 表示期間変更アクション
	private final int ACTION_OLDEST = 0;		// 最古期間のデータ
	private final int ACTION_PREV = 1;			// 前へ
	private final int ACTION_NEXT = 2;			// 次へ
	private final int ACTION_NEWEST = 3;		// 最新のデータへ
	private final String STRING_ACTION_OLDEST = Messages.getString("collection.button.oldest");
	private final String STRING_ACTION_PREV = Messages.getString("collection.button.prev");
	private final String STRING_ACTION_NEXT = Messages.getString("collection.button.next");
	private final String STRING_ACTION_NEWEST = Messages.getString("collection.button.newest");

	// 期間の定数
	private final long MILLISECOND_HOUR = 3600L * 1000L;
	private final long MILLISECOND_DAY = 24L * 3600L * 1000L;
	private final long MILLISECOND_WEEK = 7L * 24L * 3600L * 1000L;
	private final long MILLISECOND_MONTH = 31L * 24L * 3600L * 1000L;
	private final String STRING_TIME_HOUR = Messages.getString("collection.button.hour");
	private final String STRING_TIME_DAY = Messages.getString("collection.button.day");
	private final String STRING_TIME_WEEK = Messages.getString("collection.button.week");
	private final String STRING_TIME_MONTH = Messages.getString("collection.button.month");

	/** グラフヘッダ情報 */
	private PerformanceDataSettings performanceDataSettings =  null;

	// プルダウンメニュー
	private Combo displayTypeCombo = null;	// 表示種別プルダウンメニュ
	private Combo displayItem = null;		// 表示項目プルダウンメニュ
	private Combo graphTypeCombo = null;	// グラフ種別プルダウメニュ

	// ボタン]
	private Button firstButton = null;		// 最初へ
	private Button backButton = null;		// 前へ
	private Button forwardButton = null;	// 次へ
	private Button lastButton = null;		// 最後へ
	private Button updateButton = null;		// グラフ更新

	// トグルボタン
	private Button hourButton = null;	// 時
	private Button dayButton = null;	// 日
	private Button weekButton = null;	// 週
	private Button monthButton = null;	// 月

	// チェックボックス
	private Button autoUpdate = null;	// 自動更新をする

	// 現在適用しようとしている設定
	private int targetDiaplayType = INDEX_DISPLAY_NODE;	// 表示種別(ノード別とか)
	private int targetGraphType = INDEX_GRAPH_LINE;		// グラフ種別
	private CollectorItemParentInfo targetItemCodeInfo = null;	// 表示項目
	private FacilityTreeItem targetFacilityTreeItem = null;//ファシリティツリー
	private int targetRange = GraphConstant.RANGE_HOUR;	// 指定された表示期間

	// 現在適用しようとしているデータの取得条件
	private Date targetConditionStartDate = null;
	private Date targetConditionEndDate = null;
	private ArrayList<CollectorItemInfo> targetConditionItemList = new ArrayList<CollectorItemInfo>();
	private ArrayList<String> targetConditionFacilityIdList = new ArrayList<String>();

	private boolean targetAutoUpdate = false;				// 画面自動更新の有無

	private ArrayList<CollectorItemParentInfo> itemCodeListByDisplayDevice = new ArrayList<CollectorItemParentInfo>();

	// グラフ描画用
	private ChartComposite chartComposite = null;
	private Browser chartBrowser = null;
	private JFreeChart jfreeChart = null;
	private TimeSeriesCollection timeSeriesCollection = null;
	private TimeSeries[] timeSeries = null;

	private String managerName = null;

	// レンダラと色を選択
	private static Color[] color = {
			Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.DARK_GRAY, 
			ChartColor.DARK_RED, ChartColor.DARK_BLUE, ChartColor.DARK_GREEN, ChartColor.VERY_DARK_YELLOW, ChartColor.DARK_CYAN, ChartColor.DARK_MAGENTA };

	/**
	 * コンストラクタ
	 *
	 * @param settings グラフのヘッダ情報
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 */
	public PerformanceGraphComposite(String managerName, PerformanceDataSettings settings, Composite parent, int style) {
		super(parent, style);
		this.managerName = managerName;

		// ヘッダ情報の設定
		setPerformanceDataSettings(settings);

		// 初期化
		initialize();
	}

	private void createButtonBar(Composite parent){
		parent.setLayout( new GridLayout(13, true) );
		GridData gridData;

		//////
		// 1行目
		//////
		// 1行目左：表示種別ラベル
		Label displayTypeLabel = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "displaytype", displayTypeLabel);
		displayTypeLabel.setText(Messages.getString("collection.label.display.type") + " : ");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		displayTypeLabel.setLayoutData(gridData);

		// 1行目左：表示種別(プルダウン)
		this.displayTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "displaycombo", displayTypeCombo);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		this.displayTypeCombo.setLayoutData(gridData);
		this.displayTypeCombo.add(STRING_DISPLAY_NODE);
		this.displayTypeCombo.add(STRING_DISPLAY_ITEMCODE);
		this.displayTypeCombo.add(STRING_DISPLAY_DEVICE);
		this.displayTypeCombo.select(INDEX_DISPLAY_NODE);//default
		this.displayTypeCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Combo combo = (Combo)e.widget;

				String selectedDisplayType = combo.getText();
				m_log.debug("displayTypeCombo : select " + selectedDisplayType);

				if(STRING_DISPLAY_DEVICE.equals(selectedDisplayType)){
					displayItem.setEnabled(true);
					displayItem.removeAll();
					for (CollectorItemParentInfo itemCodeInfo : itemCodeListByDisplayDevice){
						displayItem.add(getFullItemName(itemCodeInfo));
					}
					displayItem.select(0);
				}
				else if(STRING_DISPLAY_ITEMCODE.equals(selectedDisplayType)){
					displayItem.removeAll();
					for (CollectorItemParentInfo itemCodeInfo : performanceDataSettings.getItemCodeList()){
						displayItem.add(getFullItemName(itemCodeInfo));
					}
					displayItem.select(0);
					displayItem.setEnabled(false);

				}
				else{
					displayItem.setEnabled(true);
					displayItem.removeAll();
					for (CollectorItemParentInfo itemCodeInfo : performanceDataSettings.getItemCodeList()){
						displayItem.add(getFullItemName(itemCodeInfo));
					}
					displayItem.select(0);
				}
			}
		});

		// 1行目中央：グラフ種別ラベル
		Label graphTypeLabel = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "graphtype", graphTypeLabel);
		graphTypeLabel.setText(Messages.getString("collection.label.graph.type") + " : ");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		graphTypeLabel.setLayoutData(gridData);

		// 1行目中央：グラフ種別(プルダウン)
		this.graphTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "graphtype", graphTypeCombo);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		this.graphTypeCombo.setLayoutData(gridData);
		this.graphTypeCombo.add(STRING_GRAPH_LINE);
		this.graphTypeCombo.add(STRING_GRAPH_STACK);
		this.graphTypeCombo.select(INDEX_GRAPH_LINE);//default

		// 1行目右：スペース
		Label space1 = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "space1", space1);
		space1.setText("");
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		space1.setLayoutData(gridData);

		//////
		// 2行目
		//////
		// 2行目左：表示項目ラベル
		Label displayItemLabel = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "labelitem", displayItemLabel);
		displayItemLabel.setText(Messages.getString("collection.label.item") + " : ");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		displayItemLabel.setLayoutData(gridData);

		// 2行目左：表示項目(プルダウン)
		this.displayItem = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "displayitem", displayItem);
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		this.displayItem.setLayoutData(gridData);
		this.displayItem.removeAll();
		for (CollectorItemParentInfo itemCodeInfo : this.performanceDataSettings.getItemCodeList()){
			this.displayItem.add(getFullItemName(itemCodeInfo));
		}
		this.displayItem.select(0);//default

		// 2行目中央：表示項目ラベル
		Label updateLabel = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "update", updateLabel);
		updateLabel.setText(Messages.getString("collection.label.update") + " : ");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		updateLabel.setLayoutData(gridData);

		// 2行目中央：自動更新有無(チェックボックス)
		this.autoUpdate = new Button(parent, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "autoupdate", autoUpdate);
		this.autoUpdate.setText(Messages.getString("collection.label.auto.update"));
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		this.autoUpdate.setLayoutData(gridData);
		this.autoUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// GUIの入力項目を取得
				targetAutoUpdate = autoUpdate.getSelection();

				// 現在表示しているビュータイトル
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				String currentViewTitle = page.getActivePart().getTitle();
				m_log.debug("autoUpdate SelectionEvent : currentViewTitle = " + currentViewTitle);

				for (IViewReference ref : page.getViewReferences()) {
					if (ref.getView(false) instanceof PerformanceGraphView) {
						PerformanceGraphView view = (PerformanceGraphView) ref.getView(false);
						// 現在表示しているビューの場合
						if (view.getTitle().equals(currentViewTitle)) {

							if(targetAutoUpdate){
								m_log.debug("autoUpdate SelectionEvent : Interval = " + performanceDataSettings.getInterval());
								int minute = 1;
								if (performanceDataSettings.getInterval() > 60){
									minute = performanceDataSettings.getInterval() / 60;
								}
								view.setInterval(minute);
								view.startAutoReload();

								String args[] = { Integer.toString(minute) };

								MessageDialog.openInformation(getShell(),
										Messages.getString("confirmed"), // "確認"
										Messages.getString("message.collection.1",args));
							}
							else{
								view.stopAutoReload();
								MessageDialog.openInformation(getShell(),
										Messages.getString("confirmed"), // "確認"
										Messages.getString("message.collection.2"));
							}
							break;
						}
					}
				}
			}
		});


		// 2行目右：更新ボタン
		this.updateButton = new Button(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "update", updateButton);
		this.updateButton.setText(Messages.getString("collection.button.apply"));
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 1;
		this.updateButton.setLayoutData(gridData);
		this.updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long start = System.currentTimeMillis();

				// target変数の更新
				int graphType = 0;
				int displayType = 0;
				CollectorItemParentInfo itemCodeInfo = null;

				// グラフ種別
				int graphTypeIndex = graphTypeCombo.getSelectionIndex();
				m_log.debug("updateButton SelectionEvent : graphTypeIndex = " + graphTypeIndex);
				switch (graphTypeIndex) {
				case INDEX_GRAPH_LINE:
					graphType = GraphConstant.GRAPH_LINE;
					break;
				case INDEX_GRAPH_STACK:
					graphType = GraphConstant.GRAPH_STACK;
					break;
				default:
					graphType = GraphConstant.GRAPH_LINE;//default
					break;
				}

				// 表示種別
				int displayTypeIndex = displayTypeCombo.getSelectionIndex();
				int itemCodeInfoIndex = displayItem.getSelectionIndex();
				if(itemCodeInfoIndex < 0){
					MessageDialog.openWarning(getShell(),
							Messages.getString("message.hinemos.1"), // "確認"
							Messages.getString("message.collection.4"));
					return;
				}

				m_log.debug("updateButton SelectionEvent : displayTypeIndex = " + displayTypeIndex);
				switch (displayTypeIndex) {
				case INDEX_DISPLAY_NODE:
					displayType = GraphConstant.DISPLAY_NODE;
					itemCodeInfo = performanceDataSettings.getItemCodeList().get(itemCodeInfoIndex);
					break;

				case INDEX_DISPLAY_ITEMCODE:
					displayType = GraphConstant.DISPLAY_ITEMCODE;
					itemCodeInfo = performanceDataSettings.getItemCodeList().get(itemCodeInfoIndex);
					break;

				case INDEX_DISPLAY_DEVICE:
					displayType = GraphConstant.DISPLAY_DEVICE;
					itemCodeInfo = itemCodeListByDisplayDevice.get(itemCodeInfoIndex);
					break;

				default:
					displayType = GraphConstant.DISPLAY_NODE;//default
					break;
				}

				// グラフ再描画
				updateGraph(managerName, graphType, displayType, itemCodeInfo);

				m_log.debug(String.format("updateButton SelectionEvent: %dms", System.currentTimeMillis() - start));
			}
		});
		//////
		// 3行目
		//////
		// 3行目：グラフ

		//////
		// 4行目
		//////
		// 4行目左：表示期間を過去へ
		// 4行目左：表示期間を過去へ
		// 前の期間へ(4行目)
		this.firstButton = new Button(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "first", firstButton);
		this.firstButton.setText(STRING_ACTION_OLDEST);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		this.firstButton.setLayoutData(gridData);
		this.firstButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// グラフ更新
				updateByRangeAction(ACTION_OLDEST);
			}
		});

		// 4行目左：表示期間を過去へ
		this.backButton = new Button(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "back", backButton);
		this.backButton.setText(STRING_ACTION_PREV);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		this.backButton.setLayoutData(gridData);
		this.backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// グラフ更新
				updateByRangeAction(ACTION_PREV);
			}
		});

		// スペース
		Label space4 = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "space4", space4);
		space4.setText("");
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		space4.setLayoutData(gridData);

		// 4行目中央：表示期間ラベル
		Label spanLabel = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "spanlabel", spanLabel);
		spanLabel.setText(Messages.getString("collection.label.span") + " : ");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		spanLabel.setLayoutData(gridData);

		// 4行目中央：表示期間「時」
		this.hourButton = new Button(parent, SWT.TOGGLE);
		WidgetTestUtil.setTestId(this, "hour", hourButton);
		this.hourButton.setText(STRING_TIME_HOUR);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		this.hourButton.setLayoutData(gridData);
		this.hourButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// 自分以外の選択を解除
				dayButton.setSelection(false);
				weekButton.setSelection(false);
				monthButton.setSelection(false);

				// グラフ更新
				updateGraphRange(GraphConstant.RANGE_HOUR);
			}
		});
		this.hourButton.setSelection(true);

		// 4行目中央：表示期間「日」
		this.dayButton = new Button(parent, SWT.TOGGLE);
		WidgetTestUtil.setTestId(this, "day", dayButton);
		this.dayButton.setText(STRING_TIME_DAY);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		this.dayButton.setLayoutData(gridData);
		this.dayButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// 自分以外の選択を解除
				hourButton.setSelection(false);
				weekButton.setSelection(false);
				monthButton.setSelection(false);

				// グラフ更新
				updateGraphRange(GraphConstant.RANGE_DAY);
			}
		});

		// 4行目中央：表示期間「週」
		this.weekButton = new Button(parent, SWT.TOGGLE);
		WidgetTestUtil.setTestId(this, "week", weekButton);
		this.weekButton.setText(STRING_TIME_WEEK);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.TOP;
		gridData.horizontalSpan = 1;
		this.weekButton.setLayoutData(gridData);
		this.weekButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// 自分以外の選択を解除
				hourButton.setSelection(false);
				dayButton.setSelection(false);
				monthButton.setSelection(false);

				// グラフ更新
				updateGraphRange(GraphConstant.RANGE_WEEK);
			}
		});

		// 4行目中央：表示期間「月」
		this.monthButton = new Button(parent, SWT.TOGGLE);
		WidgetTestUtil.setTestId(this, "month", monthButton);
		this.monthButton.setText(STRING_TIME_MONTH);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		this.monthButton.setLayoutData(gridData);
		this.monthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// 自分以外の選択を解除
				hourButton.setSelection(false);
				dayButton.setSelection(false);
				weekButton.setSelection(false);

				// グラフ更新
				updateGraphRange(GraphConstant.RANGE_MONTH);
			}
		});

		// スペース
		Label space5 = new Label(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "space5", space5);
		space5.setText("");
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		space5.setLayoutData(gridData);

		// 4行目右：表示期間を未来へ
		this.forwardButton = new Button(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "forward", forwardButton);
		this.forwardButton.setText(STRING_ACTION_NEXT);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		this.forwardButton.setLayoutData(gridData);
		this.forwardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// グラフ更新
				updateByRangeAction(ACTION_NEXT);
			}
		});

		// 4行目右：表示期間を未来へ
		this.lastButton = new Button(parent, SWT.CENTER);
		WidgetTestUtil.setTestId(this, "last", lastButton);
		this.lastButton.setText(STRING_ACTION_NEWEST);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 1;
		this.lastButton.setLayoutData(gridData);
		this.lastButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				// グラフ更新
				updateByRangeAction(ACTION_NEWEST);
			}
		});
	}

	/**
	 * グラフコンポジットのウィジェットの構成初期化
	 *
	 * @param parent
	 * @param style
	 */
	private void initialize() {
		timeSeriesCollection = new TimeSeriesCollection();
		// ここでサブスコープグラフ表示用にSeriesを10個準備します

		// グラフ本数
		int lineNumMax = ClusterControlPlugin.getDefault().getPreferenceStore().getInt(PerformancePreferencePage.P_GRAPH_MAX);
		timeSeries = new TimeSeries[lineNumMax];
		for (int i = 0; i < timeSeries.length; i++) {
			timeSeries[i] = new TimeSeries("");
		}

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		this.setLayout( layout );

		// Button bar
		Composite buttonBar = new Composite( this, SWT.NONE );
		WidgetTestUtil.setTestId(this, "button", buttonBar);
		buttonBar.setLayoutData( new GridData( GridData.CENTER, GridData.BEGINNING, false, false ) );
		createButtonBar(buttonBar);

		// Graph Composite
		// TODO Encapsulate the following chart generator
		// Choose chart generator according to platform
		Composite chartBox = new Composite( this, SWT.NONE );
		WidgetTestUtil.setTestId(this, "chart", chartBox);
		chartBox.setLayoutData( new GridData( GridData.FILL_BOTH ) );
		chartBox.setLayout( new FillLayout() );

		if( ClusterControlPlugin.isRAP() ){
			this.chartBrowser = new Browser(chartBox, SWT.NONE );
			WidgetTestUtil.setTestId(this, "chart", chartBrowser);
		}else{
			this.chartComposite = new ChartComposite(chartBox, SWT.NONE, jfreeChart, true);
			WidgetTestUtil.setTestId(this, "chart", chartComposite);
		}

		// 初回表示時は、必ずView.doSelectTreeItem()が呼ばれるため、この処理の最後でグラフは作成しない
	}

	/**
	 * 表示項目一覧に表示するitemName/itemName[displayName]の作成
	 * @param info
	 * @return
	 */
	private String getFullItemName(CollectorItemParentInfo info){
		String str = "";
		if(info.getDisplayName() != null && !"".equals(info.getDisplayName())){
			str = PerformanceDataSettingsUtil.getItemName(this.performanceDataSettings,
					info.getItemCode()) + "[" + info.getDisplayName() + "]";
		}
		else{
			str = PerformanceDataSettingsUtil.getItemName(this.performanceDataSettings,
					info.getItemCode());
		}
		int maxStr = 30;
		if (maxStr < str.length()) {
			str = str.substring(0, maxStr);
			str += "...";
		}
		return str;
	}

	/**
	 * グラフのヘッダ情報(ファシリティツリー、収集ID、デバイス、などの情報)をビューに設定する
	 *
	 * @param info グラフのヘッダ情報
	 */
	private void setPerformanceDataSettings(PerformanceDataSettings info) {
		// グラフ表示のヘッダ情報をセット(performanceGraphInfoはインスタンス生成時にセットされた後は更新しない)
		this.performanceDataSettings = info;

		// 一番上の設定を表示
		if((this.performanceDataSettings.getItemCodeList()) != null
				&& (this.performanceDataSettings.getItemCodeList()).size() > 0){
			this.targetItemCodeInfo = (this.performanceDataSettings.getItemCodeList()).get(0);
		}
		else{
			this.targetItemCodeInfo = null;
		}

		// デバイス別表示用
		TreeSet<String> itemCodeSet = new TreeSet<String>();
		for (CollectorItemParentInfo itemCodeInfo : performanceDataSettings.getItemCodeList()){
			if(itemCodeInfo.getDisplayName() != null && !"".equals(itemCodeInfo.getDisplayName())){
				itemCodeSet.add(itemCodeInfo.getItemCode());
			}
		}
		for(String code : itemCodeSet){
			CollectorItemParentInfo itemCodeInfo = new CollectorItemParentInfo();
			itemCodeInfo.setItemCode(code);
			itemCodeInfo.setDisplayName("");
			itemCodeInfo.setParentItemCode("");
			itemCodeInfo.setCollectorId(performanceDataSettings.getMonitorId());
			itemCodeListByDisplayDevice.add(itemCodeInfo);
		}
	}

	/**
	 * FacilityTree の item を選択した際に表示されるメソッド(一番初めに呼ばれる描画処理)
	 */
	public void updateGraph(FacilityTreeItem item){
		m_log.debug("updateGraph() " +
				"facilityId = " + item.getData().getFacilityId() +
				", facilityType = " + item.getData().getFacilityType());

		// グラフプロットするデータがない場合
		if(targetItemCodeInfo == null){
			MessageDialog.openError(this.getShell(),
					Messages.getString("confirmed"), // "確認"
					Messages.getString("message.collection.6"));
			return;
		}

		// グラフの組み合わせとして表示可能なら
		if(validateGrapSettings(
				item.getData().getFacilityType(),
				targetDiaplayType,
				targetItemCodeInfo.getItemCode(),
				targetItemCodeInfo.getDisplayName())){

			this.targetFacilityTreeItem = item;

			updateGraph();
		}
	}

	/**
	 * 更新ボタンを押下した際に呼ばれるメソッド
	 *
	 * @param managerName マネージャ名
	 * @param graphType グラフ種別
	 * @param displayType 表示種別
	 * @param itemCodeInfo 収集項目ID
	 */
	private void updateGraph(String managerName, int graphType, int displayType, CollectorItemParentInfo itemCodeInfo){
		m_log.debug("updateGrap() graphType = " + graphType +
				", displayType = " + displayType +
				", itemCode = " + itemCodeInfo.getItemCode() +
				", displayName = " + itemCodeInfo.getDisplayName());

		// グラフプロットするデータがない場合
		if(targetItemCodeInfo == null){
			MessageDialog.openError(this.getShell(),
					Messages.getString("confirmed"), // "確認"
					Messages.getString("message.collection.6"));
			return;
		}

		// グラフの組み合わせとして表示可能なら
		if(validateGrapSettings(
				targetFacilityTreeItem.getData().getFacilityType(),
				displayType,
				itemCodeInfo.getItemCode(),
				itemCodeInfo.getDisplayName())){

			this.targetGraphType = graphType;
			this.targetDiaplayType = displayType;
			this.targetItemCodeInfo = itemCodeInfo;
			updateGraph();
		}
	}

	/**
	 * 指定したtargetの情報でグラフ更新
	 */
	private void updateGraph(){
		m_log.debug("updateGraph()");

		////
		// Title(タイトル)
		////
		ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
		jfreeChart =
				ChartFactory.createTimeSeriesChart("", // タイトルはここで未設定
						Messages.getString("timestamp"),
						performanceDataSettings.getMeasure(),
						timeSeriesCollection,
						true, true, false);

		// Show chart directly if in RAP
		if( ! ClusterControlPlugin.isRAP() ){
			chartComposite.setChart(jfreeChart);
		}

		////
		// Renderer/Color Settings(積み上げ、折れ線)
		int lineNumMax = ClusterControlPlugin.getDefault().getPreferenceStore().getInt(PerformancePreferencePage.P_GRAPH_MAX);

		switch (targetGraphType) {
		case GraphConstant.GRAPH_LINE:
			XYItemRenderer simplerenderer = jfreeChart.getXYPlot().getRenderer();
			if( simplerenderer instanceof XYLineAndShapeRenderer ){
				XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)simplerenderer;
				renderer.setBaseShapesVisible( false );
				renderer.setDrawSeriesLineAsPath( true );
				// set the default stroke for all series
				renderer.setAutoPopulateSeriesStroke(false);
			}
			for (int i = 0; i < lineNumMax; i++) {
				simplerenderer.setSeriesPaint(i, color[i%color.length]);
			}
			//jfreeChart.getXYPlot().setRenderer(renderer);
			break;
		case GraphConstant.GRAPH_STACK:
			XYItemRenderer renderer = new XYAreaRenderer();
			for (int i = 0; i < lineNumMax; i++) {
				renderer.setSeriesPaint(i, color[i%color.length]);
			}
			jfreeChart.getXYPlot().setRenderer(renderer);
			break;
		default:
			m_log.warn("updateGraph() targetGraphType is not defined. targetGraphType = " + targetGraphType);
			break;
		}

		////
		// Range Axis(Y軸)
		////
		if((HinemosModuleConstant.MONITOR_PERFORMANCE).equals(performanceDataSettings.getMonitorTypeId())){
			boolean rangeFixed =
					CollectorItemCodeFactory.isRangeFixed(
							managerName,
							performanceDataSettings.getTargetItemCode(),
							targetGraphType);

			if (rangeFixed) {
				ValueAxis valueaxis = jfreeChart.getXYPlot().getRangeAxis();
				valueaxis.setRange(0.0D, 100D);
			} else {
				NumberAxis valueaxis = (NumberAxis) (jfreeChart.getXYPlot().getRangeAxis());
				valueaxis.setAutoRangeIncludesZero(true);
			}
		}

		////
		// 収集データの検索条件(対象ファシリティID)
		////
		targetConditionFacilityIdList.clear();
		switch (targetFacilityTreeItem.getData().getFacilityType()) {

		case FacilityConstant.TYPE_NODE:
			// 指定したノードだけ
			targetConditionFacilityIdList.clear();
			targetConditionFacilityIdList.add(targetFacilityTreeItem.getData().getFacilityId());

			break;

		case FacilityConstant.TYPE_SCOPE:
			// 指定したスコープ配下に含まれる全てのノードを対象
			targetConditionFacilityIdList.clear();
			setTargetNodeFacilityId(targetFacilityTreeItem, targetConditionFacilityIdList, lineNumMax);

			break;
		}

		////
		// 収集データの検索条件(収集項目ID)
		////
		targetConditionItemList.clear();
		CollectorItemInfo itemInfo = null;
		switch (targetDiaplayType) {
		case GraphConstant.DISPLAY_NODE:
			// 指定した収集項目IDのみ
			itemInfo = new CollectorItemInfo();
			itemInfo.setCollectorId(this.performanceDataSettings.getMonitorId());
			itemInfo.setItemCode(this.targetItemCodeInfo.getItemCode());
			itemInfo.setDisplayName(this.targetItemCodeInfo.getDisplayName());

			targetConditionItemList.add(itemInfo);
			m_log.debug("updateGraph() DISPLAY_NODE itemCode = " + itemInfo.getItemCode() + ", displayName = " + itemInfo.getDisplayName());

			break;

		case GraphConstant.DISPLAY_ITEMCODE:
			// 指定した収集項目IDを親とする収集項目IDを持つもの全て
			for(CollectorItemParentInfo info : performanceDataSettings.getItemCodeList()){
				if(targetItemCodeInfo.getItemCode().equals(info.getParentItemCode())){
					itemInfo = new CollectorItemInfo();
					itemInfo.setCollectorId(this.performanceDataSettings.getMonitorId());
					itemInfo.setItemCode(info.getItemCode());
					itemInfo.setDisplayName(info.getDisplayName());
					targetConditionItemList.add(itemInfo);
					m_log.debug("updateGraph() DISPLAY_ITEMCODE itemCode = " + itemInfo.getItemCode() + ", displayName = " + itemInfo.getDisplayName());
				}
			}

			break;

		case GraphConstant.DISPLAY_DEVICE:
			// 指定した収集項目IDを同じ収集項目IDを持つもの(デバイス名が異なる)

			for(CollectorItemParentInfo info : performanceDataSettings.getItemCodeList()){
				if(targetItemCodeInfo.getItemCode().equals(info.getItemCode())){
					itemInfo = new CollectorItemInfo();
					itemInfo.setCollectorId(this.performanceDataSettings.getMonitorId());
					itemInfo.setItemCode(info.getItemCode());
					itemInfo.setDisplayName(info.getDisplayName());
					targetConditionItemList.add(itemInfo);
					m_log.debug("updateGraph() DISPLAY_ITEMCODE itemCode = " + itemInfo.getItemCode() + ", displayName = " + itemInfo.getDisplayName());
				}
			}

			break;
		}

		updateGraphRange();
	}

	/**
	 * FacilityTreeItemに所属する全てのノード(スコープではない)のファシリティIDをArrayListに設定する
	 *
	 * @param item
	 * @param facilityIdList
	 */
	private void setTargetNodeFacilityId(FacilityTreeItem treeItem, ArrayList<String> facilityIdList, int lineNumMax){
		m_log.debug("setTargetNodeFacilityId() treeItem = " + treeItem.getData().getFacilityId());
		if(facilityIdList != null && facilityIdList.size() >= lineNumMax){
			m_log.info("setTargetNodeFacilityId() target facilityId is over GRAPH_MAX = " + lineNumMax);
			String[] args = { Integer.toString(lineNumMax) };
			MessageDialog.openInformation(null, Messages.getString("message"),
					Messages.getString("message.performance.2", args));
			return;
		}

		List<FacilityTreeItem> children = treeItem.getChildren();

		for(int i = 0; i < children.size(); i++){
			FacilityTreeItem item = children.get(i);
			switch (item.getData().getFacilityType()) {
			case FacilityConstant.TYPE_NODE:
				if(facilityIdList != null && facilityIdList.size() >= lineNumMax){
					m_log.info("setTargetNodeFacilityId() target facilityId is over GRAPH_MAX = " + lineNumMax);
					String[] args = { Integer.toString(lineNumMax) };
					MessageDialog.openInformation(null, Messages.getString("message"),
							Messages.getString("message.performance.2", args));
					return;
				}
				facilityIdList.add(item.getData().getFacilityId());
				break;

			case FacilityConstant.TYPE_SCOPE:
				setTargetNodeFacilityId(item, facilityIdList, lineNumMax);
				break;

			default:
				break;
			}
		}
	}

	/**
	 * グラフの表示範囲の更新(時、日、週、月、の範囲変更)
	 *
	 * @param range
	 */
	private void updateGraphRange(int range){
		long start = System.currentTimeMillis();

		m_log.debug("updateGraphRange() range = " + range);
		// NULL CHECK
		if(targetConditionStartDate == null || targetConditionEndDate == null){
			m_log.debug("updateGraphRange() targetConditionStartDate = " + targetConditionStartDate + ", targetConditionEndDate = " + targetConditionEndDate);
			return;
		}

		////
		// 収集データの検索条件(開始時刻、終了時刻)
		////
		setTargetTimePeriod(range, targetConditionStartDate);

		updateGraphRange();

		m_log.debug(String.format("updateGraphRange: %dms", System.currentTimeMillis() - start));
	}

	/**
	 * X軸の単位形式を設定する
	 *
	 * @param range
	 */
	private void setRanePlotFormat(){
		switch (targetRange) {
		case GraphConstant.RANGE_HOUR:
			((DateAxis)jfreeChart.getXYPlot().getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("HH:mm"));

			break;

		case GraphConstant.RANGE_DAY:
			((DateAxis)jfreeChart.getXYPlot().getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("HH:mm"));

			break;

		case GraphConstant.RANGE_WEEK:
			((DateAxis)jfreeChart.getXYPlot().getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("MM/dd HH:mm"));

			break;

		case GraphConstant.RANGE_MONTH:
			((DateAxis)jfreeChart.getXYPlot().getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("MM/dd"));

			break;

		default:
			break;
		}
	}

	/**
	 * targetDateの時刻を含むrangeの期間の最初(tartDate)と最後(endDate)を設定する
	 *
	 * @param range
	 * @param targetDate
	 */
	private void setTargetTimePeriod(int range, Date targetDate){
		m_log.debug("setTargetTimePeriod() targetDate = " + targetDate);
		this.targetRange = range;

		switch (targetRange) {
		case GraphConstant.RANGE_HOUR:
			targetConditionStartDate = DateUtils.truncate(targetDate, Calendar.HOUR);
			targetConditionEndDate = new Date(targetConditionStartDate.getTime() + MILLISECOND_HOUR);

			break;

		case GraphConstant.RANGE_DAY:
			targetConditionStartDate = DateUtils.truncate(targetDate, Calendar.DAY_OF_MONTH);
			targetConditionEndDate = new Date(targetConditionStartDate.getTime() + MILLISECOND_DAY);

			break;

		case GraphConstant.RANGE_WEEK:
			Date tmpDate = DateUtils.truncate(targetDate, Calendar.DAY_OF_MONTH);
			Calendar cal = Calendar.getInstance();
			cal.setTime(tmpDate);
			int days = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;//days日前が日曜日

			targetConditionStartDate = new Date(tmpDate.getTime() - days * MILLISECOND_DAY);
			targetConditionEndDate = new Date(targetConditionStartDate.getTime() + MILLISECOND_WEEK);

			break;

		case GraphConstant.RANGE_MONTH:
			targetConditionStartDate = DateUtils.truncate(targetDate, Calendar.MONTH);
			targetConditionEndDate = new Date(targetConditionStartDate.getTime() + MILLISECOND_MONTH);

			break;

		default:
			break;
		}

		m_log.debug("setTargetTimePeriod() targetConditionStartDate = " + targetConditionStartDate + ", targetConditionEndDate = " + targetConditionEndDate);
	}

	/**
	 * ビューの更新時に呼ばれるアクション(最新時刻のデータを表示)
	 */
	@Override
	public void update() {
		super.update();
		updateByRangeAction(ACTION_NEWEST);
	}

	/**
	 * グラフの表示範囲の更新(次へ、前へ、等ボタン押下)
	 *
	 * @param action
	 */
	private void updateByRangeAction(int action){
		long start = System.currentTimeMillis();
		m_log.debug("updateByRangeAction() action = " + action);
		// NULL CHECK
		if(targetConditionStartDate == null || targetConditionEndDate == null){
			m_log.debug("updateGraphRange() targetConditionStartDate = " + targetConditionStartDate + ", targetConditionEndDate = " + targetConditionEndDate);
			return;
		}
		long distance = 0;

		////
		// 収集データの検索条件(開始時刻、終了時刻)
		////
		switch (action) {
		case ACTION_OLDEST:
			setTargetTimePeriod(targetRange, new Date(this.performanceDataSettings.getOldestDate()));
			break;
		case ACTION_PREV:
			distance = targetConditionEndDate.getTime() - targetConditionStartDate.getTime();
			targetConditionEndDate = targetConditionStartDate;
			targetConditionStartDate = new Date(targetConditionStartDate.getTime() - distance);
			break;
		case ACTION_NEXT:
			distance = targetConditionEndDate.getTime() - targetConditionStartDate.getTime();
			targetConditionStartDate = targetConditionEndDate;
			targetConditionEndDate = new Date(targetConditionEndDate.getTime() + distance);
			break;
		case ACTION_NEWEST:
			setTargetTimePeriod(targetRange, new Date(this.performanceDataSettings.getLatestDate()));
			break;
		default:
			break;
		}

		updateGraphRange();
		m_log.debug(String.format("updateByRangeAction: %dms", System.currentTimeMillis() - start));
	}

	/**
	 * グラフの描画期間の変更(X軸の指定/ビュー更新時はX軸)
	 */
	private void updateGraphRange(){
		////
		// 収集データの検索条件(開始時刻、終了時刻)
		////
		if(targetConditionStartDate == null || targetConditionEndDate == null){
			// 初期表示=最新時刻のデータを含む期間を指定
			setTargetTimePeriod(targetRange, new Date(performanceDataSettings.getLatestDate()));
		}

		// 描画範囲の設定
		ValueAxis domainAxis = this.jfreeChart.getXYPlot().getDomainAxis();
		domainAxis.setRange(targetConditionStartDate.getTime(), targetConditionEndDate.getTime());

		updateGraphData();
	}

	/**
	 * グラフデータの更新(ビュー更新時はこのメソッドを呼ぶ)
	 *
	 */
	private void updateGraphData(){
		m_log.debug("updateGraphData()");
		////
		// Range Axis(X軸)
		////
		setRanePlotFormat();

		////
		// 収集データの取得
		////
		CollectedDataSet dataSet =
				new RecordController().getRecordCollectedData(
						this.managerName,
						targetConditionFacilityIdList,
						targetConditionItemList,
						targetConditionStartDate,
						targetConditionEndDate);

		// 性能データの取得に失敗した場合はエラーダイアログを表示
		if (dataSet == null) {
			MessageDialog.openError(this.getShell(),
					Messages.getString("message.hinemos.1"), // "確認"
					Messages.getString("message.collection.7"));
			return;
		}

		////
		// 収集データのグラフ設定
		////

		// 表示されているものを削除
		int lineNumMax = ClusterControlPlugin.getDefault().getPreferenceStore().getInt(PerformancePreferencePage.P_GRAPH_MAX);
		//TODO 更新時は差分のみ追加する
		timeSeriesCollection.removeAllSeries();
		timeSeries = new TimeSeries[lineNumMax];
		for (int i = 0; i < timeSeries.length; i++) {
			timeSeries[i] = new TimeSeries("");
		}

		// データの登録
		int dataIndex = 0;
		List<CollectedDataInfo> collectedDataListTotal = null;
		outside: for (String facilityId : targetConditionFacilityIdList) {
			for (CollectorItemInfo itemInfo : targetConditionItemList){
				if(dataIndex == lineNumMax){
					String[] args = { Integer.toString(lineNumMax) };
					MessageDialog.openInformation(null, Messages.getString("message"),
							Messages.getString("message.performance.2", args));
					break outside;
				}

				List<CollectedDataInfo> collectedDataList = CollectedDataSetUtil.getCollectedDataList(dataSet, facilityId, itemInfo);
				String name = getGraphLegends(facilityId, itemInfo);

				switch (targetGraphType) {
				case GraphConstant.GRAPH_LINE:
					setGraphData(
							dataIndex,
							name,
							collectedDataList);
					break;

				case GraphConstant.GRAPH_STACK:
					//前回値と足し算をしてから設定
					collectedDataListTotal = getSumData(collectedDataListTotal, collectedDataList);
					setGraphData(
							dataIndex,
							name,
							collectedDataListTotal);
					break;

				default:
					break;
				}

				dataIndex++;
			}
		}

		// タイトルに時刻を表示
		String title = getGraphTitle();
		this.jfreeChart.setTitle(title);

		// Draw SVG after data updated
		if( ClusterControlPlugin.isRAP() ){
			// Reload or Redraw
			Point size = chartBrowser.getSize();
			if( 0 != size.x ){
				SVGGraphics2D g2d = new SVGGraphics2D(size.x, size.y);
				g2d.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
				Rectangle r = new Rectangle(0, 0, size.x, size.y);
				jfreeChart.draw(g2d, r);
				chartBrowser.setText( g2d.getSVGDocument() );
			}
		}
	}

	/**
	 * 表示種別(ノード別、収集項目別、デバイス別)の各々で示す凡例を返却する
	 *
	 * @param itemInfo
	 * @return
	 */
	private String getGraphLegends(String facilityId, CollectorItemInfo itemInfo){
		String name = null;

		switch (targetDiaplayType) {
		case GraphConstant.DISPLAY_NODE:
			name = facilityId;
			break;

		case GraphConstant.DISPLAY_ITEMCODE:
			if(itemInfo.getDisplayName() != null && !"".equals(itemInfo.getDisplayName())){
				name = PerformanceDataSettingsUtil.getItemName(performanceDataSettings, itemInfo.getItemCode()) + "[" + itemInfo.getDisplayName() + "]";
			}else{
				name = PerformanceDataSettingsUtil.getItemName(performanceDataSettings, itemInfo.getItemCode());
			}
			break;

		case GraphConstant.DISPLAY_DEVICE:
			name = "[" + itemInfo.getDisplayName() + "]";
			break;

		default:
			break;
		}

		return name;
	}

	/**
	 * 表示するグラフタイトルを作成
	 * @return
	 */
	private String getGraphTitle(){
		String title = null;

		// 表示項目名
		switch (targetDiaplayType) {
		case GraphConstant.DISPLAY_NODE:
			if(targetItemCodeInfo.getDisplayName() != null && !"".equals(targetItemCodeInfo.getDisplayName())){
				title = PerformanceDataSettingsUtil.getItemName(performanceDataSettings, targetItemCodeInfo.getItemCode()) + "[" + targetItemCodeInfo.getDisplayName() + "]";
			}else{
				title = PerformanceDataSettingsUtil.getItemName(performanceDataSettings, targetItemCodeInfo.getItemCode());
			}
			title = title + " " + STRING_DISPLAY_NODE;
			break;

		case GraphConstant.DISPLAY_ITEMCODE:
			title = PerformanceDataSettingsUtil.getItemName(performanceDataSettings, targetItemCodeInfo.getItemCode());
			title = title + " " + STRING_DISPLAY_ITEMCODE;
			break;

		case GraphConstant.DISPLAY_DEVICE:
			title = PerformanceDataSettingsUtil.getItemName(performanceDataSettings, targetItemCodeInfo.getItemCode());
			title = title + " " + STRING_DISPLAY_DEVICE;
			break;

		default:
			break;
		}

		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		title = title + "(" + df.format(targetConditionStartDate) + " - " + df.format(targetConditionEndDate) + ")";

		return title;
	}

	/**
	 * 2つの収集データの和を取得する。
	 * 積み上げグラフ用の前回値との和の取得用。
	 *
	 * @param dataSet1
	 * @param dataSet2
	 * @return
	 */
	private List<CollectedDataInfo> getSumData(List<CollectedDataInfo> dataSet1, List<CollectedDataInfo> dataSet2) {
		// Null Check
		if(dataSet1 == null && dataSet2 != null){
			return dataSet2;
		}
		if(dataSet2 == null && dataSet1 != null){
			return dataSet1;
		}
		if(dataSet1 == null && dataSet2 == null){
			return new ArrayList<CollectedDataInfo>();
		}
		
		// 空 check
		if(dataSet1.size() == 0 && dataSet2.size() > 0){
			return dataSet2;
		}
		if(dataSet2.size() == 0 && dataSet1.size() > 0){
			return dataSet1;
		}
		if(dataSet1.size() == 0 && dataSet2.size() == 0){
			return new ArrayList<CollectedDataInfo>();
		}


		ArrayList<CollectedDataInfo> dataList = new ArrayList<CollectedDataInfo>();

		// 時刻がずれている場合は、別時刻の収集値であるため加算しない
		for(CollectedDataInfo data1: dataSet1){
			for(CollectedDataInfo data2: dataSet2){
				if(data1.getD().longValue() == data2.getD().longValue()){
					CollectedDataInfo returnData = new CollectedDataInfo();
					returnData.setD(data1.getD());
					returnData.setV(data1.getV() + data2.getV());
					dataList.add(returnData);
					break;
				}
			}
		}

		return dataList;
	}

	/**
	 * index番目の線に凡例nameの線としてcollectedDataListをセットする
	 * @param index
	 * @param name
	 * @param collectedDataList
	 */
	private void setGraphData(int index, String name, List<CollectedDataInfo> collectedDataList){
		m_log.debug("setGraphData() index = " + index + ", name = " + name);
		long start = System.currentTimeMillis();

		// 凡例name
		TimeSeries a = timeSeriesCollection.getSeries(name);
		if (a == null) {
			timeSeries[index].setKey(name); // キーにファシリティ名を設定
			timeSeriesCollection.addSeries(timeSeries[index]);
		}

		Iterator<CollectedDataInfo> itr = collectedDataList.iterator();
		long date = 0;
		long datePrev = 0;
		while (itr.hasNext()) {
			CollectedDataInfo data = itr.next();
			if (!Double.isNaN(data.getV())) {
				if (date == data.getD()) {
					continue;
				}
				date = data.getD();
				if( data.getV() >= 0.0 && 1 < (date - datePrev) / 1000 ){ // Only add if > 1 sec. Preventing x-label duplicates
					timeSeries[index].add(new Second(new Date(date)), data.getV(), !(itr.hasNext()));
					datePrev = date;
				}

				// 表示するデータがヘッダの最新時刻より新しいものなら、ヘッダ情報を更新する
				if(data.getD() > performanceDataSettings.getLatestDate()){
					performanceDataSettings.setLatestDate(data.getD());
					m_log.trace("LatestDate Update = " + performanceDataSettings.getLatestDate());
				}
			}
		}
		m_log.debug("setGraphData() time=" + (System.currentTimeMillis() - start) + "ms, size=" + collectedDataList.size());
	}

	/**
	 * 指定の組み合わせでグラフの表示が可能かをチェックする
	 *
	 * @param facilityType 指定したファシリティIDの種別(ノードかスコープか)
	 * @param displayType 表示種別(ノード別、収集項目別、デバイス別)
	 * @param itemCode 表示項目ID
	 * @param displayName デバイス表示名
	 * @return
	 */
	private boolean validateGrapSettings(int facilityType, int displayType, String itemCode, String displayName){
		m_log.debug("validateGrapSettings() facilityType = " + facilityType + ", displayType = " + displayType + ", itemCode = " + itemCode + ", displayName = " + displayName);
		boolean valid = false;

		// スコープの場合は、ノード別表示のみ対応
		if(FacilityConstant.TYPE_SCOPE == facilityType){

			switch (displayType) {
			case GraphConstant.DISPLAY_NODE:
				valid = true;
				break;

			default:
				MessageDialog.openWarning(this.getShell(),
						Messages.getString("message.hinemos.1"), // "確認"
						Messages.getString("message.collection.3"));

				break;
			}

		}
		// ノードの場合、デバイス別表示と収集項目別のチェック
		else {

			switch (displayType) {
			// デバイス別表示の場合に、デバイスが1つ以上存在するか(空文字列以外)
			case GraphConstant.DISPLAY_DEVICE:
				for (CollectorItemParentInfo itemCodeInfo : performanceDataSettings.getItemCodeList()){
					if(itemCode != null && itemCode.equals(itemCodeInfo.getItemCode()) &&
							itemCodeInfo.getDisplayName() != null && !"".equals(itemCodeInfo.getDisplayName())){
						valid = true;
						break;
					}
				}

				if(!valid){
					MessageDialog.openWarning(this.getShell(),
							Messages.getString("message.hinemos.1"), // "確認"
							Messages.getString("message.collection.4"));
				}
				break;
			case GraphConstant.DISPLAY_ITEMCODE:
				for (CollectorItemParentInfo itemCodeInfo : this.performanceDataSettings.getItemCodeList()){
					if(itemCodeInfo.getParentItemCode() != null && itemCodeInfo.getParentItemCode().equals(itemCode)){
						valid = true;
						break;
					}
				}
				if(!valid){
					MessageDialog.openWarning(this.getShell(),
							Messages.getString("message.hinemos.1"), // "確認"
							Messages.getString("message.collection.5"));
				}
				break;
			default:
				valid = true;

				break;
			}

		}

		m_log.debug("validateGrapSettings() is " + valid);
		return valid;
	}

	/**
	 * jfreeChart の取得
	 *
	 * @return
	 */
	public JFreeChart getChart() {
		return jfreeChart;
	}

	/**
	 * グラフのヘッダ情報
	 *
	 * @return
	 */
	public PerformanceDataSettings getPerformanceDataSettings() {
		return performanceDataSettings;
	}
}
