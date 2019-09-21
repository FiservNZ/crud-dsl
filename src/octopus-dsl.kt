package com.garethnz.cruddsl.octopus

import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType

val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()


// Spaces
class SpaceList : ListAPI<Array<Space>, Space>() {
    fun space(init: Space.() -> Unit) = initTag(Space(), init)

    override fun url(): String {
        return url
    }

    companion object {
        const val url = "http://localhost:1322/api/spaces"
    }

    override fun getJsonAdapter(): JsonAdapter<Array<Space>> {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter<Array<Space>>(Array<Space>::class.java)
    }

    override fun getChildElements(): MutableList<Space> {
        return children.filterIsInstance<Space>().toMutableList()
    }

    override fun listOfChildren(sourceData: Array<Space>): Iterator<Space> {
        return sourceData.iterator()
    }
}

class Space : ItemApi<Space>() {
    fun environments(init: EnvironmentList.() -> Unit) = initTag(EnvironmentList(), init)

    var Id : String? = null
    var Name : String? = null
    var Description : String? = null
    var IsDefault : Boolean? = false
    var TaskQueueStopped : Boolean? = false
    // TODO: SpaceManagersTeams	[string]
    // TODO: SpaceManagersTeamMembers	[string]
    //var LastModifiedOn : String? = null // date-time
    //var LastModifiedBy : String? = null
    //var Links: Map<String,String>? = null
    override fun setPrimaryId(destinationPrimary: Space) {
        Id = destinationPrimary.Id
    }

    override fun primaryKeyEquals(target: Space): Boolean {
        return Id == target.Id
    }

    override fun itemUrl(type: HttpRequestType): String {
        return when(type) {
            HttpRequestType.POST -> url
            HttpRequestType.GET,HttpRequestType.PUT,HttpRequestType.DELETE -> url + Id
        }
    }

    override fun userVisibleName(): String {
        return Name!!
    }

    override fun getAsJson(): String {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<Space>(Space::class.java)
        return jsonAdapter.toJson(this)
    }

    companion object {
        const val url = "http://localhost:1322/api/spaces"
    }
}

// Environment
class EnvironmentList : ListAPI<Array<Environment>, Environment>() {
    fun environment(init: Environment.() -> Unit) = initTag(Environment(), init)

    override fun url(): String {
        return url
    }

    override fun getJsonAdapter(): JsonAdapter<Array<Environment>> {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter<Array<Environment>>(Array<Environment>::class.java)
    }

    override fun getChildElements(): MutableList<Environment> {
        return children.filterIsInstance<Environment>().toMutableList()
    }

    override fun listOfChildren(sourceData: Array<Environment>): Iterator<Environment> {
        return sourceData.iterator()
    }

    companion object {
        const val url = "http://localhost:1322/api/environments/all"
    }
}

data class ExtensionSettingsValues(val extensionId: String, val values: Array<String>)
class Environment() : ItemApi<Environment>() {
    var SpaceId : String? = null
    var ExtensionSettings: List<ExtensionSettingsValues>? = null
    var readOnly : Boolean = false
    var Name : String? = null
    var SortOrder : Int? = null
    //var LastModifiedBy:	String? = null
    var AllowDynamicInfrastructure:	Boolean? = null
    //var Links: Map<String,String>? = null
    var Id:	String? = null
    var UseGuidedFailure: Boolean? = null
    var Description : String? = null
    //var LastModifiedOn:	String? = null //($date-time)

    override fun itemUrl(type: HttpRequestType): String {
        return when(type) {
            HttpRequestType.POST -> url
            HttpRequestType.GET,HttpRequestType.PUT,HttpRequestType.DELETE -> url + Id
        }
    }

    companion object {
        const val url = "http://localhost:1322/api/environments/" // TODO: end slash not required for POST, is required when PUT/DELETE and including the id
    }

    override fun setPrimaryId(destinationPrimary: Environment) {
        Id = destinationPrimary.Id
    }

    override fun primaryKeyEquals(target: Environment): Boolean {
        return this.Id == target.Id
    }

    override fun userVisibleName(): String {
        return Name!!
    }

    override fun getAsJson(): String {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<Environment>(Environment::class.java)
        return jsonAdapter.toJson(this)
    }
}

fun spaces(init: SpaceList.() -> Unit): SpaceList {
    val list = SpaceList()
    list.init()
    return list
}