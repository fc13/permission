package com.example.service;

import com.example.beans.PageQuery;
import com.example.beans.PageResult;
import com.example.common.RequestHolder;
import com.example.dao.SysUserMapper;
import com.example.dto.Mail;
import com.example.exception.ParamException;
import com.example.model.SysUser;
import com.example.param.UserParam;
import com.example.util.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SysUserService {

    @Resource
    private SysUserMapper mapper;
    @Resource
    private SysLogService logService;

    public void save(UserParam param) {
        BeanValidator.check(param);
        if (checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException("邮箱已被占用");
        }
        if (checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException("手机号码已被占用");
        }
        String password = PasswordUtil.randomPassword();
//        password = "123456";
        String encryptedPassword = MD5Util.encrypt(password);
        SysUser user = SysUser.builder().username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .password(encryptedPassword).deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        user.setOperator(RequestHolder.getCurrentUser().getUsername());
        user.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        user.setOperateTime(new Date());

        //发送邮件
        Mail mail = Mail.builder().subject("登录密码").message("密码：" + "\n" + password).receivers(Sets.newHashSet(param.getMail())).build();
        boolean isSuccess = MailUtil.send(mail);
        if (isSuccess) {
            mapper.insertSelective(user);
        } else {
            throw new ParamException("邮件发送失败");
        }
        logService.saveUserLog(null, user);
    }

    public void update(UserParam param) {
        BeanValidator.check(param);
        if (checkEmailExist(param.getMail(), param.getId())) {
            throw new ParamException("邮箱已被占用");
        }
        if (checkTelephoneExist(param.getTelephone(), param.getId())) {
            throw new ParamException("手机号码已被占用");
        }
        SysUser before = mapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "用户不存在，无法更新");
        SysUser after = SysUser.builder().id(param.getId()).username(param.getUsername()).telephone(param.getTelephone()).mail(param.getMail())
                .deptId(param.getDeptId()).status(param.getStatus()).remark(param.getRemark()).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getCurrentRequest()));
        after.setOperateTime(new Date());
        log.info("remoteIp:"+IpUtil.getRemoteIp(RequestHolder.getCurrentRequest())+",userIp"+IpUtil.getUserIP(RequestHolder.getCurrentRequest())+",serverIp:"+IpUtil.getServerIP());
        mapper.updateByPrimaryKeySelective(after);
        logService.saveUserLog(before, after);
    }

    // 判断email是否已经注册过
    private boolean checkEmailExist(String mail, Integer id) {
        return mapper.countByMail(mail, id) > 0;
    }

    // 判断手机号是否已经注册过
    private boolean checkTelephoneExist(String telephone, Integer id) {
        return mapper.countByTelephone(telephone, id) > 0;
    }

    public SysUser findByKeyword(String keyword) {
        return mapper.findByKeyword(keyword);
    }

    public PageResult<SysUser> getPageByDeptId(int deptId, PageQuery page) {
        BeanValidator.check(page);
        int count = mapper.countByDeptId(deptId);
        if (count > 0) {
            List<SysUser> list = mapper.getPageByDeptId(deptId, page);
            return PageResult.<SysUser>builder().data(list).total(count).build();
        }
        return PageResult.<SysUser>builder().build();
    }

    public List<SysUser> getAll() {
        return mapper.getAll();
    }
}
