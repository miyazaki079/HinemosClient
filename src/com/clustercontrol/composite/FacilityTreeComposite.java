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

package com.clustercontrol.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import com.clustercontrol.repository.bean.FacilityTreeAttributeConstant;
import com.clustercontrol.repository.util.RepositoryEndpointWrapper;
import com.clustercontrol.util.FacilityTreeCache;
import com.clustercontrol.util.FacilityTreeItemUtil;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.viewer.FacilityTreeContentProvider;
import com.clustercontrol.viewer.FacilityTreeLabelProvider;
import com.clustercontrol.viewer.FacilityTreeViewerSorter;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * スコープツリーを表示するコンポジットクラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class FacilityTreeComposite extends Composite {
	// ログ
	private static Log m_log = LogFactory.getLog( FacilityTreeComposite.class );

	//	 ----- instance フィールド ----- //

	/** テーブルビューア */
	private CommonTableViewer tableViewer = null;

	/** ツリービューア */
	//ノードマップオプションで使用するためprotected
	protected TreeViewer treeViewer = null;

	/** rootFacilityId以下のスコープ情報を取得する。 */
	private String rootFacilityId = null;

	/** tableViewer登録Item */
	private FacilityTreeItem treeItem = null;

	/** リポジトリツリーの時刻 */
	private Date cacheDate = null;

	/** 選択アイテム */
	//ノードマップオプションで使用するためprotected
	protected FacilityTreeItem selectItem = null;

	private List<?> selectionList;

	/** 選択アイテム数 */
	//ノードマップオプションで使用するためprotected
	protected int subScopeNumber;

	/**ノードをツリーに含めるか？ */
	private boolean scopeOnly = false;

	/**選択対象はノードだけか？ */
	boolean selectNodeOnly = false;

	/**未登録ノード　スコープをツリーに含めるか？**/
	private boolean unregistered = true;

	/**内部イベント　スコープをツリーに含めるか？**/
	private boolean internal = true;

	/** リポジトリ情報更新により、表示をリフレッシュするかどうか **/
	//ノードマップオプションで使用するためprotected
	protected boolean topicRefresh = true;

	/** オーナーロールID */
	private String ownerRoleId = null; // FIXME This variable is not used. Remove?

	/** parent Composite */
	private Composite parent = null;

	/** マネージャ名 */
	private String managerName = null;

	// ----- コンストラクタ ----- //
	public FacilityTreeComposite(Composite parent, int style,
			String managerName,
			String ownerRoleId,
			boolean selectNodeOnly) {
		super(parent, style);

		this.managerName = managerName;
		this.selectNodeOnly = selectNodeOnly;
		this.scopeOnly = false;
		this.unregistered = false;
		this.internal = false;
		this.parent = parent;
		this.ownerRoleId = ownerRoleId;
		this.createContents();
	}

	public FacilityTreeComposite(Composite parent, int style,
			String managerName,
			String ownerRoleId,
			boolean scopeOnly ,
			boolean unregistered,
			boolean internal) {
		super(parent, style);

		this.managerName = managerName;
		this.scopeOnly = scopeOnly;
		this.unregistered = unregistered;
		this.internal = internal;
		this.parent = parent;
		this.ownerRoleId = ownerRoleId;
		this.createContents();
	}

	/**
	 * 特定のスコープ配下のみを表示するコンストラクタ<br>
	 * （ver 4.1.0 現在、VM管理のみから呼び出されているコンストラクタ）
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 * @param managerName マネージャ名
	 * @param ownerRoleId オーナーロールID
	 * @param scopeOnly スコープのみのスコープツリーとするかどうか
	 * @param unregistered UNREGISTEREDをスコープツリーに含めるかどうか
	 * @param internal INTERNALをスコープツリーに含めるかどうか
	 * @param rootFacilityId ツリーのルートとするファシリティID
	 */
	public FacilityTreeComposite(Composite parent, int style,
			String managerName,
			String ownerRoleId,
			boolean scopeOnly ,
			boolean unregistered,
			boolean internal,
			String rootFacilityId) {
		super(parent, style);

		this.managerName = managerName;
		this.scopeOnly = scopeOnly;
		this.unregistered = unregistered;
		this.internal = internal;
		this.rootFacilityId = rootFacilityId;
		this.parent = parent;
		this.ownerRoleId = ownerRoleId;
		this.createContents();
	}

	/**
	 * コンストラクタ
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 * @param ownerRoleId オーナーロールID
	 * @param scopeOnly スコープのみのスコープツリーとするかどうか
	 * @param unregistered UNREGISTEREDをスコープツリーに含めるかどうか
	 * @param internal INTERNALをスコープツリーに含めるかどうか
	 * @param topicRefresh リポジトリ情報が更新された際に画面リフレッシュするかどうか
	 */
	public FacilityTreeComposite(Composite parent, int style,
			String ownerRoleId,
			boolean scopeOnly ,
			boolean unregistered,
			boolean internal,
			boolean topicRefresh) {
		super(parent, style);

		this.scopeOnly = scopeOnly;
		this.unregistered = unregistered;
		this.internal = internal;
		this.topicRefresh = topicRefresh;
		this.parent = parent;
		this.ownerRoleId = ownerRoleId;
		this.createContents();
	}

	// ----- instance メソッド ----- //


	/**
	 * このコンポジットが利用するツリービューアを返します。
	 *
	 * @return ツリービューア
	 */
	public TreeViewer getTreeViewer() {
		return this.treeViewer;
	}

	/**
	 * このコンポジットが利用するツリーを返します。
	 *
	 * @return ツリー
	 */
	public Tree getTree() {
		return this.treeViewer.getTree();
	}

	/**
	 * 現在選択されているツリーアイテムを返します。
	 *
	 * @return ツリーアイテム
	 */
	public FacilityTreeItem getSelectItem() {
		return this.selectItem;
	}

	/*
	 * ツリーアイテムを選択します。
	 */
	public void setSelectItem(FacilityTreeItem item) {
		selectItem = item;
	}

	/** 現在選択されているツリーアイテムリストを返します。
	 *
	 * @return
	 */
	public List<?> getSelectionList() {
		return this.selectionList;
	}

	/**

	/**
	 * 現在選択されているツリーのサブスコープ数を返します。
	 *
	 * @return サブスコープ数
	 */
	public int getSubScopeNumber() {
		return subScopeNumber;
	}

	/**
	 * コンポジットを生成します。
	 * 
	 * ノードマップオプションで使用するためprotected
	 */
	protected void createContents() {

		// コンポジットのレイアウト定義
		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		this.setLayout(layout);

		// ツリーのレイアウトデータ定義
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.verticalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;

		// ツリー作成
		Tree tree = new Tree(this, SWT.MULTI | SWT.BORDER);
		WidgetTestUtil.setTestId(this, null, tree);
		tree.setLayoutData(layoutData);

		// ツリービューア作成
		this.treeViewer = new TreeViewer(tree);

		// ツリービューア設定
		this.treeViewer.setContentProvider(new FacilityTreeContentProvider());
		this.treeViewer.setLabelProvider(new FacilityTreeLabelProvider());
		this.treeViewer.setSorter(new FacilityTreeViewerSorter());

		// 選択アイテム取得イベント定義
		this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				StructuredSelection selection = (StructuredSelection) event.getSelection();

				selectItem = (FacilityTreeItem) selection.getFirstElement();
				selectionList = selection.toList();

				if (selectItem instanceof FacilityTreeItem) {
					subScopeNumber = selectItem.getChildren().size();
				}
			}
		});

		//マネージャからのファシリティツリー更新
		final FacilityTreeComposite composite = this;
		if (topicRefresh) {
			FacilityTreeCache.addComposite(composite);
		}

		this.addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				FacilityTreeCache.delComposite(composite);
			}
		});

		// 表示します。
		this.update();
	}

	/**
	 * ビューの表示内容を更新します。
	 */
	@Override
	public void update() {
		// 外部契機でファシリティツリーが更新された場合に、自分の画面もリフレッシュ
		if (this.ownerRoleId != null) {
			try {
				if (this.selectNodeOnly) {
					// ノードのみ取得
					m_log.debug("getNodeFacilityTree " + managerName);
					treeItem = RepositoryEndpointWrapper.getWrapper(managerName).getNodeFacilityTree(this.ownerRoleId);
				} else {
					m_log.debug("getFacilityTree " + managerName);
					treeItem = RepositoryEndpointWrapper.getWrapper(managerName).getFacilityTree(this.ownerRoleId);
				}
			} catch (Exception e) {
				m_log.warn("getTreeItem(), " + e.getMessage(), e);
				return;
			}
		} else {
			treeItem = FacilityTreeCache.getTreeItem(managerName);
		}

		Date cacheDate = null;
		if (managerName != null) {
			cacheDate = FacilityTreeCache.getCacheDate(managerName);
		}
		if (cacheDate != null && cacheDate.equals(this.cacheDate)) {
			return;
		}
		this.cacheDate = cacheDate;

		if( null == treeItem ){
			m_log.trace("treeItem is null. Skip.");
		}else {
			//"スコープ"というツリーから直下のスコープを削除する。
			FacilityTreeItem scope = (treeItem.getChildren()).get(0);

			//ファシリティツリーから特定のスコープを取り外す。
			if(!internal){
				if(FacilityTreeItemUtil.removeChild(scope, FacilityTreeAttributeConstant.INTERNAL_SCOPE)){
				}else{
					//まずい。"INTERNAL"スコープがない！
				}
			}
			if(!unregistered){
				if(FacilityTreeItemUtil.removeChild(scope, FacilityTreeAttributeConstant.UNREGISTEREFD_SCOPE)){
				}else{
					//まずい。"UNREGISTERED"スコープがない！
				}
			}
			if (rootFacilityId != null) {
				// rootFacilityId以外を全て消す。
				FacilityTreeItemUtil.keepChild(scope, rootFacilityId);
			}
			if (scopeOnly) {
				FacilityTreeItemUtil.removeNode(scope);
			}

			// SWTアクセスを許可するスレッドからの操作用
			checkAsyncExec(new Runnable(){
				@Override
				public void run() {
					m_log.trace("FacilityTreeComposite.checkAsyncExec() do runnnable");

					Control control = treeViewer.getControl();
					if (control == null || control.isDisposed()) {
						m_log.info("treeViewer is disposed. ");
						return;
					}
					
					FacilityTreeItem oldTreeItem = (FacilityTreeItem)treeViewer.getInput();
					m_log.debug("run() oldTreeItem=" + oldTreeItem);
					if( null != oldTreeItem ){
						if (!oldTreeItem.equals(treeItem)) {
							ArrayList<String> expandIdList = new ArrayList<String>();
							for (Object item : treeViewer.getExpandedElements()) {
								expandIdList.add(((FacilityTreeItem)item).getData().getFacilityId());
							}
							m_log.debug("expandIdList.size=" + expandIdList.size());
							treeViewer.setInput(treeItem);
							treeViewer.refresh();
							expand(treeItem, expandIdList);
						}
					}else{
						treeViewer.setInput(treeItem);
						List<FacilityTreeItem> selectItem = treeItem.getChildren();
						treeViewer.setSelection(new StructuredSelection(selectItem.get(0)), true);
						//スコープのレベルまで展開
						treeViewer.expandToLevel(3);
					}
				}

				private void expand(FacilityTreeItem item, List<String> expandIdList) {
					if (expandIdList.contains(item.getData().getFacilityId())) {
						treeViewer.expandToLevel(item, 1);
					}
					for (FacilityTreeItem child : item.getChildren()) {
						expand(child, expandIdList);
					}
				}
			});
		}
	}

	/**
	 * 同期チェック
	 * @param r
	 * @return
	 */
	private boolean checkAsyncExec(Runnable r){

		if(!this.isDisposed()){
			m_log.trace("FacilityTreeComposite.checkAsyncExec() is true");
			parent.getDisplay().asyncExec(r);
			return true;
		}
		else{
			m_log.trace("FacilityTreeComposite.checkAsyncExec() is false");
			return false;
		}
	}

	/**
	 * ツリーを展開して表示するかを指定します。
	 *
	 */
	public void setExpand(boolean isExpand) {
		if (isExpand) {
			this.treeViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		}
	}

	/**
	 * ツリーの表示内容を更新します。
	 *
	 * @param treeItem
	 */
	public void setScopeTree(FacilityTreeItem treeItem) {
		try {
			this.treeItem = treeItem;
			this.treeViewer.setInput(treeItem);
			this.treeViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		} catch (Exception e) {
			m_log.warn("setScopeTree(), " + e.getMessage(), e);
		}
	}

	/**
	 * TreeをセットしながらfacilityIDに対応する要素を選択状態にします。
	 *
	 * @param treeItem
	 * @param facilityID
	 */
	public void setScopeTreeWithSelection(FacilityTreeItem treeItem,
			String facilityID) {
		this.setScopeTree(treeItem);

		List<FacilityTreeItem> tmpItem = treeItem.getChildren();

		//引数のFaiclityIDに対応するTreeItemがあるか探します。
		for (int i = 0; i < tmpItem.size(); i++) {
			setScopeTreeWithSelectionSub(tmpItem.get(i), facilityID);
			if (facilityID.equals(tmpItem.get(i).getData().getFacilityId())) {
				this.treeViewer.setSelection(
						new StructuredSelection(tmpItem.get(i)), true);
			}
		}
	}

	/**
	 * setScopeTreeWithSelectionから呼ばれること前提とした再帰用のメソッド
	 *
	 * @param treeItem
	 * @param facilityID
	 */
	public void setScopeTreeWithSelectionSub(FacilityTreeItem treeItem,
			String facilityID) {
		List<FacilityTreeItem> tmpItem = treeItem.getChildren();

		for (int i = 0; i < tmpItem.size(); i++) {
			setScopeTreeWithSelectionSub(tmpItem.get(i), facilityID);

			if (facilityID.equals(tmpItem.get(i).getData().getFacilityId())) {
				this.treeViewer.setSelection(
						new StructuredSelection(tmpItem.get(i)), true);

			}
		}
	}

	public CommonTableViewer getTableViewer() {
		return tableViewer;
	}

	public void setTableViewer(CommonTableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	/**
	 * このコンポジットが利用するテーブルを返します。
	 *
	 * @return テーブル
	 */
	public Table getTable() {
		return this.tableViewer.getTable();
	}

}
