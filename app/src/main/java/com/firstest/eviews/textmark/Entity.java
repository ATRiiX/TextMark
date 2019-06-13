package com.firstest.eviews.textmark;

    public class Entity{
        String entityName,nerTag;
        int start,end;
        public Entity(String entityName,int start,int end,String nertag)
        {
            this.entityName=entityName;
            this.nerTag = nertag;
            this.start = start;
            this.end = end;
        }
    }

