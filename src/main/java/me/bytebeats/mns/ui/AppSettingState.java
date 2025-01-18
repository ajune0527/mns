package me.bytebeats.mns.ui;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.util.xmlb.XmlSerializerUtil;
import me.bytebeats.mns.enumation.StockChartType;
import me.bytebeats.mns.tool.NotificationUtil;
import me.bytebeats.mns.tool.StringResUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@State(name = "me.bytebeats.mns.ui.AppSettingState", storages = {@Storage("mns_plugin_setting_dev.xml")})
public class AppSettingState implements PersistentStateComponent<AppSettingState> {

    public final static boolean IS_RED_RISE = true;
    public final static boolean IS_HIDDEN_SYMBOL_MODE = false;
    public final static String US_STOCKS = "AAPL;TSLA;NFLX;MSFT";
    public final static String HK_STOCKS = "00981;09988;09618";
    public final static String CN_STOCKS = "002352;600036";
    public final static String DAILY_FUNDS = "320003;002621;519674";
    public final static String ALL_INDICES = "usDJI;usIXIC;usINX;usNDX;hkHSI;hkHSTECH;";
    public final static String STOCK_SYMBOL = "sh600519";
    public final static String FUND_SYMBOL = "570008";

    public boolean isRedRise = IS_RED_RISE;
    public boolean isHiddenSymbol = IS_HIDDEN_SYMBOL_MODE;
    public String cryptoCurrencies = "BTC;DOGE";
    public String usStocks = US_STOCKS;
    public String hkStocks = HK_STOCKS;
    public String cnStocks = CN_STOCKS;
    public String dailyFunds = DAILY_FUNDS;
    public String allIndices = ALL_INDICES;
    public String stockSymbol = STOCK_SYMBOL;
    public String fundSymbol = FUND_SYMBOL;
    public int indicesFrequency = 5;
    public int stockFrequency = 3;
    public int fundFrequency = 20;
    public int cryptoFrequency = 5;

    public String localVersion = "0.0.0";
    public String version = "1.8.4";

    @Override
    public void initializeComponent() {
        PersistentStateComponent.super.initializeComponent();
        version = Objects.requireNonNull(PluginManagerCore.getPlugin(PluginId.getId("me.ajune.mns.plus"))).getVersion();
        if (isNewVersion()) {
            updateLocalVersion();
//            NotificationUtil.infoToolWindow("修复了新浪财经接口被禁止调用的问题; 近 4 年来的工程层面最大的 Upgrade! ");
        }
    }

    private boolean isNewVersion() {
        String[] subLocalVersions = localVersion.split("\\.");
        String[] subVersions = version.split("\\.");
        if (subLocalVersions.length != subVersions.length) {
            return false;
        }
        int idx = 0;
        do {
            int localVersion = Integer.parseInt(subLocalVersions[idx]);
            int version = Integer.parseInt(subVersions[idx]);
            if (version > localVersion) {
                return true;
            }
            idx++;
        } while (idx < subLocalVersions.length);
        return false;
    }

    private void updateLocalVersion() {
        localVersion = version;
    }

    public static AppSettingState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingState.class);
    }

    public void reset() {
        isRedRise = IS_RED_RISE;
        isHiddenSymbol = IS_HIDDEN_SYMBOL_MODE;
        usStocks = US_STOCKS;
        hkStocks = HK_STOCKS;
        cnStocks = CN_STOCKS;
        dailyFunds = DAILY_FUNDS;
        allIndices = ALL_INDICES;
        stockSymbol = STOCK_SYMBOL;
        fundSymbol = FUND_SYMBOL;
    }

    @Nullable
    @Override
    public AppSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingState appSettingState) {
        XmlSerializerUtil.copyBean(appSettingState, this);
    }

    public boolean addFundSymbol(String fundSymbol) {
        if (dailyFunds.contains(fundSymbol)) {
            return false;
        }
        String df = dailyFunds.trim();
        if (df.endsWith(";")) {
            dailyFunds = df + fundSymbol;
        } else {
            dailyFunds = df + ";" + fundSymbol;
        }
        // 触发持久化
        ApplicationManager.getApplication().saveSettings();
        return true;
    }

    public boolean deleteFundSymbol(String fundSymbol) {
        if (!dailyFunds.contains(fundSymbol)) {
            return false;
        }
        String df = dailyFunds.trim();
        dailyFunds = df.replace(fundSymbol, "");
        // 触发持久化
        ApplicationManager.getApplication().saveSettings();
        return true;
    }

    // 使用 LinkedHashSet 存储符号，保持插入顺序
    private Set<String> allIndicesSet = new LinkedHashSet<>();

    private Set<String> initializeIndicesSet(String data) {
        String[] indicesArray = data.split(";");
        return new LinkedHashSet<>(Arrays.asList(indicesArray));
    }

    public void addIndexSymbol(String symbol) {
        allIndicesSet = initializeIndicesSet(allIndices);
        if (allIndicesSet.add(symbol)) {
            allIndices = String.join(";", allIndicesSet);
            ApplicationManager.getApplication().saveSettings();
        }
    }

    public void deleteIndexSymbol(String symbol) {
        allIndicesSet = initializeIndicesSet(allIndices);
        if (allIndicesSet.remove(symbol)) {
            allIndices = String.join(";", allIndicesSet);
            ApplicationManager.getApplication().saveSettings();
        }
    }

    public boolean addStockSymbol(String symbol, String market) {
        symbol = symbol.replace("sz", "").replace("sh", "").replace("hk", "").replace("us", "");
        if (market.contains("沪") || market.contains("深") || market.contains("京")) {
            Set<String> cnStocksSet = initializeIndicesSet(cnStocks);
            if (!cnStocksSet.contains(symbol)) {
                cnStocksSet.add(symbol);
                cnStocks = String.join(";", cnStocksSet);
                ApplicationManager.getApplication().saveSettings();
            }
        }

        if (market.contains("港")) {
            Set<String> hkStocksSet = initializeIndicesSet(hkStocks);
            if (!hkStocksSet.contains(symbol)) {
                hkStocksSet.add(symbol);
                hkStocks = String.join(";", hkStocksSet);
                ApplicationManager.getApplication().saveSettings();
            }
        }

        if (market.contains("美")) {
            Set<String> usStocksSet = initializeIndicesSet(usStocks);
            if (!usStocksSet.contains(symbol)) {
                usStocksSet.add(symbol);
                usStocks = String.join(";", usStocksSet);
                ApplicationManager.getApplication().saveSettings();
            }
        }


        return true;
    }

    private String joinStocks(List<String> stocks) {
        return String.join(";", stocks);
    }

    private List<String> initializeIndicesList(String stocks) {
        return new ArrayList<>(Arrays.asList(stocks.split(";")));
    }

    private String getMarketType(String symbol) {
        if (symbol.startsWith("sz") || symbol.startsWith("sh")) {
            return "cnStocks";
        } else if (symbol.startsWith("hk")) {
            return "hkStocks";
        } else if (symbol.startsWith("us")) {
            return "usStocks";
        }
        return null;
    }

    private void moveSymbolInList(String symbol, StockChartType selectedValue, List<String> stocksList, String stocksField) {
        symbol = symbol.replace("sz", "").replace("sh", "").replace("hk", "").replace("us", "");
        int index = stocksList.indexOf(symbol);
        if (index != -1) {
            if (selectedValue == StockChartType.Up && index > 0) {
                // 向前移动一位
                Collections.swap(stocksList, index, index - 1);
            } else if (selectedValue == StockChartType.Down && index < stocksList.size() - 1) {
                // 向后移动一位
                Collections.swap(stocksList, index, index + 1);
            }
            // 更新字符串并保存设置
            switch (stocksField) {
                case "cnStocks":
                    cnStocks = joinStocks(stocksList);
                    break;
                case "hkStocks":
                    hkStocks = joinStocks(stocksList);
                    break;
                case "usStocks":
                    usStocks = joinStocks(stocksList);
                    break;
            }
            ApplicationManager.getApplication().saveSettings();
        }
    }

    public void moveStockSymbol(String symbol, StockChartType selectedValue) {
        String marketType = getMarketType(symbol);
        if (marketType != null) {
            List<String> stocksList = initializeIndicesList(getStocksByMarket(marketType));
            moveSymbolInList(symbol, selectedValue, stocksList, marketType);
        }
    }

    private String getStocksByMarket(String marketType) {
        switch (marketType) {
            case "cnStocks":
                return cnStocks;
            case "hkStocks":
                return hkStocks;
            case "usStocks":
                return usStocks;
            default:
                return "";
        }
    }

    public void deleteStockSymbol(String symbol) {
        symbol = symbol.replace("sz", "").replace("sh", "").replace("hk", "").replace("us", "");
        Set<String> cnStocksSet = initializeIndicesSet(cnStocks);
        if (cnStocksSet.contains(symbol)) {
            if (cnStocksSet.remove(symbol)) {
                cnStocks = String.join(";", cnStocksSet);
                ApplicationManager.getApplication().saveSettings();
            }
        }

        Set<String> hkStocksSet = initializeIndicesSet(hkStocks);
        if (hkStocksSet.contains(symbol)) {
            if (hkStocksSet.remove(symbol)) {
                hkStocks = String.join(";", hkStocksSet);
                ApplicationManager.getApplication().saveSettings();
            }
        }

        Set<String> usStocksSet = initializeIndicesSet(usStocks);
        if (usStocksSet.contains(symbol)) {
            if (usStocksSet.remove(symbol)) {
                usStocks = String.join(";", usStocksSet);
                ApplicationManager.getApplication().saveSettings();
            }
        }

    }

    private String currentMarket;

    public void setCurrentMarketSymbol(String currentMarket) {
        this.currentMarket = currentMarket;
    }

    public String getCurrentMarket() {
        return this.currentMarket;
    }

    public List<String> getStockSymbol() {
        return switch (currentMarket) {
            case StringResUtils.STOCK_CN -> Arrays.asList(cnStocks.split(";"));
            case StringResUtils.STOCK_HK -> Arrays.asList(hkStocks.split(";"));
            case StringResUtils.STOCK_US -> Arrays.asList(usStocks.split(";"));
            default -> getAllStockSymbols();
        };
    }

    private List<String> getAllStockSymbols() {
        // 使用 LinkedHashSet 确保没有重复符号且保持插入顺序
        Set<String> allStocksSet = new LinkedHashSet<>();
        allStocksSet.addAll(Arrays.asList(cnStocks.split(";")));
        allStocksSet.addAll(Arrays.asList(hkStocks.split(";")));
        allStocksSet.addAll(Arrays.asList(usStocks.split(";")));

        // 将 Set 转换为 List
        return new ArrayList<>(allStocksSet);
    }

    public List<String> getAllIndex() {
        return List.of(allIndices.split(";"));
    }
}