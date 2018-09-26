### 注意：v1.1.1自己打包的，运行会报错误，因为增加了SSH通道功能，需要将用户目录下的.redis_plus文件夹删掉，然后重新启动就会正常了

# RedisPlus

#### 项目介绍

RedisPlus是为Redis管理开发的桌面客户端软件，支持Windows 、Linux 、Mac三大系统平台，RedisPlus提供更加高效、方便、快捷的使用体验，有着更加现代化的界面风格。该软件参考了RedisStudio的界面逻辑，但是和RedisStudio软件完全没有关系，并不是官方推出的跨平台软件。该软件为开源免费使用（可商用），但是禁止二次开发打包发布盈利，违反必究！ 该软件遵循GPL开源协议，致力于为大家提供一个高效的Redis可视化管理软件。

#### 下载地址

1.微云下载：https://share.weiyun.com/5UIOsxY

2.百度下载：https://pan.baidu.com/s/1ETwWnEj4rbsE1S3GlYHlWg


#### 版本说明

版本的命名规则以x.y.z形式

重大版本更新发布会更改x位主版本，例如v2.0.0 版本会增加集群、支持ssh通道连接、国际化等功能

一般解决了多个缺陷后，会发布一个小版本，更改y位，例如v1.1.0解决了1.0.0 的问题后发布的小版本

z位的更改不会发布版本，这是缺陷修复或者小需求增加的正常迭代


#### 版本规划

v2.0.0

1.SSH通道支持 （已完成）

2.集群连接支持

3.支持国际化

4.修复v1.1.0版本反馈问题


v3.0.0

1.使用jdk11+javafx11重构项目

2.使用zgc技术优化性能

3.修复v2.0.0反馈问题


#### 软件交流

1.QQ群：857111033  点击链接加入群聊【RedisPlusj交流群】：https://jq.qq.com/?_wv=1027&k=5nFw9eg

2.issues专区：https://gitee.com/MaxBill/RedisPlus/issues
 

#### 技术选型

1.支持跨平台，使用java开发

2.使用javafx的桌面元素

3.使用derby内嵌数据库

4.内置服务使用springboot开发


#### 打包说明

1.linux平台：需要安装fakeroot库

2.windows：需要安装WiX Toolset、Inno Setup、microsoft .net framework库

#### 应用截图

![输入图片说明](https://images.gitee.com/uploads/images/2018/0917/143612_a7381776_1252126.png "深度截图_com.maxbill.MainApplication_20180917142650.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/0917/143636_9ddd2654_1252126.png "深度截图_com.maxbill.MainApplication_20180917142708.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/0917/143649_8af0a861_1252126.png "深度截图_com.maxbill.MainApplication_20180917142726.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/0917/143659_a2e1bd9b_1252126.png "深度截图_com.maxbill.MainApplication_20180917142741.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/0917/143714_81d8a576_1252126.png "深度截图_com.maxbill.MainApplication_20180917142749.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/0917/143724_227d3df1_1252126.png "深度截图_com.maxbill.MainApplication_20180917142829.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/0917/143733_073bd1ef_1252126.png "深度截图_com.maxbill.MainApplication_20180917142840.png")
