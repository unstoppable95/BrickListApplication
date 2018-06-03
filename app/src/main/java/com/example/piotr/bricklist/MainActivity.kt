package com.example.piotr.bricklist

import android.content.ContextWrapper
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private var list: ArrayList<myInventory>?=null
    var inventoriesNames: ArrayList<String?> = java.util.ArrayList()
    var lastAcced : ArrayList<Int> = java.util.ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        copyDB()

        var myDB :DataBaseHelper = DataBaseHelper(this)
        list=myDB.getMyInventories()

        lastAcced.clear()

//        if (list!!.size==1) {
//            inventoriesNames.add(list!!.get(0).name)
//            lastAcced.add(list!!.get(0).lastAccessed!!)
//        }

        Log.i("---rozmiar " + list!!.size, "msg")

        for(i in 0..list!!.size-1){
            inventoriesNames.add(list!!.get(i).name)
            lastAcced.add(list!!.get(i).lastAccessed!!)

        }

        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, inventoriesNames)
        listView.adapter = adapter

        listView.onItemClickListener = AdapterView.OnItemClickListener{
            adapterView, view, position, id ->
            val intent = Intent(this, Set::class.java )
            intent.putExtra("name",inventoriesNames[position] )
            startActivity(intent)
        }

        Log.i("---jestem w oncrea", "msg")


    }

    fun NewProject(view : View){
        val intent = Intent(this,Project::class.java)
        //intent.putExtra("stosSave", stack)
        startActivity(intent)
    }

    fun sortList(view : View){
        var myDB :DataBaseHelper = DataBaseHelper(this)

        list!!.clear()
        list=myDB.getMyInventories()
        inventoriesNames.clear()
        lastAcced.clear()

        for(i in 0..list!!.size-1){
            lastAcced.add(list!!.get(i).lastAccessed!!)

        }

        Collections.sort(lastAcced , Collections.reverseOrder())

        for(i in 0..lastAcced!!.size-1){
           inventoriesNames.add(myDB.getInventoryNameByDate(lastAcced.get(i)))
        }

        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, inventoriesNames)
        listView.adapter = adapter


    }

    private fun copyDB() {

        val cw = ContextWrapper(applicationContext)
        val db_name = "BrickDatabase.db"
        val db_path = cw.dataDir.absolutePath
        val outDir = File(db_path, "databases")
        outDir.mkdir()
        val file = File(db_path + "/databases/", db_name)
        if(!file.exists()){
            val input =applicationContext.getAssets().open("BrickDatabase.db");
            val mOutput = FileOutputStream(file)
            val mBuffer = ByteArray(1024)
            var mLength = input.read(mBuffer)
            while (mLength > 0) {
                mOutput.write(mBuffer, 0, mLength)
                mLength = input.read(mBuffer)
            }
            mOutput.flush()
            mOutput.close()
            input.close()
        }

    }



}
