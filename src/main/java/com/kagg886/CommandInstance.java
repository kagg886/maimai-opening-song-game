package com.kagg886;

import com.kagg886.maimai_game.Config;
import net.mamoe.mirai.console.command.CommandOwner;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import org.jetbrains.annotations.NotNull;

/**
 * 指令集合
 *
 * @author kagg886
 * @date 2023/7/13 19:55
 **/
public class CommandInstance extends JCompositeCommand {
    public static final CommandInstance INSTANCE = new CommandInstance();

    public CommandInstance() {
        super(MaiMaiOpeningSongGame.INSTANCE, "mai-game");
        setDescription("maimai开字母游戏");
    }

    @SubCommand
    @Description("刷新配置环境")
    public void reload() {
        MaiMaiOpeningSongGame.INSTANCE.loadSongConfig();
        MaiMaiOpeningSongGame.INSTANCE.reloadPluginConfig(Config.INSTANCE);
        MaiMaiOpeningSongGame.INSTANCE.getLogger().info("刷新完成!");
    }

}
