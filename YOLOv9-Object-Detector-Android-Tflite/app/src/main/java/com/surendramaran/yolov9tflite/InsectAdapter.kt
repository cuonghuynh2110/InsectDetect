package com.surendramaran.yolov9tflite

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class InsectAdapter(private val context: Context, private val insectList: List<Insect>) :
    ArrayAdapter<Insect>(context, 0, insectList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_insect, parent, false)

        val imageView = view.findViewById<ImageView>(R.id.imageInsect)
        val textView = view.findViewById<TextView>(R.id.textInsectName)

        val insect = insectList[position]
        imageView.setImageResource(insect.imageResId)
        textView.text = insect.name

        return view
    }
}
