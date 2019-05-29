package com.example.dao;

import com.example.model.SysRoleUser;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleUserMapper {
    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    List<Integer> getRoleIdListByUserId(@Param("userId") int userId);

    List<Integer> getUserIdListByRoleId(@Param("roleId") int roleId);

    List<Integer> getUserIdListByRoleIdList(@Param("roleIdList") List<Integer> roleIdList);

    void deleteByRoleId(@Param("roleId") int roleId);

    void batchInsertRoleUser(@Param("roleUsers") List<SysRoleUser> roleUsers);
}