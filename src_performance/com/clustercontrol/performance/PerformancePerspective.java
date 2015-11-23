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

package com.clustercontrol.performance;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

import com.clustercontrol.ClusterControlPerspectiveBase;
import com.clustercontrol.performance.view.PerformanceGraphView;

/**
 * 性能管理のパースペクティブを生成するクラス
 * 
 */
public class PerformancePerspective extends ClusterControlPerspectiveBase {

	/**
	 * 画面レイアウトを実装します。
	 * 
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		super.createInitialLayout(layout);

		// エディタ領域のIDを取得
		String editorArea = layout.getEditorArea();
		float percent = 0.35f;
		// エディタ領域の上部percent％を占めるフォルダを作成
		IFolderLayout top = layout.createFolder( "top", IPageLayout.TOP, percent, editorArea );
		// エディタ領域の下部(1-percent)％を占めるフォルダの作成
		layout.createFolder( "bottom", IPageLayout.BOTTOM, (IPageLayout.RATIO_MAX-percent), editorArea).addPlaceholder( PerformanceGraphView.ID );

		top.addView(com.clustercontrol.performance.view.PerformanceListView.ID);
	}
}
