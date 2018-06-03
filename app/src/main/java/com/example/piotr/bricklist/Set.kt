package com.example.piotr.bricklist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView

import kotlinx.android.synthetic.main.activity_set.*
import java.util.*

class Set : AppCompatActivity() {

    private var myInventoryName:String = ""
    private var myInventoryPartList : ArrayList<myInventoryPart> ? =null
    private var myDB :DataBaseHelper = DataBaseHelper(this)
    private var myInventoryID : Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)


        try {
            myInventoryName= getIntent().getStringExtra ("name")
            var myDB1 :DataBaseHelper = DataBaseHelper(this)

            myInventoryID=myDB1.getMyInventoryIDIP(myInventoryName)
            textView6.setText("Inventory ID in Database : " + myInventoryID.toString())

            val date = Date()

            myDB1.updatelastAccessed(myInventoryID,date.time.toInt())
            myDB1.close()

        }
        catch (e : Exception){}


        val listView: ListView = findViewById<ListView>(R.id.listView)
//        val names: ArrayList<String> = java.util.ArrayList()
//        names.add("Kasia")
//        names.add("Piciu")
//        names.add("Agata")
//        names.add("Anka")


        myInventoryPartList=myDB.getMyInventoriesPart(myInventoryName)
        Log.i("---dlugosc listy z bazy " + myInventoryPartList!!.size,"xxx")







        var adapter = myListAdapter(this, myInventoryPartList!!, this)
        listView?.adapter = adapter
        adapter.notifyDataSetChanged()






    }
}
