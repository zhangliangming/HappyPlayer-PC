package com.happy.widget.panel.madelrc;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.happy.common.Constants;
import com.happy.lyrics.utils.TimeUtils;
import com.happy.manage.MediaManage;
import com.happy.model.SongInfo;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.widget.button.DefButton;
import com.happy.widget.scrollbar.ScrollBarUI;
import com.happy.widget.slider.MakeLrcSlider;

/**
 * 录入面板
 * 
 * @author zhangliangming
 * 
 */
public class MakeLrcLvRuPanel extends JPanel implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width = 0;
	private int height = 0;
	private int bWSize = 0;
	private int bHSize = 0;

	private int padding = 10;

	/**
	 * 停止
	 */
	private DefButton stopButton;

	/**
	 * 播放
	 */
	private DefButton playButton;

	/**
	 * 暂停
	 */
	private DefButton pauseButton;

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
	 * 歌词内容
	 */
	private JTextArea lrcComTextArea;

	public MakeLrcLvRuPanel(int width, int height, int bWSize, int bHSize) {
		this.width = width;
		this.height = height;
		this.bWSize = bWSize;
		this.bHSize = bHSize;
		this.setBackground(Color.white);
		initComponent();
		ObserverManage.getObserver().addObserver(this);
	}

	private void initComponent() {
		this.setLayout(null);

		int oH = bHSize * 3 + 10;
		// 操作面板背景
		String obgBackgroundPath = Constants.PATH_ICON + File.separator
				+ "makeLrc_obg.png";
		ImageIcon obgBackground = new ImageIcon(obgBackgroundPath);
		obgBackground.setImage(obgBackground.getImage().getScaledInstance(
				width - 16, oH, Image.SCALE_SMOOTH));
		JLabel bg = new JLabel(obgBackground);
		bg.setBounds(10, 0, width - 16, oH);
		// 操作面板
		JPanel oPanel = new JPanel();
		oPanel.setOpaque(false);
		oPanel.setBounds(10, 0, width - 16, oH);
		oPanel.setLayout(null);
		//
		int by = (oH - bHSize) / 2;
		// 停止按钮
		stopButton = new DefButton(bWSize, bHSize);
		stopButton.setText("停止");
		stopButton.setBounds(padding, by, bWSize, bHSize);
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.STOPMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);

			}
		});
		// 播放按钮
		playButton = new DefButton(bWSize, bHSize);
		playButton.setText("播放");
		playButton.setBounds(stopButton.getX() + stopButton.getWidth()
				+ padding, by, bWSize, bHSize);
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PLAYMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});
		// 暂停按钮
		pauseButton = new DefButton(bWSize, bHSize);
		pauseButton.setText("暂停");
		pauseButton.setBounds(stopButton.getX() + stopButton.getWidth()
				+ padding, by, bWSize, bHSize);
		pauseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SongMessage songMessage = new SongMessage();
				songMessage.setType(SongMessage.PAUSEMUSIC);
				// 通知
				ObserverManage.getObserver().setMessage(songMessage);
			}
		});
		// 歌曲总进度
		songLabel = new JLabel();
		songLabel.setForeground(Color.black);
		songLabel.setBounds(oPanel.getWidth() - 120 + padding / 2, by, 120,
				bHSize);
		songLabel.setText(TimeUtils.parseString(0) + "/"
				+ TimeUtils.parseString(0));
		//
		songSlider = new MakeLrcSlider();
		songSlider.setOpaque(false); // slider的背景透明
		songSlider.setFocusable(false);
		songSlider.setValue(0);
		songSlider.setMaximum(100);

		int sw = oPanel.getWidth() - 5 * padding - songLabel.getWidth()
				- pauseButton.getWidth() * 2;

		songSlider.setBounds(pauseButton.getX() + pauseButton.getWidth()
				+ padding, by + 2, sw, bHSize);

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

		//
		oPanel.add(songSlider);
		oPanel.add(songLabel);
		oPanel.add(pauseButton);
		oPanel.add(playButton);
		oPanel.add(stopButton);
		//

		lrcComTextArea = new JTextArea();
		lrcComTextArea.setLineWrap(true); // 激活自动换行功能
		lrcComTextArea.setWrapStyleWord(true); // 激活断行不断字功能

		JScrollPane jScrollPane = new JScrollPane(lrcComTextArea);
		// jScrollPane.setBorder(null);

		jScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI(100));
		jScrollPane.getVerticalScrollBar().setUnitIncrement(30);
		// 不显示水平的滚动条
		jScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		jScrollPane.setBounds(10, oPanel.getY() + oPanel.getHeight() + padding,
				width - 16, height - oH - padding);

		this.add(jScrollPane);
		this.add(oPanel);
		this.add(bg);
	}

	/**
	 * 
	 * @param songInfo
	 */
	public void initData(SongInfo songInfo) {
		SongMessage songMessage = new SongMessage();
		songMessage.setSongInfo(songInfo);
		songMessage.setType(SongMessage.INITMUSIC);
		refreshUI(songMessage);
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

				if (MediaManage.PLAYING == MediaManage.getMediaManage()
						.getPlayStatus()) {
					playButton.setVisible(false);
					pauseButton.setVisible(true);

				} else {
					playButton.setVisible(true);
					pauseButton.setVisible(false);

				}

				if (lrcComTextArea != null) {
					lrcComTextArea.setText(mSongInfo.getDisplayName());
				}

				songSlider.setValue(0);
				songSlider.setMaximum((int) mSongInfo.getDuration());

				songLabel.setText(TimeUtils.parseString(0) + "/"
						+ TimeUtils.parseString((int) mSongInfo.getDuration()));

			} else if (songMessage.getType() == SongMessage.SERVICEPLAYMUSIC) {
				playButton.setVisible(false);
				pauseButton.setVisible(true);

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
				playButton.setVisible(true);
				pauseButton.setVisible(false);

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
			playButton.setVisible(true);
			pauseButton.setVisible(false);

		}
	}

	public JTextArea getLrcComTextArea() {
		return lrcComTextArea;
	}

	public void dispose() {
		ObserverManage.getObserver().deleteObserver(this);
	}

}
