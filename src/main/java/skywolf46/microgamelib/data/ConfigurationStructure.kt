package skywolf46.microgamelib.data

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import skywolf46.extrautility.util.MethodInvoker
import skywolf46.extrautility.util.MethodWrapper
import skywolf46.extrautility.util.getMap
import skywolf46.extrautility.util.log
import skywolf46.microgamelib.annotations.Replace
import skywolf46.microgamelib.storage.DataConverterStorage
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.kotlinProperty

class ConfigurationStructure {
    val fields = mutableMapOf<String, FieldData>()
    val fieldErrorListener = mutableMapOf<String, Method>()
    var declaredVariables = mutableMapOf<String, Any>()

    @Suppress("JoinDeclarationAndAssignment")
    val targetClass: KClass<*>

    constructor(cls: KClass<*>) {
        targetClass = cls
        if (cls.primaryConstructor!!.parameters.isEmpty()) {
            val created = cls.primaryConstructor!!.call()
            for (x in cls.declaredMemberProperties) {
                x.isAccessible = true
                val field = x.javaField
                if (field == null || Modifier.isStatic(field.modifiers) || Modifier.isFinal(field.modifiers)) {
                    continue
                }
                if (x.isLateinit) {
                    log("§c---- @GameConfiguration target class not supports lateinit field. Ignoring field ${cls.qualifiedName}#${x.name}.")
                    continue
                }
                x.javaField!!.isAccessible = true
                val fieldType =
                    FieldData(x.name,
                        x.javaField!!.getAnnotation(Replace::class.java)?.run {
                            value
                        } ?: x.name,
                        x.javaField!!,
                        x.javaField!!.type as Class<Any>,
                        (x as KProperty1<Any, Any>).get(created))
                fields[fieldType.displayName] = fieldType
                if (fieldType.defValue != null)
                    declaredVariables[fieldType.displayName] = fieldType.defValue
            }
        } else {
            log("§c---- Cannot process ${cls.qualifiedName} : @GameConfiguration target constructor must empty.")
        }
        for (x in cls.java.declaredMethods) {
            if (x.name in fields) {
                x.isAccessible = true
                fieldErrorListener[x.name] = x
            }
        }
    }

    constructor(cls: KClass<*>, config: ConfigurationSection) : this(cls) {
        for (x in config.getKeys(false)) {
            // Skip if not member
            if (x !in fields)
                continue
            val data = config.get(x)!!
            if (data is ConfigurationSection) {
                // Construct to map
                declaredVariables[x] = config.getMap(x)
            } else {
                // Try to cast if type illegal
                val type = fields[x]!!.type
                if (!type.isAssignableFrom(data.javaClass.run {
                        if (kotlin.javaPrimitiveType == null) this else kotlin.javaPrimitiveType!!
                    })) {
                    try {
                        DataConverterStorage.of(
                            data.javaClass, fields[x]!!.type
                        )?.apply {
                            declaredVariables[x] =
                                DataConverterStorage.of(data.javaClass, type)!!.invoke(data)
                        }
                            ?: log("§c---- Deserialization denied for field $x at configuration class ${cls.qualifiedName} : No converter registered from ${data.javaClass.name} to ${fields[x]!!.type.name}")
                    } catch (e: Exception) {
                        log("§c---- Illegal field deserialization in field $x at configuration class ${cls.qualifiedName} : Error occurred while converting from ${data.javaClass.name} to ${fields[x]!!.type.name}")
                        e.printStackTrace()
                    }
                } else {
                    declaredVariables[x] = data
                }
            }
        }

    }

    operator fun get(name: String) = declaredVariables[name]

    fun callError() {

    }

    fun getUndeclaredFields(): List<String> {
        val lst = mutableListOf<String>()
        for ((_, y) in fields) {
            if (y.fieldName !in declaredVariables) {
                lst.add(y.displayName)
            }
        }
        return lst.distinct()
    }

    fun constructToInstance(): Any {
        val instance = targetClass.primaryConstructor!!.call()
//        if (getUndeclaredFields().isNotEmpty())
//            throw IllegalStateException("Cannot create configuration instance : Variable not configured")
        for ((_, y) in fields) {
            y.field.set(instance, declaredVariables[y.fieldName])
        }
        return instance
    }

    fun loadFromSection(sector: ConfigurationSection): ConfigurationStructure {
        return ConfigurationStructure(targetClass, sector)
    }

    fun saveToSection(yamlConfiguration: ConfigurationSection) {
        for ((x, y) in declaredVariables) {
            yamlConfiguration.set(x, y)
        }
    }
}