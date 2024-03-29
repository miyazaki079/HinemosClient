package com.clustercontrol.startup.composite;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import com.clustercontrol.ClusterControlPlugin;
import com.clustercontrol.startup.bean.StartUpItem;
import com.clustercontrol.startup.figure.StartUpFigure;

public class StartUpComposite extends Composite{

	// ログ
	private static Log m_log = LogFactory.getLog( StartUpComposite.class );

	// 描画対象のマップの情報を保持したモデル
	List<StartUpItem> m_startUpItem;

	//図形を配置するキャンバス
	private FigureCanvas m_canvas;

	//図形を配置するパネル
	private Panel m_panel;
	//ラベルタイトル
	private Label m_labelTitle = null;

	private Color m_backGround = new Color(null, 224, 226, 237);
	private Color m_labelColor = new Color(null, 0, 63, 133);

	/**
	 * フォントは何度もnewするとリークするので、複数定義しない。
	 */
	private static Font titleLabelFont = null;
	public static Font topLabelFont = null;
	public static Font bottomLabelFont = null;


	// モデルと図の関係を保持するマップ
	// 描画対象スコープ、ノードのファシリティIDとそれを描画している図（Figure）のリファレンスを保持
	private ConcurrentHashMap<String, StartUpFigure> m_figureMap = new ConcurrentHashMap<String, StartUpFigure>();

	/**
	 * インスタンスを返します。
	 *
	 * @param parent
	 *            親のコンポジット
	 * @param style
	 *            スタイル
	 */
	public StartUpComposite(Composite parent, int style, List<StartUpItem> m_startupitem) {
		super(parent, style);
		m_startUpItem = m_startupitem;
		initialize();
	}

	private void initialize() {
		// キャンバス表示コンポジットをparentの残り領域全体に拡張して表示
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.setLayoutData(gridData);

		// キャンバスコンポジット内のレイアウトを設定
		this.setLayout(new FillLayout());

		// 図を配置するキャンバスを生成
		m_canvas = new FigureCanvas(this, SWT.NONE);

		// 背景は白
		m_canvas.setBackground(ColorConstants.white);

		//パネル作成
		m_panel = new Panel();
		m_panel.setBackgroundColor(m_backGround);
		m_canvas.setContents(m_panel);
		m_panel.setLayoutManager(new XYLayout());

		/*
		 * フォントは何度もnewするとリークするので、複数定義しない。
		 */
		if (titleLabelFont == null) {
			if(ClusterControlPlugin.isRAP()) {
				titleLabelFont = new Font(getDisplay(), "Arial", 32, SWT.BOLD);
			} else {
				titleLabelFont = new Font(getDisplay(), "Arial", 24, SWT.BOLD);
			}

		}
		if (topLabelFont == null) {
			if(ClusterControlPlugin.isRAP()) {
				topLabelFont = new Font(getDisplay(), "Arial", 14, SWT.BOLD);
			} else {
				topLabelFont = new Font(getDisplay(), "Arial", 14, SWT.BOLD);
			}
		}
		if (bottomLabelFont == null) {
			if(ClusterControlPlugin.isRAP()) {
				bottomLabelFont = new Font(getDisplay(), "Arial", 12, SWT.NONE);
			} else {
				bottomLabelFont = new Font(getDisplay(), "Arial", 11, SWT.NONE);
			}
		}

	}

	private StartUpFigure drawFigure(StartUpItem startUpItem, int nest) {
		// 図を生成する
		StartUpFigure figure = new StartUpFigure();
		figure.draw(startUpItem, nest);

		// モデルとマップの関係を保持
		this.putStartUpFigure(startUpItem.getFieldId(), figure);

		// 配置情報の生成
		Point point = null;
		point = new Point(startUpItem.getPosX(), startUpItem.getPosY());

		// サイズは情報がないので、-1を設定
		Dimension dimension = new Dimension(-1, -1);
		Rectangle rectangle = new Rectangle(point, dimension);

		// 図を描画する
		m_panel.add(figure);
		m_panel.setConstraint(figure, rectangle);
		// マウスイベントを登録する
		MouseEventListener listener = new MouseEventListener();
		figure.addMouseListener(listener);
		figure.addMouseMotionListener(listener);

		return figure;
	}

	// draw2D Figure用のイベントリスナ
	private class MouseEventListener extends MouseMotionListener.Stub implements MouseListener {


		@Override
		public void mouseDoubleClicked(MouseEvent me) {
			// イベントを消費
			me.consume();
		}

		@Override
		public void mousePressed(MouseEvent me) {
			// イベントを消費
			me.consume();
		}

		@Override
		public void mouseEntered(MouseEvent me) {
			Figure figure = (Figure)me.getSource();
			if (figure instanceof StartUpFigure) {
				StartUpItem startupItem = m_startUpItem.get(((StartUpFigure) figure).getNest());
				if(startupItem.getPerspectiveName() != null) {
					setCursor(Cursors.HAND);
				}
			}
			// イベントを消費
			me.consume();
		}

		@Override
		public void mouseExited(MouseEvent me) {
			Figure figure = (Figure)me.getSource();
			if (figure instanceof StartUpFigure) {
				setCursor(null);
			}
			// イベントを消費
			me.consume();
		}

		// クリック時に呼ばれる
		// クリックするとパースペクティブを表示する。
		@Override
		public void mouseReleased(MouseEvent me) {
			Figure figure = (Figure)me.getSource();
			if (figure instanceof StartUpFigure) {
				StartUpItem startupItem = null;
				startupItem = m_startUpItem.get(((StartUpFigure) figure).getNest());

				if(startupItem.getPerspectiveName() == null) {
					me.consume();
					return;
				}
				try {
					IPerspectiveDescriptor perspective = PlatformUI.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(startupItem.getPerspectiveName());

					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(perspective);
				} catch (Exception e) {
					m_log.warn("mouseReleased() page.showView, " + e.getMessage(), e);
				}
			}
			// イベントを消費
			me.consume();

		}

		@Override
		public void mouseDragged(MouseEvent me) {
			// イベントを消費
			me.consume();
		}
	}

	@Override
	public void update() {
		m_labelTitle = new Label(m_startUpItem.get(0).getMsgTextTop());
		m_labelTitle.setVisible(true);

		m_labelTitle.setFont(titleLabelFont);
		m_labelTitle.setForegroundColor(m_labelColor);
		m_panel.add(m_labelTitle);
		Dimension dimension = new Dimension(-1, -1);
		Point point = new Point(8, 8);
		Rectangle zeroRectangle = new Rectangle(point, dimension);
		m_panel.setConstraint(m_labelTitle, zeroRectangle);
		m_canvas.setContents(m_panel);
		this.updateMap();
	}

	private void updateMap() {
		m_figureMap.clear();
		// タイトル以外（パースペクティブ説明）のみ描画。
		for (int i=1; i < m_startUpItem.size(); i++) {
			this.drawFigure(m_startUpItem.get(i), i);
		}
	}

	private void putStartUpFigure (String field, StartUpFigure figure) {
		m_figureMap.put(field, figure);
	}

}
