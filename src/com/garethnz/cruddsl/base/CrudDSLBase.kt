package com.garethnz.cruddsl.base

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaType

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
        val name = this::class.simpleName!!.toLowerCase().replace("list","s")
        builder.append("$indent${name} {${renderAttributes(indent+"  ")}")
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

            prop.javaGetter?.let {
                it.isAccessible = true
                val obj = it.invoke(this)
                obj?.let {
                    if (prop.returnType.javaType.typeName == "java.lang.String") {
                        builder.append("$indent${prop.name} = \"${obj}\"\n")
                    } else if (prop.returnType.javaType.typeName == "java.lang.String[]") {
                        val objArray = obj as Array<String>
                        builder.append("$indent${prop.name} = ${objArray.joinToString("\", \"", prefix = "[\"", postfix = "\"]")}\n")
                    } else {
                        builder.append("$indent${prop.name} = ${obj}\n")
                    }
                }
            }
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
                        element.delete(client)
                    }
                }
            }
        }

        childrenToProcess.forEach { element: C -> element.applyToServer(client) }
        // CREATE CHILD, LET CHILD Update the existing instance, DELETE user on server
    }

    fun readFromServer(client: OkHttpClient) {
        val request = Request.Builder()
            .url(url())
            .build()

        var response: S?
        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            response = getJsonAdapter().fromJson(this.body?.source()!!)
            response?.let {
                if (it is Array<*>) {
                    it.forEach {
                        if (it is ItemApi<*>) {
                            it.readFromServer(client)
                            children.add(it)
                        }
                    }
                }

            }
        }
    }
}

// TODO: NOTE: T == Subclass for now
// Allowed to have children, but matching won't be done on them as if from a list
abstract class ItemApi<T> : Tag() {
    // Only override this if you want to get the object needed to match against
    override fun applyToServer(client: OkHttpClient) {
        applyToServer(client, null)
    }

    // NOTE: Any children should be updated if relevant
    abstract fun setPrimaryId(destinationPrimary: T)
    abstract fun primaryKeyEquals(target: T) : Boolean // TODO: Just a property that returns the value of primarykey which can then be .equals?

    enum class HttpRequestType {
        GET,
        PUT,
        POST,
        DELETE
    }
    abstract fun itemUrl(type: HttpRequestType = HttpRequestType.POST) : String // Not a property because reflection to print / JSON would then include it

    abstract fun userVisibleName() : String



    fun getAsJson(): String {
        return getJsonAdapter().toJson(this)
    }

    fun getJsonAdapter(): JsonAdapter<ItemApi<T>> {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter(this.javaClass)

    }

    fun getFromJson(source: BufferedSource?) : T? {
        return getJsonAdapter().fromJson(source!!) as T?
    }


    fun applyToServer(client: OkHttpClient, target: T?) {
        var createTputF = true
        // This would compare an instance of user from https://reqres.in/api/users/2 with the current object. CREATE / UPDATE as needed
        // Just take the ID of this and then do a PUT? if there are any other differences
        target?.let {
            setPrimaryId(target)
            createTputF = false
        }

        if (this.equals(target)) {
            println("${this::class.simpleName} ${userVisibleName()} already exists and is equal. Skipping")

            // Check children, but no actions needed for this item
            children.forEach{
                it.applyToServer(client)
            }
            return // No Actions Needed
        }

        val request : Request
        if (createTputF) {
            request = Request.Builder()
                .url(itemUrl(HttpRequestType.POST))
                .post(getAsJson().toRequestBody(MEDIA_TYPE_JSON))
                .build()
        } else {
            request = Request.Builder()
                .url(itemUrl(HttpRequestType.PUT))
                .put(getAsJson().toRequestBody(MEDIA_TYPE_JSON))
                .build()
        }

        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            if (this.isSuccessful) {
                println("Create? ${createTputF} call successful")
                setPrimaryId(getFromJson(this.body?.source())!!)
            } else {
                println("Create? ${createTputF} call failed")
                println("Request: ${getAsJson()}")
                println("Response: ${this.body?.string()}")
                throw RuntimeException("Bad State")
            }
        }

        children.forEach{
            it.applyToServer(client)
        }
    }

    fun delete(client: OkHttpClient) {
        // TODO: Not really tested this
        val request = Request.Builder()
            .url(itemUrl(HttpRequestType.DELETE))
            .delete(getAsJson().toRequestBody(MEDIA_TYPE_JSON))
            .build()

        client.newCall(request).execute().apply {
            println("Item ${userVisibleName()} deleted successfully")
            println(this.request.url.toUrl().toString())
            println(this.body?.string())
        }
    }

    // Default that does nothing as there are no children to get
    open fun readFromServer(client: OkHttpClient) {

    }
}