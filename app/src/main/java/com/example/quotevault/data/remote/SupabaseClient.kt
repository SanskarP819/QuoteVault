package com.example.quotevault.data.remote

import android.util.Log
import com.example.quotevault.utils.Constants
import io.github.jan.supabase.SupabaseClient


import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseClientWrapper @Inject constructor() {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = Constants.SUPABASE_URL,
        supabaseKey = Constants.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }

    init {
        Log.d("SUPABASE_TEST", "Supabase REST URL")
    }

    val auth get() = client.auth
    val postgrest get() = client.postgrest
}
