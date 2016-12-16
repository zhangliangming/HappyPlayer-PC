package com.happy.widget.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

import com.happy.common.BaseData;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.manage.MediaManage;
import com.happy.manage.SongInfoTipManage;
import com.happy.model.Category;
import com.happy.model.EventIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.ui.MainFrame;
import com.happy.widget.dialog.SongInfoDialog;
import com.happy.widget.panel.songlist.ListViewComItemPanel;
import com.happy.widget.panel.songlist.ListViewComPanel;
import com.happy.widget.panel.songlist.ListViewHeadPanel;
import com.happy.widget.panel.songlist.ListViewItemPanel;
import com.happy.widget.scrollbar.BaseScrollBarUI;

/**
 * 歌曲列表面板
 * 
 * @author zhangliangming
 * 
 */
public class SongListPanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 高度和宽度
	 */
	private int mWidth, mHeight;

	/**
	 * 滚动面板
	 */
	private JScrollPane jScrollPane;
	/**
	 * listview面板
	 */
	private JPanel listViewPanel;

	/**
	 * 播放列表
	 */
	private List<Category> categorys;

	private MainFrame mainFrame;

	private MainCenterPanel mainCenterPanel;

	public SongListPanel( MainFrame mainFrame,
			MainCenterPanel mainCenterPanel, int width, int height) {
		this.mainCenterPanel = mainCenterPanel;
		this.mainFrame = mainFrame;
		this.mWidth = width;
		this.mHeight = height;

		initComponent();

		// this.setBackground(Color.BLUE);
		this.setOpaque(false);// 设置透明
		ObserverManage.getObserver().addObserver(this);
	}

	/**
	 * 初始化组件
	 */
	private void initComponent() {
		this.setPreferredSize(new Dimension(mWidth, mHeight));
		this.setMaximumSize(new Dimension(mWidth, mHeight));
		this.setMinimumSize(new Dimension(mWidth, mHeight));
		//
		listViewPanel = new JPanel();
		listViewPanel.setOpaque(false);

		jScrollPane = new JScrollPane(listViewPanel);
		jScrollPane.setBorder(null);

		jScrollPane.getVerticalScrollBar().setUI(
				new BaseScrollBarUI(BaseData.listViewAlpha));
		jScrollPane.getVerticalScrollBar().setOpaque(false);
		jScrollPane.getVerticalScrollBar().setUnitIncrement(30);
		// 不显示水平的滚动条
		jScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setOpaque(false);
		jScrollPane.getViewport().setOpaque(false);
		// jScrollPane.getViewport().setBackground(new Color(255, 255, 255, 0));

		//
		this.setLayout(new BorderLayout());
		this.add(jScrollPane, "Center");

		//
		listViewPanel.setLayout(new BoxLayout(listViewPanel, BoxLayout.Y_AXIS));
		loadData();
	}

	/**
	 * 
	 */
	public void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// 消除线条锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// 设定渐变 -滑过区域的颜色设置
		g2d.setPaint(new Color(255, 255, 255, BaseData.listViewAlpha));
		g2d.fillRect(0, 0, mWidth, mHeight);
	}

	/**
	 * 加载歌曲列表数据
	 */
	private void loadData() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				// 遍历数据，生成界面
				categorys = MediaManage.getMediaManage().getCategorys();
				return null;

			}

			@Override
			protected void done() {
				initComponentData();
			}
		}.execute();
	}

	/**
	 * 初始化控件数据
	 */
	protected void initComponentData() {
		for (int i = 0; i < categorys.size(); i++) {
			Category category = categorys.get(i);
			String titleName = category.getCategoryName();
			// 获取该列表下的歌曲数据
			List<SongInfo> songInfos = category.getSongInfos();
			// 列表内容
			ListViewComPanel listViewComPanel = new ListViewComPanel();
			listViewComPanel.setVisible(false);
			// 列表头
			ListViewHeadPanel listViewHeadPanel = new ListViewHeadPanel(
					listViewPanel, this, listViewComPanel, songInfos, mWidth,
					titleName, category.getCid(), i);

			// 遍历列表下的歌曲
			for (int j = 0; j < songInfos.size(); j++) {
				SongInfo songInfo = songInfos.get(j);

				refreshLVComPanelUI(category.getCid(), listViewComPanel,
						songInfo);
			}

			// listviewitem面板
			ListViewItemPanel itemPanel = new ListViewItemPanel();
			itemPanel.add(listViewHeadPanel, 0);
			itemPanel.add(listViewComPanel, 1);

			listViewPanel.add(itemPanel);
		}
	}

	/**
	 * 显示或者隐藏列表
	 * 
	 * @param eventIntent
	 */
	private void showORHidePlayList(EventIntent eventIntent) {

		// 获取点击的列表id
		String clickPlId = eventIntent.getpLId();

		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
					.getComponent(i);
			ListViewHeadPanel listViewItemHeadPanel = (ListViewHeadPanel) itemPanel
					.getComponent(0);
			String pLId = listViewItemHeadPanel.getpLId();
			if (pLId.equals(clickPlId)) {
				// 获取点击面板的展开状态
				boolean isShow = listViewItemHeadPanel.getShow();
				if (isShow) {
					listViewItemHeadPanel.setShow(false);
				} else {
					listViewItemHeadPanel.setShow(true);
				}
			} else {
				listViewItemHeadPanel.setShow(false);
			}
		}
		// 设置当前已展开的索引
		BaseData.pLId = clickPlId;
	}

	/**
	 * 刷新播放列表下的歌曲
	 * 
	 * @param playListId
	 *            播放列表索引
	 * @param listViewComPanel
	 *            歌曲内容列表面板
	 * @param songInfo
	 *            歌曲数据
	 */
	private void refreshLVComPanelUI(String playListId,
			ListViewComPanel listViewComPanel, SongInfo songInfo) {
		ListViewComItemPanel listViewItemComItemPanel = new ListViewComItemPanel(
				this, listViewPanel, playListId, songInfo, mWidth);
		listViewComPanel.add(listViewItemComItemPanel);
	}

	@Override
	public void update(Observable o, Object data) {
		if (data instanceof EventIntent) {
			EventIntent eventIntent = (EventIntent) data;
			// 播放列表点击
			if (eventIntent.getEventType() == EventIntent.PLAYLIST) {
				showORHidePlayList(eventIntent);
			} else if (eventIntent.getEventType() == EventIntent.SONGLIST) {
				refreshListViewComItemPanelUI(eventIntent);
			}
		} else if (data instanceof SongMessage) {
			udateListViewComItemPanelUI(data);
		}
	}

	/**
	 * 更新进度条
	 * 
	 * @param data
	 */
	private void udateListViewComItemPanelUI(Object data) {
		// 获取双击的列表下的歌曲id

		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
					.getComponent(i);
			ListViewComPanel listViewComPanel = (ListViewComPanel) itemPanel
					.getComponent(1);
			for (int j = 0; j < listViewComPanel.getComponentCount(); j++) {
				ListViewComItemPanel listViewComItemPanel = (ListViewComItemPanel) listViewComPanel
						.getComponent(j);
				String sId = listViewComItemPanel.getsId();
				if (sId.equals(BaseData.playInfoID)) {
					if (listViewComItemPanel.getSongProgress() != null) {
						SongMessage songMessage = (SongMessage) data;
						if (songMessage.getType() == SongMessage.INITMUSIC
								|| songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC
								|| songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC
								|| songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC
								|| songMessage.getType() == SongMessage.ERRORMUSIC
								|| songMessage.getType() == SongMessage.SERVICEERRORMUSIC) {
							SongInfo mSongInfo = songMessage.getSongInfo();
							if (mSongInfo != null) {
								listViewComItemPanel.getSongProgress().setText(
										TimeUtils.parseString((int) mSongInfo
												.getPlayProgress())
												+ "/"
												+ mSongInfo.getDurationStr());
								listViewComItemPanel.updateUI();
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 刷新列表内容
	 * 
	 * @param eventIntent
	 */
	private void refreshListViewComItemPanelUI(EventIntent eventIntent) {
		if (eventIntent.getMouseType() == EventIntent.SINGLECLICK) {
			// 单击
			refreshListViewComItemSingleClickPanelUI(eventIntent);
		} else if (eventIntent.getMouseType() == EventIntent.DOUBLECLICK) {
			// 双击
			refreshListViewComItemDoubleClickPanelUI(eventIntent);
		} else if (eventIntent.getMouseType() == EventIntent.ENTERED) {
			// 鼠标进入
			showSongInfoTipDialog(eventIntent);
		} else if (eventIntent.getMouseType() == EventIntent.EXITED) {
			// 鼠标退出
			hideSongInfoTipDialog();
		} else if (eventIntent.getMouseType() == EventIntent.RESET) {
			// 还原
			refreshListViewComItemResetPanelUI(eventIntent);
		}
	}

	/**
	 * 双击还原
	 * 
	 * @param eventIntent
	 */
	private void refreshListViewComItemResetPanelUI(EventIntent eventIntent) {
		// 获取双击的列表下的歌曲id
		String doubleClickSId = eventIntent.getsId();

		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
					.getComponent(i);
			ListViewComPanel listViewComPanel = (ListViewComPanel) itemPanel
					.getComponent(1);
			for (int j = 0; j < listViewComPanel.getComponentCount(); j++) {
				ListViewComItemPanel listViewComItemPanel = (ListViewComItemPanel) listViewComPanel
						.getComponent(j);
				String sId = listViewComItemPanel.getsId();
				if (sId.equals(doubleClickSId)) {
					listViewComItemPanel.setDoubSelect(false);
				}
			}
		}
	}

	/**
	 * 隐藏歌曲信息窗口
	 */
	private void hideSongInfoTipDialog() {
		SongInfoTipManage.hideSongInfoTipDialog();
	}

	/**
	 * 显示歌曲信息窗口
	 * 
	 * @param eventIntent
	 */
	private void showSongInfoTipDialog(EventIntent eventIntent) {
		ListViewComItemPanel listViewComItemPanel = null;
		String enterClickSId = eventIntent.getsId();
		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
					.getComponent(i);
			ListViewComPanel listViewComPanel = (ListViewComPanel) itemPanel
					.getComponent(1);
			for (int j = 0; j < listViewComPanel.getComponentCount(); j++) {
				ListViewComItemPanel temp = (ListViewComItemPanel) listViewComPanel
						.getComponent(j);
				String sId = temp.getsId();
				if (sId.equals(enterClickSId)) {
					listViewComItemPanel = temp;
					break;
				}
			}
		}
		if (listViewComItemPanel == null) {
			SongInfoTipManage.hideSongInfoTipDialog();
			return;
		}
		SongInfo songInfo = listViewComItemPanel.getSongInfo();
		if (songInfo == null) {
			SongInfoTipManage.hideSongInfoTipDialog();
			return;
		}
		SongInfoDialog songInfoDialog = SongInfoTipManage.getSongInfoTipManage(
				56 * 5, 26 * 3).getSongInfoDialog();
		songInfoDialog.updateUI(songInfo);

		//
		Point thisPoint = mainCenterPanel.getSongListPanel()
				.getLocationOnScreen();

		// 当前控件的位置
		Point componentPoint = listViewComItemPanel.getLocationOnScreen();
		int componentY = componentPoint.y;
		int dialogH = songInfoDialog.getHeight();

		Point framePoint = mainFrame.getLocationOnScreen();
		int frameY = framePoint.y;
		int frameH = mainFrame.getHeight();

		int y = componentY;
		if (y < thisPoint.y) {
			y = thisPoint.y;
		} else if (componentY + dialogH > frameY + frameH
				- mainFrame.getMainOperatePanel().getHeight()) {
			// 歌曲提示信息窗口位置不够，往上移动
			y = frameY + frameH - dialogH
					- mainFrame.getMainOperatePanel().getHeight() - 5;
		}
		songInfoDialog.setLocation(mainFrame.getX() + mWidth + 5, y);
		SongInfoTipManage.showSongInfoTipDialog();

	}

	/**
	 * 歌曲item列表双击样式
	 * 
	 * @param eventIntent
	 */
	private void refreshListViewComItemDoubleClickPanelUI(
			EventIntent eventIntent) {
		// 获取双击的列表下的歌曲id
		String doubleClickSId = eventIntent.getsId();

		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
					.getComponent(i);
			ListViewComPanel listViewComPanel = (ListViewComPanel) itemPanel
					.getComponent(1);
			for (int j = 0; j < listViewComPanel.getComponentCount(); j++) {
				ListViewComItemPanel listViewComItemPanel = (ListViewComItemPanel) listViewComPanel
						.getComponent(j);
				String sId = listViewComItemPanel.getsId();
				if (sId.equals(doubleClickSId)) {
					listViewComItemPanel.setDoubSelect(true);
				} else {
					if (sId.equals(BaseData.playInfoID)) {
						listViewComItemPanel.setDoubSelect(false);
					}
				}
			}
		}
		BaseData.playInfoID = doubleClickSId;
		BaseData.playInfoPID = eventIntent.getpLId();
	}

	/**
	 * 歌曲item列表单击样式
	 * 
	 * @param eventIntent
	 */
	private void refreshListViewComItemSingleClickPanelUI(
			EventIntent eventIntent) {

		// 获取点击的列表下的歌曲id
		String clickSId = eventIntent.getsId();

		for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
			ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
					.getComponent(i);
			ListViewComPanel listViewComPanel = (ListViewComPanel) itemPanel
					.getComponent(1);
			for (int j = 0; j < listViewComPanel.getComponentCount(); j++) {
				ListViewComItemPanel listViewComItemPanel = (ListViewComItemPanel) listViewComPanel
						.getComponent(j);
				String sId = listViewComItemPanel.getsId();
				if (sId.equals(clickSId)) {
					listViewComItemPanel.setSingleSelect(true);
				} else {
					if (sId.equals(BaseData.sId)) {
						listViewComItemPanel.setSingleSelect(false);
					}
				}
			}
		}
		BaseData.sId = clickSId;
	}
}
