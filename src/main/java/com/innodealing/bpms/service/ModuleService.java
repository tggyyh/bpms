package com.innodealing.bpms.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.innodealing.bpms.common.model.ReqData;
import com.innodealing.bpms.mapper.ModuleMapper;
import com.innodealing.bpms.model.Module;
import com.innodealing.bpms.model.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Ids;

import java.util.ArrayList;
import java.util.List;
@Transactional
@Service("moduleService")
public class ModuleService {
    @Autowired
    ModuleMapper moduleMapper;
    @Autowired
    PermissionService permissionService;
    public List findAll() {
        return moduleMapper.findAll();
    }

    public PageInfo<Module> findByPage(ReqData reqData) {
        List<Module> moduleList;

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
            moduleList = moduleMapper.findByPage(reqData);
            page = new PageInfo(moduleList);

        } finally {
            PageHelper.clearPage();
        }

        return page;
    }

    public Module findById(Integer id) {
        return moduleMapper.findById(id);
    }

    public void save(Module module) throws DuplicateKeyException,Exception  {
        moduleMapper.save(module);
        Permission p = new Permission();
        p.setCode("show");
        p.setName("查看");
        p.setModuleId(module.getId());
        permissionService.save(p);
    }

    public void update(Module module)throws DuplicateKeyException,Exception  {
        moduleMapper.update(module);
    }

    public void delete(int[] ids) throws Exception{

        List<Module> listModule = moduleMapper.findByIds(ids);
        List<Module> modules = getListChildModules(listModule,findAll());
        List<Integer> list = new ArrayList();
        list = getIds(modules, list);
        Integer[] moduleIds = (Integer[])list.toArray(new Integer[list.size()]);
        permissionService.deleteByModuleIds(moduleIds);
        moduleMapper.delete(moduleIds);
    }
    public List<Integer> getIds(List<Module> modules, List<Integer> list) {
        for(Module module:modules){
            list.add(module.getId());
            getIds(module.getModules(),list);
        }
        return list;
    }

    public List getModuleByUserId(String userId) {
        List<Module> moduleList = moduleMapper.getModuleByUserId(userId);
        return convertToTree(moduleList);
    }
    private List<Module> getListChildModules(List<Module> rootModules, List<Module> allModules) {
        for (Module module : rootModules) {
            List<Module> listModule = getChildModuleById(module.getId(), allModules);
            module.setModules(listModule);
            getListChildModules(listModule, allModules);
        }
        return rootModules;
    }

    private List<Module> getChildModuleById(Integer id, List<Module> allModules) {
        List<Module> listModule = new ArrayList<Module>();
        for (Module module : allModules) {
            if (module.getParentId() == id) {
                listModule.add(module);
            }
        }
        return listModule;
    }

    public List<Module> convertToTree(List<Module> modules) {
        List<Module> rootModuleList = new ArrayList();
        if (modules != null && !modules.isEmpty()) {
            for (Module module : modules) {
                //获取一级节点
                if (module.getParentId() == null) {
                    rootModuleList.add(module);
                }
            }
            getListChildModules(rootModuleList, modules);
        }
        return  rootModuleList;
    }
}
