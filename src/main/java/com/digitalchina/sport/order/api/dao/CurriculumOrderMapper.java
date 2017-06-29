package com.digitalchina.sport.order.api.dao;


import com.digitalchina.sport.order.api.model.CurriculumOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface CurriculumOrderMapper {

    int deleteByPrimaryKey(String id);


    int insert(CurriculumOrder record);


    int insertSelective(CurriculumOrder record);


    CurriculumOrder selectByPrimaryKey(String id);


    int updateByPrimaryKeySelective(CurriculumOrder record);


    int updateByPrimaryKey(CurriculumOrder record);

    int selectJoinCurriculumCount(@Param("student_name") String student_name,
                                  @Param("phone") String phone,
                                  @Param("xubanCurriculumIdList") List<Integer> xubanCurriculumIdList);
}

