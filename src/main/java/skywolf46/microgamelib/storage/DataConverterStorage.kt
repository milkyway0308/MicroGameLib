package skywolf46.microgamelib.storage

object DataConverterStorage {
    private val converters = mutableMapOf<Pair<Class<*>, Class<*>>, (Any) -> Any>()

    init {
        registerConverter<String, Double> {
            it.toDouble()
        }

        registerConverter<String, Int> {
            it.toInt()
        }

        registerConverter<String, Float> {
            it.toFloat()
        }

    }

    inline fun <reified FROM : Any, reified TO : Any> registerConverter(noinline convert: (FROM) -> TO) {
        registerConverter(FROM::class.java, TO::class.java, convert)
    }

    fun <FROM : Any, TO : Any> registerConverter(from: Class<FROM>, to: Class<TO>, convert: (FROM) -> TO) {
        converters[from.primitive() to to.primitive()] =
            convert as (Any) -> Any
    }

    inline fun <reified FROM : Any, reified TO : Any> of(): ((FROM) -> TO)? {
        return of(FROM::class.java, TO::class.java)
    }

    fun <FROM : Any, TO : Any> of(from: Class<FROM>, to: Class<TO>): ((FROM) -> TO)? {
        return converters[from.primitive() to to.primitive()] as ((FROM) -> TO)?
    }

    private fun Class<*>.primitive() = kotlin.javaPrimitiveType ?: this

}