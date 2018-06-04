package com.example.piotr.bricklist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_project.*
import kotlinx.android.synthetic.main.activity_project_settings.*

class ProjectSettings : AppCompatActivity() {

    private var name:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_settings)

        try {
            name = getIntent().getStringExtra("name").toString()
            if(getIntent().getStringExtra("id").toString().length>0){
                urlText1.setText(getIntent().getStringExtra("id").toString())}
        }
        catch (e : Exception){}


        buttonConfirm.setOnClickListener(){

            if(urlText1.text.toString().length==0){
                Toast.makeText(this, "Musisz podac ID zestawu ktory chcesz pobrac!",Toast.LENGTH_SHORT).show()
            }
            else{
                var fileUrl = addressText.text.toString() + urlText1.text.toString() + setNameText1.text.toString()
                val intent = Intent(this,Project::class.java)
                intent.putExtra("SetURL" , fileUrl)
                intent.putExtra("name",name)
                intent.putExtra("id",urlText1.text.toString())

                startActivity(intent)
            }
        }
    }





}
