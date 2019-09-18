package com.example.reqres

import com.garethnz.cruddsl.base.Element
import com.garethnz.cruddsl.base.ItemApi
import com.garethnz.cruddsl.base.ListAPI
import com.garethnz.cruddsl.base.Tag
import com.garethnz.cruddsl.octopus.Environment
import com.squareup.moshi.JsonAdapter
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

class UserList : ListAPI<UsersResponse, User>() {
    fun user(init: User.() -> Unit) = initTag(User(), init)
    companion object {
        val url = "https://reqres.in/api/users"
    }

    override fun url(): String {
        return url
    }

    override fun getJsonAdapter(): JsonAdapter<UsersResponse> {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        return moshi.adapter<UsersResponse>(UsersResponse::class.java)
    }

    override fun getChildElements(): MutableList<User> {
        return children.filterIsInstance<User>().toMutableList()
    }

    override fun listOfChildren(sourceData: UsersResponse): Iterator<User> {
        return sourceData.data.iterator()
    }
}

class User : ItemApi<User>() {
    var id: Int? = null
    var email: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var avatar: String? = null



    companion object {
        val url = "https://reqres.in/api/users/"
    }

    override fun setPrimaryId(destinationPrimary: User) {
        this.id = destinationPrimary.id
    }

    override fun url(): String {
        return url
    }

    override val primaryIdForUrl: String
        get() = id.toString()

    override fun userVisibleName(): String {
        return email!!
    }

    override fun getAsJson(): String {
        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<User>(User::class.java)

        return jsonAdapter.toJson(this)
    }

    override fun primaryKeyEquals(other: User): Boolean {

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