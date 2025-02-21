package me.bytebeats.mns.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class SettingWindow implements Configurable {
    private JPanel mns_setting;
    private JTextField us_stock_input;
    private JTextField hk_stock_input;
    private JTextField cn_stock_input;
    private JLabel us_stock;
    private JLabel hk_stock;
    private JLabel cn_stock;
    private JRadioButton red_rise_green_fall;
    private JRadioButton red_fall_green_rise;
    private JPanel mkt_setting_radio;
    private JLabel idx_label;
    private JTextField idx_input;
    private JLabel daily_fund;
    private JTextField daily_fund_input;
    private JLabel mkt_setting_label;
    private JTextField crypto_currency_input;
    private JLabel crypto_currency;
    private JLabel refresh_frequency_label;
    private JLabel refresh_frequency_stock;
    private JComboBox<String> refresh_frequency_stock_list;
    private JLabel refresh_frequency_fund;
    private JComboBox<String> refresh_frequency_fund_list;
    private JLabel refresh_frequency_digital;
    private JComboBox<String> refresh_frequency_digital_list;
    private JLabel refresh_frequency_indices;
    private JComboBox<String> refresh_frequency_indices_list;

    private final String[] INDICES_FREQUENCIES = {"2", "5", "10", "20", "30"};
    private final String[] STOCK_FREQUENCIES = {"1", "3", "5", "8", "10"};
    private final String[] FUND_FREQUENCIES = {"5", "10", "20", "30", "60"};
    private final String[] DIGITAL_FREQUENCIES = {"2", "5", "10", "15", "20"};

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return mns_setting.getToolTipText();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        for (String frequency : INDICES_FREQUENCIES) {
            refresh_frequency_indices_list.addItem(frequency);
        }
        for (String frequency : STOCK_FREQUENCIES) {
            refresh_frequency_stock_list.addItem(frequency);
        }
        for (String frequency : FUND_FREQUENCIES) {
            refresh_frequency_fund_list.addItem(frequency);
        }
        for (String frequency : DIGITAL_FREQUENCIES) {
            refresh_frequency_digital_list.addItem(frequency);
        }
        red_rise_green_fall.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                red_fall_green_rise.setSelected(false);
            }
        });
        red_fall_green_rise.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                red_rise_green_fall.setSelected(false);
            }
        });
        return mns_setting;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        final AppSettingState settings = AppSettingState.getInstance();
        settings.usStocks = us_stock_input.getText();
        settings.hkStocks = hk_stock_input.getText();
        settings.cnStocks = cn_stock_input.getText();
        settings.dailyFunds = daily_fund_input.getText();
        settings.allIndices = idx_input.getText();
        settings.cryptoCurrencies = crypto_currency_input.getText();
        settings.isRedRise = red_rise_green_fall.isSelected();
        settings.indicesFrequency = Integer.parseInt(refresh_frequency_indices_list.getSelectedItem().toString());
        settings.stockFrequency = Integer.parseInt(refresh_frequency_stock_list.getSelectedItem().toString());
        settings.fundFrequency = Integer.parseInt(refresh_frequency_fund_list.getSelectedItem().toString());
        settings.cryptoFrequency = Integer.parseInt(refresh_frequency_digital_list.getSelectedItem().toString());
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return us_stock_input;
    }

    @Override
    public void reset() {
        initSettingUI();
    }

    private void initSettingUI() {
        final AppSettingState settings = AppSettingState.getInstance();
        us_stock_input.setText(settings.usStocks);
        hk_stock_input.setText(settings.hkStocks);
        cn_stock_input.setText(settings.cnStocks);
        daily_fund_input.setText(settings.dailyFunds);
        idx_input.setText(settings.allIndices);
        crypto_currency_input.setText(settings.cryptoCurrencies);
        red_rise_green_fall.setSelected(settings.isRedRise);
        red_fall_green_rise.setSelected(!settings.isRedRise);
        boolean isHiddenSymbol = settings.isHiddenSymbol;
        refresh_frequency_indices_list.setSelectedItem(String.valueOf(settings.indicesFrequency));
        refresh_frequency_stock_list.setSelectedItem(String.valueOf(settings.stockFrequency));
        refresh_frequency_fund_list.setSelectedItem(String.valueOf(settings.fundFrequency));
        refresh_frequency_digital_list.setSelectedItem(String.valueOf(settings.cryptoFrequency));
    }

    @Override
    public void disposeUIResources() {
        mns_setting = null;
    }

    @Override
    public void cancel() {

    }
}
