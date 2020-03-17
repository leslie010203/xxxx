package com.xxx.service;


import com.xxx.entity.MyUser;
import com.xxx.entity.OauthUser;
import com.xxx.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @Author 王川
 * @Date 2020/3/17 15:24
 **/

@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户信息
        MyUser userinfo = userMapper.getUserByUsername(username);
        //需要构造org.springframework.security.core.userdetails.User 对象包含账号密码还有用户的角色
        if (userinfo != null) {
            User user = new User(userinfo.getUsername(), userinfo.getPassword(), AuthorityUtils.createAuthorityList("admin"));
            return new OauthUser(userinfo, user);
        } else {
            throw new UsernameNotFoundException("用户[" + username + "]不存在");
        }
    }
}
