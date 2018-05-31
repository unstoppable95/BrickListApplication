package com.example.piotr.bricklist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import java.sql.SQLException


class DataBaseHelper (private val myContext: Context) : SQLiteOpenHelper(myContext, DB_NAME, null, 1) {

    private var myDataBase: SQLiteDatabase? = null

    fun addInventoryToDatabase(inventory: myInventory){
        val values = ContentValues()
        values.put("id", inventory.id)
        values.put("Name",inventory.name)
        values.put("Active",inventory.active)
        values.put("LastAccessed", inventory.lastAccessed)
        val db = this.writableDatabase
        db.insert("Inventories",null,values)
        db.close()
    }


    fun getBricSetId():Int{
        var brickSetId : Int =0;
        val query = "select count (*)+1 from Inventories"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            brickSetId=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return brickSetId
    }

    fun getBrickSetPartId():Int{
        var brickSetPartId : Int =0;
        val query = "select count (*)+1 from InventoriesParts"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            brickSetPartId=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return brickSetPartId
    }


    fun getItemID(idXML : String?):Int{
        var itemID: Int =0;
        val query = "select id from Parts where code = '"+idXML+"'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            itemID=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return itemID

    }


    fun getTypeID(itemType : String?):Int{
        var itemID: Int =0;
        val query = "select id from ItemTypes where code = '"+itemType+"'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            itemID=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return itemID

    }

    fun getColorID(color :Int?):Int{
        var colorID : Int=0
        val query = "select id from Colors where code=" +color
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            colorID=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return colorID
    }

    fun lookForPartInDataBase( idFromXml : String ?) :Int {
        var isInDataBase :Int=0
        val query = "select * from Parts where code = '" + idFromXml+ "'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            cursor.close()
            db.close()
            isInDataBase=1
        }
        cursor.close()
        db.close()
        return isInDataBase

    }



    fun addInventoryPartToDatabase( inventoryPart : myInventoryPart){
        val values = ContentValues()

        values.put("id", inventoryPart.id)
        values.put("InventoryID",inventoryPart.inventoryID)
        values.put("TypeID",inventoryPart.typeID)
        values.put("ItemID",inventoryPart.itemIDDatabase)
        values.put("QuantityInSet",inventoryPart.quantityInSet)
        values.put("QuantityInStore", 0)
        values.put("ColorID", inventoryPart.colorID)
        values.put("Extra", inventoryPart.extra)
        val db = this.writableDatabase
        db.insert("InventoriesParts",null,values)
        db.close()

    }




    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    @Throws(IOException::class)
    fun createDataBase() {

        val dbExist = checkDataBase()

        if (dbExist) {
            //do nothing - database already exist
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.readableDatabase

            try {

                copyDataBase()

            } catch (e: IOException) {

                throw Error("Error copying database")

            }

        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {

        var checkDB: SQLiteDatabase? = null

        try {
            val myPath = DB_PATH + DB_NAME
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

        } catch (e: SQLiteException) {

            //database does't exist yet.

        }

        if (checkDB != null) {

            checkDB.close()

        }

        return if (checkDB != null) true else false
    }







    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        //Open your local db as the input stream
        val myInput = myContext.getAssets().open(DB_NAME)

        // Path to the just created empty db
        val outFileName = DB_PATH + DB_NAME

        //Open the empty db as the output stream
        val myOutput = FileOutputStream(outFileName)

        //transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int =myInput.read(buffer)
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            length=myInput.read(buffer)
        }

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()

    }

    @Throws(SQLException::class)
    fun openDataBase() {

        //Open the database
        val myPath = DB_PATH + DB_NAME
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

    }

    @Synchronized
    override fun close() {

        if (myDataBase != null)
            myDataBase!!.close()

        super.close()

    }

    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {

        //The Android's default system path of your application database.
        private val DB_PATH = "/data/data/com.example.piotr.bricklist/databases/"

        private val DB_NAME = "BrickDatabase.db"
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}