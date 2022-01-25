package fnf.playground

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import fnf.playground.services.ServiceForMusic

// клас который отвечает за то чтобы запустить сервис и вопроизводить в нём музыку. Подключается
// к нему и держит у себя экземпляр класса сервис
class MyViewModel(application: Application) : AndroidViewModel(application) {

    // плохо но тут хранится сервис. Это приводит к утечки памяти
    @SuppressLint("StaticFieldLeak")
    var service: ServiceForMusic? = null
    val TAG = "DebugAnimation"

    private var sCon : ServiceConnection // переменная соединения с сервисом
    var intentForService : Intent =
        Intent(application.baseContext, ServiceForMusic::class.java)
    // класс для того, чтобы понять какой сервис запускать


    init {

        // определение что делать при подключении и отключении сервиса
        sCon = object: ServiceConnection {
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder) {
                // получения экземпляра сервиса
                service = (p1 as ServiceForMusic.BinderMusic).service
                // при подключении музыки запустить музыку
                playMusicIfItNotPlaying(application)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                // при отключении сервиса вырубить его
                service?.stopService(intentForService)
            }

        }

        // установить подключение к сервису
        application.baseContext.bindService(
            intentForService,
            sCon,
            // создавать сервис
            AppCompatActivity.BIND_AUTO_CREATE
        )
    }
    fun pauseMusic(){
        service?.player?.pause()
    }

// метод который вызывается при полной очистке приложения вроде
    override fun onCleared() {
        Log.d(TAG, "onCleared")

        service!!.stopSelf()
        super.onCleared()
    }

    fun playMusicIfItNotPlaying(application: Application) {
        if (service == null){
            // подключить музыку или стартовать плеер
            application.baseContext.bindService(
                intentForService,
                sCon, AppCompatActivity.BIND_AUTO_CREATE
            )
        }else if (!service!!.isPlayingMusic) {
            service!!.player!!.start()
        }
        else {
            Log.d(TAG, "not")
        }


    }



}