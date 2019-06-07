package com.yjl.sample.mapper;

import com.yjl.vertx.base.autoroute.anno.AutoRouteIf;
import com.yjl.vertx.base.autoroute.anno.AutoRouteIfMethod;
import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.dao.anno.component.Dao;
import com.yjl.vertx.base.dao.anno.operation.Select;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

@Dao
@AutoRouteIf("client")
public interface ClientMapper {
    
    @Select("select clientID, channelID, wechatID, phoneNum, standard, payType, status, score, attentionTime, bindTime, cancelTime, recommendPhone, " +
                "sceneStr from t_client where clientID = #{clientID}")
    @AutoRouteIfMethod("findByClientId")
    Future<JsonObject> findByClientId(@Param("clientID") final int clientID);
}
