package com.garethnz.cruddsl.octopusdeploy

import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MachineList : ListAPI<Array<Machine>, Machine>() {
    fun machine(init: Machine.() -> Unit) = initTag(Machine(), init)

    override fun url(): String {
        return url
    }

    override fun getJsonAdapter(): JsonAdapter<Array<Machine>> {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter(Array<Machine>::class.java)
    }

    override fun getChildElements(): MutableList<Machine> {
        return children.filterIsInstance<Machine>().toMutableList()
    }

    override fun listOfChildren(sourceData: Array<Machine>): Iterator<Machine> {
        return sourceData.iterator()
    }

    companion object {
        const val url = "http://localhost:1322/api/machines/all"
    }
}
data class Endpoint(val Id: String? = null, val CommunicationStyle: String = "TentaclePassive", val Thumbprint:String, val Uri: String)

class Machine : ItemApi<Machine>() {
    // TODO: Remove anything that octopus updates from the Tentacle and is not human-entered
    var Endpoint: Endpoint? = null
    var EnvironmentIds: Array<String> = arrayOf()
    //var HasLatestCalamari = false
    //var HealthStatus = "Unavailable"
    var Id: String? = null
    var IsDisabled = false
    //var IsInProcess = false
    //Links: null
    var MachinePolicyId: String? = null
    var Name: String? = null
    var Roles: Array<String> = arrayOf()
    //var Status: String? = null
    //var StatusSummary: String? = null
    var TenantIds: Array<String> = arrayOf()
    var TenantTags: Array<String> = arrayOf()
    var TenantedDeploymentParticipation = "Untenanted"
    var OperatingSystem : String? = null
    var ShellName : String? = null
    var ShellVersion : String? = null

    override fun setPrimaryId(destinationPrimary: Machine) {
        Id = destinationPrimary.Id
    }

    override fun primaryKeyEquals(target: Machine): Boolean {
        return Id == target.Id
    }

    override fun itemUrl(type: HttpRequestType): String {
        return when(type) {
            HttpRequestType.POST -> url
            HttpRequestType.GET, HttpRequestType.PUT, HttpRequestType.DELETE -> url + Id
        }
    }

    companion object {
        const val url = "http://localhost:1322/api/machines/"
    }

    override fun userVisibleName(): String {
        return Name!!
    }

    override fun getAsJson(): String {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(Machine::class.java)
        return jsonAdapter.toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Machine

        if (Endpoint != other.Endpoint) return false
        if (!EnvironmentIds.contentEquals(other.EnvironmentIds)) return false
        //if (HasLatestCalamari != other.HasLatestCalamari) return false
        if (Id != other.Id) return false
        if (IsDisabled != other.IsDisabled) return false
        if (MachinePolicyId != other.MachinePolicyId) return false
        if (Name != other.Name) return false
        if (!Roles.contentEquals(other.Roles)) return false
        if (!TenantIds.contentEquals(other.TenantIds)) return false
        if (!TenantTags.contentEquals(other.TenantTags)) return false
        if (TenantedDeploymentParticipation != other.TenantedDeploymentParticipation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Endpoint?.hashCode() ?: 0
        result = 31 * result + EnvironmentIds.contentHashCode()
        //result = 31 * result + HasLatestCalamari.hashCode()
        result = 31 * result + (Id?.hashCode() ?: 0)
        result = 31 * result + IsDisabled.hashCode()
        result = 31 * result + (MachinePolicyId?.hashCode() ?: 0)
        result = 31 * result + (Name?.hashCode() ?: 0)
        result = 31 * result + Roles.contentHashCode()
        result = 31 * result + TenantIds.contentHashCode()
        result = 31 * result + TenantTags.contentHashCode()
        result = 31 * result + TenantedDeploymentParticipation.hashCode()
        return result
    }


}
