package com.vole.controller;

import com.vole.entity.User;
import com.vole.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 编写者： vole
 * Time： 2018/7/24.16:38
 * 内容：User Controller 层
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/login")
    public Object login(User user, HttpServletRequest request) {
        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUserName(), user.getPassword());
        try{
            subject.login(token);
            Session session=subject.getSession();
            System.out.println("sessionId:"+session.getId());
            System.out.println("sessionHost:"+session.getHost());
            System.out.println("sessionTimeout:"+session.getTimeout());
            session.setAttribute("info", "session的数据");
            return "redirect:/success.jsp";
        }catch(Exception e){
            e.printStackTrace();
            request.setAttribute("user", user);
            request.setAttribute("errorMsg", "用户名或密码错误！");
            return "index";
        }
    }

    @RequestMapping("/unauthor")
    @ResponseBody
    public String getUnauthor() {
        return "权限认证失败";
    }

    @RequestMapping("/admin/a")
    @ResponseBody
    public Object getAdmin() {
        return "Admin";
    }

    @RequestMapping("/student")
    @ResponseBody
    public Object getStudent() {
        return "student";
    }

    @RequestMapping("/teacher")
    @ResponseBody
    public Object getteacher() {
        return "teacher";
    }

    @RequestMapping("/getUser")
    @ResponseBody
    public User getUser() {
        return userService.getByUserName("gakki");
    }

    /**
     * 注销
     * @return 重定向到登入页面
     * @throws Exception e
     */
    @RequestMapping("/logout")
    public String logout() throws Exception {
        SecurityUtils.getSubject().logout();
        return "redirect:/login.jsp";
    }
}
