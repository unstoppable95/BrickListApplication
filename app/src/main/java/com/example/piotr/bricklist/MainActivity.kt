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


class MainActivity : AppCompatActivity() {

    private var list: ArrayList<myInventory>?=null
    var inventoriesNames: ArrayList<String?> = java.util.ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        copyDB()
        var myDB :DataBaseHelper = DataBaseHelper(this)
        list=myDB.getMyInventories()


        for(i in 0..list!!.size-1){
            inventoriesNames.add(list!!.get(i).name)
        }

        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, inventoriesNames)
        listView.adapter = adapter

//        listView.onItemClickListener = AdapterView.OnItemClickListener{
//            adapterView, view, position, id ->
//            val intent = Intent(this, LegoSetActivity::class.java )
//            intent.putExtra("name",inventoriesNames[position] )
//            startActivity(intent)
//        }


    }

    fun NewProject(view : View){
        val intent = Intent(this,Project::class.java)
        //intent.putExtra("stosSave", stack)
        startActivity(intent)
    }

    fun showList(view : View){
        val intent = Intent(this,Set::class.java)

        startActivity(intent)
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
