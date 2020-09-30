package com.example.workout.main.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.workout.R


class IconChangeAdapter(context: Context): BaseAdapter() {
    val context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(R.layout.item_icon_change, parent, false)
        val image: ImageView = view.findViewById(R.id.imageView)
        image.setImageResource(mThumbIds[position])
        image.tag = mThumbIds[position] //порядковый номер изображения в массиве
        return view
    }

    override fun getItem(position: Int): Any {
        return mThumbIds[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mThumbIds.size
    }

    //image collection
    private val mThumbIds = arrayOf(R.drawable.user, R.drawable.battle_axe, R.drawable.indeec,
        R.drawable.raven, R.drawable.viking, R.drawable.wolf, R.drawable.varenie,
        R.drawable.goose, R.drawable.corgi, R.drawable.dinosavr, R.drawable.fish)
}