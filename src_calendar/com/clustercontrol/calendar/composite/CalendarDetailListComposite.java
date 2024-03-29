/*

 Copyright (C) 2013 NTT DATA Corporation

 This program is free software; you can redistribute it and/or
 Modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation, version 2.

 This program is distributed in the hope that it will be
 useful, but WITHOUT ANY WARRANTY; without even the implied
 warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.calendar.composite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.bean.DayOfWeekConstant;
import com.clustercontrol.calendar.action.GetCalendarDetailTableDefine;
import com.clustercontrol.calendar.bean.DayOfWeekInMonthConstant;
import com.clustercontrol.calendar.bean.MonthConstant;
import com.clustercontrol.calendar.bean.OperateConstant;
import com.clustercontrol.calendar.dialog.CalendarDetailDialog;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.viewer.CommonTableViewer;
import com.clustercontrol.ws.calendar.CalendarDetailInfo;

/**
 * カレンダ詳細情報一覧コンポジットクラス<BR>
 *
 * @version 4.1.0
 * @since 4.1.0
 */
public class CalendarDetailListComposite extends Composite {

	/** テーブルビューアー。 */
	private CommonTableViewer m_tableViewer = null;
	/** カレンダ詳細情報一覧 */
	private ArrayList<CalendarDetailInfo> detailList = null;
	/** オーナーロールID */
	private String m_ownerRoleId = null;
	/** マネージャ名 */
	private String m_managerName = null;

	/**
	 * @return the m_managerName
	 */
	public String getManagerName() {
		return m_managerName;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<CalendarDetailInfo> getDetailList(){
		return this.detailList;
	}
	/**
	 *
	 * @return
	 */
	public String getOwnerRoleId() {
		return m_ownerRoleId;
	}
	/**
	 *
	 * @param ownerRoleId
	 */
	public void setOwnerRoleId(String ownerRoleId) {
		this.m_ownerRoleId = ownerRoleId;
	}

	/**
	 * インスタンスを返します。
	 * <p>
	 * 初期処理を呼び出し、コンポジットを配置します。
	 *
	 * @param parent 親のコンポジット
	 * @param style スタイル
	 * @param managerName マネージャ名
	 *
	 * @see org.eclipse.swt.SWT
	 * @see org.eclipse.swt.widgets.Composite#Composite(Composite parent, int style)
	 * @see #initialize()
	 */
	public CalendarDetailListComposite(Composite parent, int style, String managerName) {
		super(parent, style);
		this.m_managerName = managerName;
		this.initialize();
	}

	/**
	 * テーブル選択項目のの優先度を上げる
	 */
	public void up() {
		StructuredSelection selection = (StructuredSelection) m_tableViewer.getSelection();//.firstElement;
		ArrayList<?> list =  (ArrayList<?>) selection.getFirstElement();
		//選択したテーブル行番号を取得
		Integer order = (Integer) list.get(0);
		List<CalendarDetailInfo> detailList = this.detailList;

		//orderは、テーブルカラム番号のため、1 ～ n listから値を取得する際は、 order - 1
		order = order-1;
		if(order > 0){
			CalendarDetailInfo a = detailList.get(order);
			CalendarDetailInfo b = detailList.get(order-1);
			detailList.set(order, b);
			detailList.set(order-1, a);
		}
		update();
		//更新後に再度選択項目にフォーカスをあてる
		selectItem(order - 1);
	}
	/**
	 * テーブル選択項目の優先度を下げる
	 */
	public void down() {
		StructuredSelection selection = (StructuredSelection) m_tableViewer.getSelection();//.firstElement;
		ArrayList<?> list =  (ArrayList<?>) selection.getFirstElement();
		//選択したテーブル行番号を取得
		Integer order = (Integer) list.get(0);
		List<CalendarDetailInfo> detailList = this.detailList;
		//list内 order+1 の値を取得するため、
		if(order < detailList.size()){
			//orderは、テーブルカラム番号のため、1 ～ n listから値を取得する際は、 order - 1
			order = order - 1;
			CalendarDetailInfo a = detailList.get(order);
			CalendarDetailInfo b = detailList.get(order + 1);
			detailList.set(order, b);
			detailList.set(order+1, a);
		}
		update();
		//更新後に再度選択項目にフォーカスをあてる
		selectItem(order + 1);
	}
	/**
	 * 引数で指定された判定情報の行を選択状態にします。
	 *
	 * @param identifier 識別キー
	 */
	private void selectItem(Integer order) {
		Table calDetailListSelectItemTable = m_tableViewer.getTable();
		TableItem[] items = calDetailListSelectItemTable.getItems();

		if (items == null || order == null) {
			return;
		}
		calDetailListSelectItemTable.select(order);
		return;
	}
	/**
	 * コンポジットを配置します。
	 */
	private void initialize() {
		/*
		 * カレンダ詳細初期化
		 */
		//this.detailList = new ArrayList<CalendarDetailInfo>();

		GridLayout layout = new GridLayout(1, true);
		this.setLayout(layout);
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Table calDetialListTable = new Table(this, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		WidgetTestUtil.setTestId(this, null, calDetialListTable);
		calDetialListTable.setHeaderVisible(true);
		calDetialListTable.setLinesVisible(true);

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		calDetialListTable.setLayoutData(gridData);

		// テーブルビューアの作成
		m_tableViewer = new CommonTableViewer(calDetialListTable);
		m_tableViewer.createTableColumn(GetCalendarDetailTableDefine.get(),
				GetCalendarDetailTableDefine.SORT_COLUMN_INDEX,
				GetCalendarDetailTableDefine.SORT_ORDER);
		m_tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Integer order = getSelection();
				List<CalendarDetailInfo> detailList = getDetailList();
				if (order != null) {
					// シェルを取得
					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

					//FIXME
					//CalendarDetailDialog dialog = new CalendarDetailDialog(shell,detailList.get(order - 1), m_calendarInfo.getOwnerRoleId());
					CalendarDetailDialog dialog = new CalendarDetailDialog(shell, m_managerName, detailList.get(order - 1), m_ownerRoleId);
					if (dialog.open() == IDialogConstants.OK_ID) {
						detailList.remove(order - 1);
						detailList.add(order - 1,dialog.getInputData());
						setSelection();
					}
				}
			}
		});
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
	}
	/**
	 * 選択したテーブル行番号を返す。
	 *
	 */
	public Integer getSelection() {
		StructuredSelection selection = (StructuredSelection) m_tableViewer.getSelection();
		if (selection.getFirstElement() instanceof ArrayList) {
			ArrayList<?> list = (ArrayList<?>)selection.getFirstElement();
			if (list.get(0) instanceof Integer) {
				return (Integer)list.get(0);
			}
		}
		return null;
	}

	public void setSelection() {
		Table calDetailListSetSelectionTable = m_tableViewer.getTable();
		WidgetTestUtil.setTestId(this, null, calDetailListSetSelectionTable);
		int selectIndex = calDetailListSetSelectionTable.getSelectionIndex();
		update();
		calDetailListSetSelectionTable.setSelection(selectIndex);
	}
	/**
	 * 現在選択されているアイテムを返します。
	 * <p>
	 * 選択されていない場合は、<code>null</code>を返します。
	 *
	 * @return 選択アイテム
	 */
	public CalendarDetailInfo getFilterItem() {
		StructuredSelection selection = (StructuredSelection) m_tableViewer.getSelection();

		if (selection == null) {
			return null;
		} else {
			return (CalendarDetailInfo) selection.getFirstElement();
		}
	}

	/**
	 * 引数で指定されたカレンダ詳細情報をコンポジット内リストに反映させる
	 * @param detailList
	 */
	public void setDetailList(ArrayList<CalendarDetailInfo> detailList){
		if (detailList != null) {
			this.detailList = detailList;
			this.update();
		}
	}
	/**
	 * コンポジットを更新します。<BR>
	 * カレンダ詳細情報一覧を取得し、テーブルビューアーにセットします。
	 */
	@Override
	public void update() {
		// テーブル更新
		ArrayList<Object> listAll = new ArrayList<Object>();
		int i = 1;
		for (CalendarDetailInfo detail : getDetailList()) {
			ArrayList<Object> list = new ArrayList<Object>();
			String ruleMonthDay = "";
			//規則（日程）表示項目設定
			if(detail.getYear() != null){
				if(detail.getYear() == 0){
					ruleMonthDay = Messages.getString("calendar.detail.every.year");
				}else{
					ruleMonthDay = detail.getYear() + Messages.getString("year");
				}
			}
			if(detail.getMonth() != null && detail.getMonth() >= 0){
				ruleMonthDay = ruleMonthDay + MonthConstant.typeToString(detail.getMonth());
			}
			switch(detail.getDayType()){
			case 0:
				ruleMonthDay = ruleMonthDay + Messages.getString("calendar.detail.everyday");
				break;
			case 1:
				if(detail.getDayOfWeekInMonth() != null && detail.getDayOfWeekInMonth() >= 0){
					ruleMonthDay = ruleMonthDay + " " + DayOfWeekInMonthConstant.typeToString(detail.getDayOfWeekInMonth());
				}
				if(detail.getDayOfWeek() != null && detail.getDayOfWeek() > 0){
					ruleMonthDay = ruleMonthDay + DayOfWeekConstant.typeToString(detail.getDayOfWeek());
				}
				break;
			case 2:
				if(detail.getDate() != null && detail.getDate() > 0){
					ruleMonthDay = ruleMonthDay + " " + detail.getDate() + Messages.getString("monthday");
				}
				break;
			case 3:
				if(detail.getCalPatternId() != null && detail.getCalPatternId().length() > 0){
					ruleMonthDay = ruleMonthDay + " " + detail.getCalPatternId();
				}
				break;
			}
			if(detail.getAfterday() != null && detail.getAfterday() != 0){
				if(detail.getAfterday() > 0){
					ruleMonthDay = ruleMonthDay + " " + detail.getAfterday() + Messages.getString("calendar.detail.after.2");
				}else if (detail.getAfterday() < 0){
					ruleMonthDay = ruleMonthDay + " " + Math.abs(detail.getAfterday()) + Messages.getString("calendar.detail.after.3");
				}
			}
			//規則（時間）表示項目設定
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			SimpleDateFormat sdfHH = new SimpleDateFormat("HH");
			SimpleDateFormat sdfMmSs = new SimpleDateFormat("mm:ss");
			String strHour24 = "1970/01/02 00:00:00";
			String strHour48 = "1970/01/03 00:00:00";

			Date date = null;
			Date date2 = null;
			try {
				date = sdfYmd.parse(strHour24);
				date2 = sdfYmd.parse(strHour48);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String strFrom = "";
			String strTo = "";
			long hour24 = date.getTime();
			long hour48 = date2.getTime();
			if(detail.getTimeFrom() != null){
				if(hour48 <= detail.getTimeFrom()){
					String strHH = sdfHH.format(detail.getTimeFrom());
					Integer hh = Integer.parseInt(strHH);
					hh = hh + 48;
					strFrom = String.valueOf(hh) + ":" + sdfMmSs.format(detail.getTimeFrom());

				}else if(hour24 <= detail.getTimeFrom()){
					String strHH = sdfHH.format(detail.getTimeFrom());
					Integer hh = Integer.parseInt(strHH);
					hh = hh + 24;
					strFrom = String.valueOf(hh) + ":" + sdfMmSs.format(detail.getTimeFrom());

				}else {
					//開始時間
					strFrom = sdf.format(detail.getTimeFrom());
				}
			}
			if(detail.getTimeTo() != null){
				if(hour48 <= detail.getTimeTo()){
					String strHH = sdfHH.format(detail.getTimeTo());
					Integer hh = Integer.parseInt(strHH);
					hh = hh + 48;
					strTo = String.valueOf(hh) + ":" + sdfMmSs.format(detail.getTimeTo());

				}else if(hour24 <= detail.getTimeTo()){
					String strHH = sdfHH.format(detail.getTimeTo());
					Integer hh = Integer.parseInt(strHH);
					hh = hh + 24;
					strTo = String.valueOf(hh) + ":" + sdfMmSs.format(detail.getTimeTo());

				}else {
					//終了時間
					strTo = sdf.format(detail.getTimeTo());
				}
			}
			String ruleTime = "";
			ruleTime = strFrom + " - " + strTo;

			//順序
			list.add(i);
			//規則（日程）
			list.add(ruleMonthDay);
			//規則（時間）
			list.add(ruleTime);

			//稼動・非稼動
			if (detail.isOperateFlg() != null) {
				if(detail.isOperateFlg()){
					list.add(OperateConstant.typeToString(1));
				}else {
					list.add(OperateConstant.typeToString(0));
				}
			}
			list.add(detail.getDescription());
			listAll.add(list);
			i++;
		}
		m_tableViewer.setInput(listAll);
	}

	/* (非 Javadoc)
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.m_tableViewer.getTable().setEnabled(enabled);
	}
}
