package com.happy.manage;

import com.happy.widget.dialog.ProgressTipDialog;

/**
 * 歌曲进度提示管理
 * 
 * @author zhangliangming
 * 
 */
public class SongProgressTipManage {
	/**
	 * 是否显示
	 */
	private static boolean isShow = false;

	private static SongProgressTipManage _SongInfoTipManage;

	public static SongProgressTipManage getSongInfoTipManage() {
		if (_SongInfoTipManage == null) {
			_SongInfoTipManage = new SongProgressTipManage();
		}
		return _SongInfoTipManage;
	}

	/**
	 * 
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 */
	public SongProgressTipManage() {
		songProgressTipDialog = new ProgressTipDialog();
		songProgressTipDialog.setVisible(false);
	}

	/**
	 * 歌曲进度提示窗口
	 */
	private static ProgressTipDialog songProgressTipDialog;

	public ProgressTipDialog getSongProgressTipDialog() {
		return songProgressTipDialog;
	}

	/**
	 * 显示窗口
	 */
	public static void showSongProgressTipDialog() {
		if (songProgressTipDialog != null) {
			isShow = true;
			if (!songProgressTipDialog.isShowing())
				songProgressTipDialog.setVisible(true);
		}
	}

	/**
	 * 隐藏歌曲信息窗口
	 */
	public static void hideSongProgressTipDialog() {
		if (songProgressTipDialog != null) {
			isShow = false;
			new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!isShow)
						songProgressTipDialog.setVisible(false);
				}

			}.start();
		}
	}
}
