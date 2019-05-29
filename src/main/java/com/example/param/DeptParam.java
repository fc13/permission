package com.example.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class DeptParam {
    private Integer id;

    @NotBlank(message = "部门名称不能为空")
    @Length(max = 15, min = 2, message = "部门名称需要再2-15个字之间")
    private String name;

    private Integer parentId = 0;

    @NotNull(message = "展示顺序不能为空")
    private Integer seq;

    @Length(max = 150, message = "备注的长度在150个字以内")
    private String remark;
}
