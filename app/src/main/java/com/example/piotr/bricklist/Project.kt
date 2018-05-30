package com.example.piotr.bricklist

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_project.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class Project : AppCompatActivity() {

    private var fileURL : String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        try {
            fileURL= getIntent().getStringExtra("SetURL");
            idText.setText(fileURL);
        }
        catch (e : Exception){}



    }

    fun showSettings(view: View){
        val intent = Intent(this,ProjectSettings::class.java)
        startActivity(intent)
    }

    fun downloadAdd(view: View){
        downloadData(fileURL)
    }

    fun downloadData(path:String){
        val cd = XmlDownloader()
        cd.execute(path)
    }








    private inner class XmlDownloader: AsyncTask<String, Int, String>(){

        override fun doInBackground(vararg params: String?): String { //params - full url
            try {
                //val itemIDEditText: EditText = findViewById<EditText>(R.id.enterItemIdText)
               // val projectNameEditText: EditText = findViewById<EditText>(R.id.enterNameText)
                var filename = "myfirstdown.xml";

                val url = URL(params[0])
                val connection = url.openConnection()
                connection.connect()
                // val lengthOfFile = connection.contentLength
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
                Log.i("----- XXXXX"+"$filesDir/XML","-----$filesDir/XML")
                if (!testDirectory.exists()) testDirectory.mkdir()
                val fos = FileOutputStream("$testDirectory/$filename")
                val data = ByteArray(1024)
                var count = 0
                var total : Long = 0
                count = isStream.read(data)

                while (count != -1) {
                    total+=count.toLong()
                    fos.write(data, 0, count)
                    count = isStream.read(data)
                }
                isStream.close()
                fos.close()
            }
            catch(e: MalformedURLException){
                Log.i("-----Malformed URL", e.toString())
                return "Malformed URL"
            }
            catch (e: FileNotFoundException){
                Log.i("-----File not found", e.toString())
                return "File not found"
            }
            catch (e: IOException){
                Log.i("-----IO Exception", e.toString())
                return "IO Exception"
            }

            return "success"
        }
    }

}


