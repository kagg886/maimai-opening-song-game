package com.kagg886.maimai_game.dao;

import com.kagg886.maimai_game.Config;
import com.kagg886.maimai_game.service.SongManager;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.*;

/**
 * 代表了一个群的游戏进度
 *
 * @author kagg886
 * @date 2023/7/12 20:26
 **/
public class GameProgress {
    private final Group group; //游戏开始所在的群
    private final NormalMember owner; //游戏发起人

    protected final HashMap<SongInfo, List<Character>> songs; //随机出的题目

    private final List<Character> allowDisplayChars; //允许显示的字符

    private List<SongInfo> known; //已经知道的曲目

    private final HashMap<NormalMember, Integer> ranks; //排名

    public GameProgress(Group group, NormalMember owner) {
        this.group = group;
        this.owner = owner;
        allowDisplayChars = new ArrayList<>();
        songs = new HashMap<>();
        known = new ArrayList<>();
        ranks = new HashMap<>();
    }

    public List<SongInfo> getAnswers() {
        List<SongInfo> infos = songs.keySet().stream().toList();
        known = infos;
        return infos;
    }

    public void updateAllowDisplayChar(char c) {
        allowDisplayChars.add(c);
    }

    public List<Character> getAllowDisplayChars() {
        return allowDisplayChars;
    }

    public List<Map.Entry<NormalMember, Integer>> getRanks() {
        return ranks.entrySet().stream().sorted((a,b) -> b.getValue() - a.getValue()).toList();
    }

    public void answerRank(NormalMember member) {
        ranks.put(member, ranks.getOrDefault(member, 0) + 1);
    }

    public void updateKnown(SongInfo info) {
        for (Map.Entry<SongInfo, List<Character>> entry : songs.entrySet()) {
            if (entry.getKey() == info) {
                known.add(info);
                return;
            }
        }
        throw new IllegalStateException("本歌曲不在题目列表中哦~");
    }

    public boolean isAllComplete() {
        return songs.size() <= known.size();
    }

    public void lazyInit() {
        List<SongInfo> infos = SongManager.getInstance().random(Config.INSTANCE.getQuestionCount());
        for (SongInfo info : infos) {
            List<Character> characters = new ArrayList<>();
            for (char c : info.getName().toCharArray()) {
                characters.add(c);
            }
            songs.put(info, characters);
        }
    }

    public List<String> getDisplayString() {
        if (songs.isEmpty()) {
            throw new IllegalStateException("游戏未初始化");
        }
        List<String> result = new ArrayList<>();

        for (Map.Entry<SongInfo, List<Character>> entry : songs.entrySet()) {
            List<Character> songChar = entry.getValue();
            SongInfo song = entry.getKey();
            if (known.contains(song)) {
                result.add(song.getName());
                continue;
            }

            List<Character> encrypt = songChar.stream().map(charSequence -> {
                if (!allowDisplayChars.contains(charSequence) && charSequence != ' ') {
                    //该字符不在可显示列表内且不为空格返回?，否则返回原字符
                    return '?';
                }
                return charSequence;
            }).toList();

            char[] r = new char[encrypt.size()];
            for (int i = 0; i < r.length; i++) {
                r[i] = encrypt.get(i);
            }
            result.add(new String(r));
        }
        return result;
    }

    public Group getGroup() {
        return group;
    }

    public NormalMember getOwner() {
        return owner;
    }


}
