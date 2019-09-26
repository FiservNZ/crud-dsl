package com.garethnz.cruddsl.octopusdeploy

fun bashScript(name: String, ScriptBody: String) : Action {
    return Action(
        Name = name,
        ActionType = "Octopus.Script",
        IsDisabled = false,
        CanBeUsedForProjectVersioning = false,
        IsRequired = false,
        WorkerPoolId = null,
        Environments = arrayOf(),
        ExcludedEnvironments = arrayOf(),
        Channels = arrayOf(),
        TenantTags = arrayOf(),
        Packages = arrayOf(),
        Properties = mapOf(
            "Octopus.Action.RunOnServer" to "false",
            "Octopus.Action.Script.ScriptSource" to "Inline",
            "Octopus.Action.Script.Syntax" to "Bash",
            "Octopus.Action.Script.ScriptBody" to ScriptBody
        )
    )
}