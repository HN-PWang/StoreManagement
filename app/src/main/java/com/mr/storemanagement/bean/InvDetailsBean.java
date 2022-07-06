package com.mr.storemanagement.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/7/6
 * @email: 1929774468@qq.com
 * @description:
 */
public class InvDetailsBean implements Serializable {

    public String uid;
    public String inventory_code;
    public String OriginLineSeq;
    public String stock_info_id;
    public String item_Code;
    public String item_unit;
    public String production_date;
    public String product_batch;
    public String item_batch_id;
    public String Origin_batch_id;
    public String location_code;
    public String real_qty;
    public String available_qty;
    public String order_occupy_qty;
    public String frozen_qty;
    public String check_qty;
    public String diff_qty;
    public String is_diff;
    public String status;
    public String remark;
    public String create_time;
    public String create_user;
    public String check_time;
    public String check_user;
    public String container_code;
    public String is_SN;
    public SnData sn_list;

    public static class SnData implements Serializable {
        public String work_id;
        public List<String> SnList;
    }
}
