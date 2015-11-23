package com.clustercontrol.logfile.dialog;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.bean.HinemosModuleConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.monitor.run.dialog.CommonMonitorStringDialog;
import com.clustercontrol.monitor.util.MonitorSettingEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.ws.monitor.InvalidRole_Exception;
import com.clustercontrol.ws.monitor.LogfileCheckInfo;
import com.clustercontrol.ws.monitor.MonitorDuplicate_Exception;
import com.clustercontrol.ws.monitor.MonitorInfo;
import com.clustercontrol.ws.notify.NotifyRelationInfo;

/**
 * ログファイル監視の設定ダイアログクラス<BR>
 *
 * @version 4.0.0
 * @since 4.0.0
 */
public class LogfileStringCreateDialog extends CommonMonitorStringDialog {

	// ログ
	private static Log m_log = LogFactory.getLog( LogfileStringCreateDialog.class );

	// ----- instance フィールド ----- //
	/** ログファイル */
	//private Text m_textLogfile = null;

	/** ディレクトリ */
	private Text m_directory = null;

	/** ファイル名 */
	private Text m_fileName = null;

	/** エンコード */
	private Text m_fileEncoding = null;

	/** 改行コード */
	private Combo m_fileReturnCode = null;

	/** マネージャ名 */
	private String managerName = null;
	// ----- コンストラクタ ----- //

	/**
	 * 作成用ダイアログのインスタンスを返します。
	 *
	 * @param parent
	 *            親のシェルオブジェクト
	 */
	public LogfileStringCreateDialog(Shell parent) {
		super(parent, null);
		logLineFlag = true;
	}

	/**
	 * 作成用ダイアログのインスタンスを返します。
	 *
	 * @param parent 親のシェルオブジェクト
	 * @param managerName マネージャ名
	 * @param monitorType 監視判定タイプ
	 */
	public LogfileStringCreateDialog(Shell parent, String managerName, int monitorType) {
		super(parent, managerName);

		logLineFlag = true;
		this.managerName = managerName;
		this.m_monitorType = monitorType;
	}

	/**
	 * 変更用ダイアログのインスタンスを返します。
	 *
	 * @param parent 親のシェルオブジェクト
	 * @param managerName マネージャ名
	 * @param monitorId 変更する監視項目ID
	 * @param updateFlg 更新するか否か（true:変更、false:新規登録）
	 */
	public LogfileStringCreateDialog(Shell parent, String managerName, String monitorId, boolean updateFlg) {
		super(parent, managerName);

		logLineFlag = true;
		this.managerName = managerName;
		this.monitorId = monitorId;
		this.updateFlg = updateFlg;
	}

	// ----- instance メソッド ----- //

	/**
	 * ダイアログエリアを生成します。
	 *
	 * @param parent 親のインスタンス
	 */
	@Override
	protected void customizeDialog(Composite parent) {

		super.customizeDialog(parent);

		// タイトル
		shell.setText(Messages.getString("dialog.logfile.create.modify"));


		// 変数として利用されるラベル
		Label label = null;
		// 変数として利用されるグリッドデータ
		GridData gridData = null;

		/*
		 * チェック設定グループ（条件グループの子グループ）
		 */
		Group groupCheckRule = new Group(groupRule, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, groupCheckRule);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns = BASIC_UNIT;
		groupCheckRule.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = BASIC_UNIT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupCheckRule.setLayoutData(gridData);
		groupCheckRule.setText(Messages.getString("check.rule"));

		//ディレクトリ
		// ラベル
		label = new Label(groupCheckRule, SWT.NONE);
		WidgetTestUtil.setTestId(this, "directory", label);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_TITLE_LONG;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("directory") + " : ");
		// テキスト
		this.m_directory = new Text(groupCheckRule, SWT.BORDER | SWT.LEFT);
		WidgetTestUtil.setTestId(this, "directory", m_directory);
		gridData = new GridData();
		gridData.horizontalSpan = LONG_UNIT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.m_directory.setLayoutData(gridData);
		this.m_directory.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		//ファイル名
		// ラベル
		label = new Label(groupCheckRule, SWT.NONE);
		WidgetTestUtil.setTestId(this, "filename", label);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_TITLE_LONG;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("file.name") + "(" + Messages.getString("regex") + ") : ");
		// テキスト
		this.m_fileName = new Text(groupCheckRule, SWT.BORDER | SWT.LEFT);
		WidgetTestUtil.setTestId(this, "filename", m_fileName);
		gridData = new GridData();
		gridData.horizontalSpan = LONG_UNIT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.m_fileName.setLayoutData(gridData);
		this.m_fileName.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		this.m_fileName.setToolTipText(Messages.getString("dialog.logfile.pattern"));

		//ファイルエンコーディング
		// ラベル
		label = new Label(groupCheckRule, SWT.NONE);
		WidgetTestUtil.setTestId(this, "fileencoding", label);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_TITLE_LONG;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("file.encoding") + " : ");
		// テキスト
		this.m_fileEncoding = new Text(groupCheckRule, SWT.BORDER | SWT.LEFT);
		WidgetTestUtil.setTestId(this, "fileencoding", m_fileEncoding);
		gridData = new GridData();
		gridData.horizontalSpan = LONG_UNIT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.m_fileEncoding.setLayoutData(gridData);
		this.m_fileEncoding.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		
		//ファイル改行コード
		// ラベル
		label = new Label(groupCheckRule, SWT.NONE);
		WidgetTestUtil.setTestId(this, "filereturncode", label);
		gridData = new GridData();
		gridData.horizontalSpan = WIDTH_TITLE_LONG;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);
		label.setText(Messages.getString("file.returncode") + " : ");
		// コンボボックス
		this.m_fileReturnCode = new Combo(groupCheckRule, SWT.DROP_DOWN | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "filereturncode", m_fileReturnCode);
		gridData = new GridData();
		gridData.horizontalSpan = LONG_UNIT;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.m_fileReturnCode.setLayoutData(gridData);

		m_fileReturnCode.add("LF");
		m_fileReturnCode.add("CR");
		m_fileReturnCode.add("CRLF");
		m_fileReturnCode.setText("LF");//デフォルト
		
		// 監視間隔の設定を利用不可とする
		this.m_monitorRule.setRunIntervalEnabled(false);

		// ダイアログを調整
		this.adjustDialog();

		// 初期表示
		MonitorInfo info = null;
		if(this.monitorId == null){
			// 作成の場合
			info = new MonitorInfo();
			this.setInfoInitialValue(info);
		} else {
			// 変更の場合、情報取得
			try {
				MonitorSettingEndpointWrapper wrapper = MonitorSettingEndpointWrapper.getWrapper(managerName);
				info = wrapper.getMonitor(monitorId, HinemosModuleConstant.MONITOR_LOGFILE);
			} catch (InvalidRole_Exception e) {
				// アクセス権なしの場合、エラーダイアログを表示する
				MessageDialog.openInformation(
						null,
						Messages.getString("message"),
						Messages.getString("message.accesscontrol.16"));

			} catch (Exception e) {
				// 上記以外の例外
				m_log.warn("customizeDialog() getMonitor, " + e.getMessage(), e);
				MessageDialog.openInformation(
						null,
						Messages.getString("message"),
						Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());

			}
		}
		this.setInputData(info);
	}

	/**
	 * 更新処理
	 *
	 */
	@Override
	public void update(){
		super.update();

		// 必須項目を明示
		Text[] texts = {m_directory, m_fileName, m_fileEncoding};
		for (Text text : texts) {
			if("".equals(text.getText())){
				text.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
			}else{
				text.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
			}
		}
	}

	/**
	 * 各項目に入力値を設定します。
	 *
	 * @param monitor 設定値として用いる監視情報
	 */
	@Override
	protected void setInputData(MonitorInfo monitor) {

		super.setInputData(monitor);
		this.inputData = monitor;

		List<NotifyRelationInfo> c = monitor.getNotifyId();
		if (c != null ) {
			for (NotifyRelationInfo i : c) {
				m_log.debug("notifyId : " + i.getNotifyId());
			}
		}

		// 監視条件 ログファイル監視情報
		LogfileCheckInfo logfileInfo = monitor.getLogfileCheckInfo();
		if(logfileInfo == null){
			logfileInfo = new LogfileCheckInfo();
		}
		if(logfileInfo != null){
			if (logfileInfo.getDirectory() != null){
				this.m_directory.setText(logfileInfo.getDirectory());
			}
			if (logfileInfo.getFileName() != null){
				this.m_fileName.setText(logfileInfo.getFileName());
			}
			if (logfileInfo.getFileEncoding() != null){
				this.m_fileEncoding.setText(logfileInfo.getFileEncoding());
			} else {
				this.m_fileEncoding.setText("UTF-8");
			}
			if (logfileInfo.getFileReturnCode() != null){
				this.m_fileReturnCode.setText(logfileInfo.getFileReturnCode());
			}
		}

		// ログファイル名が必須項目であることを明示
		this.update();

		m_stringValueInfo.setInputData(monitor);
	}

	/**
	 * 入力値を用いて通知情報を生成します。
	 *
	 * @return 入力値を保持した通知情報
	 */
	@Override
	protected MonitorInfo createInputData() {
		super.createInputData();
		if(validateResult != null){
			return null;
		}

		// ログファイル監視（文字列）固有情報を設定
		monitorInfo.setMonitorTypeId(HinemosModuleConstant.MONITOR_LOGFILE);

		// 監視条件 ログファイル監視情報
		LogfileCheckInfo logfileInfo = new LogfileCheckInfo();
		logfileInfo.setMonitorTypeId(HinemosModuleConstant.MONITOR_LOGFILE);
		logfileInfo.setMonitorId(monitorInfo.getMonitorId());

		//テキストボックスから文字列を取得
		if (this.m_directory.getText() != null
				&& !"".equals(this.m_directory.getText())) {
			logfileInfo.setDirectory(this.m_directory.getText());
		}
		if (this.m_fileName.getText() != null
				&& !"".equals(this.m_fileName.getText())) {
			logfileInfo.setFileName(this.m_fileName.getText());
		}
		if (this.m_fileEncoding.getText() != null
				&& !"".equals(this.m_fileEncoding.getText())) {
			logfileInfo.setFileEncoding(this.m_fileEncoding.getText());
		}
		if (this.m_fileReturnCode.getText() != null
				&& !"".equals(this.m_fileReturnCode.getText())) {
			logfileInfo.setFileReturnCode(this.m_fileReturnCode.getText());
		}
		
		monitorInfo.setLogfileCheckInfo(logfileInfo);

		// 結果判定の定義
		validateResult = m_stringValueInfo.createInputData(monitorInfo);
		if(validateResult != null){
			return null;
		}

		// 通知関連情報とアプリケーションの設定
		validateResult = m_notifyInfo.createInputData(monitorInfo);
		if (validateResult != null) {
			if(validateResult.getID() == null){	// 通知ID警告用出力
				if(!displayQuestion(validateResult)){
					validateResult = null;
					return null;
				}
			}
			else{	// アプリケーション未入力チェック
				return null;
			}
		}

		return monitorInfo;
	}

	/**
	 * 入力値をマネージャに登録します。
	 *
	 * @return true：正常、false：異常
	 *
	 * @see com.clustercontrol.dialog.CommonDialog#action()
	 */
	@Override
	protected boolean action() {
		boolean result = false;

		MonitorInfo info = this.inputData;
		String managerName = this.getManagerName();
		if(info != null){
			String[] args = { info.getMonitorId(), managerName };
			MonitorSettingEndpointWrapper wrapper = MonitorSettingEndpointWrapper.getWrapper(managerName);
			if(!this.updateFlg){
				// 作成の場合
				try {
					result = wrapper.addMonitor(info);

					if(result){
						MessageDialog.openInformation(
								null,
								Messages.getString("successful"),
								Messages.getString("message.monitor.33", args));
					} else {
						MessageDialog.openError(
								null,
								Messages.getString("failed"),
								Messages.getString("message.monitor.34", args));
					}
				} catch (MonitorDuplicate_Exception e) {
					// 監視項目IDが重複している場合、エラーダイアログを表示する
					MessageDialog.openInformation(
							null,
							Messages.getString("message"),
							Messages.getString("message.monitor.53", args));

				} catch (Exception e) {
					String errMessage = "";
					if (e instanceof InvalidRole_Exception) {
						// アクセス権なしの場合、エラーダイアログを表示する
						MessageDialog.openInformation(
								null,
								Messages.getString("message"),
								Messages.getString("message.accesscontrol.16"));
					} else {
						errMessage = ", " + e.getMessage();
					}

					MessageDialog.openError(
							null,
							Messages.getString("failed"),
							Messages.getString("message.monitor.34", args) + errMessage);
				}
			} else {
				// 変更の場合
				String errMessage = "";
				try {
					result = wrapper.modifyMonitor(info);
				} catch (InvalidRole_Exception e) {
					// アクセス権なしの場合、エラーダイアログを表示する
					MessageDialog.openInformation(
							null,
							Messages.getString("message"),
							Messages.getString("message.accesscontrol.16"));
				} catch (Exception e) {
					errMessage = ", " + e.getMessage();
				}

				if(result) {
					MessageDialog.openInformation(
							null,
							Messages.getString("successful"),
							Messages.getString("message.monitor.35", args));
				} else {
					MessageDialog.openError(
							null,
							Messages.getString("failed"),
							Messages.getString("message.monitor.36", args) + errMessage);
				}
			}
		}

		return result;
	}
}
