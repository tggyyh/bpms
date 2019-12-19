package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.PermissionMapper;
import com.innodealing.bpms.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transactional
@Service("permissionService")
public class PermissionService {
    @Autowired
    PermissionMapper permissionMapper;

    public List findAll() {
        return permissionMapper.findAll();
    }

    public PageInfo<Permission> findByPage(ReqData reqData) {
        List<Permission> permissionList;

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
            permissionList = permissionMapper.findByPage(reqData);
            page = new PageInfo(permissionList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public Permission findById(Integer id) {
        return permissionMapper.findById(id);
    }

    public void save(Permission permission) throws DuplicateKeyException,Exception  {
         permissionMapper.save(permission);
    }

    public void update(Permission permission)throws DuplicateKeyException,Exception  {
        permissionMapper.update(permission);
    }

    public void delete(int[] ids) throws Exception{
        permissionMapper.delete(ids);
    }

    public List<Permission> findByRoleId(Integer roleId) {
        return permissionMapper.findByRoleId(roleId);
    }

    public Set<String> getPermissionsByUserId(String userId) {
        return permissionMapper.getPermissionsByUserId(userId);
    }

    public void deleteByModuleIds(Integer[] moduleIds) {
        permissionMapper.deleteByModuleIds(moduleIds);
    }
}
