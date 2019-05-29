package com.example.service;

import com.example.beans.LogType;
import com.example.common.RequestHolder;
import com.example.dao.SysLogMapper;
import com.example.dao.SysRoleUserMapper;
import com.example.dao.SysUserMapper;
import com.example.model.SysLogWithBLOBs;
import com.example.model.SysRoleUser;
import com.example.model.SysUser;
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
public class SysRoleUserService {
    @Resource
    private SysRoleUserMapper roleUserMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysLogMapper logMapper;

    public List<SysUser> getSelectUser(int roleId) {
        List<Integer> userIdList = roleUserMapper.getUserIdListByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }
        return userMapper.getByUserIdList(userIdList);
    }

    public void changeUser(Integer roleId, List<Integer> userIdList) {
        List<Integer> userIds = roleUserMapper.getUserIdListByRoleId(roleId);
        if (userIdList.size() == userIds.size()) {
            Set<Integer> orignUserIdSet = Sets.newHashSet(userIds);
            Set<Integer> newUserIdSet = Sets.newHashSet(userIdList);
            orignUserIdSet.removeAll(newUserIdSet);
            if (CollectionUtils.isEmpty(orignUserIdSet)) {
                return;
            }
        }
        batchUpdateUserRole(roleId, userIdList);
        saveRoleUserLog(roleId, userIds, userIdList);
    }

    private void saveRoleUserLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(roleId);
        sysLog.setType(LogType.TYPE_ROLE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        logMapper.insertSelective(sysLog);
    }

    @Transactional
    public void batchUpdateUserRole(Integer roleId, List<Integer> userIdList) {
        roleUserMapper.deleteByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<SysRoleUser> userList = Lists.newArrayList();
        for (Integer userId : userIdList) {
            SysRoleUser user = SysRoleUser.builder().roleId(roleId).userId(userId).operator(RequestHolder.getCurrentUser().getUsername())
                    .operateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())).operateTime(new Date()).build();
            userList.add(user);
        }
        roleUserMapper.batchInsertRoleUser(userList);
    }
}
