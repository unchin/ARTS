

netstat -ntlp

查看端口和进程



CentOS 7快速开放端口：

CentOS升级到7之后，发现无法使用iptables控制Linuxs的端口，baidu之后发现Centos 7使用firewalld代替了原来的iptables。下面记录如何使用firewalld开放Linux端口：

开启端口
firewall-cmd --zone=public --add-port=80/tcp --permanent

查询端口号80 是否开启：

firewall-cmd --query-port=80/tcp

重启防火墙：

firewall-cmd --reload

查询有哪些端口是开启的:

firewall-cmd --list-port

命令含义：


--zone #作用域
--add-port=80/tcp #添加端口，格式为：端口/通讯协议
--permanent #永久生效，没有此参数重启后失效

关闭firewall：

systemctl stop firewalld.service #停止firewall

systemctl disable firewalld.service #禁止firewall开机启动



