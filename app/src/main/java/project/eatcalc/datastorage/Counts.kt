package project.eatcalc.datastorage

import java.io.Serializable

class Counts : Serializable{
    @JvmField
    var countsIds: String? = null
    @JvmField
    var countsNums: String? = null

    constructor() {}
    constructor(
        countsIds: String?,
        countsNums: String?
    ) {
        this.countsIds =  countsIds
        this.countsNums =  countsNums
    }
}
