package com.yjl.vertx.base.auth.mapper;

import com.yjl.vertx.base.com.anno.Param;
import com.yjl.vertx.base.dao.anno.operation.Select;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public interface AccountMapper {
    
    @Select("select id, account, password, type, locked, next_login_change_pwd, pwd_no_exceed, account_exceed_date " +
        "from st_account_info where account = #{account} limit 1")
    Future<JsonObject> selectAccount(@Param("account")final String account);
}
