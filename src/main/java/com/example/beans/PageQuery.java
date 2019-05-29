package com.example.beans;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

public class PageQuery {

    @Getter
    @Setter
    @Min(value = 1,message = "当前页码不合法")
    private int pageNo;

    @Getter
    @Setter
    @Min(value = 1,message = "每页展示的页数不合法")
    private int pageSize;

    @Getter
    @Setter
    private int offset;

    private int getOffset(){
        return (pageNo - 1) * pageSize;
    }
}
