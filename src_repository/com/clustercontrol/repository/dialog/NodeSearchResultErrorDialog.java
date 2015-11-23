/*

Copyright (C) 2007 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.repository.dialog;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.repository.NodeInfoDeviceSearch;

/**
 * リポジトリ[ノードサーチ]のノードサーチエラー実行結果ダイアログクラスです。
 *
 * @version 5.0.0
 * @since 5.0.0
 */
public class NodeSearchResultErrorDialog extends Dialog {

	public static final int WIDTH_TITLE = 1;
	public static final int WIDTH_TEXT = 8;

	// ログ
	private static Log m_log = LogFactory.getLog(NodeSearchResultErrorDialog.class);

	private List<NodeInfoDeviceSearch> nodeinfoList;
	private Shell shell;

	/**
	 * コンストラクタ
	 *
	 * @param parent 親シェル
	 */
	public NodeSearchResultErrorDialog(Shell parent, List<NodeInfoDeviceSearch> list) {
		super(parent);
		this.nodeinfoList = list;
	}

	/**
	 * ダイアログエリアを生成します。
	 *
	 * @param parent 親コンポジット
	 */
	@Override
	protected Control createContents(Composite parent) {
		this.shell = this.getShell();
		GridData gridData;

		// タイトル
		shell.setText(Messages.getString("message"));

		// レイアウト
		GridLayout layout = new GridLayout(1, false);
		shell.setLayout(layout);

		//ベースコンポジット（タイトル部 + データ部）
		Composite baseCmp = new Composite(shell, SWT.NONE);
		WidgetTestUtil.setTestId(this, "base", baseCmp);
		baseCmp.setLayout(new FillLayout());
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		baseCmp.setLayoutData(gridData);

		//登録ノード情報のグループ
		Group group = new Group(baseCmp, SWT.RIGHT);
		WidgetTestUtil.setTestId(this, null, group);
		group.setLayout(new GridLayout(1, true));
		int count = 0;
		for(NodeInfoDeviceSearch info : this.nodeinfoList) {
			if (info.getErrorNodeInfo() != null) {
				count++;
			}
		}
		Object[] arg = {count};
		group.setText(Messages.getString("message.repository.nodesearch.9", arg));

		gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gridData.horizontalSpan = WIDTH_TITLE;

		//タイトル部
		Composite titleCmp = new Composite(group, SWT.NONE);
		WidgetTestUtil.setTestId(this, "title", titleCmp);
		titleCmp.setLayout(new GridLayout(WIDTH_TEXT, true));
		titleCmp.setLayoutData(gridData);

		// ファシリティID
		setDataGrid(titleCmp, Messages.getString("facility.id"), 5);
		// IPアドレス
		setDataGrid(titleCmp, Messages.getString("ip.address"), 3);

		// ラインを引く
		Label line = new Label(titleCmp, SWT.SEPARATOR | SWT.HORIZONTAL);
		WidgetTestUtil.setTestId(this, "line", line);
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = WIDTH_TEXT;
		line.setLayoutData(gridData);

		//データ部
		Composite cntCmp = new Composite(group, SWT.NONE);
		WidgetTestUtil.setTestId(this, "cnt", cntCmp);
		cntCmp.setLayout(new FillLayout());
		gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.horizontalSpan = WIDTH_TITLE;
		cntCmp.setLayoutData(gridData);

		//スクロール用のコンポジット
		ScrolledComposite sCmp = new ScrolledComposite(cntCmp, SWT.V_SCROLL);

		Composite fbrCmp = new Composite(sCmp, SWT.NONE);
		WidgetTestUtil.setTestId(this, "fbr", fbrCmp);
		//取得した情報
		for (NodeInfoDeviceSearch info : nodeinfoList) {
			if (info.getErrorNodeInfo() == null) {
				continue;
			}
			setDataGrid(fbrCmp, info.getNodeInfo().getFacilityId(), 5);
			setDataGrid(fbrCmp, info.getNodeInfo().getIpAddressV4(), 3);
		}

		fbrCmp.setLayout(new GridLayout(WIDTH_TEXT, true));
		fbrCmp.pack();
		Point point = fbrCmp.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		fbrCmp.setSize(point.x, point.y);
        sCmp.setContent(fbrCmp);

        sCmp.setMinHeight(point.y);
        sCmp.setExpandHorizontal(true);
        sCmp.setExpandVertical(true);
        sCmp.pack();

        //ボタン部
        Composite btnCmp = new Composite(shell, SWT.NONE);
		WidgetTestUtil.setTestId(this, "btn", btnCmp);
		layout = new GridLayout(1, false);
        btnCmp.setLayout(layout);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCmp.setLayoutData(gridData);

        Button b = new Button(btnCmp, SWT.PUSH);
        WidgetTestUtil.setTestId(this, null, b);
        b.setText(Messages.getString("ok"));

		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;

		b.setLayoutData(gridData);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				shell.close();
			}
		});

		// サイズを最適化
		// グリッドレイアウトを用いた場合、こうしないと横幅が画面いっぱいになる
		shell.pack();
		shell.setSize(new Point(450, 300));
        m_log.debug("sCmp getSize():" + sCmp.getSize());
        m_log.debug("fbrCmp getSize():" +  fbrCmp.getSize());
        m_log.debug("shell getSize():" + shell.getSize());

		// 画面中央に配置
		Display display = shell.getDisplay();
		shell.setLocation((display.getBounds().width - shell.getSize().x) / 2,
				(display.getBounds().height - shell.getSize().y) / 2);

		return sCmp;
	}

	private void setDataGrid(Composite parent, String val, int horizontalSpan) {
		Label label = new Label(parent, SWT.WRAP);
		WidgetTestUtil.setTestId(this, null, label);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = horizontalSpan;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(val);
	}

}
