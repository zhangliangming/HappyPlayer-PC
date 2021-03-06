package com.happy.widget.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.happy.common.BaseData;
import com.happy.common.Constants;
import com.happy.event.PanelMoveDialog;
import com.happy.lyrics.LyricsFileWriter;
import com.happy.lyrics.model.LyricsInfo;
import com.happy.lyrics.model.TranslateLyricsInfo;
import com.happy.lyrics.utils.LyricsIOUtils;
import com.happy.manage.LyricsManage;
import com.happy.manage.MediaManage;
import com.happy.model.MessageIntent;
import com.happy.model.SongInfo;
import com.happy.observable.ObserverManage;
import com.happy.util.LyricsUtil;
import com.happy.widget.button.DefButton;
import com.happy.widget.button.ImageButton;
import com.happy.widget.panel.madetranslatelrc.TranslateLrcPanel;

/**
 * 制作翻译歌词窗口
 * 
 * @author zhangliangming
 * 
 */
public class MadeTranslateLrcDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width = 0;

	private int height = 0;

	/**
	 * 左右边距
	 */
	private int rightPad = 10;

	/**
	 * 背景图片
	 */
	private JLabel bgJLabel;
	/**
	 * 歌曲名称
	 */
	private JLabel songNameJLabel;

	private SongInfo songInfo;
	private TranslateLrcPanel translateLrcPanel;

	public MadeTranslateLrcDialog() {
		// 设定禁用窗体装饰，这样就取消了默认的窗体结构
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);

		Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		width = (screenDimension.width / 5 * 3 + 100) / 4 * 3;
		height = (screenDimension.height / 4 * 3) / 5 * 4 + 10;

		this.setSize(width, height);
		this.setAlwaysOnTop(true);
		this.setModal(true);

		initComponent();
		initSkin();
		loadData();
	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				songInfo = MediaManage.getMediaManage().getSongInfo();
				songNameJLabel.setText("  " + songInfo.getDisplayName());
				translateLrcPanel.initData(songInfo);
			}
		});
	}

	private void initComponent() {

		this.getContentPane().setLayout(null);

		int titleHeight = height / 4 / 3 - 10;

		// 标题面板
		JPanel titlePanel = new JPanel();
		titlePanel.setBounds(0, 0, width, titleHeight);
		new PanelMoveDialog(this, titlePanel);

		// 关闭按钮
		int buttonSize = titleHeight / 3 * 2;
		int buttonY = (titleHeight - buttonSize) / 2;
		ImageButton closeButton = new ImageButton(buttonSize, buttonSize,
				"close_def.png", "close_rollover.png", "close_pressed.png");
		closeButton.setBounds(width - buttonSize - rightPad, buttonY,
				buttonSize, buttonSize);
		closeButton.setToolTipText("关闭");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		titlePanel.setLayout(null);
		// 标题
		JLabel titleJLabel = new JLabel("翻译歌词");
		titleJLabel.setForeground(Color.white);
		titleJLabel.setBounds(10, 0, width / 4, titleHeight);

		titlePanel.add(titleJLabel);
		titlePanel.add(closeButton);
		titlePanel.setOpaque(false);

		// 高度
		int lH = titleHeight + 15;
		// 歌曲名称
		songNameJLabel = new JLabel("歌手-歌曲");
		songNameJLabel.setOpaque(true);
		songNameJLabel.setForeground(Color.BLACK);
		songNameJLabel.setBackground(new Color(240, 240, 240));
		songNameJLabel.setBounds(1, titlePanel.getHeight(), width - 2, lH / 2);

		// 提示语
		JLabel tipLabel = new JLabel("  温馨提示：请在每一行填写对应的翻译");
		tipLabel.setOpaque(true);
		tipLabel.setForeground(Color.RED);
		tipLabel.setBackground(new Color(240, 240, 240));
		tipLabel.setBounds(1,
				songNameJLabel.getY() + songNameJLabel.getHeight(), width - 2,
				lH / 2);

		// 歌词面板
		translateLrcPanel = new TranslateLrcPanel((width - 1 * 2), (height
				- titleHeight - lH - lH / 2 - 1));
		translateLrcPanel.setBackground(Color.white);
		translateLrcPanel.setBounds(
				1,
				titlePanel.getHeight() + songNameJLabel.getHeight()
						+ tipLabel.getHeight(), width - 1 * 2, height
						- (titlePanel.getHeight() + songNameJLabel.getHeight())
						- lH - lH / 2 - 1);

		// 底部面板
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBounds(1,
				translateLrcPanel.getY() + translateLrcPanel.getHeight(),
				width - 2, lH);
		bottomPanel.setBackground(new Color(240, 240, 240));
		//
		int bHSize = bottomPanel.getHeight() / 2;
		int bWSize = bHSize * 4 * 2;
		int y = (bottomPanel.getHeight() - bHSize) / 2;

		DefButton finishButton = new DefButton(bWSize, bHSize);
		finishButton.setText("保存并使用");
		finishButton.setBounds(bottomPanel.getWidth() - rightPad - bWSize, y,
				bWSize, bHSize);
		finishButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				LyricsUtil lyricsParserUtil = LyricsManage
						.getLyricsParser(songInfo.getSid());
				if (lyricsParserUtil != null
						&& lyricsParserUtil.getDefLyricsLineTreeMap() != null
						&& lyricsParserUtil.getDefLyricsLineTreeMap().size() > 0) {

					// 保存翻译歌词
					saveTranslateLrc(lyricsParserUtil);

				} else {
					JOptionPane.showMessageDialog(MadeTranslateLrcDialog.this,
							"翻译歌词文件保存失败", "提示", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		bottomPanel.setLayout(null);
		bottomPanel.add(finishButton);

		this.getContentPane().add(titlePanel);
		this.getContentPane().add(songNameJLabel);
		this.getContentPane().add(tipLabel);
		this.getContentPane().add(translateLrcPanel);
		this.getContentPane().add(bottomPanel);
	}

	/**
	 * 保存翻译歌词
	 * 
	 * @param lyricsParserUtil
	 */
	protected void saveTranslateLrc(LyricsUtil lyricsParserUtil) {
		TranslateLyricsInfo translateLyricsInfo = translateLrcPanel
				.getTranslateLyricsInfo();
		if (translateLyricsInfo == null) {
			JOptionPane.showMessageDialog(MadeTranslateLrcDialog.this,
					"翻译歌词不能为空", "提示", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		try {
			LyricsInfo lyricsInfo = lyricsParserUtil.getLyricsIfno();
			lyricsInfo.setTranslateLyricsInfo(translateLyricsInfo);

			if (lyricsInfo != null) {
				File hrcsFile = new File(Constants.PATH_LYRICS + File.separator
						+ songInfo.getDisplayName() + ".hrcs");

				LyricsFileWriter lyricsFileWriter = LyricsIOUtils
						.getLyricsFileWriter(hrcsFile);
				//
				boolean saveResult = lyricsFileWriter.writer(lyricsInfo,
						hrcsFile.getPath());
				if (saveResult) {
					JOptionPane.showMessageDialog(MadeTranslateLrcDialog.this,
							"翻译歌词文件保存成功", "提示", JOptionPane.PLAIN_MESSAGE);

					// 通知
					MessageIntent messageIntent = new MessageIntent();
					messageIntent
							.setAction(MessageIntent.CLOSE_MADETRANSLATELRCDIALOG);
					ObserverManage.getObserver().setMessage(messageIntent);

					close();
				} else {
					JOptionPane.showMessageDialog(MadeTranslateLrcDialog.this,
							"翻译歌词文件保存失败", "提示", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(MadeTranslateLrcDialog.this,
					"翻译歌词文件保存失败", "提示", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * 关闭窗口
	 */
	protected void close() {
		dispose();
	}

	/**
	 * 初始化皮肤
	 */
	private void initSkin() {

		bgJLabel = new JLabel(getBackgroundImageIcon());// 把背景图片显示在一个标签里面
		// 把标签的大小位置设置为图片刚好填充整个面板
		bgJLabel.setBounds(1, 1, width - 2, height - 2);
		this.getContentPane().setBackground(new Color(50, 50, 50));
		this.getContentPane().add(bgJLabel);
	}

	/**
	 * 获取背景图片
	 * 
	 * @return
	 */
	private ImageIcon getBackgroundImageIcon() {
		String backgroundPath = Constants.PATH_SKIN + File.separator
				+ BaseData.bGroundFileName;
		ImageIcon background = new ImageIcon(backgroundPath);// 背景图片
		background.setImage(background.getImage().getScaledInstance(width,
				height, Image.SCALE_SMOOTH));
		return background;
	}

	public int getMWidth() {
		return width;
	}

	public int getMHeight() {
		return height;
	}

}
