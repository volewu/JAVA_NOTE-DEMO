## 一台电脑启动多个 Tomcat 

#### 前言

在学习 Nginx 负载均衡的时候，需要启动两个或者两个以上的 Tomcat 来进行测试，如果要让两个 Tomcat 启动成功，这时就要对这两个 Tomcat 进行配置。下面就介绍在 Window 和 Linux 下的配置说明。

#### Windows 版

1. 在 Tomcat 官网下载与自己系统位数相同的 Window 版本的压缩包，分别将压缩包解压到自己定义好的路径，下面是我自己的路径地址：

```
E:\JAVAIDE\tomcat\tomcat-8080
E:\JAVAIDE\tomcat\tomcat-9494
```

2.  修改配置文件

我们默认 tomcat-8080 服务端口为 8080 ，这样我们就不修改它了，在这里我们主要修改 tomcat-9494 服务。

打开路径 **E:\JAVAIDE\tomcat\tomcat-9494\conf\server.xml** 文件，有三处需要修改的地方

```
8015 为远程停止服务端口
<Server port="8015" shutdown="SHUTDOWN">

9494 为 http 端口
<Connector port="9494" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
               
8019 为 AJP 端口，Apache 能通过 AJP 协议访问 Tomcat 的 8019 端口            
<Connector port="8019" protocol="AJP/1.3" redirectPort="8443" />           
```

上面的三个端口号你可以按照自己的想法来进行修改，但是要与另一个 tomcat 的端口不一样即可。

打开 **E:\JAVAIDE\tomcat\tomcat-9494\bin\startup.bat** 文件，在文件的开头加上如下配置即可：

```
set TITLE="E:\JAVAIDE\tomcat\tomcat-9494"
set CATALINA_HOME="E:\JAVAIDE\tomcat\tomcat-9494"
set CATALINA_BASE="E:\JAVAIDE\tomcat\tomcat-9494"
```

完成上面两个文件的修改，即可在 Window 下启动两个 tomcat 服务，我们分别启动两个 tomcat 服务，然后在浏览器分别输入 

http://localhost:9494

http://localhost:8080

出现 tomcat 的导航页即为成功。

如果启动两个以上的 tomcat 服务，也是按上面的修改即可。**只要端口号不一样即可**

#### Linux 版

在 Linux上启动多个 tomcat 服务与 Window 类似，

1. 在官网下载与系统相同版本的 apache-tomcat-8.5.29.tar.gz 包，把它上传到服务器上，并分别解压，且重命名解压好的文件名

```
[root@localhost data]# tar -zxvf  apache-tomcat-8.5.29.tar.gz
[root@localhost data]# mv apache-tomcat-8.5.29.tar.gz tomcat-8080
[root@localhost data]# tar -zxvf  apache-tomcat-8.5.29.tar.gz
[root@localhost data]# mv apache-tomcat-8.5.29.tar.gz tomcat-9494
```

2. 修改配置文件

打开配置文件并修改

```
[root@localhost /]# vi /home/tomcat/tomcat-9494/conf/server.xml
```

```
8015 为远程停止服务端口
<Server port="8015" shutdown="SHUTDOWN">

9494 为 http 端口
<Connector port="9494" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
               
8019 为 AJP 端口，Apache 能通过 AJP 协议访问 Tomcat 的 8019 端口            
<Connector port="8019" protocol="AJP/1.3" redirectPort="8443" />           
```

上面的三个端口号你可以按照自己的想法来进行修改，但是要与另一个 tomcat 的端口不一样即可。Linux只需要修改这个文件即可。

3. 为其端口配置防火墙权限

```
[root@localhost /]# firewall-cmd --zone=public --add-port=8080/tcp --permanent
[root@localhost /]# firewall-cmd --zone=public --add-port=9494/tcp --permanent
[root@localhost /]# firewall-cmd --reload
```

这样在 Linux就能启动多个 tomcat 服务了。

如果你的服务器在阿里云上则需要添加安全组规则 ，就可以外部访问了

**阿里云 > 管理控制台 > 云服务器 ECE > 安全组 > 配置规则 > 添加安全组规则 **

![aliyun](C:\Users\krystal\Desktop\aliyun.png)

> 参考：
>
> [在一台Win10机器上同时启动多个Tomcat](https://www.cnblogs.com/andy1234/p/8866588.html)



