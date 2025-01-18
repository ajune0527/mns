package me.bytebeats.mns.component;

import javax.swing.*;
import java.awt.*;

public class CheckComboRenderer implements ListCellRenderer<CheckComboStore> {
    @Override
    public Component getListCellRendererComponent(JList<? extends CheckComboStore> list, CheckComboStore value, int index, boolean isSelected, boolean cellHasFocus) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setText(value.getId());
        checkBox.setSelected(value.isSelected());
        if (isSelected) {
            checkBox.setBackground(list.getSelectionBackground());
            checkBox.setForeground(list.getSelectionForeground());
        } else {
            checkBox.setBackground(list.getBackground());
            checkBox.setForeground(list.getForeground());
        }
        return checkBox;
    }
}
