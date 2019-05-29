package com.example.controller;

import com.example.common.JsonData;
import com.example.dto.DeptLevelDto;
import com.example.param.DeptParam;
import com.example.service.SysDeptService;
import com.example.service.SysTreeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

    @Resource
    private SysDeptService deptService;
    @Resource
    private SysTreeService treeService;

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(DeptParam param){
        deptService.save(param);
        return JsonData.success();
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<DeptLevelDto> list = treeService.deptTree();
        return JsonData.success(list);
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData update(DeptParam param){
        deptService.update(param);
        return JsonData.success();
    }

    @RequestMapping("/dept.page")
    public ModelAndView page(){
        return new ModelAndView("dept");
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(@RequestParam("id")Integer id){
        deptService.delete(id);
        return JsonData.success("删除成功");
    }
}
