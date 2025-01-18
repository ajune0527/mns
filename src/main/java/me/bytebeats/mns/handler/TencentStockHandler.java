package me.bytebeats.mns.handler;

import me.bytebeats.mns.listener.MousePressedListener;
import me.bytebeats.mns.meta.Stock;
import me.bytebeats.mns.network.HttpClientPool;
import me.bytebeats.mns.tool.NotificationUtil;
import me.bytebeats.mns.tool.StringResUtils;
import me.bytebeats.mns.ui.AppSettingState;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TencentStockHandler extends AbsStockHandler {

    public TencentStockHandler(JTable table, JLabel label) {
        super(table, label);
        table.addMouseListener(new MousePressedListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRowIdx = jTable.getSelectedRow();
                if (selectedRowIdx < 0) {
                    return;
                }
                String symbol = stocks.get(selectedRowIdx).getSymbol();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 2 && onItemDoubleClickListener != null) {
                        onItemDoubleClickListener.onItemDoubleClick(symbol, e.getXOnScreen(), e.getYOnScreen());
                    } else if (e.getClickCount() == 1 && onItemClickListener != null) {
                        onItemClickListener.onItemClick(symbol, e.getXOnScreen(), e.getYOnScreen());
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    if (onItemRightClickListener != null) {
                        onItemRightClickListener.onItemRightClick(symbol, e.getXOnScreen(), e.getYOnScreen());
                    }
                }
            }
        });

    }

    @Override
    public String[] getColumnNames() {
        return handleColumnNames(stockColumnNames);
    }

    @Override
    public synchronized void load(List<String> symbols) {
        stocks.clear();
        if (timer == null) {
            timer = new Timer();
            updateFrequency();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fetch(parse());
            }
        }, 0, frequency);
        NotificationUtil.info("starts updating " + getTipText() + " stocks");
    }

    @Override
    protected String getTipText() {
        return jTable.getToolTipText();
    }

    public List<String> symbols;

    private void fetch(List<String> symbols) {
        if (symbols.isEmpty()) {
            return;
        }
        StringBuilder params = new StringBuilder();
        for (String symbol : symbols) {
            if (params.length() != 0) {
                params.append(',');
            }
            params.append(symbol);
        }
        try {
            String entity = HttpClientPool.getInstance().get(appendParams(params.toString()));
            parse(symbols, entity);
        } catch (Exception e) {
            NotificationUtil.info(e.getMessage());
            timer.cancel();
            timer = null;
            NotificationUtil.info("stops updating " + jTable.getToolTipText() + " data because of " + e.getMessage());
        }
    }

    private void parse(List<String> symbols, String entity) {
        String[] raws = entity.split("\n");
        for (int i = 0; i < symbols.size(); i++) {
            String symbol = symbols.get(i);
            for (String raw : raws) {
                String assertion = String.format("(?<=v_%s=\").*?(?=\";)", symbol);
                Pattern pattern = Pattern.compile(assertion);
                Matcher matcher = pattern.matcher(raw);
                while (matcher.find()) {
                    String[] metas = matcher.group().split("~");
                    Stock stock = new Stock();
                    stock.setSymbol(symbol);
                    stock.setName(metas[1]);
                    stock.setLatestPrice(Double.parseDouble(metas[3]));
                    stock.setChange(Double.parseDouble(metas[4]));
                    stock.setChangeRatio(Double.parseDouble(metas[5]));
                    updateStock(stock);
                    updateView();
                }
            }
        }
    }

    public void updateSymbols(List<String> symbols) {
        this.symbols = symbols;
    }

    public List<String> parse() {
        List<String> symbols = new ArrayList<>();
        String market = AppSettingState.getInstance().getCurrentMarket();
        if (market == null) {
            market = StringResUtils.STOCK_ALL; // 设置一个默认值
        }
        switch (market) {
            case StringResUtils.STOCK_US:
                Arrays.stream(AppSettingState.getInstance().usStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_us" + s));
                break;
            case StringResUtils.STOCK_HK:
                Arrays.stream(AppSettingState.getInstance().hkStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_hk" + s));
                break;
            case StringResUtils.STOCK_CN:
                Arrays.stream(AppSettingState.getInstance().cnStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sh" + s));
                Arrays.stream(AppSettingState.getInstance().cnStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sz" + s));
                break;
            default:
                Arrays.stream(AppSettingState.getInstance().usStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_us" + s));
                Arrays.stream(AppSettingState.getInstance().hkStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_hk" + s));
                Arrays.stream(AppSettingState.getInstance().cnStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sh" + s));
                Arrays.stream(AppSettingState.getInstance().cnStocks.split("[,; ]")).filter(s -> !s.isEmpty()).forEach(s -> symbols.add("s_sz" + s));
        }
        return symbols;
    }
}
