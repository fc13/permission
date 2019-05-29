package com.example.controller;

import com.example.beans.PageQuery;
import com.example.beans.PageResult;
import com.example.common.JsonData;
import com.example.model.SysUser;
import com.example.param.UserParam;
import com.example.service.SysRoleService;
import com.example.service.SysTreeService;
import com.example.service.SysUserService;
import com.google.common.collect.Maps;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Resource
    private SysUserService service;
    @Resource
    private SysTreeService treeService;
    @Resource
    private SysRoleService roleService;

    @RequestMapping("/noAuth.page")
    public ModelAndView noAuth(){
        return new ModelAndView("noAuth");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(UserParam param){
        service.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(UserParam param){
        service.update(param);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData page(@Param("deptId") int deptId, PageQuery pageQuery){
        PageResult<SysUser> result = service.getPageByDeptId(deptId, pageQuery);
        return JsonData.success(result);
    }

    @RequestMapping("/acls.json")
    @ResponseBody
    public JsonData acl(@Param("userId") int userId){
        Map<String,Object> map = Maps.newHashMap();
        map.put("role",roleService.getRoleListByUserId(userId));
        map.put("acl",treeService.userAclTree(userId));
        return JsonData.success(map);
    }
}
