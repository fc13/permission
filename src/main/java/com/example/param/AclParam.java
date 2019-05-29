package com.example.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AclParam {
    private Integer id;

    @NotBlank(message = "权限名称不能为空")
    @Length(min = 2,max = 20,message = "权限名称字数在2~20之间")
    private String name;

    @NotNull(message = "必须制定权限模块")
    private Integer aclModelId;

    @Length(min = 6,max = 100,message = "权限点URL长度必须在6~100之间")
    private String url;

    @NotNull(message = "权限点类型不能为空")
    @Min(value = 1,message = "权限点类型不合法")
    @Max(value = 3,message = "权限点类型不合法")
    private Integer type;

    @NotNull(message = "权限点状态爱不能为空")
    @Min(value = 0,message = "权限点状态不合法")
    @Max(value = 1,message = "权限点状态不合法")
    private Integer status;

    @NotNull(message = "权限点顺序不能为空")
    private Integer seq;

    @Length(min = 2,max = 200,message = "权限点备注字数长度在2~200之间")
    private String remark;
}
