package com.vole.service.impl;

import com.vole.dao.UserDao;
import com.vole.entity.User;
import com.vole.service.UserService;

import org.springframework.stereotype.Service;

import java.util.Set;

import javax.annotation.Resource;

/**
 * 编写者： vole
 * Time： 2018/7/24.16:36
 * 内容：User Service 实现层
 */
@Service("userService")
public class UserServiceImpl implements UserService{

    @Resource
    private UserDao userDao;

    @Override
    public User getByUserName(String userName) {
        return userDao.getByUserName(userName);
    }

    @Override
    public Set<String> getRoles(String userName) {
        return userDao.getRoles(userName);
    }

    @Override
    public Set<String> getPermissions(String userName) {
        return userDao.getPermissions(userName);
    }
}
