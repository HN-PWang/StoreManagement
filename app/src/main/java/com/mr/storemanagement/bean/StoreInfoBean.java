package com.mr.storemanagement.bean;

import java.util.List;

public class StoreInfoBean {

//    public String id;
//    public String asn_code;
//    public String line_seq;
//    public String originLineSeq;
//    public String barcode;
//    public String item_Code;
//    public String quantity;
//    public String finish_qty;
//    public String item_unit;
//    public String item_batch_id;
//    public String origin_batch;
//    public String originPutLocation;
//    public String product_batch;
//    public String production_date;
//    public String expiry_date;
//    public String remarks;
//    public String status;
//    public String create_time;
//    public String create_user;
//    public String update_time;
//    public String update_user;
//    public String keyid;
//    public String container_code;
//    public String is_SN;

    public String asn_code;
    public String barcode;
    public String item_Code;
    public String quantity;
    public String finish_qty;
    public String item_unit;
    public String is_SN;
    public String status;
    public List<SenNum> sns;

    public class SenNum {
        public String SN;
        public String keyid;
    }
}
