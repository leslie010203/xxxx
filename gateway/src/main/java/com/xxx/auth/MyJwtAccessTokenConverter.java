package com.xxx.auth;

import com.xxx.entity.OauthUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;

/**
 * @Author 王川
 * @Date 2020/3/17 15:22
 **/
public class MyJwtAccessTokenConverter extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (accessToken instanceof DefaultOAuth2AccessToken) {
            //((DefaultOAuth2AccessToken) accessToken).setRefreshToken();
            Object principal = authentication.getPrincipal();
            if (principal instanceof OauthUser) {
                OauthUser user = (OauthUser) principal;
                HashMap<String, Object> map = new HashMap<>();
                map.put("user_id", user.getBaseOperator().getId());
                map.put("phone", user.getBaseOperator().getMobile());
                ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(map);
            }
        }
        return super.enhance(accessToken, authentication);
    }

}
