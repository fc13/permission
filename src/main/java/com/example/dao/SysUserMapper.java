package com.example.dao;

import com.example.beans.PageQuery;
import com.example.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int insert(SysUser record);

    int insertSelective(SysUser record);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    SysUser findByKeyword(@Param("keyword") String keyword);

    int countByMail(@Param("mail") String mail, @Param("id") Integer id);

    int countByTelephone(@Param("telephone") String telephone, @Param("id") Integer id);

    int countByDeptId(@Param("deptId") int deptId);

    List<SysUser> getPageByDeptId(@Param("deptId") int deptId, @Param("page") PageQuery pageQuery);

    List<SysUser> getByUserIdList(@Param("userIds") List<Integer> userIds);

    List<SysUser> getAll();


}