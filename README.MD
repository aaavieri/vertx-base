# readme

### 1，使用前的话
本框架是基于vert.x二次开发的，采用guice进行依赖注入管理的，为获得最佳体验，可参考以下文档。

[vert.x中文文档地址](http://vertxchina.github.io/vertx-translation-chinese/)

[vert.x英文文档地址](https://vertx.io/docs/)

[google guice框架文档地址](https://github.com/google/guice/wiki/GettingStarted)

[sample地址](https://github.com/aaavieri/vertx-base-sample)

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
- #### 返回值类型扩展
  重写返回值适配工厂DaoAdaptorFactory的依赖
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
- @RequestClient注解说明：
  1. host：地址
  2. port：端口（不能用默认缺省）
  3. ssl：是否是https
  4. initLevel：真正访问用的webclient的初始化等级（默认是全局共享）
- @Request注解说明：
  1. path：路径
  2. initLevel：同上，两个注解中的initLevel会组合在一起生效
  3. method：http请求方法，默认是GET
- @RequestData注解说明：
  1. type：数据类型，包括header，query参数，form表单参数以及json参数
  2. key：通过webclient访问时传递的参数名
  3. value：固定值，即不是传入的参数而是固定传参
  4. paramKey：传入到当前接口的参数名
  5. whole：如果为true，则只传递该参数而忽略其它参数（只在typ是json时生效），并把它作为参数集，这个相对复杂，请看下面这个在
  sample2工程中的例子
     ```java
     @Request(path = "wxChannel/getChannelInfo", method = HttpMethod.GET)
     @RequestData(type = RequestDataType.JSON_PARAM, key = "appId", paramKey = "whole", whole = true)
     @AutoRouteIfMethod(value = "testSecond", route = HttpMethod.POST)
     Future<JsonObject> testSecond(@Param("whole") JsonObject whole);
     ```
     运行sample1和sample2两个工程，然后测试
     ```console
     curl -X POST -H "Content-Type:application/json" -d '{"whole": {"appId": "testAppId"}}' http://localhost:4203/localWxChannel/testSecond
     {"wxappID":"testAppId","channelID":1,"wxsecret":"testSecret","barCodeURL":null,"information":null}
     ```
- 支持的返回类型
  1. Buffer：vert.x框架自带的一种类似于String的结构，非常推荐
  2. String：默认编码的字符串
  3. JsonArray
  4. JsonObject
- 支持的返回类型的扩展，同[Dao的Adaptor的扩展类似](#返回值类型扩展)，书写新的adaptor，并被加入到
工厂定义的value中
### 7，profile
- 各个sample工程中，都在resources/META-INF/services/io.vertx.core.spi.launcher.CommandFactory
中定义了一个快速启动的cli命令行的class，有了它就可以使用runProfile来运行verticle，并为其指定约定的配置文件
- 配置文件默认使用resources/config.${profile}.json
- mvn打包时加入profile，这里的profile是mvn环境的profile，vertx.profile属性才是vertx的profile
    ```xml
    <profiles>
        <profile>
            <id>dev</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <vertx.profile>dev</vertx.profile>
            </properties>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <vertx.profile>test</vertx.profile>
            </properties>
        </profile>
        <profile>
            <id>prod</id>
            <properties>
                <vertx.profile>prod</vertx.profile>
            </properties>
        </profile>
    </profiles>
    ```
- 在fatjar打包插件中使用定义的profile以及runProfile命令
    ```xml
    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>${exec-maven-plugin.version}</version>
        <configuration>
            <mainClass>io.vertx.core.Launcher</mainClass>
            <arguments>
                <argument>runProfile</argument>
                <argument>${main.verticle}</argument>
                <argument>-profile=${vertx.profile}</argument>
            </arguments>
        </configuration>
    </plugin>
    ```
- pom.xml的具体写法参考[pom.xml示例](https://github.com/aaavieri/vertx-base-sample/blob/master/vertx-base-sample1/pom.xml)
### 8，工厂的使用
- 工厂使用guice框架实现IOC功能
- 使用时添加main-verticle类注解，以让工厂生效，共有三个注解供声明用
  1. @FirstClassComponent注解：头等工厂，同等条件下会被最优先初始化执行其中的IOC操作
  2. @OverrideDependency注解：复写依赖工厂，会将其value对应的工厂的依赖重写
  3. @ComponentInitializer注解：普通工厂，会在factoryClass中声明对应的工厂类，并在工厂类的类注解中
  声明该工厂所依赖的其它工厂，如
  ```java
    package com.yjl.vertx.base.dao.factory;
    
    // 默认依赖mysql工厂，如果不用mysql数据库，则需要在main-verticl类注解中，
    // 用@OverrideDependency注解进行复写
    @ComponentInitializer(factoryClass = MysqlSqlClientFactory.class)
    // 返回值适配工厂，如果有新的返回值类型要处理，也应复写
    @ComponentInitializer(factoryClass = DaoAdaptorFactory.class, value = "com.yjl.vertx.base.dao.adaptor")
    @ComponentInitializer(factoryClass = SqlExecutorFactory.class, value = "com.yjl.vertx.base.dao.executor")
    @ComponentInitializer(factoryClass = DaoContextCacheFactory.class)
    public class DaoFactory extends BaseAnnotationComponentFactory {
    }
  ```
- 工厂初始化顺序：
  1. 根工厂，默认是VertxResourceFactory，可以通过在verticle中重写getRootFactory方法修改
  2. @FirstClassComponent注解中的工厂，默认只有一个SimpleSlf4jLogbackFactory，可自行添加，用于日志输出，配置参数大致如下：
  ```json
    "logbackConfig": {
      "file": "./var/log/wwx-mini-vertx.log",
      "level": {
        "root": "DEBUG",
        "com.yjl.vertx.base": "DEBUG",
        "com.yjl.vertx.base.test": "DEBUG"
      }
    }
  ```
  3. @OverrideDependency注解中的工厂
  4. @ComponentInitializer注解中的工厂
  5. 初始化工厂时各法执行顺序：
    + beforeConfigure：自动注入之前的操作
    + configure：使用guice框架自动注入的逻辑
    + afterConfigure：自动注入之后的操作
- @OverrideDependency注解说明：
  1. value：要重写依赖的工厂定义
  2. customInclude：value声明的工厂依赖中，需要额外添加的工厂（在原有基础上添加）
  3. customExclude：value声明的工厂依赖中，需要排队的工厂
  4. customAll：value声明的工厂依赖全部废弃，而使用customAll中声明的工厂，
  使用它的话会导致customInclude和customExclude不生效
  5. dependNothing：为true表示不依赖任何工厂，将无示customInclude、customExclude、
  customAll声明的内容
- 框架已经实现的常用框架一览：
  1. VertxResourceFactory：注入vertx基础组件，包括vertx对象以及config
  2. SimpleSlf4jLogbackFactory：简单的日志组件，不自动注入而使用代码获取
  ```java
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  ```
  3. CommonComponentFactory：注入常用service组件
  4. JdbcSqlClientFactory：注入JDBC数据库连接SQLClient
  5. MysqlSqlClientFactory：注入mysql数据库连接SQLClient，采用了vertx特别定制的异步驱动
  6. DaoFactory：给mapper接口提供动态代理，并自动注入
  7. SqlExecutorFactory：自动注入执行sql文工具，一般会被DaoFactory所依赖，而非直接使用
  8. DaoAdaptorFactory：自动注入mapper执行结果适配，一般会被DaoFactory所依赖，而非直接使用
  ，也可以添加新的适配种类
  9. DaoContextCacheFactory：自动注入mapper动态代理相关的一些缓存，一般会被DaoFactory所依赖，而非直接使用
  10. HttpServerFactory：自动注入vertx创建的httpServer，一般会被RestRouteV2Factory所依赖，而非直接使用
  11. DefaultFailureHandlerFactory：自动注入http请求时失败时的handler，一般会被RestRouteV2Factory所依赖，而非直接使用
  12. RestRouteV1Factory：自动注入响应http请求的handler，不推荐使用，更推荐v2
  13. RestRouteV2Factory：自动注入响应http请求的handler，推荐
  14. RestHandlerV1Factory：扫描handler并注入到RestRouteV1Factory中供其绑定httpServer，
  被RestRouteV1Factory所依赖，而非直接使用
  15. RestHandlerV2Factory：同上
  16. WebClientFactory：为webclient接口生成动态代理，并自动注入
  17. RequestExecutorFactory：自动注入http请求执行工具到WebClientFactory，被WebClientFactory
  所依赖，不直接使用
  18. ResponseAdaptorFactory：自动注入http响应适配器到WebClientFactory，被WebClientFactory
  所依赖，不直接使用，可以根据返回类型的需要添加新的响应类型
  19. WebClientContextCacheFactory：自动注入webclient动态代理相关的一些缓存，
  一般会被WebClientFactory所依赖，而非直接使用
  20. AutoRouteDaoFactory：自动将mapper接口转换成http请求接口，即不用handler，也能处理请求路径
  与mapper的@AutoRouteIf注解+@AutoRouteMethod注解一致的请求
  21. AutoRouteWebClientFactory：自动将webclient接口转换成http请求接口，即不用handler，也能处理请求路径
  与webclient的@AutoRouteIf注解+@AutoRouteMethod注解一致的请求
  
  上述Factory在sample1、2、3中几乎都能找到例子，请参考[再发一次sample地址](https://github.com/aaavieri/vertx-base-sample)
### 9，game over
没想到我也能写这么多的md文档，也许这就是爱吧^_^