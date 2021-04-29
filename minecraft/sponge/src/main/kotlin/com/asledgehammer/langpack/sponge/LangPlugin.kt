package com.asledgehammer.langpack.sponge

import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Plugin

@Plugin(id = "langpack", name = "LangPack", version = "1.0.0")
class LangPlugin {

    val pack = SpongeLangPack(LangPlugin::class.java.classLoader)
    val testsEnabled = true

    @Listener
    fun on(event: GameInitializationEvent) {
        pack.append("lang", true)
        if (testsEnabled) pack.append("lang_test", true)
        val commandManager = Sponge.getCommandManager()
        commandManager.register(this, LangCommand(this), "lang")
    }
}
