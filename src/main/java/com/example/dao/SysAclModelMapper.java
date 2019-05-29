package com.example.dao;

import com.example.model.SysAclModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModelMapper {
    int insert(SysAclModel record);

    int insertSelective(SysAclModel record);

    int deleteByPrimaryKey(@Param("id") Integer id);

    int updateByPrimaryKeySelective(SysAclModel record);

    int updateByPrimaryKey(SysAclModel record);

    SysAclModel selectByPrimaryKey(@Param("id") Integer id);

    int countByNameAndParentId(@Param("id") Integer parentId, @Param("name") String aclModuleName, @Param("parentId") Integer aclModuleId);

    List<SysAclModel> getChildAclModuleListByLevel(@Param("level") String level);

    void batchUpdateLevel(@Param("aclModuleList") List<SysAclModel> childAclModuleList);

    List<SysAclModel> queryAllAclModule();

    int countByParentId(@Param("parentId") Integer id);
}