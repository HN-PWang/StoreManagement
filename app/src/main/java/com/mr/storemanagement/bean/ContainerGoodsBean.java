package com.mr.storemanagement.bean;

import java.util.List;

/**
 * @auther: pengwang
 * @date: 2022/6/21
 * @email: 1929774468@qq.com
 * @description: 容器所含商品
 */
public class ContainerGoodsBean {

    public Integer id;
    public String picktaskguid;
    public String so_code;
    public String site_code;
    public String currlocation;
    public String fromlocation;
    public String tolocation;
    public String container_code;
    public String item_Code;
    public String barcode;
    public String item_name;
    public String item_batch_id;
    public Integer request_Qty;
    public String item_unit;
    public String product_batch;
    public Integer status;
    public String pick_time;
    public String pick_user;
    public Integer pickedqty;
    public Integer checkQty;
    public String check_user;
    public Integer is_SN;
    public Integer is_danger;
    public Integer is_fragile;
    public Integer isArrive;
    public List<String> snList;

}
