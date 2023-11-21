/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ru.hse.miem.ros.ui.opengl.visualisation

import android.content.Context
import android.graphics.PixelFormat
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.common.collect.Lists
import org.ros.internal.message.Message
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.ui.opengl.layer.CameraControl
import ru.hse.miem.ros.ui.views.widgets.IPublisherView
import ru.hse.miem.ros.ui.views.widgets.ISubscriberView
import ru.hse.miem.ros.ui.views.widgets.LayerView
import java.util.Collections

/**
 * @author damonkohler@google.com (Damon Kohler)
 * @author moesenle@google.com (Lorenz Moesenlechner)
 * @version 1.0.0
 * @updated on 08.03.2021
 * @modified by Maxim Kolpakov
 */
class VisualizationView : GLSurfaceView {
    lateinit var camera: XYOrthographicCamera
        private set
    private lateinit var cameraControl: CameraControl
    private lateinit var layers: MutableList<LayerView>
    private var renderer: XYOrthographicRenderer? = null
        private set

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        layers = ArrayList()
        renderer = XYOrthographicRenderer(this)
        debugFlags = DEBUG_CHECK_GL_ERROR
        setZOrderMediaOverlay(true)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY

        //frameTransformTree = new FrameTransformTree();
        camera = XYOrthographicCamera()

        //camera = new XYOrthographicCamera(frameTransformTree);
        cameraControl = CameraControl(this)
        cameraControl.init(true, true, true)
        camera.jumpToFrame("map")
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        for (layer: LayerView in Lists.reverse(layers)) {
            if (layer.onTouchEvent(this, event)) {
                requestRender()
                return true
            }
        }
        if (cameraControl.onTouchEvent(event)) {
            requestRender()
            return true
        }
        return super.onTouchEvent(event)
    }

    fun getLayers(): List<LayerView> {
        return Collections.unmodifiableList(layers)
    }

    fun addLayer(layer: LayerView) {
        layer.parentView = (this)
        layers.add(layer)
        if (layer is IPublisherView) {
            layer.frame = camera.frame
        }
    }

    fun onNewData(data: RosData) {
        val message: Message = data.message
        val topic: Topic = data.topic
        var dirtyView = false

        // Forward message to sub layers
        for (layer: LayerView in getLayers()) {
            if (layer is ISubscriberView) {
                if ((((layer.widgetEntity?.topic ?: false) == topic))) {
                    (layer as ISubscriberView).onNewMessage(message)
                    dirtyView = true
                }
            }
        }
        if (dirtyView) {
            requestRender()
        }
    }

    companion object {
        var TAG: String = VisualizationView::class.java.simpleName
    }
}