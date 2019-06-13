package com.firstest.eviews.textmark;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TriplesClass {
    private String doc_id, title, sent_ctx;
    private int sent_id;
    
    ArrayList<Triple> data = new ArrayList<>();
    public TriplesClass() {

    }
    

    public String getDocid() {
        return doc_id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return sent_ctx;
    }

    public int getSentid() {
        return sent_id;
    }

    public void appendTriple(Triple t) {
        data.add(t);
    }

    public void deleteTriple(Triple t) {
        data.remove(t);
    }

    public ArrayList<Triple> getTriples() {
        return data;
    }




    public void JSONLoader(String str) {
        try {
            JSONObject t = new JSONObject(str);
            this.title = t.getString("title");
            this.sent_ctx = t.getString("sent_ctx");
            this.sent_id = t.getInt("sent_id");
            this.doc_id = t.getString("doc_id");
            data.clear();
            JSONArray t1 = t.getJSONArray("triples");
            for (int i = 0; i < t1.length(); i++) {
                JSONObject t2 = t1.getJSONObject(i);
                Triple tt = new Triple(t2.getString("id"),
                        t2.getInt("left_e_start"),t2.getInt("left_e_end"),
                        t2.getInt("right_e_start"),t2.getInt("right_e_end"),
                        t2.getInt("relation_start"),t2.getInt("relation_end"),
                        t2.getString("left_entity"),t2.getString("right_entity"),
                        t2.getInt("relation_id"),1);
                data.add(tt);
            }
        }   catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String toJSONString() {
        JSONObject t=new JSONObject();
        JSONArray t1 = new JSONArray();
        try{

            t.put("doc_id",doc_id);
            t.put("sent_id",sent_id);
            t.put("title",title);
            t.put("sent_ctx",sent_ctx);

            for (Triple i:data
                    ) {
                JSONObject t2 = new JSONObject();
                t2.put("id",i.id);
                t2.put("left_e_start",i.left_e_start);
                t2.put("left_e_end",i.left_e_end);
                t2.put("right_e_start",i.right_e_start);
                t2.put("right_e_end",i.right_e_end);
                t2.put("relation_start",i.relation_start);
                t2.put("relation_end",i.relation_end);
                t2.put("left_entity",i.left_entity);
                t2.put("right_entity",i.right_entity);
                t2.put("relation_id",i.relation_id);
                t2.put("status",i.status);
                t1.put(t2);

            }
            t.put("triples",t1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t.toString();
    }

}
