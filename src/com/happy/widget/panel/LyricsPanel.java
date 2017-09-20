package com.happy.widget.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingWorker;

import com.happy.common.BaseData;
import com.happy.lyrics.model.LyricsLineInfo;
import com.happy.manage.LyricsManage;
import com.happy.manage.MediaManage;
import com.happy.model.MessageIntent;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.util.LyricsParserUtil;
import com.happy.util.LyricsUtil;
import com.happy.widget.dialog.DesLrcDialog;
import com.happy.widget.panel.ManyLineLyricsView.MetaDownListener;

/**
 * 歌词面板
 * 
 * @author zhangliangming
 * 
 */
public class LyricsPanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 歌词面板
	 */
	private ManyLineLyricsView manyLineLyricsView;

	/**
	 * 鼠标右键事件
	 */
	private MetaDownListener metaDownListener;

	/**
	 * 歌词解析
	 */
	private LyricsParserUtil lyricsParser;

	/**
	 * 歌词列表
	 */
	private TreeMap<Integer, LyricsLineInfo> lyricsLineTreeMap;
	/**
	 * 当前播放歌曲
	 */
	private SongInfo mSongInfo;

	/**
	 * 当前播放歌曲
	 * 
	 * @param desktopLrcDialog
	 */

	private DesLrcDialog desktopLrcDialog;

	/**
	 * 弹出菜单
	 */
	private JPopupMenu menuPop;
	/**
	 * 歌词颜色菜单
	 */
	private JMenu lrcColorMenuItem;
	/**
	 * 歌词大小
	 */
	private JMenu lrcSizeMenuItem;

	/**
	 * 翻译歌词
	 */
	private JMenuItem translateMenuItem;

	/**
	 * 音译歌词
	 */
	private JMenuItem transliterationMenuItem;

	/**
	 * 歌词选项
	 */
	private JRadioButtonMenuItem colorItem[] = new JRadioButtonMenuItem[BaseData.lrcColorStr.length];
	/**
	 * 
	 */
	private JRadioButtonMenuItem lrcSizeItem[] = new JRadioButtonMenuItem[BaseData.lrcSizeTip.length];

	public LyricsPanel(DesLrcDialog desktopLrcDialog, int width, int height) {
		this.desktopLrcDialog = desktopLrcDialog;
		initComponent(width, height);
		initPopMenu();
		// this.setBackground(Color.black);
		ObserverManage.getObserver().addObserver(this);
		this.setOpaque(false);

	}

	/**
	 * 初始化弹出菜单
	 */
	private void initPopMenu() {
		menuPop = new JPopupMenu();
		//
		translateMenuItem = new JMenuItem("制作翻译歌词");
		translateMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SongInfo songInfo = MediaManage.getMediaManage().getSongInfo();
				if (songInfo == null || BaseData.playInfoID.equals("-1")) {
					JOptionPane.showMessageDialog(null, "请选择歌曲", "提示",
							JOptionPane.ERROR_MESSAGE);
				} else {
					LyricsParserUtil lyricsParserUtil = LyricsManage
							.getLyricsParser(songInfo.getSid());

					if (lyricsParserUtil == null
							|| lyricsParserUtil.getLyricsLineTreeMap() == null
							|| lyricsParserUtil.getLyricsLineTreeMap().size() == 0) {
						JOptionPane.showMessageDialog(null, "请先制作歌曲，然后再制作翻译歌词",
								"提示", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					MessageIntent messageIntent = new MessageIntent();
					messageIntent.setAction(MessageIntent.OPEN_MADETRANSLATELRCDIALOG);
					ObserverManage.getObserver().setMessage(messageIntent);
				
				}

			}
		});

		transliterationMenuItem = new JMenuItem("制作音译歌词");
		transliterationMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				SongInfo songInfo = MediaManage.getMediaManage().getSongInfo();
				if (songInfo == null || BaseData.playInfoID.equals("-1")) {
					JOptionPane.showMessageDialog(null, "请选择歌曲", "提示",
							JOptionPane.ERROR_MESSAGE);
				} else {
					LyricsParserUtil lyricsParserUtil = LyricsManage
							.getLyricsParser(songInfo.getSid());

					if (lyricsParserUtil == null
							|| lyricsParserUtil.getLyricsLineTreeMap() == null
							|| lyricsParserUtil.getLyricsLineTreeMap().size() == 0) {
						JOptionPane.showMessageDialog(null, "请先制作歌曲，然后再制作音译歌词",
								"提示", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					MessageIntent messageIntent = new MessageIntent();
					messageIntent.setAction(MessageIntent.OPEN_MADETRANSLITERATIONLRCDIALOG);
					ObserverManage.getObserver().setMessage(messageIntent);
				
				}

			}
		});

		//
		lrcColorMenuItem = new JMenu("配色方案");
		ButtonGroup lrcColorGroup = new ButtonGroup();
		for (int i = 0; i < colorItem.length; i++) {
			colorItem[i] = new JRadioButtonMenuItem(BaseData.lrcColorTipStr[i]);
			lrcColorGroup.add(colorItem[i]);
			colorItem[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < colorItem.length; i++) {
						if (e.getSource() == colorItem[i]) {

							BaseData.lrcColorIndex = i;
							MessageIntent messageIntent = new MessageIntent();
							messageIntent
									.setAction(MessageIntent.MANYLINELRCCOLOR);
							ObserverManage.getObserver().setMessage(
									messageIntent);

							break;
						}
					}
				}
			});
			if (i == BaseData.lrcColorIndex) {
				colorItem[i].setSelected(true);
			}
			lrcColorMenuItem.add(colorItem[i]);
		}

		//
		lrcSizeMenuItem = new JMenu("歌词大小");
		ButtonGroup lrcSizeGroup = new ButtonGroup();

		// 计算歌词的字体大小比例
		final int[] lrcSizeNum = new int[BaseData.lrcSizeTip.length];
		int avgNum = (BaseData.lrcFontMaxSize - BaseData.lrcFontMinSize)
				/ (BaseData.lrcSizeTip.length - 1);
		for (int i = lrcSizeNum.length - 1; i >= 0; i--) {
			lrcSizeNum[(lrcSizeNum.length - 1) - i] = BaseData.lrcFontMinSize
					+ i * avgNum;
		}
		for (int i = 0; i < BaseData.lrcSizeTip.length; i++) {
			lrcSizeItem[i] = new JRadioButtonMenuItem(BaseData.lrcSizeTip[i]);

			lrcSizeItem[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for (int i = 0; i < lrcSizeItem.length; i++) {
						if (e.getSource() == lrcSizeItem[i]) {

							BaseData.lrcFontSize = lrcSizeNum[i];
							MessageIntent messageIntent = new MessageIntent();
							messageIntent
									.setAction(MessageIntent.MANYLINEFONTSIZE);
							ObserverManage.getObserver().setMessage(
									messageIntent);

							break;
						}
					}
				}
			});

			lrcSizeGroup.add(lrcSizeItem[i]);

			if (lrcSizeNum[i] > BaseData.lrcFontSize
					&& i + 1 != lrcSizeItem.length
					&& BaseData.lrcFontSize > lrcSizeNum[i + 1]) {
				lrcSizeItem[i].setSelected(true);
			} else if (lrcSizeNum[i] == BaseData.lrcFontSize) {
				lrcSizeItem[i].setSelected(true);
			}

			lrcSizeMenuItem.add(lrcSizeItem[i]);
		}
		//
		menuPop.add(translateMenuItem);
		menuPop.add(transliterationMenuItem);
		menuPop.add(lrcColorMenuItem);
		menuPop.add(lrcSizeMenuItem);

	}

	/**
	 * 初始化组件
	 */
	private void initComponent(int width, final int height) {
		this.setLayout(new BorderLayout());
		manyLineLyricsView = new ManyLineLyricsView(width, height, true);

		metaDownListener = new MetaDownListener() {

			@Override
			public void MetaDown(MouseEvent e) {
				menuPop.show(manyLineLyricsView, e.getX(), e.getY());
			}
		};
		manyLineLyricsView.setMetaDownListener(metaDownListener);
		this.add(manyLineLyricsView, BorderLayout.CENTER);
	}

	@Override
	public void update(Observable o, final Object data) {
		updateUI(data);
	}

	/**
	 * 刷新ui
	 * 
	 * @param data
	 */
	protected void updateUI(Object data) {

		if (data instanceof SongMessage) {
			SongMessage songMessage = (SongMessage) data;
			if (songMessage.getType() == SongMessage.INITMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC
					|| songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC
					|| songMessage.getType() == SongMessage.ERRORMUSIC
					|| songMessage.getType() == SongMessage.SERVICEERRORMUSIC) {
				refreshUI(songMessage);
			} else if (songMessage.getType() == SongMessage.LRCKSCLOADED) {
				if (mSongInfo == null)
					return;
				if (!mSongInfo.getSid().equals(songMessage.getSid())) {
					return;
				}
				String kscFilePath = songMessage.getLrcFilePath();
				String sid = songMessage.getSid();

				initKscLrc(sid, kscFilePath, mSongInfo.getDuration(), true);
			} else if (songMessage.getType() == SongMessage.LRCKSCDOWNLOADED) {
				if (mSongInfo == null)
					return;
				if (!mSongInfo.getSid().equals(songMessage.getSid())) {
					return;
				}
				String sid = songMessage.getSid();

				initKscLrc(sid, null, mSongInfo.getDuration(), false);

			}
		}
	}

	/**
	 * 刷新ui
	 * 
	 * @param mSongInfo
	 */
	protected void refreshUI(SongMessage songMessage) {
		SongInfo songInfo = songMessage.getSongInfo();
		if (songInfo != null) {
			if (songMessage.getType() == SongMessage.INITMUSIC) {

				this.mSongInfo = songInfo;

				LyricsUtil.loadLyrics(mSongInfo.getSid(), mSongInfo.getTitle(),
						mSongInfo.getSinger(), mSongInfo.getDisplayName(),
						mSongInfo.getLyricsUrl(), SongMessage.KSCTYPELRC);

				manyLineLyricsView.setHasLrc(false);

				desktopLrcDialog.getFloatLyricsView().setHasLrc(false);

			} else if (songMessage.getType() == SongMessage.SERVICEPLAYINGMUSIC) {

				if (manyLineLyricsView.getHasLrc()
						&& !manyLineLyricsView.getBlScroll()) {

					manyLineLyricsView.showLrc((int) mSongInfo
							.getPlayProgress());

				}

				if (desktopLrcDialog.getFloatLyricsView().getHasLrc()) {
					desktopLrcDialog.getFloatLyricsView().showLrc(
							(int) mSongInfo.getPlayProgress());
				}

			} else if (songMessage.getType() == SongMessage.SERVICEPAUSEEDMUSIC
					|| songMessage.getType() == SongMessage.SERVICESTOPEDMUSIC) {

				if (manyLineLyricsView.getHasLrc()
						&& !manyLineLyricsView.getBlScroll()) {

					manyLineLyricsView.showLrc((int) mSongInfo
							.getPlayProgress());
				}

				if (desktopLrcDialog.getFloatLyricsView().getHasLrc()) {
					desktopLrcDialog.getFloatLyricsView().showLrc(
							(int) mSongInfo.getPlayProgress());
				}

			}
		} else {
			if (manyLineLyricsView != null)
				manyLineLyricsView.setHasLrc(false);

			if (desktopLrcDialog.getFloatLyricsView() != null) {
				desktopLrcDialog.getFloatLyricsView().setHasLrc(false);
			}
		}
	}

	public ManyLineLyricsView getManyLineLyricsView() {
		return manyLineLyricsView;
	}

	/**
	 * 
	 * @param sid
	 * @param lrcFilePath
	 * @param duration
	 * @param isFile
	 */
	private void initKscLrc(final String sid, final String lrcFilePath,
			final long duration, final boolean isFile) {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				if (isFile)
					lyricsParser = LyricsManage.getLyricsParser(sid, new File(
							lrcFilePath));
				else
					lyricsParser = LyricsManage
							.getKscLyricsParserByInputStream(sid);

				return null;
			}

			@Override
			protected void done() {
				lyricsLineTreeMap = lyricsParser.getLyricsLineTreeMap();
				if (lyricsLineTreeMap != null && lyricsLineTreeMap.size() != 0) {
					manyLineLyricsView.init((int) duration, lyricsParser);
					manyLineLyricsView.setHasLrc(true);

					if (mSongInfo != null) {
						manyLineLyricsView.showLrc((int) mSongInfo
								.getPlayProgress());

					}

					desktopLrcDialog.getFloatLyricsView().init(lyricsParser);
					desktopLrcDialog.getFloatLyricsView().setHasLrc(true);

					if (mSongInfo != null) {
						desktopLrcDialog.getFloatLyricsView().showLrc(
								(int) mSongInfo.getPlayProgress());
					}

				}
			}
		}.execute();
	}
}
