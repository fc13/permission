package com.example.service;

import com.example.dao.SysAclMapper;
import com.example.dao.SysAclModelMapper;
import com.example.dao.SysDeptMapper;
import com.example.dao.SysRoleMapper;
import com.example.dto.AclDto;
import com.example.dto.AclModuleLevelDto;
import com.example.dto.DeptLevelDto;
import com.example.model.SysAcl;
import com.example.model.SysAclModel;
import com.example.model.SysDept;
import com.example.model.SysRole;
import com.example.util.LevelUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper mapper;
    @Resource
    private SysAclModelMapper aclModelMapper;
    @Resource
    private SysCoreService coreService;
    @Resource
    private SysAclMapper aclMapper;

    public List<AclModuleLevelDto> userAclTree(int userId) {
        List<AclDto> aclDtoList = Lists.newArrayList();
        List<SysAcl> userAclList = coreService.getUserAclList(userId);
        for (SysAcl acl : userAclList) {
            AclDto dto = AclDto.adapt(acl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    public List<AclModuleLevelDto> roleTree(int roleId) {
        List<AclDto> aclDtoList = Lists.newArrayList();
        // 1、当前用户已分配的权限点
        List<SysAcl> currentUserAcl = coreService.getCurrentUserAcl();
        // 2、当前角色已分配的权限点
        List<SysAcl> roleAclList = coreService.getRoleAclList(roleId);
        // 3、获取系统中所有的权限点
        List<SysAcl> allAcl = aclMapper.getAll();
        Set<Integer> userAclIdSet = currentUserAcl.stream().map(SysAcl::getId).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(SysAcl::getId).collect(Collectors.toSet());
        for (SysAcl acl : allAcl) {
            AclDto dto = AclDto.adapt(acl);
            if (userAclIdSet.contains(acl.getId())) {
                dto.setHasAcl(true);
            }
            if (roleAclIdSet.contains(acl.getId())) {
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        return aclListToTree(aclDtoList);
    }

    private List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleLevelDtoList = aclModuleTree();
        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        for (AclDto dto : aclDtoList) {
            if (dto.getStatus() == 1) {
                moduleIdAclMap.put(dto.getAclModelId(), dto);
            }
        }
        bindAclWithOrder(aclModuleLevelDtoList, moduleIdAclMap);
        return aclModuleLevelDtoList;
    }

    private void bindAclWithOrder(List<AclModuleLevelDto> aclModuleLevelDtoList, Multimap<Integer, AclDto> moduleIdAclMap) {
        if (CollectionUtils.isEmpty(aclModuleLevelDtoList)) {
            return;
        }
        for (AclModuleLevelDto dto : aclModuleLevelDtoList) {
            List<AclDto> aclDtos = (List<AclDto>) moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtos)) {
                aclDtos.sort(Comparator.comparingInt(AclDto::getSeq));
                dto.setAclList(aclDtos);
            }
            bindAclWithOrder(dto.getAclModuleList(), moduleIdAclMap);
        }
    }

    public List<AclModuleLevelDto> aclModuleTree() {
        List<SysAclModel> aclModelList = aclModelMapper.queryAllAclModule();
        List<AclModuleLevelDto> aclModuleLevelDtoList = Lists.newArrayList();
        for (SysAclModel aclModel : aclModelList) {
            aclModuleLevelDtoList.add(AclModuleLevelDto.adapt(aclModel));
        }
        return AclModuleToTree(aclModuleLevelDtoList);
    }

    private List<AclModuleLevelDto> AclModuleToTree(List<AclModuleLevelDto> aclModuleLevelDtoList) {
        if (CollectionUtils.isEmpty(aclModuleLevelDtoList)) {
            return Lists.newArrayList();
        }
        Multimap<String, AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();
        for (AclModuleLevelDto dto : aclModuleLevelDtoList) {
            levelAclModuleMap.put(dto.getLevel(), dto);
            if (dto.getLevel().equals(LevelUtil.ROOT)) {
                rootList.add(dto);
            }
        }
        rootList.sort(Comparator.comparingInt(SysAclModel::getSeq));
        transformAclModuleTree(rootList, levelAclModuleMap, LevelUtil.ROOT);
        return rootList;
    }

    private void transformAclModuleTree(List<AclModuleLevelDto> rootList, Multimap<String, AclModuleLevelDto> levelAclModuleMap, String rootLevel) {
        for (AclModuleLevelDto dto : rootList) {
            String nextLevel = LevelUtil.calculateLevel(rootLevel, dto.getId());
            List<AclModuleLevelDto> aclModuleLevelDtos = (List<AclModuleLevelDto>) levelAclModuleMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(aclModuleLevelDtos)) {
                aclModuleLevelDtos.sort(Comparator.comparingInt(SysAclModel::getSeq));
                dto.setAclModuleList(aclModuleLevelDtos);
                transformAclModuleTree(aclModuleLevelDtos, levelAclModuleMap, nextLevel);
            }
        }
    }

    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = mapper.queryAllDept();

        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            dtoList.add(DeptLevelDto.adapt(dept));
        }
        return deptListToTree(dtoList);
    }

    private List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList) {
        if (CollectionUtils.isEmpty(deptLevelList)) {
            return Lists.newArrayList();
        }
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();
        for (DeptLevelDto levelDto : deptLevelList) {
            levelDeptMap.put(levelDto.getLevel(), levelDto);
            if (levelDto.getLevel().equals(LevelUtil.ROOT)) {
                rootList.add(levelDto);
            }
        }
        rootList.sort(Comparator.comparingInt(SysDept::getSeq));
        transformDeptTree(rootList, levelDeptMap, LevelUtil.ROOT);
        return rootList;
    }

    private void transformDeptTree(List<DeptLevelDto> rootList, Multimap<String, DeptLevelDto> levelDeptMap, String level) {
        for (DeptLevelDto dto : rootList) {
            String nextLevel = LevelUtil.calculateLevel(level, dto.getId());
            List<DeptLevelDto> tempDeptLevelList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tempDeptLevelList)) {
                tempDeptLevelList.sort(Comparator.comparingInt(SysDept::getSeq));
                dto.setDeptLevelList(tempDeptLevelList);
                transformDeptTree(tempDeptLevelList, levelDeptMap, nextLevel);
            }
        }
    }
}
