package com.example.dao;

import com.example.model.SysRoleAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleAclMapper {
    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    List<Integer> getAclIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

    List<Integer> getRoleIdByAclId(@Param("aclId") int aclId);

    void deleteByRoleId(@Param("roleId") int roleId);

    void batchInsert(@Param("roleAclList")List<SysRoleAcl> roleAclList);
}