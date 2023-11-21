package ru.hse.miem.ros.ui.general

import android.graphics.Matrix
import android.view.MotionEvent

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 11.09.20
 * @updated on
 * @modified by
 */
class MatrixGestureDetector(
    private val mMatrix: Matrix,
    private val mListener: OnMatrixChangeListener?
) {
    private val mTempMatrix: Matrix = Matrix()
    private val mSrc: FloatArray = FloatArray(4)
    private val mDst: FloatArray = FloatArray(4)
    private var ptpIdx: Int = 0
    private var mCount: Int = 0
    fun onTouchEvent(event: MotionEvent) {
        if (event.pointerCount > 2) {
            return
        }
        val action: Int = event.actionMasked
        val index: Int = event.actionIndex
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val idx: Int = index * 2
                mSrc[idx] = event.getX(index)
                mSrc[idx + 1] = event.getY(index)
                mCount++
                ptpIdx = 0
            }

            MotionEvent.ACTION_MOVE -> {
                var i: Int = 0
                while (i < mCount) {
                    val idx: Int = ptpIdx + i * 2
                    mDst[idx] = event.getX(i)
                    mDst[idx + 1] = event.getY(i)
                    i++
                }
                mTempMatrix.setPolyToPoly(mSrc, ptpIdx, mDst, ptpIdx, mCount)
                mMatrix.postConcat(mTempMatrix)
                mListener?.onChange(mMatrix)
                System.arraycopy(mDst, 0, mSrc, 0, mDst.size)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                if (event.getPointerId(index) == 0) ptpIdx = 2
                mCount--
            }
        }
    }

    interface OnMatrixChangeListener {
        fun onChange(matrix: Matrix?)
    }

    companion object {
        private val TAG: String = "MatrixGestureDetector"
    }
}