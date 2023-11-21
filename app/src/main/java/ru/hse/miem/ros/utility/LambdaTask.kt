package ru.hse.miem.ros.utility

import android.os.AsyncTask

/**
 * TODO: Description
 * TODO: Edit to return values of called functions
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 31.01.20
 * @updated on 31.01.20
 * @modified by
 */
class LambdaTask(private var taskRunnable: TaskRunnable) : AsyncTask<Void, Void, Void>() {
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg voids: Void): Void? {
        taskRunnable.run()
        return null
    }

    interface TaskRunnable {
        fun run()
    }
}
