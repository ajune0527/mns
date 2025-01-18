package me.bytebeats.mns.enumation

enum class StockChartType(val type: String, val description: String) {
    Minute("min", "分时图"),
    Daily("daily", "日K图"),
    Weekly("weekly", "周K图"),
    Monthly("monthly", "月K图"),
    Up("up", "上移"),
    Down("low", "下移"),
    Delete("delete", "删除");
}