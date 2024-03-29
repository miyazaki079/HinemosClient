/*

Copyright (C) 2011 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.notify.util;

import com.clustercontrol.notify.bean.NotifyTypeConstant;
import com.clustercontrol.util.Messages;

/**
 * 通知に関するUtilityクラス<br/>
 */
public class NotifyTypeUtil {
	public static String typeToString(int notifyType) {
		switch (notifyType) {
		case NotifyTypeConstant.TYPE_STATUS:
			return Messages.getString("notifies.status");
		case NotifyTypeConstant.TYPE_EVENT:
			return Messages.getString("notifies.event");
		case NotifyTypeConstant.TYPE_MAIL:
			return Messages.getString("notifies.mail");
		case NotifyTypeConstant.TYPE_JOB:
			return Messages.getString("notifies.job");
		case NotifyTypeConstant.TYPE_LOG_ESCALATE:
			return Messages.getString("notifies.log.escalate");
		case NotifyTypeConstant.TYPE_COMMAND:
			return Messages.getString("notifies.command");
		default:
			return "";
		}
	}
}
