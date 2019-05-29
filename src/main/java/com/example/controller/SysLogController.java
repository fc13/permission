package com.example.controller;

import com.example.beans.PageQuery;
import com.example.common.JsonData;
import com.example.param.SearchLogParam;
import com.example.service.SysLogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@RestController
@RequestMapping("/sys/log")
public class SysLogController {

    @Resource
    private SysLogService logService;

    @RequestMapping("/log.page")
    public ModelAndView page(){
        return new ModelAndView("log");
    }

    @RequestMapping("/page.json")
    public JsonData searchPage(SearchLogParam param, PageQuery pageQuery){
        return JsonData.success(logService.searchPageList(param, pageQuery));
    }

    @RequestMapping("/recover.json")
    public JsonData recover(@RequestParam("id") int id){
        logService.recover(id);
        return JsonData.success();
    }
}
