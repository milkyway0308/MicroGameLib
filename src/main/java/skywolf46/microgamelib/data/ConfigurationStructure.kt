package skywolf46.microgamelib.data

import org.bukkit.configuration.ConfigurationSection
import skywolf46.extrautility.util.log
import skywolf46.microgamelib.annotations.Replace
import java.lang.IllegalStateException
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.kotlinProperty

class ConfigurationStructure {
    //                                       FieldName, Field type, Default declare
    val fields = mutableMapOf<String, FieldData>()
    var declaredVariables = mutableMapOf<String, Any>()
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
                log("§e---- Loaded ${cls.qualifiedName}#${x.name}")
            }
        } else {
            log("§c---- Cannot process ${cls.qualifiedName} : @GameConfiguration target constructor must empty.")
        }
    }

    constructor(cls: KClass<*>, config: ConfigurationSection) : this(cls) {
        for (x in config.getKeys(false)) {
            declaredVariables[x] = config[x]
        }
    }

    operator fun get(name: String) = declaredVariables[name]

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
        if (getUndeclaredFields().isNotEmpty())
            throw IllegalStateException("Cannot create configuration instance : Variable not configured")
        for ((_, y) in fields) {
            y.field.set(instance, declaredVariables[y.fieldName])
        }
        return instance
    }

    fun loadFromSection(sector: ConfigurationSection): ConfigurationStructure {
        return ConfigurationStructure(targetClass, sector)
    }
}