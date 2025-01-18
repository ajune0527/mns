package me.bytebeats.mns.ui;

import com.google.gson.Gson;
import me.bytebeats.mns.meta.FundFirm;
import me.bytebeats.mns.meta.SearchStockResponse;
import me.bytebeats.mns.meta.Stock;
import me.bytebeats.mns.network.HttpClientPool;
import me.bytebeats.mns.tool.NotificationUtil;
import me.bytebeats.mns.tool.StringResUtils;
import me.bytebeats.mns.ui.swing.JHintTextField;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

public class StockSearchDialog extends JDialog {
    private JPanel contentPane;
    private JButton add;
    private JButton cancel;
    private JPanel content_panel;
    private JLabel stock_brief_info;
    private JTable table;
    private JScrollPane scroll_panel;
    private JTextField search_keyword;

    private static final String[] FUND_HEADERS = {"代码", "名称", "类型"};

    private final List<FundFirm> firms = new ArrayList<>();
    private final List<Stock> briefs = new ArrayList<>();

    private boolean filtered = false;
    private OnFundChangeListener callback;

    private final List<FundFirm> filteredFirms = new ArrayList<>();
    private final List<Stock> filteredBriefs = new ArrayList<>();

    public StockSearchDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(add);

        add.addActionListener(e -> onAdd());
        cancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        search_keyword.registerKeyboardAction(e -> search(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        // 设置文本变化时自动查询
        setupKeywordFieldListener();
    }


    private void onAdd() {
        String[] data = stock_brief_info.getText().split(" ");
        String code = data[0];
        String market = data[data.length - 1];
        if (AppSettingState.getInstance().addStockSymbol(code, market)) {
            stock_brief_info.setText("success");
            if (callback != null) {
                callback.onChange();
            }
            NotificationUtil.info("Succeeded in adding " + code);
        } else {
            NotificationUtil.info("You already added " + code);
        }
    }

    private void onCancel() {
        dispose();
    }

    private void search() {
        this.filtered = !search_keyword.getText().trim().isEmpty();
        if (search_keyword.getText().trim().isEmpty()) {
            return;
        }
        try {
            String entity = HttpClientPool.getInstance().get(appendParams(search_keyword.getText().trim()));
            parseBriefs(entity);
            updateViews();
        } catch (Exception e) {
            NotificationUtil.info(e.getMessage());
            firms.clear();
            briefs.clear();
            updateViews();
        }
    }

    private void updateViews() {
        SwingUtilities.invokeLater(() -> {
            DefaultTableModel model;
            model = new DefaultTableModel(convertBriefs2Data(), FUND_HEADERS);
            table.setModel(model);
            ListSelectionModel selectionModel = table.getSelectionModel();
            selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            selectionModel.addListSelectionListener(e -> {
                int selectedRowIdx = table.getSelectedRow();
                if (selectedRowIdx > -1) {
                    Stock brief = filteredBriefs.get(selectedRowIdx);
                    stock_brief_info.setText(String.format("%s %s %s", brief.getSymbol(), brief.getName(), brief.getMarketName()));
                }
            });
        });

    }

    private void createUIComponents() {
        search_keyword = new JHintTextField("e.g.: 贵州茅台");
    }

    private Object[][] convertBriefs2Data() {
        Object[][] data = new Object[filteredBriefs.size()][FUND_HEADERS.length];
        for (int i = 0; i < filteredBriefs.size(); i++) {
            Stock brief = filteredBriefs.get(i);
            data[i] = new Object[]{brief.getSymbol(), brief.getName(), brief.getMarketName()};
        }
        return data;
    }

    private void parseBriefs(String jsonp) {
        // 创建Gson实例
        Gson gson = new Gson();
        // 解析JSON字符串
        SearchStockResponse response = gson.fromJson(jsonp, SearchStockResponse.class);
        // 获取Data列表
        List<Map<String, String>> data = response.getQuotationCodeTable().getData();
        Set<String> skip = new HashSet<>();
        skip.add("1");
        skip.add("2");
        skip.add("5");
        skip.add("7");
        briefs.clear();
        // 遍历Data列表
        for (Map<String, String> item : data) {
            if (!skip.contains(item.get("MarketType"))) {
                continue;
            }
            Stock s = new Stock();
            s.setSymbol(item.get("Code"));
            s.setName(item.get("Name"));
            s.setMarketName(item.get("SecurityTypeName"));
            updateBriefs(s);
        }
        filteredBriefs.clear();
        filteredBriefs.addAll(briefs);
    }

    private void updateBriefs(Stock brief) {
        int idx = briefs.indexOf(brief);
        if (idx > -1) {
            briefs.set(idx, brief);
        } else {
            briefs.add(brief);
        }
    }

    public OnFundChangeListener getCallback() {
        return callback;
    }

    public void setCallback(OnFundChangeListener callback) {
        this.callback = callback;
    }

    public interface OnFundChangeListener {
        void onChange();
    }

    private Timer searchTimer;

    private void setupKeywordFieldListener() {
        searchTimer = new Timer(500, e -> search());  // 500毫秒后执行搜索
        searchTimer.setRepeats(false);  // 确保计时器只执行一次

        search_keyword.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                restartTimer();
            }

            public void removeUpdate(DocumentEvent e) {
                restartTimer();
            }

            public void changedUpdate(DocumentEvent e) {
                // 此方法不用于纯文本文档的修改
            }

            private void restartTimer() {
                if (searchTimer.isRunning()) {
                    searchTimer.restart();
                } else {
                    searchTimer.start();
                }
            }
        });
    }

    public String appendParams(String params) {
        return StringResUtils.URL_SEARCH_STOCK + "?type=14&count=30&input=" + params;
    }
}
