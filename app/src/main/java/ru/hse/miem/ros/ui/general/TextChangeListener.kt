package ru.hse.miem.ros.ui.general

import android.text.Editable
import android.text.TextWatcher

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 19.01.20
 * @updated on 19.01.20
 * @modified by
 */
abstract class TextChangeListener<T>(private val target: T) : TextWatcher {
    public override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        return
        // System.out.println("Before Text Changed: " + s);
    }

    public override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        return
        // System.out.println("On Text Changed: " + s);
    }

    public override fun afterTextChanged(s: Editable) {
        // System.out.println("After Text Changed: " + s.toString());
        this.onTextChanged(target, s)
    }

    abstract fun onTextChanged(target: T, s: Editable?)
}