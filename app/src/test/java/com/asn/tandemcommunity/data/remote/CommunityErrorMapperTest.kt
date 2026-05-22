package com.asn.tandemcommunity.data.remote

import com.asn.tandemcommunity.domain.model.CommunityError
import io.ktor.client.engine.cio.FailToConnectException
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.ConnectException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException

class CommunityErrorMapperTest {

    @Test
    fun `maps UnknownHostException to Network`() {
        assertEquals(CommunityError.Network, UnknownHostException().toCommunityError())
    }

    @Test
    fun `maps ConnectException to Network`() {
        assertEquals(CommunityError.Network, ConnectException().toCommunityError())
    }

    @Test
    fun `maps FailToConnectException to Network`() {
        assertEquals(CommunityError.Network, FailToConnectException().toCommunityError())
    }

    @Test
    fun `maps UnresolvedAddressException to Network`() {
        assertEquals(CommunityError.Network, UnresolvedAddressException().toCommunityError())
    }
}
