package com.example.piotr.bricklist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_project.*

class Set : AppCompatActivity() {

   private var myInventoryName:String = ""
    private var myInventoryPartList : ArrayList<myInventoryPart> ? =null
    private var myDB :DataBaseHelper = DataBaseHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)


        try {
            myInventoryName= getIntent().getStringExtra ("name")

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
