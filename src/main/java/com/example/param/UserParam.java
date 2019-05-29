package com.example.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UserParam {
    private Integer id;

    @NotBlank(message = "用户名不能为空")
    @Length(min = 1,max = 20,message = "用户名长度在20以内")
    private String username;

    @NotBlank(message = "电话号码不能为空")
    @Length(min = 1,max = 13,message = "电话号码不合法")
    private String telephone;

    @NotBlank(message = "邮箱不能为空")
    @Length(min = 5,max = 20,message = "邮箱长度在20以内")
    private String mail;

    @Length(min = 1,max = 200,message = "备注长度在200以内")
    private String remark;

    @NotNull(message = "必须提供用户部门")
    private Integer deptId;

    @Min(value = 0,message = "用户状态不合法")
    @Max(value = 2,message = "用户状态不合法")
    private Integer status;
}
