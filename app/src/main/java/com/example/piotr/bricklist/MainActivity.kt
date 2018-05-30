package com.example.piotr.bricklist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }

    fun NewProject(view : View){
        val intent = Intent(this,Project::class.java)
        //intent.putExtra("stosSave", stack)
        startActivity(intent)
    }
}
