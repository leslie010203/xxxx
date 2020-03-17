package com.xxx.service;


import com.xxx.entity.MyUser;
import com.xxx.entity.OauthUser;
import com.xxx.entity.Permission;
import com.xxx.entity.Role;
import com.xxx.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

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
            if (userinfo == null) {
                throw new UsernameNotFoundException(username);
            }
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            // 可用性 :true:可用 false:不可用
            boolean enabled = true;
            // 过期性 :true:没过期 false:过期
            boolean accountNonExpired = true;
            // 有效性 :true:凭证有效 false:凭证无效
            boolean credentialsNonExpired = true;
            // 锁定性 :true:未锁定 false:已锁定
            boolean accountNonLocked = true;
            for (Role role : userinfo.getRoles()) {
                //角色必须是ROLE_开头，可以在数据库中设置
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getRoleName());
                grantedAuthorities.add(grantedAuthority);
                //获取权限
                for (Permission permission : role.getPermissions()) {
                    GrantedAuthority authority = new SimpleGrantedAuthority(permission.getUri());
                    grantedAuthorities.add(authority);
                }
            }
            User user = new User(userinfo.getMemberName(), userinfo.getPassword(),
                    enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, grantedAuthorities);
            return new OauthUser(userinfo, user);
        } else {
            throw new UsernameNotFoundException("用户[" + username + "]不存在");
        }
    }
}
