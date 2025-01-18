package me.bytebeats.mns

@Deprecated(
    message = "Replaced with OnItemClick<T>",
    replaceWith = ReplaceWith(expression = "Replaced with OnItemClick<T>", imports = emptyArray()),
    level = DeprecationLevel.WARNING
)
interface OnSymbolSelectedListener {
    fun onSelected(symbol: String?)
}