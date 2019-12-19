package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface RoleMapper {

    List<Role> findByPage(ReqData reqData);

    Role findById(Integer id);

    void save(Role role);

    void update(Role role);

    void delete(int[] ids);

    List findAll();

    void deleteRolePremissionByRoleId(int roleId);

    void insertRolePremission(Map<String, Object> param);

    void deleteRolePremissionByRoleIds(int[] roleIds);

    List<String> findByUser(String name);

    Set<String> findByUserId(String userId);
}
