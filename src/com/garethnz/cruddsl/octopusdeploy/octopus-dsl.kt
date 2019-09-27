package com.garethnz.cruddsl.octopusdeploy

// TODO: Test with a parent object providing properties for child?
// TODO: To gradle
// TODO: Swagger file processor

import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Spaces
// This is also the top level entry point
fun spaces(init: SpaceList.() -> Unit): SpaceList {
    val list = SpaceList()
    list.init()
    return list
}

class SpaceList : ListAPI<Array<Space>, Space>() {
    fun space(init: Space.() -> Unit) = initTag(Space(), init)

    override fun url(): String {
        return url
    }

    companion object {
        const val url = "http://localhost:1322/api/spaces/all"
    }

    override fun getJsonAdapter(): JsonAdapter<Array<Space>> {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter(Array<Space>::class.java)
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
    fun machines(init: MachineList.() -> Unit) = initTag(MachineList(), init)
    fun projects(init: ProjectList.() -> Unit) = initTag(ProjectList(), init)

    var Id : String? = null
    var Name : String? = null
    var Description : String? = null
    var IsDefault : Boolean? = false
    var TaskQueueStopped : Boolean? = false
    var SpaceManagersTeams : Array<String> = arrayOf()
    var SpaceManagersTeamMembers : Array<String> = arrayOf()
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Space

        if (Id != other.Id) return false
        if (Name != other.Name) return false
        if (Description != other.Description) return false
        if (IsDefault != other.IsDefault) return false
        if (TaskQueueStopped != other.TaskQueueStopped) return false
        if (!SpaceManagersTeams.contentEquals(other.SpaceManagersTeams)) return false
        if (!SpaceManagersTeamMembers.contentEquals(other.SpaceManagersTeamMembers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Id?.hashCode() ?: 0
        result = 31 * result + (Name?.hashCode() ?: 0)
        result = 31 * result + (Description?.hashCode() ?: 0)
        result = 31 * result + (IsDefault?.hashCode() ?: 0)
        result = 31 * result + (TaskQueueStopped?.hashCode() ?: 0)
        result = 31 * result + SpaceManagersTeams.contentHashCode()
        result = 31 * result + SpaceManagersTeamMembers.contentHashCode()
        return result
    }

    companion object {
        const val url = "http://localhost:1322/api/spaces/"
    }


}
