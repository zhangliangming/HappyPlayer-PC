package com.happy.manage;

import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;
import com.happy.widget.dialog.MadeTransliterationLrcDialog;

/**
 * 音译歌词制作窗口管理
 * 
 * @author zhangliangming
 * 
 */
public class MadeTransliterationLrcDialogManage {
	/**
	 * 窗口
	 */
	private static MadeTransliterationLrcDialog madeTransliterationLrcDialog;

	public static void initMadeTransliterationLrcDialog() {
		if (madeTransliterationLrcDialog != null)
			madeTransliterationLrcDialog.dispose();
		madeTransliterationLrcDialog = new MadeTransliterationLrcDialog();
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
	public static void showMadeTransliterationLrcDialog(int x, int y) {
		madeTransliterationLrcDialog.setModal(true);
		madeTransliterationLrcDialog.setLocation(x, y);
		madeTransliterationLrcDialog.setVisible(true);
	}

	/**
	 * 隐藏窗口
	 */
	public static void hideMadeTransliterationLrcDialog() {
		if (madeTransliterationLrcDialog != null) {
			madeTransliterationLrcDialog.dispose();
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
		if (madeTransliterationLrcDialog != null)
			return madeTransliterationLrcDialog.getMWidth();
		return 0;
	}

	public static int getHeight() {
		if (madeTransliterationLrcDialog != null)
			return madeTransliterationLrcDialog.getMHeight();
		return 0;
	}

}
