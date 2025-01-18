package me.bytebeats.mns;

import com.intellij.ui.JBColor;

public interface UISettingProvider {
    boolean isInHiddenSymbolMode();

    boolean isRedRise();

    default JBColor getTextColor(double offset) {
        if (isRedRise()) {
            if (offset > 0) {
                return JBColor.RED;
            } else {
                return JBColor.GREEN;
            }
        } else {
            if (offset > 0) {
                return JBColor.GREEN;
            } else {
                return JBColor.RED;
            }
        }
    }
}
