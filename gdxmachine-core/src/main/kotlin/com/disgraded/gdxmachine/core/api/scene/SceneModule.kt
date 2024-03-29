package com.disgraded.gdxmachine.core.api.scene

import com.disgraded.gdxmachine.core.Config
import com.disgraded.gdxmachine.core.Core
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class SceneModule : Core.Module {

    class Api(private val sceneModule: SceneModule) : Core.Api {
        fun set(sceneClass: KClass<out Scene>) = sceneModule.setScene(sceneClass)
    }

    override val api: Core.Api = Api(this)

    private lateinit var core: Core
    private var nextScene: KClass<out Scene>? = null
    private var currentScene : Scene? = null

    override fun load(core: Core, config: Config) {
        this.core = core
    }

    override fun update(deltaTime: Float) {
        if (nextScene !== null) {
            if (currentScene !== null) {
                currentScene!!.destroy()
                core.reset()
            }
            currentScene = nextScene!!.createInstance()
            nextScene = null
            currentScene!!.context = core.context
            currentScene!!.initialize()
        }
        if (currentScene !== null) {
            currentScene!!.update(deltaTime)
        }
    }

    override fun unload() {
        currentScene = null
        nextScene = null
    }

    private fun setScene(sceneClass: KClass<out Scene>) {
        if (nextScene == null) {
            nextScene = sceneClass
        }
    }

    fun destroy() {
        if (currentScene !== null) currentScene!!.destroy()
    }

    fun pause() {
        if (currentScene !== null) currentScene!!.pause()
    }

    fun resume() {
        if (currentScene !== null) currentScene!!.resume()
    }
}