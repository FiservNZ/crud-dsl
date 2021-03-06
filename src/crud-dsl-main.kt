import com.garethnz.cruddsl.octopusdeploy.Endpoint
import com.garethnz.cruddsl.octopusdeploy.Step
import com.garethnz.cruddsl.octopusdeploy.bashScript
import com.garethnz.cruddsl.octopusdeploy.spaces
import okhttp3.OkHttpClient


const val API_KEY = "API-AZACHJDLANF6JCYU5GCKJ0VOWCY"

// TODO: Maybe we track Ids just to confirm if things have changed... but we don't NEED them... mostly
fun octopusdsl() {
    fun result() =
        spaces {
            space {
                TaskQueueStopped = false
                Name = "Space 1"
                Id = "Spaces-1"
                Description = ""
                IsDefault = true
                SpaceManagersTeams = arrayOf("teams-administrators",
                                            "teams-managers",
                                            "teams-spacemanagers-Spaces-1")
                environments {
                    exhaustive = true
                    environment {
                        Id = "Environments-1" // Can't be set obv... How to get it to machine config?
                        Name = "Test"
                        Description = ""
                        SortOrder = 0
                        UseGuidedFailure = false
                        AllowDynamicInfrastructure = false
                        SpaceId = "Spaces-1"
                        ExtensionSettings = listOf()
                    }
                }

                machines {
                    machine {
                        Id = "Machines-1" // As usual can't be set
                        Endpoint = Endpoint(Thumbprint = "1234567890", Uri = "https://example:10933/")
                        EnvironmentIds = arrayOf("Environments-1")
                        MachinePolicyId = "MachinePolicies-1"
                        Name = "PretendTentacle"
                        Roles = arrayOf("Pretend")
                    }
                }

                projects {
                    project {
                        ProjectGroupId = "ProjectGroups-1"
                        LifecycleId = "Lifecycles-1"
                        Name = "TestProject"
                        //SpaceId = "Spaces-1"

                        deploymentprocess {
                            Steps = arrayOf(
                                Step(
                                    Name = "TestStep",
                                    PackageRequirement = "LetOctopusDecide",
                                    Properties = mapOf("Octopus.Action.TargetRoles" to "Pretend"),
                                    Condition = "Success",
                                    StartTrigger = "StartAfterPrevious",
                                    Actions = arrayOf(
                                        bashScript("TestStep", "echo \"Hello World OHH YUS!\"")
                                    )
                                )
                            )
                            LastSnapshotId = null
                            //SpaceId = "Spaces-1"
                        }
                    }
                }
            }
        }

//    println("Hello, World!")
//    fun result() =
//        html {
//            head {
//                title { +"XML encoding with Kotlin" }
//            }
//            body {
//                h1 { +"XML encoding with Kotlin" }
//                p { +"this format can be used as an alternative markup to XML" }
//
//                // an element with attributes and text content
//                a(href = "http://kotlinlang.org") { +"Kotlin" }
//
//                // mixed content
//                p {
//                    +"This is some"
//                    b { +"mixed" }
//                    +"text. For more see the"
//                    a(href = "http://kotlinlang.org") { +"Kotlin" }
//                    +"project"
//                }
//                p { +"some text" }
//            }
//        }
//
    println( result().toString() )

    val client = OkHttpClient.Builder().apply {
        addInterceptor {
            chain ->
                val original = chain.request()

                // Request customization: add request headers
                val requestBuilder = original.newBuilder()
                    .header("X-Octopus-ApiKey", API_KEY)

                val request = requestBuilder.build()
                chain.proceed(request)
        }
    }.build()
    result().applyToServer(client)
}

fun readOctopusDSL() {
    val result = spaces {

    }

    val client = OkHttpClient.Builder().apply {
        addInterceptor {
                chain ->
            val original = chain.request()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .header("X-Octopus-ApiKey", API_KEY)

            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }.build()
    val fromServer = result.readFromServer(client)
    println( fromServer.toString() )

}

fun main() {
    //readOctopusDSL()
    octopusdsl()
    // TODO: Read OctopusDSL from server
    //reqresdsl()
}