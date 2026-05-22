package com.asn.tandemcommunity.data.remote

import com.asn.tandemcommunity.data.remote.dto.CommunityMemberDto
import com.asn.tandemcommunity.data.remote.dto.CommunityPageResponseDto
import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.util.DomainResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess

class CommunityApi(private val client: HttpClient) {

    suspend fun getCommunity(page: Int): DomainResult<List<CommunityMemberDto>> =
        try {
            val response = client.get(NetworkConstants.communityPageUrl(page))
            if (!response.status.isSuccess()) {
                DomainResult.Failure(response.toHttpError())
            } else {
                val pageResponse = response.body<CommunityPageResponseDto>()
                val errorCode = pageResponse.errorCode
                if (!errorCode.isNullOrBlank()) {
                    DomainResult.Failure(CommunityError.ApiResponse(errorCode))
                } else {
                    DomainResult.Success(pageResponse.response)
                }
            }
        } catch (e: Exception) {
            DomainResult.Failure(e.toCommunityError())
        }

    private fun HttpResponse.toHttpError(): CommunityError = when (status.value) {
        404 -> CommunityError.HttpNotFound
        in 500..599 -> CommunityError.HttpServer(status.value)
        else -> CommunityError.HttpClient(status.value)
    }
}
