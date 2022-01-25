package fnf.playground.fragments

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fnf.playground.R
import fnf.playground.config.Prefs
import fnf.playground.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    lateinit var binding: FragmentInfoBinding
    val soundPool = SoundPool(4, AudioManager.STREAM_MUSIC, 100)
    var idSound = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val volume = requireActivity()
            .getSharedPreferences(
                Prefs.APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
            .getFloat(
                Prefs.APP_PREFERENCES_VOLUME_SOUNDS_BUTTONS,
                1f
            )

        binding = FragmentInfoBinding.inflate(inflater, container, false)
        idSound = soundPool.load(requireContext(), R.raw.scroll_menu, 1)

        binding.imageButton.setOnClickListener {

            soundPool.play(idSound, volume, volume, 1, 0, 1f)

            requireActivity().onBackPressed()
        }
        return binding.root
    }

}