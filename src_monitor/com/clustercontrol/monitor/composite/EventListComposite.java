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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.accesscontrol.util.ClientSession;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.monitor.action.CommentEvent;
import com.clustercontrol.monitor.action.GetEventListTableDefine;
import com.clustercontrol.monitor.dialog.EventInfoDialog;
import com.clustercontrol.monitor.preference.MonitorPreferencePage;
import com.clustercontrol.monitor.util.ConvertListUtil;
import com.clustercontrol.monitor.util.EventFilterPropertyUtil;
import com.clustercontrol.monitor.util.EventSearchRunUtil;
import com.clustercontrol.monitor.view.EventView;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.PropertyUtil;
import com.clustercontrol.util.UIManager;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.monitor.EventDataInfo;
import com.clustercontrol.ws.monitor.EventFilterInfo;
import com.clustercontrol.ws.monitor.ViewListInfo;

/**
 * イベント情報一覧のコンポジットクラス<BR>
 *
 * イベント情報一覧部分のテーブルのコンポジット
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class EventListComposite extends Composite {
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
	public EventListComposite(Composite parent, int style) {
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
		this.tableViewer.createTableColumn(GetEventListTableDefine.getEventListTableDefine(),
				GetEventListTableDefine.SORT_COLUMN_INDEX1,
				GetEventListTableDefine.SORT_COLUMN_INDEX2,
				GetEventListTableDefine.SORT_ORDER);

		for (int i = 0; i < table.getColumnCount(); i++){
			table.getColumn(i).setMoveable(true);
		}
		// ダブルクリックした場合、イベントログの詳細情報ダイアログを表示する

		this.tableViewer.addDoubleClickListener(
				new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent event) {

						// 選択アイテムを取得する
						List<?> list = (List<?>) ((StructuredSelection)event.getSelection()).getFirstElement();

						EventInfoDialog dialog = new EventInfoDialog(m_shell, list);
						if (dialog.open() == IDialogConstants.OK_ID) {
							Property eventdetail = dialog.getInputData();
							CommentEvent comment = new CommentEvent();
							String managerName = (String) list.get(GetEventListTableDefine.MANAGER_NAME);
							comment.updateComment(managerName, eventdetail);
							IWorkbench workbench = ClusterControlPlugin.getDefault().getWorkbench();
							IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

							EventView eventView = (EventView) page.findView(EventView.ID);
							if (eventView != null){
								eventView.update();
							}
						}
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
	 * 引数で指定されたファシリティの配下全てのファシリティのイベント一覧情報を取得し、
	 * 共通テーブルビューアーにセットします。
	 * <p>
	 * <ol>
	 * <li>監視管理のプレファレンスページより、監視[イベント]ビューの表示イベント数を取得します。</li>
	 * <li>引数で指定されたファシリティに属するイベント一覧情報を、表示イベント数分取得します。</li>
	 * <li>表示イベント数を超える場合、メッセージダイアログを表示します。</li>
	 * <li>共通テーブルビューアーにイベント情報一覧をセットします。</li>
	 * </ol>
	 *
	 * @param facilityId 表示対象の親ファシリティID
	 * @param condition 検索条件（条件なしの場合はnull）
	 * @param managerList マネージャ名リスト
	 * @see #updateStatus(ViewListInfo)
	 */
	public void update(String facilityId, Property condition, List<String> managerList) {
		super.update();

		/** 表示用リスト */
		Map<String, ViewListInfo> dispDataMap= new ConcurrentHashMap<>();
		Map<String, String> errorMsgs = new ConcurrentHashMap<>();

		if(condition == null) {
			dispDataMap = getEventList(facilityId, managerList);
		} else {
			dispDataMap = getEventListByCondition(facilityId, condition, managerList);
		}

		//メッセージ表示
		if( 0 < errorMsgs.size() ){
			UIManager.showMessageBox(errorMsgs, true);
		}

		List<EventDataInfo> eventListRaw = ConvertListUtil.eventLogDataMap2SortedList(dispDataMap);
		ArrayList<ArrayList<Object>> eventList = ConvertListUtil.eventLogList2Input(eventListRaw);
		int total = 0;
		for(Map.Entry<String, ViewListInfo> entrySet : dispDataMap.entrySet()) {
			total += entrySet.getValue().getTotal();
		}

		if(ClusterControlPlugin.getDefault().getPreferenceStore().getBoolean(
				MonitorPreferencePage.P_EVENT_MESSAGE_FLG)){
			if(total > eventList.size()){
				if(ClientSession.isDialogFree()){
					ClientSession.occupyDialog();
					// 最大表示件数を超える場合、エラーダイアログを表示する
					MessageDialogWithToggle.openInformation(
							null,
							Messages.getString("message"),
							Messages.getString("message.monitor.12"),
							Messages.getString("message.will.not.be.displayed"),
							false,
							ClusterControlPlugin.getDefault().getPreferenceStore(),
							MonitorPreferencePage.P_EVENT_MESSAGE_FLG);
					ClientSession.freeDialog();
				}
			}
		}

		this.updateStatus(eventListRaw);
		tableViewer.setInput(eventList);
	}

	private Map<String, ViewListInfo> getEventList(String facilityId, List<String> managerList) {
		Map<String, ViewListInfo> map = new ConcurrentHashMap<>();
		int messages = ClusterControlPlugin.getDefault().getPreferenceStore().getInt(
				MonitorPreferencePage.P_EVENT_MAX);
		EventSearchRunUtil bean = new EventSearchRunUtil();
		map = bean.searchInfo(managerList, facilityId, null, messages);

		return map;
	}

	private Map<String, ViewListInfo> getEventListByCondition(String facilityId,
			Property condition, List<String> managerList) {
		Map<String, ViewListInfo> map = new ConcurrentHashMap<>();
		int messages = ClusterControlPlugin.getDefault().getPreferenceStore().getInt(
				MonitorPreferencePage.P_EVENT_MAX);

		PropertyUtil.deletePropertyDefine(condition);
		EventFilterInfo filter = EventFilterPropertyUtil.property2dto(condition);
		EventSearchRunUtil bean = new EventSearchRunUtil();
		map = bean.searchInfo(managerList, facilityId, filter, messages);
		return map;
	}

	/**
	 * ステータスラベルを更新します。<BR>
	 * 引数で指定されたビュー一覧情報より、重要度ごとの件数，全件数を取得し、
	 * ステータスラベルを更新します。
	 *
	 * @param map ビュー一覧情報
	 */
	private void updateStatus(List<EventDataInfo> list) {
		// 表示最大件数の取得
		int critical = 0, warning = 0, info = 0, unknown = 0, total = 0;
		for( EventDataInfo eventInfo : list ){
			// ラベル更新
			switch( eventInfo.getPriority() ){
			case( PriorityConstant.TYPE_INFO ):
				info++;
				break;
			case( PriorityConstant.TYPE_WARNING ):
				warning++;
				break;
			case( PriorityConstant.TYPE_CRITICAL ):
				critical++;
				break;
			case( PriorityConstant.TYPE_UNKNOWN ):
				unknown++;
				break;
			}
			total++;
		}
		this.criticalLabel.setText(String.valueOf(critical));
		this.warningLabel.setText(String.valueOf(warning));
		this.infoLabel.setText(String.valueOf(info));
		this.unknownLabel.setText(String.valueOf(unknown));
		this.totalLabel.setText(Messages.getString("records", new Object[]{ total }));
	}
}
