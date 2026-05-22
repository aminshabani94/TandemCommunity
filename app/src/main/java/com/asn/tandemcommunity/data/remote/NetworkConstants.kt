package com.asn.tandemcommunity.data.remote

object NetworkConstants {
    const val BASE_URL = "https://tandem2019.web.app/api"

    fun communityPageUrl(page: Int): String = "$BASE_URL/community_$page.json"
}
