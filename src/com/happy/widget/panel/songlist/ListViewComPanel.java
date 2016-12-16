package com.happy.widget.panel.songlist;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * 歌曲列表内容面板，该面板包含（ListViewComItemPanel）
 * 
 * @author zhangliangming
 * 
 */
public class ListViewComPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public ListViewComPanel(){
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setOpaque(false);
	}
}
