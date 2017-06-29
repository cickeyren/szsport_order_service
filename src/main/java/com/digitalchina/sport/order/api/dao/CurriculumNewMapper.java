package com.digitalchina.sport.order.api.dao;


import com.digitalchina.sport.order.api.model.CurriculumNew;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CurriculumNewMapper {

    int deleteByPrimaryKey(Integer id);


    int insert(CurriculumNew record);


    int insertSelective(CurriculumNew record);


    CurriculumNew selectByPrimaryKey(Integer id);


    int updateByPrimaryKeySelective(CurriculumNew record);


    int updateByPrimaryKey(CurriculumNew record);

}

