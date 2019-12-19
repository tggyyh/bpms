package com.innodealing.bpms.mapper;

import com.innodealing.bpms.model.CustomSubMatter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CustomSubMatterMapper {
    int insertCustomSubMatter(CustomSubMatter customSubMatter);
    int insertCustomSubMatterList(List<CustomSubMatter> customSubMatterList);
    int deleteByCustomId(int customId);
    List<CustomSubMatter> findByProcessId(String processId);
    List<CustomSubMatter> findByProcessId1(String processId);
}
