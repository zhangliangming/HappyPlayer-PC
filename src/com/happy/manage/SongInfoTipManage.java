package com.happy.manage;

import com.happy.widget.dialog.SongInfoDialog;

/**
 * 歌曲提示管理
 * 
 * @author zhangliangming
 * 
 */
public class SongInfoTipManage {
	/**
	 * 是否显示
	 */
	private static boolean isShow = false;

	private static SongInfoTipManage _SongInfoTipManage;

	public static SongInfoTipManage getSongInfoTipManage(int width, int height) {
		if (_SongInfoTipManage == null) {
			_SongInfoTipManage = new SongInfoTipManage(width, height);
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
	public SongInfoTipManage(int width, int height) {
		songInfoDialog = new SongInfoDialog(width, height);
	}

	/**
	 * 歌曲提示窗口
	 */
	private static SongInfoDialog songInfoDialog;

	public SongInfoDialog getSongInfoDialog() {
		return songInfoDialog;
	}

	/**
	 * 显示窗口
	 */
	public static void showSongInfoTipDialog() {
		if (songInfoDialog != null) {
			isShow = true;
			if (!songInfoDialog.isShowing())
				songInfoDialog.setVisible(true);
		}
	}

	/**
	 * 隐藏歌曲信息窗口
	 */
	public static void hideSongInfoTipDialog() {
		if (songInfoDialog != null) {
			isShow = false;
			// 延迟关闭窗口，当鼠标移进下一个列表时，不用隐藏，直接修改位置显示。
			new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!isShow)
						songInfoDialog.setVisible(false);
				}

			}.start();
		}
	}
}
