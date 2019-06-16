package com.yjl.vertx.base.auth.factory;

import com.google.inject.Inject;
import com.yjl.vertx.base.com.anno.component.Config;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

public class PublicKeyJwtAuthProviderFactory extends AbstractJwtAuthProviderFactory {
    
    @Inject(optional = true)
    @Config("auth.jwt.algorithm")
    private String algorithm = "HS256";
    
    @Inject(optional = true)
    @Config("auth.jwt.publicKey")
    private String publicKey = "public-key-135246";
    
    @Override
    protected JWTAuthOptions getJwtAuthOptions() {
        return new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                .setAlgorithm(this.algorithm)
                .setPublicKey(this.publicKey)
                .setSymmetric(true));
    }
}
