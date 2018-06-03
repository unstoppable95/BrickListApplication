package com.example.piotr.bricklist

class myInventory {

    var id:Int? = null
    var name:String? = null
    var active:Int? = null
    var lastAccessed:Int ? = null
   // var parts:ArrayList<myInventoryPart>? = null

    constructor(){}

    constructor(id:Int?, name:String?, active:Int?, lastAccessed:Int?){
        this.id = id
        this.name = name
        this.active = active
        this.lastAccessed = lastAccessed
    }

//    constructor(name:String?, active:Int?, lastAccessed:Int){
//        this.name = name
//        this.active = active
//        this.lastAccessed = lastAccessed
//    }


}


