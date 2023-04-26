### 参考文献

https://blog.csdn.net/wxgxgp/article/details/89855525

### SSO介绍

SSO(Single Sign On)，单点登录，简单来说就是在一个具有多个子系统的系统中，只用登录一个子系统，然后访问其他子系统时不需要再次登录，即“一次登录，多处访问”，能够有效的提升用户体验。

单点登录的大致流程如下（基于cookie）：

1.用户首次访问A系统，A系统发现用户未登录，则重定向到SSO认证中心并携带请求url，进行登录验证；

2.用户在SSO认证中心进行用户名和密码验证登录，登录成功后，服务器生成一个ticket，然后重定向到系统A的源url并将该ticket追加到url参数。

3.系统A获取到url参数中的ticket，向SSO发起ticket较验，较验成功，则系统A放行，并将ticket存入到cookie。

4.用户访问B系统，此时B系统domain下已经携带ticket，直接向SSO发起ticket较验，较验成功，则放行，并将ticket存入cookie(更新ticket过期时间)

5.用户登出时，移除domain下的cookie。

流程图大致如下：

![[Pasted image 20230406231032.png]]

### 问题与思考

**1.使用cookie还是url?**

对于ticket的传输，一直在纠结使用cookie还是url附加参数的形式，最后处于方便考虑，还是使用了cookie。

**2.如何保证ticket的安全性？**

这个问题也困扰了我一下午，一直在寻求一种完美的、安全的ticket传递形式，最后明白了，在网络上没有绝对的安全，当破解它所带来的利益小于破解后所带来的利益时，他就是安全的，即安全是相对的。所以既然ticket是作为cookie或者url参数传递的，那么它的安全性本来就没有保证，我们能保证的是如何对ticket进行较验，如何验证拿到同一个ticket的用户是同一个用户。对于这个问题，我使用了一种一次性的ticket，上边也说了，这样虽然不能保证绝对的安全，但是在某种程度上能够有效防止他人直接截获cookie而获得权限。

**3.关于cookie的domian**

在测试过程中发现cookie写入的都是二级域名，例如aa.test.com而不是test.com，这导致其他系统无法共享cookie而导致单点登录失败，解决办法是直接设置domain为.test.com即可，注意前面的点不能省略。其次直接在yml里使用如下配置设置cookie的domain是无效的

server:
	servlet:
	    session:
	      cookie:
	        domain: .test.com


**4.登出时直接请求sso-server还是请求子系统？**

考虑到是子系统之间共享的cookie，所以清除子系统的cookie即可。

