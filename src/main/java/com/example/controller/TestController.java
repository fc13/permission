package com.example.controller;

import com.example.common.ApplicationContextHelper;
import com.example.dao.SysUserMapper;
import com.example.model.SysUser;
import com.example.util.BeanValidator;
import com.example.common.JsonData;
import com.example.dao.TestDao;
import com.example.exception.ParamException;
import com.example.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
@Slf4j
public class TestController {

    @GetMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        log.info("hello");
//        throw new RuntimeException("error");
        return JsonData.success("success","hello, permission");
    }

    @GetMapping("/validator.json")
    @ResponseBody
    public JsonData validator(TestDao dao) throws ParamException{
        log.info("hello");
//        SysUserMapper sysUserMapper = ApplicationContextHelper.popBean(SysUserMapper.class);
//        SysUser user = sysUserMapper.selectById(1);
//        log.info(JsonMapper.objectToString(user));
        BeanValidator.check(dao);
        return JsonData.success("success","success");
    }
}
