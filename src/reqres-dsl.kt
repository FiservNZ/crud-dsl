package com.example.reqres

import com.garethnz.cruddsl.base.Element
import com.garethnz.cruddsl.base.ListAPI
import com.garethnz.cruddsl.base.Tag
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

val MEDIA_TYPE_JSON = "application/json; charset=utf-8".toMediaType()

// https://reqres.in/
data class UsersResponse( val page: Int,
                         val per_page: Int,
                         val total: Int,
                         val total_pages: Int,
                         val data: Array<User>
                         ) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsersResponse

        if (page != other.page) return false
        if (per_page != other.per_page) return false
        if (total != other.total) return false
        if (total_pages != other.total_pages) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = page
        result = 31 * result + per_page
        result = 31 * result + total
        result = 31 * result + total_pages
        result = 31 * result + data.contentHashCode()
        return result
    }
}

class UserList : ListAPI() {
    fun user(init: User.() -> Unit) = initTag(User(), init)
    val url = "https://reqres.in/api/users"

    override fun applyToServer(client: OkHttpClient) {
        // This would compare children to the list returned from https://reqres.in/api/users
        val request = Request.Builder()
            .url(url)
            .build()

        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<UsersResponse>(UsersResponse::class.java)

        var usersResponse: UsersResponse?
        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            usersResponse = jsonAdapter.fromJson(this.body?.source()!!)
            //println( usersResponse )
        }
        val childUsersToProcess = children.filterIsInstance<User>().toMutableList()

        usersResponse?.let {
            it.data.forEach { user ->
                    // If it matches a child we want, then sync to it
                    val matchingChildUser = childUsersToProcess.firstOrNull { childUser -> childUser.primaryKeyEquals(user) }
                    if (matchingChildUser != null) {
                        matchingChildUser.applyToServer(client, user)
                        childUsersToProcess.remove(matchingChildUser)
                    }
                    else {

                        if (exhaustive) {
                            // TODO: Delete user from the server as it doesn't match something we want to add
                            user.delete(client)
                        }
                    }
            }
        }

        childUsersToProcess.forEach { element: Element -> element.applyToServer(client) }
        // CREATE CHILD, LET CHILD Update the existing instance, DELETE user on server
    }
}

class User : Tag() {
    var id: Int? = null
    var email: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var avatar: String? = null

    override fun applyToServer(client: OkHttpClient) {
        // TODO: If this isn't running as part of a LIST, targetUser needs to be GET'd
        applyToServer(client, null)
    }


    val url = "https://reqres.in/api/users/"
    /**
     * user is a user from the server's response that 'approximatelyEquals' returns true
     */
    fun applyToServer(client: OkHttpClient, targetUser: User?) {
        var createTputF = true
        // This would compare an instance of user from https://reqres.in/api/users/2 with the current object. CREATE / UPDATE as needed
        // Just take the ID of this and then do a PUT? if there are any other differences
        targetUser?.let {
            id = it.id
            createTputF = false
        }

        if (this.equals(targetUser)) {
            println("User $email already exists and is equal. Skipping")
            return // No Actions Needed
        }

        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<User>(User::class.java)

        val jsonUser = jsonAdapter.toJson(this)



        val request : Request
        if (createTputF) {
            request = Request.Builder()
                .url(url+id) // TODO does this type of API usually work? i.e. requesting the ID of a new object?
                .post(jsonUser.toRequestBody(MEDIA_TYPE_JSON))
                .build()
        } else {
            request = Request.Builder()
                .url(url+id)
                .put(jsonUser.toRequestBody(MEDIA_TYPE_JSON))
                .build()
        }

        client.newCall(request).execute().apply {
            println(this.request.url.toUrl().toString())
            println(this.body?.string())
        }
    }

    fun delete(client: OkHttpClient) {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<User>(User::class.java)

        val jsonUser = jsonAdapter.toJson(this)

        val request = Request.Builder()
                .url(url+id) // TODO does this type of API usually work? i.e. requesting the ID of a new object?
                .delete(jsonUser.toRequestBody(MEDIA_TYPE_JSON))
                .build()

        client.newCall(request).execute().apply {
            println("User $id deleted successfully")
            println(this.request.url.toUrl().toString())
            println(this.body?.string())
        }
    }

    fun primaryKeyEquals(other: User): Boolean {

        if (id == null)
            return false
        if (id != other.id)
            return false
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        // TODO: if (attributes.keys.map)
        if (id != other.id) return false
        if (first_name != other.first_name) return false
        if (last_name != other.last_name) return false
        if (email != other.email) return false
        if (avatar != other.avatar) return false
        return true
    }


}

fun users(init: UserList.() -> Unit): UserList {
    val userList = UserList()
    userList.init()
    return userList
}