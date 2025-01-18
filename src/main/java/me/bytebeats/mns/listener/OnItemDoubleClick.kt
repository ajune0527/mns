package me.bytebeats.mns.listener

interface OnItemDoubleClick<T> {
    fun onItemDoubleClick(t: T, xOnScreen: Int, yOnScreen: Int)
}