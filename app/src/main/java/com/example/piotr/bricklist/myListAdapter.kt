package com.example.piotr.bricklist

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color.GREEN
import android.graphics.Color.TRANSPARENT
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.ByteArrayInputStream

class myListAdapter (private var activity: Activity, private var items: ArrayList<myInventoryPart> , private var context : Context) : BaseAdapter(){

    private class ViewHolder(row: View?) {
        var desription: TextView? = null
        var layout: RelativeLayout? = null
        var Have: TextView? = null
        var buttonPlus : Button?=null
        var buttonMinus : Button?=null
        var image: ImageView?=null
        init {
            this.desription = row?.findViewById<TextView>(R.id.textView1)
            this.layout = row?.findViewById<RelativeLayout>(R.id.layout)
            this.Have = row?.findViewById<TextView>(R.id.textView2)
            this.buttonPlus = row?.findViewById<Button>(R.id.button1)
            this.buttonMinus = row?.findViewById<Button>(R.id.button2)
            this.image=row?.findViewById<ImageView>(R.id.imageView)
        }
    }

    fun ByteArrayToBitmap(byteArray: ByteArray): Bitmap {
        val arrayInputStream = ByteArrayInputStream(byteArray)
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
        var text1=userDto.colorName + " : " +userDto.name
        viewHolder.desription?.text = text1
        var text = userDto.quantityInStore.toString() + " / " + userDto.quantityInSet.toString()
       viewHolder.Have?.text= text
        if(userDto.quantityInStore==userDto.quantityInSet){
            viewHolder.layout?.setBackgroundColor(GREEN)
        }
        try {
            var x: ByteArray = userDto.image!!
            var y: Bitmap = ByteArrayToBitmap(x)
            viewHolder.image?.setImageBitmap(y)
        }
        catch (e : Exception){
           // Log.i("---Konwersja do bitarray","error")
        }

        //function plus brick
        viewHolder.buttonPlus?.setOnClickListener(){
            var myDB  = DataBaseHelper(context)
            if(userDto.quantityInStore<userDto.quantityInSet!!){
                userDto.quantityInStore+=1
                var text = userDto.quantityInStore.toString() + " / " + userDto.quantityInSet.toString()
                viewHolder.Have?.text=text

                myDB.updateInStore(userDto.quantityInStore,userDto.id)
            }

            if(userDto.quantityInStore==userDto.quantityInSet){
                viewHolder.layout?.setBackgroundColor(GREEN)
            }
            myDB.close()
        }

        //function minus brick
        viewHolder.buttonMinus?.setOnClickListener(){
            var myDB = DataBaseHelper(context)
            if(userDto.quantityInStore>0){
                userDto.quantityInStore-=1

                var text = userDto.quantityInStore.toString() + " / " + userDto.quantityInSet.toString()
                viewHolder.Have?.text=text

                myDB.updateInStore(userDto.quantityInStore,userDto.id)
            }
            if(userDto.quantityInStore<userDto.quantityInSet!!){
                viewHolder.layout?.setBackgroundColor(TRANSPARENT)
            }
            myDB.close()
        }

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