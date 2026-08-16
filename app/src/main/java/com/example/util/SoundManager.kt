package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.ToneGenerator
import android.media.AudioManager
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance, zero-latency Sound Manager for interactive warehouse applet.
 * Provides custom synthesized crystal-clear click sounds, cart chimes, and order feedback.
 */
class SoundManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val prefs = context.getSharedPreferences("app_sound_prefs", Context.MODE_PRIVATE)

    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("sound_enabled", true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    // Pre-generated PCM audio buffers for instant zero-latency playback
    private val clickBuffer: ShortArray = generateTone(frequency = 2200.0, durationMs = 12, decay = 35.0)
    private val addCartBuffer: ShortArray = generateChirp(startFreq = 880.0, endFreq = 1760.0, durationMs = 28)
    private val removeBuffer: ShortArray = generateChirp(startFreq = 1200.0, endFreq = 500.0, durationMs = 20)
    private val tabBuffer: ShortArray = generateTone(frequency = 1600.0, durationMs = 10, decay = 40.0)
    private val successBuffer: ShortArray = generateSuccessChord()

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_SYSTEM, 60)
        } catch (_: Exception) {}
    }

    fun toggleSound(): Boolean {
        val newState = !_isSoundEnabled.value
        _isSoundEnabled.value = newState
        prefs.edit().putBoolean("sound_enabled", newState).apply()
        if (newState) {
            playClick()
        }
        return newState
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        prefs.edit().putBoolean("sound_enabled", enabled).apply()
    }

    /**
     * Standard tactile click sound
     */
    fun playClick(view: View? = null) {
        if (!_isSoundEnabled.value) return
        view?.playSoundEffect(SoundEffectConstants.CLICK)
        scope.launch {
            playPcm(clickBuffer, volume = 0.65f)
        }
    }

    /**
     * Positive feedback when adding item to cart
     */
    fun playAddToCart() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playPcm(addCartBuffer, volume = 0.85f)
        }
    }

    /**
     * Subtle blip when removing or decrementing item
     */
    fun playRemove() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playPcm(removeBuffer, volume = 0.7f)
        }
    }

    /**
     * Soft tap when switching tabs / agencies / categories
     */
    fun playTabSwitch() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playPcm(tabBuffer, volume = 0.55f)
        }
    }

    /**
     * Soft dismiss/close sound
     */
    fun playDismiss() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playPcm(removeBuffer, volume = 0.5f)
        }
    }

    /**
     * Melodious celebration chord for successfully sending an order
     */
    fun playOrderSuccess() {
        if (!_isSoundEnabled.value) return
        scope.launch {
            playPcm(successBuffer, volume = 0.9f)
        }
    }

    private fun playPcm(buffer: ShortArray, volume: Float = 1.0f) {
        scope.launch(Dispatchers.IO) {
            var track: AudioTrack? = null
            try {
                val sampleRate = 44100
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                val trackSize = maxOf(minBufferSize, buffer.size * 2)

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build()

                track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(trackSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.setVolume(volume.coerceIn(0f, 1f))
                track.write(buffer, 0, buffer.size)
                track.play()

                val durationMs = ((buffer.size.toDouble() / sampleRate) * 1000).toLong() + 30L
                kotlinx.coroutines.delay(durationMs)
                try {
                    track.stop()
                } catch (_: Exception) {}
            } catch (_: Exception) {
                // Fallback to system tone if AudioTrack throws
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 35)
                } catch (_: Exception) {}
            } finally {
                try {
                    track?.release()
                } catch (_: Exception) {}
            }
        }
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }

        private fun generateTone(frequency: Double, durationMs: Int, decay: Double): ShortArray {
            val sampleRate = 44100
            val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(totalSamples)

            for (i in 0 until totalSamples) {
                val t = i.toDouble() / sampleRate
                val envelope = exp(-decay * t * 1000 / durationMs)
                val sample = sin(2.0 * PI * frequency * t) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.75).toInt().toShort()
            }
            return buffer
        }

        private fun generateChirp(startFreq: Double, endFreq: Double, durationMs: Int): ShortArray {
            val sampleRate = 44100
            val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(totalSamples)

            for (i in 0 until totalSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / totalSamples
                val currentFreq = startFreq + (endFreq - startFreq) * progress
                val envelope = (1.0 - progress) * (if (progress < 0.1) progress * 10.0 else 1.0)
                val sample = sin(2.0 * PI * currentFreq * t) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.8).toInt().toShort()
            }
            return buffer
        }

        private fun generateSuccessChord(): ShortArray {
            val sampleRate = 44100
            val durationMs = 180
            val totalSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val buffer = ShortArray(totalSamples)

            // C6 (1046.5Hz), E6 (1318.5Hz), G6 (1567.98Hz), C7 (2093.0Hz)
            val freqs = doubleArrayOf(1046.5, 1318.5, 1567.98, 2093.0)

            for (i in 0 until totalSamples) {
                val t = i.toDouble() / sampleRate
                val progress = i.toDouble() / totalSamples
                val envelope = exp(-8.0 * progress) * (if (progress < 0.05) progress * 20.0 else 1.0)
                var combined = 0.0
                freqs.forEachIndexed { index, f ->
                    val delaySamples = (index * totalSamples * 0.15).toInt()
                    if (i >= delaySamples) {
                        val subT = (i - delaySamples).toDouble() / sampleRate
                        val subEnvelope = exp(-10.0 * (i - delaySamples).toDouble() / totalSamples)
                        combined += sin(2.0 * PI * f * subT) * subEnvelope
                    }
                }
                val sample = (combined / freqs.size) * envelope
                buffer[i] = (sample * Short.MAX_VALUE * 0.85).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            return buffer
        }
    }
}

val LocalSoundManager = compositionLocalOf<SoundManager> {
    error("No SoundManager provided")
}

@Composable
fun rememberAppSoundManager(): SoundManager {
    val context = LocalContext.current
    return remember(context) { SoundManager.getInstance(context) }
}
