package com.garethnz.cruddsl.octopusdeploy

import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

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
        return moshi.adapter(Array<Environment>::class.java)
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
            HttpRequestType.GET, HttpRequestType.PUT, HttpRequestType.DELETE -> url + Id
        }
    }

    companion object {
        const val url = "http://localhost:1322/api/environments/"
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
        val jsonAdapter = moshi.adapter(Environment::class.java)
        return jsonAdapter.toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Environment

        if (SpaceId != other.SpaceId) return false
        if (ExtensionSettings != other.ExtensionSettings) return false
        if (Name != other.Name) return false
        if (SortOrder != other.SortOrder) return false
        if (AllowDynamicInfrastructure != other.AllowDynamicInfrastructure) return false
        if (Id != other.Id) return false
        if (UseGuidedFailure != other.UseGuidedFailure) return false
        if (Description != other.Description) return false

        return true
    }

    override fun hashCode(): Int {
        var result = SpaceId?.hashCode() ?: 0
        result = 31 * result + (ExtensionSettings?.hashCode() ?: 0)
        result = 31 * result + (Name?.hashCode() ?: 0)
        result = 31 * result + (SortOrder ?: 0)
        result = 31 * result + (AllowDynamicInfrastructure?.hashCode() ?: 0)
        result = 31 * result + (Id?.hashCode() ?: 0)
        result = 31 * result + (UseGuidedFailure?.hashCode() ?: 0)
        result = 31 * result + (Description?.hashCode() ?: 0)
        return result
    }


}
