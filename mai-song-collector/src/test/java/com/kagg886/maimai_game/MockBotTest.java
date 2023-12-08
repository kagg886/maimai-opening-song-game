package com.kagg886.maimai_game;

import com.kagg886.maimai_game.dao.GameProgress;
import com.kagg886.maimai_game.dao.SongInfo;
import com.kagg886.maimai_game.service.GameManager;
import com.kagg886.maimai_game.service.SongManager;
import com.kagg886.maimai_game.service.SongManagerTest;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.mock.MockBot;
import net.mamoe.mirai.mock.MockBotFactory;
import net.mamoe.mirai.mock.contact.MockGroup;
import net.mamoe.mirai.mock.contact.MockMember;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

/**
 * @author kagg886
 * @date 2023/7/13 9:05
 **/
public class MockBotTest {

    private static MockMember member1, member2, member3, member4;

    private static MockGroup group;

    @BeforeAll
    static void loadSong() {
        SongManagerTest.LoadAllSongs();
        MockBotFactory.initialize();

        MockBot bot = MockBotFactory.Companion.newMockBotBuilder().id(1693256674).create();
        bot.login();
        bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, GroupListener.getInstance());

        group = bot.addGroup(114514, "测试群");

        member1 = group.addMember(1, "1");
        member2 = group.addMember(2, "2");
        member3 = group.addMember(3, "3");
        member4 = group.addMember(4, "4");
    }

    @SuppressWarnings("all")
    @Test
    void joinGame() throws InterruptedException {
        member1.says("舞萌开字母");
        Thread.sleep(1000);
        GameProgress progress = GameManager.getInstance().findGameByGroup(group);
        List<SongInfo> infos = progress.getAnswers();
        int index = 0;
        while (!progress.isAllComplete()) {
            int base = Math.random() > 0.5 ? 'a' : 'A';

            member2.says("开字母 " + (char) (new Random().nextInt(25) + base));
            Thread.sleep(1000);
            member3.says("开字母 " + "a");
            Thread.sleep(1000);

            if (Math.random() > 0.5) {
                member4.says("回答 " + infos.get(Math.min(infos.size()-1,index++)).getName());
            } else {
                member4.says("回答 " + SongManager.getInstance().random(1).get(0).getName());
            }
        }
    }
}
