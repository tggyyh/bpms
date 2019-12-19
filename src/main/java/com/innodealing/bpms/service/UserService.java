package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.UserMapper;
import com.innodealing.bpms.model.Role;
import com.innodealing.bpms.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
@Transactional
@Service("userService")
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    ModuleService moduleService;

    public List findUser() {
        return userMapper.findUser();
    }

    public PageInfo<User> findByPage(ReqData reqData) {
        List<User> userList;

        Integer pageNum = 0;
        Integer pageSize = 30;
        PageInfo page;
        if (reqData.getInteger("offset") != null) {
            pageNum = reqData.getInteger("offset");
        }
        if (reqData.getInteger("pageSize") != null) {
            pageSize = reqData.getInteger("pageSize");
        }
        try {
            PageHelper.offsetPage(pageNum, pageSize);
            userList = userMapper.findByPage(reqData);
            page = new PageInfo(userList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public User findById(String id) {
        return userMapper.findById(id);
    }

    public void save(User user)throws DuplicateKeyException,Exception  {
         userMapper.save(user);
    }

    public void update(User user)throws DuplicateKeyException,Exception  {
        userMapper.deleteRole(user);
        userMapper.addRole(user);
    }

    public void delete(int[] ids) {
        userMapper.delete(ids);
    }

    public User findByAccount(String account) {
        return userMapper.findByAccount(account);
    }

    public void updateByRoleIds(int[] ids) {
        userMapper.updateByRoleIds(ids);
    }

    public void updatePWD(String account, String newPassword) {
        userMapper.updatePWD(account,newPassword);
    }

    public List<User> findByRoleCode(String roleCode) {
        return  userMapper.findByRoleCode(roleCode);
    }

    public List<User> findByCompany(String companyName) {
        return userMapper.findByCompany(companyName);
    }

    public List<User> findByBond(String bondCode) {
        return userMapper.findByBond(bondCode);
    }

    public List<User>  findLinkmanByCompany(String companyName) {
        return userMapper.findLinkmanByCompany(companyName);
    }

    public Role findRoleById(String id) {
        return userMapper.findRoleById(id);
    }

    public void deleteRoles(int[] ids) {
        userMapper.deleteRoles(ids);
    }

    public List<User> findUserRoleById(String id){
        List<User> userList = userMapper.findUserRoleById(id);
        return userList;
    }
}
