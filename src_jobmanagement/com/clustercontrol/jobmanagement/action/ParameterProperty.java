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

package com.clustercontrol.jobmanagement.action;

import java.util.ArrayList;
import java.util.HashMap;

import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.jobmanagement.bean.JobParamTypeConstant;
import com.clustercontrol.jobmanagement.bean.SystemParameterConstant;
import com.clustercontrol.util.Messages;

/**
 * ジョブ変数用プロパティを作成するクライアント側アクションクラス<BR>
 *
 * @version 2.1.0
 * @since 2.1.0
 */
public class ParameterProperty {

	/** パラメータID（システムジョブ変数） */
	public static final String ID_SYSTEM_JOB_PARAM_ID = "systemJobId";
	
	/** パラメータID（システムジョブ変数） */
	public static final String ID_SYSTEM_NODE_PARAM_ID = "systemNodeId";

	/** パラメータID（ユーザ変数） */
	public static final String ID_USER_PARAM_ID = "useId";

	/** 種別 */
	public static final String ID_TYPE = "type";

	/** 値 */
	public static final String ID_VALUE = "value";

	/** 説明 */
	public static final String ID_DESCRIPTION = "description";

	/**
	 * ジョブ変数用プロパティを返します。
	 *
	 * <p>
	 * <ol>
	 *  <li>ジョブ変数の設定項目毎にID, 名前, 処理定数（{@link com.clustercontrol.bean.PropertyDefineConstant}）を指定し、
	 *      プロパティ（{@link com.clustercontrol.bean.Property}）を生成します。</li>
	 *  <li>各設定項目のプロパティをツリー状に定義します。</li>
	 * </ol>
	 *
	 * <p>プロパティに定義するジョブ変数設定項目は、下記の通りです。
	 * <p>
	 * <ul>
	 *  <li>プロパティ（親。ダミー）
	 *  <ul>
	 *   <li>種別（子。コンボボックス）
	 *   <ul>
	 *    <li>システム（種別の選択肢）
	 *    <ul>
	 *     <li>名前（孫。コンボボックス）
	 *     <li>説明（孫。テキスト）
	 *    </ul>
	 *    <li>ユーザ（種別の選択肢）
	 *    <ul>
	 *     <li>名前（孫。テキスト）
	 *     <li>値（孫。テキスト）
	 *     <li>説明（孫。テキスト）
	 *    </ul>
	 *   </ul>
	 *  </ul>
	 * </ul>
	 *
	 * @param type ジョブ変数の種別
	 * @return ジョブ変数用プロパティ
	 *
	 * @see com.clustercontrol.bean.Property
	 * @see com.clustercontrol.bean.PropertyDefineConstant
	 * @see com.clustercontrol.bean.JobParamTypeConstant
	 * @see com.clustercontrol.jobmanagement.bean.SystemParameterConstant
	 */
	public Property getProperty(int type) {
		//プロパティ項目定義
		Property sysytemJobId = new Property(ID_SYSTEM_JOB_PARAM_ID, Messages.getString("name"),
				PropertyDefineConstant.EDITOR_SELECT);
		Property sysytemNodeId = new Property(ID_SYSTEM_NODE_PARAM_ID, Messages.getString("name"),
				PropertyDefineConstant.EDITOR_SELECT);
		Property useId = new Property(ID_USER_PARAM_ID, Messages.getString("name"),
				PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_64);
		Property paramType = new Property(ID_TYPE, Messages.getString("type"),
				PropertyDefineConstant.EDITOR_SELECT);
		Property value = new Property(ID_VALUE, Messages.getString("value"),
				PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_1024);
		Property description = new Property(ID_DESCRIPTION, Messages.getString("description"),
				PropertyDefineConstant.EDITOR_TEXT, DataRangeConstant.VARCHAR_256);

		//システムパラメータ
		ArrayList<Object> systemJobList = new ArrayList<Object>();
		systemJobList.add(sysytemJobId);
		systemJobList.add(description);
		HashMap<String, Object> systemJobListMap = new HashMap<String, Object>();
		systemJobListMap.put("value", JobParamTypeConstant.STRING_SYSTEM_JOB);
		systemJobListMap.put("property", systemJobList);

		ArrayList<Object> systemNodeList = new ArrayList<Object>();
		systemNodeList.add(sysytemNodeId);
		systemNodeList.add(description);
		HashMap<String, Object> systemNodeListMap = new HashMap<String, Object>();
		systemNodeListMap.put("value", JobParamTypeConstant.STRING_SYSTEM_NODE);
		systemNodeListMap.put("property", systemNodeList);

		//ユーザパラメータ
		ArrayList<Object> userList = new ArrayList<Object>();
		userList.add(useId);
		userList.add(value);
		userList.add(description);

		HashMap<String, Object> userListMap = new HashMap<String, Object>();
		userListMap.put("value", JobParamTypeConstant.STRING_USER);
		userListMap.put("property", userList);

		//種別コンボボックスの選択項目
		Object typeValues[][] = {
				{ JobParamTypeConstant.STRING_SYSTEM_JOB,
					JobParamTypeConstant.STRING_SYSTEM_NODE,
					JobParamTypeConstant.STRING_USER},
					{ systemJobListMap,
						systemNodeListMap,
						userListMap} };

		Object systemIdJobList[][] = {
				SystemParameterConstant.SYSTEM_ID_LIST_JOB,SystemParameterConstant.SYSTEM_ID_LIST_JOB
		};

		Object systemIdNodeList[][] = {
				SystemParameterConstant.SYSTEM_ID_LIST_NODE, SystemParameterConstant.SYSTEM_ID_LIST_NODE
		};

		paramType.setSelectValues(typeValues);
		sysytemJobId.setSelectValues(systemIdJobList);
		sysytemJobId.setValue(SystemParameterConstant.FACILITY_ID);
		sysytemNodeId.setSelectValues(systemIdNodeList);
		sysytemNodeId.setValue(SystemParameterConstant.FACILITY_NAME);

		//値を初期化
		useId.setValue("");
		paramType.setValue("");
		value.setValue("");
		description.setValue("");

		//変更の可/不可を設定
		sysytemJobId.setModify(PropertyDefineConstant.MODIFY_OK);
		sysytemNodeId.setModify(PropertyDefineConstant.MODIFY_OK);
		useId.setModify(PropertyDefineConstant.MODIFY_OK);
		paramType.setModify(PropertyDefineConstant.MODIFY_OK);
		value.setModify(PropertyDefineConstant.MODIFY_OK);
		description.setModify(PropertyDefineConstant.MODIFY_OK);

		Property property = new Property(null, null, null);

		if (type == JobParamTypeConstant.TYPE_SYSTEM_JOB ||
				type == JobParamTypeConstant.TYPE_SYSTEM_NODE) {
			paramType.setValue(JobParamTypeConstant.typeToString(type));

			// 初期表示ツリーを構成。
			property.removeChildren();
			property.addChildren(paramType);

			// 判定対象ツリー
			paramType.removeChildren();
			if (type == JobParamTypeConstant.TYPE_SYSTEM_JOB) {
				paramType.addChildren(sysytemJobId);
			} else if (type == JobParamTypeConstant.TYPE_SYSTEM_NODE){
				paramType.addChildren(sysytemNodeId);
			}
			paramType.addChildren(description);
		}
		else if (type == JobParamTypeConstant.TYPE_USER) {
			paramType.setValue(JobParamTypeConstant.STRING_USER);

			// 初期表示ツリーを構成。
			property.removeChildren();
			property.addChildren(paramType);

			// 判定対象ツリー
			paramType.removeChildren();
			paramType.addChildren(useId);
			paramType.addChildren(value);
			paramType.addChildren(description);
		}

		return property;
	}
}
