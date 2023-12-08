package com.kagg886.maimai_game.service;

import com.kagg886.maimai_game.dao.SongInfo;
import com.kagg886.util.IOUtil;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SongManagerTest {


    @BeforeAll
    public static void LoadAllSongs() {
        File base = new File("mai-data-test.json");
        System.out.println(base.getAbsolutePath());
        Assertions.assertDoesNotThrow(() -> SongManager.getInstance().loadAllSongs(IOUtil.loadStringFromFile(base)));
        Assertions.assertNotEquals(SongManager.getInstance().allSongs.size(), 0);
    }

    @Test
    void testFindById() {
        SongInfo info = SongManager.getInstance().findSongById(653);
        System.out.println(info);
        Assertions.assertNotNull(info);
    }

    @Test
    void testFindByAlias() {
        List<SongInfo> info = SongManager.getInstance().findSongsByAlias("茄子卡狗");
        System.out.println(info.get(0));
        Assertions.assertNotEquals(info.size(), 0);
    }

    @Test
    void testRandom() throws IOException {
        File base = new File("mai-data.json");
        SongManager.getInstance().loadAllSongs(IOUtil.loadStringFromFile(base));
        List<SongInfo> infos = SongManager.getInstance().random(100);
        System.out.println(infos);
        for (int i = 0; i < infos.size() - 1; i++) {
            for (int j = i+1; j < infos.size(); j++) {
                if (infos.get(i) == infos.get(j)) {
                    System.out.println(infos.get(i));
                    System.out.println(infos.get(j));
                    throw new RuntimeException(i + "==" + j);
                }
            }
        }
    }

}