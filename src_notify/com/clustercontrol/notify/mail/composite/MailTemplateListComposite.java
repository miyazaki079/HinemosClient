/*

Copyright (C) 2008 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.notify.mail.composite;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import com.clustercontrol.bean.Property;
import com.clustercontrol.notify.mail.action.GetMailTemplate;
import com.clustercontrol.notify.mail.action.GetMailTemplateListTableDefine;
import com.clustercontrol.notify.mail.composite.actioin.MailTemplateDoubleClickListener;
import com.clustercontrol.util.Messages;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.mailtemplate.MailTemplateInfo;
import com.clustercontrol.util.WidgetTestUtil;
;

/**
 * メールテンプレート一覧コンポジットクラス<BR>
 *
 * @version 2.4.0
 * @since 2.4.0
 */
public class MailTemplateListComposite extends Composite {

	/** テーブルビューアー。 */
	private CommonTableViewer tableViewer = null;

	/** 合計ラベル */
	private Label totalLabel = null;

	/** 検索条件 */
	private Property condition = null;

	/**
	 * インスタンスを返します。
	 * <p>
	 * 初期処理を呼び出し、コンポジットを配置します。
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public MailTemplateListComposite(Composite parent, int style) {
		super(parent, style);

		this.initialize();
	}

	/**
	 * コンポジットを配置します。
	 *
	 * @see com.clustercontrol.notify.action.GetMailTemplateListTableDefine#get()
	 * @see #update()
	 */
	private void initialize() {
		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Table table = new Table(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		WidgetTestUtil.setTestId(this, null, table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		table.setLayoutData(gridData);

		// テーブルビューアの作成
		this.tableViewer = new CommonTableViewer(table);
		this.tableViewer.createTableColumn(GetMailTemplateListTableDefine.get(),
				GetMailTemplateListTableDefine.SORT_COLUMN_INDEX1,
				GetMailTemplateListTableDefine.SORT_COLUMN_INDEX2,
				GetMailTemplateListTableDefine.SORT_ORDER);

		for (int i = 0; i < table.getColumnCount(); i++){
			table.getColumn(i).setMoveable(true);
		}

		// ダブルクリックリスナの追加
		this.tableViewer.addDoubleClickListener(new MailTemplateDoubleClickListener(this));

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;

		// 合計ラベルの作成
		this.totalLabel = new Label(this, SWT.RIGHT);
		WidgetTestUtil.setTestId(this, "total", totalLabel);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.totalLabel.setLayoutData(gridData);

	}

	/**
	 * このコンポジットが利用するテーブルビューアーを返します。
	 *
	 * @return テーブルビューアー
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
	 * コンポジットを更新します。<BR>
	 * メールテンプレート一覧情報を取得し、テーブルビューアーにセットします。
	 *
	 * @see com.clustercontrol.notify.mail.action.GetMailTemplate#getMailTemplateList()
	 */
	@Override
	public void update() {
		// データ取得
		Map<String, List<MailTemplateInfo>> dispDataMap = new GetMailTemplate().getMailTemplateList();
		ArrayList<ArrayList<Object>> listInput = new ArrayList<ArrayList<Object>>();

		int cnt = 0;
		for(Map.Entry<String, List<MailTemplateInfo>> entrySet : dispDataMap.entrySet()) {
			List<MailTemplateInfo> list = entrySet.getValue();
			if(list == null){
				list = new ArrayList<MailTemplateInfo>();
			}

			// tableViewer にセットするための詰め替え
			for (MailTemplateInfo mailTemplateInfo : list) {
				ArrayList<Object> a = new ArrayList<Object>();
				a.add(entrySet.getKey());
				a.add(mailTemplateInfo.getMailTemplateId());
				a.add(mailTemplateInfo.getDescription());
				a.add(mailTemplateInfo.getOwnerRoleId());
				a.add(mailTemplateInfo.getRegUser());
				if(mailTemplateInfo.getRegDate() == null){
					a.add(null);
				}
				else{
					a.add(new Date(mailTemplateInfo.getRegDate()));
				}
				a.add(mailTemplateInfo.getUpdateUser());
				if(mailTemplateInfo.getUpdateDate() == null){
					a.add(null);
				}
				else{
					a.add(new Date(mailTemplateInfo.getUpdateDate()));
				}
				a.add(null);
				listInput.add(a);
				cnt++;
			}
		}

		// テーブル更新
		this.tableViewer.setInput(listInput);

		// 合計欄更新
		String[] args = { new Integer(cnt).toString() };
		String message = null;
		if (this.condition == null) {
			message = Messages.getString("records", args);
		} else {
			message = Messages.getString("filtered.records", args);
		}
		this.totalLabel.setText(message);
	}
}
