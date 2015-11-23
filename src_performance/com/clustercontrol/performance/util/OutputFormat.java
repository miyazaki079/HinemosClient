/*

 Copyright (C) 2006 NTT DATA Corporation

 This program is free software; you can redistribute it and/or
 Modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation, version 2.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 */

package com.clustercontrol.performance.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.clustercontrol.util.Messages;

/**
 * エクスポートのフォーマットを規定するクラス
 * 
 * @version 1.0
 * @since 1.0
 */
public class OutputFormat {

	/**
	 * date型を画面表示用の文字列に変換します。
	 * @param date
	 * @return
	 */
	public static String dateToString(Date date) {
		DateFormat df = new SimpleDateFormat(Messages.getString("FMT_OUTPUT"));
		return df.format(date);
	}

	/**
	 * ミリ秒を画面表示用の文字列に変換します。
	 * @param miliSecond
	 * @return
	 */
	public static String timeToString(long miliSecond) {
		NumberFormat timenf = new DecimalFormat("00");
		NumberFormat daynf = new DecimalFormat("000");
		long second = (miliSecond / 1000) % 60;
		long minute = (miliSecond / 1000) / 60 % 60;
		long hour = (miliSecond / 1000) / (60 * 60) % 24;
		long day = (miliSecond / 1000) / (60 * 60 * 24);

		String str = daynf.format(day) + " " + timenf.format(hour) + ":"
				+ timenf.format(minute) + ":" + timenf.format(second);

		return str;
	}

	/**
	 * double型を画面表示用の文字列に変換します。
	 * @param value
	 * @return
	 */
	public static String doubleToString(double value) {
		NumberFormat nf = new DecimalFormat(Messages.getString("FMT_1"));
		return nf.format(value);
	}
}
