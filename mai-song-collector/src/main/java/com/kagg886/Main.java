package com.kagg886;

import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.annotation.JSONField;
import com.kagg886.maimai_game.dao.SongInfo;
import com.kagg886.util.IOUtil;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        fetchYuzhuai();
    }

    public static void fetchYuzhuai() throws IOException {
        Map<String,YuzhuaiDAO> alias = JSON.parseObject(Jsoup.connect("https://api.yuzuai.xyz/maimaidx/maimaidxalias").ignoreContentType(true).execute().body(),
                new TypeReference<>() {});

        List<SongInfo> infos = new ArrayList<>();

        alias.forEach((id,dao) -> infos.add(new SongInfo(Integer.parseInt(id),dao.Name,dao.Alias)));

        System.out.println(JSON.toJSONString(infos));
        IOUtil.writeStringToFile(new File("mai-song-collector/mai-data.json"),JSON.toJSONString(infos, JSONWriter.Feature.PrettyFormat));
    }

    private static class YuzhuaiDAO {
        private String Name;
        private List<String> Alias;

        public YuzhuaiDAO(String name, List<String> alias) {
            Name = name;
            Alias = alias;
        }

        public YuzhuaiDAO() {
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public List<String> getAlias() {
            return Alias;
        }

        public void setAlias(List<String> alias) {
            Alias = alias;
        }

        //        "Name": "let you dive!",
//                "Alias": [
//                "let you dive!",
//                "让你潜",
//                "让你死",
//                "让你落",
//                "让你潜水",
//                "wacca",
//                "lyd",
//                "遗体捐赠"
//                ]
    }

    @SuppressWarnings("unused")
    public static void fetchLegacy() throws IOException {
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
        IOUtil.writeStringToFile(new File("mai-song-collector/mai-data.json"),k);
    }
}