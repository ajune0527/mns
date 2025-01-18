package me.bytebeats.mns.component;

import javax.swing.*;

public class CheckComboItem {
    private JCheckBox checkBox;

    public CheckComboItem(String text) {
        checkBox = new JCheckBox(text);
        checkBox.setSelected(true);
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public String getText() {
        return checkBox.getText();
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }

    public void setSelected(boolean selected) {
        checkBox.setSelected(selected);
    }
}