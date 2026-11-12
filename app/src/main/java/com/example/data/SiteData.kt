package com.example.data

enum class SiteId(
    val code: String,
    val title: String,
    val subhead: String,
    val facilityName: String,
    val dutyFactor: Double,
    val padOffset: Double
) {
    SJU(
        code = "SJU",
        title = "San Juan (SJU)",
        subhead = "Luis Muñoz Marín International Airport (TJSJ)",
        facilityName = "San Juan Radar / Transmitter Facility",
        dutyFactor = 619.71,
        padOffset = -0.99
    ),
    STT(
        code = "STT",
        title = "Saint Thomas (STT)",
        subhead = "Cyril E. King Airport (TIST)",
        facilityName = "St. Thomas Radar / Transmitter Facility",
        dutyFactor = 625.80,
        padOffset = 1.16
    )
}

enum class ChannelId(val code: String, val title: String) {
    CH_A("CH A", "Channel A"),
    CH_B("CH B", "Channel B")
}

data class ChannelBaseline(
    val siteId: SiteId,
    val channelId: ChannelId,
    val incidentCouplerDb: Double,
    val revCouplerDb: Double,
    val fwdReadingMw: Double,
    val fwdReadingDb: Double,
    val revReadingDbAbs: Double,
    val peakPowerOutputMw: Double,
    val vswr: Double
)

data class ChannelInputState(
    val incidentCouplerInput: String,
    val revCouplerInput: String,
    val fwdReadingMwInput: String,
    val fwdReadingDbInput: String,
    val revReadingDbAbsInput: String
)

object SiteDataDefaults {

    val SJU_CH_A = ChannelBaseline(
        siteId = SiteId.SJU,
        channelId = ChannelId.CH_A,
        incidentCouplerDb = 54.18,
        revCouplerDb = 44.87,
        fwdReadingMw = 1.81,
        fwdReadingDb = 2.55,
        revReadingDbAbs = 9.55,
        peakPowerOutputMw = 0.76,
        vswr = 1.21
    )

    val SJU_CH_B = ChannelBaseline(
        siteId = SiteId.SJU,
        channelId = ChannelId.CH_B,
        incidentCouplerDb = 54.71,
        revCouplerDb = 44.77,
        fwdReadingMw = 1.72,
        fwdReadingDb = 2.35,
        revReadingDbAbs = 9.55,
        peakPowerOutputMw = 0.82,
        vswr = 1.20
    )

    val STT_CH_A = ChannelBaseline(
        siteId = SiteId.STT,
        channelId = ChannelId.CH_A,
        incidentCouplerDb = 54.00,
        revCouplerDb = 45.10,
        fwdReadingMw = 1.92,
        fwdReadingDb = 2.83,
        revReadingDbAbs = 29.53,
        peakPowerOutputMw = 0.77,
        vswr = 1.10
    )

    val STT_CH_B = ChannelBaseline(
        siteId = SiteId.STT,
        channelId = ChannelId.CH_B,
        incidentCouplerDb = 54.42,
        revCouplerDb = 44.52,
        fwdReadingMw = 1.78,
        fwdReadingDb = 2.52,
        revReadingDbAbs = 5.50,
        peakPowerOutputMw = 0.79,
        vswr = 1.25
    )

    fun getBaseline(site: SiteId, channel: ChannelId): ChannelBaseline {
        return when (site) {
            SiteId.SJU -> when (channel) {
                ChannelId.CH_A -> SJU_CH_A
                ChannelId.CH_B -> SJU_CH_B
            }
            SiteId.STT -> when (channel) {
                ChannelId.CH_A -> STT_CH_A
                ChannelId.CH_B -> STT_CH_B
            }
        }
    }

    fun emptyInputFor(baseline: ChannelBaseline): ChannelInputState {
        return ChannelInputState(
            incidentCouplerInput = baseline.incidentCouplerDb.toString(),
            revCouplerInput = baseline.revCouplerDb.toString(),
            fwdReadingMwInput = "",
            fwdReadingDbInput = "",
            revReadingDbAbsInput = ""
        )
    }

    fun populatedInputFromBaseline(baseline: ChannelBaseline): ChannelInputState {
        return ChannelInputState(
            incidentCouplerInput = baseline.incidentCouplerDb.toString(),
            revCouplerInput = baseline.revCouplerDb.toString(),
            fwdReadingMwInput = baseline.fwdReadingMw.toString(),
            fwdReadingDbInput = baseline.fwdReadingDb.toString(),
            revReadingDbAbsInput = baseline.revReadingDbAbs.toString()
        )
    }
}
