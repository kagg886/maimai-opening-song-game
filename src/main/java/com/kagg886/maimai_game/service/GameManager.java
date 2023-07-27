package com.kagg886.maimai_game.service;

import com.kagg886.maimai_game.dao.GameProgress;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理游戏的启停
 *
 * @author kagg886
 * @date 2023/7/12 20:40
 **/
public class GameManager {
    private static final GameManager INSTANCE = new GameManager();

    private final List<GameProgress> progresses = Collections.synchronizedList(new ArrayList<>()); //存储所有的游戏实例

    public GameProgress joinGame(NormalMember owner) {
        boolean isJoined = progresses.stream().map(GameProgress::getGroup).map(Group::getId).collect(Collectors.toList()).contains(owner.getGroup().getId());
        if (isJoined) {
            throw new IllegalStateException("本群已经存在一个开曲名游戏，请完成当前游戏，或寻找发起人/群管理员以停止先前的游戏");
        }
        GameProgress progress = new GameProgress(owner.getGroup(), owner);
        progresses.add(progress);
        return progress;
    }

    public GameProgress findGameByGroup(Group group) {
        List<GameProgress> progresses1 = progresses.stream().filter(gameProgress -> gameProgress.getGroup().getId() == group.getId()).collect(Collectors.toList());
        if (progresses1.size() == 0) {
            throw new IllegalStateException("本群未开始开字母游戏!");
        }
        return progresses1.get(0);
    }

    public void exitGame(NormalMember member) {
        GameProgress progress0 = findGameByGroup(member.getGroup());
        if (progress0.isAllComplete()) { //游戏结束后任何人都可以结束游戏
            progresses.remove(progress0);
            return;
        }

        List<GameProgress> wantToDelete = new ArrayList<>();
        for (GameProgress progress : progresses) {
            if (progress.getGroup().contains(member.getId())) {
                if (progress.getOwner().getId() == member.getId() || member.getPermission() != MemberPermission.MEMBER) {
                    //游戏发起人为触发者 或 触发者为管理员 时 关闭游戏
                    wantToDelete.add(progress);
                    break;
                }
                throw new IllegalStateException("权限不足，无法关闭游戏!");
            }
        }
        if (wantToDelete.size() != 0) {
            progresses.removeAll(wantToDelete);
            return;
        }
        throw new IllegalStateException("本群未进行任何一个开曲目游戏");
    }

    private GameManager() {

    }

    public static GameManager getInstance() {
        return INSTANCE;
    }
}
