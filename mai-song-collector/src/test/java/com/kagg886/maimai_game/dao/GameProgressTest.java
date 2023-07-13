package com.kagg886.maimai_game.dao;

import com.kagg886.maimai_game.service.SongManagerTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 测试
 *
 * @author kagg886
 * @date 2023/7/13 8:08
 **/
public class GameProgressTest {
    @BeforeAll
    static void loadConfig() {
        SongManagerTest.LoadAllSongs();
    }


    @Test
    void testDisplayString() {
        GameProgress progress = new GameProgress(null,null);
        progress.lazyInit();
        System.out.println(progress.songs);
        System.out.println(progress.getDisplayString());
        progress.updateAllowDisplayChar('a');
        System.out.println(progress.getDisplayString());
    }

    @Test
    void testAnswer() {
        GameProgress progress = new GameProgress(null,null);
        progress.lazyInit();

        System.out.println(progress.songs);
        System.out.println(progress.getDisplayString());

        SongInfo info = progress.songs.keySet().toArray(new SongInfo[0])[0];
        System.out.println(info);
        progress.updateKnown(info);
        System.out.println(progress.getDisplayString());
    }

}
