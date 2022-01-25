package fnf.playground.services


import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.util.Log
import fnf.playground.R
import fnf.playground.config.Prefs
import kotlin.properties.Delegates

// класс который нужен для того чтобы воспроизводить музыку в фоне, не прерываясь при перевороте.
class ServiceForMusic : Service(), Runnable {

    val TAG = "DebugAnimation" // для дебага
    var isPlayingMusic = false // уже не нужный флаг
    var player: MediaPlayer? = null // то, что будет воспроизводить музыку
    var volumeMusic by Delegates.notNull<Float>() // громкость музыки
    var t : Thread? = Thread(this) // новый поток, в котором и будет работать плеер

    // класс который нужен, чтобы устанавливать синхронное соединение с сервисом
    inner class BinderMusic : Binder(){
        // единственный метод возвращающий экзэмпляр класа ServiceForMusic
        val service: ServiceForMusic
            get() = this@ServiceForMusic
    }

    // метод который вызывается при повторном соединении сервиса с кем-то
    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind")

        super.onRebind(intent)
    }
    // метод который вызывается при первом соединении сервиса с кем-то
    override fun onBind(intent: Intent?): BinderMusic {
        Log.d(TAG, "onBind")

        return BinderMusic()
    }

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        // получение настроек приложения, установленных в меню настроек
        val settings = getSharedPreferences(Prefs.APP_PREFERENCES, Context.MODE_PRIVATE)
        // получение громкости музыки, по ключу
        volumeMusic = settings.getFloat(Prefs.APP_PREFERENCES_VOLUME_MUSIC, 0.3f)

        // инициализация плеера
        player = MediaPlayer.create(this, R.raw.title1)
        player?.isLooping = true // зацикливаем
        player?.setVolume(volumeMusic, volumeMusic) // устанавливаем громкость

    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        // при остановке сервиса сначала остновить музыку
        player!!.stop()
        // потом очистить память
        player = null
        // завершить поток
        t?.interrupt()
        // стереть данные
        t = null
    }

    override fun onStart(intent: Intent?, startid: Int) {
        Log.d(TAG, "onStart")
        // при старте сервиса начать выполнение потока
        if (!isPlayingMusic){
            t?.start()
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")

        return true
    }

    // метод, который будет выполняться в другом потоке и воспроизводить музыку
    override fun run() {
        isPlayingMusic = true
        player!!.start()
    }

}