package com.disgraded.gdxmachine.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.disgraded.gdxmachine.core.EntryPoint
import com.disgraded.gdxmachine.core.engine.Engine

class GdxMachineLauncher {

    fun run(entryPoint: EntryPoint) {
        Engine.boot(entryPoint)
        val cfg = generateConfig()
        Lwjgl3Application(Engine.get(), cfg)
    }

    private fun generateConfig() : Lwjgl3ApplicationConfiguration {
        return Lwjgl3ApplicationConfiguration().apply {

        }
    }
}