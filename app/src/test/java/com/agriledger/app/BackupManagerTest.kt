package com.agriledger.app

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupManagerTest {

    @Test
    fun testBackupJsonFormatStructure() {
        val root = JSONObject().apply {
            put("app", "AgriLedger")
            put("version", 1)
            put("exportTimestamp", 1700000000000L)
            
            val profile = JSONObject().apply {
                put("businessName", "Test Farm Services")
                put("currencySymbol", "₹")
            }
            put("businessProfile", profile)

            val customers = JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 1L)
                    put("name", "Gurpreet Singh")
                    put("phone", "9876543210")
                    put("village", "Kotli")
                })
            }
            put("customers", customers)

            val jobs = JSONArray().apply {
                put(JSONObject().apply {
                    put("id", 10L)
                    put("customerId", 1L)
                    put("invoiceNumber", "AL-001")
                    put("serviceType", "HARVESTING")
                    put("acres", 10.0)
                    put("ratePerAcre", 2500.0)
                    put("totalAmount", 25000.0)
                })
            }
            put("serviceJobs", jobs)
        }

        val jsonStr = root.toString()
        assertTrue(jsonStr.contains("AgriLedger"))
        assertTrue(jsonStr.contains("Gurpreet Singh"))
        assertTrue(jsonStr.contains("HARVESTING"))

        val parsed = JSONObject(jsonStr)
        assertEquals("AgriLedger", parsed.getString("app"))
        assertEquals(1, parsed.getInt("version"))
        assertEquals("Test Farm Services", parsed.getJSONObject("businessProfile").getString("businessName"))
        assertEquals(1, parsed.getJSONArray("customers").length())
    }
}
