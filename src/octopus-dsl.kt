package com.garethnz.cruddsl.octopus

import com.garethnz.cruddsl.base.Element
import com.garethnz.cruddsl.base.ListAPI
import com.garethnz.cruddsl.base.Tag
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request

val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()


// Environment
class EnvironmentList : ListAPI() {
    fun environment(init: Environment.() -> Unit) = initTag(Environment(), init)
    val url = "http://localhost:1322/api/environments/all"

    override fun applyToServer(client: OkHttpClient) {
        // This would compare children to the list returned from https://reqres.in/api/users
        val request = Request.Builder()
            .url(url)
            .build()

        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<Array<Environment>>(Array<Environment>::class.java)

        var response: Array<Environment>?
        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            //println( this.body?.string() )
            response = jsonAdapter.fromJson(this.body?.source()!!)

        }
        val childUsersToProcess = children.filterIsInstance<Environment>().toMutableList()

        println("Envs from server:")
        response?.let {
            it.forEach { item ->
                println(item)
            }
        }

        println("Envs from dsl:")
        childUsersToProcess.forEach { element: Element -> println(element) } //element.applyToServer(client) }
        // CREATE CHILD, LET CHILD Update the existing instance, DELETE user on server
    }
}

data class ExtensionSettingsValues(val extensionId: String, val values: Array<String>)
class Environment() : Tag() {
    var SpaceId : String? = null
    var ExtensionSettings: List<ExtensionSettingsValues>? = null
    var readOnly : Boolean = false
    var Name : String? = null
    var SortOrder : Int? = null
    var LastModifiedBy:	String? = null
    var AllowDynamicInfrastructure:	Boolean? = null
    var Links: Map<String,String>? = null
    var Id:	String? = null
    var UseGuidedFailure: Boolean? = null
    var Description : String? = null
    var LastModifiedOn:	String? = null //($date-time)

    // TODO: const val url = "https://reqres.in/api/users/"
    override fun applyToServer(client: OkHttpClient) {

    }
}

fun environments(init: EnvironmentList.() -> Unit): EnvironmentList {
    val environmentList = EnvironmentList()
    environmentList.init()
    return environmentList
}