package com.asn.tandemcommunity.data.remote

import com.asn.tandemcommunity.data.remote.dto.CommunityPageResponseDto
import com.asn.tandemcommunity.domain.model.CommunityError
import com.asn.tandemcommunity.domain.util.DomainResult
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunityApiTest {

    @Test
    fun `getCommunity returns success when errorCode is absent`() = runTest {
        val body = Json.encodeToString(
            CommunityPageResponseDto.serializer(),
            CommunityPageResponseDto(response = emptyList(), errorCode = null),
        )
        val api = createApi(body, HttpStatusCode.OK)

        val result = api.getCommunity(1)

        assertTrue(result is DomainResult.Success)
        assertEquals(0, (result as DomainResult.Success).data.size)
    }

    @Test
    fun `getCommunity returns ApiResponse error when errorCode is present`() = runTest {
        val body = """{"response":[],"errorCode":"ERR","type":"error"}"""
        val api = createApi(body, HttpStatusCode.OK)

        val result = api.getCommunity(99)

        assertTrue(result is DomainResult.Failure)
        val error = (result as DomainResult.Failure).error
        assertTrue(error is CommunityError.ApiResponse)
        assertEquals("ERR", (error as CommunityError.ApiResponse).apiErrorCode)
    }

    @Test
    fun `getCommunity returns HttpNotFound on 404`() = runTest {
        val api = createApi("not found", HttpStatusCode.NotFound)

        val result = api.getCommunity(5)

        assertTrue(result is DomainResult.Failure)
        assertEquals(CommunityError.HttpNotFound, (result as DomainResult.Failure).error)
    }

    private fun createApi(responseBody: String, status: HttpStatusCode): CommunityApi {
        val engine = MockEngine {
            respond(
                content = responseBody,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val client = HttpClient(engine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
        return CommunityApi(client)
    }
}
