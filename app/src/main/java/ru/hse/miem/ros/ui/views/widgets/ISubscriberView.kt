package ru.hse.miem.ros.ui.views.widgets

import org.ros.internal.message.Message

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
interface ISubscriberView {
    fun onNewMessage(message: Message)
}
