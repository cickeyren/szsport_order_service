package com.digitalchina.sport.order.api.dao;

import com.digitalchina.sport.order.api.model.ApiOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface ApiOrderDao {

	ApiOrder getOrderInfoByPK(Map<String,Object> params);

}
