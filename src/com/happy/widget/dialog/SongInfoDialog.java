package com.happy.widget.dialog;

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.happy.common.Constants;
import com.happy.model.SongInfo;
import com.sun.awt.AWTUtilities;

/***
 * 歌曲信息窗口
 * 
 * @author zhangliangming
 * 
 */
public class SongInfoDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 歌手图片
	 */
	private JLabel iconJLabel;
	/**
	 * 歌名
	 */
	private JLabel displayNameJLabel;

	/**
	 * 歌曲大小
	 */
	private JLabel sizeJLabel;

	/**
	 * 文件格式
	 */
	private JLabel fileTypeJLabel;

	/**
	 * 音质
	 */
	private JLabel timbreJLabel;
	/**
	 * 来源
	 */
	private JLabel sourceJLabel;
	/**
	 * 窗口宽度
	 */
	private int width = 0;
	/**
	 * 窗口高度
	 */
	private int height = 0;
	/**
	 * 间隔
	 */
	private int padding = 5;

	public SongInfoDialog(int width, int height) {
		this.width = width;
		this.height = height;

		// 设定禁用窗体装饰，这样就取消了默认的窗体结构
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.setSize(width, height);
		AWTUtilities.setWindowOpacity(this, 0.8f);
		initComponent();
	}

	/**
	 * 初始化控件
	 */
	private void initComponent() {
		this.getContentPane().setLayout(null);
		int componentWidth = width / 5;
		int componentHeight = height / 3;

		int iconSize = height - 4 * padding;
		String singerIconPath = Constants.PATH_ICON + File.separator
				+ "ic_launcher.png";
		ImageIcon singerIcon = new ImageIcon(singerIconPath);
		singerIcon.setImage(singerIcon.getImage().getScaledInstance(iconSize,
				iconSize, Image.SCALE_SMOOTH));
		iconJLabel = new JLabel(singerIcon);
		int iconX = (height - iconSize) / 2;
		iconJLabel.setBounds(padding, iconX, iconSize, iconSize);

		//
		displayNameJLabel = new JLabel("歌名");
		displayNameJLabel.setBounds(iconJLabel.getX() + iconJLabel.getWidth()
				+ padding, padding,
				width - iconJLabel.getX() - iconJLabel.getWidth() - padding,
				componentHeight);

		JLabel sizeTextJLabel = new JLabel("大小:");
		sizeTextJLabel.setBounds(iconJLabel.getX() + iconJLabel.getWidth()
				+ padding,
				displayNameJLabel.getY() + displayNameJLabel.getHeight()
						- padding, componentWidth - padding * 2,
				componentHeight);

		//
		sizeJLabel = new JLabel("0.00MB");
		sizeJLabel.setBounds(sizeTextJLabel.getX() + sizeTextJLabel.getWidth(),
				displayNameJLabel.getY() + displayNameJLabel.getHeight()
						- padding, componentWidth + padding, componentHeight);

		//
		JLabel fileTypeTextJLabel = new JLabel("格式:");
		fileTypeTextJLabel.setBounds(sizeJLabel.getX() + sizeJLabel.getWidth(),
				displayNameJLabel.getY() + displayNameJLabel.getHeight()
						- padding, componentWidth - padding * 2,
				componentHeight);

		//
		fileTypeJLabel = new JLabel("MP3");
		fileTypeJLabel.setBounds(
				fileTypeTextJLabel.getX() + fileTypeTextJLabel.getWidth(),
				displayNameJLabel.getY() + displayNameJLabel.getHeight()
						- padding, componentWidth, componentHeight);
		//
		JLabel timbreTextJLabel = new JLabel("音质:");
		timbreTextJLabel.setBounds(iconJLabel.getX() + iconJLabel.getWidth()
				+ padding, sizeTextJLabel.getY() + sizeTextJLabel.getHeight()
				- padding, componentWidth - padding * 2, componentHeight);
		timbreJLabel = new JLabel("普通");
		timbreJLabel
				.setBounds(
						timbreTextJLabel.getX() + timbreTextJLabel.getWidth()
								+ padding, sizeTextJLabel.getY()
								+ sizeTextJLabel.getHeight() - padding,
						componentWidth, componentHeight);

		//
		JLabel sourceTextJLabel = new JLabel("来源:");
		sourceTextJLabel.setBounds(
				timbreJLabel.getX() + timbreJLabel.getWidth(),
				sizeTextJLabel.getY() + sizeTextJLabel.getHeight() - padding,
				componentWidth - padding * 2, componentHeight);

		sourceJLabel = new JLabel("本地");
		sourceJLabel.setBounds(
				sourceTextJLabel.getX() + sourceTextJLabel.getWidth(),
				sizeTextJLabel.getY() + sizeTextJLabel.getHeight() - padding,
				componentWidth, componentHeight);

		this.getContentPane().add(displayNameJLabel);
		this.getContentPane().add(iconJLabel);
		this.getContentPane().add(sizeTextJLabel);
		this.getContentPane().add(sizeJLabel);
		this.getContentPane().add(fileTypeTextJLabel);
		this.getContentPane().add(fileTypeJLabel);
		this.getContentPane().add(timbreTextJLabel);
		this.getContentPane().add(timbreJLabel);
		this.getContentPane().add(sourceTextJLabel);
		this.getContentPane().add(sourceJLabel);
	}

	/**
	 * 更新窗口数据
	 * 
	 * @param songInfo
	 */
	public void updateUI(final SongInfo songInfo) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateDialogUI(songInfo);
			}
		});
	}

	/**
	 * 更新窗口ui
	 * 
	 * @param songInfo
	 */
	protected void updateDialogUI(SongInfo songInfo) {
		displayNameJLabel.setText(songInfo.getDisplayName());
		sizeJLabel.setText(songInfo.getSizeStr());

		String filePath = songInfo.getFilePath();
		int beginIndex = filePath.lastIndexOf(".");
		String fileType = filePath.substring(beginIndex + 1, filePath.length());
		fileTypeJLabel.setText(fileType);

		String fileTypeText = "普通";
		if (fileType.equals("ape") || fileType.equals("flac")) {
			fileTypeText = "无损";
		}

		timbreJLabel.setText(fileTypeText);

		int type = songInfo.getType();
		String typeText = "本地";
		if (type != SongInfo.LOCALSONG) {
			typeText = "网络";
		}
		sourceJLabel.setText(typeText);
	}

}
