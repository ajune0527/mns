package me.bytebeats.mns.ui;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.ui.awt.RelativePoint;
import me.bytebeats.mns.OnSymbolSelectedListener;
import me.bytebeats.mns.component.CheckComboModel;
import me.bytebeats.mns.component.CheckComboRenderer;
import me.bytebeats.mns.component.CheckComboStore;
import me.bytebeats.mns.enumation.StockChartType;
import me.bytebeats.mns.handler.AbsStockHandler;
import me.bytebeats.mns.handler.TencentStockHandler;
import me.bytebeats.mns.listener.OnItemRightClickListener;
import me.bytebeats.mns.listener.WindowSwitchListener;
import me.bytebeats.mns.tool.PopupsUtil;
import me.bytebeats.mns.tool.StringResUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StockWindow {
    private JPanel stock_window;
    private JScrollPane stock_scroll;
    private JTable stock_table;
    private JLabel stock_timestamp;
    private JButton stock_refresh;
    private JComboBox<String> stock_market_list;
    private JPanel select_cols_panel;
    private JButton stock_search;
    private StockSearchDialog stockSearchDialog;

    private final AbsStockHandler handler;

    private static final String[] markets = {StringResUtils.STOCK_ALL, StringResUtils.STOCK_US, StringResUtils.STOCK_HK, StringResUtils.STOCK_CN};

    private static final String[] stockColumnNames = {StringResUtils.STOCK_NAME, StringResUtils.SYMBOL, StringResUtils.STOCK_LATEST_PRICE, StringResUtils.RISE_AND_FALL, StringResUtils.RISE_AND_FALL_RATIO};
    List<Integer> selectedIndices = new ArrayList<>();

    public StockWindow() {
        handler = new TencentStockHandler(stock_table, stock_timestamp) {
            @Override
            protected String getTipText() {
                return Objects.requireNonNull(stock_market_list.getSelectedItem()).toString();
            }
        };
        handler.setOnItemDoubleClickListener((s, xOnScreen, yOnScreen) -> {
            JBPopupFactory.getInstance().createListPopup(new BaseListPopupStep<StockChartType>("操作", StockChartType.values()) {
                @Override
                public @NotNull String getTextFor(StockChartType value) {
                    return value.getDescription();
                }

                @Override
                public @Nullable PopupStep<?> onChosen(StockChartType selectedValue, boolean finalChoice) {
                    PopupsUtil.INSTANCE.popupStockChart(s, selectedValue, new Point(xOnScreen, yOnScreen));
                    operation(s, selectedValue, finalChoice);
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
                        operation(s, selectedValue, finalChoice);
                        return super.onChosen(selectedValue, finalChoice);
                    }
                }).show(RelativePoint.fromScreen(new Point(xOnScreen, yOnScreen)));
            }
        });
    }


    public void operation(String symbol, StockChartType selectedValue, boolean finalChoice) {
        if (selectedValue == StockChartType.Delete && finalChoice) {
            AppSettingState.getInstance().deleteStockSymbol(symbol);
            syncRefresh();
        }

        if ((selectedValue == StockChartType.Up || selectedValue == StockChartType.Down) && finalChoice) {
            AppSettingState.getInstance().moveStockSymbol(symbol, selectedValue);
            syncRefresh();
        }
    }

    public void setOnSymbolSelectedListener(OnSymbolSelectedListener listener) {
        handler.setOnSymbolSelectedListener(listener);
    }

    public JPanel getJPanel() {
        return stock_window;
    }

    public void onInit() {
        stock_market_list.removeAllItems();
        for (String market : markets) {
            stock_market_list.addItem(market);
        }

        JComboBox<CheckComboStore> box = getCheckComboStoreJComboBox();
        box.putClientProperty("stockWindow", this); // 设置 ClientProperty
        select_cols_panel.setLayout(new BoxLayout(select_cols_panel, BoxLayout.Y_AXIS));
        select_cols_panel.add(box);

        stock_market_list.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                handler.stop();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                syncRefresh();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        // 添加 ActionListener 到 stock_market_list
        stock_market_list.addActionListener(e -> {
            String selectedMarket = (String) stock_market_list.getSelectedItem();
            AppSettingState.getInstance().setCurrentMarketSymbol(selectedMarket);
        });

        stock_refresh.addActionListener(e -> {
            handler.stop();
            syncRefresh();
            updateTableColumns();
        });
        stock_search.addActionListener(e -> popSearchDialog());
        syncRefresh();
        updateTableColumns();
    }


    private @NotNull JComboBox<CheckComboStore> getCheckComboStoreJComboBox() {
        CheckComboModel model = new CheckComboModel(Arrays.asList(stockColumnNames));
        JComboBox<CheckComboStore> box = new JComboBox<>(model);
        box.setRenderer(new CheckComboRenderer());
        box.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CheckComboStore store = (CheckComboStore) e.getItem();
                store.setSelected(!store.isSelected());
                updateTableColumns(); // 更新 JTable 的列显示状态
            }
        });
        return box;
    }

    public void updateTableColumns() {
        // 确保 stock_table 已经被初始化
        if (stock_table == null || stock_table.getColumnModel() == null) {
            return; // 如果 stock_table 未初始化，直接返回
        }

        JComboBox<CheckComboStore> box = (JComboBox<CheckComboStore>) select_cols_panel.getComponent(0);
        CheckComboModel model = (CheckComboModel) box.getModel();
        this.selectedIndices = model.getSelectedIndices();

        // 获取列数
        int columnCount = stock_table.getColumnCount();
        handler.updateSelectedIndices(selectedIndices);
        for (int i = 0; i < columnCount; i++) {
            if (selectedIndices.contains(i)) {
                stock_table.getColumnModel().getColumn(i).setPreferredWidth(100);
                stock_table.getColumnModel().getColumn(i).setMinWidth(100);
                stock_table.getColumnModel().getColumn(i).setWidth(100);
                stock_table.getColumnModel().getColumn(i).setMaxWidth(1000);
                continue;
            }
            stock_table.getColumnModel().getColumn(i).setPreferredWidth(0);
            stock_table.getColumnModel().getColumn(i).setMinWidth(0);
            stock_table.getColumnModel().getColumn(i).setWidth(0);
            stock_table.getColumnModel().getColumn(i).setMaxWidth(0);
        }
    }

    private void syncRefresh() {
        handler.load(new ArrayList<>());
        updateTableColumns();
    }


    private void popSearchDialog() {
        if (stockSearchDialog == null) {
            stockSearchDialog = new StockSearchDialog();
            stockSearchDialog.setCallback(() -> {
                // do nothing here
            });
            stockSearchDialog.addWindowListener(new WindowSwitchListener() {
                @Override
                public void windowOpened(WindowEvent e) {
                    handler.stop();
                }

                @Override
                public void windowClosed(WindowEvent e) {
//                    fundSymbols = AppSettingState.getInstance().dailyFunds;
                    syncRefresh();
                }
            });
        }
        if (stockSearchDialog.isVisible()) {
            return;
        }
        stockSearchDialog.pack();
        Dimension screenerSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) (screenerSize.getWidth() / 2 - stockSearchDialog.getWidth() / 2);
        int y = (int) (screenerSize.getHeight() / 2 - stockSearchDialog.getHeight() / 2);
        stockSearchDialog.setLocation(x, y);
        stockSearchDialog.setVisible(true);
    }
}
