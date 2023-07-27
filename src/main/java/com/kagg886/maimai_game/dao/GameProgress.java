package com.kagg886.maimai_game.dao;

import com.kagg886.maimai_game.Config;
import com.kagg886.maimai_game.service.SongManager;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;

import java.util.*;
import java.util.stream.Collectors;

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

    private final List<SongInfo> known; //已经知道的曲目

    private final HashMap<NormalMember, Integer> ranks; //排名

    public GameProgress(Group group, NormalMember owner) {
        this.group = group;
        this.owner = owner;
        allowDisplayChars = new ArrayList<>() {
            @Override
            public boolean contains(Object o) {
                if (o instanceof Character) {
                    char c = (Character) o;
                    if (c > 'a' && c < 'z') { //忽略大小写
                        return super.contains(c) || stream().anyMatch(v -> v == c - 32);
                    }

                    if (c > 'A' && c < 'Z') {
                        return super.contains(c) || stream().anyMatch(v -> v == c + 32);
                    }

                    if (c > 65281 && c < 65374) { //忽略全半角
                        return super.contains(c) || stream().anyMatch(v -> v == c - 65248);
                    }

                    if (c > 33 && c < 126) {
                        return super.contains(c) || stream().anyMatch(v -> v == c + 65248);
                    }
                    return super.contains(c);
                }
                return super.contains(o);
            }
        };
        songs = new HashMap<>();
        known = new ArrayList<>();
        ranks = new HashMap<>();
    }

    public List<SongInfo> getAnswers() {
        return new ArrayList<>(songs.keySet());
    }

    public void updateAllowDisplayChar(char c) {
        if (allowDisplayChars.contains(c)) {
            throw new IllegalStateException("该字母已经被拆过惹!");
        }
        allowDisplayChars.add(c);
    }

    public List<Character> getAllowDisplayChars() {
        return allowDisplayChars;
    }

    public List<Map.Entry<NormalMember, Integer>> getRanks() {
        return ranks.entrySet().stream().sorted((a, b) -> b.getValue() - a.getValue()).collect(Collectors.toList());
    }

    public void answerRank(NormalMember member) {
        Integer rank = ranks.getOrDefault(member,0);
        ranks.put(member,rank+1);

    }

    public void updateKnown(SongInfo info) {
        if (known.contains(info)) {
            throw new IllegalStateException("这个曲目已经有人作答了喵~");
        }
        for (Map.Entry<SongInfo, List<Character>> entry : songs.entrySet()) {
            if (entry.getKey() == info) {
                known.add(info);
                return;
            }
        }
        throw new IllegalStateException("本歌曲不在题目列表中哦~");
    }

    public boolean isAllComplete() { //已知曲目中包含所有题目曲
        return new HashSet<>(known).containsAll(songs.keySet());
    }

    public void lazyInit() {
        int count = 5;
        try {
            count = Config.INSTANCE.getQuestionCount();
        } catch (NoClassDefFoundError ignored) {
            //mock时此代码加载失败，此时证明为调试环境，可适当减少count值
        }
        List<SongInfo> infos = SongManager.getInstance().random(count);
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
                result.add("[√]:" + song.getName());
                continue;
            }

            List<Character> encrypt = songChar.stream().map(charSequence -> {

                if (!allowDisplayChars.contains(charSequence) && charSequence != ' ') {
                    //该字符不在可显示列表内且不为空格返回?，否则返回原字符
                    return '?';
                }

                return charSequence;
            }).collect(Collectors.toList());

            char[] r = new char[encrypt.size()];
            for (int i = 0; i < r.length; i++) {
                r[i] = encrypt.get(i);
            }
            result.add("[x]:" + new String(r));
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
