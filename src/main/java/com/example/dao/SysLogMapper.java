package com.example.dao;

import com.example.beans.PageQuery;
import com.example.dto.SearchLogDto;
import com.example.model.SysLog;
import com.example.model.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper {
    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int countBySearchDto(@Param("dto") SearchLogDto dto);

    List<SysLogWithBLOBs> getBySearchDto(@Param("dto")SearchLogDto dto, @Param("page")PageQuery pageQuery);
}