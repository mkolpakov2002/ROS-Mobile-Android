package ru.hse.miem.ros.data.model.general

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import ru.hse.miem.ros.data.model.entities.WidgetStorageData
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 23.09.20
 */
class GsonWidgetParser {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(BaseEntity::class.java, WidgetSerializationAdapter())
        .setPrettyPrinting()
        .create()

    fun convert(storageDataList: List<WidgetStorageData>): List<BaseEntity> {
        return storageDataList.mapNotNull { convert(it) }
    }

    fun convert(storageData: WidgetStorageData): BaseEntity? {
        return try {
            val type: Class<*>? = storageData.typeName?.let { Class.forName(it) }
            val widget = gson.fromJson<BaseEntity>(storageData.data, type)
            widget.id = storageData.id
            widget
        } catch (e: ClassNotFoundException) {
            Log.e(
                TAG, String.format(
                    "Conversion error. Class for %s can not be found",
                    storageData.typeName
                )
            )
            null
        }
    }

    fun convert(widget: BaseEntity): WidgetStorageData {
        return WidgetStorageData().apply {
            id = widget.id
            name = widget.name
            typeName = widget.javaClass.name
            configId = widget.configId
            data = gson.toJson(widget)
        }
    }

    companion object {
        private val TAG = GsonWidgetParser::class.java.simpleName
        var instance: GsonWidgetParser = GsonWidgetParser()
    }
}