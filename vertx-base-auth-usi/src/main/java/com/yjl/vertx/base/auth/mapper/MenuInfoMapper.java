package com.yjl.vertx.base.auth.mapper;

import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.dao.anno.operation.Select;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface MenuInfoMapper {
    @Select("select mi.server_uri, urr.role_logic_id, mi.require_auth " +
        "from st_user_role_relation urr inner join st_role_menu_relation rmr on (urr.role_logic_id = rmr.role_logic_id) " +
        "inner join st_menu_info mi on (rmr.menu_id = mi.id) where urr.account = #{account} and mi.enabled = true")
    Future<List<JsonObject>> selectUserMenus(@Param("account")final String account);
}
