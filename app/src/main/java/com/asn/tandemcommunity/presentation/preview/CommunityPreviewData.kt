package com.asn.tandemcommunity.presentation.preview

import com.asn.tandemcommunity.domain.model.CommunityMember

val previewCommunityMemberNew = CommunityMember(
    id = "1",
    name = "Tobi",
    pictureUrl = "https://tandem2019.web.app/img/pic1.png",
    topic = "I can help you learn English and Spanish.",
    nativeLanguages = listOf("de", "ja", "it"),
    learnsLanguages = listOf("en", "pt"),
    referenceCount = 0,
)

val previewCommunityMemberExisting = CommunityMember(
    id = "2",
    name = "Luca",
    pictureUrl = "https://tandem2019.web.app/img/pic1.png",
    topic = "I can help you learn English and Spanish.",
    nativeLanguages = listOf("de", "ko", "it"),
    learnsLanguages = listOf("en", "pt"),
    referenceCount = 10,
)

val previewCommunityMembers = listOf(
    previewCommunityMemberNew,
    previewCommunityMemberExisting,
)
