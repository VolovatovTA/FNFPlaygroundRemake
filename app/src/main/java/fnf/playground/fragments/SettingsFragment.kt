package fnf.playground.fragments

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import fnf.playground.MyViewModel
import fnf.playground.R
import fnf.playground.config.Prefs
import fnf.playground.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment(), SeekBar.OnSeekBarChangeListener {
    lateinit var binding: FragmentSettingsBinding
    lateinit var viewModel: MyViewModel

    val soundPool = SoundPool(4, AudioManager.STREAM_MUSIC, 100)
    var idSoundButtons = 0
    var idSoundCharacters = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MyViewModel::class.java]

        idSoundButtons = soundPool.load(requireContext(), R.raw.scroll_menu, 1)
        idSoundCharacters = soundPool.load(requireContext(), R.raw.example_down_bf, 1)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        val settings = requireActivity().getSharedPreferences(Prefs.APP_PREFERENCES, Context.MODE_PRIVATE)


        binding.seekBarMusicVolume.progress = (settings.getFloat(Prefs.APP_PREFERENCES_VOLUME_MUSIC, 30f)*100).toInt()
        binding.seekBarSoundsVolume.progress = (settings.getFloat(Prefs.APP_PREFERENCES_VOLUME_SOUNDS_BUTTONS, 10f)*10).toInt()
        binding.seekBarSoundsCharactersVolume.progress = (settings.getFloat(Prefs.APP_PREFERENCES_VOLUME_SOUNDS_CHARACTERS, 10f)*10).toInt()


        binding.seekBarMusicVolume.setOnSeekBarChangeListener(this)
        binding.seekBarSoundsVolume.setOnSeekBarChangeListener(this)
        binding.seekBarSoundsCharactersVolume.setOnSeekBarChangeListener(this)

        binding.imageButtonBack.setOnClickListener {
            soundPool.play(
                idSoundButtons,
                binding.seekBarSoundsVolume.progress.toFloat()/10,
                binding.seekBarSoundsVolume.progress.toFloat()/10,
                1,
                0,
                1f
            )

            val editor = settings.edit()
            editor.putFloat(Prefs.APP_PREFERENCES_VOLUME_MUSIC, binding.seekBarMusicVolume.progress.toFloat()/100)
            editor.putFloat(Prefs.APP_PREFERENCES_VOLUME_SOUNDS_BUTTONS, binding.seekBarSoundsVolume.progress.toFloat()/10)
            editor.putFloat(Prefs.APP_PREFERENCES_VOLUME_SOUNDS_CHARACTERS, binding.seekBarSoundsCharactersVolume.progress.toFloat()/10)
            editor.apply()


            activity?.onBackPressed()
        }
        return binding.root
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

        when (p0?.id){
            R.id.seekBarMusicVolume ->{
                viewModel.service?.player?.setVolume(p1 / 100f, p1 / 100f)
            }
            R.id.seekBarSoundsVolume -> {
                val volume = p0.progress.toFloat()/p0.max

                soundPool.play(idSoundButtons, volume, volume, 1, 0, 1f)

            }
            R.id.seekBarSoundsCharactersVolume -> {
                val volume = p0.progress.toFloat()/p0.max

                soundPool.play(idSoundCharacters, volume, volume, 1, 0, 1f)

            }
        }

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
    }


}