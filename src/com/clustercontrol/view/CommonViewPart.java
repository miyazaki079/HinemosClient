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

package com.clustercontrol.view;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.ViewPluginAction;
import org.eclipse.ui.part.ViewPart;

import com.clustercontrol.startup.view.StartUpView;
import com.clustercontrol.util.LoginManager;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 共通ViewPartクラス<BR>
 * 
 * クラスタコントローラ用のViewにて基底クラスとして使用する
 * 
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommonViewPart extends ViewPart {

	@Override
	public void init( IViewSite site ) throws PartInitException{
		super.init( site );

		// Set testid for test
		Widget widget;
		widget = ((ToolBarManager)getViewSite().getActionBars().getToolBarManager()).getControl();
		if( null != widget ){
			WidgetTestUtil.setTestId( this, null, widget );
		}
		widget = ((MenuManager)getViewSite().getActionBars().getMenuManager()).getMenu();
		if( null != widget ){
			WidgetTestUtil.setTestId( this, null, widget );
		}
	}

	/**
	 * コンストラクタ
	 */
	public CommonViewPart(){
		super(); 

		// Condition-1 Skip login if it is StartUpView
		// Condition-2 Skip and redirect to the login in postWindowCreate() at startup ( 0 == loginAttempts)
		if( !(this instanceof StartUpView) && 0 < LoginManager.getLoginAttempts() ){
			// ログインしていない場合は、ビュー表示前に必ずログインダイアログを出すようにする
			if (! LoginManager.isLogin() ) {
				LoginManager.login();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * アダプターとして要求された場合、自身のインスタンスを渡します。
	 * 
	 * @return 自身のインスタンス
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 * @since 1.0.0
	 */
	@Override
	public Object getAdapter(Class cls) {
		if (cls.isInstance(this)) {
			return this;
		} else {
			return super.getAdapter(cls);
		}
	}

	/**
	 * @param enable
	 */
	public void setEnabledAction(String actionID, boolean enable) {
		ActionContributionItem ci = (ActionContributionItem)getViewSite().getActionBars().getToolBarManager().find(actionID);
		IAction action =  ci.getAction();
		action.setEnabled(enable);
	}

	/**
	 * 
	 * @since
	 */
	public void setEnabledActionAll(boolean enable) {
		IToolBarManager tm = getViewSite().getActionBars().getToolBarManager();
		IContributionItem[] cis = tm.getItems();

		for (int i = 0; i < cis.length; i++) {
			if (cis[i] instanceof ActionContributionItem) {
				ActionContributionItem ci = (ActionContributionItem) cis[i];
				ci.getAction().setEnabled(enable);
			}
		}
	}

	/**
	 * @param enable
	 */
	public void setEnabledAction(String actionID,ISelection selection) {
		ActionContributionItem ci = (ActionContributionItem)getViewSite().getActionBars().getToolBarManager().find(actionID);
		ViewPluginAction action =  (ViewPluginAction)ci.getAction();
		action.selectionChanged(selection);
	}
}
