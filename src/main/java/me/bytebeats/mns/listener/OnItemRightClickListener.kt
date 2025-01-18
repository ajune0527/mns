package me.bytebeats.mns.listener

interface OnItemRightClickListener<T> {
    fun onItemRightClick(t: T, xOnScreen: Int, yOnScreen: Int)
}