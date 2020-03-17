package com.xxx.auth;

import com.xxx.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

/**
 * @Author 王川
 * @Date 2020/3/17 15:15
 **/

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    // 认证管理器
    @Autowired
    private AuthenticationManager authenticationManager;
    // redis连接工厂
    /*
    @Autowired
    private JedisConnectionFactory JedisConnectionFactory;
    */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataSource dataSource;

    /**
     * 令牌存储
     * @return redis令牌存储对象
     */
    /*@Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(JedisConnectionFactory);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(this.authenticationManager);
        endpoints.tokenStore(tokenStore());
    }*/

    @Bean
    protected JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new MyJwtAccessTokenConverter();
        converter.setKeyPair(RSAUtil.GetKeyPair());
        return converter;
    }


}
