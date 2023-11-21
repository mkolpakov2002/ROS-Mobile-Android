package ru.hse.miem.ros.data.model.general

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import java.lang.reflect.Type

/**
 * TODO: Description
 * Credits to Marcus Brutus.
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 23.09.20
 * @updated on
 * @modified by
 */
class WidgetSerializationAdapter : JsonSerializer<BaseEntity>, JsonDeserializer<BaseEntity> {
    override fun serialize(
        src: BaseEntity, typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonObject().apply {
            addProperty(CLASSNAME, src.javaClass.name)
            add(INSTANCE, context.serialize(src))
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement, typeOfT: Type,
        context: JsonDeserializationContext
    ): BaseEntity {
        val jsonObject = json.asJsonObject
        val className = jsonObject.getAsJsonPrimitive(CLASSNAME).asString
        val klass: Class<*> = try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            throw JsonParseException(e.message)
        }
        return context.deserialize(jsonObject[INSTANCE], klass)
    }

    companion object {
        private const val CLASSNAME = "CLASSNAME"
        private const val INSTANCE = "INSTANCE"
    }
}
