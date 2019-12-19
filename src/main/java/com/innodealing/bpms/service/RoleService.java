package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.RoleMapper;
import com.innodealing.bpms.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service("roleService")
public class RoleService {
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    UserService userService;
    public List findAll() {
        return roleMapper.findAll();
    }
    public List<String> findByUser(String name) {
        return roleMapper.findByUser(name);
    }
    public PageInfo<Role> findByPage(ReqData reqData) {
        List<Role> roleList;

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
            roleList = roleMapper.findByPage(reqData);
            page = new PageInfo(roleList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public Role findById(Integer id) {
        return roleMapper.findById(id);
    }

    public void save(Role role) throws DuplicateKeyException,Exception  {
        roleMapper.save(role);
    }

    public void update(Role role)throws DuplicateKeyException,Exception  {
        roleMapper.update(role);
    }

    public void delete(int[] ids) throws Exception{
        roleMapper.deleteRolePremissionByRoleIds(ids);
        userService.deleteRoles(ids);
        roleMapper.delete(ids);
    }

    public void updateRolePremission(int roleId, int[] permissionIds)throws Exception {
        roleMapper.deleteRolePremissionByRoleId(roleId);
        if(!(permissionIds.length==1&&permissionIds[0]==0)) {
            Map<String, Object> param = new HashMap();
            param.put("roleId", roleId);
            param.put("permissionIds", permissionIds);
            roleMapper.insertRolePremission(param);
        }
    }

    public Set<String> findByUserId(String tid) {
        return roleMapper.findByUserId(tid);
    }
}
