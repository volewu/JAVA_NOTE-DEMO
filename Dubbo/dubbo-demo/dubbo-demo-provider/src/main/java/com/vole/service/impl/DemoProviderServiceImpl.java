package com.vole.service.impl;

import com.vole.service.DemoProviderService;

/**
 * 编写者： vole
 * Time： 2018/7/10.16:20
 * 内容：服务提供者接口实现类
 */
public class DemoProviderServiceImpl implements DemoProviderService {
    public String sayHello(String name) {
        return "服务001  " + name;
    }
}
