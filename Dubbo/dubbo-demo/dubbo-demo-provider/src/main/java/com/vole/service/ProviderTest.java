package com.vole.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 编写者： vole
 * Time： 2018/7/10.16:22
 * 内容：
 */
public class ProviderTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-demo-provider.xml"});
        context.start();
        System.out.println("服务提供者向zookeeper注册中心注册服务成功（端口：20881）");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.close();
    }
}
