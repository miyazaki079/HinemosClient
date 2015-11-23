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

package com.clustercontrol.viewer;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.clustercontrol.bean.CheckBoxImageConstant;
import com.clustercontrol.bean.DayOfWeekConstant;
import com.clustercontrol.bean.EndStatusConstant;
import com.clustercontrol.bean.EndStatusImageConstant;
import com.clustercontrol.bean.FacilityImageConstant;
import com.clustercontrol.bean.JobImageConstant;
import com.clustercontrol.bean.PerformanceStatusImageConstant;
import com.clustercontrol.bean.PriorityColorConstant;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.ProcessConstant;
import com.clustercontrol.bean.ScheduleConstant;
import com.clustercontrol.bean.StatusConstant;
import com.clustercontrol.bean.TableColumnInfo;
import com.clustercontrol.bean.ValidConstant;
import com.clustercontrol.bean.YesNoConstant;
import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.bean.JobParamTypeConstant;
import com.clustercontrol.jobmanagement.bean.JudgmentObjectConstant;
import com.clustercontrol.jobmanagement.bean.ScheduleOnOffImageConstant;
import com.clustercontrol.jobmanagement.bean.StatusImageConstant;
import com.clustercontrol.monitor.bean.ConfirmConstant;
import com.clustercontrol.notify.util.NotifyTypeUtil;
import com.clustercontrol.performance.bean.PerformanceStatusConstant;
import com.clustercontrol.repository.bean.FacilityConstant;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.TimeTo48hConverter;
import com.clustercontrol.ws.common.Schedule;

/**
 * CommonTableViewerクラス用のLabelProviderクラス<BR>
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommonTableLabelProvider extends LabelProvider implements ICommonTableLabelProvider {
	// ログ
	private static Log m_log = LogFactory.getLog( CommonTableLabelProvider.class );

	private CommonTableViewer m_viewer;

	/**
	 * コンストラクタ
	 *
	 * アイコンイメージを取得
	 *
	 * @param viewer
	 * @since 1.0.0
	 */
	public CommonTableLabelProvider(CommonTableViewer viewer) {
		m_viewer = viewer;
	}

	/**
	 * カラム文字列取得処理
	 *
	 * @since 1.0.0
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		ArrayList<TableColumnInfo> tableColumnList = m_viewer.getTableColumnList();

		ArrayList<?> list = (ArrayList<?>) element;
		if (list.size() <= columnIndex) {
			m_log.debug("Bad implements. IndexOutOfBoundsException."
					+ " list.size=" + list.size() +
					", columnIndex=" + columnIndex);
			return "";
		}
		Object item = list.get(columnIndex);

		TableColumnInfo tableColumn = tableColumnList.get(columnIndex);

		if (item == null || item.equals("")) {
			return "";
		}

		if (tableColumn.getType() == TableColumnInfo.JOB) {
			//データタイプが「ジョブ」の処理
			return JobConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.STATE) {
			//データタイプが「状態」の処理
			return StatusConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.PRIORITY) {
			//データタイプが「重要度」の処理
			return PriorityConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.VALID) {
			//データタイプが「有効/無効」の処理
			return ValidConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.JUDGMENT_OBJECT) {
			//データタイプが「判定対象」の処理
			return JudgmentObjectConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.NOTIFY_TYPE) {
			//データタイプが「判定対象」の処理
			return NotifyTypeUtil.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.WAIT_RULE_VALUE) {
			//データタイプが「開始条件値」の処理
			Class<?> itemClass = item.getClass();
			if (itemClass == Date.class) {
				//表示形式を48:00まで対応
				return TimeTo48hConverter.dateTo48hms((Date) item);
			} else if (itemClass == String.class) {
				return String.valueOf(item);
			} else if (itemClass.getSuperclass() == Number.class) {
				return ((Number) item).toString();
			}
		} else if (tableColumn.getType() == TableColumnInfo.SCHEDULE) {
			//データタイプが「スケジュール」の処理
			Schedule schedule = (Schedule) item;
			String scheduleString = null;
			DecimalFormat format = new DecimalFormat("00");
			if (schedule.getType() == ScheduleConstant.TYPE_DAY) {
				if (schedule.getMonth() != null) {
					scheduleString = format.format(schedule.getMonth()) +
							"/" + format.format(schedule.getDay()) + " " +
							format.format(schedule.getHour()) + ":" +
							format.format(schedule.getMinute());
				} else if (schedule.getDay() != null){
					scheduleString = format.format(schedule.getDay()) +
							Messages.getString("monthday") + " " +
							format.format(schedule.getHour()) + ":" +
							format.format(schedule.getMinute());
				} else if (schedule.getHour() != null) {
					scheduleString = format.format(schedule.getHour()) + ":" +
							format.format(schedule.getMinute());
				} else if (schedule.getMinute() != null) {
					scheduleString = format.format(schedule.getMinute()) +
							Messages.getString("minute");
				}
			} else if (schedule.getType() == ScheduleConstant.TYPE_WEEK){
				if (schedule.getHour() != null) {
					scheduleString = DayOfWeekConstant.typeToString(schedule.getWeek()) +
							" " + format.format(schedule.getHour()) + ":" +
							format.format(schedule.getMinute());
				} else {
					scheduleString = DayOfWeekConstant.typeToString(schedule.getWeek()) +
							" " + format.format(schedule.getMinute()) + Messages.getString("minute");
				}
			} else {
				// ここは通らないはず。
				m_log.warn("CommonTableLabelProvider 165");
			}
			return scheduleString;
		} else if (tableColumn.getType() == TableColumnInfo.CONFIRM) {
			//データタイプが「確認/未確認」の処理
			return ConfirmConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.WAIT_RULE) {
			//データタイプが「待ち条件」の処理
			return YesNoConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.PROCESS) {
			//データタイプが「処理」の処理
			return ProcessConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.END_STATUS) {
			//データタイプが「終了状態」の処理
			return EndStatusConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.CHECKBOX) {
			//データタイプが「チェックボックス」の処理
			return "";
		} else if (tableColumn.getType() == TableColumnInfo.DAY_OF_WEEK) {
			//データタイプが「曜日」の処理
			return DayOfWeekConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.SCHEDULE_ON_OFF) {
			//データタイプが「予定」の処理
			return "";
		} else if (tableColumn.getType() == TableColumnInfo.JOB_PARAM_TYPE) {
			//データタイプが「ジョブパラメータ種別」の処理
			return JobParamTypeConstant.typeToString(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.COLLECT_STATUS) {
			//データタイプが「収集状態」の処理
			return PerformanceStatusConstant.typeToString(((Number) item).intValue());
		}  else {
			//上記以外のデータタイプの処理
			Class<?> itemClass = item.getClass();

			if (itemClass == String.class) {
				return String.valueOf(item);
			} else if (itemClass == Date.class) {
				return DateFormat.getDateTimeInstance().format((Date) item);
			} else if (itemClass == Time.class) {
				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				return formatter.format((Time) item);
			} else if (itemClass.getSuperclass() == Number.class) {
				return ((Number) item).toString();
			} else if (itemClass.isEnum()) {
				return ((Enum<?>) item).toString();
			} else {
				return item.toString();
			}
		}
		return "";
	}

	/**
	 * カラムイメージ(アイコン)取得処理
	 *
	 * @since 1.0.0
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		ArrayList<TableColumnInfo> tableColumnList = m_viewer.getTableColumnList();

		ArrayList<?> list = (ArrayList<?>) element;
		if (list.size() <= columnIndex) {
			m_log.debug("Bad implements. IndexOutOfBoundsException");
			return null;
		}
		Object item = list.get(columnIndex);

		TableColumnInfo tableColumn = tableColumnList.get(columnIndex);

		if (item == null || item.equals("")) {
			return null;
		}

		if (tableColumn.getType() == TableColumnInfo.JOB) {
			//データタイプが「ジョブ」の処理
			return JobImageConstant.typeToImage(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.FACILITY) {
			//データタイプが「ファシリティ」の処理
			// TODO スコープとノードの判別方法は要検討！
			Pattern p = Pattern.compile(".*>");
			Matcher m = p.matcher((String) item);
			if (m.matches()) {
				return FacilityImageConstant
						.typeToImage(FacilityConstant.TYPE_SCOPE);
			} else {
				return FacilityImageConstant
						.typeToImage(FacilityConstant.TYPE_NODE);
			}

		} else if (tableColumn.getType() == TableColumnInfo.STATE) {
			//データタイプが「状態」の処理
			return StatusImageConstant.typeToImage(((Number) item).intValue());
		} else if (tableColumn.getType() == TableColumnInfo.END_STATUS) {
			//データタイプが「終了状態」の処理
			return EndStatusImageConstant.typeToImage(((Number) item)
					.intValue());
		} else if (tableColumn.getType() == TableColumnInfo.CHECKBOX) {
			//データタイプが「チェックボックス」の処理
			return CheckBoxImageConstant.typeToImage(((Boolean) item)
					.booleanValue());
		} else if (tableColumn.getType() == TableColumnInfo.SCHEDULE_ON_OFF) {
			//データタイプが「予定」の処理
			return ScheduleOnOffImageConstant.dateToImage(new Date((Long)item));
		} else if (tableColumn.getType() == TableColumnInfo.COLLECT_STATUS) {
			//データタイプが「収集状態」の処理
			return PerformanceStatusImageConstant.typeToImage(((Number) item).intValue());
		}

		return null;
	}

	/**
	 * カラムカラー取得処理
	 *
	 * @since 1.0.0
	 * @see com.clustercontrol.viewer.ICommonTableLabelProvider#getColumnColor(java.lang.Object,
	 *      int)
	 */
	@Override
	public Color getColumnColor(Object element, int columnIndex) {
		ArrayList<TableColumnInfo> tableColumnList = m_viewer.getTableColumnList();

		ArrayList<?> list = (ArrayList<?>) element;
		if (list.size() <= columnIndex) {
			m_log.debug("Bad implements. IndexOutOfBoundsException");
			return null;
		}
		Object item = list.get(columnIndex);

		TableColumnInfo tableColumn = tableColumnList.get(columnIndex);

		if (item == null) {
			return null;
		}

		if (tableColumn.getType() == TableColumnInfo.PRIORITY) {
			//データタイプが「重要度」の処理
			return PriorityColorConstant.typeToColor(((Number) item).intValue());
		}

		return null;
	}
}
