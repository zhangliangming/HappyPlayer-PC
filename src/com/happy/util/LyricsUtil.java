package com.happy.util;

import java.io.File;

import javax.swing.SwingWorker;

import com.happy.common.Constants;
import com.happy.model.SongMessage;
import com.happy.observable.ObserverManage;

/**
 * 歌词处理类
 * 
 * @author zhangliangming
 * 
 */
public class LyricsUtil {

	/**
	 * 加载歌词文件
	 * 
	 * @param context
	 * @param sid
	 * @param title
	 * @param singer
	 * @param lrcUrl
	 */
	public static void loadLyrics(final String sid, String title,
			String singer, final String displayName, String lrcUrl,
			final int type) {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {

				String lrcFilePath = Constants.PATH_LYRICS + File.separator
						+ displayName + ".hrc";

				File lrcFile = new File(lrcFilePath);
				if (!lrcFile.exists()) {
					return null;
				} else {

					SongMessage songMessage = new SongMessage();

					if (type == SongMessage.KSCTYPELRC) {

						songMessage.setType(SongMessage.LRCKSCLOADED);
					} else if (type == SongMessage.KSCTYPEDES) {

						songMessage.setType(SongMessage.DESKSCLOADED);
					} else if (type == SongMessage.KSCTYPELOCK) {

						songMessage.setType(SongMessage.LOCKKSCLOADED);
					}

					songMessage.setLrcFilePath(lrcFilePath);
					songMessage.setSid(sid);
					// 通知
					ObserverManage.getObserver().setMessage(songMessage);
				}
				return null;

			}

			@Override
			protected void done() {

			}
		}.execute();
	}
}
