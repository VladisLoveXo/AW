package app.awake.player

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.awake.ACTIVE_SOUNDS
import app.awake.MusicItem
import java.lang.reflect.Executable

class MusicPlayer() : ViewModel() {
    private var players: MutableList<SoundPlayer> = mutableListOf()

    var musicItem: MusicItem? = null
    var isPlaying = MutableLiveData<Boolean>(false)
    var activeSounds = MutableLiveData<ACTIVE_SOUNDS>(mutableSetOf())

    fun setActiveSounds(sounds: ACTIVE_SOUNDS) {
        activeSounds.value = mutableSetOf()
        activeSounds.value = sounds

        updateVolumes()
    }

    fun currentTime(): Int {
        if (!players.isEmpty()) {
            if (players[0].player != null) {
                return players[0].player!!.currentPosition / 1000 // secs
            }
        }

        return 0
    }

    fun duration(): Int {
        if (!players.isEmpty()) {
            if (players[0].player != null) {
                return players[0].player!!.duration / 1000 // secs
            }
        }

        return 0
    }

    fun prepare(item: MusicItem, context: Context) {
        try {
            if (musicItem?.name != item.name) {
                reset()

                musicItem = item

                for (sound in item.sounds) {
                    val player = SoundPlayer(name = sound.name)

                    if (player.prepare(path = "musics/${sound.path}", context = context)) {
                        players.add(player)

                        if (sound.selected) {
                            activeSounds.value!!.add(sound)
                        }
                    }
                }

                if (players.count() > 0) {
                    players[0].player!!.setOnCompletionListener {
                        isPlaying.value = false
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("", "MusicPlayer ERROR: prepare failed.")
        }
    }

    fun play() {
        updateVolumes()

        for (player in players) {
            player.player?.start()
        }

        sync()

        isPlaying.value = true
    }

    fun pause() {
        for (player in players) {
            player.player?.pause()
        }

        isPlaying.value = false
    }

    fun stop() {
        for (player in players) {
            player.player?.stop()
        }

        isPlaying.value = false
    }

    fun setTime(time: Int) { // secs
        updateVolumes()

        for (player in players) {
            player.player?.seekTo(time * 1000)
        }
    }


    private fun updateVolumes() { // todo: smooth fade and appear
        for (player in players) {

            var isActive: Boolean = false

            for (sound in activeSounds.value!!) {
                if (sound.name == player.name) {
                    isActive = true
                    break
                }
            }

            var volume = if (isActive) 1f else 0f
            player.player?.setVolume(volume, volume)
        }
    }

    private fun reset() {
        stop()
        players.clear()
    }

    private fun sync() {
        var lastPlayer: MediaPlayer? = null

        for (player in players) {
            if (lastPlayer != null) {
                player.player?.seekTo(lastPlayer.currentPosition)
            }

            lastPlayer = player.player
        }
    }
}