package com.yjl.vertx.base.test.dbmapper;

import com.yjl.vertx.base.autoroute.anno.AutoRouteIf;
import com.yjl.vertx.base.autoroute.anno.AutoRouteIfMethod;
import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.anno.operation.Select;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

@Dao
@AutoRouteIf("var")
public interface WxUserMapper {

	@Select("select wxappID, openID, clientID, channelID, unionid, phoneNum, information, sessionKey, token, tokenStartTime, insertUser, insertTime, updateUser, updateTime from t_wxuser " +
		"where wxappID = #{wxappID} and openid = #{openID}")
	@AutoRouteIfMethod(route = HttpMethod.POST)
	Future<JsonObject> getWxUser(@Param("wxappID") final String appId, @Param("openID") final String openid);
}
