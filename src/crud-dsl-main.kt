import com.example.reqres.users
import com.garethnz.cruddsl.octopus.ExtensionSettingsValues
import com.garethnz.cruddsl.octopus.environments
import okhttp3.OkHttpClient


val API_KEY = "API-8UUF2C3J5UZZXKLFJZLPVQPMKW"

fun octopusdsl() {
    fun result() =
        environments {
            exhaustive = true
            environment {
                Id = "Environments-1"
                Name = "Test"
                Description = ""
                SortOrder = 0
                UseGuidedFailure = false
                AllowDynamicInfrastructure = false
                SpaceId = "Spaces-1"
                ExtensionSettings = listOf<ExtensionSettingsValues>()
                Links = mapOf(
                    "Self" to "/api/Spaces-1/environments/Environments-1",
                    "Machines" to "/api/Spaces-1/environments/Environments-1/machines{?skip,take,partialName,roles,isDisabled,healthStatuses,commStyles,tenantIds,tenantTags,shellNames}",
                    "SinglyScopedVariableDetails" to "/api/Spaces-1/environments/Environments-1/singlyScopedVariableDetails",
                    "Metadata" to "/api/Spaces-1/environments/Environments-1/metadata"
                )
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

fun reqresdsl() {
    fun result() =
        users {
            exhaustive = true
            user {
                id = 1 // Things like primary keys will change. if it doesn't exist, the api to create a new one will not match this id
                email = "george.bluth@reqres.in"
                first_name = "George"
                last_name = "Bluth"
                avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg"
            }
            user {
                id = 6
                email = "tracey.ramos@newemail.com"
                first_name = "Tracey"
                last_name = "Ramos"
                avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/bigmancho/128.jpg"
            }
            user {
                id = 200
                email = "gareth@garethnz.com"
                first_name = "Gareth"
                last_name = "NZ"
                avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/bigmancho/128.jpg"
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
    result().applyToServer(OkHttpClient())
}

fun main(args : Array<String>) {
    octopusdsl()
    //reqresdsl()
}