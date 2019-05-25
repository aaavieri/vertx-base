# readme

### 1，使用前的话
本框架是基于vert.x二次开发的，采用guice进行依赖注入管理的，为获得最佳体验，可参考以下文档。

[vert.x中文文档地址](http://vertxchina.github.io/vertx-translation-chinese/)

[vert.x英文文档地址](https://vertx.io/docs/)

[google guice框架文档地址](https://github.com/google/guice/wiki/GettingStarted)

### 2，getting started
其实不看以上的文档也并没有太大关系，毕竟以您的智慧，这并不会难到你。
所以，让我们来创建点简单的DB检索(基于mysql)接口web工程试试看吧。
[工程源码地址](https://github.com/aaavieri/vertx-base-sample/tree/master/vertx-base-sample1)

先安装vertx-base的jar包
```
git clone https://github.com/aaavieri/vertx-base.git
cd vertx-base
mvn clean install
```

然后开始撸代码了，这样的工程采用本框架开发需要的class有：
- 类似于带有@SpringBootApplication注解的入口类
- 一个类似于Mybatis的mapper接口

你会问：然后呢？然后还有配置文件，再然后就没有了，真的没有了^_^
- 入口类代码：
```java
package com.yjl.sample.vercicle;

import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.verticle.InitVerticle;
import com.yjl.vertx.base.dao.factory.AutoRouteDaoFactory;

// factoryClass指定了要实现的功能，value则规定了该功能使用的包
@ComponentInitializer(factoryClass = AutoRouteDaoFactory.class, value = "com.yjl.sample.mapper")
public class DbInitVerticle extends InitVerticle {
    
}
```
- mapper接口代码
```java
// 包名要包含在入口类中DbInitVerticle的@ComponentInitializer
// 注解的value中，才会被扫描到
package com.yjl.sample.mapper;

import com.yjl.vertx.base.autoroute.anno.AutoRouteIf;
import com.yjl.vertx.base.autoroute.anno.AutoRouteIfMethod;
import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.anno.operation.Select;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

// @Dao注解表示这是一个mapper接口
@Dao
// @AutoRouteIf注解定义了web访问该接口的路径，
// 类似于spring中的@Controller，没有该注解则使用类名
@AutoRouteIf("wxChannel")
public interface WxChannelMapper {
    
    // 这个注解很熟悉吧^_^，跟mybatis用法接近
    @Select("select wxappID, channelID, wxsecret, barCodeURL, information from t_wxchannel where wxappID = #{appId}")
    // 这个注解定义了web访问该接口的路径，同@AutoRouteIf拼接起来构成实际路径
    // route则定义了采用什么样的HTTP方法访问，
    // 同样如果没有该注解会使用方法名+GET方法来作为缺省值
    @AutoRouteIfMethod(value = "channel", route = HttpMethod.POST)
    Future<JsonObject> getChannelInfo(@Param("appId") String appId);
}
```
- 然后是配置文件
  1. resources/config.${profile}.json：主要是服务的相关参数配置
  2. resources/META-INF/services/io.vertx.core.spi.launcher.CommandFactory：定义了一个快速启动的cli命令行
  3. pom.xml：不算是配置文件，不过定义了一些profile和打成fat-jar的插件和参数
  4. 具体参考[工程源码](https://github.com/aaavieri/vertx-base-sample/tree/master/vertx-base-sample1)
  
好了，我们来试试运行吧
- 把sample工程中的init.sql导入数据库
- 在IDE中debug运行
```
MainClass：io.vertx.core.Launcher
ProgramArguments：runProfile com.yjl.sample.vercicle.DbInitVerticle -profile=dev
```
- 在fat-jar中运行
```
// 先打包
mvn clean package
cd target
// 开始运行
java -jar vertx-base-sample1-1.0.0-SNAPSHOT-fat.jar -profile=dev
```
- 尝试运行
```
curl http://localhost:4202/wxChannel/getChannelInfo?appId=testAppId
{"wxappID":"testAppId","channelID":1,"wxsecret":"testSecret","barCodeURL":null,"information":null}
```

OK！一个简单的web工程就做好了
### 3，another further project
来吧，我们再来写一个调用webclient的web接口工程吧，Emmm，为了方便起见，
就直接调用【getting started】里面的工程吧
[工程源码地址](https://github.com/aaavieri/vertx-base-sample/tree/master/vertx-base-sample2)

开始撸代码吧：
- 同样需要一个入口类：
```java
package com.yjl.sample.vercicle;

import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.verticle.InitVerticle;
import com.yjl.vertx.base.webclient.factory.AutoRouteWebClientFactory;

// 这里的factoryClass变了，因为实现的功能不同
@ComponentInitializer(factoryClass = AutoRouteWebClientFactory.class, value = "com.yjl.sample.client")
public class WebClientVerticle extends InitVerticle {
}
```
- 同样也需要一个接口，不过它是一个webclient的接口(类似于feignClient)
```java
package com.yjl.sample.client;

import com.yjl.vertx.base.autoroute.anno.AutoRouteIf;
import com.yjl.vertx.base.autoroute.anno.AutoRouteIfMethod;
import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.webclient.anno.component.RequestClient;
import com.yjl.vertx.base.webclient.anno.request.Request;
import com.yjl.vertx.base.webclient.anno.request.RequestData;
import com.yjl.vertx.base.webclient.enumeration.RequestDataType;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

// webclient要访问接口的地址
@RequestClient(port = 4202, host = "localhost")
// 跟WxChannelMapper类里面的注解作用相同
@AutoRouteIf("localWxChannel")
public interface LocalWebClient {

	// 要访问的路径，以及HTTP方法
	@Request(path = "wxChannel/getChannelInfo", method = HttpMethod.GET)
	// 访问(注意不是被访问)接口参数类型：以查询参数的形式
	// key是指访问时传递的参数名，paramKey是传入到当前接口的参数名
	@RequestData(type = RequestDataType.QUERY_PARAM, key = "appId", paramKey = "a")
	@AutoRouteIfMethod("testFirst")
	// @Param("a")就是对应的@RequestData注解中的paramKey = "a"
	Future<JsonObject> testFirst(@Param("a") String a);
}
```
- 然后跟【getting started】中的工程一样的打包或者debug，只不过main-verticle要改成com.yjl.sample.vercicle.WebClientVerticle
- 最后测试一下吧：
```
curl http://localhost:4203/localWxChannel/testFirst?a=testAppId
{"wxappID":"testAppId","channelID":1,"wxsecret":"testSecret","barCodeURL":null,"information":null}
```
