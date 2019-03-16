package com.yjl.vertx.base.test.dbmapper;

import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.anno.operation.Select;
import com.yjl.vertx.base.dao.anno.operation.SqlParam;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

@Dao
public interface WxUserMapper {

	@Select("select wxappID, openID, clientID, channelID, unionid, phoneNum, information, sessionKey, token, tokenStartTime, insertUser, insertTime, updateUser, updateTime from t_wxuser " +
		"where wxappID = #{wxappID} and openid = #{openID}")
	Future<JsonObject> getWxUser(@SqlParam("wxappID") final String appId, @SqlParam("openID")final String openid);
}
