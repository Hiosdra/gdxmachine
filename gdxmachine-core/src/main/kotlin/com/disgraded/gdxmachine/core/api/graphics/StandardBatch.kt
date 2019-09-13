package com.disgraded.gdxmachine.core.api.graphics

import com.badlogic.gdx.math.Matrix4
import com.disgraded.gdxmachine.core.api.graphics.drawable.Drawable
import com.disgraded.gdxmachine.core.api.graphics.drawable.Sprite
import com.disgraded.gdxmachine.core.api.graphics.renderer.*

class StandardBatch: SpriteBatch {

    private val rendererMap = hashMapOf<String, Renderer>()
    private var currentRenderer: Renderer? = null
    private var currentRendererType: String? = null
    private var gpuCalls = 0

    init {
        rendererMap["sprite_standard"] = SpriteStandardRenderer()
        rendererMap["sprite_mask"] = SpriteMaskRenderer()
    }

    override fun render(drawableList: ArrayList<Drawable>, projectionMatrix: Matrix4): Int {
        gpuCalls = 0
        for (renderer in rendererMap.toList()) {
            renderer.second.setProjectionMatrix(projectionMatrix)
        }
        for (drawable in drawableList) {
            adaptCurrentRenderer(drawable)
            currentRenderer!!.draw(drawable)
        }
        return gpuCalls
    }

    private fun adaptCurrentRenderer(drawable: Drawable) {
        val type = getRendererType(drawable) ?: throw RuntimeException("Wrong drawable type sent to the standard batch [${drawable.type}]")
        if (currentRendererType !== type) {
            if (currentRenderer !== null) gpuCalls += currentRenderer!!.end()
            currentRendererType = type
            val newRenderer = rendererMap[type] ?: throw RuntimeException("Renderer with type $type isn't registered yet")
            currentRenderer = newRenderer
            currentRenderer!!.begin()
        }
    }

    private fun getRendererType(drawable: Drawable): String? {
        return when(drawable.type) {
            Drawable.Type.SPRITE -> {
                val sprite = drawable as Sprite
                return if (sprite.getMask() === null) "sprite_standard" else "sprite_mask"
            }
            else -> null
        }
    }

    override fun dispose() {
        for(renderer in rendererMap) {
            renderer.value.dispose()
        }
        rendererMap.clear()
    }
}