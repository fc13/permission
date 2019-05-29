package com.example.controller;

import com.example.common.JsonData;
import com.example.dto.AclModuleLevelDto;
import com.example.model.SysUser;
import com.example.param.RoleParam;
import com.example.service.*;
import com.example.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    @Resource
    private SysRoleService service;
    @Resource
    private SysTreeService treeService;
    @Resource
    private SysUserService userService;
    @Resource
    private SysRoleUserService roleUserService;
    @Resource
    private SysRoleAclService roleAclService;

    @RequestMapping("/role.page")
    public ModelAndView page() {
        return new ModelAndView("role");
    }

    @RequestMapping("/save.json")
    public JsonData save(RoleParam param) {
        service.save(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    public JsonData update(RoleParam param) {
        service.update(param);
        return JsonData.success();
    }

    @RequestMapping("/list.json")
    public JsonData list() {
        return JsonData.success(service.getAllRole());
    }

    @RequestMapping("/roleTree.json")
    public JsonData roleTree(@RequestParam("roleId") Integer id) {
        List<AclModuleLevelDto> list = treeService.roleTree(id);
        return JsonData.success(list);
    }

    @RequestMapping("/changeAcls.json")
    public JsonData changeAcl(@RequestParam("roleId") Integer roleId, @RequestParam(value = "aclIds",required = false,defaultValue = "") String aclIds) {
        List<Integer> list = StringUtil.spiltToListInt(aclIds);
        roleAclService.changeAcl(roleId, list);
        return JsonData.success();
    }

    @RequestMapping("/changeUsers.json")
    public JsonData changeUsers(@RequestParam("roleId") Integer roleId, @RequestParam(value = "userIds",required = false,defaultValue = "") String userIds) {
        List<Integer> list = StringUtil.spiltToListInt(userIds);
        roleUserService.changeUser(roleId, list);
        return JsonData.success();
    }

    @RequestMapping("/users.json")
    public JsonData users(@RequestParam("roleId") int roleId) {
        List<SysUser> selectedUser = roleUserService.getSelectUser(roleId);
        List<SysUser> allUser = userService.getAll();
        List<SysUser> unselectedUser = Lists.newArrayList();
        Set<Integer> selectUserIdSet = selectedUser.stream().map(SysUser::getId).collect(Collectors.toSet());
        for (SysUser user : allUser) {
            if (user.getStatus() == 1 && !selectUserIdSet.contains(user.getId())) {
                unselectedUser.add(user);
            }
        }
        Map<String, List<SysUser>> map = Maps.newHashMap();
        map.put("selected", selectedUser);
        map.put("unselected", unselectedUser);
        return JsonData.success(map);
    }
}
