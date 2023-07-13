package com.kagg886;

import com.kagg886.maimai_game.Config;
import com.kagg886.maimai_game.GroupListener;
import com.kagg886.maimai_game.service.SongManager;
import com.kagg886.util.IOUtil;
import com.kagg___.maimai_opening_song_name.BuildConfig;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

public final class MaiMaiOpeningSongGame extends JavaPlugin {
    @SuppressWarnings("all")
    public static final MaiMaiOpeningSongGame INSTANCE = new MaiMaiOpeningSongGame();

    public Function<String, File> fileSupplier = s -> {
        Path a = INSTANCE.getDataFolderPath();
        for (String sub : s.split("/")) {
            a = a.resolve(sub);
        }
        return a.toFile();
    };

    private MaiMaiOpeningSongGame() {
        super(new JvmPluginDescriptionBuilder("com.kagg886.MaiMaiOpeningSongGame", BuildConfig.VERSION_NAME)
                .name("MaiMaiOpeningSongGame")
                .author("kagg886")
                .info("maimai开曲名bot")
                .build());
    }


    @Override
    public void onEnable() {
        reloadPluginConfig(Config.INSTANCE);
        CommandManager.INSTANCE.registerCommand(CommandInstance.INSTANCE,false);
        loadSongConfig();
        getLogger().info("配置文件已加载" + SongManager.getInstance().count() + "首歌曲");
        GlobalEventChannel.INSTANCE.parentScope(this).subscribeAlways(GroupMessageEvent.class, GroupListener.getInstance());
    }

    public void loadSongConfig() {
        File song = fileSupplier.apply("songs.json");
        try {
            String s = IOUtil.loadStringFromFile(song);
            if (s.isEmpty()) {
                throw new IOException("Empty!");
            }
            SongManager.getInstance().loadAllSongs(s);
        } catch (IOException e) {
            try {
                IOUtil.writeStringToFile(song, Objects.requireNonNull(this.getResource("mai-data.json")));
                getLogger().debug("未检测到配置文件，准备释放默认配置文件");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            loadSongConfig();
        }
    }
}