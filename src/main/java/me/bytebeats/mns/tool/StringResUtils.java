package me.bytebeats.mns.tool;

public class StringResUtils {
    public static final String STOCK = "Stocks";
    public static final String STOCK_ALL = "ALL";
    public static final String STOCK_US = "US";
    public static final String STOCK_HK = "HK";
    public static final String STOCK_CN = "CN";
    public static final String INDICES = "Indices";
    public static final String STOCK_NAME = "名称";
    public static final String SYMBOL = "代码";
    public static final String STOCK_LATEST_PRICE = "最新价";
    public static final String RISE_AND_FALL = "涨跌";
    public static final String RISE_AND_FALL_RATIO = "涨跌幅";
    public static final String TIMESTAMP_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    public static final String REFRESH_TIMESTAMP = "Updated at: %s";

    public static final String INDEX_NAME = "名称";
    public static final String INDEX_LATEST = "当前";

    public static final String FUNDS = "Funds";
    public static final String FUND_NAME = "名称";
    public static final String FUND_CODE = "代码";
    public static final String FUND_NET_VALUE_DATE = "单位净值";
    public static final String FUND_NET_VALUE_ESTIMATED = "净值估值";

    public static final String STOCK_DETAIL = "Details";

    public static final String FUND_SEARCH_NAME = "基金";
    public static final String FUND_SEARCH_FIRM = "基金公司";
    public static final String URL_SEARCH_FUND_FIRM = "http://fund.eastmoney.com/js/jjjz_gs.js";
    public static final String URL_SEARCH_FUND = "http://fund.eastmoney.com/js/fundcode_search.js";
    public static final String URL_SEARCH_STOCK = "https://searchadapter.eastmoney.com/api/suggest/get";
    public static final String QT_STOCK_URL = "http://qt.gtimg.cn/q=";//腾讯股票数据接口
    public static final String TIANTIAN_FUND_URL = "http://fundgz.1234567.com.cn/js/%s.js?rt=%d";//天天基金数据接口
    public static final String SINA_CRYPTO_CURRENCY_URL = "http://hq.sinajs.cn/list=%s";//新浪数据接口

    public static final String CRYPTO_CURRENCIES = "Cryptos";
    public static final String CRYPTO_CURRENCY_NAME = "名称";
    public static final String CRYPTO_CURRENCY_CODE = "代码";
    public static final String CRYPTO_CURRENCY_VOLUME = "成交量(24H)";
    public static final String CRYPTO_CURRENCY_PRICE = "最新价(USD)";

    public static final String URL_FUND_CHART_ESTIMATED_NET_WORTH_WITH_WATER_PRINT = "http://j4.dfcfw.com/charts/pic6/%s.png?%s";
    public static final String URL_FUND_CHART_ESTIMATED_NET_WORTH = "http://j4.dfcfw.com/charts/pic7/%s.png?%s";

    public static final String URL_SINA_CHART = "http://image.sinajs.cn/newchart";
    // 沪深股
    // 分时线图  http://image.sinajs.cn/newchart/min/n/sh600519.gif
    // 日K线图  http://image.sinajs.cn/newchart/daily/n/sh600519.gif
    // 周K线图  http://image.sinajs.cn/newchart/weekly/n/sh600519.gif
    // 月K线图  http://image.sinajs.cn/newchart/monthly/n/sh600519.gif
    public static final String URL_SINA_CHART_CN_FORMATTER = "%s/%s/n/%s.gif?%s";
    // 美股
    // 分时线图 http://image.sinajs.cn/newchart/png/min/us/AAPL.png
    // 日K线图 http://image.sinajs.cn/newchart/usstock/daily/aapl.gif
    // 周K线图 http://image.sinajs.cn/newchart/usstock/weekly/aapl.gif
    // 月K线图 http://image.sinajs.cn/newchart/usstock/monthly/aapl.gif
    public static final String URL_SINA_CHART_MIN_FORMATTER = "%s/png/%s/%s/%s.png?%s";
    public static final String URL_SINA_CHART_US_FORMATTER = "%s/%sstock/%s/%s.gif?%s";
    // 港股
    // 分时线图 http://image.sinajs.cn/newchart/png/min/hk/02202.png
    // 日K线图 http://image.sinajs.cn/newchart/hk_stock/daily/02202.gif
    // 周K线图 http://image.sinajs.cn/newchart/hk_stock/weekly/02202.gif
    // 月K线图 http://image.sinajs.cn/newchart/hk_stock/monthly/02202.gif
    public static final String URL_SINA_CHART_HK_FORMATTER = "%s/%s_stock/%s/%s.gif?%s";
}
