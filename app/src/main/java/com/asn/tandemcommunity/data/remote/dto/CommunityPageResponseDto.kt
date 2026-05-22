package com.asn.tandemcommunity.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityPageResponseDto(
    @SerialName("response") val response: List<CommunityMemberDto> = emptyList(),
    @SerialName("errorCode") val errorCode: String? = null,
    @SerialName("type") val type: String? = null,
)
