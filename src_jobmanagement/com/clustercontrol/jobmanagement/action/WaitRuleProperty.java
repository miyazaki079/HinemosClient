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
import com.clustercontrol.bean.EndStatusConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.jobmanagement.bean.JudgmentObjectConstant;
import com.clustercontrol.jobmanagement.editor.JobPropertyDefine;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;

/**
 * 待ち条件用プロパティを作成するクライアント側アクションクラス<BR>
 * 
 * @version 2.1.0
 * @since 1.0.0
 */
public class WaitRuleProperty {

	/** 名前 */
	public static final String ID_JUDGMENT_OBJECT = "judgmentObject";

	/** ジョブID */
	public static final String ID_JOB_ID = "jobId";

	/** 値（終了状態） */
	public static final String ID_CONDITION_END_STATUS = "conditionEndStatus";

	/** 値（終了値） */
	public static final String ID_CONDITION_END_VALUE = "conditionEndValue";

	/** 時刻 */
	public static final String ID_TIME = "time";

	/** セッション開始時の時間（分）*/
	public static final String ID_START_MINUTE = "endStartMinute";

	/**
	 * 待ち条件用プロパティを取得します。<BR>
	 * 
	 * <p>
	 * <ol>
	 *  <li>待ち条件の設定項目毎にID, 名前, 処理定数（{@link com.clustercontrol.bean.PropertyDefineConstant}）を指定し、
	 *      プロパティ（{@link com.clustercontrol.bean.Property}）を生成します。</li>
	 *  <li>各設定項目のプロパティをツリー状に定義します。</li>
	 * </ol>
	 * 
	 * <p>プロパティに定義する待ち条件設定項目は、下記の通りです。
	 * <p>
	 * <ul>
	 *  <li>プロパティ（親。ダミー）
	 *  <ul>
	 *   <li>名前（子。コンボボックス）
	 *   <ul>
	 *    <li>ジョブ(終了状態)（名前の選択肢）
	 *    <ul>
	 *     <li>ジョブID（孫。ダイアログ）
	 *     <li>値（孫。コンボボックス）
	 *    </ul>
	 *    <li>ジョブ(終了値)（名前の選択肢）
	 *    <ul>
	 *     <li>ジョブID（孫。ダイアログ）
	 *     <li>値（孫。テキスト）
	 *    </ul>
	 *    <li>時刻（名前の選択肢）
	 *    <ul>
	 *     <li>時刻（孫。テキスト）
	 *    </ul>
	 *    <li>セッション開始時の時間（分）（名前の選択肢）
	 *    <ul>
	 *     <li>セッション開始時の時間（分）（孫。テキスト）
	 *    </ul>
	 *   </ul>
	 *  </ul>
	 * </ul>
	 * 
	 * @param parentJobId 親ジョブID
	 * @param jobId ジョブID
	 * @param type ジョブ変数の種別
	 * @return 待ち条件用プロパティ
	 * 
	 * @see com.clustercontrol.bean.Property
	 * @see com.clustercontrol.bean.PropertyDefineConstant
	 * @see com.clustercontrol.bean.JudgmentObjectConstant
	 * @see com.clustercontrol.bean.EndStatusConstant
	 */
	public Property getProperty(JobTreeItem item, int type) {
		//プロパティ項目定義
		Property judgmentObject = new Property(ID_JUDGMENT_OBJECT,
				Messages.getString("name"), PropertyDefineConstant.EDITOR_SELECT);
		Property job = new Property(ID_JOB_ID,
				Messages.getString("job.id"), PropertyDefineConstant.EDITOR_JOB);
		Property conditionEndStatus = new Property(ID_CONDITION_END_STATUS,
				Messages.getString("value"), PropertyDefineConstant.EDITOR_SELECT);
		Property conditionEndValue = new Property(ID_CONDITION_END_VALUE,
				Messages.getString("value"), PropertyDefineConstant.EDITOR_NUM, DataRangeConstant.SMALLINT_HIGH, DataRangeConstant.SMALLINT_LOW);
		Property time = new Property(ID_TIME,
				Messages.getString("wait.rule.time.example"), PropertyDefineConstant.EDITOR_TIME);
		Property startMinute = new Property(ID_START_MINUTE,
				Messages.getString("minute"), PropertyDefineConstant.EDITOR_NUM, DataRangeConstant.SMALLINT_HIGH, 0);

		//JobPropertyDefineクラスはClusterControlでは定義されていない
		JobPropertyDefine define = new JobPropertyDefine(item);
		job.setDefine(define);

		//ジョブ終了状態
		ArrayList<Object> jobEndStatusPropertyList = new ArrayList<Object>();
		jobEndStatusPropertyList.add(job);
		jobEndStatusPropertyList.add(conditionEndStatus);

		HashMap<String, Object> jobEndStatusMap = new HashMap<String, Object>();
		jobEndStatusMap.put("value", JudgmentObjectConstant.STRING_JOB_END_STATUS);
		jobEndStatusMap.put("property", jobEndStatusPropertyList);

		//ジョブ終了値
		ArrayList<Object> jobEndValuePropertyList = new ArrayList<Object>();
		jobEndValuePropertyList.add(job);
		jobEndValuePropertyList.add(conditionEndValue);

		HashMap<String, Object> jobEndValueMap = new HashMap<String, Object>();
		jobEndValueMap.put("value", JudgmentObjectConstant.STRING_JOB_END_VALUE);
		jobEndValueMap.put("property", jobEndValuePropertyList);

		//時刻
		ArrayList<Object> timePropertyList = new ArrayList<Object>();
		timePropertyList.add(time);

		HashMap<String, Object> timeMap = new HashMap<String, Object>();
		timeMap.put("value", JudgmentObjectConstant.STRING_TIME);
		timeMap.put("property", timePropertyList);

		//セッション開始時の時間（分）
		ArrayList<Object> startMinuteList = new ArrayList<Object>();
		startMinuteList.add(startMinute);

		HashMap<String, Object> startMinuteMap = new HashMap<String, Object>();
		startMinuteMap.put("value", JudgmentObjectConstant.STRING_START_MINUTE);
		startMinuteMap.put("property", startMinuteList);

		//判定対象コンボボックスの選択項目
		Object judgmentObjectValues[][] = {
				{ JudgmentObjectConstant.STRING_JOB_END_STATUS,
					JudgmentObjectConstant.STRING_JOB_END_VALUE,
					JudgmentObjectConstant.STRING_TIME,
					JudgmentObjectConstant.STRING_START_MINUTE},
					{ jobEndStatusMap, jobEndValueMap, timeMap, startMinuteMap } };

		Object conditionEndStatuss[][] = {
				{ EndStatusConstant.STRING_NORMAL,
					EndStatusConstant.STRING_WARNING,
					EndStatusConstant.STRING_ABNORMAL,
					EndStatusConstant.STRING_ANY},
					{ EndStatusConstant.STRING_NORMAL,
						EndStatusConstant.STRING_WARNING,
						EndStatusConstant.STRING_ABNORMAL,
						EndStatusConstant.STRING_ANY} };

		judgmentObject.setSelectValues(judgmentObjectValues);
		conditionEndStatus.setSelectValues(conditionEndStatuss);

		//値を初期化
		judgmentObject.setValue("");
		job.setValue("");
		conditionEndStatus.setValue("");
		time.setValue("");
		startMinute.setValue("");

		//変更の可/不可を設定
		judgmentObject.setModify(PropertyDefineConstant.MODIFY_OK);
		job.setModify(PropertyDefineConstant.MODIFY_OK);
		conditionEndStatus.setModify(PropertyDefineConstant.MODIFY_OK);
		conditionEndValue.setModify(PropertyDefineConstant.MODIFY_OK);
		time.setModify(PropertyDefineConstant.MODIFY_OK);
		startMinute.setModify(PropertyDefineConstant.MODIFY_OK);

		Property property = new Property(null, null, null);

		if (type == JudgmentObjectConstant.TYPE_JOB_END_STATUS) {
			judgmentObject.setValue(JudgmentObjectConstant.STRING_JOB_END_STATUS);

			// 初期表示ツリーを構成。
			property.removeChildren();
			property.addChildren(judgmentObject);

			// 判定対象ツリー
			judgmentObject.removeChildren();
			judgmentObject.addChildren(job);
			judgmentObject.addChildren(conditionEndStatus);
		}
		else if (type == JudgmentObjectConstant.TYPE_JOB_END_VALUE) {
			judgmentObject.setValue(JudgmentObjectConstant.STRING_JOB_END_VALUE);

			// 初期表示ツリーを構成。
			property.removeChildren();
			property.addChildren(judgmentObject);

			// 判定対象ツリー
			judgmentObject.removeChildren();
			judgmentObject.addChildren(job);
			judgmentObject.addChildren(conditionEndValue);
		}
		else if (type == JudgmentObjectConstant.TYPE_TIME) {
			judgmentObject.setValue(JudgmentObjectConstant.STRING_TIME);

			// 初期表示ツリーを構成。
			property.removeChildren();
			property.addChildren(judgmentObject);

			// 判定対象ツリー
			judgmentObject.removeChildren();
			judgmentObject.addChildren(time);
		}
		else {
			judgmentObject.setValue(JudgmentObjectConstant.STRING_START_MINUTE);

			// 初期表示ツリーを構成。
			property.removeChildren();
			property.addChildren(judgmentObject);

			// 判定対象ツリー
			judgmentObject.removeChildren();
			judgmentObject.addChildren(startMinute);
		}

		return property;
	}
}
