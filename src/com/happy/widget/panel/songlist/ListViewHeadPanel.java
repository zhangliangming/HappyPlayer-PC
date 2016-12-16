package com.happy.widget.panel.songlist;

import java.awt.BasicStroke;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.db.CategoryDB;
import com.happy.db.SongInfoDB;
import com.happy.manage.MediaManage;
import com.happy.model.Category;
import com.happy.model.EventIntent;
import com.happy.model.SongInfo;
import com.happy.observable.ObserverManage;
import com.happy.util.AudioFilter;
import com.happy.util.DateUtil;
import com.happy.util.IDGenerate;
import com.happy.util.MediaUtils;
import com.happy.widget.button.ImageButton;
import com.happy.widget.panel.SongListPanel;

/**
 * 歌曲列表头面板
 * 
 * @author zhangliangming
 * 
 */
public class ListViewHeadPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 高度
	 */
	private int height = 40;

	/**
	 * 宽度
	 */
	private int width = 0;
	/**
	 * 标题
	 */
	private String titleName;

	/**
	 * 歌曲数目
	 */
	private int size = 0;
	/**
	 * 
	 */
	private ImageIcon leftIcon;
	/**
	 * 
	 */
	private ImageIcon downIcon;

	/**
	 * 不展开图标标志
	 */
	private JLabel statusLeftJLabel;

	/**
	 * 展开图标标志
	 */
	private JLabel statusDownJLabel;

	/**
	 * 标题标签
	 */
	private JLabel titleNameJLabel;
	/**
	 * 判断是否展开
	 */
	private boolean isShow = false;

	/**
	 * 菜单按钮
	 */
	private ImageButton menuButton;

	/**
	 * 定义添加按钮的弹出菜单
	 */
	private JPopupMenu addPop;

	/**
	 * 定义一个添加歌曲菜单
	 */
	private JMenuItem addSongMenu;

	/**
	 * 定义一个添加歌曲文件菜单
	 */
	public JMenuItem addSongFiledMenu;
	/**
	 * 删除播放列表菜单
	 */
	public JMenuItem delPlaylistMenu;

	/**
	 * 歌曲文件数组
	 */
	private File[] songfiles;

	/**
	 * 播放列表索引
	 */
	private String pLId;
	/**
	 * 列表内容面板
	 */
	private ListViewComPanel listViewComPanel;
	/**
	 * 该列表下的所有歌曲列表
	 */
	private SongListPanel songListPanel;

	private JPanel listViewPanel;

	public ListViewHeadPanel(JPanel listViewPanel, SongListPanel songListPanel,
			ListViewComPanel listViewComPanel, List<SongInfo> songInfos,
			int mWidth, String titleName, String cid, int pIndex) {
		this.listViewPanel = listViewPanel;
		this.songListPanel = songListPanel;
		this.listViewComPanel = listViewComPanel;
		this.width = mWidth;
		this.titleName = titleName;
		this.size = songInfos.size();
		this.pLId = cid;
		this.setPreferredSize(new Dimension(width, height));
		this.setMaximumSize(new Dimension(width, height));
		this.setMinimumSize(new Dimension(width, height));
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {

				EventIntent eventIntent = new EventIntent();
				eventIntent.setEventType(EventIntent.PLAYLIST);
				eventIntent.setpLId(pLId);

				ObserverManage.getObserver().setMessage(eventIntent);

			}

			@Override
			public void mouseReleased(MouseEvent e) {

			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});
		initComponent(pIndex);
		// 如果是上一次展开的播放列表
		if (BaseData.playInfoPID.equals(cid)) {
			isShow = true;
			listViewComPanel.setVisible(true);
		}
		this.setOpaque(false);
	}

	/**
	 * 初始化控件
	 * 
	 * @param pIndex
	 * 
	 */
	private void initComponent(int pIndex) {
		this.setLayout(null);

		String leftIconPath = Constants.PATH_ICON + File.separator
				+ "arrow_left.png";
		leftIcon = new ImageIcon(leftIconPath);
		leftIcon.setImage(leftIcon.getImage().getScaledInstance(height / 3,
				height / 3, Image.SCALE_SMOOTH));

		String downIconPath = Constants.PATH_ICON + File.separator
				+ "arrow_down.png";
		downIcon = new ImageIcon(downIconPath);
		downIcon.setImage(downIcon.getImage().getScaledInstance(height / 3,
				height / 3, Image.SCALE_SMOOTH));

		statusLeftJLabel = new JLabel(leftIcon);
		statusLeftJLabel.setBounds(10, (height - height / 3) / 2, height / 3,
				height / 3);

		statusDownJLabel = new JLabel(downIcon);
		statusDownJLabel.setBounds(10, (height - height / 3) / 2, height / 3,
				height / 3);
		statusDownJLabel.setVisible(false);

		titleNameJLabel = new JLabel(titleName + " [" + size + "]");
		titleNameJLabel.setBounds(
				statusLeftJLabel.getX() + statusLeftJLabel.getWidth() + 10,
				(height - height / 2) / 2, width / 2, height / 2);

		menuButton = new ImageButton(height / 2, height / 2,
				"list_btn_def.png", "list_btn_rollover.png",
				"list_btn_pressed.png");
		menuButton.setToolTipText("列表菜单");
		menuButton.setBounds(width - height, (height - height / 2) / 2,
				height / 2, height / 2);

		addPop = new JPopupMenu();
		addSongMenu = new JMenuItem("添加歌曲");
		addSongFiledMenu = new JMenuItem("添加歌曲文件");

		delPlaylistMenu = new JMenuItem("删除播放列表");

		addPop.add(addSongMenu);
		addPop.add(addSongFiledMenu);
		if (pIndex != 0) {
			addPop.addSeparator();
			addPop.add(delPlaylistMenu);
		}

		//
		menuButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// new Thread() {
				//
				// @Override
				// public void run() {
				addPop.show(menuButton, 0, height / 2);
				// }
				// }.start();
			}
		});

		//
		menuButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				songListPanel.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				songListPanel.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				songListPanel.repaint();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				songListPanel.repaint();
			}

			@Override
			public void mouseClicked(MouseEvent e) {

			}
		});

		addSongMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// new Thread() {
				//
				// @Override
				// public void run() {

				JFileChooser songfile = new JFileChooser();
				songfile.setMultiSelectionEnabled(true);
				songfile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				songfile.showOpenDialog(ListViewHeadPanel.this);
				if (songfile.getSelectedFile() != null) {
					String playlistname = JOptionPane.showInputDialog(
							ListViewHeadPanel.this, "请输入歌曲列表名", "新建列表");
					if (playlistname == null)
						return;
					if (playlistname.equals("")) {
						playlistname = "新建列表";
					}

					File[] files = songfile.getSelectedFile().listFiles();
					// 更新内容面板，添加新播放列表数据
					refreshListViewPanelUI(playlistname, files);

				}
			}

			/**
			 * 刷新列表的item页面
			 * 
			 * @param playlistname
			 * @param files
			 */
			private void refreshListViewPanelUI(String playlistname,
					File[] files) {

				Category category = new Category(playlistname);
				category.setCid(IDGenerate.getId(Category.key));
				category.setCreateTime(DateUtil.dateToString(new Date()));

				// 列表内容
				ListViewComPanel listViewComPanel = new ListViewComPanel();
				listViewComPanel.setVisible(false);

				List<SongInfo> songInfos = new ArrayList<SongInfo>();

				// 列表头
				ListViewHeadPanel listViewHeadPanel = new ListViewHeadPanel(
						listViewPanel, songListPanel, listViewComPanel,
						songInfos, width, titleName, category.getCid(), -1);

				// listviewitem面板
				ListViewItemPanel itemPanel = new ListViewItemPanel();
				itemPanel.add(listViewHeadPanel, 0);
				itemPanel.add(listViewComPanel, 1);

				listViewPanel.add(itemPanel);

				songListPanel.updateUI();

				category.setSongInfos(songInfos);

				// 添加到MediaManage
				MediaManage.getMediaManage().addCategory(category);

				// 添加播放列表
				CategoryDB.getCategoryDB().add(category);

				// 更新歌曲列表
				updateListViewComPanelUI(category.getCid(), playlistname, files);
			}
			// }.start();
			// }
		});

		addSongFiledMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// new Thread() {
				//
				// @Override
				// public void run() {

				/**
				 * 打开默认的路径
				 */
				JFileChooser songchooser = new JFileChooser();
				/**
				 * 利用方法加入过滤的文件类型,用这种方法可以加入多种文件的类型
				 * 若只想筛选一种文件类型，可使用setFileFilter()的方法
				 */
				songchooser.addChoosableFileFilter(new AudioFilter());
				/**
				 * 只能选择文件列表
				 */
				songchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				songchooser.setMultiSelectionEnabled(true); // 实现多选

				int result = songchooser.showOpenDialog(ListViewHeadPanel.this);
				boolean hasUpdate = false;
				if (result == JFileChooser.APPROVE_OPTION) {
					// 从MediaManage类获取当前的播放列表下的所有歌曲列表
					List<SongInfo> songInfos = MediaManage.getMediaManage()
							.getSongInfoList(pLId);
					songfiles = songchooser.getSelectedFiles();
					for (int i = 0; i < songfiles.length; i++) {// 支持多选
						File file = songfiles[i];
						String filePath = file.getPath();
						if (!isSongExists(filePath, songInfos)) {
							hasUpdate = true;
							SongInfo songInfo = MediaUtils
									.getSongInfoByFile(filePath);
							if (songInfo != null) {

								songInfo.setCategoryId(pLId);
								songInfos.add(songInfo);

								// 添加到数据库
								SongInfoDB.getSongInfoDB().add(songInfo);

								// 添加单首歌曲
								refreshListViewComPanelUI(pLId,
										songInfos.size() - 1, listViewComPanel,
										songInfo);

								titleNameJLabel.setText(titleName + "["
										+ songInfos.size() + "]");
							}
						}
					}
					if (hasUpdate) {

						// 更新MediaManage下当前播放列表下的所有歌曲列表数据
						MediaManage.getMediaManage().updateSongInfoList(pLId,
								songInfos);
						// 更新ui
						songListPanel.updateUI();

					}
				} else if (result == JFileChooser.CANCEL_OPTION) {
				}

			}
			// }.start();
			// }
		});

		delPlaylistMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// new Thread() {
				//
				// @Override
				// public void run() {
				// new Thread() {
				//
				// @Override
				// public void run() {
				int result = JOptionPane.showConfirmDialog(
						ListViewHeadPanel.this, "删除该【" + titleName + "】播放列表?",
						"确认", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.INFORMATION_MESSAGE);
				if (result == JOptionPane.OK_OPTION) {
					// 删除列表面板
					delPlayListPanel();
					// 删除列表数据库数据
					delPlayListDBData();
					// 删除列表数据
					delPlayListData();
					// 判断歌曲是否是当前播放的播放列表
					if (pLId.equals(BaseData.playInfoPID)) {
						BaseData.playInfoPID = "-1";
						MediaManage.getMediaManage().stopToPlay();
						MediaManage.getMediaManage().setSongInfo(null);
					}
				}
			}

			/**
			 * 删除列表数据
			 */
			private void delPlayListData() {
				MediaManage.getMediaManage().delCategory(pLId);
			}

			/**
			 * 删除列表数据库数据
			 */
			private void delPlayListDBData() {
				//
				SongInfoDB.getSongInfoDB().deleteAllSongs(pLId);
				CategoryDB.getCategoryDB().delete(pLId);
			}

			/**
			 * 删除播放列表面板
			 */
			private void delPlayListPanel() {

				for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
					ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
							.getComponent(i);
					ListViewHeadPanel listViewHeadPanel = (ListViewHeadPanel) itemPanel
							.getComponent(0);
					if (listViewHeadPanel.getpLId().equals(pLId)) {
						// 找到该播放列表所在的面板 ，进行移除
						listViewPanel.remove(i);
						listViewPanel.updateUI();

						break;
					}
				}
			}
			// }.start();
			// }
			// }.start();
			// }
		});

		this.add(statusLeftJLabel);
		this.add(statusDownJLabel);
		this.add(titleNameJLabel);
		this.add(menuButton);

	}

	/**
	 * 刷新歌曲列表
	 * 
	 * @param pLId
	 * @param innerIndex
	 * @param listViewComPanel
	 * @param songInfo
	 */
	private void refreshListViewComPanelUI(String pLId, int innerIndex,
			ListViewComPanel listViewComPanel, SongInfo songInfo) {
		ListViewComItemPanel listViewComItemPanel = new ListViewComItemPanel(
				songListPanel, listViewPanel, pLId, songInfo, width);
		listViewComPanel.add(listViewComItemPanel);
	}

	/**
	 * 更新歌曲列表数据
	 * 
	 * @param i
	 * @param playlistname
	 * @param files
	 */
	private void updateListViewComPanelUI(final String pLId,
			final String playlistname, final File[] files) {
		new Thread() {

			@Override
			public void run() {
				ListViewHeadPanel listViewHeadPanel = null;
				ListViewComPanel listViewComPanel = null;

				for (int i = 0; i < listViewPanel.getComponentCount(); i++) {
					ListViewItemPanel itemPanel = (ListViewItemPanel) listViewPanel
							.getComponent(i);
					ListViewHeadPanel headTemp = (ListViewHeadPanel) itemPanel
							.getComponent(0);
					if (headTemp.getpLId().equals(pLId)) {
						listViewHeadPanel = headTemp;
						listViewComPanel = (ListViewComPanel) itemPanel
								.getComponent(1);
						break;
					}
				}
				if (listViewHeadPanel == null || listViewComPanel == null)
					return;

				boolean hasUpdate = false;
				List<SongInfo> songInfos = new ArrayList<SongInfo>();
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (!file.exists() || file.isDirectory()
							|| !AudioFilter.acceptFilter(file))
						continue;
					String filePath = file.getPath();
					if (!isSongExists(filePath, songInfos)) {
						hasUpdate = true;
						SongInfo songInfo = MediaUtils
								.getSongInfoByFile(filePath);
						if (songInfo != null) {

							songInfo.setCategoryId(pLId);
							songInfos.add(songInfo);

							// 添加歌曲到数据库
							SongInfoDB.getSongInfoDB().add(songInfo);

							// 添加单首歌曲
							refreshListViewComPanelUI(pLId,
									songInfos.size() - 1, listViewComPanel,
									songInfo);
							listViewHeadPanel.getTitleNameJLabel()
									.setText(
											playlistname + "["
													+ songInfos.size() + "]");
						}
					}
				}
				if (hasUpdate) {
					// 更新ui
					songListPanel.updateUI();

					// 更新mediamanage下的播放列表下的所有歌曲列表
					MediaManage.getMediaManage().updateSongInfoList(pLId,
							songInfos);
				}
			}

		}.start();
	}

	/**
	 * 获取播放列表的标题
	 * 
	 * @return
	 */
	public JLabel getTitleNameJLabel() {
		return titleNameJLabel;
	}

	/**
	 * 判断歌曲文件是否存在
	 * 
	 * @param filePath
	 * @param mSongInfo
	 * @return
	 */
	protected boolean isSongExists(String filePath, List<SongInfo> mSongInfo) {
		for (int i = 0; i < mSongInfo.size(); i++) {
			SongInfo tempSongInfo = mSongInfo.get(i);
			if (tempSongInfo.getFilePath().equals(filePath)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 重绘状态图标
	 */
	private void repaintUI() {
		if (isShow) {
			statusLeftJLabel.setVisible(false);
			statusDownJLabel.setVisible(true);
			// 显示歌曲列表内容
			listViewComPanel.setVisible(true);
		} else {
			statusLeftJLabel.setVisible(true);
			statusDownJLabel.setVisible(false);
			// 隐藏歌曲列表内容
			listViewComPanel.setVisible(false);
		}
		songListPanel.updateUI();
	}

	public String getTitleName() {
		return titleName;
	}

	public String getpLId() {
		return pLId;
	}

	public boolean getShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
		repaintUI();
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
		g2d.setPaint(new Color(240, 240, 240, 100));
		BasicStroke stokeLine = new BasicStroke(2.5f);
		g2d.setStroke(stokeLine);
		if (!isShow) {
			g2d.drawLine(0, height, width, height);
		}
	}
}
