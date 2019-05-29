package com.example.controller;

import com.example.common.JsonData;
import com.example.dto.AclModuleLevelDto;
import com.example.dto.DeptLevelDto;
import com.example.param.AclModuleParam;
import com.example.service.SysAclModuleService;
import com.example.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/aclModule")
@Slf4j
public class SysAclModelController {

    @Resource
    private SysAclModuleService service;
    @Resource
    private SysTreeService treeService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveAclModel(AclModuleParam param){
        service.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAclModel(AclModuleParam param){
        service.update(param);
        return JsonData.success();
    }

    @RequestMapping("/acl.page")
    public ModelAndView page(){
        return new ModelAndView("acl");
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<AclModuleLevelDto> list = treeService.aclModuleTree();
        return JsonData.success(list);
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(@RequestParam("id")Integer id){
        service.delete(id);
        return JsonData.success("删除成功");
    }

}
