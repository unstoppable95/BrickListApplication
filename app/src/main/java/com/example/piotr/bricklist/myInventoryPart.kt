package com.example.piotr.bricklist

class myInventoryPart {
    var id:Int? = null // dodajemi sami do bazu
    var inventoryID:Int? = null //dodajemy sami do bazy

    var itemType:String? = null // code from ItemTypes, znajuduje sie w xml
    var typeID:Int? = null //wskazuje na itemtypes
    var itemID:String? = null // wskazuje klocek w code obrazka , jest w xml
    var colorID:Int? = null  // wskazuuje klocek w code do obrazka

    var color:Int? = null //kolor z xml do wyszkkuania colorID

    var quantityInSet:Int? = null //jest w xml qty
    var quantityInStore:Int = 0 // ile zebralem
    var extra:String? = null // extra jest w xml
    var name:String? = null // jest w Parts w bazie, wybieram na podstawie partID where id = partsid
    var partID:Int? = null // jest to itemID z xml , where code =
    var designID:Int? = null //  pole Code w codes laczy itemid i colorid, val query = "select code from codes where itemid=" + part.partID +" and colorid=" + part.colorID
    var image:ByteArray ?= null // obrazek z codes

    override fun toString(): String {
        return id.toString() + " "+ inventoryID.toString()+ " " + itemType + " "+ typeID.toString()+ " "+
                itemID + " " + color.toString() + " "+ colorID.toString()+ " " +
                quantityInSet.toString()+ " " + quantityInStore.toString() + " "+
                extra + " "+ name + " "+ partID.toString() + " " + designID.toString()
    }


}