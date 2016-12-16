package com.happy.widget.panel.songlist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.db.SongInfoDB;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.manage.MediaManage;
import com.happy.model.EventIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.widget.button.ImageButton;
import com.happy.widget.panel.SongListPanel;

/**
 * 歌曲列表内容Item面板
 * 
 * @author zhangliangming
 * 
 */
public class ListViewComItemPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 歌曲信息
	 */
	private SongInfo songInfo;

	/**
	 * 默认高度
	 */
	private int defHeight = 50;
	/**
	 * 点击高度
	 */
	private int selectHeight = 70;
	/**
	 * 高度
	 */
	private int height = defHeight;

	/**
	 * 宽度
	 */
	private int width = 0;

	/**
	 * 播放列表面板
	 */
	private SongListPanel songListPanel;

	/**
	 * 鼠标经过
	 */
	private boolean isEnter = false;

	/**
	 * 双选
	 */
	private boolean isDoubSelect = false;
	/**
	 * 单选
	 */
	private boolean isSingleSelect = false;

	/**
	 * 面板鼠标事件
	 */
	private PanelMouseListener panelMouseListener = new PanelMouseListener();
	/**
	 * 播放列表索引
	 */
	private String playListId;
	/**
	 * 歌曲列表索引
	 */
	private String sId;
	/**
	 * 歌曲名称
	 */
	private JLabel songName;

	/**
	 * 歌曲长度
	 */
	private JLabel songSize;
	/**
	 * 播放进度
	 */
	private JLabel songProgress;
	/**
	 * 歌手图片
	 */
	private JLabel singerIconLabel;
	/**
	 * 删除按钮
	 */
	private ImageButton delButton;
	/**
	 * 
	 */
	private JPanel listViewPanel;
	/**
	 * 是否进入控件
	 */
	private boolean isEnterComponent = false;

	public ListViewComItemPanel(SongListPanel songListPanel,
			JPanel listViewPanel, String playListId, SongInfo songInfo,
			int mWidth) {
		this.playListId = playListId;
		this.sId = songInfo.getSid();
		this.songListPanel = songListPanel;
		this.listViewPanel = listViewPanel;
		this.width = mWidth;
		this.songInfo = songInfo;

		this.addMouseListener(panelMouseListener);
		initComponent();
		this.setOpaque(false);
	}

	private void initComponent() {
		this.setLayout(null);

		// 如果歌曲id == 当前的歌曲id，则显示该样式为双击样式
		if (songInfo.getSid().equals(BaseData.playInfoID)) {

			// 设置当前的播放歌曲数据
			MediaManage.getMediaManage().setSongInfo(songInfo);
			
			// 获取当前播放歌曲数据，通知其它界面更新ui

			SongMessage msg = new SongMessage();
			msg.setSongInfo(songInfo);
			msg.setType(SongMessage.INITMUSIC);
			ObserverManage.getObserver().setMessage(msg);

			isDoubSelect = true;
			initDoubSelectedComponent();
		} else {
			isDoubSelect = false;
			initDefComponent();
		}
	}

	/**
	 * 初始化默认的列表样式
	 */
	private void initDefComponent() {

		this.removeAll();
		height = defHeight;
		this.setPreferredSize(new Dimension(width, height));
		this.setMaximumSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(width, height));
		songName = new JLabel(songInfo.getDisplayName());
		songSize = new JLabel(songInfo.getDurationStr());

		songName.setBounds(10, 0, width / 2, height);
		songSize.setBounds(width - 60 - 15, 0, 60, height);

		delButton = new ImageButton(defHeight / 2 + 5, defHeight / 2,
				"del_def.png", "del_rollover.png", "del_pressed");
		delButton.setBounds(songSize.getX() - (defHeight / 2 + 10),
				(defHeight - defHeight / 2) / 2, defHeight / 2 + 5,
				defHeight / 2);
		delButton.setToolTipText("删除");
		delButton.setVisible(false);

		initDelButtonEvent();

		this.add(delButton);

		this.add(songName);
		this.add(songSize);

		songListPanel.updateUI();

	}

	/**
	 * 初始化双击布局
	 */
	private void initDoubSelectedComponent() {
		this.removeAll();
		height = selectHeight;
		this.setPreferredSize(new Dimension(width, height));
		this.setMaximumSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(width, height));

		songName = new JLabel(songInfo.getDisplayName());
		songSize = new JLabel(songInfo.getDurationStr());

		String progressTime = "00:00";
		SongInfo tempSongInfo = MediaManage.getMediaManage().getSongInfo();
		if (tempSongInfo != null) {
			progressTime = TimeUtils.parseString((int) tempSongInfo
					.getPlayProgress());
		}
		songProgress = new JLabel(progressTime + "/"
				+ songInfo.getDurationStr());

		String singerIconPath = Constants.PATH_ICON + File.separator
				+ "ic_launcher.png";
		ImageIcon singerIcon = new ImageIcon(singerIconPath);
		singerIcon.setImage(singerIcon.getImage().getScaledInstance(
				height * 3 / 4, height * 3 / 4, Image.SCALE_SMOOTH));
		singerIconLabel = new JLabel(singerIcon);

		singerIconLabel.setBounds(10, (height - height * 3 / 4) / 2,
				height * 3 / 4, height * 3 / 4);

		songName.setBounds(
				10 + singerIconLabel.getX() + singerIconLabel.getWidth(),
				singerIconLabel.getHeight() / 4 - 5, width - 10, height / 2);

		songSize.setBounds(width - 60 - 15, 0, 60, height);
		songSize.setVisible(false);

		songProgress.setBounds(
				singerIconLabel.getX() + singerIconLabel.getWidth() + 10,
				songName.getY() + 25, 12 * 50, height / 2);

		delButton = new ImageButton(defHeight / 2 + 5, defHeight / 2,
				"del_def.png", "del_rollover.png", "del_pressed");
		delButton.setBounds(songSize.getX() - (defHeight / 2 + 10), height / 2
				+ (defHeight - defHeight / 2) / 2 - 2, defHeight / 2 + 5,
				defHeight / 2);
		delButton.setToolTipText("删除");

		initDelButtonEvent();
		this.add(delButton);
		this.add(singerIconLabel);
		this.add(songName);
		this.add(songSize);
		this.add(songProgress);
		songListPanel.updateUI();
	}

	/**
	 * 初始化删除按钮事件
	 */
	private void initDelButtonEvent() {
		delButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread() {

					@Override
					public void run() {
						int result = JOptionPane.showConfirmDialog(
								ListViewComItemPanel.this,
								"删除【" + songInfo.getDisplayName() + "】歌曲吗?",
								"确认", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.INFORMATION_MESSAGE);
						if (result == JOptionPane.OK_OPTION) {
							// 执行删除歌曲操作
							delSongPanel();
							// 删除歌曲数据库数据
							delSongDBData();
							// 删除歌曲数据
							delSongData();
							// 更新标题栏的歌曲总数面板
							updatePLHTitlePanel();
							// 判断是否是当前正在播放的歌曲
							if (sId.equals(BaseData.sId)) {

								MediaManage.getMediaManage().stopToPlay();
								MediaManage.getMediaManage().setSongInfo(null);

							}
						}
					}

					/**
					 * 更新标题栏的歌曲总数面板
					 */
					private void updatePLHTitlePanel() {
						for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
							ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
									.getComponent(i);
							ListViewHeadPanel listViewHeadPanel = (ListViewHeadPanel) itemPanel
									.getComponent(0);
							if (listViewHeadPanel.getpLId().equals(playListId)) {
								//
								List<SongInfo> songInfos = MediaManage
										.getMediaManage().getSongInfoList(
												playListId);
								listViewHeadPanel.getTitleNameJLabel().setText(
										listViewHeadPanel.getTitleName() + "["
												+ songInfos.size() + "]");
								listViewHeadPanel.updateUI();
								break;
							}
						}
					}

					/**
					 * 删除歌曲数据
					 */
					private void delSongData() {
						MediaManage.getMediaManage().delSongInfo(playListId,
								sId);
					}

					/**
					 * 删除歌曲数据库数据
					 */
					private void delSongDBData() {
						SongInfoDB.getSongInfoDB().delete(sId);
					}

					/**
					 * 删除歌曲面板
					 */
					private void delSongPanel() {

						for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
							ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
									.getComponent(i);
							ListViewHeadPanel listViewHeadPanel = (ListViewHeadPanel) itemPanel
									.getComponent(0);
							if (listViewHeadPanel.getpLId().equals(playListId)) {
								ListViewComPanel listViewComPanel = (ListViewComPanel) itemPanel
										.getComponent(1);
								for (int j = 0; j < listViewComPanel
										.getComponentCount(); j++) {
									ListViewComItemPanel listViewComItemPanel = (ListViewComItemPanel) listViewComPanel
											.getComponent(j);
									if (sId.equals(listViewComItemPanel
											.getsId())) {
										// 找到该歌曲所在的面板，进行移除
										listViewComPanel.remove(j);
										listViewComPanel.updateUI();
										break;
									}
								}
							}
						}
					}
				}.start();
			}
		});

		// 删除按钮鼠标事件
		delButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				panelMouseListener.mouseReleased(e);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				panelMouseListener.mousePressed(e);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				panelMouseListener.mouseExited(e);
				isEnterComponent = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				isEnterComponent = true;
				panelMouseListener.mouseEntered(e);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				panelMouseListener.mouseClicked(e);
			}
		});
	}

	/**
	 * 面板鼠标事件
	 * 
	 * @author zhangliangming
	 * 
	 */
	class PanelMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (isEnterComponent) {
				songListPanel.repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (isEnterComponent) {
				songListPanel.repaint();
				return;
			}

			if (e.getClickCount() == 1) {

				// 如果当前歌曲正在播放，则对其它进行暂停操作

				if (songInfo.getSid().equals(BaseData.playInfoID)) {

					if (MediaManage.getMediaManage().getPlayStatus() == MediaManage.PLAYING) {
						// 当前正在播放，发送暂停
						SongMessage msg = new SongMessage();
						msg.setSongInfo(songInfo);
						msg.setType(SongMessage.PAUSEMUSIC);
						ObserverManage.getObserver().setMessage(msg);
					} else {

						SongMessage songMessage = new SongMessage();
						songMessage.setType(SongMessage.PLAYMUSIC);
						// 通知
						ObserverManage.getObserver().setMessage(songMessage);
					}
				}

				EventIntent eventIntent = new EventIntent();
				eventIntent.setEventType(EventIntent.SONGLIST);
				eventIntent.setpLId(playListId);
				eventIntent.setsId(sId);
				eventIntent.setMouseType(EventIntent.SINGLECLICK);

				ObserverManage.getObserver().setMessage(eventIntent);

			}

			if (e.getClickCount() == 2) {

				if (songInfo.getSid().equals(BaseData.playInfoID)) {

					return;
				} else {
					EventIntent eventIntent = new EventIntent();
					eventIntent.setEventType(EventIntent.SONGLIST);
					eventIntent.setpLId(playListId);
					eventIntent.setsId(sId);
					eventIntent.setMouseType(EventIntent.DOUBLECLICK);

					ObserverManage.getObserver().setMessage(eventIntent);
				}

				BaseData.playInfoPID = playListId;
				BaseData.playInfoID = songInfo.getSid();
				// 发送播放
				SongMessage msg = new SongMessage();
				msg.setSongInfo(songInfo);
				msg.setType(SongMessage.PLAYINFOMUSIC);
				ObserverManage.getObserver().setMessage(msg);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (isEnterComponent) {
				songListPanel.repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			isEnter = true;
			if (isEnterComponent) {

				delButton.setVisible(true);
				songListPanel.repaint();

			} else {
				if (isDoubSelect) {
					initDoubSelectedComponent();
				} else {
					delButton.setVisible(true);
					songListPanel.repaint();
				}

				EventIntent eventIntent = new EventIntent();
				eventIntent.setEventType(EventIntent.SONGLIST);
				eventIntent.setpLId(playListId);
				eventIntent.setsId(sId);
				eventIntent.setMouseType(EventIntent.ENTERED);

				ObserverManage.getObserver().setMessage(eventIntent);

			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (isEnterComponent) {
				isEnter = false;
				songListPanel.repaint();
				if (!isDoubSelect) {
					delButton.setVisible(false);
				}
			} else {
				if (isDoubSelect) {
					initDoubSelectedComponent();
				} else {
					isEnter = false;
					delButton.setVisible(false);
					songListPanel.repaint();
				}

				EventIntent eventIntent = new EventIntent();
				eventIntent.setEventType(EventIntent.SONGLIST);
				eventIntent.setpLId(playListId);
				eventIntent.setsId(sId);
				eventIntent.setMouseType(EventIntent.EXITED);

				ObserverManage.getObserver().setMessage(eventIntent);
			}
		}
	}

	public boolean getSingleSelect() {
		return isSingleSelect;
	}

	public boolean getDoubSelect() {
		return isDoubSelect;
	}

	/**
	 * 单击
	 * 
	 * @param isSingleSelect
	 */
	public void setSingleSelect(boolean isSingleSelect) {
		this.isSingleSelect = isSingleSelect;
		songListPanel.revalidate();
		songListPanel.repaint();
	}

	/**
	 * 
	 * @param isDoubSelect
	 */
	public void setDoubSelect(boolean isDoubSelect) {
		this.isDoubSelect = isDoubSelect;
		if (isDoubSelect) {
			isSingleSelect = false;
			initDoubSelectedComponent();
		} else {
			isEnter = false;
			isSingleSelect = false;
			initDefComponent();
		}
	}

	// 绘制组件
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// 消除线条锯齿
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (isDoubSelect) {
			g2d.setPaint(new Color(0, 0, 0, 80));
		} else if (isSingleSelect) {
			g2d.setPaint(new Color(0, 0, 0, 50));
		} else if (isEnter) {
			g2d.setPaint(new Color(0, 0, 0, 20));
		} else {
			g2d.setPaint(new Color(0, 0, 0, 0));
		}
		g2d.fillRect(0, 0, width, height);
	}

	public SongInfo getSongInfo() {
		return songInfo;
	}

	/**
	 * 获取进度条控件
	 * 
	 * @return
	 */
	public JLabel getSongProgress() {
		return songProgress;
	}

	public String getsId() {
		return sId;
	}

}
