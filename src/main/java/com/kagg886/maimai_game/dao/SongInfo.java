package com.kagg886.maimai_game.dao;

import com.alibaba.fastjson2.JSON;

import java.util.List;


public class SongInfo {
    private int id;
    private String name;
    private List<String> alias;

    @SuppressWarnings("unused")
    public SongInfo(int id, String name, List<String> alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }
    public SongInfo() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getAlias() {
        return alias;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
