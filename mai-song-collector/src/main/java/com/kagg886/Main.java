package com.kagg886;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.kagg886.maimai_game.dao.SongInfo;
import com.kagg886.util.IOUtil;
import org.jsoup.Jsoup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        JSONArray id_name = JSON.parseArray(Jsoup.connect("https://www.diving-fish.com/api/maimaidxprober/music_data").ignoreContentType(true).execute().body());
        JSONObject id_alias = JSON.parseObject(Jsoup.connect("https://download.fanyu.site/maimai/alias.json").ignoreContentType(true).execute().body());


        //{"id":1,"name":"","alias":["","",""]}
        List<SongInfo> infos = new ArrayList<>();
        id_name.stream().map(v -> (JSONObject) v).forEach(item -> {
            SongInfo info = new SongInfo();
            info.setId(Integer.parseInt(item.getString("id")));
            info.setName(item.getString("title"));
            info.setAlias(new ArrayList<>());

            id_alias.forEach((alias, o) -> {
                JSONArray array = (JSONArray) o; //alias对应别名，array对应id
                if (array.contains(String.valueOf(info.getId()))) {
                    info.getAlias().add(alias);
                }
            });
            infos.add(info);
        });

        String k = JSON.toJSONString(infos);

        System.out.println(JSON.parseArray(k,SongInfo.class));
        IOUtil.writeStringToFile(new File("mai-data.json"),k);
    }
}