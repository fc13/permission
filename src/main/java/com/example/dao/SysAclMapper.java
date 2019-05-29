package com.example.dao;

import com.example.beans.PageQuery;
import com.example.model.SysAcl;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int countByAclModelId(@Param("aclModelId") int aclModelId);

    List<SysAcl> getPageByAclModelId(@Param("aclModelId") int aclModelId, @Param("page") PageQuery pageQuery);

    int countByNameAndAclModelId(@Param("id") Integer id, @Param("name") String name, @Param("aclModelId") Integer aclModelId);

    List<SysAcl> getAll();

    List<SysAcl> getAclByAclIdList(@Param("aclIdList") List<Integer> aclIdList);

    List<SysAcl> getByUrl(@Param("url") String url);
}