package com.digitalchina.sport.order.api.dao;


import com.digitalchina.sport.order.api.model.CurriculumClassNew;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface CurriculumClassNewMapper {

    int deleteByPrimaryKey(String id);


    int insert(CurriculumClassNew record);


    int insertSelective(CurriculumClassNew record);


    CurriculumClassNew selectByPrimaryKey(String id);


    int updateByPrimaryKeySelective(CurriculumClassNew record);


    int updateByPrimaryKey(CurriculumClassNew record);

}

