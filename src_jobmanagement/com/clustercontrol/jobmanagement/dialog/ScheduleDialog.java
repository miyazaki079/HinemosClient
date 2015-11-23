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

package com.clustercontrol.jobmanagement.dialog;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.clustercontrol.util.WidgetTestUtil;
import com.clustercontrol.bean.DataRangeConstant;
import com.clustercontrol.bean.DayOfWeekConstant;
import com.clustercontrol.bean.Property;
import com.clustercontrol.bean.PropertyDefineConstant;
import com.clustercontrol.bean.RequiredFieldColorConstant;
import com.clustercontrol.bean.ScheduleConstant;
import com.clustercontrol.bean.ValidConstant;
import com.clustercontrol.calendar.composite.CalendarIdListComposite;
import com.clustercontrol.composite.ManagerListComposite;
import com.clustercontrol.composite.RoleIdListComposite;
import com.clustercontrol.composite.RoleIdListComposite.Mode;
import com.clustercontrol.composite.action.ComboModifyListener;
import com.clustercontrol.composite.action.NumberKeyListener;
import com.clustercontrol.composite.action.StringVerifyListener;
import com.clustercontrol.dialog.CommonDialog;
import com.clustercontrol.dialog.ValidateResult;
import com.clustercontrol.jobmanagement.action.GetJobKick;
import com.clustercontrol.jobmanagement.bean.JobConstant;
import com.clustercontrol.jobmanagement.util.JobEndpointWrapper;
import com.clustercontrol.util.Messages;
import com.clustercontrol.ws.jobmanagement.InvalidRole_Exception;
import com.clustercontrol.ws.jobmanagement.InvalidSetting_Exception;
import com.clustercontrol.ws.jobmanagement.InvalidUserPass_Exception;
import com.clustercontrol.ws.jobmanagement.JobKickDuplicate_Exception;
import com.clustercontrol.ws.jobmanagement.JobSchedule;
import com.clustercontrol.ws.jobmanagement.JobTreeItem;

/**
 * ジョブ[スケジュールの作成・変更]ダイアログクラスです。
 *
 * @version 4.0.0
 * @since 1.0.0
 */
public class ScheduleDialog extends CommonDialog {

	// ログ
	private static Log m_log = LogFactory.getLog( ScheduleDialog.class );

	private JobSchedule m_jobSchedule ;

	/** 実行契機ID用テキスト */
	private Text txtJobKickId = null;
	/** 実行契機名用テキスト */
	private Text txtJobKickName = null;
	/** ジョブID用テキスト */
	private Text txtJobId = null;
	/** ジョブ名用テキスト */
	private Text txtJobName = null;
	/** カレンダID用コンボボックス */
	private CalendarIdListComposite cmpCalendarId = null;
	/** ジョブ参照用ボタン */
	private Button btnJobSelect = null;
	/** スケジュール日時指定用ラジオボタン */
	private Button btnType1 = null;
	/** スケジュール曜日指定用ラジオボタン */
	private Button btnType2 = null;
	/** スケジュール曜日指定用ラジオボタン */
	private Button btnType3 = null;

	/** 有効用ラジオボタン */
	private Button btnValid = null;
	/** 無効用ラジオボタン */
	private Button btnInvalid = null;

	/** 時指定用コンボボックス */
	private Combo cmbHours1 = null;
	/** 分指定用コンボボックス */
	private Combo cmbMinutes1 = null;
	/** 分指定用コンボボックス */
	private Combo cmbMinutesP = null;
	/** 分指定用コンボボックス */
	private Combo cmbMinutesQ = null;
	/** 曜日指定用コンボボックス */
	private Combo cmbDayOfWeek = null;
	/** 時指定用コンボボックス */
	private Combo cmbHours2 = null;
	/** 分指定用コンボボックス */
	private Combo cmbMinutes2 = null;
	/** オーナーロールID用テキスト */
	private RoleIdListComposite cmpOwnerRoleId = null;
	/** シェル */
	private Shell m_shell = null;
	/** マネージャ名コンボボックス用コンポジット */
	private ManagerListComposite m_managerComposite = null;

	/**
	 * 作成：MODE_ADD = 0;
	 * 変更：MODE_MODIFY = 1;
	 * 複製：MODE_COPY = 3;
	 * */
	private int mode = PropertyDefineConstant.MODE_ADD;

	/** 所属ジョブユニットのジョブID */
	private String m_jobunitId = null;

	/** 実行契機ID */
	private String m_jobKickId;

	/** マネージャ名 */
	private String m_managerName;

	/**
	 * コンストラクタ
	 * 変更時、コピー時
	 * @param parent
	 * @param scheduleId
	 */
	public ScheduleDialog(Shell parent, String managerName, String scheduleId, int mode){
		super(parent);
		this.m_managerName = managerName;
		this.m_jobKickId = scheduleId;
		this.mode = mode;
	}
	/**
	 * ダイアログエリアを生成します。
	 *
	 * @param parent 親コンポジット
	 */
	@Override
	protected void customizeDialog(Composite parent) {
		m_shell = this.getShell();

		parent.getShell().setText(
				Messages.getString("dialog.job.add.modify.schedule"));
		/**
		 * レイアウト設定
		 * ダイアログ内のベースとなるレイアウトが全てを変更
		 */
		GridLayout baseLayout = new GridLayout(1, true);
		baseLayout.marginWidth = 10;
		baseLayout.marginHeight = 10;
		baseLayout.numColumns = 10;
		//一番下のレイヤー
		parent.setLayout(baseLayout);

		Composite composite = null;
		GridData gridData= null;

		composite = new Composite(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, null, composite);
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns =10;
		composite.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gridData);

		// マネージャ
		Label labelManager = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "manager", labelManager);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelManager.setLayoutData(gridData);
		labelManager.setText(Messages.getString("facility.manager") + " : ");
		if(this.mode == PropertyDefineConstant.MODE_MODIFY){
			this.m_managerComposite = new ManagerListComposite(composite, SWT.NONE, false);
		} else {
			this.m_managerComposite = new ManagerListComposite(composite, SWT.NONE, true);
		}
		WidgetTestUtil.setTestId(this, "managerComposite", this.m_managerComposite);
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.m_managerComposite.setLayoutData(gridData);

		if(this.m_managerName != null) {
			this.m_managerComposite.setText(this.m_managerName);
		}
		if(this.mode != PropertyDefineConstant.MODE_MODIFY) {
			this.m_managerComposite.getComboManagerName().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					String managerName = m_managerComposite.getText();
					cmpOwnerRoleId.createRoleIdList(managerName);
					updateCalendarId();
				}
			});
		}

		/*
		 * 実行契機ID
		 */
		//ラベル
		Label labelScheduleId = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "scheduleid", labelScheduleId);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelScheduleId.setText(Messages.getString("jobkick.id") + " : ");
		labelScheduleId.setLayoutData(gridData);

		/*
		 * テキスト
		 */
		this.txtJobKickId = new Text(composite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobkickid", txtJobKickId);
		this.txtJobKickId.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.txtJobKickId.setLayoutData(gridData);
		this.txtJobKickId.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		/*
		 * 実行契機名
		 */
		//ラベル
		Label labelScheduleName = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "schedulename", labelScheduleName);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelScheduleName.setText(Messages.getString("jobkick.name") + " : ");
		labelScheduleName.setLayoutData(gridData);
		//テキスト
		this.txtJobKickName = new Text(composite, SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobkickname", txtJobKickName);
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.txtJobKickName.setLayoutData(gridData);
		this.txtJobKickName.addVerifyListener(
				new StringVerifyListener(DataRangeConstant.VARCHAR_64));
		this.txtJobKickName.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		/*
		 * オーナーロールID
		 */
		Label labelRoleId = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "roleid", labelRoleId);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelRoleId.setLayoutData(gridData);
		labelRoleId.setText(Messages.getString("owner.role.id") + " : ");
		if (this.mode == PropertyDefineConstant.MODE_ADD
				|| this.mode == PropertyDefineConstant.MODE_COPY) {
			this.cmpOwnerRoleId = new RoleIdListComposite(composite, SWT.NONE,
					this.m_managerName, true, Mode.OWNER_ROLE);
			this.cmpOwnerRoleId.getComboRoleId().addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					cmpCalendarId.createCalIdCombo(m_managerName, cmpOwnerRoleId.getText());
					txtJobId.setText("");
					txtJobName.setText("");
					setJobunitId(null);
				}
			});
		} else {
			this.cmpOwnerRoleId = new RoleIdListComposite(composite, SWT.NONE,
					this.m_managerName, false, Mode.OWNER_ROLE);
		}
		WidgetTestUtil.setTestId(this, "ownerroleidlist", cmpOwnerRoleId);
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmpOwnerRoleId.setLayoutData(gridData);

		/*
		 * ジョブID
		 */
		//ラベル
		Label labelJobId = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "jobid", labelJobId);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelJobId.setText(Messages.getString("job.id") + " : ");
		labelJobId.setLayoutData(gridData);
		//テキスト
		this.txtJobId = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
		WidgetTestUtil.setTestId(this, "jobid", txtJobId);
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.txtJobId.setLayoutData(gridData);
		this.txtJobId.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		//ボタン
		btnJobSelect = new Button(composite, SWT.NONE);
		WidgetTestUtil.setTestId(this, "jobselect", btnJobSelect);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnJobSelect.setText(Messages.getString("refer"));
		btnJobSelect.setLayoutData(gridData);
		btnJobSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				JobTreeDialog dialog = new JobTreeDialog(m_shell, m_managerComposite.getText(), cmpOwnerRoleId.getText(), true);
				if (dialog.open() == IDialogConstants.OK_ID) {
					JobTreeItem selectItem = dialog.getSelectItem().isEmpty() ? null : dialog.getSelectItem().get(0);
					if (selectItem != null && selectItem.getData().getType() != JobConstant.TYPE_COMPOSITE) {
						txtJobId.setText(selectItem.getData().getId());
						txtJobName.setText(selectItem.getData().getName());
						setJobunitId(selectItem.getData().getJobunitId());
					} else {
						txtJobId.setText("");
						txtJobName.setText("");
						setJobunitId(null);
					}
				}
			}
		});
		/*
		 * ジョブ名
		 */
		//ラベル
		Label labelJobName = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "jobname", labelJobName);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelJobName.setText(Messages.getString("job.name") + " : ");
		labelJobName.setLayoutData(gridData);
		//テキスト
		txtJobName = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		WidgetTestUtil.setTestId(this, "jobname", txtJobName);
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		txtJobName.setLayoutData(gridData);
		/*
		 * カレンダID
		 */
		//ラベル
		Label labelCalendarId = new Label(composite, SWT.LEFT);
		WidgetTestUtil.setTestId(this, "calendarid", labelCalendarId);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelCalendarId.setText(Messages.getString("calendar.id") + " : ");
		labelCalendarId.setLayoutData(gridData);
		//コンボボックス
		cmpCalendarId = new CalendarIdListComposite(composite, SWT.NONE, false);
		WidgetTestUtil.setTestId(this, "calendarid", cmpCalendarId);
		gridData = new GridData();
		gridData.horizontalSpan = 6;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmpCalendarId.setLayoutData(gridData);

		/**
		 * スケジュール設定グループ
		 */
		Group groupSchedule = new Group(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "setting", groupSchedule);
		// 変数として利用されるグリッドデータ
		gridData = null;
		layout = new GridLayout(1, true);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		layout.numColumns = 20;
		groupSchedule.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupSchedule.setLayoutData(gridData);
		groupSchedule.setText(Messages.getString("schedule.setting"));

		createType1(groupSchedule);
		createType2(groupSchedule);
		createType3(groupSchedule);

		Group groupValidOrInvalid = new Group(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "validorinvalid", groupValidOrInvalid);
		layout = new GridLayout(1, true);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.numColumns =6;
		groupValidOrInvalid.setLayout(layout);
		gridData = new GridData();
		gridData.horizontalSpan = 10;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupValidOrInvalid.setLayoutData(gridData);
		groupValidOrInvalid.setText(Messages.getString("valid") + "/"
				+ Messages.getString("invalid"));

		//有効ボタン
		btnValid = new Button(groupValidOrInvalid, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "valid", btnValid);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnValid.setText(ValidConstant.STRING_VALID);
		btnValid.setLayoutData(gridData);
		btnValid.setSelection(true);
		//無効ボタン
		btnInvalid = new Button(groupValidOrInvalid, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "invalid", btnInvalid);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnInvalid.setText(ValidConstant.STRING_INVALID);
		btnInvalid.setLayoutData(gridData);

		// ダイアログを調整
		this.adjustDialog();
		//スケジュール情報反映
		reflectJobSchedule();
		update();
	}

	/**
	 * ダイアログエリアを調整します。
	 *
	 */
	private void adjustDialog(){
		// サイズを最適化
		// グリッドレイアウトを用いた場合、こうしないと横幅が画面いっぱいになります。
		m_shell.pack();
		m_shell.setSize(new Point(500, m_shell.getSize().y));

		// 画面中央に配置
		Display display = m_shell.getDisplay();
		m_shell.setLocation((display.getBounds().width - m_shell.getSize().x) / 2,
				(display.getBounds().height - m_shell.getSize().y) / 2);
	}


	/**
	 * スケジュール日時指定を生成します。
	 *
	 * @param parent 親グループ
	 */
	private void createType1(Group parent) {
		btnType1 = new Button(parent, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "type1", btnType1);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnType1.setLayoutData(gridData);
		btnType1.setText(Messages.getString("everyday"));
		btnType1.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		DecimalFormat format = new DecimalFormat("00");

		//空白ラベル
		Label label = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "space", label);
		label.setText(Messages.getString(""));
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		label.setLayoutData(gridData);

		cmbHours1 = new Combo(parent, SWT.READ_ONLY | SWT.NONE);
		WidgetTestUtil.setTestId(this, "hours1", cmbHours1);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmbHours1.setLayoutData(gridData);
		cmbHours1.setTextLimit(2);
		cmbHours1.setVisibleItemCount(10);
		cmbHours1.addKeyListener(new NumberKeyListener());
		cmbHours1.addModifyListener(new ComboModifyListener());
		format = new DecimalFormat("00");
		cmbHours1.add("*");
		for (int hour = 0; hour <= 48; hour++) {
			cmbHours1.add(format.format(hour));
		}
		cmbHours1.select(0);
		this.cmbHours1.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label labelHours = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "hours", labelHours);
		labelHours.setText(Messages.getString("hr"));
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelHours.setLayoutData(gridData);

		cmbMinutes1 = new Combo(parent, SWT.READ_ONLY | SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutes1", cmbMinutes1);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmbMinutes1.setLayoutData(gridData);
		cmbMinutes1.setTextLimit(2);
		cmbMinutes1.setVisibleItemCount(10);
		cmbMinutes1.addKeyListener(new NumberKeyListener());
		cmbMinutes1.addModifyListener(new ComboModifyListener());
		format = new DecimalFormat("00");
		for (int minutes = 0; minutes < 60; minutes++) {
			cmbMinutes1.add(format.format(minutes));
		}
		cmbMinutes1.select(0);
		this.cmbMinutes1.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label labelMinutes = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutes", labelMinutes);
		labelMinutes.setText(Messages.getString("min"));
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelMinutes.setLayoutData(gridData);
	}

	/**
	 * スケジュール曜日指定を生成します。
	 *
	 * @param parent 親グループ
	 */
	private void createType2(Group parent) {
		btnType2 = new Button(parent, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "type2", btnType2);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnType2.setLayoutData(gridData);
		btnType2.setText(Messages.getString("schedule.everyweek"));
		btnType2.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		cmbDayOfWeek = new Combo(parent, SWT.READ_ONLY | SWT.CENTER);
		WidgetTestUtil.setTestId(this, "dayofweek", cmbDayOfWeek);
		gridData = new GridData();
		gridData.horizontalSpan =4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmbDayOfWeek.setLayoutData(gridData);
		cmbDayOfWeek.setTextLimit(3);
		cmbDayOfWeek.setVisibleItemCount(10);
		cmbDayOfWeek.add(DayOfWeekConstant.STRING_SUNDAY);
		cmbDayOfWeek.add(DayOfWeekConstant.STRING_MONDAY);
		cmbDayOfWeek.add(DayOfWeekConstant.STRING_TUESDAY);
		cmbDayOfWeek.add(DayOfWeekConstant.STRING_WEDNESDAY);
		cmbDayOfWeek.add(DayOfWeekConstant.STRING_THURSDAY);
		cmbDayOfWeek.add(DayOfWeekConstant.STRING_FRIDAY);
		cmbDayOfWeek.add(DayOfWeekConstant.STRING_SATURDAY);
		cmbDayOfWeek.select(0);
		this.cmbDayOfWeek.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});
		cmbHours2 = new Combo(parent, SWT.READ_ONLY | SWT.NONE);
		WidgetTestUtil.setTestId(this, "hour2", cmbHours2);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmbHours2.setLayoutData(gridData);
		cmbHours2.setTextLimit(2);
		cmbHours2.setVisibleItemCount(10);
		cmbHours2.addKeyListener(new NumberKeyListener());
		cmbHours2.addModifyListener(new ComboModifyListener());
		DecimalFormat format = new DecimalFormat("00");
		cmbHours2.add("*");
		for (int hour = 0; hour <= 48; hour++) {
			cmbHours2.add(format.format(hour));
		}
		cmbHours2.select(0);
		this.cmbHours2.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label labelHours = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "hours", labelHours);
		labelHours.setText(Messages.getString("hr"));
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelHours.setLayoutData(gridData);

		cmbMinutes2 = new Combo(parent, SWT.READ_ONLY | SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutes2", cmbMinutes2);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmbMinutes2.setLayoutData(gridData);
		cmbMinutes2.setTextLimit(2);
		cmbMinutes2.setVisibleItemCount(10);
		cmbMinutes2.addKeyListener(new NumberKeyListener());
		cmbMinutes2.addModifyListener(new ComboModifyListener());
		format = new DecimalFormat("00");
		for (int minutes = 0; minutes < 60; minutes++) {
			cmbMinutes2.add(format.format(minutes));
		}
		cmbMinutes2.select(0);
		this.cmbMinutes2.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label labelMinutes = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutes2", labelMinutes);
		labelMinutes.setText(Messages.getString("min"));
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelMinutes.setLayoutData(gridData);
	}

	/**
	 * 「ｐ分からｑ分ごとに繰り返し実行」
	 * @param parent
	 */
	private void createType3(Group parent) {

		btnType3 = new Button(parent, SWT.RADIO);
		WidgetTestUtil.setTestId(this, "type3", btnType3);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 4;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		btnType3.setLayoutData(gridData);
		btnType3.setText(Messages.getString("hourly"));
		btnType3.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		cmbMinutesP = new Combo(parent, SWT.READ_ONLY | SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutesp", cmbMinutesP);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmbMinutesP.setLayoutData(gridData);
		cmbMinutesP.setTextLimit(2);
		cmbMinutesP.setVisibleItemCount(10);
		cmbMinutesP.addKeyListener(new NumberKeyListener());
		cmbMinutesP.addModifyListener(new ComboModifyListener());
		DecimalFormat format = new DecimalFormat("00");
		this.cmbMinutesP.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				update();
			}
		});

		Label labelMinutesFrom = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutesfrom", labelMinutesFrom);
		labelMinutesFrom.setText(Messages.getString("schedule.min.start.time"));
		gridData = new GridData();
		gridData.horizontalSpan = 5;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelMinutesFrom.setLayoutData(gridData);

		cmbMinutesQ = new Combo(parent, SWT.READ_ONLY | SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutesq", cmbMinutesQ);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		cmbMinutesQ.setLayoutData(gridData);
		cmbMinutesQ.setTextLimit(2);
		cmbMinutesQ.setVisibleItemCount(10);
		cmbMinutesQ.addKeyListener(new NumberKeyListener());
		cmbMinutesQ.addModifyListener(new ComboModifyListener());
		format = new DecimalFormat("00");
		cmbMinutesQ.add(format.format(5));
		cmbMinutesQ.add(format.format(10));
		cmbMinutesQ.add(format.format(15));
		cmbMinutesQ.add(format.format(20));
		cmbMinutesQ.add(format.format(30));
		cmbMinutesQ.add(format.format(60));
		cmbMinutesQ.select(5);

		this.cmbMinutesQ.addModifyListener(new ModifyListener(){
			@Override
			public void modifyText(ModifyEvent arg0) {
				DecimalFormat format = new DecimalFormat("00");
				int tmp = new Integer(cmbMinutesP.getSelectionIndex());
				int minutesQ = 0;
				try {
					minutesQ = Integer.parseInt(cmbMinutesQ.getText());
					cmbMinutesP.removeAll();
					for (int minutes = 0; minutes < minutesQ; minutes++) {
						cmbMinutesP.add(format.format(minutes));
					}
				}catch (NumberFormatException e){
					// 初期表示の場合にしかこの例外は発生しない
					cmbMinutesQ.setText("60");
				}
				if (minutesQ > 0) {
					tmp = tmp%minutesQ;
				}
				if(cmbMinutesP.getItemCount() > tmp){
					cmbMinutesP.select(tmp);
				}
				update();
			}
		});
		int minutesQ = Integer.parseInt(cmbMinutesQ.getText());
		for (int minutes = 0; minutes < minutesQ; minutes++) {
			cmbMinutesP.add(format.format(minutes));
		}
		Label labelMinutesTo = new Label(parent, SWT.NONE);
		WidgetTestUtil.setTestId(this, "minutesto", labelMinutesTo);
		labelMinutesTo.setText(Messages.getString("schedule.min.execution.interval"));
		gridData = new GridData();
		gridData.horizontalSpan = 7;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		labelMinutesTo.setLayoutData(gridData);
	}

	/**
	 * 更新処理
	 *
	 */
	public void update(){
		// 必須項目を明示
		if("".equals(this.txtJobKickId.getText())){
			this.txtJobKickId.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtJobKickId.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.txtJobKickName.getText())){
			this.txtJobKickName.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtJobKickName.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if("".equals(this.txtJobId.getText())){
			this.txtJobId.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.txtJobId.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}

		if(this.cmbMinutes1.getEnabled() && "".equals(this.cmbMinutes1.getText())){
			this.cmbMinutes1.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.cmbMinutes1.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}

		if(this.cmbDayOfWeek.getEnabled() && "".equals(this.cmbDayOfWeek.getText())){
			this.cmbDayOfWeek.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.cmbDayOfWeek.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}

		if(this.cmbMinutes2.getEnabled() && "".equals(this.cmbMinutes2.getText())){
			this.cmbMinutes2.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.cmbMinutes2.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(this.cmbMinutesP.getEnabled() && "".equals(this.cmbMinutesP.getText())){
			this.cmbMinutesP.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.cmbMinutesP.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}
		if(this.cmbMinutesQ.getEnabled() && "".equals(this.cmbMinutesQ.getText())){
			this.cmbMinutesQ.setBackground(RequiredFieldColorConstant.COLOR_REQUIRED);
		}else{
			this.cmbMinutesQ.setBackground(RequiredFieldColorConstant.COLOR_UNREQUIRED);
		}

		if (btnType1.getSelection()) {
			btnType1.setSelection(true);
			btnType2.setSelection(false);
			btnType3.setSelection(false);
			cmbHours1.setEnabled(true);
			cmbMinutes1.setEnabled(true);
			cmbDayOfWeek.setEnabled(false);
			cmbHours2.setEnabled(false);
			cmbMinutes2.setEnabled(false);
			cmbMinutesP.setEnabled(false);
			cmbMinutesQ.setEnabled(false);
		}
		else if (btnType2.getSelection()) {
			btnType1.setSelection(false);
			btnType2.setSelection(true);
			btnType3.setSelection(false);
			cmbHours1.setEnabled(false);
			cmbMinutes1.setEnabled(false);
			cmbDayOfWeek.setEnabled(true);
			cmbHours2.setEnabled(true);
			cmbMinutes2.setEnabled(true);
			cmbMinutesP.setEnabled(false);
			cmbMinutesQ.setEnabled(false);
		}
		else if (btnType3.getSelection()) {
			btnType1.setSelection(false);
			btnType2.setSelection(false);
			btnType3.setSelection(true);
			cmbHours1.setEnabled(false);
			cmbMinutes1.setEnabled(false);
			cmbDayOfWeek.setEnabled(false);
			cmbHours2.setEnabled(false);
			cmbMinutes2.setEnabled(false);
			cmbMinutesP.setEnabled(true);
			cmbMinutesQ.setEnabled(true);
		}
	}

	/**
	 * ダイアログにスケジュール情報を反映します。
	 *
	 * @see com.clustercontrol.jobmanagement.bean.ScheduleTableDefine
	 */
	private void reflectJobSchedule() {
		JobSchedule jobschedule = null;
		//マネージャより実行契機IDと一致するスケジュールを取得する
		//if(this.m_jobKickId!= null){
		if(mode == PropertyDefineConstant.MODE_MODIFY
				|| mode == PropertyDefineConstant.MODE_COPY){
			jobschedule = GetJobKick.getJobSchedule(this.m_managerName, this.m_jobKickId);
		}else {
			jobschedule = new JobSchedule();
			btnType1.setSelection(true);
			cmbHours1.setEnabled(true);
			cmbMinutes1.setEnabled(true);
			cmbDayOfWeek.setEnabled(false);
			cmbHours2.setEnabled(false);
			cmbMinutes2.setEnabled(false);
		}
		m_jobSchedule = jobschedule;

		// オーナーロールID設定
		if (jobschedule != null && jobschedule.getOwnerRoleId() != null) {
			this.cmpOwnerRoleId.setText(jobschedule.getOwnerRoleId());
		}
		// 他CompositeへのオーナーロールIDの設定
		this.cmpCalendarId.createCalIdCombo(this.m_managerComposite.getText(), cmpOwnerRoleId.getText());

		//実行契機IDを設定
		if(jobschedule.getId() != null){
			txtJobKickId.setText(jobschedule.getId());
			this.m_jobKickId = jobschedule.getId();
			if(mode == PropertyDefineConstant.MODE_MODIFY){
				//ファイルチェック変更時、ファイルチェックIDは変更不可とする
				txtJobKickId.setEnabled(false);
			}
		}
		//実行契機名を設定
		if(jobschedule.getName() != null){
			txtJobKickName.setText(jobschedule.getName());
		}
		//ジョブIDを設定
		if(jobschedule.getJobId() != null){
			txtJobId.setText(jobschedule.getJobId());
		}
		//ジョブ名を設定
		if(jobschedule.getJobName() != null){
			txtJobName.setText(jobschedule.getJobName());
		}
		//ジョブユニットIDを設定
		if(jobschedule.getJobunitId() != null){
			String jobunitId = jobschedule.getJobunitId();
			this.setJobunitId(jobunitId);
		}
		//カレンダIDを設定
		if (jobschedule.getCalendarId() != null) {
			this.cmpCalendarId.setText(jobschedule.getCalendarId());
		}

		//日時を設定
		DecimalFormat format = new DecimalFormat("00");
		if(jobschedule.getScheduleType() == ScheduleConstant.TYPE_DAY){
			btnType1.setSelection(true);
			if(jobschedule.getHour() != null){
				//時を設定
				cmbHours1.select(0);
				for (int i = 0; i < cmbHours1.getItemCount(); i++) {
					String hours = format.format(jobschedule.getHour());
					if (hours.equals(cmbHours1.getItem(i))) {
						cmbHours1.select(i);
						break;
					}
				}
			}
			if(jobschedule.getMinute() != null){
				//分を設定
				cmbMinutes1.select(0);
				for (int i = 0; i < cmbMinutes1.getItemCount(); i++) {
					String minutes = format.format(jobschedule.getMinute());
					if (minutes.equals(cmbMinutes1.getItem(i))) {
						cmbMinutes1.select(i);
						break;
					}
				}
			}
		}

		else if(jobschedule.getScheduleType() == ScheduleConstant.TYPE_WEEK){
			btnType2.setSelection(true);
			//曜日を設定
			cmbDayOfWeek.select(0);
			for (int i = 0; i < cmbDayOfWeek.getItemCount(); i++) {
				if (jobschedule.getWeek() == null) {
					break;
				}
				String dayOfWeek = DayOfWeekConstant.typeToString(jobschedule.getWeek());
				if (dayOfWeek.equals(cmbDayOfWeek.getItem(i))) {
					cmbDayOfWeek.select(i);
					break;
				}
			}
			//時を設定
			if(jobschedule.getHour() != null){
				cmbHours2.select(0);
				for (int i = 0; i < cmbHours2.getItemCount(); i++) {
					String hours = format.format(jobschedule.getHour());
					if (hours.equals(cmbHours2.getItem(i))) {
						cmbHours2.select(i);
						break;
					}
				}
			}
			//分を設定
			if(jobschedule.getMinute() != null){
				cmbMinutes2.select(0);
				for (int i = 0; i < cmbMinutes2.getItemCount(); i++) {
					String minutes = format.format(jobschedule.getMinute());
					if (minutes.equals(cmbMinutes2.getItem(i))) {
						cmbMinutes2.select(i);
						break;
					}
				}
			}
		}
		else if(jobschedule.getScheduleType() == ScheduleConstant.TYPE_REPEAT){
			btnType3.setSelection(true);
			/**
			 * FIXME
			 * 「q分毎」が設定された後に一度「m_comboMinutesP」をリセットするため、
			 * 「q分毎」を設定後に、「ｐ分から」を設定する必要があります
			 */
			//「q分毎」を設定
			if(jobschedule.getEveryXminutes() != null){
				cmbMinutesQ.select(0);
				for (int i = 0; i < cmbMinutesQ.getItemCount(); i++) {
					String minutes = format.format(jobschedule.getEveryXminutes());
					if (minutes.equals(cmbMinutesQ.getItem(i))) {
						cmbMinutesQ.select(i);
						break;
					}
				}
			}
			//「ｐ分から」を設定
			if(jobschedule.getFromXminutes() != null){
				cmbMinutesP.select(0);
				for (int i = 0; i < cmbMinutesP.getItemCount(); i++) {
					String minutes = format.format(jobschedule.getFromXminutes());
					if (minutes.equals(cmbMinutesP.getItem(i))) {
						cmbMinutesP.select(i);
						break;
					}
				}
			}
		}
		if(jobschedule.getValid() != null){
			//有効/無効設定
			Integer effective = jobschedule.getValid();
			if (effective == ValidConstant.TYPE_VALID) {
				btnValid.setSelection(true);
				btnInvalid.setSelection(false);
			} else {
				btnValid.setSelection(false);
				btnInvalid.setSelection(true);
			}
		}
		// 必須入力項目を可視化
		this.update();
	}

	/**
	 * ダイアログの情報からスケジュール情報を作成します。
	 *
	 * @return 入力値の検証結果
	 *
	 * @see com.clustercontrol.jobmanagement.bean.GetJobKickTableDefine
	 */
	private ValidateResult createJobSchedule() {
		ValidateResult result = null;

		this.m_jobSchedule = new JobSchedule();

		//ジョブユニットID取得
		if (getJobunitId() != null) {
			m_jobSchedule.setJobunitId(getJobunitId());
		}

		//オーナーロールID
		if (cmpOwnerRoleId.getText().length() > 0) {
			m_jobSchedule.setOwnerRoleId(cmpOwnerRoleId.getText());
		}

		//実行契機ID取得
		if (txtJobKickId.getText().length() > 0) {
			m_jobSchedule.setId(txtJobKickId.getText());
			this.m_jobKickId = txtJobKickId.getText();
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.88"));
			return result;
		}

		//実行契機名取得
		if (txtJobKickName.getText().length() > 0) {
			m_jobSchedule.setName(txtJobKickName.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.89"));
			return result;
		}

		//ジョブID取得
		if (txtJobId.getText().length() > 0) {
			m_jobSchedule.setJobId(txtJobId.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.22"));
			return result;
		}

		//ジョブ名取得
		if (txtJobName.getText().length() > 0) {
			m_jobSchedule.setJobName(txtJobName.getText());
		} else {
			result = new ValidateResult();
			result.setValid(false);
			result.setID(Messages.getString("message.hinemos.1"));
			result.setMessage(Messages.getString("message.job.23"));
			return result;
		}

		//カレンダID
		if (cmpCalendarId.getText().length() > 0) {
			m_jobSchedule.setCalendarId(cmpCalendarId.getText());
		}
		else{
			m_jobSchedule.setCalendarId("");
		}
		//スケジュール設定
		Integer hours = null;
		Integer minutes = null;
		Integer week = null;
		Integer fromXminutes = null;
		Integer everyXminutes = null;
		//スケジュール設定が「毎日」の場合
		if(btnType1.getSelection()){
			m_jobSchedule.setScheduleType(ScheduleConstant.TYPE_DAY);
			//時を取得
			if(cmbHours1.getText().length() > 0){
				//Cron定義では毎時「*」はNULLと扱っているため
				if(cmbHours1.getText().equals("*")){
					hours = null;
				}else {
					hours = new Integer(cmbHours1.getText());
				}
			}
			//分を取得
			if(cmbMinutes1.getText().length() > 0){
				minutes = new Integer(cmbMinutes1.getText());
			}
			m_jobSchedule.setHour(hours);
			m_jobSchedule.setMinute(minutes);
		}
		//スケジュール設定が「曜日」の場合
		else if(btnType2.getSelection()){
			m_jobSchedule.setScheduleType(ScheduleConstant.TYPE_WEEK);
			if(cmbDayOfWeek.getText().length() > 0){
				week = new Integer(DayOfWeekConstant
						.stringToType(cmbDayOfWeek.getText()));
			} else {
				result = new ValidateResult();
				result.setValid(false);
				result.setID(Messages.getString("message.hinemos.1"));
				result.setMessage(Messages.getString("message.job.37"));
				return result;
			}


			//時を取得
			if(cmbHours2.getText().length() > 0){
				//Cron定義では毎時「*」はNULLと扱っているため
				if(cmbHours2.getText().equals("*")){
					hours = null;
				}else {
					hours = new Integer(cmbHours2.getText());
				}
			}
			//分を取得
			if(cmbMinutes2.getText().length() > 0){
				minutes = new Integer(cmbMinutes2.getText());
			}
			m_jobSchedule.setWeek(week);
			m_jobSchedule.setHour(hours);
			m_jobSchedule.setMinute(minutes);
		}

		else if (btnType3.getSelection()){
			m_jobSchedule.setScheduleType(ScheduleConstant.TYPE_REPEAT);
			//時を取得
			if(cmbMinutesP.getText().length() > 0){
				fromXminutes = new Integer(cmbMinutesP.getText());
			}
			//分を取得
			if(cmbMinutesQ.getText().length() > 0){
				everyXminutes = new Integer(cmbMinutesQ.getText());
			}
			m_jobSchedule.setFromXminutes(fromXminutes);
			m_jobSchedule.setEveryXminutes(everyXminutes);
		}


		//有効/無効取得
		if (btnValid.getSelection()) {
			m_jobSchedule.setValid(ValidConstant.TYPE_VALID);
		} else {
			m_jobSchedule.setValid(ValidConstant.TYPE_INVALID);
		}

		return result;
	}

	/**
	 * ＯＫボタンテキスト取得
	 *
	 * @return ＯＫボタンのテキスト
	 */
	@Override
	protected String getOkButtonText() {
		return Messages.getString("register");
	}

	/**
	 * キャンセルボタンテキスト取得
	 *
	 * @return キャンセルボタンのテキスト
	 */
	@Override
	protected String getCancelButtonText() {
		return Messages.getString("cancel");
	}

	/**
	 * 入力値を保持したプロパティを返します。<BR>
	 * プロパティオブジェクトのコピーを返します。
	 *
	 * @return プロパティ
	 *
	 * @see com.clustercontrol.util.PropertyUtil#copy(Property)
	 */
	@Override
	protected ValidateResult validate() {
		ValidateResult result = null;

		result = createJobSchedule();
		if (result != null) {
			return result;
		}

		return null;
	}

	/**
	 * 所属ジョブユニットのジョブIDを返します。<BR>
	 * @return 所属ジョブユニットのジョブID
	 */
	public String getJobunitId() {
		return m_jobunitId;
	}

	/**
	 * 所属ジョブユニットのジョブIDを設定します。<BR>
	 * @param jobunitId 所属ジョブユニットのジョブID
	 */
	public void setJobunitId(String jobunitId) {
		m_jobunitId = jobunitId;
	}

	@Override
	protected boolean action() {
		boolean result = false;
		try {
			String managerName = this.m_managerComposite.getText();

			JobEndpointWrapper wrapper = JobEndpointWrapper.getWrapper(managerName);
			if(mode == PropertyDefineConstant.MODE_MODIFY){
				wrapper.modifySchedule(m_jobSchedule);
				Object[] arg = {managerName};
				MessageDialog.openInformation(null, Messages.getString("successful"),
						Messages.getString("message.job.77", arg));
			}
			else {//
				wrapper.addSchedule(m_jobSchedule);
				Object[] arg = {managerName};
				MessageDialog.openInformation(null, Messages.getString("successful"),
						Messages.getString("message.job.79", arg));
			}
			result = true;
		} catch (InvalidRole_Exception e) {
			// アクセス権なしの場合、エラーダイアログを表示する
			MessageDialog.openInformation(
					null,
					Messages.getString("message"),
					Messages.getString("message.accesscontrol.16"));
		} catch (JobKickDuplicate_Exception e) {
			String[] args = {m_jobSchedule.getId()};
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.job.83",args) + " " + e.getMessage());
		} catch (InvalidUserPass_Exception e) {
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.job.74") + " " + e.getMessage());
		} catch (InvalidSetting_Exception e) {
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.job.74") + " " + e.getMessage());
		} catch (Exception e) {
			m_log.warn("action(), " + e.getMessage(), e);
			MessageDialog.openError(
					null,
					Messages.getString("failed"),
					Messages.getString("message.hinemos.failure.unexpected") + ", " + e.getMessage());
		}
		return result;
	}

	private void updateCalendarId() {
		cmpCalendarId.createCalIdCombo(this.m_managerComposite.getText(), cmpOwnerRoleId.getText());
		txtJobId.setText("");
		txtJobName.setText("");
		setJobunitId(null);
	}
}
