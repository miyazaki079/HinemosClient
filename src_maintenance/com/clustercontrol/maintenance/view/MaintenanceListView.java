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

package com.clustercontrol.maintenance.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.accesscontrol.util.ObjectBean;
import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.maintenance.action.GetMaintenanceListTableDefine;
import com.clustercontrol.maintenance.composite.MaintenanceListComposite;
import com.clustercontrol.maintenance.view.action.MaintenanceAddAction;
import com.clustercontrol.maintenance.view.action.MaintenanceCopyAction;
import com.clustercontrol.maintenance.view.action.MaintenanceDeleteAction;
import com.clustercontrol.maintenance.view.action.MaintenanceDisableAction;
import com.clustercontrol.maintenance.view.action.MaintenanceEnableAction;
import com.clustercontrol.maintenance.view.action.MaintenanceModifyAction;
import com.clustercontrol.maintenance.view.action.MaintenanceRefreshAction;
import com.clustercontrol.maintenance.view.action.ObjectPrivilegeMaintenanceListAction;
import com.clustercontrol.view.CommonViewPart;
import com.clustercontrol.view.ObjectPrivilegeTargetListView;

/**
 * メンテナンス[一覧]ビュークラス<BR>
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class MaintenanceListView extends CommonViewPart implements ObjectPrivilegeTargetListView {

	/** メンテナンス[一覧]ビューID */
	public static final String ID = MaintenanceListView.class.getName();

	/** メンテナンス[一覧]コンポジット */
	private MaintenanceListComposite composite = null;

	/** 選択レコード数 */
	private int rowNum = 0;

	/**
	 * コンストラクタ
	 */
	public MaintenanceListView() {
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

		composite = new MaintenanceListComposite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.composite.setLayoutData(gridData);

		//ポップアップメニュー作成
		createContextMenu();

		this.composite.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				//メンテナンス[履歴情報削除]ビューのインスタンスを取得
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IViewPart viewPart = page.findView(MaintenanceListView.ID);
				//選択アイテムを取得
				StructuredSelection selection = (StructuredSelection) event.getSelection();

				if ( viewPart != null && selection != null) {
					MaintenanceListView view = (MaintenanceListView) viewPart.getAdapter(MaintenanceListView.class);
					//ビューのボタン（アクション）の使用可/不可を設定する
					view.setEnabledAction(selection.size(), event.getSelection());
				}
			}
		});

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
		this.update();
	}

	/**
	 * ビューを更新します。
	 * <p>
	 *
	 * 検索条件が事前に設定されている場合、その条件にヒットする監視設定の一覧を 表示します <br>
	 * 検索条件が設定されていない場合は、全監視設定を表示します。
	 */
	@Override
	public void update() {
		this.composite.update();
	}

	/**
	 * 選択レコード数を返します。
	 * @return rowNum
	 */
	public int getSelectedNum(){
		return this.rowNum;
	}

	/**
	 * ビューのアクションの有効/無効を設定します。
	 *
	 * @param num 選択イベント数
	 * @param selection ボタン（アクション）を有効にするための情報
	 */
	public void setEnabledAction(int num, ISelection selection) {
		this.rowNum = num;

		//ビューアクションの使用可/不可を設定
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService( ICommandService.class );
		if( null != service ){
			service.refreshElements(ObjectPrivilegeMaintenanceListAction.ID, null);
			service.refreshElements(MaintenanceAddAction.ID, null);
			service.refreshElements(MaintenanceModifyAction.ID, null);
			service.refreshElements(MaintenanceDeleteAction.ID, null);
			service.refreshElements(MaintenanceCopyAction.ID, null);
			service.refreshElements(MaintenanceEnableAction.ID, null);
			service.refreshElements(MaintenanceDisableAction.ID, null);
			service.refreshElements(MaintenanceRefreshAction.ID, null);

			// Update ToolBar after elements refreshed
			// WARN : Both ToolBarManager must be updated after updateActionBars(), otherwise icon won't change.
			getViewSite().getActionBars().updateActionBars();
			getViewSite().getActionBars().getToolBarManager().update(false);
		}
	}

	public String getSelectedId() {
		StructuredSelection selection = (StructuredSelection) this.composite.getTableViewer().getSelection();

		List<?> list = (List<?>) selection.getFirstElement();
		String id = null;
		if (list != null) {
			id = (String) list.get(GetMaintenanceListTableDefine.MAINTENANCE_ID);
		}
		return id;
	}

	@Override
	public List<ObjectBean> getSelectedObjectBeans() {
		StructuredSelection selection = (StructuredSelection) this.composite.getTableViewer().getSelection();
		Object [] objs = selection.toArray();

		String managerName = null;
		String objectType = HinemosModuleConstant.SYSYTEM_MAINTENANCE;
		String objectId = null;
		List<ObjectBean> objectBeans = new ArrayList<ObjectBean>();
		for (Object obj : objs) {
			managerName = (String) ((List<?>)obj).get(GetMaintenanceListTableDefine.MANAGER_NAME);
			objectId = (String) ((List<?>)obj).get(GetMaintenanceListTableDefine.MAINTENANCE_ID);
			ObjectBean objectBean = new ObjectBean(managerName, objectType, objectId);
			objectBeans.add(objectBean);
		}
		return objectBeans;
	}

	@Override
	public String getSelectedOwnerRoleId() {
		StructuredSelection selection = (StructuredSelection) this.composite.getTableViewer().getSelection();

		List<?> list = (List<?>) selection.getFirstElement();
		String id = null;
		if (list != null) {
			id = (String) list.get(GetMaintenanceListTableDefine.OWNER_ROLE);
		}
		return id;
	}

}
