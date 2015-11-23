/*

 Copyright (C) 2006 NTT DATA Corporation

 This program is free software; you can redistribute it and/or
 Modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation, version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 */

package com.clustercontrol.performance.view;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.Property;
import com.clustercontrol.performance.bean.PerformanceStatusConstant;
import com.clustercontrol.performance.composite.PerformanceListComposite;
import com.clustercontrol.performance.composite.action.PerformanceListSelectionChangedListener;
import com.clustercontrol.performance.view.action.AddGraphAction;
import com.clustercontrol.performance.view.action.ExportCollectedDataAction;
import com.clustercontrol.performance.view.action.PerformanceListFilterAction;
import com.clustercontrol.performance.view.action.RefreshPerformanceListAction;
import com.clustercontrol.view.CommonViewPart;
/**
 * 性能[一覧]ビュークラス<BR>
 *
 * @version 5.0.0
 * @since 4.0.0
 */
public class PerformanceListView extends CommonViewPart {


	/** 性能[一覧]ビューID */
	public static final String ID = PerformanceListView.class.getName();

	/** 性能一覧コンポジット */
	private PerformanceListComposite composite = null;

	/** 検索条件 */
	private Property condition = null;

	/** 実績収集の状態 */
	private int status = PerformanceStatusConstant.TYPE_INIT;

	/**
	 * コンストラクタ
	 */
	public PerformanceListView() {
		super();
	}

	/**
	 * ViewPartへのコントロール作成処理<BR>
	 *
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		composite = new PerformanceListComposite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.composite.setLayoutData(gridData);

		//ポップアップメニュー作成
		createContextMenu();

		// ボタン（アクション）を制御するリスナーを登録
		this.composite.getTableViewer().addSelectionChangedListener(
				new PerformanceListSelectionChangedListener());

		this.update();
	}

	/**
	 * コンテキストメニューを作成します。
	 *
	 * @see org.eclipse.jface.action.MenuManager
	 * @see org.eclipse.swt.widgets.Menu
	 */
	private void createContextMenu() {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		Menu menu = menuManager.createContextMenu(composite.getTable());
		WidgetTestUtil.setTestId(this, null, menu);
		composite.getTable().setMenu(menu);
		getSite().registerContextMenu( menuManager, this.composite.getTableViewer() );
	}

	/**
	 * 追加コンポジットを返します。
	 *
	 * @return 追加コンポジット
	 */
	public Composite getListComposite() {
		return this.composite;
	}

	/**
	 * 検索条件にヒットした監視設定の一覧を表示します。
	 * <p>
	 *
	 * conditionがnullの場合、全監視設定を表示します。
	 *
	 * @param condition
	 *            検索条件
	 */
	public void update(Property condition) {
		this.condition = condition;

		this.update();
	}

	/**
	 * 検索条件を設定します。
	 * <p>設定後の<code>update</code>は、この検索条件の結果が表示されます。
	 *
	 * @param condition 検索条件
	 */
	public void setCondition(Property condition) {
		this.condition = condition;
	}

	/**
	 * ビューを更新します。
	 * <p>
	 *
	 * 検索条件が事前に設定されている場合、その条件にヒットする監視設定の一覧を 表示します <br>
	 * 検索条件が設定されていない場合は、全監視設定を表示します。
	 */
	public void update() {
		this.composite.update(this.condition);
	}

	/**
	 * 選択されている監視ID
	 * @return
	 */
	public String getSelectedMonitorId(){
		return composite.getSelectedMonitorId();
	}

	/**
	 * 選択されている監視IDの監視種別ID
	 * @return
	 */
	public String getSelectedMonitorTypeId(){
		return composite.getSelectedMonitorTypeId();
	}

	/**
	 * 選択されているマネージャ名を返します。
	 * @return
	 */
	public String getSelectedManagerName(){
		return composite.getSelectedManagerName();
	}

	/**
	 * 実績収集ステータスを返します。
	 * @return status
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * ボタンを初期状態へ戻す
	 */
	public void initButton() {
		setEnabledAction(-1, null);
	}

	/**
	 * ビューのアクションの有効/無効を設定します。
	 *
	 * @param num 選択イベント数
	 * @param selection ボタン（アクション）を有効にするための情報
	 */
	public void setEnabledAction(int status, ISelection selection) {
		this.status = status;

		//ビューアクションの使用可/不可を設定
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService( ICommandService.class );
		if( null != service ){

			service.refreshElements(AddGraphAction.ID, null);
			service.refreshElements(ExportCollectedDataAction.ID, null);
			service.refreshElements(PerformanceListFilterAction.ID, null);
			service.refreshElements(RefreshPerformanceListAction.ID, null);

			// Update ToolBar after elements refreshed
			// WARN : Both ToolBarManager must be updated after updateActionBars(), otherwise icon won't change.
			getViewSite().getActionBars().updateActionBars();
			getViewSite().getActionBars().getToolBarManager().update(false);

		}
	}

}
