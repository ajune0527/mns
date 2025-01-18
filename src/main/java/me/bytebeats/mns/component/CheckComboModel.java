package me.bytebeats.mns.component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CheckComboModel extends DefaultComboBoxModel<CheckComboStore> implements ActionListener {

    public CheckComboModel(List<String> items) {
        for (String item : items) {
            this.addElement(new CheckComboStore(item, true)); // 默认选中
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox<CheckComboStore> combo = (JComboBox<CheckComboStore>) e.getSource();
        CheckComboStore store = (CheckComboStore) combo.getSelectedItem();
        if (store != null) {
            store.setSelected(!store.isSelected());
            combo.repaint(); // 刷新组合框显示状态
        }
    }

    public List<String> getSelectedItems() {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < this.getSize(); i++) {
            CheckComboStore store = this.getElementAt(i);
            if (store.isSelected()) selected.add(store.getId());
        }
        return selected;
    }

    public List<Integer> getSelectedIndices() {
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < this.getSize(); i++) {
            CheckComboStore store = this.getElementAt(i);
            if (store.isSelected()) {
                selectedIndices.add(i);
            }
        }
        return selectedIndices;
    }

}