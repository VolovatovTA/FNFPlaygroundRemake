package fnf.playground.creators

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.media.SoundPool

// class for load the animation and sounds of one character from assets package in memory
class CreatorCharacterData(
    val context: Context, // base class of app. it need to interact with assets file and load
    // something in memory
    character: String, // name of character for whom CreatorCharacterData load the data
) {

    var FILE_PATH_TO_ANIMATIONS: String // path in assets to package with animation

    val FILE_PATH_TO_OFFICIAL_ANIMATIONS = "shared/official characters/"
    private val DEFAULT_DURATION = 30 // duration between two adjacent frames, ms

    val sp = SoundPool(2, AudioManager.STREAM_MUSIC, 1) // class for play
    // short sounds

    var animations: MutableMap<ActionsCharacter, AnimationDrawable> = mutableMapOf() // map
    // of animations
    var soundsId: MutableMap<ActionsCharacter, Int> = mutableMapOf()
    private var sortedListAnimations: MutableMap<ActionsCharacter, String> // sorted by names of
    // actions map of animations
    private var sortedListSounds: MutableMap<ActionsCharacter, String>

    init {

        FILE_PATH_TO_ANIMATIONS = FILE_PATH_TO_OFFICIAL_ANIMATIONS

        // list of packages in directory "animations"
        val listOfNamesFilesInAnimations =
            context.assets.list("$FILE_PATH_TO_ANIMATIONS$character/animations") as Array<String>
        sortedListAnimations = sortList(listOfNamesFilesInAnimations)

        // list of packages in directory "sounds"
        val listOfNamesFilesInSounds =
            context.assets.list("$FILE_PATH_TO_ANIMATIONS$character/sounds") as Array<String>
        sortedListSounds = sortList(listOfNamesFilesInSounds)

        // load the sounds in memory and get the id for each sounds. id is needed to play sounds in
        // the future
        for (s in sortedListSounds.keys){
            soundsId[s] = sp.load(context.assets.openFd(
                FILE_PATH_TO_ANIMATIONS +
                    "$character/sounds/${sortedListSounds[s]}"), 1)
        }

        // дальше буду на русском а то лень. для каждого из действий (вверх вниз и тд.)
        // здесь загружаются анимации
        for (nameAction in sortedListAnimations.keys) {
            // тут получаем массив имён картинок для одной анимации
            val fileNamesForOneAnim =
                context.assets.list("$FILE_PATH_TO_ANIMATIONS$character/animations/" +
                        "${sortedListAnimations[nameAction]}")
                    // создаём пустую анимацию
            animations[nameAction] = AnimationDrawable()
            // говорим чтобы она не повторялась, только если это не базовая анимаци idle
            animations[nameAction]?.isOneShot = nameAction != ActionsCharacter.IDLE

            // если в папке есть хоть один файл добавляем из него кадры в анимацию для конкретного
            // действия
            if (fileNamesForOneAnim != null) {
                // предыдущий номер кадра нужен для определения задержки между кадрами
                var previousNumberOfPhoto = fileNamesForOneAnim[0]
                // пробегаемся по файлам циклом
                for (fileName in fileNamesForOneAnim) {

                    // считаем задержку исходя из имени файла
                    val duration_coefficiemt = fileName.split(".")[0].takeLast(3)
                        .toInt() - previousNumberOfPhoto.split(".")[0].takeLast(3).toInt()
                    // получаем поток данных для одного файла
                    val inputStream = context.assets
                        .open("$FILE_PATH_TO_ANIMATIONS$character/animations/${sortedListAnimations[nameAction]}/$fileName")
                    // делаем из этого потока матрицу
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    // добавляем матрицу (кадр) в анимацию, с учётом задержки
                    animations[nameAction]?.addFrame(
                        BitmapDrawable(context.resources, bitmap)
                        , DEFAULT_DURATION*duration_coefficiemt
                    )
                    // меняем предыдущий номер чтоб на следующем шаге опять можно было бы вычислить задержку
                    previousNumberOfPhoto = fileName
                }

            }
        }
    }
    // здесь проверяется наличие букв в названии файла и в зависимости от названия файла ему
    // сопостовляется одно из действий
    private fun sortList(list: Array<String>) : MutableMap<ActionsCharacter, String> {

        val sortedList : MutableMap<ActionsCharacter, String> = HashMap(list.size)
        // просто долгая проверка на то какие буквы содержит имя файла (i - это имя файла, в
        // котором хранится кадр или звук)
        for (i in list) {
            if (i.contains("idle") || i.contains("IDLE") || i.contains("Idle") || i.contains("I")) {
                sortedList[ActionsCharacter.IDLE]= i
            }
            if (i.contains("spec") || i.contains("SPEC") || i.contains("Spec")|| i.contains("S")) {
                sortedList[ActionsCharacter.SPEC] = i
            }
            if (i.contains("left") || i.contains("LEFT") || i.contains("Left")|| i.contains("L")) {
                sortedList[ActionsCharacter.LEFT] = i
            }
            if (i.contains("right") || i.contains("RIGHT") || i.contains("Right")|| i.contains("R")) {
                sortedList[ActionsCharacter.RIGHT] = i
            }
            if (i.contains("up") || i.contains("UP") || i.contains("Up") || i.contains("U")) {
                sortedList[ActionsCharacter.UP] = i
            }
            if (i.contains("down") || i.contains("DOWN") || i.contains("Down")|| i.contains("D")) {
                sortedList[ActionsCharacter.DOWN] = i
            }

        }

        return sortedList

    }

    fun play(action: ActionsCharacter, volume: Float) {

        soundsId[action]?.let { sp.play(it, volume, volume, 1, 0, 1F) }
    }
}