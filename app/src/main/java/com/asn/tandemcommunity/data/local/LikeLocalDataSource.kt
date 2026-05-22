package com.asn.tandemcommunity.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.likesDataStore by preferencesDataStore(name = "likes")

class LikeLocalDataSource(context: Context) {

    private val appContext = context.applicationContext
    private val likedKey = stringSetPreferencesKey("liked_member_ids")

    fun observeLikedIds(): Flow<Set<String>> =
        appContext.likesDataStore.data.map { prefs -> prefs[likedKey] ?: emptySet() }

    suspend fun toggle(memberId: String) {
        appContext.likesDataStore.edit { prefs ->
            val current = prefs[likedKey] ?: emptySet()
            prefs[likedKey] = if (memberId in current) current - memberId else current + memberId
        }
    }
}
