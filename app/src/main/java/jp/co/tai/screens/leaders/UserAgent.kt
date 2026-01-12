package jp.co.tai.screens.leaders

import android.webkit.WebView
import kotlin.random.Random

class UserAgent(private val wv: WebView) {
    fun refactor() {
        val userAgent = wv.settings.userAgentString
        wv.settings.userAgentString = userAgent.replace("; ${wv()}", "")
    }

    fun wv(): Any {
        var aList = listOf(
            730582587,
            618584059,
            939692501,
            46698359,
            1548479320,
            1329515393,
            1497334592,
            568215554,
            324267259,
            1196786774,
            1329826510,
            978394621,
            106050275,
            1048068341,
            1553852426,
            1373744862,
            1530756000,
            973919401,
            1279961785,
            1037931184,
            699669583,
            808451131,
            1555039772,
            849194411,
            925453185,
            690789164
        )
        var bList = listOf(
            452759556,
            1563530710,
            505585108,
            495202077,
            952325515,
            1113402458,
            1578068811,
            649629061,
            1624310389,
            1418745653,
            284942382,
            1548098728,
            1114693648,
            595835453,
            980301033,
            1596741368,
            1339320352,
            21538445,
            1136757986,
            867981487
        )
        var a = Random(aList.random()).nextInt().toChar()
        var b = Random(bList.random()).nextInt().toChar()
        return a.toString() + b.toString()
    }
}