package com.example.service;

import com.example.common.RequestHolder;
import com.example.dao.SysDeptMapper;
import com.example.dao.SysUserMapper;
import com.example.exception.ParamException;
import com.example.model.SysDept;
import com.example.param.DeptParam;
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
public class SysDeptService {

    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysLogService logService;

    public void save(DeptParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("当前同一层级下存在相同名称的部门");
        }
        SysDept sysDept = SysDept.builder().name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        sysDept.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        sysDept.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysDept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        sysDept.setOperateTime(new Date());
        deptMapper.insertSelective(sysDept);
        logService.saveDeptLog(null,sysDept);
    }

    private boolean checkExist(Integer parentId, String deptName, Integer deptId) {
        return deptMapper.countByNameAndParentId(parentId, deptName, deptId) > 0;
    }

    private String getLevel(Integer deptId) {
        SysDept dept = deptMapper.selectByPrimaryKey(deptId);
        if (dept == null) {
            return null;
        } else {
            return dept.getLevel();
        }
    }

    public void update(DeptParam param) {
        BeanValidator.check(param);
        if (checkExist(param.getParentId(), param.getName(), param.getId())) {
            throw new ParamException("当前同一层级下存在相同名称的部门");
        }
        SysDept before = deptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "将要更新的部门不存在");
        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        updateWithChild(before, after);
        logService.saveDeptLog(before,after);
    }

    @Transactional
    public void updateWithChild(SysDept before, SysDept after) {
        String oldLevel = before.getLevel();
        String newLevel = after.getLevel();
        if (!oldLevel.equals(newLevel)) {
            List<SysDept> childDeptList = deptMapper.getChildDeptListByLevel(oldLevel);
            if (CollectionUtils.isNotEmpty(childDeptList)) {
                for (SysDept dept : childDeptList) {
                    String level = dept.getLevel();
                    if (level.indexOf(oldLevel) == 0) {
                        level = newLevel + level.substring(oldLevel.length());
                        dept.setLevel(level);
                    }
                }
                deptMapper.batchUpdateLevel(childDeptList);
            }
        }
        deptMapper.updateByPrimaryKey(after);
    }

    public void delete(Integer id) {
        SysDept dept = deptMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(dept, "要删除的部门不存在，无法删除");
        if (deptMapper.countByParentId(id) > 0) {
            throw new ParamException("要删除的部门下存在子部门，请删除部门下的所有子部门后再进行删除");
        }
        if (userMapper.countByDeptId(id)>0){
            throw new ParamException("要删除的部门中存在用户，请删除用户后再进行删除");
        }
        deptMapper.deleteByPrimaryKey(id);
    }
}
