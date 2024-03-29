/*

Copyright (C) 2014 NTT DATA Corporation

This program is free software; you can redistribute it and/or
Modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, version 2.

This program is distributed in the hope that it will be
useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
PURPOSE.  See the GNU General Public License for more details.

 */

package com.clustercontrol.infra.dialog;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.bean.RequiredFieldColorConstant;

public class ChangeBackgroundModifyListener implements ModifyListener {

	@Override
	public void modifyText(ModifyEvent e) {
		if (!(e.widget instanceof Text)) {
			return;
		}

		Text textControl = (Text)e.widget;
		if("".equals(textControl.getText())){
			textControl.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			textControl.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
	}
}
