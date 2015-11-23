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

package com.clustercontrol.performance.view.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import com.clustercontrol.view.ScopeListBaseView;

/**
 * スコープ階層ペイン表示切替を行うアクションクラス
 * 
 * @version 1.0.0
 * @since 1.0.0
 */
public class ScopeShowAction implements IViewActionDelegate {

	/** ビュー */
	private IViewPart viewPart;


	/**
	 * ビューを保持します。ワークベンチにロードされた際に呼ばれます。
	 * 
	 * @param view ビューのインスタンス
	 * 
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	@Override
	public void init(IViewPart view) {
		viewPart = view;
	}

	/**
	 * スコープ階層ペインの表示／非表示を行います。
	 * 
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(IAction action) {
		ScopeListBaseView view = (ScopeListBaseView) viewPart
				.getAdapter(ScopeListBaseView.class);

		if (action.isChecked()) {
			view.show();
		} else {
			view.hide();
		}

		view.setFocus();
	}

	/**
	 * 選択を変更した際に呼ばれます。
	 * 
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
