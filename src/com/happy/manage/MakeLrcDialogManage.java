package com.happy.manage;

import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.widget.dialog.MakeLrcDialog;

/**
 * 歌词制作窗口管理
 * 
 * @author zhangliangming
 * 
 */
public class MakeLrcDialogManage {
	/**
	 * 窗口
	 */
	private static MakeLrcDialog makeLrcDialog;

	public static void initMakeLrcDialog() {
		if (makeLrcDialog != null)
			makeLrcDialog.dispose();
		makeLrcDialog = new MakeLrcDialog();
		// 暂停歌曲
		SongMessage songMessage = new SongMessage();
		songMessage.setType(SongMessage.PAUSEMUSIC);
		// 通知
		ObserverManage.getObserver().setMessage(songMessage);

	}

	/**
	 * 显示窗口
	 * 
	 * @param x
	 * @param y
	 */
	public static void showMakeLrcDialog(int x, int y) {
		makeLrcDialog.setModal(true);
		makeLrcDialog.setLocation(x, y);
		makeLrcDialog.setVisible(true);
	}

	/**
	 * 隐藏窗口
	 */
	public static void hideMakeLrcDialog() {
		if (makeLrcDialog != null) {
			makeLrcDialog.dispose();
		}
		// 清空歌词缓存
		LyricsManage.clean();
		// 重新播放歌曲
		SongMessage songMessage = new SongMessage();
		songMessage.setType(SongMessage.REINITMUSIC);
		// 通知
		ObserverManage.getObserver().setMessage(songMessage);
	}

	public static int getWidth() {
		if (makeLrcDialog != null)
			return makeLrcDialog.getMWidth();
		return 0;
	}

	public static int getHeight() {
		if (makeLrcDialog != null)
			return makeLrcDialog.getMHeight();
		return 0;
	}

}
