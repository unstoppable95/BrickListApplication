package com.example.piotr.bricklist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_project_settings.*

class ProjectSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_settings)

        buttonConfirm.setOnClickListener(){

            if(urlText.text.toString().length==0){
                Toast.makeText(this, "Musisz podac ID zestawu ktory chcesz pobrac!",Toast.LENGTH_SHORT).show();
            }
            else{
                var fileUrl = addressText.text.toString() + urlText.text.toString() + setNameText.text.toString() ;
                val intent = Intent(this,Project::class.java)
                intent.putExtra("SetURL" , fileUrl);
               // intent.putExtra("idSet",Integer.parseInt(urlText.text.toString()))
                startActivity(intent)
            }
        }
    }




}
