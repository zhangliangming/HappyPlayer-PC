package com.happy.manage;

import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.widget.dialog.MadeLrcDialog;

/**
 * 歌词制作窗口管理
 * 
 * @author zhangliangming
 * 
 */
public class MadeLrcDialogManage {
	/**
	 * 窗口
	 */
	private static MadeLrcDialog madeLrcDialog;

	public static void initMadeLrcDialog() {
		if (madeLrcDialog != null)
			madeLrcDialog.dispose();
		madeLrcDialog = new MadeLrcDialog();
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
	public static void showMadeLrcDialog(int x, int y) {
		madeLrcDialog.setModal(true);
		madeLrcDialog.setLocation(x, y);
		madeLrcDialog.setVisible(true);
	}

	/**
	 * 隐藏窗口
	 */
	public static void hideMadeLrcDialog() {
		if (madeLrcDialog != null) {
			madeLrcDialog.dispose();
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
		if (madeLrcDialog != null)
			return madeLrcDialog.getMWidth();
		return 0;
	}

	public static int getHeight() {
		if (madeLrcDialog != null)
			return madeLrcDialog.getMHeight();
		return 0;
	}

}
