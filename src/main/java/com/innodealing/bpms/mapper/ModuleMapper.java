package com.innodealing.bpms.mapper;

import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.model.Module;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ModuleMapper {

    List<Module> findByPage(ReqData reqData);

    Module findById(Integer id);

    void save(Module module);

    void update(Module module);

    void delete(Integer[] ids);

    List findAll();
    List<Module> getModuleByUserId(String userId);

    List<Module> findByIds(int[] ids);
}
