package com.example

import com.example.data.ChannelId
import com.example.data.RfCalculator
import com.example.data.SiteDataDefaults
import com.example.data.SiteId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMwToDbConversion() {
        val db1 = RfCalculator.mwToDb(1.81)
        assertEquals(2.58, db1, 0.05)

        val db2 = RfCalculator.mwToDb(1.92)
        assertEquals(2.83, db2, 0.05)
    }

    @Test
    fun testDbToMwConversion() {
        val mw1 = RfCalculator.dbToMw(2.55)
        assertEquals(1.80, mw1, 0.05)

        val mw2 = RfCalculator.dbToMw(2.83)
        assertEquals(1.92, mw2, 0.05)
    }

    @Test
    fun testSjuChannelABaseline() {
        val b = SiteDataDefaults.SJU_CH_A
        val res = RfCalculator.calculate(
            incidentCouplerDb = b.incidentCouplerDb,
            revCouplerDb = b.revCouplerDb,
            fwdReadingDb = b.fwdReadingDb,
            fwdReadingMw = b.fwdReadingMw,
            revReadingDbAbs = b.revReadingDbAbs,
            dutyFactor = SiteId.SJU.dutyFactor,
            padOffset = SiteId.SJU.padOffset
        )
        // Peak Power should be 0.76 MW
        assertEquals(0.76, res.peakPowerMw, 0.01)
        // VSWR should be 1.21
        assertEquals(1.21, res.vswr, 0.01)
    }

    @Test
    fun testSjuChannelBBaseline() {
        val b = SiteDataDefaults.SJU_CH_B
        val res = RfCalculator.calculate(
            incidentCouplerDb = b.incidentCouplerDb,
            revCouplerDb = b.revCouplerDb,
            fwdReadingDb = b.fwdReadingDb,
            fwdReadingMw = b.fwdReadingMw,
            revReadingDbAbs = b.revReadingDbAbs,
            dutyFactor = SiteId.SJU.dutyFactor,
            padOffset = SiteId.SJU.padOffset
        )
        // Peak Power should be 0.82 MW
        assertEquals(0.82, res.peakPowerMw, 0.01)
        // VSWR should be 1.20
        assertEquals(1.20, res.vswr, 0.01)
    }

    @Test
    fun testSttChannelABaseline() {
        val b = SiteDataDefaults.STT_CH_A
        val res = RfCalculator.calculate(
            incidentCouplerDb = b.incidentCouplerDb,
            revCouplerDb = b.revCouplerDb,
            fwdReadingDb = b.fwdReadingDb,
            fwdReadingMw = b.fwdReadingMw,
            revReadingDbAbs = b.revReadingDbAbs,
            dutyFactor = SiteId.STT.dutyFactor,
            padOffset = SiteId.STT.padOffset
        )
        // Peak Power should be 0.77 MW
        assertEquals(0.77, res.peakPowerMw, 0.01)
    }

    @Test
    fun testSttChannelBBaseline() {
        val b = SiteDataDefaults.STT_CH_B
        val res = RfCalculator.calculate(
            incidentCouplerDb = b.incidentCouplerDb,
            revCouplerDb = b.revCouplerDb,
            fwdReadingDb = b.fwdReadingDb,
            fwdReadingMw = b.fwdReadingMw,
            revReadingDbAbs = b.revReadingDbAbs,
            dutyFactor = SiteId.STT.dutyFactor,
            padOffset = SiteId.STT.padOffset
        )
        // Peak Power should be 0.79 MW
        assertEquals(0.79, res.peakPowerMw, 0.01)
        // VSWR should be 1.25
        assertEquals(1.25, res.vswr, 0.01)
    }
}
