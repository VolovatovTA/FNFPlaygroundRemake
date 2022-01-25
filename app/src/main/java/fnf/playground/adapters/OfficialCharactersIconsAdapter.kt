package fnf.playground.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView

// класс который будет заниматься сбором иконок из папки assets и закидывает их в память в
// момент инициализации. После инициализации он отвечает за распихивание иконок по нужным местам
// в gridView.
class OfficialCharactersIconsAdapter(private val context: Context,val heightIcon: Int,val widthIcon: Int) : BaseAdapter() {
    // получение списка всех персонажей (всех названий папок с персонажем)
    var arrayListCharactersFolders : Array<String> = context.assets.list("shared/official characters") as Array<String>
    // объявление списка иконок для отображения в gridView
    private val icons = Array(arrayListCharactersFolders.size, init = {BitmapDrawable(context.resources)})


    init {

        //
        for (i in arrayListCharactersFolders.indices){
            val nameIcon : Array<String> = context.assets.list("shared/official characters/${arrayListCharactersFolders[i]}/icon") as Array<String>

            val inputStream = context.assets
                .open("shared/official characters/${arrayListCharactersFolders[i]}/icon/${nameIcon[0]}")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            icons[i] = BitmapDrawable(context.resources, bitmap)
        }
    }
    override fun getCount(): Int {
        return arrayListCharactersFolders.size
    }

    override fun getItem(position: Int): Any {
        return arrayListCharactersFolders[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val imageView = ImageView(context)
        imageView.setImageDrawable(icons[position] as Drawable)
        val layoutParams = FrameLayout.LayoutParams(heightIcon,widthIcon)
        layoutParams.gravity = 0
        imageView.layoutParams = layoutParams


        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        return imageView
    }

}
