package com.example.service;

import com.example.beans.PageQuery;
import com.example.beans.PageResult;
import com.example.common.RequestHolder;
import com.example.dao.SysAclMapper;
import com.example.exception.ParamException;
import com.example.model.SysAcl;
import com.example.param.AclParam;
import com.example.util.BeanValidator;
import com.example.util.IpUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SysAclService {

    @Resource
    private SysAclMapper mapper;
    @Resource
    private SysLogService logService;

    public void save(AclParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getId(), param.getName(), param.getAclModelId())) {
            throw new ParamException("同一权限模块下存在相同权限点");
        }
        SysAcl sysAcl = SysAcl.builder().name(param.getName()).code(generateCode()).aclModelId(param.getAclModelId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
        sysAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAcl.setOperateTime(new Date());
        mapper.insertSelective(sysAcl);
        logService.saveAclLog(null,sysAcl);
    }

    public void update(AclParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getId(), param.getName(), param.getAclModelId())) {
            throw new ParamException("同一权限模块下存在相同权限点");
        }
        SysAcl before = mapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "要更新的权限点不存在");
        SysAcl sysAcl = SysAcl.builder().id(param.getId()).name(param.getName()).aclModelId(param.getAclModelId()).url(param.getUrl())
                .type(param.getType()).status(param.getStatus()).seq(param.getSeq()).remark(param.getRemark()).build();
        sysAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysAcl.setOperateTime(new Date());
        mapper.updateByPrimaryKeySelective(sysAcl);
        logService.saveAclLog(before,sysAcl);
    }

    private boolean checkExist(Integer id, String aclName, Integer aclModelId) {
        return mapper.countByNameAndAclModelId(id, aclName, aclModelId) > 0;
    }

    private String generateCode() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return format.format(new Date()) + "_" + (int) (Math.random() * 100);
    }

    public PageResult<SysAcl> page(int aclModelId, PageQuery pageQuery) {
        BeanValidator.check(pageQuery);
        int count = mapper.countByAclModelId(aclModelId);
        if (count > 0) {
            List<SysAcl> list = mapper.getPageByAclModelId(aclModelId, pageQuery);
            return PageResult.<SysAcl>builder().data(list).total(count).build();
        }
        return PageResult.<SysAcl>builder().build();
    }
}
