# 更新日志 #
- 2017-09-27：实现歌词快进功能
- 2017-09-26：初步实现多行歌词优化。
- 2017-0-25：注释多行歌词（多行歌词还要想一下怎样实现）。实现桌面歌词（双行歌词）支持显示翻译歌词和音译歌词。
- 2017-09-24：添加音译歌词功能（暂时不支持预览，有兴趣可到下面的链接查看android版本的播放器）。
- 2017-09-23：添加多国语言字体支持，方便制作其他语言的歌词和显示其他语言的歌词
- 2017-09-20：添加生成翻译歌词功能。（暂时不支持翻译歌词预览，有兴趣可到下面的链接查看android版本的播放器）
- 2017-09-20：因为读取歌词的时候，将歌词格式编码都设置为utf-8,所以解析之前的歌词时，会造成乱码问题，到时有乱码，只要将utf-8编码修改一下即可。
- 2017-09-20：基于krc歌词，研究和生成新的hrcs歌词格式，支持翻译歌词和音译歌词。
# android版本播放器 #
该版本播放器已经实现和支持翻译歌词和音译歌词。链接：https://github.com/zhangliangming/HappyPlayer5.git

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
- 应用入口
com.happy.enterProgram.EnterProgram
- 主界面
- 标题栏：MainMenuPanel
- 中间界面：MainCenterPanel
- 底部界面：MainOperatePanel
- 中间界面：X轴的BoxLayout，方便点击可隐藏/展开
- 播放列表面板：SongListPanel
- 列表Item面板：
- 列表标题栏面板：
- 列表下的歌曲列表内容面板：
- 列表下的歌曲列表Item面板：
- 歌词面板：LyricsPanel
- 多行歌词面板：ManyLineLyricsView
- 桌面歌词面板

#4.参考博客


- http://blog.csdn.net/qingzi635533/article/details/30231733
- http://www.jianshu.com/p/f6e7c8b9b2a3


#5.效果图

![](https://i.imgur.com/DJDEWqE.png)

![](https://i.imgur.com/gDaRzC2.png)

![](https://i.imgur.com/O4vXBwA.png)

![](https://i.imgur.com/7JB4bnp.png)

![](https://i.imgur.com/D0GFqAT.png)

![](https://i.imgur.com/O8fcpQP.png)

![](http://i.imgur.com/KehXwfn.jpg)

![](http://i.imgur.com/N1QDJoE.jpg)

![](https://i.imgur.com/5DqmxSg.png)

![](https://i.imgur.com/Ca3PZnB.png)

![](https://i.imgur.com/3ngktqS.png)

![](https://i.imgur.com/N4H0mrw.png)

![](https://i.imgur.com/XUPYjpe.png)

![](https://i.imgur.com/qq2psvM.png)

![](https://i.imgur.com/ObVuEp7.png)

![](https://i.imgur.com/Hr7NtgA.png)


#6.博客地址
http://zhangliangming.github.io/

# 捐赠 #
如果该项目对您有所帮助，欢迎您的赞赏

- 微信
![](https://i.imgur.com/e3hERHh.png)

- 支付宝
![](https://i.imgur.com/29AcEPA.png)
