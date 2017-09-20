package com.happy.manage;

import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.widget.dialog.MadeTranslateLrcDialog;

/**
 * 翻译歌词制作窗口管理
 * 
 * @author zhangliangming
 * 
 */
public class MadeTranslateLrcDialogManage {
	/**
	 * 窗口
	 */
	private static MadeTranslateLrcDialog madeTranslateLrcDialog;

	public static void initMadeTranslateLrcDialog() {
		if (madeTranslateLrcDialog != null)
			madeTranslateLrcDialog.dispose();
		madeTranslateLrcDialog = new MadeTranslateLrcDialog();
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
	public static void showMadeTranslateLrcDialog(int x, int y) {
		madeTranslateLrcDialog.setModal(true);
		madeTranslateLrcDialog.setLocation(x, y);
		madeTranslateLrcDialog.setVisible(true);
	}

	/**
	 * 隐藏窗口
	 */
	public static void hideMadeTranslateLrcDialog() {
		if (madeTranslateLrcDialog != null) {
			madeTranslateLrcDialog.dispose();
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
		if (madeTranslateLrcDialog != null)
			return madeTranslateLrcDialog.getMWidth();
		return 0;
	}

	public static int getHeight() {
		if (madeTranslateLrcDialog != null)
			return madeTranslateLrcDialog.getMHeight();
		return 0;
	}

}
