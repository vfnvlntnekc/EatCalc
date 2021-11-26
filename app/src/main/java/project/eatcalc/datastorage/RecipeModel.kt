package project.eatcalc.datastorage

class RecipeModel {
    @JvmField
    var id: String? = null
    @JvmField
    var name: String? = null
    @JvmField
    var method: String? = null
    @JvmField
    var portions = 0
    @JvmField
    var price = 0.0
    var mass = 0.0
    @JvmField
    var ingredients: String? = null
    @JvmField
    var ingredients_counts: String? = null
        //get() = field
    @JvmField
    var photo: String? = null
    @JvmField
    var currencyId: String? = null
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
        id: String?,
        name: String?,
        method: String?,
        portions: Int,
        price: Double,
        mass: Double,
        ingredients: String?,
        ingredients_counts: String?,
        photo: String?,
        currencyId: String?,
        calories: Double,
        proteins: Double,
        fats: Double,
        carbohydrates: Double,
        icon: Int
    ) {
        this.id = id
        this.name = name
        this.method = method
        this.portions = portions
        this.price = price
        this.mass = mass
        this.ingredients = ingredients
        this.ingredients_counts = ingredients_counts
        this.photo = photo
        this.currencyId = currencyId
        this.calories = calories
        this.proteins = proteins
        this.fats = fats
        this.carbohydrates = carbohydrates
        this.icon = icon
    }
}