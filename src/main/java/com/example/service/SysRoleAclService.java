package com.example.service;

import com.example.beans.LogType;
import com.example.common.RequestHolder;
import com.example.dao.SysLogMapper;
import com.example.dao.SysRoleAclMapper;
import com.example.model.SysLogWithBLOBs;
import com.example.model.SysRoleAcl;
import com.example.util.IpUtil;
import com.example.util.JsonMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class SysRoleAclService {
    @Resource
    private SysRoleAclMapper roleAclMapper;
    @Resource
    private SysLogMapper logMapper;

    public void changeAcl(Integer roleId, List<Integer> aclIdList) {
        List<Integer> aclIds = roleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        if (aclIdList.size() == aclIds.size()) {
            Set<Integer> originAclSet = Sets.newHashSet(aclIds);
            Set<Integer> afterAclSet = Sets.newHashSet(aclIdList);
            originAclSet.removeAll(afterAclSet);
            if (CollectionUtils.isEmpty(originAclSet)) {
                return;
            }
        }
        batchUpdateRoleAcl(roleId, aclIdList);
        saveRoleAclLog(roleId, aclIds, aclIdList);
    }

    private void saveRoleAclLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(roleId);
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        logMapper.insertSelective(sysLog);
    }

    @Transactional
    public void batchUpdateRoleAcl(Integer roleId, List<Integer> aclIdList) {
        roleAclMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isEmpty(aclIdList)) {
            return;
        }
        List<SysRoleAcl> list = Lists.newArrayList();
        for (Integer id : aclIdList) {
            SysRoleAcl roleAcl = SysRoleAcl.builder().roleId(roleId).aclId(id).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            list.add(roleAcl);
        }
        roleAclMapper.batchInsert(list);
    }
}
