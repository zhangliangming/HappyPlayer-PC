#HappyPlayer-JavaFX版本

##网络歌曲界面
![网络歌曲.png](https://upload-images.jianshu.io/upload_images/4111431-7d9a1e54fe923ec5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##多行歌词界面
![多行歌词.png](https://upload-images.jianshu.io/upload_images/4111431-563d70e67035f526.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

##桌面歌词
![桌面歌词.png](https://upload-images.jianshu.io/upload_images/4111431-26486b851ee5d995.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


# 简介 #
乐乐音乐是基于musique 开源播放器开发的java swing音乐播放器，实现了mp3、flac、ape、wav等多种音频格式的播放和ksc歌词的解析、制作和显示

# 乐乐音乐6.0 #
乐乐6.0是网络版，添加了在线歌曲试听、下载、歌曲搜索、歌手头像、歌手写真及在线歌词功能，还有桌面歌词添加了桌面窗口调整大小功能。由于学习交流时间结束，该版本源码暂不提供下载。

# 开发环境 #
myeclipse、java swing

# 部分图片预览 #

![](https://i.imgur.com/t9MqGTS.png)


![](https://i.imgur.com/W4e2hwh.png)

![](https://i.imgur.com/uh0j5Ln.png)

![20200905160747.png](https://upload-images.jianshu.io/upload_images/4111431-1e9250612ad166f6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


# 安装包 #

链接: [https://pan.baidu.com/s/1NEfCFLmqOSL7uQTa0eNUMw](https://pan.baidu.com/s/1NEfCFLmqOSL7uQTa0eNUMw) 提取码: 5pq4 复制这段内容后打开百度网盘手机App，操作更方便哦


# 更新日志 #
- 2020-09-05：制作歌词添加行编辑，制作时按下↓键即可；桌面歌词添加多颜色渐变。
- 2017-09-27：实现歌词快进功能
- 2017-09-26：初步实现多行歌词优化。
- 2017-0-25：注释多行歌词（多行歌词还要想一下怎样实现）。实现桌面歌词（双行歌词）支持显示翻译歌词和音译歌词。
- 2017-09-24：添加音译歌词功能（暂时不支持预览，有兴趣可到下面的链接查看android版本的播放器）。
- 2017-09-23：添加多国语言字体支持，方便制作其他语言的歌词和显示其他语言的歌词
- 2017-09-20：添加生成翻译歌词功能。（暂时不支持翻译歌词预览，有兴趣可到下面的链接查看android版本的播放器）
- 2017-09-20：因为读取歌词的时候，将歌词格式编码都设置为utf-8,所以解析之前的歌词时，会造成乱码问题，到时有乱码，只要将utf-8编码修改一下即可。
- 2017-09-20：基于krc歌词，研究和生成新的hrcs歌词格式，支持翻译歌词和音译歌词。

# 音频解码器 #
  对musique 播放器的解码器进行优化和修改，这里我将它项目使用到的核心解码代码，
  抽取出来封装成一个jar包，方便以后移植到其它的项目中。其项目源地址：[https://github.com/tulskiy/musique.git](https://github.com/tulskiy/musique.git)，该项目十分强大，推荐大家看一下他的源码。
# 歌词解析器 #

乐乐第一个版本，使用的是KSC卡拉OK的歌词格式，
该歌词格式，虽然可以实现动态歌词的效果，
解析也方便，但是该歌词文件的大小差不多是酷狗krc文件的两倍多，
所以在存储时还是占用了比较大的空间。
## hrc (happy lyrics)歌词 ##

乐乐第二个版本，使用自定义的歌词格式文件hrc (happy lyrics) 。
该版本可自制歌词并生成hrc歌词，它与酷狗krc比较，感觉还是不错的，占空间小了好多。
## ksc歌词 ##
## krc酷狗歌词 ##

# 程序入口 #

com.happy.enterProgram.EnterProgram

# 参考博客 #

- [酷狗的krc歌词文件的解析](http://blog.csdn.net/qingzi635533/article/details/30231733)


- [iOS音乐播放器开发(扩展)-歌词格式](http://www.jianshu.com/p/f6e7c8b9b2a3)


# 效果图 #

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


# 博客地址 #
[http://zhangliangming.github.io/](http://zhangliangming.github.io/)

# android版本播放器 #
[https://github.com/zhangliangming/HappyPlayer5.git](https://github.com/zhangliangming/HappyPlayer5.git)

# 捐赠 #
如果该项目对您有所帮助，欢迎您的赞赏

- 微信

![](https://i.imgur.com/hOs6tPn.png)

- 支付宝

![](https://i.imgur.com/DGB9Lq0.png)
