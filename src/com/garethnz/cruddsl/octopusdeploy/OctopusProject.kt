package com.garethnz.cruddsl.octopusdeploy

import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient

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
    fun deploymentprocess(init: DeploymentProcess.() -> Unit) = initTag(DeploymentProcess(), init)

    var Id: String? = null
    var VariableSetId: String? = null
    var DeploymentProcessId: String? = null
    var DiscreteChannelRelease = false
    var IncludedLibraryVariableSetIdS: String? = null
    var DefaultToSkipIfAlreadyInstalled = false
    var TenantedDeploymentMode = "Untenanted"
    var VersioningStrategy = VersioningStrategy("#{Octopus.Version.LastMajor}.#{Octopus.Version.LastMinor}.#{Octopus.Version.NextPatch}")
    var ReleaseCreationStrategy = ReleaseCreationStrategy(null,null,null) // IS THIS ACTUALLY THE DEFAULT?
    var Templates: List<String?> = arrayListOf()
    var AutoDeployReleaseOverrides: List<String>? = arrayListOf()
    var Name: String? = null
    var Slug: String? = null
    var Description: String = ""
    var IsDisabled = false
    var ProjectGroupId: String? = null
    var LifecycleId: String? = null
    var AutoCreateRelease = false
    var DefaultGuidedFailureMode = "EnvironmentDefault"
    var ProjectConnectivityPolicy = ProjectConnectivityPolicy("None", arrayListOf())
    var ClonedFromProjectId: String? = null
    var ExtensionSettings: Array<String> = arrayOf()
    var ReleaseNotesTemplate: String? = null
    var SpaceId: String? = null

    override fun setPrimaryId(destinationPrimary: Project) {
        Id = destinationPrimary.Id
    }

    override fun primaryKeyEquals(target: Project): Boolean {
        return Id == target.Id
    }

    override fun itemUrl(type: HttpRequestType): String {
        return when(type) {
            HttpRequestType.POST -> url
            HttpRequestType.GET, HttpRequestType.PUT, HttpRequestType.DELETE -> url + Id
        }
    }

    companion object {
        const val url = "http://localhost:1322/api/projects/"
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project

        if (Id != other.Id) return false
        if (VariableSetId != other.VariableSetId) return false
        if (DeploymentProcessId != other.DeploymentProcessId) return false
        if (DiscreteChannelRelease != other.DiscreteChannelRelease) return false
        if (IncludedLibraryVariableSetIdS != other.IncludedLibraryVariableSetIdS) return false
        if (DefaultToSkipIfAlreadyInstalled != other.DefaultToSkipIfAlreadyInstalled) return false
        if (TenantedDeploymentMode != other.TenantedDeploymentMode) return false
        if (VersioningStrategy != other.VersioningStrategy) return false
        if (ReleaseCreationStrategy != other.ReleaseCreationStrategy) return false
        if (Templates != other.Templates) return false
        if (AutoDeployReleaseOverrides != other.AutoDeployReleaseOverrides) return false
        if (Name != other.Name) return false
        if (Slug != other.Slug) return false
        if (Description != other.Description) return false
        if (IsDisabled != other.IsDisabled) return false
        if (ProjectGroupId != other.ProjectGroupId) return false
        if (LifecycleId != other.LifecycleId) return false
        if (AutoCreateRelease != other.AutoCreateRelease) return false
        if (DefaultGuidedFailureMode != other.DefaultGuidedFailureMode) return false
        if (ProjectConnectivityPolicy != other.ProjectConnectivityPolicy) return false
        if (ClonedFromProjectId != other.ClonedFromProjectId) return false
        if (!ExtensionSettings.contentEquals(other.ExtensionSettings)) return false
        if (ReleaseNotesTemplate != other.ReleaseNotesTemplate) return false
        if (SpaceId != other.SpaceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Id?.hashCode() ?: 0
        result = 31 * result + (VariableSetId?.hashCode() ?: 0)
        result = 31 * result + (DeploymentProcessId?.hashCode() ?: 0)
        result = 31 * result + DiscreteChannelRelease.hashCode()
        result = 31 * result + (IncludedLibraryVariableSetIdS?.hashCode() ?: 0)
        result = 31 * result + DefaultToSkipIfAlreadyInstalled.hashCode()
        result = 31 * result + TenantedDeploymentMode.hashCode()
        result = 31 * result + VersioningStrategy.hashCode()
        result = 31 * result + ReleaseCreationStrategy.hashCode()
        result = 31 * result + Templates.hashCode()
        result = 31 * result + (AutoDeployReleaseOverrides?.hashCode() ?: 0)
        result = 31 * result + (Name?.hashCode() ?: 0)
        result = 31 * result + (Slug?.hashCode() ?: 0)
        result = 31 * result + Description.hashCode()
        result = 31 * result + IsDisabled.hashCode()
        result = 31 * result + (ProjectGroupId?.hashCode() ?: 0)
        result = 31 * result + (LifecycleId?.hashCode() ?: 0)
        result = 31 * result + AutoCreateRelease.hashCode()
        result = 31 * result + DefaultGuidedFailureMode.hashCode()
        result = 31 * result + ProjectConnectivityPolicy.hashCode()
        result = 31 * result + (ClonedFromProjectId?.hashCode() ?: 0)
        result = 31 * result + ExtensionSettings.contentHashCode()
        result = 31 * result + (ReleaseNotesTemplate?.hashCode() ?: 0)
        result = 31 * result + (SpaceId?.hashCode() ?: 0)
        return result
    }


}

data class ProjectConnectivityPolicy (
    val SkipMachineBehavior: String,
    val TargetRoles: List<String?>,
    val AllowDeploymentsToNoTargets: Boolean = false,
    val ExcludeUnhealthyTargets: Boolean = false
)

data class VersioningStrategy (
    val Template: String
)

data class ReleaseCreationStrategy (
    val ChannelId: String?,
    val ReleaseCreationPackage: String?,
    val ReleaseCreationPackageStepId: String?
)

