# HappyPlayer-PC
#1.音频解码器
  对musique 播放器的解码器进行优化和修改，这里我将它项目使用到的核心解码代码，
  抽取出来封装成一个jar包，方便以后移植到其它的项目中。
  其项目源地址：
大神的播放器（支持多种音频格式）

java swing版本：https://github.com/tulskiy/musique.git

  该项目十分强大，推荐大家看一下他的源码。
#2.歌词解析器

乐乐第一个版本，使用的是KSC卡拉OK的歌词格式，
该歌词格式，虽然可以实现动态歌词的效果，
解析也方便，但是该歌词文件的大小差不多是酷狗krc文件的两倍多，
所以在存储时还是占用了比较大的空间。
#hrc (happy lyrics)歌词

乐乐第二个版本，使用自定义的歌词格式文件hrc (happy lyrics) 。
该版本可自制歌词并生成hrc歌词，它与酷狗krc比较，感觉还是不错的，占空间小了好多。
#hrcx (happy lyrics)歌词

hrc歌词的优化

#krc酷狗歌词


#3.界面设计


- 主界面
- 标题栏：MainMenuPanel
- 中间界面：MainCenterPanel
- 底部界面：MainOperatePanel
- 中间界面
X轴的BoxLayout，方便点击可隐藏/展开


- 播放列表面板：SongListPanel
- 列表Item面板：
- 列表标题栏面板：
- 列表下的歌曲列表内容面板：
- 列表下的歌曲列表Item面板：
- 歌词面板：LyricsPanel
多行歌词面板：ManyLineLyricsView
桌面歌词面板

#4.参考博客


- http://blog.csdn.net/qingzi635533/article/details/30231733
- http://www.jianshu.com/p/f6e7c8b9b2a3


#5.效果图
![](http://i.imgur.com/KehXwfn.jpg)

![](http://i.imgur.com/N1QDJoE.jpg)

#博客地址
http://zhangliangming.github.io/