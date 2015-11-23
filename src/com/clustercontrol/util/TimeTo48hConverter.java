/*

Copyright (C) 2012 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */
package com.clustercontrol.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 
 * 00:00:00 - 48:00:00表記に対応させるクラス
 *
 */
public class TimeTo48hConverter {
	/**
	 * 表示形式を48:00:00まで対応
	 * <br>上記以上のDateは強制的に48:00:00(1970/01/03 00:00:00)に変換される</br>
	 * 
	 * @param dateValue
	 * @return "" or 00:00:00 - 48:00:00
	 */
	public static String dateTo48hms(Date dateValue){
		String strDate = "";
		//表示項目設定
		String strHour24 = "1970/01/02 00:00:00";
		String strHour48 = "1970/01/03 00:00:00";
		Date date24 = null;
		Date date48 = null;
		try {
			SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			date24 = sdfYmd.parse(strHour24);
			date48 = sdfYmd.parse(strHour48);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long hour24 = date24.getTime();
		long hour48 = date48.getTime();
		SimpleDateFormat sdfHH = new SimpleDateFormat("HH");
		SimpleDateFormat sdfMmSs = new SimpleDateFormat("mm:ss");
		if(dateValue != null){
			/*
			1970/01/03 00:00:00を超える場合は、
			強制的に1970/01/03 00:00:00に変換（DB登録時も）
			 */
			if(hour48 < dateValue.getTime()){
				dateValue.setTime(hour48);
			}
			if(hour48 == dateValue.getTime()){
				String strHH = sdfHH.format(dateValue);
				Integer hh = Integer.parseInt(strHH);
				hh = hh + 48;
				strDate = String.valueOf(hh) + ":" + sdfMmSs.format(dateValue);
			}else if(hour24 <= dateValue.getTime()){
				String strHH = sdfHH.format(dateValue);
				Integer hh = Integer.parseInt(strHH);
				hh = hh + 24;
				strDate = String.valueOf(hh) + ":" + sdfMmSs.format(dateValue);
			}else {
				//開始時間
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				strDate = sdf.format(dateValue);
			}
		}
		return strDate;
	}
	/**
	 * 表示形式を48:00まで対応
	 * 上記以上のDateは強制的に48:00(1970/01/03 00:00:00)に変換される
	 * 
	 * @param dateValue
	 * @return "" or 00:00 - 48:00
	 */
	public static String dateTo48hm(Date dateValue){
		String strDate = "";
		//表示項目設定
		String strHour24 = "1970/01/02 00:00:00";
		String strHour48 = "1970/01/03 00:00:00";
		Date date24 = null;
		Date date48 = null;
		try {
			SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			date24 = sdfYmd.parse(strHour24);
			date48 = sdfYmd.parse(strHour48);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long hour24 = date24.getTime();
		long hour48 = date48.getTime();
		SimpleDateFormat sdfHH = new SimpleDateFormat("HH");
		SimpleDateFormat sdfMmSs = new SimpleDateFormat("mm");
		if(dateValue != null){
			/*
			1970/01/03 00:00:00を超える場合は、
			強制的に1970/01/03 00:00:00に変換（DB登録時も）
			 */
			if(hour48 < dateValue.getTime()){
				dateValue.setTime(hour48);
			}
			if(hour48 == dateValue.getTime()){
				String strHH = sdfHH.format(dateValue);
				Integer hh = Integer.parseInt(strHH);
				hh = hh + 48;
				strDate = String.valueOf(hh) + ":" + sdfMmSs.format(dateValue);
			}else if(hour24 <= dateValue.getTime()){
				String strHH = sdfHH.format(dateValue);
				Integer hh = Integer.parseInt(strHH);
				hh = hh + 24;
				strDate = String.valueOf(hh) + ":" + sdfMmSs.format(dateValue);
			}else {
				//開始時間
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				strDate = sdf.format(dateValue);
			}
		}
		return strDate;
	}
}
