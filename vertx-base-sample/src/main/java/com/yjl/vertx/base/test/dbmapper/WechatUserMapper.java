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
@AutoRouteIf("wechatUser")
public interface WechatUserMapper {
    
    @Select("select clientID, channelID, openID, nickname, sex, language, city, province, country, headimgurl, subscribe_time, unionid, " +
                "remark, groupid, tagid_list from t_wechatuser where unionid = #{unionid}")
    @AutoRouteIfMethod(value = "findByClientId", route = HttpMethod.POST)
    Future<JsonObject> findByUnionId(@Param("unionid") final String unionid);
}
