package ru.hse.miem.ros.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.domain.RosDomain

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1.1
 * @created on 10.01.20
 * @updated on 15.05.20
 * @modified by Maxim Kolpakov
 * @updated on 24.09.20
 * @modified by Maxim Kolpakov
 */
class DetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val rosDomain: RosDomain = RosDomain.getInstance(application)
    private var widgetsEmpty: MediatorLiveData<Boolean>? = null
    private var lastDeletedWidget: BaseEntity? = null
    val widgetPath: LiveData<MutableList<Long>> get() = selectedPath
    private val TAG: String = DetailsViewModel::class.java.simpleName
    private val selectedPath: MutableLiveData<MutableList<Long>> = MutableLiveData(mutableListOf())


    fun createWidget(selectedText: String) = viewModelScope.launch {
        rosDomain.createWidget(getParentId(0), selectedText)
    }

    fun updateWidget(widget: BaseEntity) = viewModelScope.launch {
        rosDomain.updateWidget(getParentId(1), widget)
    }

    fun deleteWidget(widget: BaseEntity) {
        lastDeletedWidget = widget
        viewModelScope.launch {
            rosDomain.deleteWidget(getParentId(0), widget)
        }
    }

    fun restoreWidget() {
        lastDeletedWidget?.let { widget ->
            viewModelScope.launch {
                rosDomain.addWidget(getParentId(0), widget)
            }
        }
    }

    private fun getParentId(branch: Int): Long? {
        var parentId: Long? = null
        val path: List<Long>? = selectedPath.getValue()
        if (path!!.size > branch) {
            parentId = path[0]
        }
        return parentId
    }


    val currentWidgets: LiveData<List<BaseEntity>> get() = rosDomain.currentWidgets

    fun widgetsEmpty(): LiveData<Boolean?> {
        return widgetsEmpty ?: MediatorLiveData<Boolean>().also {
            it.addSource(currentWidgets) { widgets -> it.postValue(widgets.isEmpty()) }
            widgetsEmpty = it
        }
    }

    val topicList: List<Topic> get() = rosDomain.topicList

    val widget: LiveData<BaseEntity> get() =
        selectedPath.value?.let {
            rosDomain.findWidget(it[0])
        }!!

    fun select(widgetId: Long?) {
        lastDeletedWidget = null
        selectedPath.value = widgetId?.let {
            selectedPath.value?.apply { add(it) }
        } ?: mutableListOf()
    }

    fun popPath(steps: Int) {
        repeat(steps) {
            selectedPath.value?.run {
                if (isNotEmpty()) removeAt(lastIndex)
            }
        }
    }

}