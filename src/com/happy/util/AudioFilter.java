package com.happy.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 音乐文件的过滤
 * 
 */
public class AudioFilter extends FileFilter {

	public boolean accept(File f) {
		String fileName = f.getName().toLowerCase();
		return fileName.endsWith(".mp3") || fileName.endsWith(".ogg")
				|| fileName.endsWith(".flac") || fileName.endsWith(".wavpack")
				|| fileName.endsWith(".ape") || fileName.endsWith(".wav")
				|| fileName.endsWith(".au") || fileName.endsWith(".aiff")
				|| f.isDirectory();
	}

	public String getDescription() {
		return "*.mp3,*.ogg," + "*.flac," + "*.wavpack,*.ape,*.wav,*.au,*.aiff";
	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	public static boolean acceptFilter(File f) {
		String fileName = f.getName().toLowerCase();
		return fileName.endsWith(".mp3") || fileName.endsWith(".ogg")
				|| fileName.endsWith(".flac") || fileName.endsWith(".wavpack")
				|| fileName.endsWith(".ape") || fileName.endsWith(".wav")
				|| fileName.endsWith(".au") || fileName.endsWith(".aiff");
	}

}
