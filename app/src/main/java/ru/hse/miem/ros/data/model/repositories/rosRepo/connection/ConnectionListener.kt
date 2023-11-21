package ru.hse.miem.ros.data.model.repositories.rosRepo.connection

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 16.04.20
 * @updated on 16.04.20
 * @modified by
 */
interface ConnectionListener {
    fun onSuccess()
    fun onFailed()
}