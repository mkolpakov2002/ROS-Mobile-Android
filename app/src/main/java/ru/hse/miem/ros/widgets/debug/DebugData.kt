package ru.hse.miem.ros.widgets.debug

import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import org.apache.commons.lang.StringUtils
import org.ros.internal.message.Message
import org.ros.internal.message.field.Field
import org.ros.internal.message.field.ListField
import java.lang.reflect.Array

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 17.08.2020
 * @updated on 07.09.2020
 * @modified by Maxim Kolpakov
 */
class DebugData(message: Message) : BaseData() {
    private val content: ArrayList<String> = ArrayList()
    var value: String

    init {
        msgToString(message, 0)
        content.add("---------")
        value = joinContent("\n", content)
    }

    private fun msgToString(message: Message, level: Int) {
        val fields: List<Field> = message.toRawMessage().fields
        for (field: Field in fields) {
            fieldToString(field, level)
        }
    }

    private fun fieldToString(field: Field, level: Int) {
        val fieldString: String = StringUtils.repeat("\t", level) + field.name + ":"
        content.add(fieldString)
        val value: Any = field.getValue()
        if (field is ListField<*>) {
            for (o: Any in field.value) {
                val listPrefix: String = StringUtils.repeat("\t", level + 1) + "-"
                content.add(listPrefix)
                if (o is String) {
                    content.add(o)
                } else if (o is Message) {
                    msgToString(o, level + 2)
                }
            }
        } else if (value is Field) {
            fieldToString(field, level + 1)
        } else if (value is Message) {
            msgToString(value, level + 1)
        } else {
            var valueStr: String
            if (value.javaClass.isArray) {
                // Value is a type of list
                val length: Int = Array.getLength(value)
                valueStr = "["
                for (i in 0 until length) {
                    if (i > 0) valueStr += ", "
                    valueStr += Array.get(value, i)
                }
                valueStr += "]"
            } else {
                // Only single value
                valueStr = value.toString()
            }
            val last: String = content[content.size - 1]
            content[content.size - 1] = "$last $valueStr"
        }
    }

    private fun joinContent(delimiter: String, content: List<String>): String {
        var loopDelim: String? = ""
        val out: StringBuilder = StringBuilder()
        for (s: String in content) {
            out.append(loopDelim)
            out.append(s)
            loopDelim = delimiter
        }
        return out.toString()
    }

    companion object {
        val TAG: String = DebugData::class.java.simpleName
    }
}