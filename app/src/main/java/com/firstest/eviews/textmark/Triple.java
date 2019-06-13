package com.firstest.eviews.textmark;

public class Triple {
    String id = null, left_entity = null, right_entity = null;
    int left_e_start,left_e_end,
        right_e_start,right_e_end,
        relation_start,relation_end,
        relation_id,status;



    public Triple(String id, int left_e_start, int left_e_end,
                  int right_e_start,int right_e_end,
                  int relation_start,int relation_end,
                  String left_entity,String right_entity,
                  int relation_id,int status) {
        this.id = id;
        this.left_e_start = left_e_start;
        this.left_e_end = left_e_end;
        this.right_e_start = right_e_start;
        this.right_e_end = right_e_end;
        this.relation_start = relation_start;
        this.relation_end = relation_end;
        this.relation_id = relation_id;
        this.left_entity = left_entity;
        this.right_entity = right_entity;
        this.status = status;
    }


}
