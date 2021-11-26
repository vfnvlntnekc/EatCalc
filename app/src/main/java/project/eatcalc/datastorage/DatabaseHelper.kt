package project.eatcalc.datastorage

import android.database.sqlite.SQLiteOpenHelper
import project.eatcalc.datastorage.DatabaseHelper
import android.database.sqlite.SQLiteDatabase
import project.eatcalc.R
import project.eatcalc.ImagePickUpUtil
import android.app.Activity
import java.io.InputStream
import java.io.File
import android.os.Environment
import java.io.FileOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import project.eatcalc.datastorage.IngredientModel
import kotlin.jvm.Synchronized
import project.eatcalc.datastorage.RecipeModel
import java.util.ArrayList

class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        val queryI = "CREATE TABLE " + TABLE_INGREDIENTS +
                "(ID INTEGER PRIMARY KEY, Count REAL, Name TEXT, Price REAL, UnitId TEXT, CurrencyId TEXT, Calories REAL, Proteins REAL, Fats REAL, Carbs REAL, Icon INTEGER)"
        val queryR = "CREATE TABLE " + TABLE_RECIPES +
                "(ID INTEGER PRIMARY KEY, Name TEXT, Method TEXT, Portions INTEGER, Price REAL, Mass REAL,  Ingredients TEXT, IngredientsCounts TEXT, Photo TEXT, CurrencyId TEXT, Calories REAL, Proteins REAL, Fats REAL, Carbs REAL, Icon INTEGER)"
        db.execSQL(queryI)
        db.execSQL(queryR)
        val units = context.resources.getStringArray(R.array.units)
        val currs = context.resources.getStringArray(R.array.currency)
        createIngredient(
            db, 1.0, "Картофель", 0.035, units[1], currs[0],
            0.77, 0.02, 0.0009, 0.17, 1
        )
        createIngredient(
            db, 1.0, "Морковь", 0.04, units[1], currs[0],
            0.4, 0.002, 0.009, 0.068, 1
        )
        createIngredient(
            db, 1.0, "Лук репчатый", 0.04, units[1], currs[0],
            0.41, 0.014, 0.002, 0.082, 1
        )
        createIngredient(
            db, 1.0, "Вермишель", 0.08, units[1], currs[0],
            1.19, 0.035, 0.0032, 0.249, 0
        )
        createIngredient(
            db, 1.0, "Курица", 0.035, units[1], currs[0],
            1.65, 0.242, 0.076, 0.0063, 2
        )
        createIngredient(
            db, 1.0, "Лист лавровый", 2.4, units[1], currs[0],
            0.0, 0.0, 0.0, 0.0, 4
        )
        createIngredient(db, 1.0, "Перец чёрный", 0.15, units[1], currs[0], 0.0, 0.0, 0.0, 0.0, 4)
        createIngredient(db, 1.0, "Соль", 0.01, units[1], currs[0], 0.0, 0.0, 0.0, 0.0, 4)
        createIngredient(
            db, 1.0, "Масло растительное", 0.1, units[3], currs[0],
            8.8, 0.0012, 0.96, 0.14, 3
        )
        createIngredient(db, 1.0, "Вода", 0.0, units[3], currs[0], 0.0, 0.0, 0.0, 0.0, 3)
        createIngredient(
            db, 1.0, "Свинина", 0.3, units[1], currs[0],
            2.6, 0.15, 0.208, 0.033, 2
        )
        createIngredient(
            db, 1.0, "Чеснок", 0.1, units[1], currs[0],
            1.26, 0.066, 0.0088, 0.234, 4
        )
        createIngredient(db, 1.0, "Паприка", 0.15, units[1], currs[0], 0.0, 0.0, 0.0, 0.0, 4)
        createIngredient(
            db, 1.0, "Укроп", 0.5, units[1], currs[0],
            0.5, 0.036, 0.01, 0.08, 4
        )
        createIngredient(
            db, 1.0, "Тыква", 0.13, units[1], currs[0],
            0.22, 0.01, 0.001, 0.044, 1
        )
        createIngredient(
            db, 1.0, "Молоко", 0.05, units[3], currs[0],
            0.59, 0.029, 0.032, 0.047, 3
        )
        createIngredient(db, 1.0, "Сахар", 0.045, units[1], currs[0], 4.0, 0.0, 0.0, 0.997, 4)
        createIngredient(
            db, 1.0, "Масло сливочное", 1.0, units[1], currs[0],
            7.48, 0.006, 0.825, 0.008, 0
        )
        createIngredient(
            db, 1.0, "Сыр пармезан", 1.33, units[1], currs[0],
            3.92, 0.33, 0.28, 0.0, 0
        )
        createIngredient(
            db, 1.0, "Батон белый", 0.066, units[1], currs[0],
            2.62, 0.075, 0.029, 0.514, 0
        )
        createIngredient(
            db, 1.0, "Салат зелёный", 0.47, units[1], currs[0],
            2.62, 0.075, 0.029, 0.514, 1
        )
        createIngredient(
            db, 1.0, "Помидоры черри", 12.0, units[0], currs[0],
            0.15, 0.01, 0.02, 0.04, 1
        )
        createIngredient(
            db, 1.0, "Яйцо", 8.0, units[0], currs[0],
            1.57, 0.127, 0.115, 0.007, 0
        )
        createIngredient(
            db, 1.0, "Масло оливковое", 1.2, units[3], currs[0],
            8.98, 0.0, 0.998, 0.0, 3
        )
        createIngredient(
            db, 1.0, "Горчица", 0.21, units[1], currs[0],
            1.43, 0.099, 0.053, 0.127, 4
        )
        createIngredient(
            db, 1.0, "Вишня", 0.43, units[1], currs[0],
            0.52, 0.008, 0.002, 0.106, 1
        )
        createIngredient(
            db, 1.0, "Мука", 0.05, units[1], currs[0],
            2.25, 0.09, 0.03, 0.406, 0
        )
        createIngredient(
            db, 1.0, "Сода", 0.042, units[1], currs[0],
            1.45, 0.0973, 0.0956, 0.0494, 0
        )
        createIngredient(
            db, 1.0, "Творог", 0.42, units[1], currs[0],
            1.45, 0.18, 0.09, 0.03, 0
        )
        val currency = context.resources.getString(R.string.rub)
        var method = "Куриное филе вымойте, обсушите, срежьте с филе пленки и нарежьте его крупно."
        createRecipe(
            db, "Куриный суп", method, 6,
            "1,2,3,4,5,6,7,8,9,10", "300,100,100,100,500,2,1,1,100,2000", currency,
            2, "chicken_soup.jpg", R.raw.chicken_soup
        )
        method =
            """Мясо моем и осушиваем бумажными полотенцами. Нарезаем не сильно мелкими кусочками.
Разогреваем сковородку или казанок с толстым дном и вливаем масло. Мясо отправляем в горячее масло и жарим на большом огне, помешивая каждую минуту.
Лук нарезаем крупными кубиками, морковь - полукружиями.
Как только мясо начало золотится, сразу добавляем туда лук и морковь. Перемешиваем и включаем средний огонь. Готовим под крышкой до прозрачности лука.
Нарезаем картофель кубиками.

Солим, добавляем специи и перемешиваем. Уменьшаем температуру до минимума, добавляем картофель к мясу, но сразу не перемешиваем. Заливаем водой (0,25-0,5 стакана), накрываем крышкой и тушим 20-25 минут.
Перемешиваем, солим и добавляем выдавленный через пресс чеснок. Заливаем жаркое 3-4 половниками кипятка. Чтобы бульон не "потянул" всю соль с продуктов, в половник добавляем немного соли (0,25 ч.ложки) и растворяем. (Если вы решили готовить жаркое с томатом, то разведите пасту с сахаром в вышеупомянутом солёном бульоне и залейте содержимое сковородки.) Готовим еще 5 минут на среднем огне.
Жаркое из свинины с картофелем готово. Перед подачей посыпаем рубленой зеленью.
Приятного аппетита!"""
        createRecipe(
            db, "Жаркое по-домашнему", method, 6,
            "1,2,3,11,6,7,12,13,14", "1000,100,200,800,1,1,3,5,5", currency,
            3, "dish.jpg", R.raw.dish
        )
        method = """
            Очистить тыкву, мелко нарезать.
            Молоко вскипятить. Подготовленную тыкву опустить в теплое молоко.
            
            Довести до кипения, отварить на небольшом огне под крышкой до готовности (около 20 минут).
            Тыкву взбить блендером или протереть сквозь сито.
            Затем положить на каждые 400 г тыквы по 1/2 ч. ложки соли, по 1/2 ч. ложки сахара и по 2 ч. ложки сливочного масла.
            Все хорошо перемешать. Тыквенная каша на молоке готова.
            
            """.trimIndent()
        createRecipe(
            db, "Тыквенная каша", method, 2,
            "15,16,8,17,18", "400,250,0.5,0.5,20", currency,
            1, "pumpkin_breakfast.jpg", R.raw.pumpkin_breakfast
        )
        method = """
            Куриное филе нарезать и обжарить на разогретой сковороде с оливковым маслом до легкой румяности (около 10 минут).
            Можно немного отойти от классики и отварить куриное филе в подсоленной воде до мягкости (20-25 минут), а затем нарезать кусочками или разделить руками на полоски.
            Батон (желательно без корки) нарезать кубиками и подсушить в духовке (минут 15-20 при температуре 170-180 градусов). Или обжарить на сухой сковороде до румяности, помешивая. Это когда Цезарь простой.
            Но можно на сковороде нагреть немного оливкового масла с очищенным зубчиком чеснока, затем чеснок вынуть и обжаривать крутоны (гренки). Можно перемешать кусочки батона с оливковым маслом и высушить в духовке.
            Заправка: перемешать измельченный/выдавленный чеснок, горчицу, соль, перец, оливковое масло, добавить лимонный сок (можно добавить чайную ложку майонеза, но и без него заправка хороша).
            Разрезать помидорки черри пополам. Яйца отварить вкрутую и крупно нарезать. Салатные листья вымыть, обсушить, порвать руками на кусочки и выложить на тарелку. (Орехи, если используете, порубить - лучше подсушенные в духовке или на сковороде.)
            Добавить к салатным листьям курицу, полить заправкой. Можно перемешать с заправкой только салатные листья, а затем сверху выложить кусочки курицы.
            Добавить в салат помидоры, яйца, натереть сверху сыр, добавить гренки. Салат сразу же подавать к столу.
            """.trimIndent()
        createRecipe(
            db, "Салат «Цезарь»", method, 2,
            "5,19,20,21,22,23,24,12,25,8,7", "250,70,100,100,15,2,70,1,5,1,1", currency,
            4, "cesare.jpg", R.raw.cesare
        )
        method = """
            Вишню перебрать и промыть.
            Слегка обдать кипятком.
            Сразу же переложить в банки.
            Сварить сироп, всыпав в воду сахар и доведя до кипения.
            Кипящим сиропом залить банки с вишней и сразу же закатать.
            Перевернуть банки вверх дном и укутать компот из вишни до полного остывания.
            Готовый компот из вишни хранить в кладовке при комнатной температуре или в погребе.
            """.trimIndent()
        createRecipe(
            db, "Компот из вишни", method, 12,
            "26,10,17", "300,2600,300", currency,
            7, "cherry.jpg", R.raw.cherry
        )
        method =
            "Сначала готовим начинку. В творог вбиваем яйца, смешаем погружным блендером.\nГотовим тесто. Сперва натрем на крупной терке замороженное масло или маргарин.\nДобавляем муку, сахар, соль и соду. Все перетираем в крошку.\nВыкладываем начинку и равномерно распределяем по поверхности.\nДаем остыть королевской ватрушке в форме, а затем вынимаем.\nПриятного аппетита!"
        createRecipe(
            db, "Королевская ватрушка", method, 8,
            "27,18,17,8,28,29,23", "750,200,500,0.5,0.5,500,4", currency,
            6, "cake.jpg", R.raw.cake
        )
        method =
            "Картофель очищаем и натираем на крупной терке, сразу выкладываем на сито или дуршлаг. Отжимаем картофельный сок.\nДобавляем яйца, соль, перец.\nПеремешиваем\nЖарим картофельные драники на среднем огне в достаточном количестве растительного масла до румяности с обеих сторон.\nПодаем картофельные драники со сметаной или с яблочным пюре.\n"
        createRecipe(
            db, "Картофельные драники", method, 3,
            "1,23,8,7,9", "500,2,1,1,50", currency,
            3, "dranic.jpg", R.raw.dranic
        )
    }

    fun copyFileFromRaw(filename: String?, res_id: Int): String {
        ImagePickUpUtil.verifyStoragePermissions(context as Activity)
        val inputStream = context.getResources().openRawResource(res_id)
        val direct = File(Environment.getExternalStorageDirectory().path + "/EatCalc/pictures")
        if (!direct.exists()) {
            direct.mkdirs()
        }
        val fi = File(direct, filename)
        if (fi.exists()) {
            fi.delete()
        }
        val photoPath = fi.absolutePath
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(fi)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val buf = ByteArray(1024)
        var len: Int
        if (outputStream != null) {
            try {
                while (inputStream.read(buf).also { len = it } != -1) {
                    outputStream.write(buf, 0, len)
                }
                outputStream.close()
                inputStream.close()
            } catch (e: IOException) {
            }
        }
        return photoPath
    }

    private fun createIngredient(
        db: SQLiteDatabase,
        count: Double,
        name: String,
        price: Double,
        unit_id: String,
        currency_id: String,
        calories: Double,
        proteins: Double,
        fats: Double,
        carbohydrates: Double,
        icon: Int
    ) {
        val values = ContentValues()
        values.put("Count", count)
        values.put("Name", name)
        values.put("Price", price)
        values.put("UnitId", unit_id)
        values.put("CurrencyId", currency_id)
        values.put("Calories", calories)
        values.put("Proteins", proteins)
        values.put("Fats", fats)
        values.put("Carbs", carbohydrates)
        values.put("Icon", icon)
        db.insert(TABLE_INGREDIENTS, null, values)
    }

    private fun createRecipe(
        db: SQLiteDatabase,
        name: String,
        method: String,
        portions: Int,
        ingredients: String,
        ingredients_counts: String,
        currency_id: String,
        icon: Int,
        filename: String,
        resId: Int
    ) {
        val photo = copyFileFromRaw(filename, resId)
        var price = 0.0
        var calories = 0.0
        var proteins = 0.0
        var fats = 0.0
        var carbohydrates = 0.0
        val ingredientsCountsString = ingredients_counts.split(",").toTypedArray()
        val ingredientsCountsIDs = ingredients.split(",").toTypedArray()
        for (i in ingredientsCountsIDs.indices) {
            if (!ingredientsCountsIDs[i].trim { it <= ' ' }.isEmpty()) {
                val cursor = db.rawQuery(
                    "SELECT *FROM " + TABLE_INGREDIENTS + " WHERE ID=" + ingredientsCountsIDs[i],
                    null
                )
                cursor.moveToFirst()
                val ingredient = IngredientModel()
                ingredient.price = cursor.getDouble(3)
                ingredient.calories = cursor.getDouble(6)
                ingredient.proteins = cursor.getDouble(7)
                ingredient.fats = cursor.getDouble(8)
                ingredient.carbohydrates = cursor.getDouble(9)
                cursor.close()
                val count = ingredientsCountsString[i].toDouble()
                if (count != 0.0) {
                    price += ingredient.price * count
                    carbohydrates += ingredient.carbohydrates * count
                    calories += ingredient.calories * count
                    fats += ingredient.fats * count
                    proteins += ingredient.proteins * count
                }
            }
        }
        val values = ContentValues()
        values.put("Name", name)
        values.put("Method", method)
        values.put("Portions", portions)
        values.put("Price", price)
        values.put("Mass", 0)
        values.put("Ingredients", ingredients)
        values.put("IngredientsCounts", ingredients_counts)
        values.put("Photo", photo)
        values.put("CurrencyId", currency_id)
        values.put("Calories", calories)
        values.put("Proteins", proteins)
        values.put("Fats", fats)
        values.put("Carbs", carbohydrates)
        values.put("Icon", icon)
        db.insert(TABLE_RECIPES, null, values)
    }

    @Synchronized
    override fun close() {
        super.close()
    }

    //upgrading database
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES)
        onCreate(db)
    }

    //add the new ingredient
    fun addIngredient(
        count: Double, name: String?, price: Double, unit_id: String?, currency_id: String?,
        calories: Double, proteins: Double, fats: Double, carbohydrates: Double, icon: Int
    ) {
        val sqLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put("Count", count)
        values.put("Name", name)
        values.put("Price", price)
        values.put("UnitId", unit_id)
        values.put("CurrencyId", currency_id)
        values.put("Calories", calories)
        values.put("Proteins", proteins)
        values.put("Fats", fats)
        values.put("Carbs", carbohydrates)
        values.put("Icon", icon)
        sqLiteDatabase.insert(TABLE_INGREDIENTS, null, values)
        sqLiteDatabase.close()
    }

    fun addRecipe(
        name: String?, method: String?, portions: Int, price: Double, mass: Double,
        ingredients: String?, ingredients_counts: String?, photo: String?, currency_id: String?,
        calories: Double, proteins: Double, fats: Double, carbohydrates: Double, icon: Int
    ) {
        val sqLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put("Name", name)
        values.put("Method", method)
        values.put("Portions", portions)
        values.put("Price", price)
        values.put("Mass", mass)
        values.put("Ingredients", ingredients)
        values.put("IngredientsCounts", ingredients_counts)
        values.put("Photo", photo)
        values.put("CurrencyId", currency_id)
        values.put("Calories", calories)
        values.put("Proteins", proteins)
        values.put("Fats", fats)
        values.put("Carbs", carbohydrates)
        values.put("Icon", icon)
        sqLiteDatabase.insert(TABLE_RECIPES, null, values)
        sqLiteDatabase.close()
    }

    fun getIngredientById(id: String): IngredientModel {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT *FROM " + TABLE_INGREDIENTS + " WHERE ID=" + id, null)
        cursor.moveToFirst()
        val ingredientModel = IngredientModel()
        ingredientModel.id = cursor.getInt(0).toString()
        ingredientModel.count = cursor.getDouble(1)
        ingredientModel.name = cursor.getString(2)
        ingredientModel.price = cursor.getDouble(3)
        ingredientModel.unit_id = cursor.getString(4)
        ingredientModel.currency_id = cursor.getString(5)
        ingredientModel.calories = cursor.getDouble(6)
        ingredientModel.proteins = cursor.getDouble(7)
        ingredientModel.fats = cursor.getDouble(8)
        ingredientModel.carbohydrates = cursor.getDouble(9)
        ingredientModel.icon = cursor.getInt(10)
        cursor.close()
        return ingredientModel
    }

    fun getRecipeById(id: String): RecipeModel {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT *FROM " + TABLE_RECIPES + " WHERE ID=" + id, null)
        cursor.moveToFirst()
        val recipeModel = extractRecipe(cursor)
        //ID INTEGER PRIMARY KEY, Name TEXT, Method TEXT, Portions INTEGER, Price REAL, Mass REAL,  Ingredients TEXT, IngredientsCounts TEXT, Photo TEXT, CurrencyId TEXT, Calories REAL, Proteins REAL, Fats REAL, Carbs REAL
        cursor.close()
        return recipeModel
    }// select all query

    // looping through all rows and adding to list
    //get the all ingredients
    val ingredients: ArrayList<IngredientModel>
        get() {
            val arrayList = ArrayList<IngredientModel>()

            // select all query
            val select_query = "SELECT *FROM " + TABLE_INGREDIENTS
            val db = this.writableDatabase
            val cursor = db.rawQuery(select_query, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    val ingredientModel = IngredientModel()
                    ingredientModel.id = cursor.getString(0)
                    ingredientModel.count = cursor.getDouble(1)
                    ingredientModel.name = cursor.getString(2)
                    ingredientModel.price = cursor.getDouble(3)
                    ingredientModel.unit_id = cursor.getString(4)
                    ingredientModel.currency_id = cursor.getString(5)
                    ingredientModel.calories = cursor.getDouble(6)
                    ingredientModel.proteins = cursor.getDouble(7)
                    ingredientModel.fats = cursor.getDouble(8)
                    ingredientModel.carbohydrates = cursor.getDouble(9)
                    ingredientModel.icon = cursor.getInt(10)
                    arrayList.add(ingredientModel)
                } while (cursor.moveToNext())
                cursor.close()
            }
            return arrayList
        }// select all query

    // looping through all rows and adding to list
    //get the all ingredients
    val recipes: ArrayList<RecipeModel>
        get() {
            val arrayList = ArrayList<RecipeModel>()

            // select all query
            val select_query = "SELECT *FROM " + TABLE_RECIPES
            val db = this.writableDatabase
            val cursor = db.rawQuery(select_query, null)

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    arrayList.add(extractRecipe(cursor))
                } while (cursor.moveToNext())
                cursor.close()
            }
            return arrayList
        }

    //get the all ingredients
    fun getRecipesFromType(type: Int): ArrayList<RecipeModel> {
        val arrayList = ArrayList<RecipeModel>()

        // select all query
        val select_query = "SELECT *FROM " + TABLE_RECIPES + " WHERE Icon=" + type
        val db = this.writableDatabase
        val cursor = db.rawQuery(select_query, null)

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(extractRecipe(cursor))
            } while (cursor.moveToNext())
            cursor.close()
        }
        return arrayList
    }

    private fun extractRecipe(cursor: Cursor): RecipeModel {
        val recipeModel = RecipeModel()
        recipeModel.id = cursor.getString(0)
        recipeModel.name = cursor.getString(1)
        recipeModel.method = cursor.getString(2)
        recipeModel.portions = cursor.getInt(3)
        recipeModel.price = cursor.getDouble(4)
        recipeModel.mass = cursor.getDouble(5)
        recipeModel.ingredients = cursor.getString(6)
        recipeModel.ingredients_counts = cursor.getString(7)
        recipeModel.photo = cursor.getString(8)
        recipeModel.currencyId = cursor.getString(9)
        recipeModel.calories = cursor.getDouble(10)
        recipeModel.proteins = cursor.getDouble(11)
        recipeModel.fats = cursor.getDouble(12)
        recipeModel.carbohydrates = cursor.getDouble(13)
        recipeModel.icon = cursor.getInt(14)
        return recipeModel
    }

    fun deleteIngredient(ID: String) {
        val sqLiteDatabase = this.writableDatabase
        sqLiteDatabase.delete(TABLE_INGREDIENTS, "ID=$ID", null)
        sqLiteDatabase.close()
    }

    fun deleteRecipe(ID: String) {
        val sqLiteDatabase = this.writableDatabase
        sqLiteDatabase.delete(TABLE_RECIPES, "ID=$ID", null)
        sqLiteDatabase.close()
    }

    fun updateIngredient(
        ID: String, count: Double, name: String?, price: Double, unit_id: String?,
        currency_id: String?, calories: Double, proteins: Double, fats: Double,
        carbohydrates: Double, icon: Int
    ) {
        val sqLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put("Count", count)
        values.put("Name", name)
        values.put("Price", price)
        values.put("UnitId", unit_id)
        values.put("CurrencyId", currency_id)
        values.put("Calories", calories)
        values.put("Proteins", proteins)
        values.put("Fats", fats)
        values.put("Carbs", carbohydrates)
        values.put("Icon", icon)
        sqLiteDatabase.update(TABLE_INGREDIENTS, values, "ID=$ID", null)
        sqLiteDatabase.close()
    }

    fun updateRecipe(
        ID: String, name: String?, method: String?, portions: Int, price: Double,
        mass: Double, ingredients: String?, ingredients_counts: String?,
        photo: String?, currency_id: String?, calories: Double, proteins: Double,
        fats: Double, carbohydrates: Double, icon: Int
    ) {
        val sqLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put("Name", name)
        values.put("Method", method)
        values.put("Portions", portions)
        values.put("Price", price)
        values.put("Mass", mass)
        values.put("Ingredients", ingredients)
        values.put("IngredientsCounts", ingredients_counts)
        values.put("Photo", photo)
        values.put("CurrencyId", currency_id)
        values.put("Calories", calories)
        values.put("Proteins", proteins)
        values.put("Fats", fats)
        values.put("Carbs", carbohydrates)
        values.put("Icon", icon)
        sqLiteDatabase.update(TABLE_RECIPES, values, "ID=$ID", null)
        sqLiteDatabase.close()
    }

    fun updateRecipe(recipe: RecipeModel) {
        val sqLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put("Name", recipe.name)
        values.put("Method", recipe.method)
        values.put("Portions", recipe.portions)
        values.put("Price", recipe.price)
        values.put("Mass", recipe.mass)
        values.put("Ingredients", recipe.ingredients)
        values.put("IngredientsCounts", recipe.ingredients_counts)
        values.put("Photo", recipe.photo)
        values.put("CurrencyId", recipe.currencyId)
        values.put("Calories", recipe.calories)
        values.put("Proteins", recipe.proteins)
        values.put("Fats", recipe.fats)
        values.put("Carbs", recipe.carbohydrates)
        values.put("Icon", recipe.icon)
        sqLiteDatabase.update(TABLE_RECIPES, values, "ID=" + recipe.id, null)
        sqLiteDatabase.close()
    }

    companion object {
        const val DATABASE_NAME = "eatcalc_db"
        const val DATABASE_VERSION = 16
        const val TABLE_INGREDIENTS = "tbl_inrgedients"
        const val TABLE_RECIPES = "tb2_recipes"
    }
}