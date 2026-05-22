package com.asn.tandemcommunity.domain.util

import com.asn.tandemcommunity.domain.model.CommunityError

sealed interface DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>
    data class Failure(val error: CommunityError) : DomainResult<Nothing>
}