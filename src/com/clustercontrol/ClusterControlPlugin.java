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

package com.clustercontrol;

import java.net.URL;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.clustercontrol.monitor.plugin.IMonitorPlugin;
import com.clustercontrol.monitor.plugin.LoadMonitorPlugin;

/**
 * 
 * Hinemosクライアントのメインプラグインクラス<BR>
 * 
 * ここでEclipse(RCP)との接続点です。
 */
public class ClusterControlPlugin extends AbstractUIPlugin {

	private static Log m_log = LogFactory.getLog( ClusterControlPlugin.class );

	// ----- static フィールド ----- //

	/** コンソールアイコンの取得キー */
	public static final String IMG_CONSOLE = "console";

	/** スコープアイコンの取得キー */
	public static final String IMG_SCOPE = "scope";

	/** 参照不可のスコープアイコンの取得キー */
	public static final String IMG_SCOPE_INVALID = "scope_invalid";

	/** ノードアイコンの取得キー */
	public static final String IMG_NODE = "node";

	/** 使用不可のノードアイコンの取得キー */
	public static final String IMG_NODE_INVALID = "node_invalid";

	/** ジョブユニットアイコンの取得キー */
	public static final String IMG_JOBUNIT = "jobunit";

	/** 参照できないジョブユニットアイコンの取得キー */
	public static final String IMG_JOBUNIT_UNREFERABLE = "jobunit_unreferable";

	/** ジョブネットアイコンの取得キー */
	public static final String IMG_JOBNET = "jobnet";

	/** ジョブアイコンの取得キー */
	public static final String IMG_JOB = "job";

	/** ファイル転送ジョブアイコンの取得キー */
	public static final String IMG_FILEJOB = "filejob";

	/** 参照ジョブアイコンの取得キー */
	public static final String IMG_REFERJOB = "referjob";

	/** チェック有りアイコンの取得キー */
	public static final String IMG_CHECKED = "checked";

	/** チェックなしアイコンの取得キー */
	public static final String IMG_UNCHECKED = "unchecked";

	/** 実行状態(白)アイコンの取得キー */
	public static final String IMG_STATUS_WHITE = "status_white";

	/** 実行状態(黄)アイコンの取得キー */
	public static final String IMG_STATUS_YELLOW = "status_yellow";

	/** 実行状態(青)アイコンの取得キー */
	public static final String IMG_STATUS_BLUE = "status_blue";

	/** 実行状態(赤)アイコンの取得キー */
	public static final String IMG_STATUS_RED = "status_red";

	/** 実行状態(緑)アイコンの取得キー */
	public static final String IMG_STATUS_GREEN = "status_green";

	/** 終了状態(正常)アイコンの取得キー */
	public static final String IMG_END_STATUS_NORMAL = "normal";

	/** 終了状態(警告)アイコンの取得キー */
	public static final String IMG_END_STATUS_WARNING = "warning";

	/** 終了状態(異常)アイコンの取得キー */
	public static final String IMG_END_STATUS_ABNORMAL = "abnormal";

	/** 予定(過去)アイコンの取得キー */
	public static final String IMG_SCHEDULE_PAST = "schedule_past";

	/** 予定(現在)アイコンの取得キー */
	public static final String IMG_SCHEDULE_NOW = "schedule_now";

	/** 予定(未来)アイコンの取得キー */
	public static final String IMG_SCHEDULE_FUTURE = "schedule_future";

	/** 編集モードアイコンの取得キー */
	public static final String IMG_JOB_EDIT_MODE = "job_edit_mode";

	/** ロール設定のルートアイコンの取得キー */
	public static final String IMG_ROLESETTING_ROOT = "rootSettingsRoot";

	/** ロール設定のロールアイコンの取得キー */
	public static final String IMG_ROLESETTING_ROLE = "rootSettingsRole";

	/** ロール設定のユーザアイコンの取得キー */
	public static final String IMG_ROLESETTING_USER = "rootSettingsUser";

	/** Initial window size and position */
	public static final Rectangle WINDOW_INIT_SIZE = new Rectangle( -1, -1, 1024, 768 );

	/** スコープの区切り文字（セパレータ） */
	private final static String DEFAULT_SEPARATOR = ">";


	/** The shared instance */
	private static ClusterControlPlugin plugin;

	/** RAPかどうかを判別する */
	private static Boolean is_rap = null;

	// ----- instance メソッド ----- //

	//Resource bundle.
	private ResourceBundle resourceBundle;

	private String separator;

	private ILogListener listener;

	// ----- コンストラクタ ----- //

	/**
	 * The constructor.
	 */
	public ClusterControlPlugin() {
		super();
		m_log.debug("ClusterControlPlugin()");

		// log4jを使うための登録処理
		listener = new Listener();
		Platform.addLogListener(listener);

		plugin = this;
		try {
			resourceBundle = ResourceBundle
					.getBundle("com.clustercontrol.ClusterControlPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}

		// Systemプロパティ
		m_log.info("starting Hinemos Client...");
		m_log.info("java.vm.version = " + System.getProperty("java.vm.version"));
		m_log.info("java.vm.vendor = " + System.getProperty("java.vm.vendor"));
		m_log.info("java.home = " + System.getProperty("java.home"));
		m_log.info("os.name = " + System.getProperty("os.name"));
		m_log.info("os.arch = " + System.getProperty("os.arch"));
		m_log.info("os.version = " + System.getProperty("os.version"));
		m_log.info("user.name = " + System.getProperty("user.name"));
		m_log.info("user.dir = " + System.getProperty("user.dir"));
		m_log.info("user.country = " + System.getProperty("user.country"));
		m_log.info("user.language = " + System.getProperty("user.language"));
		m_log.info("file.encoding = " + System.getProperty("file.encoding"));

		// 起動時刻
		long startDate = System.currentTimeMillis();
		m_log.info("start date = " + new Date(startDate) + "(" + startDate + ")");


		// 追加監視の情報をログへ出力　*ここでクラスを初期化しています。
		for(IMonitorPlugin pluginMonitor: LoadMonitorPlugin.getExtensionMonitorList()){
			m_log.info("Extended monitor " + pluginMonitor.getMonitorPluginId() + " pluged in.");
		}
	}

	private class Listener implements ILogListener {
		@Override
		public void logging(final IStatus status, final String plugin) {
			if (status.getSeverity() == IStatus.INFO) {
				if (status.getException() == null) {
					m_log.info(status.getMessage());
				} else {
					m_log.info(status.getMessage(), status.getException());
				}
			} else if (status.getSeverity() == IStatus.WARNING) {
				if (status.getException() == null) {
					m_log.warn(status.getMessage());
				} else {
					m_log.warn(status.getMessage(), status.getException());
				}
			} else {
				if (status.getException() == null) {
					m_log.error(status.getMessage());
				} else {
					m_log.error(status.getMessage(), status.getException());
				}
			}
		}
	}

	// ----- instance メソッド ----- //

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static ClusterControlPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = ClusterControlPlugin.getDefault()
				.getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * プラグインクラスが保持するImageRegistryにイメージを登録します。
	 * 
	 * @param registry
	 *            ImageRegistryオブジェクト
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		this.registerImage(registry, IMG_CONSOLE, "console_view.gif");
		this.registerImage(registry, IMG_SCOPE, "scope.gif");
		this.registerImage(registry, IMG_SCOPE_INVALID, "scope_invalid.gif");
		this.registerImage(registry, IMG_NODE, "node.gif");
		this.registerImage(registry, IMG_NODE_INVALID, "node_invalid.gif");
		this.registerImage(registry, IMG_JOBUNIT, "job_unit.gif");
		this.registerImage(registry, IMG_JOBUNIT_UNREFERABLE, "job_unit_unreferable.gif");
		this.registerImage(registry, IMG_JOBNET, "job_net.gif");
		this.registerImage(registry, IMG_JOB, "job.gif");
		this.registerImage(registry, IMG_FILEJOB, "file_obj.gif");
		this.registerImage(registry, IMG_REFERJOB, "referJob.gif");
		this.registerImage(registry, IMG_CHECKED, "checked.gif");
		this.registerImage(registry, IMG_UNCHECKED, "unchecked.gif");
		this.registerImage(registry, IMG_STATUS_BLUE, "status_blue.gif");
		this.registerImage(registry, IMG_STATUS_GREEN, "status_green.gif");
		this.registerImage(registry, IMG_STATUS_RED, "status_red.gif");
		this.registerImage(registry, IMG_STATUS_WHITE, "status_white.gif");
		this.registerImage(registry, IMG_STATUS_YELLOW, "status_yellow.gif");
		this.registerImage(registry, IMG_END_STATUS_NORMAL, "normal.gif");
		this.registerImage(registry, IMG_END_STATUS_WARNING, "warning.gif");
		this.registerImage(registry, IMG_END_STATUS_ABNORMAL, "abnormal.gif");
		this.registerImage(registry, IMG_SCHEDULE_PAST, "schedule_g.gif");
		this.registerImage(registry, IMG_SCHEDULE_NOW, "schedule_b.gif");
		this.registerImage(registry, IMG_SCHEDULE_FUTURE, "schedule_w.gif");
		this.registerImage(registry, IMG_JOB_EDIT_MODE, "job_edit_mode.gif");

		/*
		 * RoleSettingTree
		 */
		this.registerImage(registry, IMG_ROLESETTING_ROOT, "node.gif");
		this.registerImage(registry, IMG_ROLESETTING_ROLE, "role.gif");
		this.registerImage(registry, IMG_ROLESETTING_USER, "user.gif");
	}

	/**
	 * ImageRegistryにイメージを登録します。
	 * 
	 * @param registry
	 *            ImageRegistryオブジェクト
	 * @param key
	 *            取得キー
	 * @param fileName
	 *            イメージファイル名
	 */
	private void registerImage(ImageRegistry registry, String key,
			String fileName) {
		try {
			URL url = new URL(getDefault().getBundle().getEntry("/"), "icons/"
					+ fileName);
			ImageDescriptor desc = ImageDescriptor.createFromURL(url);
			registry.put(key, desc);
		} catch (Exception e) {
		}
	}

	/**
	 * @return Returns the separator.
	 */
	public String getSeparator() {
		if (separator == null || separator.compareTo("") == 0) {
			separator = DEFAULT_SEPARATOR;
		}
		return separator;
	}

	/**
	 * @param separator
	 *            The separator to set.
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * プラグインIDとして利用できる、固有のIDを返します。
	 * 
	 * @return プラグインID
	 * @see Bundle#getSymbolicName()
	 */
	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Check if this application is run in RAP
	 * @return Is RAP or not
	 */
	public static Boolean isRAP() {
		if( null == is_rap ){
			// getAppName() will return null in RAP
			is_rap = (null == Display.getAppName());
		}
		return is_rap;
	}

}
