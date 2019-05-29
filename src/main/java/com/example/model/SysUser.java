package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SysUser {
    private Integer id;

    private String username;

    private String password;

    private String telephone;

    private String mail;

    private String remark;

    private Integer deptId;

    private Integer status;

    private String operator;

    private Date operateTime;

    private String operateIp;
}