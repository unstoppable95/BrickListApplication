package com.example.piotr.bricklist

import android.icu.util.Output
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ListView

import kotlinx.android.synthetic.main.activity_set.*
import org.w3c.dom.Element
import java.io.File
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


class Set : AppCompatActivity() {

    private var myInventoryName:String = ""
    private var myInventoryPartList : ArrayList<myInventoryPart> ? =null
    private var myDB :DataBaseHelper = DataBaseHelper(this)
    private var myInventoryID : Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set)


        try {
            myInventoryName= getIntent().getStringExtra ("name")
            var myDB1 :DataBaseHelper = DataBaseHelper(this)

            myInventoryID=myDB1.getMyInventoryIDIP(myInventoryName)
            textView6.setText("Inventory ID in Database : " + myInventoryID.toString())

            val date = Date()

            myDB1.updatelastAccessed(myInventoryID,date.time.toInt())
            myDB1.close()

        }
        catch (e : Exception){}


        val listView: ListView = findViewById<ListView>(R.id.listView)



        myInventoryPartList=myDB.getMyInventoriesPart(myInventoryName)
        Log.i("---dlugosc listy z bazy " + myInventoryPartList!!.size,"xxx")







        var adapter = myListAdapter(this, myInventoryPartList!!, this)
        listView?.adapter = adapter
        adapter.notifyDataSetChanged()



    }


    fun export( v: View){

            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = docBuilder.newDocument()
            val rootElement: Element = doc.createElement("INVENTORY")


        makeRequestExternalStorage()
        if (checkPermission()) {

                var PartList = myDB.getMyInventoriesPart(myInventoryName)

                for (part in PartList) {
                    if (part.quantityInStore != part.quantityInSet) {
                        val rootItem = doc.createElement("ITEM")

                        val itemType = doc.createElement("ITEMTYPE")
                        itemType.appendChild(doc.createTextNode(part.itemType))
                        rootItem.appendChild(itemType)



                        val itemId = doc.createElement("ITEMID")
                        itemId.appendChild(doc.createTextNode(part.itemIDXML.toString()))
                        rootItem.appendChild(itemId)

                        val qty = doc.createElement("QTY")
                        qty.appendChild(doc.createTextNode((part.quantityInSet!! - part.quantityInStore).toString()))
                        rootItem.appendChild(qty)


                        val color = doc.createElement("COLOR")
                        color.appendChild(doc.createTextNode(part.color.toString()))
                        rootItem.appendChild(color)

                        val extra = doc.createElement("EXTRA")
                        extra.appendChild(doc.createTextNode((part.extra).toString()))
                        rootItem.appendChild(extra)

                        rootElement.appendChild(rootItem)
                    }

                }

                doc.appendChild(rootElement)



                    val tranformer: Transformer = TransformerFactory.newInstance().newTransformer()
                    tranformer.setOutputProperty(OutputKeys.INDENT, "yes")
                    tranformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")

                    val path = Environment.getExternalStorageDirectory()
                    val outDir = File(path, "Output")
                    outDir.mkdir()

                    val file = File(outDir, "text.xml")
                    Log.i("---sciezka " + file.absolutePath, "mesggasasa")

                    tranformer.transform(DOMSource(doc), StreamResult(file))

                }



    }

    private fun makeRequestExternalStorage() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    private fun checkPermission() : Boolean{
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        return true
        }
        else return false;

    }
}
