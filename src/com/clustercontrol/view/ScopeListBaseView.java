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

package com.clustercontrol.view;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.composite.FacilityTreeComposite;
import com.clustercontrol.repository.FacilityPath;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.ws.repository.FacilityTreeItem;

/**
 * スコープツリーと合わせて利用するビューを作成するための基本的な実装を持つクラス<BR>
 * <p>
 *
 * 基本的には、createContentsをオーバーライドし、追加コンテンツを生成して下さい。 <br>
 * また、必要に応じてdoSelectTreeItemをオーバーライドし、ツリーアイテム選択時の イベント処理を実装して下さい。
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ScopeListBaseView extends AutoUpdateView {

	// ----- instance フィールド ----- //

	/** サッシュフォーム */
	private SashForm treeSash = null;

	/** スコープツリーのコンポジット */
	private FacilityTreeComposite scopeTreeComposite = null;

	/** 追加コンポジットのベース */
	private Composite baseComposite = null;

	/** 追加コンポジット */
	private Composite listComposite = null;

	/** パス文字列を表示するラベル */
	private Label pathLabel = null;

	/** ノードをツリーに含めるか？ */
	private boolean scopeOnly = false;

	/** 未登録ノード　スコープをツリーに含めるか？**/
	private boolean unregistered = true;

	/** 内部イベント　スコープをツリーに含めるか？**/
	private boolean internal = true;

	/** リポジトリ情報更新により、表示をリフレッシュするかどうか **/
	private boolean topicRefresh = true;
	
	/** スコープツリーのコンポジットと右側のコンポジットの割合。 */
	private int sashPer = 30;

	// ----- コンストラクタ ----- //

	/**
	 * 親のコンストラクタを呼び出します。
	 */
	public ScopeListBaseView() {
		super();
		this.scopeOnly = false;
		this.unregistered = true;
		this.internal = true;
		this.topicRefresh = true;
	}

	/**
	 * 親のコンストラクタを呼び出します。
	 *
	 * @param scopeOnly スコープのみのスコープツリーとするかどうか
	 * @param unregistered UNREGISTEREDをスコープツリーに含めるかどうか
	 * @param internal INTERNALをスコープツリーに含めるかどうか
	 * @param topicRefresh リポジトリ情報が更新された際に画面リフレッシュするかどうか
	 */
	public ScopeListBaseView(boolean scopeOnly ,boolean unregistered, boolean internal, boolean topicRefresh) {
		super();

		this.scopeOnly = scopeOnly;
		this.unregistered = unregistered;
		this.internal = internal;
		this.topicRefresh = topicRefresh;
	}

	// ----- instance メソッド ----- //

	/**
	 * ビューを生成します。
	 *
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		// レイアウト設定
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		// サッシュフォーム作成及び設定
		this.treeSash = new SashForm(parent, SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 1;
		this.treeSash.setLayoutData(gridData);

		// スコープツリー作成
		this.scopeTreeComposite = new FacilityTreeComposite(treeSash, SWT.NONE, null, this.scopeOnly, this.unregistered, this.internal, this.topicRefresh);

		// 追加コンポジットのベース作成
		this.baseComposite = new Composite(this.treeSash, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, baseComposite);

		// パス文字列表示ラベル作成
		this.pathLabel = new Label(this.baseComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "path", pathLabel);
		this.pathLabel.setText(Messages.getString("scope") + " : ");
		//		this.pathLabel.pack();

		// 追加コンテンツ作成
		this.listComposite = this.createListContents(this.baseComposite);

		// Sashの境界を調整 左部30% 右部70%
		treeSash.setWeights(new int[] { sashPer, 100 - sashPer });

		// ツリーアイテム選択時のリスナー追加
		this.scopeTreeComposite.getTreeViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						// 選択アイテム取得(ツリー自体でも行っているが、念のため)
						StructuredSelection selection = (StructuredSelection) event.getSelection();
						FacilityTreeItem selectItem = (FacilityTreeItem) selection
								.getFirstElement();
						if (selectItem instanceof FacilityTreeItem) {
							// パスラベルの更新
							FacilityPath path = new FacilityPath(
									ClusterControlPlugin.getDefault()
									.getSeparator());
							pathLabel.setText(Messages.getString("scope")
									+ " : "  + path.getPath(selectItem));
							//							pathLabel.pack();
							baseComposite.layout(true, true);
							// イベントメソッド呼び出し
							doSelectTreeItem(selectItem);
						}
					}
				});
	}
	
	/**
	 * Sashの割合を変更したいときに呼ぶ。
	 * コンストラクタ内部で呼ぶこと。
	 * @param per
	 */
	protected void setSash(int per) {
		sashPer = per;
	}

	/**
	 * 追加コンテンツを作成します。
	 * <p>
	 *
	 * オーバーライドして、追加コンポジットを生成して下さい。
	 *
	 * @param parent
	 *            追加コンテンツのベースコンポジット
	 */
	protected abstract Composite createListContents(Composite parent);

	/**
	 * スコープツリーのアイテムが選択された場合に呼び出されるメソッドです。
	 * <p>
	 *
	 * 必要に応じてオーバーライドし、アイテム選択時のイベント処理を実装して下さい。
	 *
	 * @param item
	 *            スコープツリーアイテム
	 */
	protected void doSelectTreeItem(FacilityTreeItem item) {

	}

	/**
	 * このビューのレイアウトを構築するサッシュフォームを返します。
	 *
	 * @return サッシュフォーム
	 */
	public SashForm getTreeSash() {
		return this.treeSash;
	}

	/**
	 * スコープツリーのコンポジットを返します。
	 *
	 * @return スコープツリーのコンポジット
	 */
	public FacilityTreeComposite getScopeTreeComposite() {
		return this.scopeTreeComposite;
	}

	/**
	 * 追加コンポジットのベースを返します。
	 *
	 * @return 追加コンポジットのベース
	 */
	public Composite getBaseComposite() {
		return this.baseComposite;
	}

	/**
	 * 追加コンポジットを返します。
	 *
	 * @return 追加コンポジット
	 */
	public Composite getListComposite() {
		return this.listComposite;
	}

	/**
	 * パス文字列を表示するラベルを返します。
	 *
	 * @return パス文字列を表示するラベル
	 */
	public Label getPathLabel() {
		return this.pathLabel;
	}

	/**
	 * 表示します。
	 */
	public void show() {
		this.treeSash.setMaximizedControl(null);
	}

	/**
	 * 隠します。
	 */
	public void hide() {
		this.treeSash.setMaximizedControl(this.baseComposite);
	}
}
