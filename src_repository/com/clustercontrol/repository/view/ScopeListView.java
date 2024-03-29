/**********************************************************************
 * Copyright (C) 2014 NTT DATA Corporation
 * This program is free software; you can redistribute it and/or
 * Modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, version 2.
 *
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.  See the GNU General Public License for more details.
 *********************************************************************/

package com.clustercontrol.repository.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.accesscontrol.util.ClientSession;
import com.clustercontrol.accesscontrol.util.ObjectBean;
import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.composite.FacilityTreeComposite;
import com.clustercontrol.repository.composite.ScopeListComposite;
import com.clustercontrol.repository.composite.action.FacilityTreeSelectionChangedListener;
import com.clustercontrol.repository.composite.action.ScopeListSelectionChangedListener;
import com.clustercontrol.repository.util.ScopePropertyUtil;
import com.clustercontrol.repository.view.action.NodeAssignAction;
import com.clustercontrol.repository.view.action.NodeReleaseAction;
import com.clustercontrol.repository.view.action.RefreshAction;
import com.clustercontrol.repository.view.action.ScopeAddAction;
import com.clustercontrol.repository.view.action.ScopeDeleteAction;
import com.clustercontrol.repository.view.action.ScopeModifyAction;
import com.clustercontrol.repository.view.action.ScopeObjectPrivilegeAction;
import com.clustercontrol.repository.view.action.ScopeShowAction;
import com.clustercontrol.view.ObjectPrivilegeTargetListView;
import com.clustercontrol.view.ScopeListBaseView;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * スコープ登録ビュークラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class ScopeListView extends ScopeListBaseView implements ObjectPrivilegeTargetListView {
	public static final String ID = ScopeListView.class.getName();

	// ----- instance フィールド ----- //

	/** リポジトリ[スコープ]ビュー用コンポジット */
	private ScopeListComposite composite = null;

	/** Last focus composite (Tree/List) */
	private Composite lastFocusComposite = null;

	/** 組み込みスコープかどうかのフラグ */
	private boolean builtin;

	/** スコープとノードの種別 */
	private int type;

	/** ボタン（アクション）を有効にするための情報 */
	private boolean notReferFlg;

	// ----- コンストラクタ ----- //

	// ----- instance メソッド ----- //

	/**
	 * @see com.clustercontrol.view.ScopeListBaseView#createListContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Composite createListContents(Composite parent) {
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		this.composite = new ScopeListComposite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.composite.setLayoutData(gridData);

		//ポップアップメニュー作成
		createContextMenu();

		//Listenerの追加
		super.getScopeTreeComposite().getTreeViewer().addSelectionChangedListener(
				new FacilityTreeSelectionChangedListener());

		this.composite.getTableViewer().addSelectionChangedListener(
				new ScopeListSelectionChangedListener(composite));

		this.update();
		setLastFocusComposite(this.getScopeTreeComposite());

		return this.composite;
	}

	/**
	 * ポップアップメニュー作成
	 *
	 *
	 */
	private void createContextMenu() {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		Menu treeMenu = menuManager.createContextMenu(this.getScopeTreeComposite().getTree());
		this.getScopeTreeComposite().getTree().setMenu(treeMenu);

		Menu listMenu = menuManager.createContextMenu(this.composite.getTable());
		WidgetTestUtil.setTestId(this, null, listMenu);
		this.composite.getTable().setMenu(listMenu);
		getSite().registerContextMenu(menuManager, this.composite.getTableViewer());
	}

	/**
	 * 選択されたスコープ(ノード)の情報を表示します。
	 *
	 * @param item
	 *            ツリーアイテム
	 */
	@Override
	protected void doSelectTreeItem(FacilityTreeItem item) {
		this.composite.update(item);
	}

	/**
	 * ビューを更新します。
	 *
	 * @see com.clustercontrol.view.AutoUpdateView#update()
	 */
	@Override
	public void update() {
		ClientSession.doCheck();
	}

	/**
	 * アダプターとして要求された場合、自身のインスタンスを渡します。
	 *
	 * @param cls
	 *            クラスのインスタンス
	 * @return 自身のインスタンス
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class cls) {
		if (cls.isInstance(this)) {
			return this;
		} else {
			return super.getAdapter(cls);
		}
	}

	public ScopeListComposite getComposite() {
		return this.composite;
	}

	public boolean getBuiltin() {
		return this.builtin;
	}

	public int getType() {
		return this.type;
	}

	public boolean getNotReferFlg() {
		return this.notReferFlg;
	}

	/**
	 * ビューのアクションの有効/無効を設定します。
	 *
	 * @param notReferFlg 参照不可フラグ
	 *
	 * @see com.clustercontrol.bean.FacilityConstant
	 */
	public void setEnabledAction(boolean builtin,int type, ISelection selection, boolean notReferFlg) {
		this.builtin = builtin;
		this.type = type;
		this.notReferFlg = notReferFlg;

		//ビューアクションの使用可/不可を設定
		ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService( ICommandService.class );
		if( null != service ){
			service.refreshElements(ScopeAddAction.ID, null);
			service.refreshElements(ScopeModifyAction.ID, null);
			service.refreshElements(ScopeDeleteAction.ID, null);
			service.refreshElements(NodeAssignAction.ID, null);
			service.refreshElements(NodeReleaseAction.ID, null);
			service.refreshElements(ScopeObjectPrivilegeAction.ID, null);
			service.refreshElements(RefreshAction.ID, null);
			service.refreshElements(ScopeShowAction.ID, null);

			// Update ToolBar after elements refreshed
			// WARN : Both ToolBarManager must be updated after updateActionBars(), otherwise icon won't change.
			getViewSite().getActionBars().updateActionBars();
			getViewSite().getActionBars().getToolBarManager().update(false);
		}
	}

	@Override
	public List<ObjectBean> getSelectedObjectBeans() {

		// 選択されているスコープを取得する
		FacilityTreeItem item = getSelectedScopeItem();

		// 選択されており、スコープの場合は値を返す
		List<ObjectBean> objectBeans = new ArrayList<ObjectBean>();
		if (item != null) {
			FacilityTreeItem manager = ScopePropertyUtil.getManager(item);
			String managerName = manager == null ? item.getData().getFacilityId() : manager.getData().getFacilityId();
			String objectId = item.getData().getFacilityId();
			String objectType = HinemosModuleConstant.PLATFORM_REPOSITORY;
			ObjectBean objectBean = new ObjectBean(managerName, objectType, objectId);
			objectBeans.add(objectBean);
		}
		return objectBeans;
	}

	@Override
	public String getSelectedOwnerRoleId() {

		// 選択されているスコープを取得する
		FacilityTreeItem item = getSelectedScopeItem();

		// 選択されており、スコープの場合は値を返す
		String ownerRoleId = null;
		if (item != null) {
			ownerRoleId = item.getData().getOwnerRoleId();
		}
		return ownerRoleId;
	}

	public FacilityTreeItem getSelectedScopeItem() {
		FacilityTreeComposite tree = this.getScopeTreeComposite();
		ScopeListComposite list = (ScopeListComposite) this.getListComposite();

		// tree.getTree().isFocusControl() is not working under RAP because of Toolbar focus
		FacilityTreeItem item = null;
		if( this.lastFocusComposite instanceof FacilityTreeComposite ){
			item = tree.getSelectItem();
		}else if( this.lastFocusComposite instanceof ScopeListComposite ){
			item = list.getSelectItem();
		}

		// 選択されており、スコープの場合は値を返す
		if( null != item ){
			return item;
		} else {
			return null;
		}
	}

	public List<?> getSelectedScopeItems() {
		FacilityTreeComposite tree = this.getScopeTreeComposite();
		ScopeListComposite list = (ScopeListComposite) this.getListComposite();

		// tree.getTree().isFocusControl() is not working under RAP because of Toolbar focus
		List<?> items = null;
		if( this.lastFocusComposite instanceof FacilityTreeComposite ){
			items = tree.getSelectionList();
		}else if( this.lastFocusComposite instanceof ScopeListComposite ){
			StructuredSelection selection = (StructuredSelection)list.getTableViewer().getSelection();
			items = selection.toList();
		}

		// 選択されており、スコープの場合は値を返す
		if( !( null == items || items.isEmpty() ) ){
			return items;
		} else {
			return null;
		}
	}

	/**
	 * Set last focus composite(Tree/List)
	 * @param composite
	 */
	public void setLastFocusComposite( Composite composite ){
		if( ! composite.equals(this.lastFocusComposite) ){
			this.lastFocusComposite = composite;
		}
	}
}
