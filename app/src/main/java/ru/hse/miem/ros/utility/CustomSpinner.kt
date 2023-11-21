package ru.hse.miem.ros.utility

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner

/**
 * A custom spinner class
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 17.09.2020
 * @updated on 03.11.2020
 * @modified by Maxim Kolpakov
 */
class CustomSpinner : AppCompatSpinner, AdapterView.OnItemSelectedListener {
    private var mListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated: Boolean = false

    constructor(context: Context) : super((context)) {
        onItemSelectedListener = this
    }

    constructor(context: Context, attrs: AttributeSet?) : super((context), attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        (context), attrs, defStyleAttr
    )

    public override fun onItemSelected(
        parent: AdapterView<*>?,
        view: View,
        position: Int,
        id: Long
    ) {
        mListener?.onSpinnerItemSelected(this, position)
    }

    public override fun onNothingSelected(parent: AdapterView<*>?) {
        mListener?.onSpinnerItemSelected(this, null)
    }

    public override fun performClick(): Boolean {
        // register that the Spinner was opened so we have a status
        // indicator for when the container holding this Spinner may lose focus
        mOpenInitiated = true
        mListener?.onSpinnerOpened(this)
        return super.performClick()
    }

    public override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent()
        }
    }

    /**
     * Register the listener which will listen for events.
     */
    fun setSpinnerEventsListener(
        onSpinnerEventsListener: OnSpinnerEventsListener?
    ) {
        mListener = onSpinnerEventsListener
    }

    /**
     * Propagate the closed Spinner event to the listener from outside if needed.
     */
    private fun performClosedEvent() {
        mOpenInitiated = false
        mListener?.onSpinnerClosed(this)
    }

    /**
     * Perform an empty list event if there are no items available in the adapter list
     */
    fun performEmptyListEvent() {
        mOpenInitiated = false
        mListener?.onSpinnerClosed(this)
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    private fun hasBeenOpened(): Boolean {
        return mOpenInitiated
    }

    /**
     * An interface which a client of this Spinner could use to receive
     * open/closed events for this Spinner.
     */
    interface OnSpinnerEventsListener {
        /**
         * Callback triggered when the spinner was opened.
         */
        fun onSpinnerOpened(spinner: CustomSpinner?)

        /**
         * Callback triggered when an item of the spinner was selected.
         */
        fun onSpinnerItemSelected(spinner: CustomSpinner?, position: Int?)

        /**
         * Callback triggered when the spinner was closed.
         */
        fun onSpinnerClosed(spinner: CustomSpinner?)
    }
}
