package info.ljungqvist.android.widget.util

internal data class IntPoint(val x: Int, val y: Int) {

    operator fun unaryMinus() = IntPoint(-x, -y)
    operator fun plus(p: IntPoint) = IntPoint(x + p.x, y + p.y)
    operator fun plus(i: Int) = IntPoint(x + i, y + i)
    operator fun div(i: Int) = IntPoint(x / i, y / i)

    fun toFloatPoint() = FloatPoint(x.toFloat(), y.toFloat())
}