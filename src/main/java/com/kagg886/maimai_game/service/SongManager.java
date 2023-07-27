package com.kagg886.maimai_game.service;

import com.alibaba.fastjson2.JSON;
import com.kagg886.maimai_game.dao.SongInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 记载了所有已加载的歌曲
 *
 * @author kagg886
 * @date 2023/7/12 21:35
 **/
public class SongManager {

    protected List<SongInfo> allSongs = new ArrayList<>();

    private static final Random r = new Random();

    public int count() {
        return allSongs.size();
    }

    public SongInfo findSongById(int id) {
        return allSongs.stream().filter(songInfo -> songInfo.getId() == id).findFirst().orElse(null);
    }

    public SongInfo findSongByName(String name) {
        return allSongs.stream().filter(songInfo -> songInfo.getName().equals(name)).findFirst().orElse(null);
    }

    public List<SongInfo> findSongsByAlias(String alias) {
        return allSongs.stream().filter(songInfo -> songInfo.getAlias().contains(alias)).collect(Collectors.toList());
    }

    public List<SongInfo> random(int num) {
        if (allSongs == null || allSongs.size() == 0) {
            throw new IllegalStateException("歌曲列表未初始化");
        }
        List<SongInfo> infos = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            addRandomNoRepeat(infos);
        }
        return infos;
    }

    private void addRandomNoRepeat(List<SongInfo> filter) {
        SongInfo add = allSongs.get(r.nextInt(allSongs.size()));
        if (filter.contains(add)) {
            addRandomNoRepeat(filter);
            return;
        }
        filter.add(add);
    }

    public void loadAllSongs(String json) {
        allSongs = JSON.parseArray(json, SongInfo.class);
    }

    @SuppressWarnings("unused")
    public void loadAllSongs(List<SongInfo> list) {
        allSongs = list;
    }

    private static final SongManager INSTANCE = new SongManager();

    private SongManager() {
    }

    public static SongManager getInstance() {
        return INSTANCE;
    }

}
