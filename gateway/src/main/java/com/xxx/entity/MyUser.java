package com.xxx.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.sql.Date;
import java.util.Collection;
import java.util.Set;

/**
 * @Author 王川
 * @Date 2020/3/17 15:27
 **/
@Data
public class MyUser  {

    private int id;
    private String memberName;
    private String password;
    private String mobile;
    private String email;
    private short sex;
    private Date birthday;
    private Date createTime;
    private Set<Role> roles;


}
