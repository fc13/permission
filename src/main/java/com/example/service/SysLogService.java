package com.example.service;

import com.example.beans.LogType;
import com.example.beans.PageQuery;
import com.example.beans.PageResult;
import com.example.common.RequestHolder;
import com.example.dao.*;
import com.example.dto.SearchLogDto;
import com.example.exception.ParamException;
import com.example.model.*;
import com.example.param.SearchLogParam;
import com.example.util.IpUtil;
import com.example.util.JsonMapper;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysLogService {
    @Resource
    private SysLogMapper logMapper;
    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysAclModelMapper aclModelMapper;
    @Resource
    private SysRoleAclService roleAclService;
    @Resource
    private SysRoleUserService roleUserService;

    public void recover(int id) {
        SysLogWithBLOBs sysLog = logMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(sysLog,"待还原的记录不存在");
        switch (sysLog.getType()) {
            case LogType.TYPE_DEPT:
                SysDept nowDept = deptMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(nowDept,"待还原的部门已经不存在了");
                if (StringUtils.isBlank(sysLog.getNewValue())||StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作无法还原");
                }
                SysDept beforeDept = JsonMapper.stringToObject(sysLog.getOldValue(), new TypeReference<SysDept>() {});
                beforeDept.setOperator(RequestHolder.getCurrentUser().getUsername());
                beforeDept.setOperateTime(new Date());
                beforeDept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                deptMapper.updateByPrimaryKeySelective(beforeDept);
                saveDeptLog(nowDept,beforeDept);
                break;
            case LogType.TYPE_USER:
                SysUser nowUser = userMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(nowUser,"待还原的用户已经不存在了");
                if (StringUtils.isBlank(sysLog.getNewValue())||StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作无法还原");
                }
                SysUser beforeUser = JsonMapper.stringToObject(sysLog.getOldValue(), new TypeReference<SysUser>() {});
                beforeUser.setOperator(RequestHolder.getCurrentUser().getUsername());
                beforeUser.setOperateTime(new Date());
                beforeUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                userMapper.updateByPrimaryKeySelective(beforeUser);
                saveUserLog(nowUser,beforeUser);
                break;
            case LogType.TYPE_ROLE:
                SysRole nowRole = roleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(nowRole,"待还原的角色已经不存在了");
                if (StringUtils.isBlank(sysLog.getNewValue())||StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作无法还原");
                }
                SysRole beforeRole = JsonMapper.stringToObject(sysLog.getOldValue(), new TypeReference<SysRole>() {});
                beforeRole.setOperator(RequestHolder.getCurrentUser().getUsername());
                beforeRole.setOperateTime(new Date());
                beforeRole.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                roleMapper.updateByPrimaryKeySelective(beforeRole);
                saveRoleLog(nowRole,beforeRole);
                break;
            case LogType.TYPE_ACL:
                SysAcl nowAcl = aclMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(nowAcl,"待还原的权限点已经不存在了");
                if (StringUtils.isBlank(sysLog.getNewValue())||StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作无法还原");
                }
                SysAcl beforeAcl = JsonMapper.stringToObject(sysLog.getOldValue(), new TypeReference<SysAcl>() {});
                beforeAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
                beforeAcl.setOperateTime(new Date());
                beforeAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                aclMapper.updateByPrimaryKeySelective(beforeAcl);
                saveAclLog(nowAcl,beforeAcl);
                break;
            case LogType.TYPE_ACL_MODULE:
                SysAclModel nowAclModule = aclModelMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(nowAclModule,"待还原的权限模块已经不存在了");
                if (StringUtils.isBlank(sysLog.getNewValue())||StringUtils.isBlank(sysLog.getOldValue())){
                    throw new ParamException("新增和删除操作无法还原");
                }
                SysAclModel beforeAclModule = JsonMapper.stringToObject(sysLog.getOldValue(), new TypeReference<SysAclModel>() {});
                beforeAclModule.setOperator(RequestHolder.getCurrentUser().getUsername());
                beforeAclModule.setOperateTime(new Date());
                beforeAclModule.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
                aclModelMapper.updateByPrimaryKeySelective(beforeAclModule);
                saveAclModuleLog(nowAclModule,beforeAclModule);
                break;
            case LogType.TYPE_ROLE_ACL:
                SysRole aclRole = roleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(aclRole,"角色不存在，无法更新");
                roleAclService.changeAcl(sysLog.getTargetId(),JsonMapper.stringToObject(sysLog.getOldValue(), new TypeReference<List<Integer>>() {}));
                break;
            case LogType.TYPE_ROLE_USER:
                SysRole userRole = roleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(userRole,"角色不存在，无法更新");
                roleUserService.changeUser(sysLog.getTargetId(),JsonMapper.stringToObject(sysLog.getOldValue(), new TypeReference<List<Integer>>() {}));
                break;
            default:
                break;
        }
    }

    public PageResult<SysLogWithBLOBs> searchPageList(SearchLogParam param, PageQuery pageQuery) {
        SearchLogDto dto = new SearchLogDto();
        if (param.getType() != null) {
            dto.setType(param.getType());
        }
        if (StringUtils.isNotBlank(param.getBeforeSeg())) {
            dto.setBeforeSeg("%" + param.getBeforeSeg() + "%");
        }
        if (StringUtils.isNotBlank(param.getAfterSeg())) {
            dto.setAfterSeg("%" + param.getAfterSeg() + "%");
        }
        if (StringUtils.isNotBlank(param.getOperator())) {
            dto.setOperator("%" + param.getOperator() + "%");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (StringUtils.isNotBlank(param.getFromTime())) {
                dto.setFromTime(format.parse(param.getFromTime()));
            }
            if (StringUtils.isNotBlank(param.getToTime())) {
                dto.setToTime(format.parse(param.getToTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ParamException("时间格式错误，正确的格式为yyyy-MM-dd HH:mm:ss");
        }
        int count = logMapper.countBySearchDto(dto);
        if (count > 0) {
            List<SysLogWithBLOBs> result = logMapper.getBySearchDto(dto, pageQuery);
            return PageResult.<SysLogWithBLOBs>builder().total(count).data(result).build();
        }
        return PageResult.<SysLogWithBLOBs>builder().build();
    }

    public void saveDeptLog(SysDept before, SysDept after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(before == null ? after.getId() : before.getId());
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        logMapper.insertSelective(sysLog);
    }

    public void saveUserLog(SysUser before, SysUser after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(before == null ? after.getId() : before.getId());
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        logMapper.insertSelective(sysLog);
    }

    public void saveRoleLog(SysRole before, SysRole after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(before == null ? after.getId() : before.getId());
        sysLog.setType(LogType.TYPE_ROLE);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        logMapper.insertSelective(sysLog);
    }

    public void saveAclLog(SysAcl before, SysAcl after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(before == null ? after.getId() : before.getId());
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        logMapper.insertSelective(sysLog);
    }

    public void saveAclModuleLog(SysAclModel before, SysAclModel after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setTargetId(before == null ? after.getId() : before.getId());
        sysLog.setType(LogType.TYPE_ACL_MODULE);
        sysLog.setOldValue(before == null ? "" : JsonMapper.objectToString(before));
        sysLog.setNewValue(after == null ? "" : JsonMapper.objectToString(after));
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperatorIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysLog.setOperatorTime(new Date());
        sysLog.setStatus(1);
        logMapper.insertSelective(sysLog);
    }
}
