package com.garethnz.cruddsl.octopusdeploy

import com.garethnz.cruddsl.base.ItemApi
import okhttp3.OkHttpClient
import okhttp3.Request

// Deployment Process
class DeploymentProcess(var ProjectId: String?) : ItemApi<DeploymentProcess>() {
    private val Id: String
        get() = "deploymentprocess-${ProjectId}"
    var Steps: Array<Step> = arrayOf()
    var Version: Long = 1 // TODO: Don't use as property, either 1... or use existing #
    var LastSnapshotId: String? = null
    var SpaceId: String? = null

    override fun setPrimaryId(destinationPrimary: DeploymentProcess) {
        if (Id != destinationPrimary.Id) {
            throw RuntimeException("Can't set the id of a DeploymentProcess")
        }
    }

    override fun primaryKeyEquals(target: DeploymentProcess): Boolean {
        return Id == target.Id
    }

    override fun itemUrl(type: HttpRequestType): String {
        return when(type) {
            HttpRequestType.POST -> url
            HttpRequestType.GET, HttpRequestType.PUT, HttpRequestType.DELETE -> url + Id
        }
    }

    companion object {
        const val url = "http://localhost:1322/api/deploymentprocesses/" // TODO: end slash not required for POST, is required when PUT/DELETE and including the id
    }

    override fun userVisibleName(): String {
        return Id!!
    }

    // TODO: Because parent is not a list, it can't get the target object for us
    override fun applyToServer(client: OkHttpClient) {
        val request = Request.Builder()
            .url(itemUrl(HttpRequestType.GET))
            .build()

        val response : DeploymentProcess?
        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            response = getFromJson(this.body?.source()!!)
            response?.let {
                println("Existing item found, updating")
                Version = response.Version
                super.applyToServer(client, response)
                return
            }
        }

        println("No existing item found, creating")
        super.applyToServer(client)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeploymentProcess

        if (ProjectId != other.ProjectId) return false
        if (!Steps.contentEquals(other.Steps)) return false
        if (SpaceId != other.SpaceId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ProjectId?.hashCode() ?: 0
        result = 31 * result + Steps.contentHashCode()
        result = 31 * result + (SpaceId?.hashCode() ?: 0)
        return result
    }


}

data class Step (
    val Id: String? = null,
    val Name: String,
    val Properties: Map<String,String>,
    val Condition: String,
    val StartTrigger: String,
    val PackageRequirement: String,
    val Actions: Array<Action>


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Step

        if (Name != other.Name) return false
        if (Properties != other.Properties) return false
        if (Condition != other.Condition) return false
        if (StartTrigger != other.StartTrigger) return false
        if (PackageRequirement != other.PackageRequirement) return false
        if (!Actions.contentEquals(other.Actions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Name.hashCode()
        result = 31 * result + Properties.hashCode()
        result = 31 * result + Condition.hashCode()
        result = 31 * result + StartTrigger.hashCode()
        result = 31 * result + PackageRequirement.hashCode()
        result = 31 * result + Actions.contentHashCode()
        return result
    }
}

data class Action (
    val Id: String? = null,
    val ActionType: String,
    val Name: String,
    val Environments: Array<String?>,
    val ExcludedEnvironments: Array<String?>,
    val Channels: Array<String?>,
    val TenantTags: Array<String?>,
    val Properties: Map<String,String>,
    val Packages: Array<String?>,
    val IsDisabled: Boolean,
    val CanBeUsedForProjectVersioning: Boolean,
    val IsRequired: Boolean,
    val Links: Map<String,String>? = null,
    val WorkerPoolId: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Action

        if (ActionType != other.ActionType) return false
        if (Name != other.Name) return false
        if (!Environments.contentEquals(other.Environments)) return false
        if (!ExcludedEnvironments.contentEquals(other.ExcludedEnvironments)) return false
        if (!Channels.contentEquals(other.Channels)) return false
        if (!TenantTags.contentEquals(other.TenantTags)) return false
        if (Properties != other.Properties) return false
        if (!Packages.contentEquals(other.Packages)) return false
        if (IsDisabled != other.IsDisabled) return false
        if (CanBeUsedForProjectVersioning != other.CanBeUsedForProjectVersioning) return false
        if (IsRequired != other.IsRequired) return false
        if (WorkerPoolId != other.WorkerPoolId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ActionType.hashCode()
        result = 31 * result + Name.hashCode()
        result = 31 * result + Environments.contentHashCode()
        result = 31 * result + ExcludedEnvironments.contentHashCode()
        result = 31 * result + Channels.contentHashCode()
        result = 31 * result + TenantTags.contentHashCode()
        result = 31 * result + Properties.hashCode()
        result = 31 * result + Packages.contentHashCode()
        result = 31 * result + IsDisabled.hashCode()
        result = 31 * result + CanBeUsedForProjectVersioning.hashCode()
        result = 31 * result + IsRequired.hashCode()
        result = 31 * result + (WorkerPoolId?.hashCode() ?: 0)
        return result
    }
}