package me.bytebeats.mns.handler;

import me.bytebeats.mns.UISettingProvider;
import me.bytebeats.mns.listener.OnItemClick;
import me.bytebeats.mns.listener.OnItemDoubleClick;
import me.bytebeats.mns.listener.OnItemRightClickListener;
import me.bytebeats.mns.tool.StringResUtils;
import me.bytebeats.mns.ui.AppSettingState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.*;

public abstract class AbstractHandler implements UISettingProvider {
    protected Timer timer = null;
    protected final JTable jTable;
    private final JLabel jLabel;

    private final int[] numColumnIdx = {2, 3, 4};//哪些列需要修改字体颜色, 当UI设置发生改变的时候.
    protected final Map<Integer, Double> columnTextColors = new HashMap<>();

    protected long frequency = 0L;

    protected OnItemClick<String> onItemClickListener;
    protected OnItemDoubleClick<String> onItemDoubleClickListener;
    protected OnItemRightClickListener<String> onItemRightClickListener;
    List<Integer> selectedIndices = new ArrayList<>();

    public AbstractHandler(JTable table, JLabel label) {
        this.jTable = table;
        this.jLabel = label;
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        FontMetrics metrics = jTable.getFontMetrics(jTable.getFont());
        jTable.setRowHeight(Math.max(jTable.getRowHeight(), metrics.getHeight()));
    }

    protected void updateView() {
        SwingUtilities.invokeLater(() -> {
            restoreTabSizes();
            DefaultTableModel model = new DefaultTableModel(convert2Data(), getColumnNames());
            jTable.setModel(model);
            // 恢复列的显示状态
            restoreColumnVisibility();
            resetTabSize();
            updateRowTextColors();
            updateTimestamp();
        });
    }

    public void updateSelectedIndices(List<Integer> newSelectedIndices) {
        this.selectedIndices = newSelectedIndices;
        updateView(); // 重新加载数据
    }

    public void updateSymbols(List<String> symbols) {
    }

    private void restoreColumnVisibility() {
        if (jTable == null || jTable.getColumnModel() == null) {
            return; // 如果 jTable 未初始化或没有保存的列显示状态，直接返回
        }

        if (this.selectedIndices.isEmpty()) {
            return;
        }

        int columnCount = jTable.getColumnCount();
        for (int i = 0; i < columnCount; i++) {
            if (this.selectedIndices.contains(i)) {
                jTable.getColumnModel().getColumn(i).setPreferredWidth(100);
                jTable.getColumnModel().getColumn(i).setMinWidth(100);
                jTable.getColumnModel().getColumn(i).setWidth(100);
                jTable.getColumnModel().getColumn(i).setMaxWidth(1000);
            } else {
                jTable.getColumnModel().getColumn(i).setPreferredWidth(0);
                jTable.getColumnModel().getColumn(i).setMinWidth(0);
                jTable.getColumnModel().getColumn(i).setWidth(0);
                jTable.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
    }


    public abstract void load(List<String> symbols);

    public abstract void updateFrequency();

    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    protected abstract String getTipText();

    public abstract Object[][] convert2Data();

    public abstract String[] getColumnNames();

    public abstract void restoreTabSizes();

    public abstract void resetTabSize();

    protected void updateRowTextColors() {
        for (int idx : numColumnIdx) {
            jTable.getColumn(jTable.getColumnName(idx)).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    setForeground(getTextColor(columnTextColors.getOrDefault(row, 0.0)));
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            });
        }
    }

    protected void updateTimestamp() {
        jLabel.setText(String.format(StringResUtils.REFRESH_TIMESTAMP, LocalDateTime.now().format(DateTimeFormatter.ofPattern(StringResUtils.TIMESTAMP_FORMATTER))));
//        jLabel.setForeground(JBColor.RED);
    }

    protected String[] handleColumnNames(String[] columnNames) {
        return columnNames.clone();
    }

    @Override
    public boolean isInHiddenSymbolMode() {
        return AppSettingState.getInstance().isHiddenSymbol;
    }

    @Override
    public boolean isRedRise() {
        return AppSettingState.getInstance().isRedRise;
    }

    public OnItemClick<String> getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClick<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemDoubleClick<String> getOnItemDoubleClickListener() {
        return onItemDoubleClickListener;
    }

    public void setOnItemDoubleClickListener(OnItemDoubleClick<String> onItemDoubleClickListener) {
        this.onItemDoubleClickListener = onItemDoubleClickListener;
    }

    public OnItemRightClickListener<String> getOnItemRightClickListener() {
        return onItemRightClickListener;
    }

    public void setOnItemRightClickListener(OnItemRightClickListener<String> onItemRightClickListener) {
        this.onItemRightClickListener = onItemRightClickListener;
    }
}
