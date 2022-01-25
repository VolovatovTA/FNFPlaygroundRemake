package fnf.playground.fragments

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fnf.playground.adapters.OfficialCharactersIconsAdapter
import fnf.playground.config.Prefs
import fnf.playground.databinding.FragmentFullscreenChooseCharacterBinding
import fnf.playground.R
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

// фрагмент выбора персонажа
class FullscreenFragmentChooseCharacter : Fragment() {

    var isItemsClickable = true

    var columnCount = 3
    var rawCount = 6
    private var mInterstitialAd: InterstitialAd? = null // реклама
    private var mAdIsLoading: Boolean = false // флаг загрузки рекламы
    var characterName = "" // имя персонажа
    private var TAG = "DebugAnimation" // для дебага
    private var fullscreenContent: View? = null // вью рекламы
    private var fullscreenContentControls: View? = null // контроллер рекламы
    val soundPool = SoundPool(4, AudioManager.STREAM_MUSIC, 100) // звук кнопки
    // (подготовка к проигрыванию)
    var idSound = 0 // id по котрой программа поймёт какую зхапись воспроизводить
    private var _binding: FragmentFullscreenChooseCharacterBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the Mobile Ads SDK.
        if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels){
            columnCount = 5
            rawCount = 3
        }
        MobileAds.initialize(requireContext()) {}

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf("ABCDEF012345"))
                .build()
        )



    }

    override fun onStart() {
        super.onStart()
        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()
        }
    }


    // метод который вызывается при создании View в нём прописана основная логика
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = FragmentFullscreenChooseCharacterBinding
            .inflate(inflater, container, false)

        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        idSound = soundPool.load(requireContext(), R.raw.scroll_menu, 1)

        val widthIcon = resources.displayMetrics.widthPixels/columnCount
        val heightIcon = resources.displayMetrics.heightPixels/rawCount
        Log.d(TAG, "width = $widthIcon")
        Log.d(TAG, "height = $heightIcon")
        val adapterCharacters = OfficialCharactersIconsAdapter(requireContext(), widthIcon, heightIcon)
        val gridViewCharacters = binding.coverFlowOfficialCharacters
        gridViewCharacters.adapter = adapterCharacters
        gridViewCharacters.numColumns = columnCount




        val volume = requireActivity()
            .getSharedPreferences(
                Prefs.APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
            .getFloat(
                Prefs.APP_PREFERENCES_VOLUME_SOUNDS_BUTTONS,
                1f
            )
        _binding?.imageButtonBack?.setOnClickListener {
            soundPool.play(idSound, volume, volume, 1, 0, 1f)
            requireActivity().onBackPressed()
        }
        gridViewCharacters.setOnItemClickListener { _, _, position, _ ->
            if (isItemsClickable){
                soundPool.play(idSound, volume, volume, 1, 0, 1f)
                characterName =  adapterCharacters.arrayListCharactersFolders[position]
                    .split(".")[0]
                showInterstitial()
                isItemsClickable = false

            }


        }

    }
    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireContext(), AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    isItemsClickable = true


                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                    isItemsClickable = true

                }
            }
        )
    }
    // Show the ad if it's ready. Otherwise toast and restart the game.
    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    _binding?.root?.visibility = View.GONE
                    startGame()

                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            startGame()
        }
    }

    // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
    private fun startGame() {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()

            transaction.replace(
                R.id.fragmentContainerView2, CharacterActionsFragment.newInstance(
                    characterName, false
                )
            )

            transaction.addToBackStack("1")
            transaction.commit()

    }


    override fun onDestroy() {
        super.onDestroy()
        fullscreenContent = null
        fullscreenContentControls = null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}