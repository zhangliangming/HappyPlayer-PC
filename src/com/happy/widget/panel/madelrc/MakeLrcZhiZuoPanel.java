package com.happy.widget.panel.madelrc;

import java.awt.Color;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.happy.common.Constants;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.lyrics.utils.CharUtils;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.manage.MediaManage;
import com.happy.model.LrcEventIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.service.MediaPlayerService;
import com.happy.util.StringUtils;
import com.happy.widget.scrollbar.ScrollBarUI;
import com.happy.widget.slider.MakeLrcSlider;

/**
 * 录入面板
 * 
 * @author zhangliangming
 * 
 */
public class MakeLrcZhiZuoPanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width = 0;
	private int height = 0;
	private int bHSize = 0;

	private int padding = 10;

	/**
	 * 歌曲进度
	 * 
	 */
	private JLabel songLabel;

	/**
	 * 歌曲进度条
	 */
	private MakeLrcSlider songSlider;

	/**
	 * 判断其是否是正在拖动
	 */
	private boolean isStartTrackingTouch = false;
	/**
	 * 歌词列表面板
	 */
	private JPanel lrcListView;
	/**
	 * 歌词列表数据
	 */
	private List<LyricsLineInfo> lrcLists;

	private KeyListener keyListener = new KeyListener();
	/**
	 * 滚动面板
	 */
	private JScrollPane jScrollPane;
	/**
	 * 滚动条
	 */
	private JScrollBar jScrollBar;
	/**
	 * 内容面板
	 */
	private JPanel comPanel;

	/**
	 * 当前歌词的所在行数
	 */
	private int lyricsLineNum = -1;

	public MakeLrcZhiZuoPanel(int width, int height, int bHSize, JPanel comPanel) {

		this.comPanel = comPanel;
		this.width = width;
		this.height = height;
		this.bHSize = bHSize;
		this.setBackground(Color.white);

		initComponent();

		ObserverManage.getObserver().addObserver(this);
	}

	private void initComponent() {
		this.setLayout(null);

		int oH = bHSize * 3;
		// 操作面板背景
		String obgBackgroundPath = Constants.PATH_ICON + File.separator
				+ "zhizuo_op.png";
		ImageIcon obgBackground = new ImageIcon(obgBackgroundPath);
		obgBackground.setImage(obgBackground.getImage().getScaledInstance(
				width - 10, oH - 10, Image.SCALE_SMOOTH));
		JLabel bg = new JLabel(obgBackground);
		bg.setBounds(padding, 0, width - padding * 2, oH);

		// 歌曲总进度
		songLabel = new JLabel();
		songLabel.setForeground(Color.black);
		songLabel.setBounds(width - 120 + padding / 2, oH + padding / 2, 120,
				bHSize);
		songLabel.setText(TimeUtils.parseString(0) + "/"
				+ TimeUtils.parseString(0));
		//
		songSlider = new MakeLrcSlider();
		songSlider.setOpaque(false); // slider的背景透明
		songSlider.setFocusable(false);
		songSlider.setValue(0);
		songSlider.setMaximum(100);

		int sw = width - padding * 3 - songLabel.getWidth();

		songSlider.setBounds(padding, oH + padding, sw, bHSize);

		songSlider.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				isStartTrackingTouch = true;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				final int x = e.getX();

				// // /* 获取鼠标的位置 */
				songSlider.setValue(songSlider.getMaximum() * x
						/ songSlider.getWidth());
				new Thread() {

					@Override
					public void run() {
						SongMessage songMessage = new SongMessage();
						songMessage.setType(SongMessage.SEEKTOMUSIC);
						songMessage.setProgress(songSlider.getValue());
						ObserverManage.getObserver().setMessage(songMessage);

						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						isStartTrackingTouch = false;

					}

				}.start();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
		});

		songSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (isStartTrackingTouch) {
					songLabel.setText(TimeUtils.parseString(((JSlider) e
							.getSource()).getValue())
							+ "/"
							+ TimeUtils.parseString(songSlider.getMaximum()));
				}
			}
		});

		lrcListView = new JPanel();
		lrcListView.setOpaque(false);
		jScrollPane = new JScrollPane(lrcListView);

		jScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI(100));
		jScrollPane.getVerticalScrollBar().setUnitIncrement(30);
		// 不显示水平的滚动条
		jScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollBar = jScrollPane.getVerticalScrollBar();

		int jh = height - oH - padding * 3 - bHSize;
		jScrollPane.setBounds(padding,
				songSlider.getY() + songSlider.getHeight() + padding, width
						- padding * 2, jh);
		lrcListView.setLayout(new BoxLayout(lrcListView, BoxLayout.Y_AXIS));
		this.add(jScrollPane);
		this.add(songSlider);
		this.add(songLabel);
		this.add(bg);

	}

	private class KeyListener implements KeyEventDispatcher {

		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_SPACE:
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					// 按下时你要做的事情
					playOrPauseMusic();
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					// 放开时你要做的事情

				}

				break;
			case KeyEvent.VK_KP_LEFT:
				// 用于数字键盘向左方向键的常量
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					// 按下时你要做的事情
					preLrcIndex();
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					// 放开时你要做的事情

				}
				break;
			case KeyEvent.VK_KP_RIGHT:
				// 用于数字键盘向右方向键的常量
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					// 按下时你要做的事情
					nextLrcIndex();
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					// 放开时你要做的事情

				}
				break;
			case KeyEvent.VK_LEFT:
				// 用于非数字键盘向左方向键的常量
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					// 按下时你要做的事情
					preLrcIndex();
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					// 放开时你要做的事情

				}
				break;
			case KeyEvent.VK_RIGHT:
				// 用于非数字键盘向右方向键的常量
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					// 按下时你要做的事情
					nextLrcIndex();
				} else if (e.getID() == KeyEvent.KEY_RELEASED) {
					// 放开时你要做的事情

				}
				break;
			default:
				break;
			}
			return false;
		}
	}

	/**
	 * 更新歌词列表ui
	 * 
	 * @param lrcEventIntent
	 */
	private void refreshLrcUI(LrcEventIntent lrcEventIntent) {
		if (lyricsLineNum < lrcLists.size()) {
			// 设置旧索引的界面ui
			LrcListViewItemPanel lrcListViewItemPanel = (LrcListViewItemPanel) lrcListView
					.getComponent(lyricsLineNum);
			LrcComPanel lrcComPanel = lrcListViewItemPanel.getLrcComPanel();
			lrcComPanel.setSelect(false);
		}
		// 设置新索引的界面ui
		int newLyricsLineNum = lrcEventIntent.getLrcIndex();
		lyricsLineNum = newLyricsLineNum;
		LrcListViewItemPanel newLrcListViewItemPanel = (LrcListViewItemPanel) lrcListView
				.getComponent(lyricsLineNum);
		LrcComPanel newLrcComPanel = newLrcListViewItemPanel.getLrcComPanel();
		newLrcComPanel.setSelect(true);
	}

	/**
	 * 上一个歌词
	 */
	public void preLrcIndex() {
		if (lyricsLineNum > lrcLists.size()) {
			return;
		}
		SongInfo songInfo = MediaPlayerService.getMediaPlayerService()
				.getSongInfo();
		int playProgress = (int) songInfo.getPlayProgress();
		LrcListViewItemPanel lrcListViewItemPanel = (LrcListViewItemPanel) lrcListView
				.getComponent(lyricsLineNum);
		LrcComPanel lrcComPanel = lrcListViewItemPanel.getLrcComPanel();
		lrcComPanel.setSelect(true);
		lrcComPanel.setPreLrcIndex(playProgress);
	}

	/**
	 * 下一个歌词
	 */
	public void nextLrcIndex() {
		if (lyricsLineNum > lrcLists.size()) {
			// System.out.println("制作完成");
			// // 输出相关的歌词数据
			// for (int i = 0; i < lrcListView.getComponentCount(); i++) {
			// LrcListViewItemPanel lrcListViewItemPanel =
			// (LrcListViewItemPanel) lrcListView
			// .getComponent(i);
			// LrcComPanel lrcComPanel = lrcListViewItemPanel.getLrcComPanel();
			// KscLyricsLineInfo kscLyricsLineInfo = lrcComPanel
			// .getKscLyricsLineInfo();
			// if (kscLyricsLineInfo != null)
			// System.out.println(kscLyricsLineInfo.getKscString());
			// }
			return;
		}
		// 如果是最后一行歌词已经完成最后一个歌词了，则设置最后一行歌词的最后一个歌词的结束时间
		if (lyricsLineNum == lrcLists.size()) {
			lyricsLineNum--;
		}
		LrcListViewItemPanel lrcListViewItemPanel = (LrcListViewItemPanel) lrcListView
				.getComponent(lyricsLineNum);
		LrcComPanel lrcComPanel = lrcListViewItemPanel.getLrcComPanel();
		SongInfo songInfo = MediaPlayerService.getMediaPlayerService()
				.getSongInfo();
		int playProgress = (int) songInfo.getPlayProgress();
		// 已经读到最后一个歌词了
		if (lrcComPanel.isFinish()) {
			// 判断最后一个歌词的结束时间是否存在
			if (!lrcComPanel.isComFinish())
				lrcComPanel.setLastLrcIndex(playProgress);
			if (lyricsLineNum == lrcLists.size() - 1) {
				lyricsLineNum = lyricsLineNum + 2;
			} else {
				// 跳转到下一行歌词
				lyricsLineNum++;
			}
			nextLrcIndex();
		} else {
			jScrollBar.setValue(60 * (lyricsLineNum - 1));
			lrcComPanel.setSelect(true);
			lrcComPanel.setNextLrcIndex(playProgress);
		}
	}

	/**
	 * 获取所有的歌词集合数据
	 * 
	 * @return
	 */
	public TreeMap<Integer, LyricsLineInfo> getLrcsData() {
		TreeMap<Integer, LyricsLineInfo> lyricsLineInfos = new TreeMap<Integer, LyricsLineInfo>();
		int index = 0;
		for (int i = 0; i < lrcListView.getComponentCount(); i++) {
			LrcListViewItemPanel lrcListViewItemPanel = (LrcListViewItemPanel) lrcListView
					.getComponent(i);
			LrcComPanel lrcComPanel = lrcListViewItemPanel.getLrcComPanel();
			LyricsLineInfo lyricsLineInfo = lrcComPanel.getKscLyricsLineInfo();
			if (lyricsLineInfo != null) {
				lyricsLineInfos.put(index++, lyricsLineInfo);
			}
		}
		return lyricsLineInfos;
	}

	/**
	 * 暂停或者播放
	 */
	private void playOrPauseMusic() {
		if (MediaManage.getMediaManage().getPlayStatus() == MediaManage.PLAYING) {
			// 当前正在播放，发送暂停
			SongMessage msg = new SongMessage();
			msg.setType(SongMessage.PAUSEMUSIC);
			ObserverManage.getObserver().setMessage(msg);
		} else {

			SongMessage songMessage = new SongMessage();
			songMessage.setType(SongMessage.PLAYMUSIC);
			// 通知
			ObserverManage.getObserver().setMessage(songMessage);
		}

	}

	/**
	 * 初始化歌曲数据
	 * 
	 * @param songInfo
	 */
	public void initData(SongInfo songInfo) {
		SongMessage songMessage = new SongMessage();
		songMessage.setSongInfo(songInfo);
		songMessage.setType(SongMessage.INITMUSIC);
		refreshUI(songMessage);
	}

	/**
	 * 初始化歌词面板ui
	 * 
	 * @param lrcCom
	 */
	public void initLrcPanelUI(String lrcCom) {
		lrcLists = new ArrayList<LyricsLineInfo>();
		paseLrcTxt(lrcCom);
		addKeyListener();
	}

	/**
	 * 解析歌词文本
	 * 
	 * @param lrcCom
	 */
	private void paseLrcTxt(String lrcCom) {
		lrcListView.removeAll();
		String lrcComs[] = lrcCom.split("\n");
		for (int i = 0; i < lrcComs.length; i++) {
			String lrcComTxt = lrcComs[i];
			if (StringUtils.isEmpty(lrcComTxt)
					|| StringUtils.isBlank(lrcComTxt)) {
				continue;
			}
			LyricsLineInfo lyricsLineInfo = new LyricsLineInfo();

			// 获取歌词
			String lineLyrics = getLineLyrics(lrcComTxt, lyricsLineInfo);
			lyricsLineInfo.setLineLyrics(lineLyrics);

			lrcLists.add(lyricsLineInfo);
		}

		for (int i = 0; i < lrcLists.size(); i++) {
			LyricsLineInfo lyricsLineInfo = lrcLists.get(i);
			LrcListViewItemPanel lrcListViewItemPanel = new LrcListViewItemPanel(
					i, lyricsLineInfo, this, comPanel, width);
			lrcListView.add(lrcListViewItemPanel);
		}
		lyricsLineNum = 0;

		jScrollBar.setMaximum(60 * lrcLists.size());
		jScrollBar.setMinimum(60);
		this.updateUI();
	}

	/**
	 * 获取当行歌词
	 * 
	 * @param lrcComTxt
	 *            歌词文本
	 * @return
	 */
	private String getLineLyrics(String lrcComTxt, LyricsLineInfo lyricsLineInfo) {

		Stack<String> lrcStack = new Stack<String>();
		String temp = "";
		for (int i = 0; i < lrcComTxt.length(); i++) {
			char c = lrcComTxt.charAt(i);
			if (CharUtils.isChinese(c) || CharUtils.isHangulSyllables(c)
					|| CharUtils.isHiragana(c)) {

				if (!temp.equals("")) {
					lrcStack.push(temp);
					temp = "";
				}

				lrcStack.push(String.valueOf(c));
			} else if (Character.isSpaceChar(c)) {
				if (!temp.equals("")) {
					lrcStack.push(temp);
					temp = "";
				}
				String tw = lrcStack.pop();
				if (tw != null) {
					lrcStack.push("" + tw + " " + "");
				}
			} else {
				temp += String.valueOf(c);
			}
		}
		//
		if (!temp.equals("")) {
			lrcStack.push("" + temp + "");
			temp = "";
		}
		String newLrc = "";
		String[] lyricsWords = new String[lrcStack.size()];
		Iterator<String> it = lrcStack.iterator();
		int i = 0;
		while (it.hasNext()) {
			String tempCom = it.next();
			lyricsWords[i++] = tempCom;
			newLrc += tempCom;
		}

		lyricsLineInfo.setLyricsWords(lyricsWords);
		return newLrc;
	}

	@Override
	public void update(Observable o, final Object data) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateUI(data);
			}
		});
	}

	/**
	 * 
	 * @param data
	 */
	protected void updateUI(Object data) {
		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.INITMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPLAYMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC
					|| songMessage.getType() == SongMessage.ERRORMUSIC
					|| songMessage.getType() == SongMessage.SERVICEERRORMUSIC) {
				refreshUI(songMessage);
			}
		} else if (data instanceof LrcEventIntent) {
			LrcEventIntent lrcEventIntent = (LrcEventIntent) data;
			refreshLrcUI(lrcEventIntent);
		}
	}

	/**
	 * 刷新ui
	 * 
	 * @param songMessage
	 */
	private void refreshUI(SongMessage songMessage) {

		SongInfo mSongInfo = songMessage.getSongInfo();
		if (mSongInfo != null) {
			if (songMessage.getType() == SongMessage.INITMUSIC) {

				songSlider.setValue(0);
				songSlider.setMaximum((int) mSongInfo.getDuration());

				songLabel.setText(TimeUtils.parseString(0) + "/"
						+ TimeUtils.parseString((int) mSongInfo.getDuration()));

			} else if (songMessage.getType() == SongMessage.SERVICEPLAYMUSIC) {

			} else if (songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

				if (!isStartTrackingTouch) {
					songSlider.setValue((int) mSongInfo.getPlayProgress());
					songLabel.setText(TimeUtils.parseString((int) mSongInfo
							.getPlayProgress())
							+ "/"
							+ TimeUtils.parseString((int) mSongInfo
									.getDuration()));
				}

			} else if (songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC) {

				songSlider.setValue((int) mSongInfo.getPlayProgress());
				songLabel.setText(TimeUtils.parseString((int) mSongInfo
						.getPlayProgress())
						+ "/"
						+ TimeUtils.parseString((int) mSongInfo.getDuration()));
			}
		} else {

			songSlider.setValue(0);
			songSlider.setMaximum(0);
			songLabel.setText(TimeUtils.parseString(0) + "/"
					+ TimeUtils.parseString(0));

		}
	}

	/**
	 * 添加键盘事件
	 */
	public void addKeyListener() {
		DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(keyListener);
	}

	/**
	 * 移除键盘事件
	 */
	public void removeKeyListener() {
		DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(keyListener);
	}

	/**
	 * 关闭界面
	 */
	public void dispose() {
		DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(keyListener);
		ObserverManage.getObserver().deleteObserver(this);
	}
}
