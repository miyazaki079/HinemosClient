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

package com.clustercontrol.composite.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 数値用VerifyListenerクラス<BR>
 *
 * @version 2.2.0
 * @since 1.0.0
 */
public class NumberVerifyListener implements VerifyListener {
	private Integer low;
	private Integer high;

	/**
	 * コンストラクタ
	 */
	public NumberVerifyListener(){
		this.low = null;
		this.high = null;
	}

	/**
	 * コンストラクタ
	 *
	 * @param low 下限
	 * @param high 上限
	 */
	public NumberVerifyListener(int low, int high){
		this.low = Integer.valueOf(low);
		this.high = Integer.valueOf(high);
	}

	/**
	 * 上限を取得します。
	 *
	 * @return 上限
	 */
	public Integer getHigh() {
		return high;
	}

	/**
	 * 上限を設定します。
	 *
	 * @param high 上限
	 */
	public void setHigh(Integer high) {
		this.high = high;
	}

	/**
	 * 下限を取得します。
	 *
	 * @return 下限
	 */
	public Integer getLow() {
		return low;
	}

	/**
	 * 下限を設定します。
	 *
	 * @param low 下限
	 */
	public void setLow(Integer low) {
		this.low = low;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.swt.events.VerifyListener#verifyText(org.eclipse.swt.events.VerifyEvent)
	 */
	@Override
	public void verifyText(VerifyEvent e) {

		//テキストボックスから入力前の文字列を取得
		Text text = (Text)e.getSource();
		WidgetTestUtil.setTestId(this, null, text);
		StringBuilder input = new StringBuilder(text.getText());

		//キー入力以外は有効にする
		if (e.keyCode == 0) {
			//貼り付けの場合もここに入る

			//文字列追加
			input.replace(e.start, e.end, e.text);
		}
		else {
			//BackspaceやDeleteが押されたときは、有効にする
			if (e.character == SWT.BS || e.character == SWT.DEL) {
				input.delete(e.start, e.end);
			}
			else{
				//文字追加
				input.replace(e.start, e.end, e.text);
			}
		}

		//'-'のみの入力可能
		if (input.toString().matches("-")) {
			return;
		}
		//'-'は数値の前のみ入力可能
		else if (!input.toString().matches("-?\\d*")) {
			e.doit = false;
			return;
		}

		//範囲チェック
		checkRange(e, input.toString());
	}

	/**
	 * 範囲チェック
	 *
	 * @param e イベント
	 * @param inputText 入力文字列
	 */
	protected void checkRange(VerifyEvent e, String inputText){
		try{
			//空白はチェックしない
			if(inputText.length() == 0){
				return;
			}

			//上下限の設定がない場合はチェックしない
			if(this.low == null || this.high == null){
				return;
			}

			//数値に変換
			Integer input = Integer.valueOf(inputText.toString());

			//範囲チェック
			if(input.compareTo(low) < 0 ||
					input.compareTo(high) > 0){
				//入力は無効
				e.doit = false;

				String[] args = { this.low.toString(), this.high.toString() };

				//エラーメッセージ
				MessageDialog.openWarning(
						null,
						Messages.getString("message.hinemos.1"),
						Messages.getString("message.hinemos.8", args ));
			}
		}
		catch(NumberFormatException ex){
			//数値変換失敗の為、入力は無効
			e.doit = false;
		}
	}
}
