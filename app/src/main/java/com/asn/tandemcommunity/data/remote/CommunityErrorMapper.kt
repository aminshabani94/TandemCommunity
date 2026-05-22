package com.asn.tandemcommunity.data.remote

import com.asn.tandemcommunity.domain.model.CommunityError
import io.ktor.client.engine.cio.FailToConnectException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.nio.channels.UnresolvedAddressException

internal fun Throwable.toCommunityError(): CommunityError = when (this) {
    is IOException,
    is UnresolvedAddressException,
    is FailToConnectException -> CommunityError.Network

    is ClientRequestException -> when (response.status.value) {
        404 -> CommunityError.HttpNotFound
        else -> CommunityError.HttpClient(response.status.value)
    }

    is ServerResponseException -> CommunityError.HttpServer(response.status.value)
    is SerializationException,
    is JsonConvertException -> CommunityError.Parse

    else -> CommunityError.Unknown
}
