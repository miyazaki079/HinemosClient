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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.EndStatusConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.SizeConstant;
import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.composite.action.NumberVerifyListener;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.action.GetWaitRuleTableDefine;
import com.clustercontrol.jobmanagement.bean.ConditionTypeConstant;
import com.clustercontrol.jobmanagement.bean.JudgmentObjectConstant;
import com.clustercontrol.jobmanagement.composite.action.WaitRuleSelectionChangedListener;
import com.clustercontrol.jobmanagement.dialog.WaitRuleDialog;
import com.clustercontrol.util.Messages;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.jobmanagement.JobObjectInfo;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;
import com.clustercontrol.ws.jobmanagement.JobWaitRuleInfo;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 待ち条件タブ用のコンポジットクラスです。
 *
 * @version 2.1.0
 * @since 1.0.0
 */
public class WaitRuleComposite extends Composite {
	/** ログ出力のインスタンス */
	private static Log m_log = LogFactory.getLog( WaitRuleComposite.class );
	/** テーブルビューア */
	private CommonTableViewer m_viewer = null;
	/** 判定対象の条件関係 AND用ラジオボタン */
	private Button m_andCondition = null;
	/** 判定対象の条件関係 OR用ラジオボタン */
	private Button m_orCondition = null;
	/** 条件を満たさなければ終了する用チェックボタン */
	private Button m_endCondition = null;
	/** 条件を満たさなければ終了する用グループ */
	private Group m_endConditionGroup = null;
	/** 条件を満たさない時の終了状態用テキスト */
	private Combo m_endStatus = null;
	/** 条件を満たさない時の終了値用テキスト */
	private Text m_endValue = null;
	/** 追加用ボタン */
	private Button m_createCondition = null;
	/** 変更用ボタン */
	private Button m_modifyCondition = null;
	/** 削除用ボタン */
	private Button m_deleteCondition = null;
	/** ジョブ待ち条件情報 */
	private JobWaitRuleInfo m_waitRule = null;
	/** シェル */
	private Shell m_shell = null;
	/** 選択アイテム */
	private ArrayList<Object> m_selectItem = null;

	private JobTreeItem m_jobTreeItem = null;

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
	public WaitRuleComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		m_shell = this.getShell();
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

		Composite waitRuleListComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "list", waitRuleListComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		waitRuleListComposite.setLayout(rowLayout);

		Label tableTitle = new Label(waitRuleListComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "objectlist", tableTitle);
		tableTitle.setText(Messages.getString("object.list"));

		Table table = new Table(waitRuleListComposite, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
		WidgetTestUtil.setTestId(this, null, table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new RowData(430, 130));

		Composite waitRuleDummyComposite = new Composite(waitRuleListComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy", waitRuleDummyComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 5;
		waitRuleDummyComposite.setLayout(rowLayout);

		//判定対象の追加
		Label dummy1 = new Label(waitRuleDummyComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy1", dummy1);
		dummy1.setLayoutData(new RowData(190, SizeConstant.SIZE_LABEL_HEIGHT));
		m_createCondition = new Button(waitRuleDummyComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "createcondition", m_createCondition);
		m_createCondition.setText(Messages.getString("add"));
		m_createCondition.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_createCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_log.debug("widgetSelected");
				WaitRuleDialog dialog = new WaitRuleDialog(m_shell, m_jobTreeItem);
				if (dialog.open() == IDialogConstants.OK_ID) {

					// 待ち条件を追加するかどうかを示すフラグ
					boolean addWaitRule = true;

					ArrayList<Integer> info = (ArrayList<Integer>)dialog.getInputData();
					ArrayList<ArrayList<Integer>> list = (ArrayList<ArrayList<Integer>>) m_viewer.getInput();
					if (list == null) {
						list = new ArrayList<ArrayList<Integer>>();

					} else {

						//待ち条件として複数の時刻を設定しようとした場合はフラグをfalseとする
						int newWaitRuleType = (Integer) info.get(GetWaitRuleTableDefine.JUDGMENT_OBJECT);
						if (newWaitRuleType == JudgmentObjectConstant.TYPE_TIME){

							for (ArrayList<Integer> waitRule : list) {
								m_log.debug("WaitRuleComposite_initialize_info = " + info);
								int rule = waitRule.get(GetWaitRuleTableDefine.JUDGMENT_OBJECT);
								m_log.debug("WaitRuleComposite_initialize_rule = " + rule);
								if (rule == JudgmentObjectConstant.TYPE_TIME) {
									addWaitRule = false;

									MessageDialog.openWarning(
											null,
											Messages.getString("warning"),
											Messages.getString("message.job.61"));
								}
							}
						}
					}

					if (addWaitRule) list.add(info);
					m_viewer.setInput(list);
				}
			}
		});

		//判定対象の変更
		m_modifyCondition = new Button(waitRuleDummyComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "modifycondition", m_modifyCondition);
		m_modifyCondition.setText(Messages.getString("modify"));
		m_modifyCondition.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_modifyCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WaitRuleDialog dialog = new WaitRuleDialog(m_shell, m_jobTreeItem);
				if (m_selectItem instanceof ArrayList) {
					dialog.setInputData(m_selectItem);
					if (dialog.open() == IDialogConstants.OK_ID) {

						ArrayList<?> info = dialog.getInputData();
						ArrayList<ArrayList<?>> list = (ArrayList<ArrayList<?>>) m_viewer.getInput();

						list.remove(m_selectItem);
						list.add(info);

						m_selectItem = null;
						m_viewer.setInput(list);
					}
				} else {

				}
			}
		});

		//判定対象の削除
		m_deleteCondition = new Button(waitRuleDummyComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "deletecondition", m_deleteCondition);
		m_deleteCondition.setText(Messages.getString("delete"));
		m_deleteCondition.setLayoutData(new RowData(80,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_deleteCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ArrayList<?> list = (ArrayList<?>) m_viewer.getInput();
				list.remove(m_selectItem);
				m_selectItem = null;
				m_viewer.setInput(list);
			}
		});

		//判定対象の条件関係
		Group group = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, group);
		group.setText(Messages.getString("condition.between.objects"));
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		group.setLayout(rowLayout);

		Composite waitRuleConditionComposite = new Composite(group, SWT.NONE);
		WidgetTestUtil.setTestId(this, "condition", waitRuleConditionComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		waitRuleConditionComposite.setLayout(rowLayout);
		m_andCondition = new Button(waitRuleConditionComposite, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "andcondition", m_andCondition);
		m_andCondition.setText(Messages.getString("and"));
		m_andCondition.setLayoutData(new RowData(100,
				SizeConstant.SIZE_BUTTON_HEIGHT));
		m_orCondition = new Button(waitRuleConditionComposite, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "orcondition", m_orCondition);
		m_orCondition.setText(Messages.getString("or"));
		m_orCondition.setLayoutData(new RowData(100,
				SizeConstant.SIZE_BUTTON_HEIGHT));

		Composite waitRuleEndComposite = new Composite(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "end", waitRuleEndComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		waitRuleEndComposite.setLayout(rowLayout);

		//条件を満たさないときに終了
		Label dummy2 = new Label(waitRuleEndComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "dummy2", dummy2);
		dummy2.setLayoutData(new RowData(190, SizeConstant.SIZE_LABEL_HEIGHT));
		m_endCondition = new Button(waitRuleEndComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "endcondition", m_endCondition);
		m_endCondition.setText(Messages.getString("end.if.condition.unmatched"));
		m_endCondition.setLayoutData(new RowData(220, SizeConstant.SIZE_BUTTON_HEIGHT));
		m_endCondition.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button check = (Button) e.getSource();
				WidgetTestUtil.setTestId(this, null, check);
				if (check.getSelection()) {
					m_endConditionGroup.setEnabled(true);
					m_endStatus.setEnabled(true);
					m_endValue.setEnabled(true);
				} else {
					m_endConditionGroup.setEnabled(false);
					m_endStatus.setEnabled(false);
					m_endValue.setEnabled(false);
				}
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		m_endConditionGroup = new Group(this, SWT.NONE);
		WidgetTestUtil.setTestId(this, "endcondition", m_endConditionGroup);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		rowLayout.marginWidth = 5;
		rowLayout.marginHeight = 5;
		rowLayout.spacing = 1;
		m_endConditionGroup.setLayout(rowLayout);

		// 終了状態
		Composite waitRuleEndStatusComposite = new Composite(m_endConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "endstatus", waitRuleEndStatusComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		waitRuleEndStatusComposite.setLayout(rowLayout);
		Label endStatusTitle = new Label(waitRuleEndStatusComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "endstatus", endStatusTitle);
		endStatusTitle.setText(Messages.getString("end.status") + " : ");
		endStatusTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_endStatus = new Combo(waitRuleEndStatusComposite, SWT.CENTER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "endstatus", m_endStatus);
		m_endStatus.setLayoutData(new RowData(100,
				SizeConstant.SIZE_COMBO_HEIGHT));
		m_endStatus.add(EndStatusConstant.STRING_NORMAL);
		m_endStatus.add(EndStatusConstant.STRING_WARNING);
		m_endStatus.add(EndStatusConstant.STRING_ABNORMAL);

		// 終了値
		Composite waitRuleEndValueComposite = new Composite(m_endConditionGroup, SWT.NONE);
		WidgetTestUtil.setTestId(this, "endvalue", waitRuleEndValueComposite);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		rowLayout.spacing = 10;
		waitRuleEndValueComposite.setLayout(rowLayout);
		Label endValueTitle = new Label(waitRuleEndValueComposite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "endvalue", endValueTitle);
		endValueTitle.setText(Messages.getString("end.value") + " : ");
		endValueTitle.setLayoutData(new RowData(80,
				SizeConstant.SIZE_LABEL_HEIGHT));
		m_endValue = new Text(waitRuleEndValueComposite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "endvalue", m_endValue);
		m_endValue.setLayoutData(new RowData(100, SizeConstant.SIZE_TEXT_HEIGHT));
		m_endValue.addVerifyListener(
				new NumberVerifyListener(DataRangeConstant.SMALLINT_LOW, DataRangeConstant.SMALLINT_HIGH));
		m_endValue.addModifyListener(
				new ModifyListener(){
					@Override
					public void modifyText(ModifyEvent arg0) {
						update();
					}
				}
				);

		m_viewer = new CommonTableViewer(table);
		m_viewer.createTableColumn(GetWaitRuleTableDefine.get(),
				GetWaitRuleTableDefine.SORT_COLUMN_INDEX,
				GetWaitRuleTableDefine.SORT_ORDER);
		m_viewer
		.addSelectionChangedListener(new WaitRuleSelectionChangedListener(
				this));
	}

	@Override
	public void update() {
		if(m_endCondition.getSelection() && "".equals(this.m_endValue.getText())){
			this.m_endValue.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.m_endValue.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}

	/**
	 * ジョブ待ち条件情報をコンポジットに反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobWaitRuleInfo
	 */
	public void reflectWaitRuleInfo() {
		if (m_waitRule != null) {
			//判定対象と開始条件値設定
			List<JobObjectInfo> list = m_waitRule.getObject();
			m_log.debug("reflectWaitRuleInfo_JobObjectInfo.size() = " + list.size());
			if(list != null){
				ArrayList<Object> tableData = new ArrayList<Object>();
				for (int i = 0; i < list.size(); i++) {
					m_log.debug("loop count = " + i);
					JobObjectInfo info = list.get(i);
					ArrayList<Object> tableLineData = new ArrayList<Object>();
					tableLineData.add(new Integer(info.getType()));
					if (info.getType() == JudgmentObjectConstant.TYPE_JOB_END_STATUS) {
						tableLineData.add(info.getJobId());
						tableLineData.add(EndStatusConstant.typeToString(info.getValue()));
						tableData.add(tableLineData);
					}
					else if (info.getType() == JudgmentObjectConstant.TYPE_JOB_END_VALUE) {
						tableLineData.add(info.getJobId());
						tableLineData.add(new Integer(info.getValue()));
						tableData.add(tableLineData);
					}
					else if (info.getType() == JudgmentObjectConstant.TYPE_TIME) {
						if (info.getTime() != null) {
							tableLineData.add("");
							tableLineData.add(new Date(info.getTime()));
							tableData.add(tableLineData);
						} else {
						}
					}
					else if (info.getType() == JudgmentObjectConstant.TYPE_START_MINUTE) {
						m_log.debug("reflectWaitRuleInfo_JobObjectInfo of line is  DELAY");
						if (info.getValue() != null) {
							tableLineData.add("");
							tableLineData.add(new Integer(info.getStartMinute()));
							tableData.add(tableLineData);
						} else {
						}
					}
				}
				m_log.debug("reflectWaitRuleInfo_tableData.size() = " + tableData.size());
				m_viewer.setInput(tableData);
			}

			//条件関係設定
			if (m_waitRule.getCondition() == ConditionTypeConstant.TYPE_AND) {
				m_andCondition.setSelection(true);
				m_orCondition.setSelection(false);
			} else {
				m_andCondition.setSelection(false);
				m_orCondition.setSelection(true);
			}

			//開始条件を満たさないとき終了 設定
			m_endCondition.setSelection(YesNoConstant.typeToBoolean(m_waitRule
					.getEndCondition()));

			//終了状態
			setSelectEndStatus(m_endStatus, m_waitRule.getEndStatus());

			//終了値
			m_endValue.setText(String.valueOf(m_waitRule.getEndValue()));
		}

		//開始条件を満たさないとき終了
		if (m_endCondition.getSelection()) {
			m_endConditionGroup.setEnabled(true);
			m_endStatus.setEnabled(true);
			m_endValue.setEnabled(true);
		} else {
			m_endConditionGroup.setEnabled(false);
			m_endStatus.setEnabled(false);
			m_endValue.setEnabled(false);
		}
	}

	/**
	 * ジョブ待ち条件情報を設定します。
	 *
	 * @param start ジョブ待ち条件情報
	 */
	public void setWaitRuleInfo(JobWaitRuleInfo start) {
		m_waitRule = start;
	}

	/**
	 * ジョブ待ち条件情報を返します。
	 *
	 * @return ジョブ待ち条件情報
	 */
	public JobWaitRuleInfo getWaitRuleInfo() {
		return m_waitRule;
	}

	public static JobObjectInfo array2JobObjectInfo(ArrayList<?> tableLineData) {
		Integer type = (Integer) tableLineData.get(GetWaitRuleTableDefine.JUDGMENT_OBJECT);
		JobObjectInfo info = new JobObjectInfo();
		info.setValue(0);
		info.setType(type);
		if (info.getType() == JudgmentObjectConstant.TYPE_JOB_END_STATUS) {
			info.setJobId((String) tableLineData
					.get(GetWaitRuleTableDefine.JOB_ID));
			String value = (String) tableLineData
					.get(GetWaitRuleTableDefine.START_VALUE);
			info.setValue(EndStatusConstant.stringToType(value));
		}
		else if (info.getType() == JudgmentObjectConstant.TYPE_JOB_END_VALUE) {
			info.setJobId((String) tableLineData
					.get(GetWaitRuleTableDefine.JOB_ID));
			Integer value = (Integer) tableLineData
					.get(GetWaitRuleTableDefine.START_VALUE);
			info.setValue(value);
		}
		else if (info.getType() == JudgmentObjectConstant.TYPE_TIME) {
			Date value = (Date) tableLineData
					.get(GetWaitRuleTableDefine.START_VALUE);
			info.setTime(value.getTime());
		}
		else if (info.getType() == JudgmentObjectConstant.TYPE_START_MINUTE) {
			Integer startMinute = (Integer) tableLineData
					.get(GetWaitRuleTableDefine.START_VALUE);
			info.setStartMinute(startMinute);
		}
		return info;
	}

	/**
	 * コンポジットの情報から、ジョブ待ち条件情報を作成します。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.JobWaitRuleInfo
	 */
	public ValidateResult createWaitRuleInfo() {
		m_log.debug("createWaitRuleInfo");
		ValidateResult result = null;

		//判定対象と開始条件値取得
		ArrayList<JobObjectInfo> list = new ArrayList<JobObjectInfo>();
		ArrayList<?> tableData = (ArrayList<?>) m_viewer.getInput();
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		for (int i = 0; i < tableData.size(); i++) {
			ArrayList<?> tableLineData = (ArrayList<?>) tableData.get(i);
			JobObjectInfo info = array2JobObjectInfo(tableLineData);
			// 重複チェックをしてから、リストに追加する。
			if (info.getType() == JudgmentObjectConstant.TYPE_JOB_END_STATUS) {
				Integer checkValue = map.get(info.getJobId() + info.getType());
				if (checkValue == null
						|| !checkValue.equals(info.getValue())) {
					list.add(info);
					map.put(info.getJobId() + info.getType(), new Integer(info.getValue()));
				}
			}
			else if (info.getType() == JudgmentObjectConstant.TYPE_JOB_END_VALUE) {
				Integer checkValue = map.get(info.getJobId() + info.getType());
				if (checkValue == null
						|| checkValue != info.getValue()) {
					list.add(info);
					map.put(info.getJobId() + info.getType(), new Integer(info.getValue()));
				}
			}
			else if (info.getType() == JudgmentObjectConstant.TYPE_TIME) {
				if (map.get("TIME") == null) {
					list.add(info);
					map.put("TIME", 1);
				}
			}
			else if (info.getType() == JudgmentObjectConstant.TYPE_START_MINUTE) {
				m_log.debug("info.getType="  + info.getType());
				m_log.debug("info.getStartMinute="  + info.getStartMinute());
				if (map.get(info.getType().toString()) == null) {
					list.add(info);
					map.put(info.getType().toString(), new Integer(info.getValue()));
				}
			}
		}
		List<JobObjectInfo> jobObjectInfoList = m_waitRule.getObject();
		jobObjectInfoList.clear();
		jobObjectInfoList.addAll(list);

		//条件関係取得
		if (m_andCondition.getSelection()) {
			m_waitRule.setCondition(ConditionTypeConstant.TYPE_AND);
		} else {
			m_waitRule.setCondition(ConditionTypeConstant.TYPE_OR);
		}

		//開始条件を満たさないとき終了 設定
		m_waitRule.setEndCondition(YesNoConstant.booleanToType(m_endCondition
				.getSelection()));

		//終了状態、終了値
		try {
			m_waitRule.setEndStatus(getSelectEndStatus(m_endStatus));
			m_waitRule.setEndValue(Integer.parseInt(m_endValue.getText()));
		} catch (NumberFormatException e) {
			if (m_waitRule.getEndCondition() == YesNoConstant.TYPE_YES) {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.21"));
				return result;
			}
		}

		return null;
	}

	/**
	 * 選択アイテムを設定します。
	 *
	 * @param selectItem 選択アイテム
	 */
	public void setSelectItem(ArrayList<Object> selectItem) {
		m_selectItem = selectItem;
	}

	public void setJobTreeItem(JobTreeItem jobTreeItem) {
		m_jobTreeItem = jobTreeItem;
	}

	/**
	 * 指定した重要度に該当するカレンダ終了状態用コンボボックスの項目を選択します。
	 *
	 */
	private void setSelectEndStatus(Combo combo, int status) {
		String select = "";

		select = EndStatusConstant.typeToString(status);

		combo.select(0);
		for (int i = 0; i < combo.getItemCount(); i++) {
			if (select.equals(combo.getItem(i))) {
				combo.select(i);
				break;
			}
		}
	}

	/**
	 * カレンダ終了状態用コンボボックスにて選択している項目を取得します。
	 *
	 */
	private int getSelectEndStatus(Combo combo) {
		String select = combo.getText();
		return EndStatusConstant.stringToType(select);
	}

	/**
	 * 読み込み専用時にグレーアウトします。
	 */
	@Override
	public void setEnabled(boolean enabled) {
		// super.setEnabled(enabled); // スクロールバーを動かせるように、ここはコメントアウト
		m_andCondition.setEnabled(enabled);
		m_orCondition.setEnabled(enabled);
		m_endCondition.setEnabled(enabled);
		m_endConditionGroup.setEnabled(enabled);
		m_endStatus.setEnabled(enabled);
		m_endValue.setEnabled(enabled);
		m_createCondition.setEnabled(enabled);
		m_modifyCondition.setEnabled(enabled);
		m_deleteCondition.setEnabled(enabled);
	}
}
