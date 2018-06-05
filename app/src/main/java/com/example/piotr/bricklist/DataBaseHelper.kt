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

    fun updateImage(partID:Int, colorID:Int, image:ContentValues){
        val db = writableDatabase
        val selection = "ColorID = " + colorID + " and ItemID = " + partID
        db.update("CODES", image, selection, null)
        db.close()
    }

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

    fun getMyInventoryIDIP(nameIn: String?):Int{
        var myID =0
        val query = "select id from Inventories where name = '"+nameIn+"'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            myID=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return myID
    }

    fun getItemTypeIP(typeID : Int?) :String{
        var myType =""
        val query = "select code from ItemTypes where id = " +typeID
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            myType=cursor.getString(0)
        }
        cursor.close()
        db.close()
        return myType
    }

    fun getColorIP(colID : Int?) :Int{
        var myColor =0
        val query = "select code from colors where id = " +colID
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            myColor=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return myColor
    }

    fun getNameIP(IDdatabase : Int?) :String{
        var myName =""
        val query = "select name from Parts where id = " +IDdatabase
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            myName=cursor.getString(0)
        }
        cursor.close()
        db.close()
        return myName
    }


    fun getItemIDIP(IDdatabase : Int?) :String{
        var myName =""
        val query = "select code from Parts where id = " +IDdatabase
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            myName=cursor.getString(0)
        }
        cursor.close()
        db.close()
        return myName
    }

    fun getImageIP(desingn:Int?):ByteArray ?{
        var image:ByteArray ?= null
        val query = "select Image from Codes where Code = " + desingn
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()) {
            image = cursor.getBlob(0)
        }
        cursor.close()
        db.close()
        return image


    }
    fun updateInStore(quantityInStore :Int ,id :Int?) {
        val db = writableDatabase
        val query = "UPDATE InventoriesParts SET QuantityInStore = " + quantityInStore + " WHERE id = " + id
        db.execSQL(query)
        db.close()
    }

    fun updatelastAccessed(myInventoryID :Int ,date : Int){
        val db = writableDatabase
        val query = "UPDATE Inventories SET LastAccessed = " + date + " WHERE id = " + myInventoryID
        db.execSQL(query)
        db.close()
    }

    fun getInventoryNameByDate( date :Int) :String {
        var myName =""
        val query = "select name from Inventories where lastAccessed = " +date
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            myName=cursor.getString(0)
        }
        cursor.close()
        db.close()
        return myName
    }

    fun getColorName(idCol :Int?):String{

        var color =""
        val query = "select name from Colors where id = " +idCol
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            color=cursor.getString(0)
        }
        cursor.close()
        db.close()
        return color
    }

    fun getMyInventoriesPart(inxentoryName: String) : ArrayList<myInventoryPart> {
        val inventoriesPart : ArrayList<myInventoryPart> = java.util.ArrayList()


        val query = "SELECT * FROM InventoriesParts WHERE InventoryID = " + getMyInventoryIDIP(inxentoryName)

        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var inventoryPart =myInventoryPart()

        if(cursor.moveToFirst()){
            inventoryPart.id = Integer.parseInt(cursor.getString(0))
            inventoryPart.inventoryID = Integer.parseInt(cursor.getString(1))
            inventoryPart.typeID = Integer.parseInt(cursor.getString(2))
            inventoryPart.itemIDDatabase = Integer.parseInt(cursor.getString(3))
            inventoryPart.quantityInSet = Integer.parseInt(cursor.getString(4))
            inventoryPart.quantityInStore = Integer.parseInt(cursor.getString(5))
            inventoryPart.colorID = Integer.parseInt(cursor.getString(6))
            inventoryPart.extra = cursor.getString(7)
            inventoryPart.itemType = getItemTypeIP(inventoryPart.typeID)
            inventoryPart.color =getColorIP(inventoryPart.colorID!!)
            inventoryPart.name = getNameIP(inventoryPart.itemIDDatabase)
            inventoryPart.itemIDXML=getItemIDIP(inventoryPart.itemIDDatabase)
            inventoryPart.designID = getDesignId(inventoryPart.colorID,inventoryPart.itemIDDatabase)
            inventoryPart.image=getImageIP( inventoryPart.designID )
            inventoryPart.colorName=getColorName(inventoryPart.colorID)
            inventoriesPart.add(inventoryPart)
        }
        while(cursor.moveToNext()){
            var inventoryPart =myInventoryPart()
            inventoryPart.id = Integer.parseInt(cursor.getString(0))
            inventoryPart.inventoryID = Integer.parseInt(cursor.getString(1))
            inventoryPart.typeID = Integer.parseInt(cursor.getString(2))
            inventoryPart.itemIDDatabase = Integer.parseInt(cursor.getString(3))
            inventoryPart.quantityInSet = Integer.parseInt(cursor.getString(4))
            inventoryPart.quantityInStore = Integer.parseInt(cursor.getString(5))
            inventoryPart.colorID = Integer.parseInt(cursor.getString(6))
            inventoryPart.extra = cursor.getString(7)
            inventoryPart.itemType = getItemTypeIP(inventoryPart.typeID)
            inventoryPart.color =getColorIP(inventoryPart.colorID)
            inventoryPart.name = getNameIP(inventoryPart.itemIDDatabase!!)
            inventoryPart.itemIDXML=getItemIDIP(inventoryPart.itemIDDatabase)
            inventoryPart.designID = getDesignId(inventoryPart.colorID,inventoryPart.itemIDDatabase)
            inventoryPart.image=getImageIP( inventoryPart.designID )
            inventoryPart.colorName=getColorName(inventoryPart.colorID)
            inventoriesPart.add(inventoryPart)
        }
        cursor.close()
        db.close()
        return inventoriesPart

    }

    fun getMyInventories(): ArrayList<myInventory>{
        val inventories : ArrayList<myInventory> = java.util.ArrayList()

        val query = "Select * FROM Inventories"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var inventory =myInventory()

        if(cursor.moveToFirst()){
            inventory.id = Integer.parseInt(cursor.getString(0))
            inventory.name = cursor.getString(1)
            inventory.active = Integer.parseInt(cursor.getString(2))
            inventory.lastAccessed = Integer.parseInt(cursor.getString(3))
            inventories.add(inventory)
        }

        while(cursor.moveToNext()){
            var inventory =myInventory()
            inventory.id = Integer.parseInt(cursor.getString(0))
            inventory.name = cursor.getString(1)
            inventory.active = Integer.parseInt(cursor.getString(2))
            inventory.lastAccessed = Integer.parseInt(cursor.getString(3))
            inventories.add(inventory)
        }
        cursor.close()
        db.close()
        return inventories
    }

    fun getDesignId( colorID: Int? , itemID : Int?) : Int{
        var designID: Int =0;
        val query = "select code from Codes where itemID = "+itemID+" and colorID = " +colorID
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            designID=Integer.parseInt(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return designID
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
        val query = "select id from Colors where code = '" + color + "'"
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

    fun imageExists(designID: Int?):Int{
        var isInDataBase :Int=0
        val query = "select Image from Codes where code = " + designID
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        if(cursor.moveToFirst()){
            if(cursor.getBlob(0) == null){
                isInDataBase=0
            }else{
                isInDataBase=1
            }
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