package com.vole.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 编写者： vole
 * Time： 2018/7/10.16:28
 * 内容：
 */
public class ConsumerTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"dubbo-demo-consumer.xml"});
        context.start();
        DemoProviderService demoProviderService = (DemoProviderService) context.getBean("demoProviderService");
        String result = demoProviderService.sayHello("你好");
        System.out.println("远程调用的结果：" + result);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        context.close();
    }
}
