package info.ljungqvist.android.widget.util

internal data class FloatPoint(var x: Float, var y: Float) {

    operator fun unaryMinus() = FloatPoint(-x, -y)
    operator fun plus(p: FloatPoint) = FloatPoint(x + p.x, y + p.y)
    operator fun plus(f: Float) = FloatPoint(x + f, y + f)
    operator fun minus(p: FloatPoint) = this + (-p)
    operator fun times(p: FloatPoint) = FloatPoint(x * p.x, y * p.y)
    operator fun times(f: Float) = FloatPoint(x * f, y * f)

    operator fun plusAssign(p: FloatPoint) {
        x += p.x
        y += p.y
    }

    fun toIntPoint(): IntPoint = IntPoint(x.toInt(), y.toInt())
    fun max() = if (x > y) x else y
    fun sum() = x + y
    fun squareDistance(p: FloatPoint) = (this - p).let { it * it }.sum()

}
