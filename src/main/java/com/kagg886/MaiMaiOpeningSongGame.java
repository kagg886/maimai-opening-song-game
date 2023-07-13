package com.kagg886;

import com.kagg886.maimai_game.GroupListener;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;

public final class MaiMaiOpeningSongGame extends JavaPlugin {
    @SuppressWarnings("all")
    public static final MaiMaiOpeningSongGame INSTANCE = new MaiMaiOpeningSongGame();

    private MaiMaiOpeningSongGame() {
        super(new JvmPluginDescriptionBuilder("com.kagg886.MaiMaiOpeningSongGame", "0.1.0")
                .name("MaiMaiOpeningSongGame")
                .author("kagg886")
                .info("maimai开曲名bot")
                .build());
    }

    @Override
    public void onEnable() {
        getLogger().info("Plugin loaded!");
        GlobalEventChannel.INSTANCE.parentScope(this).subscribeAlways(GroupMessageEvent.class, GroupListener.getInstance());
    }
}