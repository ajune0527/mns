package me.bytebeats.mns.component;

public class CheckComboStore {
    private String id;
    private boolean isSelected;

    public CheckComboStore(String id, boolean isSelected) {
        this.id = id;
        this.isSelected = isSelected;
    }

    // 获取id
    public String getId() {
        return id;
    }

    // 设置id
    public void setId(String id) {
        this.id = id;
    }

    // 获取isSelected
    public boolean isSelected() {
        return isSelected;
    }

    // 设置isSelected
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}