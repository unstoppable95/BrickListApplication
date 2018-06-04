package com.example.piotr.bricklist

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.SQLException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_project.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.collections.ArrayList

class Project : AppCompatActivity() {

    var progress: ProgressDialog? =null
    private var fileURL : String = "";
    private var brickSetName : String = "";
    private var id:String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        try {
            urlText.setText(getIntent().getStringExtra("SetURL"))
            id=getIntent().getStringExtra("id")
            setNameText.setText(getIntent().getStringExtra("name").toString())
        }
        catch (e : Exception){}
    }

    fun showSettings(view: View){
        val intent = Intent(this,ProjectSettings::class.java)
        intent.putExtra("name",setNameText.text.toString())
        intent.putExtra("id",id)
        startActivity(intent)
    }

    fun downloadAdd(view: View){
        if(setNameText.text.toString().length>0 && urlText.text.toString().length>0)
        {
            progress = ProgressDialog(this)
            progress!!.setTitle("Please Wait!!")
            progress!!.setMessage("Downloading XML & creating project")
            progress!!.setCancelable(true)
            progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progress!!.show()

            fileURL=urlText.text.toString()
            brickSetName=setNameText.text.toString();

            val cd = XmlDownloader(this)
            var name:String =cd.execute(fileURL).get()
            var myDB = DataBaseHelper(this)

            loadData(myDB)

            progress!!.dismiss()

        }
        else{
            Toast.makeText(this, "Musisz miec nazwe zestawu i adres URL!", Toast.LENGTH_SHORT).show();
        }
    }

    fun loadData(myDB : DataBaseHelper) {

        val filename = "downloadedFile.xml"
        val path = filesDir
        val inDir = File(path, "XML")
        if (inDir.exists()) {

            val file = File(inDir, filename)
            if (file.exists()) {
                val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

                xmlDoc.documentElement.normalize()

                val date = Date()
                val idBrickSetInDataBase = myDB.getBricSetId()

                val newInventory = myInventory(idBrickSetInDataBase, brickSetName, 1, date.time.toInt())
                myDB.addInventoryToDatabase(newInventory)

                val items: NodeList = xmlDoc.getElementsByTagName("ITEM")
                for (i in 0..items.length - 1) {

                    val itemNode: Node = items.item(i)

                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {

                        val elem = itemNode as Element
                        val children = elem.childNodes
                        var imageURL : String?
                        val part = myInventoryPart()
                        var alternate: String? = null

                        for (j in 0..children.length - 1) {
                            val node = children.item(j)
                            if (node is Element) {
                                when (node.nodeName) {
                                    "ITEMTYPE" -> part.itemType = node.textContent
                                    "ITEMID" -> part.itemIDXML = node.textContent
                                    "QTY" -> part.quantityInSet = node.textContent.toInt()
                                    "COLOR" -> part.color = node.textContent.toInt()
                                    "EXTRA" -> part.extra = node.textContent
                                    "ALTERNATE" -> alternate = node.textContent
                                }
                            }
                        }


                        if (alternate.equals("N") && myDB.lookForPartInDataBase(part.itemIDXML) == 1) {


                            part.colorID = myDB.getColorID(part.color)
                            part.typeID = myDB.getTypeID(part.itemType)
                            part.itemIDDatabase = myDB.getItemID(part.itemIDXML)
                            part.inventoryID = idBrickSetInDataBase
                            part.id = myDB.getBrickSetPartId()
                            part.designID = myDB.getDesignId(part.colorID,part.itemIDDatabase)


                            if ( myDB.imageExists(part.designID)==0){
                                var adress=""

                                if(part.designID!!>0) {
                                    val cp = RetrieveFeedTask(part.itemIDDatabase!!, part.colorID!!, myDB)
                                    adress="https://www.lego.com/service/bricks/5/2/"+part.designID
                                    if(!cp.execute(adress).get()){
                                        adress="https://www.bricklink.com/PL/" + part.designID + ".jpg"
                                        if(!cp.execute(adress).get()){
                                            adress= "http://img.bricklink.com/P/" + part.colorID + "/" + part.itemIDDatabase + ".gif"
                                            cp.execute(adress)
                                        }
                                    }

                                }

                            }

                            myDB.addInventoryPartToDatabase(part);

                        }

                    }
                }
            }
        }


        val intent = Intent(this,MainActivity::class.java)

        startActivity(intent)

    }



    private inner  class RetrieveFeedTask(partID: Int,colorID : Int, myDB: DataBaseHelper) : AsyncTask<String, Int, Boolean>() {


        private var colorIDx=colorID
        private var partID=partID
        private var myDabase=myDB

        private var exception: Exception? = null

        override fun doInBackground (vararg params: String?): Boolean {
            var My: InputStream? = null
            var bmp: Bitmap? = null
            var responseCode = -1


            try {
                val url = java.net.URL(params[0])
                val con = url.openConnection() as HttpURLConnection
                con.setDoInput(true)
                con.connect()
                responseCode = con.getResponseCode()
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //download
                    My = con.getInputStream()
                    bmp = BitmapFactory.decodeStream(My)
                    if (bmp!=null){
                        Log.i("---DOWNLOAD IMAGE OK " , "---Download IMAGE OK")
                    }
                    else{
                      //  Log.i("---COS SIE STALO  ", "---birmapa null :(  ")
                    }

                    val blob =getBytesFromBitmap(bmp)
                    val blobValues = ContentValues()
                    blobValues.put("Image", blob)
                    myDabase.updateImage(partID,colorIDx,blobValues)
                    My!!.close()
                }

            } catch (ex: Exception) {
                return false
            }
            return true
        }


    }

    fun getBytesFromBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        return stream.toByteArray()
    }



    private inner class XmlDownloader (context :Context): AsyncTask<String, Int, String>(){

        private var con :Context = context
        override fun doInBackground(vararg params: String?): String { //params - full url
            try {

                var filename = "downloadedFile.xml";

                val url = URL(params[0])
                val connection = url.openConnection()
                connection.connect()
                val isStream = url.openStream()
                val testDirectory = File("$filesDir/XML")
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



