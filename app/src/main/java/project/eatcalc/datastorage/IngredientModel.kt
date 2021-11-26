package project.eatcalc.datastorage

class IngredientModel {
    @JvmField
    var id: String? = null
    @JvmField
    var count = 0.0
    @JvmField
    var name: String? = null
    @JvmField
    var price = 0.0
    @JvmField
    var unit_id: String? = null
    @JvmField
    var currency_id: String? = null
    @JvmField
    var calories = 0.0
    @JvmField
    var proteins = 0.0
    @JvmField
    var fats = 0.0
    @JvmField
    var carbohydrates = 0.0
    @JvmField
    var icon = 0

    constructor() {}
    constructor(
        name: String?,
        price: Double,
        unit_id: String?,
        currency_id: String?,
        calories: Double,
        proteins: Double,
        fats: Double,
        carbohydrates: Double,
        icon: Int
    ) : this(
        1.0,
        name,
        price,
        unit_id,
        currency_id,
        calories,
        proteins,
        fats,
        carbohydrates,
        icon
    ) {
    }

    constructor(
        count: Double,
        name: String?,
        price: Double,
        unit_id: String?,
        currency_id: String?,
        calories: Double,
        proteins: Double,
        fats: Double,
        carbohydrates: Double,
        icon: Int
    ) {
        this.count = count
        this.name = name
        this.price = price
        this.unit_id = unit_id
        this.currency_id = currency_id
        this.calories = calories
        this.proteins = proteins
        this.fats = fats
        this.carbohydrates = carbohydrates
        this.icon = icon
    }

    // TODO: механизм получения валюты из id в string
    val currency: String
        get() =// TODO: механизм получения валюты из id в string
            currency_id.toString()

    // TODO: механизм получения единицы измерения из id в string
    val unit: String
        get() =// TODO: механизм получения единицы измерения из id в string
            unit_id.toString()
}