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
```console
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
```console
MainClass：io.vertx.core.Launcher
ProgramArguments：runProfile com.yjl.sample.vercicle.DbInitVerticle -profile=dev
```
- 在fat-jar中运行
```console
// 先打包
mvn clean package
cd target
// 开始运行
java -jar vertx-base-sample1-1.0.0-SNAPSHOT-fat.jar -profile=dev
```
- 尝试运行
```console
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
- #### webclient的接口定义：
同样也需要一个接口，不过它是一个webclient的接口(类似于feignClient)
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
```console
curl http://localhost:4203/localWxChannel/testFirst?a=testAppId
{"wxappID":"testAppId","channelID":1,"wxsecret":"testSecret","barCodeURL":null,"information":null}
```
### 4, future is ours
随便用了一个war3的梗儿，接下来我们写一点复杂的工程，这就涉及到vert.x的异步API了，
传统意义上逻辑代码都是同步代码，而vert.x的最大的特点就是异步调用IO操作等耗时较多的操作
(包含但不限于http通信、DB查询、文件操作)，从而达到非阻塞的效果，
并将性能提升到很高水平。
- vert.x的异步API中最重要的一个类就是Future了，来看看怎么使用吧。
```java
// 调用一个dbmapper检索数据，得到一个future，
// future并不能立即马上得到结果，而是要给它一个handler，让它在得到结果之后调用该handler
// 的处理
Future<JsonObject> future = this.wxUserMapper.getWxUser("a", "b");
future.setHandler(as -> {
    // as.succeeded()是获取异步执行future的调用成功与否，
    // 成功的话就可以通过result()方法拿到结果
    if (as.succeeded()) {
        JsonObject result = as.result();
    } else {
        // as.cause()就是导致失败的原因
        Throwable cause = as.cause();
    }
});
```
- 接下来通过Future调用来实现【2，getting started】中的工程同样的效果，
也就是返回mapper调用的结果，同样需要一个verticle入口类：
```java
package com.yjl.sample.verticle;

import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.anno.initializer.OverrideDependency;
import com.yjl.vertx.base.com.verticle.InitVerticle;
import com.yjl.vertx.base.dao.factory.DaoFactory;
import com.yjl.vertx.base.web.factory.component.RestHandlerV2Factory;
import com.yjl.vertx.base.web.factory.component.RestRouteV2Factory;

// 因为要自定义handler的路径，所以要用@OverrideDependency注解，用来覆盖预定义的工厂依赖关系
// RestHandlerV2Factory工厂生成处理请求的handler，DaoFactory根据mapper接口生成代理类
// RestRouteV2Factory工厂把生成的handler绑定到具体的url和请求方法
@OverrideDependency(value = @ComponentInitializer(factoryClass = RestRouteV2Factory.class),
	customInclude = {@ComponentInitializer(factoryClass = RestHandlerV2Factory.class, value = "com.yjl.sample.handler"),
		@ComponentInitializer(factoryClass = DaoFactory.class, value = "com.yjl.sample.mapper")})
public class Sample3Verticle extends InitVerticle {
}
```
- 然后是简单的mapper，除了少了注解其它的跟【2，getting started】中的一样
```java
package com.yjl.sample.mapper;

import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.anno.operation.Select;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

// 少了@AutoRouteIf注解，以及方法的@AutoRouteIfMethod
@Dao
public interface WxChannelMapper {
    @Select("select wxappID, channelID, wxsecret, barCodeURL, information from t_wxchannel where wxappID = #{appId}")
    Future<JsonObject> getChannelInfo(@Param("appId") String appId);
}
```
- 最后是一个handler
```java
package com.yjl.sample.handler;

import com.google.inject.Inject;
import com.yjl.sample.mapper.WxChannelMapper;
import com.yjl.vertx.base.web.anno.component.RestRouteMapping;
import com.yjl.vertx.base.web.anno.component.RestRouteV2Handler;
import com.yjl.vertx.base.web.handler.BaseRouteV2Handler;
import com.yjl.vertx.base.web.util.ContextUtil;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

// 定义了绑定的两个URL，其中第一个是POST
@RestRouteV2Handler({@RestRouteMapping(value = "/test/1/:appId", method = HttpMethod.POST), @RestRouteMapping("/test/2")})
public class Sample3Handler extends BaseRouteV2Handler {
	
	// @Inject是使用了Guice框架的依赖注入
	@Inject
	private WxChannelMapper wxChannelMapper;
	
	@Override
	// routingContext就是request相关的上下文
	public Future<Void> handleSuccess(RoutingContext routingContext) {
		// 获取请求参数
		JsonObject params = ContextUtil.getParam(routingContext);
		// 这里的compose下一节细说，这里就是把返回值转换成一个统一数据类型的Future，
		// 方便被父类的handle方法调用
		return this.wxChannelMapper.getChannelInfo(params.getString("appId")).compose(jsonObject -> {
			// 把mapper的future结果也就是检索结果写入到response中 
			routingContext.response().end(jsonObject.put("path", routingContext.request().path()).toBuffer());
			// 重新定义一个新的Future，作为统一接口返回用
			Future<Void> future = Future.future();
			// 直接完成Future
			future.complete();
			// 返回该Future
			return future;
		});
	}
}
```
- 以及它的父类
```java
package com.yjl.vertx.base.web.handler;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public abstract class BaseRouteV2Handler {
    @Inject
    @Named("defaultFailureHandler")
    // 默认的异常处理handler
    private Handler<RoutingContext> defaultFailureHandler;

    public BaseRouteV2Handler() {
    }

    public void handle(RoutingContext context) {
        // 调用handleSuccess之后得到Future<Void>，并给它一个handler
        this.handleSuccess(context).setHandler((voidAsyncResult) -> {
            // 失败的时候去调用失败的处理
            if (voidAsyncResult.failed()) {
                context.fail(voidAsyncResult.cause());
                this.handleFailure(context);
            }
        });
    }

    public abstract Future<Void> handleSuccess(RoutingContext var1);

    protected void handleFailure(RoutingContext context) {
        this.defaultFailureHandler.handle(context);
    }
}
```
- 之前出现的compose是什么鬼？
故名思义，就是把多个future组合起来的一个API，因为Future异步执行，所以需要设定一个handler
给它，然后呢？如果handler里面继续调用异步操作得到一个新的Future，这个Future再来一个handler
，就会形成一个可怕的回调地狱，让我们来感受一下它的魅力吧^_^
```java
    Future<JsonObject> clientInfoFuture = this.clientMapper.findByClientId(clientId);
    clientInfoFuture.setHandler(as -> {
        if (as.succeeded()) {
            String openId = as.result().getString("openId");
            String appId = as.result().getString("appId");
            Future<JsonObject> userInfoFuture = this.wxUserMapper.getWxUser(appId, openId);
            userInfoFuture.setHandler(as2 -> {
                if (as2.succeeded()) {
                    Future<JsonObject> channelFuture = this.wxChannelMapper.getChannelInfo(appId);
                    channelFuture.setHandler(as3 -> {
                        if (as3.succeeded()) {
                            routingContext.response().end(new JsonObject().mergeIn(as.result(), true)
                                .mergeIn(as2.result(), true)
                                .mergeIn(as3.result(), true).toBuffer());
                        }
                    });
                }
            });
        }
    });
```
而我们换成不完全等价（略有出入）的compose写法，是不是看起来清爽许多
```java
    final JsonObject data = new JsonObject();
    this.clientMapper.findByClientId(clientId).compose(clientInfo -> {
        // compose中的类似之前handler内容只处理succeeded时的result
        // 失败的处理会在最后的handler统一处理
        data.mergeIn(clientInfo, true);
        String openId = clientInfo.getString("openId");
        String appId = clientInfo.getString("appId");
        return this.wxUserMapper.getWxUser(appId, openId);
    }).compose(userInfo -> {
        data.mergeIn(userInfo, true);
        return this.wxChannelMapper.getChannelInfo(data.getString("appId"));
    }).setHandler(as -> {
        if (as.succeeded()) {
            data.mergeIn(as.result(), true);
            routingContext.response().end(data.toBuffer());
        }
    });
```
- 还有另外一个CompositeFuture的API，用于并行执行，类似于js里面的Promise.all。
```java
    JsonObject params = ContextUtil.getParam(routingContext);
    // 这是检索channel的Future
    Future<JsonObject> channelFuture = this.wxChannelMapper.getChannelInfo(params.getString("appId"));
    // 检索用户列表的Future
    Future<JsonArray> userListFuture = this.wxUserMapper.getAppUserList(params.getString("appId"));
    // 并行执行两个Future，然后取到结果写入到response中
    return CompositeFuture.all(channelFuture, userListFuture).compose(compositeFuture -> {
        JsonObject channelInfo = compositeFuture.resultAt(0);
        JsonArray userList = compositeFuture.resultAt(1);
        routingContext.response().end(new JsonObject().put("channel", channelInfo).put("user", userList).toBuffer());
        Future<Void> future = Future.future();
        future.complete();
        return future;
    });
```
CompositeFuture还有一些其它的API，请自行阅读vert.x的源码
### 5，mapper的详细介绍
在【2，getting started】中已经介绍了如何使用mapper的基础功能，接下来详细地介绍其规则
- Dao工厂：DaoFactory，需要被添加为依赖被申明出来，
并在value中设置mapper的包路径，源码在[sample3](https://github.com/aaavieri/vertx-base-sample/tree/master/vertx-base-sample3)中
```java
@OverrideDependency(value = @ComponentInitializer(factoryClass = RestRouteV2Factory.class),
	customInclude = {@ComponentInitializer(factoryClass = RestHandlerV2Factory.class, value = "com.yjl.sample.handler"),
		// DaoFactory的定义在这里
		@ComponentInitializer(factoryClass = DaoFactory.class, value = "com.yjl.sample.mapper")})
```
- 增删改查功能：
  1. @Select注解：查询
  2. @Insert注解：插入
  3. @Update注解：更新
  4. @Delete注解：删除
- 支持的sql文：
  1. 直接书写的SQL文
        ```java
        @Select("select clientID, channelID, wechatID, phoneNum, standard, payType, status, score, attentionTime, bindTime, cancelTime, recommendPhone, " +
                    "sceneStr from t_client where clientID = #{clientID}")
        Future<JsonObject> findByClientId(@Param("clientID") final int clientID);
        ```
  2. foreach循环的SQL文(语法和mybatis的foreach一样)
      ```java
      @Select("select wxappID, openID, clientID, channelID, unionid, phoneNum, information, sessionKey, token, tokenStartTime, insertUser, insertTime, updateUser, updateTime from t_wxuser " +
          "where wxappID in " +
          "<foreach collection='appList' item='app' open='(' close=')' separator=','>#{app}</foreach>")
      Future<JsonArray> getAppUserList(@Param("appList") List<String> appIdList);
      ```
  3. 其它的SQL文：暂未支持
- 入参的支持类型
  1. 基本类型
  2. String、Instant、BigDecimal
  3. Map
  4. Vert.x自带的JsonObject、JsonArray

  注意：暂时不支持Bean!
- 返回值的支持类型
  1. 检索：
     + JsonArray
     + JsonObject
     + List<JsonObject>
     + List<Map<String, Object>>
     + Map<String, Object>
  2. 更新、删除、插入
     + Integer
     + String
     + JsonArray
- 返回值类型扩展：重写返回值适配工厂DaoAdaptorFactory的依赖
```java
package com.yjl.sample.verticle;

import com.yjl.vertx.base.com.anno.initializer.ComponentInitializer;
import com.yjl.vertx.base.com.anno.initializer.OverrideDependency;
import com.yjl.vertx.base.com.verticle.InitVerticle;
import com.yjl.vertx.base.dao.factory.DaoAdaptorFactory;
import com.yjl.vertx.base.dao.factory.DaoFactory;
import com.yjl.vertx.base.web.factory.component.RestHandlerV2Factory;
import com.yjl.vertx.base.web.factory.component.RestRouteV2Factory;

@OverrideDependency(value = @ComponentInitializer(factoryClass = RestRouteV2Factory.class),
	customInclude = {@ComponentInitializer(factoryClass = RestHandlerV2Factory.class, value = "com.yjl.sample.handler"),
		@ComponentInitializer(factoryClass = DaoFactory.class, value = "com.yjl.sample.mapper")})
// DaoAdaptorFactory是适配返回值类型的适配器工厂，com.yjl.vertx.base.dao.adaptor包下面是默认的适配器
// 当有需要加入新的返回值类型的时候，可以创建新的适配器，并加入到DaoAdaptorFactory工厂的扫描范围中
@OverrideDependency(value = @ComponentInitializer(factoryClass = DaoAdaptorFactory.class,
	value = {"com.yjl.vertx.base.dao.adaptor", "com.yjl.sample.adaptor"}))
public class Sample3Verticle extends InitVerticle {
}
```
```java
package com.yjl.sample.adaptor;

import com.yjl.vertx.base.dao.adaptor.AbstractDaoAdaptor;
import com.yjl.vertx.base.dao.enumeration.SqlOperation;
import io.vertx.ext.sql.ResultSet;
// com.yjl.sample.adaptor包下面的适配器，加入之后就可以适配String类型的数据了，这里的适配逻辑自己写
public class SelectStringAdaptor extends AbstractDaoAdaptor<ResultSet, String> {
	@Override
	public SqlOperation matchOperation() {
		return SqlOperation.SELECT;
	}
	
	@Override
	public String adapt(ResultSet resultSet) {
		return resultSet.getRows().isEmpty() ? "" : resultSet.getRows().get(0).getInteger("channelID").toString();
	}
}
```
### 6，webclient的详细介绍
- webclient工厂：WebClientFactory，需要被添加为依赖被申明出来。
- [例子参考](#webclient的接口定义)