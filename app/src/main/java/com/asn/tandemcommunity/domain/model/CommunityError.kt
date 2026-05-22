package com.asn.tandemcommunity.domain.model

/**
 * Stable, user-facing error categories for the community feed.
 * Map to strings in the presentation layer via [code].
 */
enum class CommunityErrorCode {
    NETWORK,
    HTTP_NOT_FOUND,
    HTTP_CLIENT,
    HTTP_SERVER,
    API_RESPONSE,
    PARSE,
    UNKNOWN,
}

sealed interface CommunityError {
    val code: CommunityErrorCode

    data object Network : CommunityError {
        override val code = CommunityErrorCode.NETWORK
    }

    data object HttpNotFound : CommunityError {
        override val code = CommunityErrorCode.HTTP_NOT_FOUND
    }

    data class HttpClient(val statusCode: Int) : CommunityError {
        override val code = CommunityErrorCode.HTTP_CLIENT
    }

    data class HttpServer(val statusCode: Int) : CommunityError {
        override val code = CommunityErrorCode.HTTP_SERVER
    }

    /** API returned a JSON body with a non-empty [apiErrorCode] (e.g. business error). */
    data class ApiResponse(val apiErrorCode: String) : CommunityError {
        override val code = CommunityErrorCode.API_RESPONSE
    }

    data object Parse : CommunityError {
        override val code = CommunityErrorCode.PARSE
    }

    data object Unknown : CommunityError {
        override val code = CommunityErrorCode.UNKNOWN
    }
}
