package app.awake.player

import android.content.Context
import android.media.MediaPlayer
import app.awake.R

class SoundPlayer(
    var name: String = "",
) {
    var player: MediaPlayer? = null
        private set

    fun prepare(
        resId: Int,
        context: Context,
    ): Boolean {
        if (player != null) {
            player = null
        }

        player = MediaPlayer.create(context, resId)

        return (player != null)
    }

    fun prepare(
        path: String,
        context: Context
    ): Boolean {
        if (player != null) {
            player = null
        }

        player = MediaPlayer()

        var descriptor = context.assets.openFd(path)

        player!!.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength())
        player!!.prepare()

        descriptor.close()

        return (player != null)
    }

    fun playBowl(context: Context) {
        if (prepare(resId = R.raw.bowl, context = context)) {
            player?.start()
        }
    }
}