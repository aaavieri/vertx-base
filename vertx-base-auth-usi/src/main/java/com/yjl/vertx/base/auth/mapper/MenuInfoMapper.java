package com.yjl.vertx.base.auth.mapper;

import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.dao.anno.operation.Select;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

public interface MenuInfoMapper {
    @Select("select id, account, password, type, locked, next_login_change_pwd, pwd_no_exceed, account_exceed_date " +
        "from st_account_info where false")
    Future<List<JsonObject>> selectUserMenus(@Param("account")final String account);
}
