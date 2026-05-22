package com.asn.tandemcommunity.domain.model

data class CommunityMember(
    val id: String,
    val name: String,
    val pictureUrl: String,
    val topic: String,
    val nativeLanguages: List<String>,
    val learnsLanguages: List<String>,
    val referenceCount: Int,
    val isLiked: Boolean,
) {
    val isNew: Boolean get() = referenceCount == 0
}
