package com.asn.tandemcommunity.test

import com.asn.tandemcommunity.data.remote.dto.CommunityMemberDto
import com.asn.tandemcommunity.domain.model.CommunityMember

fun sampleMember(
    id: String = "1",
    isLiked: Boolean = false,
    referenceCount: Int = 0,
) = CommunityMember(
    id = id,
    name = "Member $id",
    pictureUrl = "https://example.com/pic.png",
    topic = "Hello",
    nativeLanguages = listOf("en"),
    learnsLanguages = listOf("de"),
    referenceCount = referenceCount,
    isLiked = isLiked,
)

fun sampleMemberDto(
    id: Long = 1L,
    referenceCnt: Int = 0,
) = CommunityMemberDto(
    id = id,
    firstName = "Tobi",
    pictureUrl = "https://example.com/pic.png",
    topic = "Topic",
    natives = listOf("de"),
    learns = listOf("en"),
    referenceCnt = referenceCnt,
)

fun sampleMembers(count: Int): List<CommunityMember> =
    (1..count).map { sampleMember(it.toString()) }
