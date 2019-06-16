package com.yjl.vertx.base.auth.component;

import com.google.inject.Inject;
import com.yjl.vertx.base.auth.dto.AuthenticationResult;
import com.yjl.vertx.base.auth.mapper.AccountMapper;
import com.yjl.vertx.base.auth.mapper.MenuInfoMapper;
import com.yjl.vertx.base.com.exception.FrameworkException;
import com.yjl.vertx.base.com.util.DateUtil;
import com.yjl.vertx.base.com.util.StringUtil;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.Date;

public class UsiAuthenticationComponent implements AuthenticationComponentIf {
    
    @Inject
    private AccountMapper accountMapper;
    
    @Inject
    private MenuInfoMapper menuInfoMapper;
    
    @Override
    public Future<AuthenticationResult> authenticate(JsonObject headers, JsonObject params) {
        AuthenticationResult authenticationResult = new AuthenticationResult();
        return this.accountMapper.selectAccount(params.getString("account"))
            .compose(userInfo -> {
                if (userInfo == null) {
                    throw new FrameworkException().message("unknown user: " + params.getString("account"));
                }
                if (userInfo.getBoolean("locked")) {
                    throw new FrameworkException().message("user was locked: " + params.getString("account"));
                }
                if (userInfo.getBoolean("next_login_change_pwd")) {
                    throw new FrameworkException().message("you should change your password").errCode(-1);
                }
                String exceedDate = userInfo.getString("account_exceed_date");
                if (!StringUtil.isBlank(exceedDate)
                    && DateUtil.parseDate(DateUtil.YYYYMMDD, exceedDate).before(new Date())) {
                    throw new FrameworkException().message("expired password").errCode(-1);
                }
                if (userInfo.getString("password").equals(params.getString("password"))) {
                    authenticationResult.result(true).userInfo(userInfo);
                } else {
                    authenticationResult.result(false);
                }
                return Future.succeededFuture(authenticationResult);
            })
            .compose(result -> {
                if (result.result()) {
                    return this.menuInfoMapper.selectUserMenus(params.getString("account"))
                        .compose(menus -> {
                            result.userInfo().put("userMenus", menus);
                            return Future.succeededFuture(result);
                        });
                } else {
                    return Future.succeededFuture(result);
                }
            });
    }
}
