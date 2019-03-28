package com.yjl.vertx.base.test.dbmapper;

import com.yjl.vertx.base.dao.anno.component.AutoRouteDao;
import com.yjl.vertx.base.dao.anno.component.AutoRouteDaoMethod;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.anno.operation.Select;
import com.yjl.vertx.base.dao.anno.operation.SqlParam;
import com.yjl.vertx.base.web.enumeration.RouteMethod;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

@Dao
@AutoRouteDao("var")
public interface WxUserMapper {

	@Select("select wxappID, openID, clientID, channelID, unionid, phoneNum, information, sessionKey, token, tokenStartTime, insertUser, insertTime, updateUser, updateTime from t_wxuser " +
		"where wxappID = #{wxappID} and openid = #{openID}")
	@AutoRouteDaoMethod(route = RouteMethod.POST)
	Future<JsonObject> getWxUser(@SqlParam("wxappID") final String appId, @SqlParam("openID")final String openid);
}
