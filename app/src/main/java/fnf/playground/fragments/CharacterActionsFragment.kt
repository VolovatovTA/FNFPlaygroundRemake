package fnf.playground.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import androidx.fragment.app.Fragment
import fnf.playground.R
import fnf.playground.creators.CreatorCharacterData
import fnf.playground.config.Prefs
import fnf.playground.creators.ActionsCharacter
import fnf.playground.databinding.FragmentCharacterActionsBinding
import java.io.IOException


private const val ARG_CHARACTER = "character name"


class CharacterActionsFragment : Fragment(), View.OnTouchListener {
    private var character: String? = null
    private lateinit var currentAnimation: AnimationDrawable
    private lateinit var creatorCharacterData: CreatorCharacterData
    val soundPool = SoundPool(4, AudioManager.STREAM_MUSIC, 100)
    var idSound = 0
    var volumeCharacters = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idSound = soundPool.load(requireContext(), R.raw.scroll_menu, 1)

        volumeCharacters = requireActivity()
            .getSharedPreferences(
                Prefs.APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
            .getFloat(
                Prefs.APP_PREFERENCES_VOLUME_SOUNDS_CHARACTERS,
                1f
            )
        arguments?.let {
            character = it.getString(ARG_CHARACTER)
        }
    }

    lateinit var binding: FragmentCharacterActionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCharacterActionsBinding.inflate(inflater, container, false)

        creatorCharacterData = CreatorCharacterData(requireContext(), character!!)

        currentAnimation = creatorCharacterData.animations[ActionsCharacter.IDLE]?:
        throw IOException("You can't create a character without idle animation")

        return binding.root
    }

    private fun compare2drawable(d1: Drawable, d2: Drawable) {


        val r = Runnable {
            var dif: Array<Array<Int>> = emptyArray()

            for (i in 0 until d1.toBitmap().width) {
                var array = arrayOf<Int>()
                for (j in 0 until d1.toBitmap().height) {
                    array += d1.toBitmap()[i, j] - d2.toBitmap()[i, j]
                }
                dif += array
            }
            Log.d("DebugAnimation", "calculate")

            var res = ""
            for (i in 0 until d1.toBitmap().width) {
                var res1 = ""
                for (j in 0 until d1.toBitmap().height) {
                    res1 += dif[i][j]
                }
                res += res1

                res += "\n"
            }
            Log.d("DebugAnimation", "res = $res")

        }
        val thread = Thread(r)

        thread.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.CharacterImageView.setImageDrawable(currentAnimation)
        currentAnimation.start()
        binding.imageButtonLeft.setOnTouchListener(this)
        binding.imageButtonRight.setOnTouchListener(this)
        binding.imageButtonDown.setOnTouchListener(this)
        binding.imageButtonUp.setOnTouchListener(this)
        binding.imageButtonB.setOnTouchListener(this)
        binding.imageButtonBack.setOnTouchListener(this)



    }

    companion object {
        @JvmStatic
        fun newInstance(nameCharacter: String, isItMode: Boolean) =
            CharacterActionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHARACTER, nameCharacter)
                }
            }
    }


    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        val volume = requireActivity()
            .getSharedPreferences(
                Prefs.APP_PREFERENCES,
                Context.MODE_PRIVATE
            )
            .getFloat(
                Prefs.APP_PREFERENCES_VOLUME_SOUNDS_BUTTONS,
                1f
            )
        p0!!.performClick()
        when (p0.id) {
            R.id.imageButtonBack -> {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0.isPressed = !p0.isPressed
                        soundPool.play(idSound, volume, volume, 1, 0, 1f)
                    }
                    MotionEvent.ACTION_UP -> {
                        p0.isPressed = !p0.isPressed
                        requireActivity().onBackPressed()


                    }
                }
            }
            R.id.imageButtonLeft -> {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0.isPressed = !p0.isPressed
                        creatorCharacterData.play(ActionsCharacter.LEFT, volumeCharacters)
                        changeAnimation(ActionsCharacter.LEFT)
                    }
                    MotionEvent.ACTION_UP -> {
                        p0.isPressed = !p0.isPressed

                        changeAnimation(ActionsCharacter.IDLE)
                    }
                }
            }
            R.id.imageButtonDown -> {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0.isPressed = !p0.isPressed

                        creatorCharacterData.play(ActionsCharacter.DOWN, volumeCharacters)
                        changeAnimation(ActionsCharacter.DOWN)
                    }
                    MotionEvent.ACTION_UP -> {
                        p0.isPressed = !p0.isPressed

                        changeAnimation(ActionsCharacter.IDLE)
                    }
                }
            }
            R.id.imageButtonRight -> {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0.isPressed = !p0.isPressed
                        creatorCharacterData.play(ActionsCharacter.RIGHT, volumeCharacters)
                        changeAnimation(ActionsCharacter.RIGHT)
                    }
                    MotionEvent.ACTION_UP -> {
                        p0.isPressed = !p0.isPressed
                        changeAnimation(ActionsCharacter.IDLE)
                    }
                }
            }
            R.id.imageButtonUp -> {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0.isPressed = !p0.isPressed
                        creatorCharacterData.play(ActionsCharacter.UP, volumeCharacters)
                        changeAnimation(ActionsCharacter.UP)
                    }
                    MotionEvent.ACTION_UP -> {
                        p0.isPressed = !p0.isPressed
                        changeAnimation(ActionsCharacter.IDLE)
                    }
                }
            }
            R.id.imageButtonB -> {
                when (p1!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        p0.isPressed = !p0.isPressed
                        creatorCharacterData.play(ActionsCharacter.SPEC, volumeCharacters)
                        changeAnimation(ActionsCharacter.SPEC)
                    }
                    MotionEvent.ACTION_UP -> {
                        p0.isPressed = !p0.isPressed
                        changeAnimation(ActionsCharacter.IDLE)
                    }
                }
            }
        }

        return true
    }

    private fun changeAnimation(action: ActionsCharacter) {

        if (creatorCharacterData.animations[action] != null){
            currentAnimation.stop()
            currentAnimation = creatorCharacterData.animations[action]!!
            binding.CharacterImageView.setImageDrawable(currentAnimation)
            currentAnimation.start()
        }

    }
}