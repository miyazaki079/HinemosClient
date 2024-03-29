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

package com.clustercontrol.snmptrap.action;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.PriorityConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.util.Messages;

/**
 * OID情報更新用プロパティを作成するクライアント側アクションクラス<BR>
 * 
 * @version 2.1.0
 * @since 2.1.0
 */
public class GetSnmpTrapModifyProperty {
	/** MIB */
	public static final String ID_MIB = "mib";

	/** トラップ名 */
	public static final String ID_TRAP_NAME = "trapName";

	/** トラップOID */
	public static final String ID_TRAP_OID = "trapOid";

	/** generic_id */
	public static final String ID_GENERIC_ID = "genericId";

	/** specific_id */
	public static final String ID_SPRCIFIC_ID = "specificId";

	/** 有効/無効 */
	public static final String ID_VALID = "valid";

	/** 重要度 */
	public static final String ID_PRIORITY = "priority";

	/** メッセージ */
	public static final String ID_LOGMSG = "logmsg";

	/** 詳細メッセージ */
	public static final String ID_DESCR = "descr";

	/**
	 * OID情報更新用プロパティを返します。
	 * 
	 * @param local
	 * @return OID情報更新用プロパティ
	 */
	public Property getProperty() {

		//プロパティ項目定義
		Property mib = new Property(ID_MIB, Messages.getString("mib"),
				PropertyDefineConstant.EDITOR_TEXT);
		Property trapName = new Property(ID_TRAP_NAME, Messages.getString("trap.name"),
				PropertyDefineConstant.EDITOR_TEXT);
		Property trapOid = new Property(ID_TRAP_OID, Messages.getString("oid"),
				PropertyDefineConstant.EDITOR_TEXT);
		Property genericId = new Property(ID_GENERIC_ID, Messages.getString("generic.id"),
				PropertyDefineConstant.EDITOR_TEXT);
		Property specificId = new Property(ID_SPRCIFIC_ID, Messages.getString("specific.id"),
				PropertyDefineConstant.EDITOR_TEXT);
		Property valid = new Property(ID_VALID, Messages.getString("valid") + "/" + Messages.getString("invalid"),
				PropertyDefineConstant.EDITOR_BOOL);
		Property priority = new Property(ID_PRIORITY, Messages.getString("priority"),
				PropertyDefineConstant.EDITOR_SELECT);
		Property logmsg = new Property(ID_LOGMSG, Messages.getString("message"),
				PropertyDefineConstant.EDITOR_TEXTAREA, DataRangeConstant.VARCHAR_256);
		Property descr = new Property(ID_DESCR, Messages.getString("detail") + Messages.getString("message"),
				PropertyDefineConstant.EDITOR_TEXTAREA, DataRangeConstant.VARCHAR_8192);

		//値を初期化

		Object priorityValues[][] = {
				{ PriorityConstant.STRING_CRITICAL, PriorityConstant.STRING_WARNING, PriorityConstant.STRING_INFO, PriorityConstant.STRING_UNKNOWN},
				{ PriorityConstant.STRING_CRITICAL, PriorityConstant.STRING_WARNING, PriorityConstant.STRING_INFO, PriorityConstant.STRING_UNKNOWN}};

		priority.setSelectValues(priorityValues);
		priority.setValue(PriorityConstant.STRING_CRITICAL);

		//値を初期化
		mib.setValue("");
		trapName.setValue("");
		trapOid.setValue("");
		genericId.setValue("");
		specificId.setValue("");
		valid.setValue(new Boolean(true));
		logmsg.setValue("");
		descr.setValue("");

		//変更の可/不可を設定
		mib.setModify(PropertyDefineConstant.MODIFY_NG);
		trapName.setModify(PropertyDefineConstant.MODIFY_NG);
		trapOid.setModify(PropertyDefineConstant.MODIFY_NG);
		genericId.setModify(PropertyDefineConstant.MODIFY_NG);
		specificId.setModify(PropertyDefineConstant.MODIFY_NG);
		valid.setModify(PropertyDefineConstant.MODIFY_OK);
		priority.setModify(PropertyDefineConstant.MODIFY_OK);
		logmsg.setModify(PropertyDefineConstant.MODIFY_OK);
		descr.setModify(PropertyDefineConstant.MODIFY_OK);

		Property property = new Property(null, null, null);

		// 初期表示ツリーを構成。
		property.removeChildren();
		property.addChildren(mib);
		property.addChildren(trapName);
		property.addChildren(trapOid);
		property.addChildren(genericId);
		property.addChildren(specificId);
		property.addChildren(valid);
		property.addChildren(priority);
		property.addChildren(logmsg);
		property.addChildren(descr);

		return property;
	}
}
