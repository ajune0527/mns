package me.bytebeats.mns.listener

import java.awt.event.WindowEvent
import java.awt.event.WindowListener

abstract class WindowSwitchListener : WindowListener {
    override fun windowClosing(e: WindowEvent?) {}

    override fun windowIconified(e: WindowEvent?) {}

    override fun windowDeiconified(e: WindowEvent?) {}

    override fun windowActivated(e: WindowEvent?) {}

    override fun windowDeactivated(e: WindowEvent?) {}
}