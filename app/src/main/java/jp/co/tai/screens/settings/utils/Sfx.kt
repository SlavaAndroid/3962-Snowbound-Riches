package jp.co.tai.screens.settings.utils

import jp.co.tai.R

enum class Sfx (val resId: Int, val defaultVol: Float = 1f, val defaultRate: Float = 1f) {
    COLLECT(R.raw.collect),
    BONUS(R.raw.bonus)
}