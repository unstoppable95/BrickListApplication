package com.example.piotr.bricklist

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

    private var fileURL : String = "";
    private var brickSetName : String = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        try {
            urlText.setText(getIntent().getStringExtra("SetURL"));

        }
        catch (e : Exception){}
    }

    fun showSettings(view: View){
        val intent = Intent(this,ProjectSettings::class.java)
        startActivity(intent)
    }

    fun downloadAdd(view: View){
        if(setNameText.text.toString().length>0 && urlText.text.toString().length>0)
        {
            fileURL=urlText.text.toString()
            brickSetName=setNameText.text.toString();

            downloadData(fileURL)



             var myDB :DataBaseHelper = DataBaseHelper(this)

            try {

                myDB.createDataBase()

            } catch (ioe: IOException) {

                throw Error("Unable to create database")

            }


            try {

                myDB.openDataBase()

            } catch (sqle: SQLException) {

                throw sqle

            }


            loadData(myDB)

        }
        else{
            Toast.makeText(this, "Musisz miec nazwe zestawu i adres URL!", Toast.LENGTH_SHORT).show();
        }

    }

    fun downloadData(path:String){
        val cd = XmlDownloader()
        cd.execute(path)
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

                                Log.i("---desingID " +part.designID, "---Nie ma obrazka ")
                                if(part.designID!!>0) {
                                    val cp = RetrieveFeedTask(part.itemIDDatabase!!, part.colorID!!, myDB)
                                    cp.execute("https://www.lego.com/service/bricks/5/2/300126")
                                }

                            }



                            myDB.addInventoryPartToDatabase(part);

                        }

                    }
                }
            }
        }

    }



    private inner  class RetrieveFeedTask(partID: Int,colorID : Int, myDB: DataBaseHelper) : AsyncTask<String, Int, String>() {


        private var colorIDx=colorID
        private var partID=partID
        private var myDabase=myDB

        private var exception: Exception? = null

        override fun doInBackground (vararg params: String?): String {
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
                        Log.i("---COS SIE STALO " , "---Bitmapa nie jest null niby ")
                    }
                    else{
                        Log.i("---COS SIE STALO  ", "---birmapa null :(  ")
                    }

                    //checking save
//                    val fos : FileOutputStream  = openFileOutput("hihi", Context.MODE_PRIVATE);
//                    try {
//
//                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                    }
//                    catch (ex: Exception) {
//                        Log.e("---Exception", ex.toString())
//                    }
//                    fos.close()

                    val blob =getBytesFromBitmap(bmp)
                    val blobValues = ContentValues()
                    blobValues.put("Image", blob)
                    myDabase.updateImage(partID,colorIDx,blobValues)
                    My!!.close()
                }



            } catch (ex: Exception) {
                Log.e("---Exception", ex.toString())
            }
            return "success"
        }


    }

    fun getBytesFromBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        return stream.toByteArray()
    }



    private inner class XmlDownloader: AsyncTask<String, Int, String>(){

        override fun doInBackground(vararg params: String?): String { //params - full url
            try {

                var filename = "downloadedFile.xml";

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



