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

package com.clustercontrol.monitor.view.action;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.custom.dialog.MonitorCustomDialog;
import com.clustercontrol.hinemosagent.dialog.AgentCreateDialog;
import com.clustercontrol.http.dialog.HttpNumericCreateDialog;
import com.clustercontrol.http.dialog.HttpScenarioCreateDialog;
import com.clustercontrol.http.dialog.HttpStringCreateDialog;
import com.clustercontrol.jmx.dialog.JmxCreateDialog;
import com.clustercontrol.logfile.dialog.LogfileStringCreateDialog;
import com.clustercontrol.monitor.composite.MonitorListComposite;
import com.clustercontrol.monitor.plugin.IMonitorPlugin;
import com.clustercontrol.monitor.plugin.LoadMonitorPlugin;
import com.clustercontrol.monitor.run.action.GetMonitorListTableDefine;
import com.clustercontrol.monitor.run.bean.MonitorTypeConstant;
import com.clustercontrol.monitor.run.dialog.CommonMonitorDialog;
import com.clustercontrol.monitor.view.MonitorListView;
import com.clustercontrol.performance.monitor.dialog.PerformanceCreateDialog;
import com.clustercontrol.ping.dialog.PingCreateDialog;
import com.clustercontrol.port.dialog.PortCreateDialog;
import com.clustercontrol.process.dialog.ProcessCreateDialog;
import com.clustercontrol.snmp.dialog.SnmpNumericCreateDialog;
import com.clustercontrol.snmp.dialog.SnmpStringCreateDialog;
import com.clustercontrol.snmptrap.dialog.SnmpTrapCreateDialog;
import com.clustercontrol.sql.dialog.SqlNumericCreateDialog;
import com.clustercontrol.sql.dialog.SqlStringCreateDialog;
import com.clustercontrol.systemlog.dialog.SystemlogStringCreateDialog;
import com.clustercontrol.winevent.dialog.WinEventDialog;
import com.clustercontrol.winservice.dialog.WinServiceCreateDialog;

/**
 * 監視[一覧]ビューのコピーアクションクラス<BR>
 *
 * @version 5.0.0
 */
public class MonitorCopyAction extends AbstractHandler implements IElementUpdater {

	// ログ
	private static Log m_log = LogFactory.getLog( MonitorCopyAction.class );

	/** アクションID */
	public static final String ID = MonitorCopyAction.class.getName();

	/** ビュー */
	private IWorkbenchPart viewPart;

	/**
	 * Dispose
	 */
	@Override
	public void dispose() {
		this.viewPart = null;
	}

	public int dialogOpen(Shell shell, String managerName, String pluginId, String monitorId, int monitorType) {
		CommonMonitorDialog dialog = null;
		if (pluginId.equals(HinemosModuleConstant.MONITOR_AGENT)) {
			dialog = new AgentCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_HTTP_N)) {
			dialog = new HttpNumericCreateDialog(shell, managerName, monitorId, false);
		} else if(pluginId.equals(HinemosModuleConstant.MONITOR_HTTP_S)) {
			dialog = new HttpStringCreateDialog(shell, managerName, monitorId, false);
		} else if(pluginId.equals(HinemosModuleConstant.MONITOR_HTTP_SCENARIO)) {
			dialog = new HttpScenarioCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_PERFORMANCE)) {
			dialog = new PerformanceCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_PING)) {
			dialog = new PingCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_PORT)) {
			dialog = new PortCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_PROCESS)) {
			dialog = new ProcessCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_SNMP_N)) {
			dialog = new SnmpNumericCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_SNMP_S)) {
			dialog = new SnmpStringCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_SQL_N)) {
			dialog = new SqlNumericCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_SQL_S)) {
			dialog = new SqlStringCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_SYSTEMLOG)) {
			dialog = new SystemlogStringCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_LOGFILE)) {
			dialog = new LogfileStringCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_CUSTOM)) {
			dialog = new MonitorCustomDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_SNMPTRAP)) {
			dialog = new SnmpTrapCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_WINSERVICE)) {
			dialog = new WinServiceCreateDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_WINEVENT)) {
			dialog = new WinEventDialog(shell, managerName, monitorId, false);
		} else if (pluginId.equals(HinemosModuleConstant.MONITOR_JMX)) {
			dialog = new JmxCreateDialog(shell, managerName, monitorId, false);
		} else {
			for(IMonitorPlugin extensionMonitor: LoadMonitorPlugin.getExtensionMonitorList()){
				if(pluginId.equals(extensionMonitor.getMonitorPluginId())){
					return extensionMonitor.create(shell, managerName, monitorId, false);
				}
			}

			m_log.warn("unknown pluginId " + pluginId);
			return -1;
		}
		return dialog.open();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// 選択アイテムの取得
		this.viewPart = HandlerUtil.getActivePart(event);
		MonitorListView view = (MonitorListView) this.viewPart.getAdapter(MonitorListView.class);

		MonitorListComposite composite = (MonitorListComposite) view.getListComposite();
		StructuredSelection selection = (StructuredSelection) composite.getTableViewer().getSelection();

		List<?> list = (List<?>) selection.getFirstElement();
		String managerName = null;
		String pluginId = null;
		String monitorId = null;
		int monitorType = 0;
		if(list != null && list.size() > 0){
			managerName = (String) list.get(GetMonitorListTableDefine.MANAGER_NAME);
			pluginId = (String) list.get(GetMonitorListTableDefine.MONITOR_TYPE_ID);
			monitorId = (String) list.get(GetMonitorListTableDefine.MONITOR_ID);
			monitorType = MonitorTypeConstant.stringToType((String) list.get(GetMonitorListTableDefine.MONITOR_TYPE));
		}


		dialogOpen(this.viewPart.getSite().getShell(), managerName, pluginId, monitorId, monitorType);

		// ビューの更新
		view.update();
		return null;
	}

	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		// page may not start at state restoring
		if( null != window ){
			IWorkbenchPage page = window.getActivePage();
			if( null != page ){
				IWorkbenchPart part = page.getActivePart();

				boolean editEnable = false;
				if(part instanceof MonitorListView){
					// Enable button when 1 item is selected
					MonitorListView view = (MonitorListView)part;

					if(view.getSelectedNum() == 1) {
						editEnable = true;
					}
				}
				this.setBaseEnabled(editEnable);
			} else {
				this.setBaseEnabled(false);
			}
		}
	}

}
