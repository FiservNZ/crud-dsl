package com.garethnz.cruddsl.octopus

import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType

val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()


// Environment
class EnvironmentList : ListAPI<Array<Environment>, Environment>() {
    fun environment(init: Environment.() -> Unit) = initTag(Environment(), init)

    override fun url(): String {
        return url
    }

    override fun listOfChildren(sourceData: Array<Environment>): Iterator<Environment> {
        return sourceData.iterator()
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
    var LastModifiedBy:	String? = null
    var AllowDynamicInfrastructure:	Boolean? = null
    var Links: Map<String,String>? = null
    var Id:	String? = null
    var UseGuidedFailure: Boolean? = null
    var Description : String? = null
    var LastModifiedOn:	String? = null //($date-time)

    override val primaryIdForUrl: String
        get() = Id!!

    override fun url(): String {
        return url
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

fun environments(init: EnvironmentList.() -> Unit): EnvironmentList {
    val environmentList = EnvironmentList()
    environmentList.init()
    return environmentList
}