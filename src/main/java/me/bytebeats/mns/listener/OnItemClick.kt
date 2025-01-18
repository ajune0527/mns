package me.bytebeats.mns.listener

interface OnItemClick<T> {
    fun onItemClick(t: T, xOnScreen: Int, yOnScreen: Int)
}