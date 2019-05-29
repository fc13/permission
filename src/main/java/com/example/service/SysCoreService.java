package com.example.service;

import com.example.beans.CacheKeyConstants;
import com.example.common.RequestHolder;
import com.example.dao.SysAclMapper;
import com.example.dao.SysRoleAclMapper;
import com.example.dao.SysRoleUserMapper;
import com.example.model.SysAcl;
import com.example.model.SysRoleUser;
import com.example.model.SysUser;
import com.example.util.JsonMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysCoreService {

    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysCacheService cacheService;

    public List<SysAcl> getCurrentUserAcl(){
        int userId = RequestHolder.getCurrentUser().getId();
        return getUserAclList(userId);
    }

    public List<SysAcl> getRoleAclList(int roleId){
        List<Integer> roleIdList = Lists.newArrayList(roleId);
        List<Integer> aclIdList = roleAclMapper.getAclIdListByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return aclMapper.getAclByAclIdList(aclIdList);
    }

    public List<SysAcl> getUserAclList(int userId){
        if (isSuperAdmin()){
            return aclMapper.getAll();
        }
        List<Integer> roleIdList = roleUserMapper.getRoleIdListByUserId(userId);
        if (CollectionUtils.isEmpty(roleIdList)){
            return Lists.newArrayList();
        }
        List<Integer> aclIdList = roleAclMapper.getAclIdListByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(aclIdList)){
            return Lists.newArrayList();
        }
        return aclMapper.getAclByAclIdList(aclIdList);
    }

    private boolean isSuperAdmin(){
        SysUser user = RequestHolder.getCurrentUser();
        return user.getUsername().contains("admin");
    }

    public boolean hasUrlAcl(String url){
        if (isSuperAdmin()){
            return true;
        }
        List<SysAcl> aclList = aclMapper.getByUrl(url);
        if (CollectionUtils.isEmpty(aclList)){
            return true;
        }
        List<SysAcl> userAclList = getCurrentUserAclFromCache();
        Set<Integer> aclIdList = userAclList.stream().map(SysAcl::getId).collect(Collectors.toSet());
        boolean hasValidAcl = false;
        for (SysAcl acl : aclList) {
            if (acl == null || acl.getStatus() != 1){
                continue;
            }
            hasValidAcl = true;
            if (aclIdList.contains(acl.getId())){
                return true;
            }
        }
        if (!hasValidAcl){
            return true;
        }
        return false;
    }

    public List<SysAcl> getCurrentUserAclFromCache(){
        int userId = RequestHolder.getCurrentUser().getId();
        String cacheValue = cacheService.getFromCache(CacheKeyConstants.USER_ACL,String.valueOf(userId));
        if (StringUtils.isBlank(cacheValue)){
            List<SysAcl> aclList = getCurrentUserAcl();
            if (CollectionUtils.isNotEmpty(aclList)){
                cacheService.saveCache(JsonMapper.objectToString(aclList),600,CacheKeyConstants.USER_ACL,String.valueOf(userId));
            }
            return aclList;
        }
        return JsonMapper.stringToObject(cacheService.getFromCache(CacheKeyConstants.USER_ACL, String.valueOf(userId)), new TypeReference<List<SysAcl>>() {});
    }
}
