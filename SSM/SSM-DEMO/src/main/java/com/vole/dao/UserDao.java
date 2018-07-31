package com.vole.dao;

import com.vole.entity.User;

import java.util.Set;

/**
 * 编写者： vole
 * Time： 2018/7/24.16:22
 * 内容：User Dao 接口
 */
public interface UserDao {

    /**
     * 通过用户名查询用户
     * @param userName 用户名
     * @return 用户
     */
    User getByUserName(String userName);

    /**
     * 通过用户名查询角色信息
     * @param userName 用户名
     * @return 角色信息
     */
    Set<String> getRoles(String userName);

    /**
     * 通过用户名查询权限信息
     * @param userName 用户名
     * @return 权限信息
     */
    Set<String> getPermissions(String userName);
}
