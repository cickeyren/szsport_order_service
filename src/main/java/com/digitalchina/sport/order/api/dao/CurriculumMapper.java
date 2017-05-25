package com.digitalchina.sport.order.api.dao;

import com.digitalchina.sport.order.api.model.CurriculumClass;

import java.util.List;
import java.util.Map;

@org.apache.ibatis.annotations.Mapper
public interface CurriculumMapper {

    List<CurriculumClass> getCurriculumClassByCurriculumId(Map<String, Object> args);
    List<Map<String, Object>> getCurriculumOrder(Map<String, Object> args);
    Map<String, Object> getCurriculumOrderbyOrderNumber(Map<String, Object> args);
    int checkXuban(Map<String, Object> args);
    int insertOrder(Map<String, Object> args);
}