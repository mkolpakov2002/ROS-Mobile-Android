package ru.hse.miem.ros.data.model.repositories.rosRepo

import org.ros.rosjava_geometry.FrameTransformTree

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 20.05.21
 */
class TransformProvider {
    lateinit var tree: FrameTransformTree
        private set

    init {
        reset()
    }

    fun reset() {
        tree = FrameTransformTree()
    }

    companion object {
        @Synchronized
        public fun getInstance(): TransformProvider {
            if (!this::instance.isInitialized) {
                instance = TransformProvider()
            }
            return instance
        }
        private lateinit var instance: TransformProvider
    }
}
