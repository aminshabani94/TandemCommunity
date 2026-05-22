package com.asn.tandemcommunity.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityMemberDto(
    @SerialName("id") val id: Long,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("pictureUrl") val pictureUrl: String? = null,
    @SerialName("topic") val topic: String? = null,
    @SerialName("natives") val natives: List<String> = emptyList(),
    @SerialName("learns") val learns: List<String> = emptyList(),
    @SerialName("referenceCnt") val referenceCnt: Int = 0,
)
