package fnf.playground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import fnf.playground.R
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {
    val TAG = "DebugAnimation" // tag for debug
    lateinit var viewModel: MyViewModel // cl
    private lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        //set the main view
        setContentView(R.layout.activity_main)
        // initialize view model class which will be live after rotate the phone. in this class
        // music will play in background
        viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        // set viewModel start the background music
        viewModel.playMusicIfItNotPlaying(application)
        // create view for ad (for reklama)
        mAdView = findViewById(R.id.adView)
        // create request to the remote source for the banner
        val adRequest = AdRequest.Builder().build()
        // dounload banner from remote source and show on the screen
        mAdView.loadAd(adRequest)


    }



    override fun onResume() {
        // play music if state = resume
        viewModel.playMusicIfItNotPlaying(application)

        super.onResume()
    }


    override fun onUserLeaveHint() {

        // pause music on user turn app out of focus
        viewModel.pauseMusic()

        super.onUserLeaveHint()
    }


    private var exit = false

    // this method called when user click on "back" button
    override fun onBackPressed() {
        // check for empty of backstack states (states which were saved in memory. this states
        // shows when user click "back". after each of click backStackEntryCount degrees by 1)
        if (supportFragmentManager.backStackEntryCount == 0) {
            // check variable "exit" which means that user really want to quit
            if (exit) {
                // if user click on "back" twice in 3 seconds pause the music and finish activity
                // (this class)
                viewModel.pauseMusic()

                finish() // finish activity
            } else {
                // if user click on "back" only once show the message and turn exit to true
                Toast.makeText(
                    this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT
                ).show()
                exit = true
                // after 3 seconds turn exit to false
                Handler().postDelayed({ exit = false }, 3 * 1000)
            }
        } else super.onBackPressed()

    }


}