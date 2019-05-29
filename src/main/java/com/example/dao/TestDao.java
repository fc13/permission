package com.example.dao;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


@Data
public class TestDao {
    @NotNull(message = "id不能为空")
    private Integer id;
    @NotBlank(message = "msg不能为空")
    private String msg;
}
