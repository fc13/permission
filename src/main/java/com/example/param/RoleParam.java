package com.example.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class RoleParam {
    private Integer id;

    @NotBlank(message = "角色名称不能为空")
    @Length(min = 2,max = 20,message = "角色名称字数在2~20之间")
    private String name;

    @Min(value = 1,message = "角色类型不合法")
    @Max(value = 2,message = "角色类型不合法")
    private Integer type = 1;

    @NotNull(message = "角色状态不能为空")
    @Min(value = 1,message = "角色状态不合法")
    @Max(value = 2,message = "角色状态不合法")
    private Integer status;

    @Length(max = 200,message = "角色备注字数在200以内")
    private String remark;
}
