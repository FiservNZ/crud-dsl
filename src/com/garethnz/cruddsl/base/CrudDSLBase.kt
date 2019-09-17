package com.garethnz.cruddsl.base

import okhttp3.OkHttpClient
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

interface Element {
    fun render(builder: StringBuilder, indent: String)
    fun applyToServer(client: OkHttpClient)
}

@DslMarker
annotation class CrudDSLMarker

@CrudDSLMarker
abstract class Tag() : Element {
    val children = arrayListOf<Element>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent${this::class.simpleName} {${renderAttributes(indent+"  ")}")
        for (c in children) {
            c.render(builder, indent + "  ")
        }
        builder.append("$indent}\n")
    }

    private fun renderAttributes(indent: String): String {
        val builder = StringBuilder("\n")
        for (prop in this::class.memberProperties) {
            if (prop.name.equals("attributes") || prop.name.equals("children")) {
                continue
            }
            builder.append("$indent${prop.name} ${prop.javaGetter?.invoke(this)}\n")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

abstract class ListAPI(
    var exhaustive: Boolean = false) : Tag() {
    // Subclass will define dsl for children
}
