### 注意1：Master分支是开发分支、也是项目重构分支，Origin分支是原来项目全部代码

### 注意2：目前的版本不会再继续进行新功能开发，只会进行BUG修复，会拉一个分支基于jdk11和javafx11重构项目，重构后会减少内存占用50%左右，启动速度优化到1-3s启动

### 注意3：v1.1.0以后自己打包的，运行会报错误，因为增加了SSH通道、集群功能，需要将用户目录下的.redis_plus文件夹删掉，然后重新启动就会正常了

# RedisPlus

#### 项目介绍

RedisPlus是为Redis可视化管理开发的一款开源免费的桌面客户端软件，支持Windows 、Linux、Mac三大系统平台，RedisPlus提供更加高效、方便、快捷的使用体验，有着更加现代化的用户界面风格。该软件支持单机、集群模式连接，同时还支持SSH（单机、集群）通道连接。RedisPlus遵循GPL-3.0开源协议，禁止二次开发打包发布盈利，违反必究！RedisPlus致力于为大家提供一个高效的Redis可视化管理软件。

#### 下载地址

百度下载：https://pan.baidu.com/s/1ETwWnEj4rbsE1S3GlYHlWg


#### 版本说明

版本的命名规则以x.y.z形式

重大版本更新发布会更改x位主版本，例如v2.0.0 版本会增加集群、支持ssh通道连接、国际化等功能

一般解决了多个缺陷后，会发布一个小版本，更改y位，例如v1.1.0解决了1.0.0 的问题后发布的小版本

z位的更改不会发布版本，这是缺陷修复或者小需求增加的正常迭代


#### 版本规划

v2.0.0

1.SSH通道支持 （已支持）

2.集群连接支持 （已支持）

3.修复v1.1.0版本反馈问题


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

![输入图片说明](https://images.gitee.com/uploads/images/2018/1028/191406_af498a20_1252126.png "深度截图_Desktop_20181028190916.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/1028/191422_be701419_1252126.png "深度截图_Desktop_20181028190936.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/1028/191433_2221bd19_1252126.png "深度截图_Desktop_20181028191006.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/1028/191442_b2f6b2ee_1252126.png "深度截图_Desktop_20181028191021.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/1028/191451_38a7e0a1_1252126.png "深度截图_Desktop_20181028191033.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/1029/090526_74e21183_1252126.png "深度截图_Desktop_20181029090238.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/1028/191501_2a51bd57_1252126.png "深度截图_Desktop_20181028191206.png")

![输入图片说明](https://images.gitee.com/uploads/images/2018/1028/191511_93417c03_1252126.png "深度截图_Desktop_20181028191227.png")