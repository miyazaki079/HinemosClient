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

package com.clustercontrol.repository.view;

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
import com.clustercontrol.repository.composite.AgentListComposite;
import com.clustercontrol.repository.view.action.AgentRestartAction;
import com.clustercontrol.repository.view.action.AgentUpdateAction;
import com.clustercontrol.repository.view.action.RefreshAgentAction;
import com.clustercontrol.view.CommonViewPart;

/**
 * ノート一覧ビュークラス<BR>
 *
 * @version 5.0.0
 * @since 1.0.0
 */
public class AgentListView extends CommonViewPart {
	public static final String ID = AgentListView.class.getName();

	// ----- instance フィールド ----- //

	/** ノード一覧コンポジット */
	private AgentListComposite composite = null;

	/** 選択レコード数 */
	private int rowNum = 0;

	// ----- コンストラクタ ----- //

	/**
	 * インスタンスを返します。
	 */
	public AgentListView() {
		super();
	}

	// ----- instance メソッド ----- //

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		composite = new AgentListComposite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);

		//ポップアップメニュー作成
		createContextMenu();

		// ボタン（アクション）を制御するリスナーを登録
		composite.getTableViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						//リポジトリ[エージェント]ビューのインスタンスを取得
						IWorkbenchPage page = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						IViewPart viewPart = page.findView(AgentListView.ID);
						//選択アイテムを取得
						StructuredSelection selection = (StructuredSelection) event.getSelection();

						if ( viewPart != null && selection != null) {
							AgentListView view = (AgentListView) viewPart.getAdapter(AgentListView.class);
							//ビューのボタン（アクション）の使用可/不可を設定する
							view.setEnabledAction(selection.size(), event.getSelection());
						}
					}
				});
		update();
	}

	/**
	 * ポップアップメニュー作成
	 *
	 *
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
	 * compositeを返します。
	 *
	 * @return composite
	 */
	public AgentListComposite getComposite() {
		return composite;
	}

	/**
	 * エージェントの一覧を表示します。
	 * <p>
	 */
	public void update() {
		composite.update();
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
			service.refreshElements(AgentRestartAction.ID, null);
			service.refreshElements(AgentUpdateAction.ID, null);
			service.refreshElements(RefreshAgentAction.ID, null);

			// Update ToolBar after elements refreshed
			// WARN : Both ToolBarManager must be updated after updateActionBars(), otherwise icon won't change.
			getViewSite().getActionBars().updateActionBars();
			getViewSite().getActionBars().getToolBarManager().update(false);

		}
	}
}
