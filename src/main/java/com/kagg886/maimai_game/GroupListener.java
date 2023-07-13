package com.kagg886.maimai_game;

import com.kagg886.maimai_game.dao.GameProgress;
import com.kagg886.maimai_game.dao.SongInfo;
import com.kagg886.maimai_game.service.GameManager;
import com.kagg886.maimai_game.service.SongManager;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 群监听器
 *
 * @author kagg886
 * @date 2023/7/12 20:16
 **/
public class GroupListener implements Consumer<GroupMessageEvent> {

    @Override
    public void accept(GroupMessageEvent event) {
        String command = event.getMessage().contentToString();
        NormalMember sender = ((NormalMember) event.getSender());

        if (command.startsWith("回答 ")) {
            try {
                GameProgress progress = GameManager.getInstance().findGameByGroup(event.getGroup());
                String[] str = command.split(" ",2);
                if (str.length != 2) {
                    event.getGroup().sendMessage("格式错误!\n正确的格式为:回答 字母/数字/标点符号/日文\n例如:回答 QZKago Requiem/回答 茄子卡狗/回答 653");
                    return;
                }
                List<SongInfo> result = new ArrayList<>();

                try {
                    result.add(Objects.requireNonNull(SongManager.getInstance().findSongById(Integer.parseInt(str[1])))); //先根据id寻找歌曲
                } catch (NumberFormatException | NullPointerException e) {
                    try {
                        result.add(Objects.requireNonNull(SongManager.getInstance().findSongByName(str[1]))); //否则根据名字寻找歌曲
                    } catch (NullPointerException e1) {
                        List<SongInfo> infos = SongManager.getInstance().findSongsByAlias(str[1]);
                        if (infos.size() == 0) {
                            throw new IllegalStateException("没有找到这个歌曲呢~建议换个关键词!");
                        }
                        result.addAll(infos);
                    }
                }

                //结果集获取后开始对比分析
                for (SongInfo info : result) {
                    try {
                        progress.updateKnown(info);
                        StringBuilder builder = new StringBuilder();
                        progress.getDisplayString().forEach((a) -> builder.append(a).append("\n"));
                        progress.answerRank(sender);
                        event.getGroup().sendMessage("恭喜猜中曲目♪(･ω･)ﾉ~\n已开出字母:" + progress.getAllowDisplayChars() + "\n题目列表" + builder);


                        if (progress.isAllComplete()) {
                            MessageChainBuilder builder1 = new MessageChainBuilder();
                            builder1.append("游戏结束!现在是，结算时间~");
                            List<Map.Entry<NormalMember,Integer>> list = progress.getRanks();
                            list.forEach((a) -> {
                                builder1.append(new At(a.getKey().getId()));
                                builder1.append("---");
                                builder1.append(String.valueOf(a.getValue()));
                                builder1.append("\n");
                            });
                            event.getGroup().sendMessage(builder1.build());

                            GameManager.getInstance().exitGame(sender);
                        }
                        return;
                    } catch (IllegalStateException ignored) {}
                }
                throw new IllegalStateException("这个曲目不在题目列表哦,加油~");
            } catch (IllegalStateException e) {
                event.getGroup().sendMessage(e.getMessage());
            }
        }

        if (command.startsWith("开子母 ")) {
            try {
                GameProgress progress = GameManager.getInstance().findGameByGroup(event.getGroup());

                String[] str = command.split(" ",2);
                if (str.length != 2) {
                    event.getGroup().sendMessage("格式错误!\n正确的格式为:开子母 字母\n例如:开子母 a");
                    return;
                }

                if (str[1].length() != 1) {
                    event.getGroup().sendMessage("开的字母必须是一个字符哦~");
                    return;
                }
                char chr = str[1].charAt(0);

                progress.updateAllowDisplayChar(chr);
                StringBuilder builder = new StringBuilder();
                progress.getDisplayString().forEach((a) -> builder.append(a).append("\n"));
                event.getGroup().sendMessage("操作完成~\n已开出字母:" + progress.getAllowDisplayChars() + "\n题目如下:\n" + builder);
            } catch (IllegalStateException e) {
                event.getGroup().sendMessage(e.getMessage());
            }

        }

        if (command.startsWith("舞萌开字母")) {
            try {
                GameProgress progress = GameManager.getInstance().joinGame(sender);
                progress.lazyInit();
                StringBuilder builder = new StringBuilder();
                progress.getDisplayString().forEach((a) -> builder.append(a).append("\n"));
                event.getGroup().sendMessage("游戏已开始!,本次题目如下:\n" + builder + "\n开字母请发送:开子母 字母/数字/标点符号/日文\n回答曲目请发送:回答 id/曲名/别名\n玩的愉快~");
            } catch (IllegalStateException e) {
                event.getGroup().sendMessage(e.getMessage());
            }
        }
    }

    private static final GroupListener INSTANCE = new GroupListener();

    private GroupListener() {

    }

    public static GroupListener getInstance() {
        return INSTANCE;
    }

}
