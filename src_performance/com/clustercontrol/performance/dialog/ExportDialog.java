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

package com.clustercontrol.performance.dialog;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.client.ui.util.FileDownloader;
import com.clustercontrol.composite.FacilityTreeComposite;
import com.clustercontrol.monitor.util.MonitorSettingEndpointWrapper;
import com.clustercontrol.performance.action.RecordDataWriter;
import com.clustercontrol.repository.FacilityPath;
import com.clustercontrol.repository.bean.FacilityConstant;
import com.clustercontrol.repository.util.RepositoryEndpointWrapper;
import com.clustercontrol.ws.monitor.InvalidRole_Exception;
import com.clustercontrol.ws.monitor.MonitorInfo;
import com.clustercontrol.ws.repository.FacilityInfo;
import com.clustercontrol.ws.repository.FacilityTreeItem;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;

/**
 * 収集した実績データをエクスポートするためのダイアログクラス
 *
 * @version 4.0.0
 * @since 1.0.0
 */
public class ExportDialog extends Dialog {

	// ログ
	private static Log m_log = LogFactory.getLog( ExportDialog.class );

	// Dialog Composite
	private FacilityTreeComposite treeComposite;
	private Label scopeLabel = null;
	private Button headerCheckbox = null; // ヘッダ出力有無用チェックボックス

	// Dialog Setting
	private String monitorId = null;
	private String monitorTypeId = null;
	private String facilityId = null;
	private FacilityTreeItem treeItem = null;

	// Export Setting
	private RecordDataWriter writer;
	private boolean isNode = true;
	/** マネージャ名 */
	private String managerName = null;

	/**
	 * コンストラクタ
	 */
	public ExportDialog(Shell parent, String managerName, String monitorId, String monitorTypeId) {
		super(parent);
		this.managerName = managerName;
		this.monitorId = monitorId;
		this.monitorTypeId = monitorTypeId;
	}

	/**
	 * 初期サイズの設定
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(640, 400);
	}

	/**
	 * タイトルの設定
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.getString("performance.export.dialog.title")); // "性能[エクスポート]"
	}

	/**
	 * ダイアログの設定
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		// コンポジット全体
		Composite allComposite = (Composite) super.createDialogArea(parent);
		WidgetTestUtil.setTestId(this, "all", allComposite);
		allComposite.setLayout(new FillLayout());

		Composite scopeComposite = new Composite(allComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scope", scopeComposite);
		scopeComposite.setLayout(new GridLayout());

		// スコープ表示
		Composite topComposite = new Composite(scopeComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "top", topComposite);
		topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		topComposite.setLayout(new FormLayout());

		scopeLabel = new Label(topComposite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "scopelabel", scopeLabel);
		scopeLabel.setText(Messages.getString("scope") + " : ");
		FormData formData = new FormData();
		formData.top = new FormAttachment(0, 0); // ウィンドウの上側にはりつく
		formData.left = new FormAttachment(0, 0); // ウィンドウの左側にはりつく
		scopeLabel.setLayoutData(formData);
		scopeLabel.pack();

		// Test
		// ヘッダ出力有無チェックボックス
		headerCheckbox = new Button(topComposite, SWT.CHECK);
		WidgetTestUtil.setTestId(this, "headercheck", headerCheckbox);
		headerCheckbox.setText(Messages.getString("performance.output.header")); // "ヘッダを出力"

		formData = new FormData();
		formData.top = new FormAttachment(0, 0); // ウィンドウの上側にはりつく
		formData.right = new FormAttachment(100, 0); // ラベルの右側にはりつく
		headerCheckbox.setLayoutData(formData);

		// スコープツリー表示
		createTree(scopeComposite);

		// セパレータ
		createSeparator(scopeComposite);

		return allComposite;
	}

	/**
	 * FacilityTreeのコンポジット。
	 * ノード指定時のみOKボタンを
	 */
	private void createTree(Composite composite) {

		try{
			MonitorSettingEndpointWrapper moniWrapper = MonitorSettingEndpointWrapper.getWrapper(managerName);
			MonitorInfo monitorInfo = moniWrapper.getMonitor(this.monitorId, this.monitorTypeId);
			this.facilityId = monitorInfo.getFacilityId();
			RepositoryEndpointWrapper repoWrapper = RepositoryEndpointWrapper.getWrapper(managerName);
			this.treeItem = repoWrapper.getExecTargetFacilityTreeByFacilityId(this.facilityId, null);
		} catch (InvalidRole_Exception e) {
			m_log.warn("createTree() : getMonitor, " + e.getMessage(), e);
			MessageDialog.openInformation(null, Messages.getString("message"), Messages.getString("message.accesscontrol.16"));
		} catch (Exception e) {
			m_log.warn("createTree() getMonitor, " + e.getMessage(), e);
		}

		treeComposite = new FacilityTreeComposite(composite, SWT.NONE, null, false,
				true, true, false);
		WidgetTestUtil.setTestId(this, "tree", treeComposite);
		GridData gridDataTree = new GridData(GridData.FILL_BOTH);
		treeComposite.setLayoutData(gridDataTree);
		treeComposite.setScopeTree(this.treeItem);
		treeComposite.getTreeViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {

					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						StructuredSelection selection = (StructuredSelection) event
								.getSelection();
						FacilityTreeItem selectItem = (FacilityTreeItem) selection
								.getFirstElement();
						ExportDialog.this.isNode = !(
							0 == selectItem.getChildren().size() &&
							FacilityConstant.TYPE_NODE == selectItem.getData().getFacilityType()
						);
						if (selectItem instanceof FacilityTreeItem) {
							// パスラベルの更新
							FacilityPath path = new FacilityPath(
								ClusterControlPlugin.getDefault().getSeparator()
							);
							scopeLabel.setText(Messages.getString("scope")
									+ " : " + path.getPath(selectItem));
							scopeLabel.pack();
						}
					}
				});
	}

	/**
	 * Customize button bar
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// 出力ボタン
		Button exportButton = this.createButton(parent,
				IDialogConstants.OPEN_ID, Messages.getString("export"), false);
		WidgetTestUtil.setTestId(this, "export", exportButton);

		this.getButton(IDialogConstants.OPEN_ID).addSelectionListener(
			new SelectionAdapter() {
				private FileDialog saveDialog;
				@Override
				public void widgetSelected(SelectionEvent e) {
					// 出力先ファイルを選択するダイアログを開く
					this.saveDialog = new FileDialog(getShell(), SWT.SAVE);

					FacilityTreeComposite composite = ExportDialog.this.treeComposite;
					FacilityTreeItem tree = composite.getSelectItem();
					FacilityInfo facilityInfo = tree.getData();
					String targetFacilityId = facilityInfo.getFacilityId();
					
					boolean headerFlag = ExportDialog.this.headerCheckbox.getSelection();

					// 対象ファイル名に含めるID(日付)を生成
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String defaultFileName = ExportDialog.this.monitorId + '_' +
							targetFacilityId + '-' +
							sdf.format(new Date(System.currentTimeMillis()));

					boolean archiveFlag = true;
					this.saveDialog.setFilterExtensions(new String[] { "*.zip" });
					defaultFileName += ".zip";
					this.saveDialog.setFileName(defaultFileName);

					String filePath = this.saveDialog.open();
					if( filePath != null ){
						output(ExportDialog.this.monitorId, targetFacilityId, headerFlag, archiveFlag, filePath, defaultFileName);
					}
				}

				/**
				 * Output
				 */
				protected void output( String monitorId, String facilityId, boolean headerFlag,
						boolean archiveFlag, String filePath, String fileName) {
					// 入力チェック
					if (facilityId  == null) {
						MessageDialog.openWarning(getShell(),
								Messages.getString("confirmed"),
								Messages.getString(" message.hinemos.3"));
						return;
					}

					// DataWriterへの入力
					// 書き込み準備
					writer = new RecordDataWriter(
							managerName,
							monitorId,
							facilityId,
							headerFlag,
							archiveFlag,
							filePath);

					// Download & 書き込み
					try {
						IRunnableWithProgress op = new IRunnableWithProgress() {
							@Override
							public void run(IProgressMonitor monitor)
									throws InvocationTargetException, InterruptedException {
								// エクスポートを開始
								ServiceContext context = ContextProvider.getContext();
								writer.setContext(context);
								Thread exportThread = new Thread(writer);
								exportThread.start();
								Thread.sleep(3000);
								monitor.beginTask(Messages.getString("export"), 100); // "エクスポート"

								int progress = 0;
								int buff = 0;
								while (progress < 100) {
									progress = writer.getProgress();

									if (monitor.isCanceled()) {
										throw new InterruptedException("");
									}
									if (writer.isCanceled()) {
										throw new InterruptedException(writer.getCancelMessage());
									}
									Thread.sleep(50);
									monitor.worked(progress - buff);
									buff = progress;
								}
								monitor.done();
							}
						};

						// ダイアログの表示
						new ProgressMonitorDialog(getShell()).run(true, true, op);

						// Start download file
						if( ClusterControlPlugin.isRAP() ){
							FileDownloader.openBrowser(getShell(), filePath, fileName);
						}else{
							MessageDialog.openInformation(getShell(),
									Messages.getString("confirmed"),
									Messages.getString("performance.export.success"));
						}
					} catch (InterruptedException e) {
						// キャンセルされた場合の処理
						MessageDialog.openInformation(getShell(),
								Messages.getString("confirmed"),
								Messages.getString("performance.export.cancel") + " : " + e.getMessage());
					} catch (Exception e) {
						// 異常終了
						m_log.warn("output() : " + e.getMessage(), e);
						MessageDialog.openInformation(getShell(),
								Messages.getString("confirmed"),
								Messages.getString("performance.export.cancel") + " : " + e.getMessage() +
								"(" + e.getClass().getName() + ")");
					} finally {
						writer.setCanceled(true);
						if (ClusterControlPlugin.isRAP()) {
							FileDownloader.cleanup( filePath );
						}
					}
				}
			});
		createButton(parent, IDialogConstants.CANCEL_ID, "close", false);
	}
	/**
	 * セパレータの作成
	 */
	private void createSeparator(Composite composite) {
		// セパレータ(水平線)を作成
		Label h_separator = new Label(composite, SWT.SEPARATOR
				| SWT.HORIZONTAL);
		WidgetTestUtil.setTestId(this, "separator", h_separator);
		GridData gridDataLabel = new GridData(GridData.FILL_HORIZONTAL);
		h_separator.setLayoutData(gridDataLabel);
	}
}
