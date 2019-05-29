package com.example.controller;

import com.example.beans.PageQuery;
import com.example.beans.PageResult;
import com.example.common.JsonData;
import com.example.model.SysAcl;
import com.example.model.SysRole;
import com.example.param.AclParam;
import com.example.service.SysAclService;
import com.example.service.SysRoleService;
import com.example.service.SysTreeService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/acl")
@Slf4j
public class SysAclController {

    @Resource
    private SysAclService aclService;
    @Resource
    private SysTreeService treeService;
    @Resource
    private SysRoleService roleService;

    @RequestMapping("/save.json")
    public JsonData save(AclParam param) {
        aclService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    public JsonData update(AclParam param) {
        aclService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    public JsonData page(@RequestParam("aclModelId") int aclModelId, PageQuery pageQuery) {
        PageResult<SysAcl> list = aclService.page(aclModelId, pageQuery);
        return JsonData.success(list);
    }

    @RequestMapping("/acls.json")
    public JsonData acl(@RequestParam("aclId") int aclId) {
        Map<String, Object> map = Maps.newHashMap();
        List<SysRole> roleList = roleService.getByAclId(aclId);
        map.put("role", roleList);
        map.put("acl", roleService.getUserListByRoleList(roleList));
        return JsonData.success(map);
    }
}
