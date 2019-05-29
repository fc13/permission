package com.example.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class JsonData {
    private boolean ret;
    private String msg;
    private Object data;

    private JsonData(boolean ret) {
        this.ret = ret;
    }

    public static JsonData success(){
        return new JsonData(true);
    }

    public static JsonData success(Object data){
        JsonData jsonData = new JsonData(true);
        jsonData.data = data;
        return jsonData;
    }

    public static JsonData success(String msg,Object data){
        JsonData jsonData = new JsonData(true);
        jsonData.msg = msg;
        jsonData.data = data;
        return jsonData;
    }

    public static JsonData fail(String msg){
        JsonData jsonData = new JsonData(false);
        jsonData.msg = msg;
        return jsonData;
    }

    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("ret",ret);
        map.put("msg",msg);
        map.put("data",data);
        return map;
    }
}
