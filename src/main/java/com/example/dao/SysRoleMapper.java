package com.example.dao;

import com.example.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int insert(SysRole record);

    int insertSelective(SysRole record);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    List<SysRole> getAll();

    int countByName(@Param("id") Integer id,@Param("name")String name);

    List<SysRole> getByAclId(int aclId);

    List<SysRole> getRoleListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);
}