package com.digitalchina.sport.order.api.service;

import com.digitalchina.common.utils.OrderHelp;
import com.digitalchina.sport.order.api.dao.CurriculumMapper;
import com.digitalchina.sport.order.api.model.CurriculumClass;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by yang_ on 2017/5/23.
 */
@Service
public class CurriculumService {
    @Autowired
    private CurriculumMapper curriculumMapper;
    public Map<String, Object> signUp(Map<String, Object> args) throws Exception {
        Map<String, Object> res = Maps.newHashMap();
        List<CurriculumClass> curriculumClasss = curriculumMapper.getCurriculumClassByCurriculumId(args);
        if (curriculumClasss.size() == 1) {
            CurriculumClass curriculumClass = curriculumClasss.get(0);
            if (curriculumClass.getSign_up() >= curriculumClass.getMax_people()) {//判断是否有名额
                res.put("code", "001");//没有名额了
            }
            Map<String, Object> checkargs = Maps.newHashMap();
            String xuban_curriculum = curriculumClass.getXuban_curriculum();//获取可享受该班级续报优惠的课程id
            List<Integer> xuban_curriculums = new Gson().fromJson(xuban_curriculum, List.class);
            checkargs.put("idCard", args.get("idCard"));
            checkargs.put("xuban_curriculums", xuban_curriculums);
            if (curriculumMapper.checkXuban(checkargs) > 0) {//查看是否享受续班优惠
                args.put("fee", curriculumClass.getDiscount_fee());
            } else {
                args.put("fee", curriculumClass.getFee());
            }
            args.put("fee_msg", curriculumClass.getFee_mark());
            String id = OrderHelp.getUUID();
            args.put("id", id);
            args.put("order_number", OrderHelp.getOrderNum());
            curriculumMapper.insertOrder(args);
            res.put("code", "000");
            res.put("id", id);
        }
        return res;
    }

    public List<Map<String, Object>> getCurriculumOrder(Map<String, Object> args) {
        return curriculumMapper.getCurriculumOrder(args);
    }
    public Map<String, Object> getCurriculumOrderbyOrderNumber(Map<String, Object> args) {
        return curriculumMapper.getCurriculumOrderbyOrderNumber(args);
    }
}
