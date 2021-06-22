package skywolf46.microgamelib.data

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.TypeVariable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

data class FieldData(
    val fieldName: String,
    val displayName: String,
    val field: Field,
    val type: Class<Any>,
    val defValue: Any?,
) {
    val argumentTypes: Array<Class<*>> = if (field.genericType is ParameterizedType) {
        val data = field.genericType as ParameterizedType
        try {
            data.actualTypeArguments.map { x ->
                Class.forName(x.typeName)
            }.toTypedArray()
        } catch (e: Exception) {
            throw IllegalStateException("Cannot process field ${field.name} : ${e.message}")
        }
    } else emptyArray()

}