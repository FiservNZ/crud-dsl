package com.example.reqres

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

// https://reqres.in/
interface Element {
    fun render(builder: StringBuilder, indent: String)
    fun applyToServer(client: OkHttpClient)
}

abstract class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

@DslMarker
annotation class ReqResUserMarker

@ReqResUserMarker
abstract class Tag(val name: String) : Element {
    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, String>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent<$name${renderAttributes()}>\n")
        for (c in children) {
            c.render(builder, indent + "  ")
        }
        builder.append("$indent</$name>\n")
    }

    private fun renderAttributes(): String {
        val builder = StringBuilder()
        for ((attr, value) in attributes) {
            builder.append(" $attr=\"$value\"")
        }
        return builder.toString()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

class UserList : Tag("userlist") {
    fun user(init: User.() -> Unit) = initTag(User(), init)
    val url = "https://reqres.in/api/users"
    override fun applyToServer(client: OkHttpClient) {
        // This would compare children to the list returned from https://reqres.in/api/users
        var request = Request.Builder()
            .url(url)
            .build();

        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            println(this.body?.string())
        }

        children.forEach { element: Element -> element.applyToServer(client) }
        // CREATE CHILD, LET CHILD Update the existing instance, DELETE user on server
    }
}

class User : Tag("user") {
    var id: Int
        get() = attributes["id"]!!.toInt()
        set(value) {
            attributes["id"] = value.toString()
        }
    var email: String
        get() = attributes["email"]!!
        set(value) {
            attributes["email"] = value
        }
    var first_name: String
        get() = attributes["first_name"]!!
        set(value) {
            attributes["first_name"] = value
        }
    var last_name: String
        get() = attributes["last_name"]!!
        set(value) {
            attributes["last_name"] = value
        }
    var avatar: String
        get() = attributes["avatar"]!!
        set(value) {
            attributes["avatar"] = value
        }

    val url = "https://reqres.in/api/users/"
    override fun applyToServer(client: OkHttpClient) {
        // This would compare an instance of user from https://reqres.in/api/users/2 with the current object. CREATE / UPDATE as needed
        var request = Request.Builder()
            .url(url+id)
            .build();

        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            println(this.body?.string())
        }
    }
}

fun users(init: UserList.() -> Unit): UserList {
    val userList = UserList()
    userList.init()
    return userList
}