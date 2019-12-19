package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Role;
import com.innodealing.bpms.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface UserMapper {

    public List findUser() ;

    List<User> findByPage(ReqData reqData);

    User findById(String id);

    void save(User user);

    void update(User user);

    void delete(int[] ids);

    User findByAccount(String account);

    void updateByRoleIds(int[] ids);

    void updatePWD(String account, String newPassword);

    List<User> findByRoleCode(String roleCode);

    List<User> findByCompany(String companyName);

    List<User> findByBond(String bondCode);

    List<User> findLinkmanByCompany(String companyName);

    Role findRoleById(String userId);

    void deleteRole(User user);

    void addRole(User user);
    void deleteRoles(int[] roleIds);

    List<User> findUserRoleById(String id);
}
