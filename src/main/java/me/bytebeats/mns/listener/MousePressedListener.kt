package me.bytebeats.mns.listener

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

abstract class MousePressedListener : MouseListener {
    override fun mouseClicked(e: MouseEvent?) {}

    override fun mouseReleased(e: MouseEvent?) {}

    override fun mouseEntered(e: MouseEvent?) {}

    override fun mouseExited(e: MouseEvent?) {}
}