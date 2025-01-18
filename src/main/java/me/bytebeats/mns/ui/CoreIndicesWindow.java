package me.bytebeats.mns.ui;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.ui.awt.RelativePoint;
import me.bytebeats.mns.OnSymbolSelectedListener;
import me.bytebeats.mns.SymbolParser;
import me.bytebeats.mns.enumation.StockChartType;
import me.bytebeats.mns.handler.TencentIndexHandler;
import me.bytebeats.mns.listener.OnItemRightClickListener;
import me.bytebeats.mns.tool.PopupsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CoreIndicesWindow implements SymbolParser {
    private JPanel indices_window;
    private JScrollPane indices_scroll;
    private JTable indices_table;
    private JLabel indices_timestamp;
    private JButton indices_sync;

    private final TencentIndexHandler handler;

    public CoreIndicesWindow() {
        handler = new TencentIndexHandler(indices_table, indices_timestamp);
        handler.setOnItemDoubleClickListener((s, xOnScreen, yOnScreen) -> {
            JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<StockChartType>("操作", StockChartType.values()) {
                @Override
                public @NotNull String getTextFor(StockChartType value) {
                    return value.getDescription();
                }

                @Override
                public @Nullable PopupStep<?> onChosen(StockChartType selectedValue, boolean finalChoice) {
                    PopupsUtil.INSTANCE.popupStockChart(s, selectedValue, new Point(xOnScreen, yOnScreen));
                    // 如果是删除
                    if (selectedValue == StockChartType.Delete && finalChoice) {
                        AppSettingState.getInstance().deleteIndexSymbol(s);
                        syncRefresh();
                    }
                    return super.onChosen(selectedValue, finalChoice);
                }
            }).show(RelativePoint.fromScreen(new Point(xOnScreen, yOnScreen)));
        });
        handler.setOnItemRightClickListener(new OnItemRightClickListener<String>() {
            @Override
            public void onItemRightClick(String s, int xOnScreen, int yOnScreen) {
                JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<StockChartType>("操作", StockChartType.values()) {
                    @Override
                    public @NotNull String getTextFor(StockChartType value) {
                        return value.getDescription();
                    }

                    @Override
                    public @Nullable PopupStep<?> onChosen(StockChartType selectedValue, boolean finalChoice) {
                        PopupsUtil.INSTANCE.popupStockChart(s, selectedValue, new Point(xOnScreen, yOnScreen));
                        // 如果是删除
                        if (selectedValue == StockChartType.Delete && finalChoice) {
                            AppSettingState.getInstance().deleteIndexSymbol(s);
                            syncRefresh();
                        }
                        return super.onChosen(selectedValue, finalChoice);
                    }
                }).show(RelativePoint.fromScreen(new Point(xOnScreen, yOnScreen)));
            }
        });
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        handler.setOnSymbolSelectedListener(listener);
    }

    public JPanel getJPanel() {
        return indices_window;
    }

    public void onInit() {
        indices_sync.addActionListener(e -> {
            syncRefresh();
        });
        syncRefresh();
    }

    private void syncRefresh() {
        handler.load(new ArrayList<>());
    }

    @Override
    public String prefix() {
        return "s_"; // 简要信息
    }

    @Override
    public String raw() {
        return AppSettingState.getInstance().allIndices;
    }

    @Override
    public List<String> parse() {
        return new ArrayList<>();
    }
}