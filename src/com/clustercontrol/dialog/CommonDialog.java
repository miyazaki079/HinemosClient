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

package com.clustercontrol.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 共通的に利用できるダイアログクラス<BR>
 * <p>
 *
 * 基本的には、getInitialSizeとcustomizeDialogを実装して下さい。 <br>
 * 入力値チェックを実施する場合、validateを実装して下さい。
 *
 * @version 2.2.0
 * @since 1.0.0
 */
public class CommonDialog extends Dialog {

	// ----- コンストラクタ ----- //

	/**
	 * parentに連なるダイアログのインスタンスを返します。
	 *
	 * @param parent
	 *            Shellオブジェクト
	 */
	public CommonDialog(Shell parent) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	// ----- instance メソッド ----- //

	/**
	 * ダイアログのカスタマイズを行います。
	 * <p>
	 *
	 * @param composite
	 *            ダイアログエリアのコンポジットオブジェクト
	 */
	protected void customizeDialog(Composite composite) {
	}

	/**
	 * ＯＫボタンのテキストを返します。
	 *
	 * @return ＯＫボタンのテキスト
	 */
	protected String getOkButtonText() {
		return null;
	}

	/**
	 * キャンセルボタンのテキストを返します。
	 *
	 * @return キャンセルボタンのテキスト
	 */
	protected String getCancelButtonText() {
		return null;
	}

	/**
	 * ダイアログの入力値チェックを行います。
	 * <p>
	 *
	 * 必要に応じて、入力値チェックを実装して下さい。
	 *
	 * @return ValidateResultオブジェクト
	 */
	protected ValidateResult validate() {
		return null;
	}

	/**
	 * ダイアログの入力値を素に処理を行います。
	 * <p>
	 *
	 * 必要に応じて、入力値を素に行う処理を実装して下さい。
	 */
	protected boolean action() {
		return true;
	}

	/**
	 * ＯＫボタンが押された場合に呼ばれるメソッドで、入力値チェックを実施します。
	 * <p>
	 *
	 * エラーの場合、ダイアログを閉じずにエラー内容を通知します。
	 */
	@Override
	protected void okPressed() {
		ValidateResult result = this.validate();

		if (result == null || result.isValid()) {

			if(this.action()){
				super.okPressed();
			}

		} else {
			this.displayError(result);
		}
	}

	/**
	 * エラー内容を通知します。
	 * <p>
	 *
	 * 警告メッセージボックスにて、クライアントに通知します。
	 *
	 * @param result
	 *            ValidateResultオブジェクト
	 */
	protected void displayError(ValidateResult result) {
		MessageDialog.openWarning(
				null,
				result.getID(),
				result.getMessage());
	}

	/**
	 * 登録してよいかを確認します。
	 *
	 * @param result ValidateResultオブジェクト
	 * @return　結果
	 */
	protected boolean displayQuestion(ValidateResult result) {
		return MessageDialog.openQuestion(
				null,
				Messages.getString("confirmed"),
				result.getMessage());

	}

	// ----- Dialogクラスのオーバーライドメソッド ----- //

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = (Composite) super.createContents (parent);
		WidgetTestUtil.setTestId(this, null, composite);
		Composite areaComposite = (Composite) this.getDialogArea();
		WidgetTestUtil.setTestId(this, "area", areaComposite);

		areaComposite.setLayout(new FillLayout(SWT.DEFAULT));
		ScrolledComposite scrolledComposite = new ScrolledComposite( areaComposite, SWT.V_SCROLL | SWT.H_SCROLL);
		WidgetTestUtil.setTestId(this, "scrolled", scrolledComposite);

		Composite childComposite = new Composite(scrolledComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "child", childComposite);

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setContent(childComposite);

		// ダイアログエリアのカスタマイズ
		this.customizeDialog(childComposite);

		if (areaComposite.getSize().x > 0 && areaComposite.getSize().y > 0) {
			// 各ダイアログ側でpackがされている場合
			scrolledComposite.setMinSize(
					areaComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			// 縦スクロールバーが表示された場合に、横スクロールバーが表示されてしまうため、
			// スクロールバーが表示されるサイズからあらかじめ引いておく
			scrolledComposite.setMinWidth(areaComposite.getSize().x - 20);
		} else {
			//各ダイアログ側でpackがされていない場合
			/*
    		 この処理でスクロールは付くが、ダイアログ内の表示がおかしくなる。
    		    		scrolledComposite.setMinSize(areaComposite.getShell().getSize().x,
    				areaComposite.getShell().getSize().y);
			 */
		}

		// ＯＫボタンのテキスト変更
		String okText = this.getOkButtonText();
		if (okText != null) {
			Button okButton = this.getButton(IDialogConstants.OK_ID);
			if (okButton != null) {
				WidgetTestUtil.setTestId(this, "ok", okButton);
				okButton.setText(okText);
			}
		}

		// キャンセルボタンのテキスト変更
		String cancelText = this.getCancelButtonText();
		if (cancelText != null) {
			Button cancelButton = this.getButton(IDialogConstants.CANCEL_ID);
			if (cancelButton != null) {
				WidgetTestUtil.setTestId(this, "cancel", cancelButton);
				cancelButton.setText(cancelText);
			}
		}

		return composite;
	}
}
