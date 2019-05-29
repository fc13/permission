package com.example.dao;

import com.example.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(@Param("id") Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    List<SysDept> queryAllDept();

    List<SysDept> getChildDeptListByLevel(@Param("level") String level);

    void batchUpdateLevel(@Param("deptList") List<SysDept> deptList);

    int countByNameAndParentId(@Param("parentId") Integer parentId,@Param("name")String name,@Param("id") Integer id);

    int countByParentId(@Param("parentId") Integer parentId);
}