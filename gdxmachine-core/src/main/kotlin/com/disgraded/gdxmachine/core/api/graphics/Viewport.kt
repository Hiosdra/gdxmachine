package com.disgraded.gdxmachine.core.api.graphics

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Disposable
import com.disgraded.gdxmachine.core.Config
import com.disgraded.gdxmachine.core.api.graphics.drawable.Drawable
import com.disgraded.gdxmachine.core.api.graphics.drawable.Light

class Viewport : Disposable {

    class Api(private val viewport: Viewport) {

        val camera: OrthographicCamera = viewport.projection.camera

        var visible = true

        var z = 0

        fun enableLights() {
            viewport.lightsEnabled = true
        }

        fun disableLights() {
            viewport.lightsEnabled = false
        }

        fun isLightsEnabled(): Boolean = viewport.lightsEnabled

        fun getGPUCalls(): Int = viewport.gpuCalls

        fun draw(drawable: Drawable) {
            if (!drawable.visible) return
            when (drawable.type) {
                Drawable.Type.SPRITE -> viewport.drawableList.add(drawable)
                Drawable.Type.LIGHT -> viewport.lightList.add(drawable as Light)
            }
        }

        fun project(xRatio: Float, yRatio: Float, scaleX: Float, scaleY: Float, worldScaleX: Float = 1f,
                    worldScaleY: Float = 1f) {
            viewport.projection.project(xRatio, yRatio, scaleX, scaleY, worldScaleX, worldScaleY)
        }
    }

    private var gpuCalls = 0
    private var lightsEnabled = false

    private val projection = Projection()
    private val standardBatch = StandardBatch()
    private val deferredLightBatch = DefferedLightBatch()

    private val drawableList = arrayListOf<Drawable>()
    private val lightList = arrayListOf<Light>()

    val api = Api(this)

    fun render() {
        if(!api.visible) return
        drawableList.sortByDescending { it.z }
        projection.apply()
        if (lightsEnabled) {
            deferredLightBatch.applyLights(lightList)
            gpuCalls = deferredLightBatch.render(drawableList, projection.camera.combined)
            projection.apply()
            deferredLightBatch.flush()
        } else {
            gpuCalls = standardBatch.render(drawableList, projection.camera.combined)
        }
        drawableList.clear()
        lightList.clear()
    }

    fun resize(width: Int, height: Int) = projection.resize(width, height)

    fun updateViewport(width: Float, height: Float, scale: Config.Graphics.Scale) {
        projection.update(width, height, scale)
    }

    override fun dispose() {
        standardBatch.dispose()
    }
}