## Nginx 安装和配置

#### 前言

目前大部分的 Java 应用都是部署在 Tomcat 上，但 Tomcat 的高并发性能很弱，为了解决该问题，于是有了 Nginx + Tomcat 结合使用的方式来处理该问题。Nginx 处理静态请求，Tomcat 处理动态请求，利用 Nginx 自身的**负载均衡**功能进行多台 tomcat 服务器调度流量。

#### 简介

**Nginx（engine x）** 是一个高性能的 **HTTP** 和 **反向代理** 服务器，也是一个 IMAP/POP3/SMTP 服务器。在连接高并发的情况下，Nginx 是 Apache 服务器不错的替代品，能够支持高达 50000 个并发连接数的响应。

一说到 Nginx 就会想到 **反向代理** 和 **负载均衡**，那是什么是  **反向代理** 和 **负载均衡** 呢？

> 反向代理（Reverse Proxy）方式是指以代理服务器来接受 internet 上的连接请求，然后将请求转发给内部网络上的服务器，并将从服务器上得到的结果返回给 internet 上请求连接的客户端，此时代理服务器对外就表现为一个服务器。--百度百科

一个动态请求通过 Nginx 转发给 Tomcat ，然后 Tomcat 处理请求返回数据，再把返回数据给 Nginx 来显示，让我们误以为是 Nginx 在处理请求，其实是 Tomcat 在处理请求。

> 负载均衡建立在现有网络结构之上，它提供了一种廉价有效透明的方法扩展网络设备和服务器的带宽、增加吞吐量、加强网络数据处理能力、提高网络的灵活性和可用性。
>
> 负载均衡是由多台服务器以对称的方式组成一个服务器集合，每台服务器都具有等价的地位，都可以单独对外提供服务而无须其他服务器的辅助。通过某种负载分担技术，将外部发送来的请求均匀分配到对称结构中的某一台服务器上，而接收到请求的服务器独立地回应客户的请求。均衡负载能够平均分配客户请求到服务器列阵，籍此提供快速获取重要数据，解决大量并发访问服务问题。

#### Windows

1. 在官网下载 Windows 版本的 [http://nginx.org/en/download.html](http://nginx.org/en/download.html) 并解压到自己喜欢的目录、

2. 点击目录下的 nginx.exe 启动服务，出现下面的欢迎页即启动 ok

   ![nginx-win](C:\Users\krystal\Desktop\nginx-win.png)

   * 上图的网络地址为 http://localhost:8080 ,因为是我的 80 端口 System 应用占用，所以我就更改了 **conf** 目录下  nginx.conf 文件，把端口改为了 8080

   ```
    server {
           listen       8080;
           server_name  localhost;
           .....
   ```

   * 在 Windows 下 80 端口可能会被占用，导致 nginx 启动失败，这时就打开 cmd 输入 **netstat -aon|findstr "80" ** 查看是那个 **PID** 占用了该端口，然后在到 任务管理器查看该 PID 下的程序是谁，关掉它既可。

   到目前为止，在 win 下简单的启动 nginx 就成功了，关于反向代理和负载均衡，主要是修改 **nginx.conf** 文件，与 Linux 相同，使用我就一起放到了后面一起讲。 

#### Linux

#####1. 安装相关组件

* 因为 nginx 是 C 语言开发，安装 nginx 需要先将官网下载的源码进行编译，编译依赖 gcc 环境，如果没有 **gcc** 环境，需要安装 **gcc**： 

```
[root@gakkiwu ~]# yum install gcc-c++
省略安装内容...
期间会有确认提示输入y回车
Is this ok [y/N]:y
省略安装内容...
```

* 安装 **PCRE（Perl Compatible Regular Expressions ）**：

>  PCRE 是一个 Perl 库，包括 Perl 兼容的正则表达式库。nginx 的 http 模块使用 pcre 来解析正则表达式 

```
[root@gakkiwu ~]# yum install -y pcre pcre-devel
...
```

* 安装 **zlib**

> zlib 库提供了很多种压缩和解压缩的方式，nginx 使用 zlib 对 http 包的内容进行 gzip 

   ```
[root@gakkiwu ~]# yum install -y zlib zlib-devel
....   
   ```

* 安装 **OpenSSL**

> OpenSSL 是一个强大的安全套接字层密码库，囊括主要的密码算法、常用的密钥和证书封装管理功能及 SSL 协议，并提供丰富的应用程序供测试或其它目的使用。nginx 不仅支持 http 协议，还支持 https（即在 ssl 协议上传输 http） 

```
[root@gakkiwu ~]# yum install -y openssl openssl-devel
....
```

* 安装 **nginx**

```
# 使用 wget 下载相对应版本的 .gz 文件
[root@gakkiwu data]# wget http://nginx.org/download/nginx-1.14.0.tar.gz

[root@gakkiwu data]# tar zxvf nginx-1.14.0.tar.gz
省略安装内容...
[root@gakkiwu data]# cd nginx-1.14.0
[root@gakkiwu nginx-1.10.2]# ./configure && make && make install
省略安装内容...
```

##### 2. 启动 nginx

```
# 找到 nginx 的位置
[root@gakkiwu /]# whereis nginx
nginx: /usr/local/nginx
# 进入 nginx 目录
[root@gakkiwu /]# cd /usr/local/nginx/
# 启动
[root@gakkiwu nginx]# sbin/nginx
[root@gakkiwu nginx]# ps -aux | grep nginx
root     32460     1  0 22:52 ?        00:00:00 nginx: master process sbin/nginx
nobody   32461 32460  0 22:52 ?        00:00:00 nginx: worker process
root     32463 26696  0 22:52 pts/0    00:00:00 grep --color=auto nginx
# 出现上面的提示就是启动成功
```

* nginx 的基本操作 

```
启动
[root@gakkiwu ~]# /usr/local/nginx/sbin/nginx
停止/重启
[root@gakkiwu ~]# /usr/local/nginx/sbin/nginx -s stop(quit、reload)
命令帮助
[root@gakkiwu ~]# /usr/local/nginx/sbin/nginx -h
验证配置文件
[root@gakkiwu ~]# /usr/local/nginx/sbin/nginx -t
配置文件
[root@gakkiwu ~]# vi /usr/local/nginx/conf/nginx.conf
```

* 如果想要外部访问则需要开通防火墙权限，

```
[root@gakkiwu ~]# firewall-cmd --zone=public --add-port=8080/tcp --permanent
success
[root@gakkiwu ~]# firewall-cmd --reload
success
```

* 如果你的服务器在阿里云上则需要添加安全组规则 ，就可以外部访问了

  阿里云 > 管理控制台 > 云服务器 ECE > 安全组 > 配置规则 > 添加安全组规则 

  ![aliyun](C:\Users\krystal\Desktop\aliyun.png)

  

##### 3.**简单配置 Nginx** + Tomcat 

* 打开 nginx.conf 配置文件位于 nginx 目录下的 conf 文件夹下 ，并修改

```
[root@gakkiwu nginx]# vi conf/nginx.conf



    server {
        listen       80;
        server_name  localhost;
	    index index.jsp index.html index.jsp index.do;#设定访问的默认首页地址
	    
	#过滤相关请求，反向代理到 http://localhost:80
    location ~ \.(html|jsp|jspx|do)?$ {
        proxy_pass http://localhost:80;  #
	} 
	#设定访问静态文件直接读取不经过 tomcat 
    location ~ .*\.(htm|gif|jpg|jpeg|png|bmp|swf|rar|zip|txt|flv|mid|doc|ppt|pdf|xls|mp3|wma|woff2|woff|tff|svg|eot|otf|)$     
	  {  
		root /home/app/static; 
		expires      30d;     
	  }
	  
:wq!	  

#重新加载配置文件
[root@gakkiwu ~]# /usr/local/nginx/sbin/nginx -s reload
```

然后访问 http://localhost:8080 就会代理到 Tomcat 的项目页面

##### 4 . 负载均衡

* 按照下面的配置文件即可达到效果。

```
#user  www www;   
worker_processes 8; 
error_log  /home/nginx/logs/nginx_error.log  crit; 
pid        /home/nginx/nginx.pid; 
 #Specifies the value for maximum file descriptors that can be opened by this process. 
worker_rlimit_nofile 65535; 
#工作模式及连接数上限 
events 
{  
# use epoll;  
 worker_connections 65535; 
} 
#设定http服务器，利用它的反向代理功能提供负载均衡支持 
http 
{   
  #设定mime类型   
  include       mime.types;   
  default_type  application/octet-stream;     
  #charset  gb2312;  
  #设定请求缓冲      
  server_names_hash_bucket_size 128;   
  client_header_buffer_size 32k;  
  large_client_header_buffers 4 32k;  
  sendfile on;  
  tcp_nopush     on;   
  keepalive_timeout 60; 
  tcp_nodelay on; 
  server_tokens off; 
  proxy_send_timeout      60;  
  gzip on; 
  gzip_min_length  1k; 
  gzip_buffers     4 16k; 
  gzip_http_version 1.1; 
  gzip_comp_level 2; 
  gzip_types       text/plain application/x-javascript text/css application/xml; 
  gzip_vary on; 
  
  #limit_zone  crawler  $binary_remote_addr  10m;
 
 upstream server_tomcat{
	server localhost:8080;
	server localhost:9494;
 }
 
  server
   {     
     listen       80;
     server_name  localhost;
     index index.jsp index.html index.jsp index.do;#设定访问的默认首页地址
     root  /home/tomcat/tomcat-9494/webapps/ROOT;#设定网站的资源存放路径
	 
	 location / {
		 proxy_pass http://server_tomcat;
		 proxy_set_header Host $host;
          # 真实的客户端IP
          proxy_set_header X-Real-IP $remote_addr;
          # 代理路由信息，此处取IP有安全隐患
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          # 真实的用户访问协议
          proxy_set_header X-Forwarded-Proto $scheme;

          # 默认值default，
          # 后端response 302时 tomcat header中location的host是http://192.168.1.62:8080
          # 因为tomcat收到的请求是nginx发过去的, nginx发起的请求url host是http://192.168.1.62:8080
          # 设置为default后，nginx自动把响应头中location host部分替换成当前用户请求的host部分
          # 网上很多教程将此值设置成 off，禁用了替换，
          # 这样用户浏览器收到302后跳到http://192.168.1.62:8080，直接将后端服务器暴露给浏览器
          # 所以除非特殊需要，不要设置这种画蛇添足的配置
          proxy_redirect default;
          client_max_body_size 100m;  #允许客户端请求的最大单文件字节数
          client_body_buffer_size 128k; #缓冲区代理缓冲用户端请求的最大字节数
          proxy_connect_timeout 90;  #nginx跟后端服务器连接超时时间
          proxy_read_timeout 90;   #连接成功后，后端服务器响应时间
          proxy_buffer_size 4k;    #设置代理服务器（nginx）保存用户头信息的缓冲区大小
          proxy_buffers 6 32k;    #proxy_buffers缓冲区，网页平均在32k以下的话，这样设置
          proxy_busy_buffers_size 64k;#高负荷下缓冲大小（proxy_buffers*2）
          proxy_temp_file_write_size 64k; #设定缓存文件夹大小，大于这个值，将从upstream服务器传
    
	 }
       #设定访问静态文件直接读取不经过tomcat      
	  location ~ .*\.(htm|gif|jpg|jpeg|png|bmp|swf|ioc|rar|zip|txt|flv|mid|doc|ppt|pdf|xls|mp3|wma)$    
	  {
		expires      30d;     
	  }      
	  location ~ .*\.(js|css)?$     
	  {  
		expires      1h;     
	  }      
   } 
}

```

相关节点说明：

```
upstream xxx{};upstream模块是命名一个后端服务器组，组名必须为后端服务器站点域名，内部可以写多台服务器ip和port，还可以设置跳转规则及权重等等
ip_hash;代表使用ip地址方式分配跳转后端服务器，同一ip请求每次都会访问同一台后端服务器
server;代表后端服务器地址

server{};server模块依然是接收外部请求的部分
server_name;代表外网访问域名
location / {};同样代表过滤器，用于制定不同请求的不同操作
proxy_pass;代表后端服务器组名，此组名必须为后端服务器站点域名

server_name和upstream{}的组名可以不一致，server_name是外网访问接收请求的域名，upstream{}的组名是跳转后端服务器时站点访问的域名
```

* nginx 的 upstream 目前支持 4 种方式的分配

1. 轮询（默认）：每个请求按时间顺序逐一分配到不同的后端服务器，如果后端服务器 down 掉，能自动剔除。 
2. weight ：指定轮询几率，weight 和访问比率成正比，用于后端服务器性能不均的情况。 
3. ip_hash：每个请求按访问 ip 的 hash 结果分配，这样每个访客固定访问一个后端服务器，可以解决 session 的问题。  

#### 参考

>  [Nginx+Tomcat整合的安装与配置(win.linux)](https://www.cnblogs.com/applerosa/p/6678312.html)
>
>  [Nginx系列3之Nginx+tomcat](https://www.cnblogs.com/liaojiafa/p/6046329.html)
>
>  [【Nginx】在Centos 7 系统下安装Nginx](https://blog.csdn.net/kisscatforever/article/details/73135874)
>
>  [Nginx详细安装部署教程](https://www.cnblogs.com/taiyonghai/p/6728707.html)