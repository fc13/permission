package com.example.beans;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResult<T> {

    private int total = 0;
    private List<T> data;
}
