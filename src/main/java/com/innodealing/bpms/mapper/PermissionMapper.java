package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface PermissionMapper {

    List<Permission> findByPage(ReqData reqData);

    Permission findById(Integer id);

    void save(Permission permission);

    void update(Permission permission);

    void delete(int[] ids);

    List<Permission> findAll();

    List<Permission> findByRoleId(Integer roleId);

    Set<String> getPermissionsByUserId(String userId);

    void deleteByModuleIds(Integer[] moduleIds);
}
