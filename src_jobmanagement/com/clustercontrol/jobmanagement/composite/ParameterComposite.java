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

package com.clustercontrol.jobmanagement.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.action.GetParameterTableDefine;
import com.clustercontrol.jobmanagement.bean.JobParamTypeConstant;
import com.clustercontrol.jobmanagement.bean.SystemParameterConstant;
import com.clustercontrol.jobmanagement.composite.action.ParameterSelectionChangedListener;
import com.clustercontrol.jobmanagement.dialog.ParameterDialog;
import com.clustercontrol.util.Messages;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.jobmanagement.JobParameterInfo;

/**
 * ジョブ変数タブ用のコンポジットクラスです。
 *
 * @version 2.1.0
 * @since 2.1.0
 */
public class ParameterComposite extends Composite {
	/** テーブルビューア */
	private CommonTableViewer m_viewer = null;
	/** 追加ボタン */
	private Button m_createCondition = null;
	/** 変更ボタン */
	private Button m_modifyCondition = null;
	/** 削除ボタン */
	private Button m_deleteCondition = null;
	/** ジョブ変数情報のリスト */
	private List<JobParameterInfo> m_paramList = null;
	/** シェル */
	private Shell m_shell = null;
	/** 選択アイテム */
	private List<?> m_selectItem = new ArrayList<Object>();
	/** 初期時フラグ(初期時はデフォルトパラメータを投入する。) */
	private boolean initFlag;

	/**
	 * コンストラクタ
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public ParameterComposite(Composite parent, int style, boolean initFlag) {
		super(parent, style);
		initialize();
		m_shell = this.getShell();
		this.initFlag = initFlag;
	}

	/**
	 * コンポジットを構築します。
	 */
	private void initialize() {
		RowLayout layout = new RowLayout();
		layout.type = SWT.VERTICAL;
		layout.spacing = 1;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.fill = true;
		this.setLayout(layout);

		RowLayout rowLayout = null;

		Composite paramComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "table", paramComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		paramComposite.setLayout(rowLayout);

		Label tableTitle = new Label(paramComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "tabletitle", tableTitle);
		tableTitle.setText(Messages.getString("list"));

		Table table = new Table(paramComposite, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		WidgetTestUtil.setTestId(this, null, table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new RowData(430, 235));

		Composite paramComposite2 = new Composite(paramComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "create", paramComposite2);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 5;
		paramComposite2.setLayout(rowLayout);

		//パラメータの追加
		Label dummy1 = new Label(paramComposite2, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy1", dummy1);
		dummy1.setLayoutData(new RowData(190, SizeConstant.SIZE_LABEL_HEIGHT));
		m_createCondition = new Button(paramComposite2, SWT.NONE);
		WidgetTestUtil.setTestId(this, "createcondition", m_createCondition);
		m_createCondition.setText(Messages.getString("add"));
		m_createCondition.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_createCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ParameterDialog dialog = new ParameterDialog(m_shell);
				if (dialog.open() == IDialogConstants.OK_ID) {
					ArrayList<?> info = dialog.getInputData();
					ArrayList<Object> list = (ArrayList<Object>) m_viewer.getInput();
					if (list == null) {
						list = new ArrayList<Object>();
					}else{
						// Check if parameter name already exists
						for( Object one : list ){
							String name = (String)((ArrayList<Object>)one).get(0);
							if( name.equals( info.get(0) ) ){
								MessageDialog.openError( null, Messages.getString("message.hinemos.1"), Messages.getString("message.hinemos.10", new Object[]{Messages.getString("job.parameter"), name}) );
								return;
							}
						}
					}
					// Finally, add it
					list.add(info);
					m_viewer.setInput(list);
				}
			}
		});

		//パラメータの変更
		m_modifyCondition = new Button(paramComposite2, SWT.NONE);
		WidgetTestUtil.setTestId(this, "modifycondition", m_modifyCondition);
		m_modifyCondition.setText(Messages.getString("modify"));
		m_modifyCondition.setLayoutData(new RowData(80, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_modifyCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ParameterDialog dialog = new ParameterDialog(m_shell);
				ArrayList<Object> objList = m_selectItem.isEmpty() ? null : (ArrayList<Object>)m_selectItem.get(0);
				if (objList instanceof ArrayList) {
					dialog.setInputData(objList);
					if (dialog.open() == IDialogConstants.OK_ID) {
						ArrayList<?> info = dialog.getInputData();
						ArrayList<Object> list = (ArrayList<Object>) m_viewer.getInput();

						list.remove(objList);
						list.add(info);

						m_selectItem = null;
						m_viewer.setInput(list);
					}
				}
			}
		});

		//パラメータの削除
		m_deleteCondition = new Button(paramComposite2, SWT.NONE);
		WidgetTestUtil.setTestId(this, "deletecondition", m_deleteCondition);
		m_deleteCondition.setText(Messages.getString("delete"));
		m_deleteCondition.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_deleteCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ArrayList<?> list = (ArrayList<?>) m_viewer.getInput();
				for(Object obj : m_selectItem) {
					if(obj instanceof ArrayList) {
						@SuppressWarnings("unchecked")
						ArrayList<Object> objList =  (ArrayList<Object>)obj;
						list.remove(objList);
						m_selectItem = new ArrayList<Object>();
						m_viewer.setInput(list);
					}
				}
			}
		});

		m_viewer = new CommonTableViewer(table);
		m_viewer.createTableColumn(GetParameterTableDefine.get(),
				GetParameterTableDefine.SORT_COLUMN_INDEX,
				GetParameterTableDefine.SORT_ORDER);

		// ジョブユニット登録時はシステム変数を自動で登録する。
		ArrayList<ArrayList<Object>> infoList = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> info = null;
		for (List<String> param : SystemParameterConstant.ALL_JOBS) {
			info = new ArrayList<Object>();

			//名前
			info.add(param.get(0));
			//種別
			info.add(JobParamTypeConstant.TYPE_SYSTEM_JOB);
			//値
			info.add("(" + Messages.getString("auto") + ")");
			//説明
			info.add(param.get(1));

			infoList.add(info);
		}

		// ジョブ変数のシステム(ノード)は自動で登録しない。ジョブ履歴のサイズが大きくなってしまうため。
		/*
		for (List<String> param : SystemParameterConstant.ALL_NODES) {
			info = new ArrayList<Object>();

			//名前
			info.add(param.get(0));
			//種別
			info.add(JobParamTypeConstant.TYPE_SYSTEM_NODE);
			//値
			info.add("(" + Messages.getString("auto") + ")");
			//説明
			info.add(param.get(1));

			infoList.add(info);
		}
		*/

		m_viewer.setInput(infoList);

		m_viewer.addSelectionChangedListener(
				new ParameterSelectionChangedListener(this));
	}

	/**
	 * ジョブ変数情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobParameterInfo
	 */
	public void reflectParamInfo() {
		if (!initFlag) {
			//パラメータ設定
			ArrayList<ArrayList<?>> tableData = new ArrayList<ArrayList<?>>();
			for (int i = 0; i < m_paramList.size(); i++) {
				JobParameterInfo info = m_paramList.get(i);
				ArrayList<Object> tableLineData = new ArrayList<Object>();
				tableLineData.add(info.getParamId());
				tableLineData.add(new Integer(info.getType()));
				if (info.getType() == JobParamTypeConstant.TYPE_SYSTEM_JOB ||
						info.getType() == JobParamTypeConstant.TYPE_SYSTEM_NODE) {
					tableLineData.add("(" + Messages.getString("auto") + ")");
				}
				else if (info.getType() == JobParamTypeConstant.TYPE_USER) {
					tableLineData.add(info.getValue());
				}
				tableLineData.add(info.getDescription());
				tableData.add(tableLineData);
			}
			m_viewer.setInput(tableData);
		}
	}

	/**
	 * ジョブ変数情報を設定します。
	 *
	 * @param paramList ジョブ変数情報のリスト
	 */
	public void setParamInfo(List<JobParameterInfo> paramList) {
		m_paramList = paramList;
	}

	/**
	 * ジョブ変数情報を返します。
	 *
	 * @return ジョブ変数情報のリスト
	 */
	public List<JobParameterInfo> getParamInfo() {
		return m_paramList;
	}

	/**
	 * コンポジットの情報から、ジョブ変数情報を作成します。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobParameterInfo
	 */
	public ValidateResult createParamInfo() {

		//パラメータ情報のインスタンスを作成・取得
		m_paramList = new ArrayList<JobParameterInfo>();

		//パラメータ取得
		ArrayList<?> tableData = (ArrayList<?>) m_viewer.getInput();
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (tableData instanceof ArrayList) {
			for (int i = 0; i < tableData.size(); i++) {
				ArrayList<?> tableLineData = (ArrayList<?>) tableData.get(i);
				JobParameterInfo info = new JobParameterInfo();
				Integer type =
						(Integer) tableLineData.get(GetParameterTableDefine.TYPE);
				info.setType(type);
				if (info.getType() == JobParamTypeConstant.TYPE_SYSTEM_JOB ||
						info.getType() == JobParamTypeConstant.TYPE_SYSTEM_NODE) {
					info.setParamId((String)tableLineData.get(
							GetParameterTableDefine.PARAM_ID));
					info.setDescription((String)tableLineData.get(
							GetParameterTableDefine.DESCRIPTION));
				}
				else if (info.getType() == JobParamTypeConstant.TYPE_USER) {
					info.setParamId((String)tableLineData.get(
							GetParameterTableDefine.PARAM_ID));
					info.setValue((String)tableLineData.get(
							GetParameterTableDefine.VALUE));
					info.setDescription((String)tableLineData.get(
							GetParameterTableDefine.DESCRIPTION));
				}

				//重複チェック
				Integer checkValue = (Integer) map.get(info.getParamId());
				if (checkValue == null) {
					m_paramList.add(info);
					map.put(info.getParamId(), new Integer(0));
				}
			}
		}

		return null;
	}

	/**
	 * 選択アイテムを返します。
	 *
	 * @return 選択アイテム
	 */
	public List<?> getSelectItem() {
		return m_selectItem;
	}

	/**
	 * 選択アイテムを設定します。
	 *
	 * @param selectItem 選択アイテム
	 */
	public void setSelectItem(List<?> selectItem) {
		m_selectItem = selectItem;
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		// super.setEnabled(enabled); // スクロールバーを動かせるように、ここはコメントアウト
		m_modifyCondition.setEnabled(enabled);
		m_createCondition.setEnabled(enabled);
		m_deleteCondition.setEnabled(enabled);
	}
}
