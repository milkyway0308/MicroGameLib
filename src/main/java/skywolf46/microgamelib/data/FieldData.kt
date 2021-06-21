package skywolf46.microgamelib.data

import java.lang.reflect.Field
import kotlin.reflect.KProperty1

data class FieldData(
    val fieldName: String,
    val displayName: String,
    val field: Field,
    val type: Class<Any>,
    val defValue: Any?,
)
