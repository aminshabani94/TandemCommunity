package com.asn.tandemcommunity.data.remote.dto

import com.asn.tandemcommunity.domain.model.CommunityMember

fun CommunityMemberDto.toDomainModel(isLiked: Boolean = false): CommunityMember =
    CommunityMember(
        id = id.toString(),
        name = firstName.orEmpty(),
        pictureUrl = pictureUrl.orEmpty(),
        topic = topic.orEmpty(),
        nativeLanguages = natives,
        learnsLanguages = learns,
        referenceCount = referenceCnt,
        isLiked = isLiked,
    )
