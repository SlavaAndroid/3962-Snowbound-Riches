package jp.co.tai.screens.settings.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.times

object SoundEffectPlayer {
    private var soundPool: SoundPool? = null
    private val sfxToId = mutableMapOf<Sfx, Int>()
    private val idToSfx = mutableMapOf<Int, Sfx>()
    private val loadedIds = mutableSetOf<Int>()
    private val pendingPlays = mutableMapOf<Sfx, MutableList<() -> Unit>>()

    var isEnabled: Boolean = true
    var globalVolume: Float = 1f

    fun initialize(context: Context, preload: Set<Sfx> = emptySet()) {
        if (soundPool != null) return

        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(attrs)
            .build().also { sp ->
                sp.setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        loadedIds += sampleId
                        val sfx = idToSfx[sampleId]
                        if (sfx != null) {
                            pendingPlays.remove(sfx)?.forEach { it.invoke() }
                        }
                    }
                }
            }

        preload.forEach { load(context, it) }
    }

    private fun load(context: Context, sfx: Sfx) {
        if (sfxToId.containsKey(sfx)) return
        val id = soundPool!!.load(context, sfx.resId, 1)
        sfxToId[sfx] = id
        idToSfx[id] = sfx
    }

    fun setVolumeLevel(level: Int) {
        globalVolume = when (level.coerceIn(0,4)) {
            4 -> 1.0f
            3 -> 0.75f
            2 -> 0.5f
            1 -> 0.25f
            else -> 0.0f
        }
        isEnabled = globalVolume > 0f
    }

    fun play(
        context: Context,
        sfx: Sfx,
        vol: Float = 1f,
        rate: Float = sfx.defaultRate,
        loop: Int = 0,
        priority: Int = 1
    ) {
        if (!isEnabled) return
        val sp = soundPool ?: return

        val action = action@{
            val id = sfxToId[sfx] ?: return@action
            if (!loadedIds.contains(id)) return@action
            val v = (sfx.defaultVol * vol * globalVolume).coerceIn(0f, 1f)
            sp.play(id, v, v, priority, loop, rate.coerceIn(0.5f, 2f))
        }

        val id = sfxToId[sfx]
        when {
            id == null -> {
                pendingPlays.getOrPut(sfx) { mutableListOf() }.add(action)
                load(context, sfx)
            }
            !loadedIds.contains(id) -> {
                pendingPlays.getOrPut(sfx) { mutableListOf() }.add(action)
            }
            else -> action()
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        sfxToId.clear()
        idToSfx.clear()
        loadedIds.clear()
        pendingPlays.clear()
    }
}