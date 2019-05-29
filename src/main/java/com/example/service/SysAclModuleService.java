package com.example.service;

import com.example.common.RequestHolder;
import com.example.dao.SysAclMapper;
import com.example.dao.SysAclModelMapper;
import com.example.exception.ParamException;
import com.example.model.SysAclModel;
import com.example.param.AclModuleParam;
import com.example.util.BeanValidator;
import com.example.util.IpUtil;
import com.example.util.LevelUtil;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleService {
    @Resource
    private SysAclModelMapper mapper;
    @Resource
    private SysAclMapper aclMapper;
    @Resource
    private SysLogService logService;

    public void save(AclModuleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("当前同一层级下存在相同名称的权限模块");
        }
        SysAclModel aclModel = SysAclModel.builder().name(param.getName()).parentId(param.getParentId())
                .level(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId())).seq(param.getSeq()).status(param.getStatus()).remark(param.getRemark()).build();
        aclModel.setOperator(RequestHolder.getCurrentUser().getUsername());
        aclModel.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        aclModel.setOperateTime(new Date());
        mapper.insertSelective(aclModel);
        logService.saveAclModuleLog(null,aclModel);
    }

    public void update(AclModuleParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("当前同一层级下存在相同名称的权限模块");
        }
        SysAclModel before = mapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的权限模块不存在");
        SysAclModel after = SysAclModel.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .level(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId())).seq(param.getSeq())
                .status(param.getStatus()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before, after);
        logService.saveAclModuleLog(before,after);
    }

    private boolean checkExist(Integer parentId, String aclModuleName, Integer aclModuleId) {
        return mapper.countByNameAndParentId(parentId, aclModuleName, aclModuleId) > 0;
    }

    private String getLevel(Integer aclModuleId) {
        SysAclModel aclModel = mapper.selectByPrimaryKey(aclModuleId);
        if (aclModel == null) {
            return null;
        } else {
            return aclModel.getLevel();
        }
    }

    @Transactional
    public void updateWithChild(SysAclModel before, SysAclModel after) {
        String oldLevel = before.getLevel();
        String newLevel = after.getLevel();
        if (!oldLevel.equals(newLevel)) {
            List<SysAclModel> childAclModuleList = mapper.getChildAclModuleListByLevel(oldLevel);
            if (CollectionUtils.isNotEmpty(childAclModuleList)) {
                for (SysAclModel aclModel : childAclModuleList) {
                    String level = aclModel.getLevel();
                    if (level.indexOf(oldLevel) == 0) {
                        level = newLevel + level.substring(oldLevel.length());
                        aclModel.setLevel(level);
                    }
                    mapper.batchUpdateLevel(childAclModuleList);
                }
            }
        }
        mapper.updateByPrimaryKey(after);
    }

    public void delete(Integer id) {
        SysAclModel aclModel = mapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(aclModel, "要删除的权限模块不存在，无法删除");
        if (mapper.countByParentId(id) > 0) {
            throw new ParamException("要删除的权限模块下存在子模块，请删除该权限模块下的所有子模块后再删除");
        }
        if (aclMapper.countByAclModelId(id) > 0) {
            throw new ParamException("要删除的权限模块下存在权限点，请先删除该权限模块下的所有权限点后再删除");
        }
        mapper.deleteByPrimaryKey(id);
    }
}
