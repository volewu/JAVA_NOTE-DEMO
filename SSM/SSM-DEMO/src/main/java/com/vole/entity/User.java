package com.vole.entity;

/**
 * 编写者： vole
 * Time： 2018/7/24.16:16
 * 内容：User model
 */
public class User {

    private int id;
    private String userName;
    private String password;
    private String roleId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
}
