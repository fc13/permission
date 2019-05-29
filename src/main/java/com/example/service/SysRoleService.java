package com.example.service;

import com.example.common.RequestHolder;
import com.example.dao.SysRoleAclMapper;
import com.example.dao.SysRoleMapper;
import com.example.dao.SysRoleUserMapper;
import com.example.dao.SysUserMapper;
import com.example.exception.ParamException;
import com.example.model.SysRole;
import com.example.model.SysRoleAcl;
import com.example.model.SysUser;
import com.example.param.RoleParam;
import com.example.util.BeanValidator;
import com.example.util.IpUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.security.auth.login.Configuration;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysRoleService {

    @Resource
    private SysRoleMapper mapper;
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysLogService logService;

    public void save(RoleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getName(), param.getId())) {
            throw new ParamException("角色名称已存在");
        }
        SysRole role = SysRole.builder().name(param.getName()).status(param.getStatus()).type(param.getType())
                .remark(param.getRemark()).build();
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        mapper.insertSelective(role);
        logService.saveRoleLog(null,role);
    }

    public void update(RoleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getName(), param.getId())) {
            throw new ParamException("角色名称已存在");
        }
        SysRole before = mapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的角色不存在");
        SysRole role = SysRole.builder().id(param.getId()).name(param.getName()).type(param.getType())
                .status(param.getStatus()).remark(param.getRemark()).build();
        role.setOperator(RequestHolder.getCurrentUser().getUsername());
        role.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        role.setOperateTime(new Date());
        mapper.updateByPrimaryKeySelective(role);
        logService.saveRoleLog(before,role);
    }

    private boolean checkExist(String roleName, Integer id) {
        return mapper.countByName(id, roleName) > 0;
    }

    public List<SysRole> getAllRole() {
        return mapper.getAll();
    }

    public List<SysRole> getByAclId(int aclId){
        List<Integer> roleIdList = roleAclMapper.getRoleIdByAclId(aclId);
        if (CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return mapper.getRoleListByRoleIdList(roleIdList);
    }

    public List<SysUser> getUserListByRoleList(List<SysRole> roleList){
        if (CollectionUtils.isEmpty(roleList)){
            return Lists.newArrayList();
        }
        List<Integer> roleIdList = roleList.stream().map(SysRole::getId).collect(Collectors.toList());
        List<Integer> userIdList = roleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(userIdList)){
            return Lists.newArrayList();
        }
        return userMapper.getByUserIdList(userIdList);
    }

    public List<SysRole> getRoleListByUserId(int userId){
        List<Integer> roleIdList = roleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        return mapper.getRoleListByRoleIdList(roleIdList);
    }
}
