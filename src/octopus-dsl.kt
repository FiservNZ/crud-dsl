package com.garethnz.cruddsl.octopus

// TODO: Project
// TODO: DeploymentProcess
// TODO: Test with a parent object providing properties for child?
// TODO: To gradle
// TODO: Swagger file processor

import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient

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
    fun machines(init: MachinesList.() -> Unit) = initTag(MachinesList(), init)
    fun projects(init: ProjectList.() -> Unit) = initTag(ProjectList(), init)

    var Id : String? = null
    var Name : String? = null
    var Description : String? = null
    var IsDefault : Boolean? = false
    var TaskQueueStopped : Boolean? = false
    var SpaceManagersTeams : Array<String>? = null
    var SpaceManagersTeamMembers : Array<String>? = null
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
        val jsonAdapter = moshi.adapter(Space::class.java)
        return jsonAdapter.toJson(this)
    }

    companion object {
        const val url = "http://localhost:1322/api/spaces/"
    }

    override fun applyToServer(client: OkHttpClient, target: Space?) {
        super.applyToServer(client, target)

        children.forEach{
            it.applyToServer(client)
        }
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
        val jsonAdapter = moshi.adapter(Environment::class.java)
        return jsonAdapter.toJson(this)
    }
}

// Machines
class MachinesList : ListAPI<Array<Machine>, Machine>() {
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
    var Endpoint: Endpoint? = null
    var EnvironmentIds: Array<String>? = null
    var HasLatestCalamari = true
    var HealthStatus = "Unknown"
    var Id: String? = null
    var IsDisabled = false
    var IsInProcess = true
    //Links: null
    var MachinePolicyId: String? = null
    var Name: String? = null
    var Roles: Array<String>? = null
    var StatusSummary: String? = null
    //TenantIds: []
    //TenantTags: []
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
            HttpRequestType.GET,HttpRequestType.PUT,HttpRequestType.DELETE -> url + Id
        }
    }

    companion object {
        const val url = "http://localhost:1322/api/machines/" // TODO: end slash not required for POST, is required when PUT/DELETE and including the id
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
}

// Project
class ProjectList : ListAPI<Array<Project>, Project>() {
    fun project(init: Project.() -> Unit) = initTag(Project(), init)
    override fun url(): String {
        return url
    }

    companion object {
        const val url = "http://localhost:1322/api/projects/all"
    }

    override fun getJsonAdapter(): JsonAdapter<Array<Project>> {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter(Array<Project>::class.java)
    }

    override fun getChildElements(): MutableList<Project> {
        return children.filterIsInstance<Project>().toMutableList()
    }

    override fun listOfChildren(sourceData: Array<Project>): Iterator<Project> {
        return sourceData.iterator()
    }
}

class Project : ItemApi<Project>() {
    var Id: String? = null
    var VariableSetId: String? = null
    var DeploymentProcessId: String? = null
    var DiscreteChannelRelease = false
    var IncludedLibraryVariableSetIdS: String? = null
    var DefaultToSkipIfAlreadyInstalled = false
    var TenantedDeploymentMode = "Untenanted"
    var VersioningStrategy: VersioningStrategy? = null
    var ReleaseCreationStrategy: ReleaseCreationStrategy? = null
    var Templates: List<String?>? = null
    var AutoDeployReleaseOverrides: List<String>? = null
    var Name: String? = null
    var Slug: String? = null
    var Description: String? = null
    var IsDisabled = false
    var ProjectGroupId: String? = null
    var LifecycleId: String? = null
    var AutoCreateRelease = false
    var DefaultGuidedFailureMode = "EnvironmentDefault"
    var ProjectConnectivityPolicy: ProjectConnectivityPolicy? = null
    var ClonedFromProjectId: String? = null
    var ExtensionSettings: Array<String>? = null
    var ReleaseNotesTemplate: String? = null
    var SpaceId: String? = null // TODO: Get from the parent Space object would be.. neat

    override fun setPrimaryId(destinationPrimary: Project) {
        Id = destinationPrimary.Id
    }

    override fun primaryKeyEquals(target: Project): Boolean {
        return Id == target.Id
    }

    override fun itemUrl(type: HttpRequestType): String {
        return when(type) {
            HttpRequestType.POST -> url
            HttpRequestType.GET,HttpRequestType.PUT,HttpRequestType.DELETE -> url + Id
        }
    }

    companion object {
        const val url = "http://localhost:1322/api/projects/" // TODO: end slash not required for POST, is required when PUT/DELETE and including the id
    }

    override fun userVisibleName(): String {
        return Name!!
    }

    override fun getAsJson(): String {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(Project::class.java)
        return jsonAdapter.toJson(this)
    }
}

data class ProjectConnectivityPolicy (
    val SkipMachineBehavior: String,
    val TargetRoles: List<String?>,
    val AllowDeploymentsToNoTargets: Boolean,
    val ExcludeUnhealthyTargets: Boolean
)

data class VersioningStrategy (
    val Template: String
)

data class ReleaseCreationStrategy (
    val ChannelId: String?,
    val ReleaseCreationPackage: String?,
    val ReleaseCreationPackageStepId: String?
)