package com.garethnz.cruddsl.base

import com.squareup.moshi.JsonAdapter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

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

// S = self/the list object, C=child object
abstract class ListAPI<S,C : ItemApi<C>>(
    var exhaustive: Boolean = false) : Tag() {

    abstract fun url() : String

    abstract fun getJsonAdapter() : JsonAdapter<S>

    abstract fun getChildElements() : MutableList<C>

    abstract fun listOfChildren(sourceData : S) : Iterator<C>

    override fun applyToServer(client: OkHttpClient) {
        // This would compare children to the list returned from https://reqres.in/api/users
        val request = Request.Builder()
            .url(url())
            .build()

        var response: S?
        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            response = getJsonAdapter().fromJson(this.body?.source()!!)
            //println( usersResponse )
        }
        val childrenToProcess = getChildElements()

        response?.let {
            listOfChildren(it).forEach { element : C ->
                // If it matches a child we want, then sync to it
                val matchingChild = childrenToProcess.firstOrNull { child -> element.primaryKeyEquals(child) }
                if (matchingChild != null) {
                    matchingChild.applyToServer(client, element)
                    childrenToProcess.remove(matchingChild)
                }
                else {

                    if (exhaustive) {
                        // TODO: Delete user from the server as it doesn't match something we want to add
                        element.delete(client)
                    }
                }
            }
        }

        childrenToProcess.forEach { element: C -> element.applyToServer(client) }
        // CREATE CHILD, LET CHILD Update the existing instance, DELETE user on server
    }
}

// TODO: NOTE: T == Subclass for now
abstract class ItemApi<T> : Tag() {
    override fun applyToServer(client: OkHttpClient) {
        applyToServer(client, null)
    }

    abstract fun setPrimaryId(destinationPrimary: T)
    abstract fun primaryKeyEquals(target: T) : Boolean // TODO: Just a property that returns the value of primarykey which can then be .equals?

    abstract fun url() : String // Not a property because reflection to print / JSON would then include it

    abstract val primaryIdForUrl : String

    abstract fun userVisibleName() : String

    abstract fun getAsJson(): String

    fun applyToServer(client: OkHttpClient, target: T?) {
        var createTputF = true
        // This would compare an instance of user from https://reqres.in/api/users/2 with the current object. CREATE / UPDATE as needed
        // Just take the ID of this and then do a PUT? if there are any other differences
        target?.let {
            setPrimaryId(target)
            createTputF = false
        }

        if (this.toString().equals(target.toString())) {
            println("${this::class.simpleName} ${userVisibleName()} already exists and is equal. Skipping")
            return // No Actions Needed
        }

        val request : Request
        if (createTputF) {
            request = Request.Builder()
                .url(url() + primaryIdForUrl) // TODO does this type of API usually work? i.e. requesting the ID of a new object?
                .post(getAsJson().toRequestBody(com.example.reqres.MEDIA_TYPE_JSON))
                .build()
        } else {
            request = Request.Builder()
                .url(url() + primaryIdForUrl)
                .put(getAsJson().toRequestBody(com.example.reqres.MEDIA_TYPE_JSON))
                .build()
        }

        client.newCall(request).execute().apply {
            println("Create? ${createTputF} call successful")
            println(this.request.url.toUrl().toString())
            println(this.body?.string())
        }
    }

    fun delete(client: OkHttpClient) {
        val request = Request.Builder()
            .url(url() + primaryIdForUrl) // TODO does this type of API usually work? i.e. requesting the ID of a new object?
            .delete(getAsJson().toRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).execute().apply {
            println("Item ${primaryIdForUrl} ${userVisibleName()} deleted successfully")
            println(this.request.url.toUrl().toString())
            println(this.body?.string())
        }
    }
}