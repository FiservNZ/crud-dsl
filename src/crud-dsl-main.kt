import com.example.reqres.*;
import okhttp3.OkHttpClient

fun main(args : Array<String>) {
    fun result() =
        users {
            user {
                id = 7
                email = "michael.lawson@reqres.in"
                first_name = "Michael"
                last_name = "Lawson"
                avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/follettkyle/128.jpg"
            }
            user {
                id = 8
                email = "lindsay.ferguson@reqres.in"
                first_name = "Lindsay"
                last_name = "Ferguson"
                avatar = "https://s3.amazonaws.com/uifaces/faces/twitter/araa3185/128.jpg"
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