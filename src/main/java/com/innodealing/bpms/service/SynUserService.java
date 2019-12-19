package com.innodealing.bpms.service;


import com.innodealing.bpms.mapper.SynUserMapper;
import com.innodealing.bpms.model.SynUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service("synUserService")
public class SynUserService {
    @Autowired
    SynUserMapper synUserMapper;

    public void save(List<SynUser> list)throws Exception  {
        synUserMapper.delete();
        synUserMapper.save(list);
    }
}
