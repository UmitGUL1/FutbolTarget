package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Futbol Target", appName)
  }

  @Test
  fun `test create room does not crash`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.supabase.OnlineMatchRepository(context)
    val result = repository.createRoom()
    // When Supabase is not configured, it gracefully returns failure without crashing
    org.junit.Assert.assertNotNull(result)
  }

  @Test
  fun `test join room validation`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = com.example.data.supabase.OnlineMatchRepository(context)
    val invalidResult = repository.joinRoom("123")
    org.junit.Assert.assertTrue(invalidResult.isFailure)
    val validCode = com.example.data.supabase.RoomCodeGenerator.generateCode()
    val validResult = repository.joinRoom(validCode)
    org.junit.Assert.assertNotNull(validResult)
  }

  @Test
  fun `test supabase config safe handling`() {
    val isConfigured = com.example.data.supabase.SupabaseConfig.isConfigured()
    // Should safely return false or true without throwing exception
    org.junit.Assert.assertNotNull(isConfigured)
  }
}
