package com.kagg886.maimai_game

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("Config") {
    val questionCount by value(15)
}