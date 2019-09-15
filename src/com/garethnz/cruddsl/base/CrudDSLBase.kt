package com.garethnz.cruddsl.base

import okhttp3.OkHttpClient
import kotlin.reflect.typeOf

interface Element {
    fun render(builder: StringBuilder, indent: String)
    fun applyToServer(client: OkHttpClient)
}

@DslMarker
annotation class CrudDSLMarker

@CrudDSLMarker
abstract class Tag(val name: String) : Element {
    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, String>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$name {${renderAttributes(indent+"  ")}")
        for (c in children) {
            c.render(builder, indent + "  ")
        }
        builder.append("$indent}\n")
    }

    private fun renderAttributes(indent: String): String {
        val builder = StringBuilder("\n")
        if (attributes.size == 0) {
            return builder.toString()
        }
        for ((attr, value) in attributes) {
            builder.append("$indent$attr = \"$value\"\n")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

public abstract class ListAPI : Tag("list") {
    // Subclass will define dsl for children
    var exhaustive: Boolean
        get() = attributes["exhaustive"]!!.toBoolean()
        set(value) {
            attributes["exhaustive"] = value.toString()
        }
}
