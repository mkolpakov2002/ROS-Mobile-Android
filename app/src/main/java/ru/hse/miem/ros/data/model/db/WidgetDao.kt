package ru.hse.miem.ros.data.model.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.Dao
import androidx.room.Query
import ru.hse.miem.ros.data.model.entities.WidgetStorageData
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.general.GsonWidgetParser

@Dao
abstract class WidgetDao : BaseDao<WidgetStorageData> {
    //TODO: Update test to real classes
    @Query("SELECT * FROM widget_table WHERE widget_config_id = :configId")
    protected abstract fun getWidgetsFor(configId: Long): LiveData<List<WidgetStorageData>>
    @Query("DELETE FROM widget_table WHERE id = :id")
    abstract suspend fun deleteById(id: Long): Int
    @Query("DELETE FROM widget_table WHERE widget_config_id = :id")
    abstract suspend fun deleteWithConfigId(id: Long): Int
    @Query("DELETE FROM widget_table")
    abstract suspend fun deleteAll()
    @Query("SELECT EXISTS (SELECT 1 FROM widget_table WHERE widget_config_id = :configId AND name = :name)")
    abstract suspend fun exists(configId: Long, name: String): Boolean
    @Query("SELECT * FROM widget_table WHERE widget_config_id = :configId AND id = :widgetId")
    abstract fun getWidgetIntern(configId: Long, widgetId: Long): LiveData<WidgetStorageData>
    fun getWidget(configId: Long, widgetId: Long): LiveData<BaseEntity> {
        val widget = MediatorLiveData<BaseEntity>()
        widget.addSource(getWidgetIntern(configId, widgetId)) { data: WidgetStorageData ->
            widget.postValue(GsonWidgetParser.instance.convert(data))
        }
        return widget
    }

    fun getWidgets(configId: Long): LiveData<List<BaseEntity>> {
        val widgetList = MediatorLiveData<List<BaseEntity>>()
        widgetList.addSource(getWidgetsFor(configId)) { widgetEntities: List<WidgetStorageData> ->
            widgetList.postValue(
                GsonWidgetParser.instance.convert(widgetEntities)
            )
        }
        return widgetList
    }

    suspend fun insert(widget: BaseEntity) {
        val storageData: WidgetStorageData =
            GsonWidgetParser.instance.convert(widget)
        this.insert(storageData)
    }

    suspend fun update(widget: BaseEntity) {
        val storageData: WidgetStorageData =
            GsonWidgetParser.instance.convert(widget)
        this.update(storageData)
    }

    suspend fun delete(widget: BaseEntity): Int {
        return deleteById(widget.id)
    }

    companion object {
        var TAG = WidgetDao::class.java.simpleName
    }
}
