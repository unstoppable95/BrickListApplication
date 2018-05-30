package com.example.piotr.bricklist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView

class Set : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)

        val listView: ListView = findViewById<ListView>(R.id.listView)
        val names: ArrayList<String> = java.util.ArrayList()
        names.add("Kasia")
        names.add("Piciu")
        names.add("Agata")
        names.add("Anka")

        var adapter = myListAdapter(this, names, this)
        listView?.adapter = adapter
        adapter.notifyDataSetChanged()


    }
}
