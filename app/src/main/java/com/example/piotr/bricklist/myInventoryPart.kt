package com.example.piotr.bricklist

class myInventoryPart {
    var id:Int? = null
    var inventoryID:Int? = null
    var itemType:String? = null
    var typeID:Int? = null
    var itemIDXML:String? = null
    var itemIDDatabase:Int? = null
    var colorID:Int? = null
    var color:Int? = null
    var quantityInSet:Int? = null
    var quantityInStore:Int = 0
    var designID:Int? = null
    var image:ByteArray ?= null
    var extra:String? = null
    var name:String? = null

    override fun toString(): String {
        return id.toString() + " "+ inventoryID.toString()+ " " + itemType + " "+ typeID.toString()+ " "+
                itemIDXML+ " " + color.toString() + " "+ colorID.toString()+ " " +
                quantityInSet.toString()+ " " + quantityInStore.toString() + " "+
                extra + " "+ name + " "+ itemIDDatabase.toString() + " " + designID.toString()
    }


}