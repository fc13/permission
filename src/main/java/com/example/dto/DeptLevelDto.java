package com.example.dto;

import com.example.model.SysDept;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class DeptLevelDto extends SysDept {
    private List<DeptLevelDto> deptLevelList = Lists.newArrayList();

    public static DeptLevelDto adapt(SysDept sysDept){
        DeptLevelDto dto = new DeptLevelDto();
        BeanUtils.copyProperties(sysDept,dto);
        return dto;
    }
}
