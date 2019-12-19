package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.SynUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SynUserMapper {
    void delete();
    void save(List<SynUser> list);
}
