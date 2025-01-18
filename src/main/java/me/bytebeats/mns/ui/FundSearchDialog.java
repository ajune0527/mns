package me.bytebeats.mns.ui;

import com.google.gson.reflect.TypeToken;
import me.bytebeats.mns.meta.FundBrief;
import me.bytebeats.mns.meta.FundFirm;
import me.bytebeats.mns.meta.FundFirmOp;
import me.bytebeats.mns.network.HttpClientPool;
import me.bytebeats.mns.tool.GsonUtils;
import me.bytebeats.mns.tool.NotificationUtil;
import me.bytebeats.mns.tool.StringResUtils;
import me.bytebeats.mns.ui.swing.JHintTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class FundSearchDialog extends JDialog {
    private JPanel contentPane;
    private JButton fund_add;
    private JButton fund_cancel;
    private JPanel fund_content_panel;
    private JLabel fund_brief_info;
    private JComboBox<String> fund_type_cb;
    private JTable fund_table;
    private JScrollPane fund_scroll_panel;
    private JTextField fund_keyword;
    private JButton fund_delete;

    private static final String[] FIRM_HEADERS = {"代码", "名称"};
    private static final String[] FUND_HEADERS = {"代码", "名称", "类型"};

    private final List<FundFirm> firms = new ArrayList<>();
    private final List<FundBrief> briefs = new ArrayList<>();

    private boolean filtered = false;
    private String keyword = "";
    private OnFundChangeListener callback;

    private final List<FundFirm> filteredFirms = new ArrayList<>();
    private final List<FundBrief> filteredBriefs = new ArrayList<>();

    public FundSearchDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(fund_add);

        fund_add.addActionListener(e -> onAdd());

        fund_delete.addActionListener(e -> onDelete());

        fund_cancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        fund_keyword.registerKeyboardAction(e -> search(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        fund_type_cb.addItem(StringResUtils.FUND_SEARCH_NAME);
        fund_type_cb.addItem(StringResUtils.FUND_SEARCH_FIRM);
        fund_type_cb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean enabled = fund_type_cb.getSelectedIndex() == 0;
                fund_add.setEnabled(enabled);
                fund_delete.setEnabled(enabled);
                fund_brief_info.setText("--- ---");
            }
        });

        // 设置文本变化时自动查询
        setupKeywordFieldListener();
    }


    private void onAdd() {
        String code = fund_brief_info.getText().split(" ")[0];
        if (AppSettingState.getInstance().addFundSymbol(code)) {
            if (callback != null) {
                callback.onChange();
            }
            NotificationUtil.info("Succeeded in adding " + code);
        } else {
            NotificationUtil.info("You already added " + code);
        }
    }

    private void onDelete() {
        String code = fund_brief_info.getText().split(" ")[0];
        if (AppSettingState.getInstance().deleteFundSymbol(code)) {
            if (callback != null) {
                callback.onChange();
            }
            NotificationUtil.info("Succeeded in deleting " + code);
        } else {
            NotificationUtil.info(code + " is not in your fund list");
        }
    }

    private void onCancel() {
        dispose();
    }

    private void search() {
        this.keyword = fund_keyword.getText().trim();
        this.filtered = !keyword.isEmpty();
        try {
            if (fund_type_cb.getSelectedIndex() == 0) {//search fund briefs
                String entity = HttpClientPool.getInstance().get(StringResUtils.URL_SEARCH_FUND);
                parseBriefs(entity);
                updateViews();
            } else {//search fund firms
                String entity = HttpClientPool.getInstance().get(StringResUtils.URL_SEARCH_FUND_FIRM);
                parseFirms(entity);
                updateViews();
            }
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
            if (fund_type_cb.getSelectedIndex() == 0) {//search fund briefs
                model = new DefaultTableModel(convertBriefs2Data(), FUND_HEADERS);
            } else {
                model = new DefaultTableModel(convertFirms2Data(), FIRM_HEADERS);
            }
            fund_table.setModel(model);
            ListSelectionModel selectionModel = fund_table.getSelectionModel();
            selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            selectionModel.addListSelectionListener(e -> {
                int selectedRowIdx = fund_table.getSelectedRow();
                if (selectedRowIdx > -1) {
                    if (fund_type_cb.getSelectedIndex() == 0) {//search fund briefs
                        FundBrief brief = filteredBriefs.get(selectedRowIdx);
                        fund_brief_info.setText(String.format("%s %s %s", brief.getCode(), brief.getName(), brief.getType()));
                    } else {
                        FundFirm firm = filteredFirms.get(selectedRowIdx);
                        fund_brief_info.setText(String.format("%s %s", firm.getCode(), firm.getName()));
                    }
                }
            });
        });

    }

    private Object[][] convertFirms2Data() {
        Object[][] data = new Object[filteredFirms.size()][FIRM_HEADERS.length];
        for (int i = 0; i < filteredFirms.size(); i++) {
            FundFirm firm = filteredFirms.get(i);
            data[i] = new Object[]{firm.getCode(), firm.getName()};
        }
        return data;
    }

    private Object[][] convertBriefs2Data() {
        Object[][] data = new Object[filteredBriefs.size()][FUND_HEADERS.length];
        for (int i = 0; i < filteredBriefs.size(); i++) {
            FundBrief brief = filteredBriefs.get(i);
            data[i] = new Object[]{brief.getCode(), brief.getName(), brief.getType()};
        }
        return data;
    }

    private void parseFirms(String jsonp) {
        String json = jsonp.substring(jsonp.indexOf("=") + 1);
        FundFirmOp op = GsonUtils.fromJson(json, FundFirmOp.class);
        if (op != null && op.getOp() != null) {
            for (List<String> sts : op.getOp()) {
                updateFirms(new FundFirm(sts.get(0), sts.get(1)));
            }
        }
        filteredFirms.clear();
        for (FundFirm firm : firms) {
            if (filtered) {
                if (firm.contains(keyword)) {
                    filteredFirms.add(firm);
                }
            } else {
                filteredFirms.add(firm);
            }
        }
    }

    private void updateFirms(FundFirm firm) {
        int idx = firms.indexOf(firm);
        if (idx > -1) {
            firms.set(idx, firm);
        } else {
            firms.add(firm);
        }
    }

    private void parseBriefs(String jsonp) {
        String json = jsonp.substring(jsonp.indexOf("["), jsonp.length() - 1);
        List<List<String>> op = GsonUtils.fromJson(json, new TypeToken<List<List<String>>>() {
        }.getType());
        if (op != null && !op.isEmpty()) {
            for (List<String> sts : op) {
                updateBriefs(new FundBrief(sts.get(0), sts.get(2), sts.get(3)));
            }
        }
        filteredBriefs.clear();
        for (FundBrief brief : briefs) {
            if (filtered) {
                if (brief.contains(keyword)) {
                    filteredBriefs.add(brief);
                }
            } else {
                filteredBriefs.add(brief);
            }
        }
    }

    private void updateBriefs(FundBrief brief) {
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

    private void createUIComponents() {
        fund_keyword = new JHintTextField("e.g.: 半导体");
    }

    public interface OnFundChangeListener {
        void onChange();
    }

    private Timer searchTimer;

    private void setupKeywordFieldListener() {
        searchTimer = new Timer(500, e -> search());  // 500毫秒后执行搜索
        searchTimer.setRepeats(false);  // 确保计时器只执行一次

        fund_keyword.getDocument().addDocumentListener(new DocumentListener() {
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
}

