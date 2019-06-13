
package com.firstest.eviews.textmark;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EntitiesClass {
    private String doc_id, title, content;
    private int sent_id;
    ArrayList<Entity> data = new ArrayList<>();

    public void init(String title, String content, int sent_id, String doc_id) {
        this.title = title;
        this.content = content;
        this.sent_id = sent_id;
        this.doc_id = doc_id;
        data.clear();
    }

    public EntitiesClass() {

    }

    public String getDocid() {
        return doc_id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getSentid() {
        return sent_id;
    }

    public void appendEntity(Entity t) {
        data.add(t);
    }

    public void deleteEntity(Entity t) {
        data.remove(t);
    }

    public void clearAll()
    {
        this.doc_id = null;
        this.sent_id = -1;
        this.title = null;
        this.content = null;
        data.clear();

    }

    public ArrayList<Entity> getEntities() {
        return data;
    }


    public void JSONLoader(String str) {
        try {
            JSONObject t = new JSONObject(str);
            this.title = t.getString("title");
            this.content = t.getString("content");
            this.sent_id = t.getInt("sent_id");
            this.doc_id = t.getString("doc_id");
            data.clear();
            JSONArray t1 = t.getJSONArray("entities");
            for (int i = 0; i < t1.length(); i++) {
                JSONObject t2 = t1.getJSONObject(i);
                Entity tt = new Entity(t2.getString("EntityName"),
                        t2.getInt("Start"), t2.getInt("End"), t2.getString("NerTag"));
                data.add(tt);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String toJSONString() {
        JSONObject t = new JSONObject();
        JSONArray t1 = new JSONArray();
        try {

            t.put("title", title);
            t.put("content", content);
            t.put("sent_id", sent_id);
            t.put("doc_id", doc_id);

            for (Entity i : data
                    ) {
                JSONObject t2 = new JSONObject();
                t2.put("EntityName", i.entityName);
                t2.put("Start", i.start);
                t2.put("End", i.end);
                t2.put("NerTag", i.nerTag);
                t1.put(t2);

            }
            t.put("entities", t1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return t.toString();
    }
    public String toJSONStringtoServer() {
        JSONObject t=new JSONObject();
        JSONArray t1 = new JSONArray();
        try{
            t.put("doc_id",doc_id);
            t.put("sent_id",sent_id);
            for (Entity i:data
                    ) {
                JSONObject t2 = new JSONObject();
                t2.put("EntityName",i.entityName);
                t2.put("Start",i.start);
                t2.put("End",i.end);
                t2.put("NerTag",i.nerTag);
                t1.put(t2);

            }
            t.put("entities",t1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t.toString();
    }

}