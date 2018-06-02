package com.example.piotr.bricklist

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import java.io.ByteArrayInputStream

class myListAdapter (private var activity: Activity, private var items: ArrayList<myInventoryPart> , private var context : Context) : BaseAdapter(){


    private class ViewHolder(row: View?) {
        var desription: TextView? = null
        var countHave: TextView? = null
        var slash: TextView? = null
        var countNeeded: TextView? = null
        var buttonPlus : Button?=null
        var buttonMinus : Button?=null
        var image: ImageView?=null
        init {
            this.desription = row?.findViewById<TextView>(R.id.textView1)
            this.countHave = row?.findViewById<TextView>(R.id.textView2)
            this.slash = row?.findViewById<TextView>(R.id.textView3)
            this.countNeeded = row?.findViewById<TextView>(R.id.textView4)
            this.buttonPlus = row?.findViewById<Button>(R.id.button1)
            this.buttonMinus = row?.findViewById<Button>(R.id.button2)
            this.image=row?.findViewById<ImageView>(R.id.imageView)
        }
    }

    fun ByteArrayToBitmap(byteArray: ByteArray): Bitmap {
        //Log.i("---", "kasia")
        val arrayInputStream = ByteArrayInputStream(byteArray)
       // Log.i("---", "piciu")
        return BitmapFactory.decodeStream(arrayInputStream)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.bricks_row_layout, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var userDto = items[position]

        viewHolder.desription?.text = userDto.name
        viewHolder.countNeeded?.text=userDto.quantityInSet.toString()
        viewHolder.countHave?.text=userDto.quantityInStore.toString()

//        var x : ByteArray = userDto.image!!
//        var y : Bitmap = ByteArrayToBitmap(x)
//        viewHolder.image?.setImageBitmap(y)

        return view as View
    }

    override fun getItem(i: Int): Any {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}