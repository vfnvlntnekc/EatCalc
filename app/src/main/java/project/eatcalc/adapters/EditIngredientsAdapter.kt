package project.eatcalc.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import project.eatcalc.adapters.EditIngredientsAdapter.RecipeIngredientViewHolder
import android.view.LayoutInflater
import project.eatcalc.datastorage.IngredientModel
import java.util.HashMap
import android.view.ViewGroup
import android.view.View
import project.eatcalc.R
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import android.text.TextWatcher
import java.lang.Exception
import android.text.Editable

class EditIngredientsAdapter(
    context: Context?,
    ingredientsIDs: Array<String>?,
    ingredientsCounts: Array<String>?
) : RecyclerView.Adapter<RecipeIngredientViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var ingredients: List<IngredientModel>? = null
    private val counts = HashMap<String, String>()
    val ingredientsIDs: String
        get() = if (counts.size > 0) java.lang.String.join(",", counts.keys) else ""
    val ingredientsCounts: String
        get() = if (counts.size > 0) java.lang.String.join(",", counts.values) else ""

    init {
        if (ingredientsIDs != null && ingredientsCounts != null) {
            for (i in ingredientsIDs.indices) {
                counts[ingredientsIDs[i]] = ingredientsCounts[i]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeIngredientViewHolder {
        val itemView = inflater.inflate(R.layout.recipe_ingredients_list_item, parent, false)
        return RecipeIngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeIngredientViewHolder, position: Int) {
        if (ingredients != null) {
            holder.countListener.updatePosition(position)
            val ingredient = ingredients!![position]
            var count = counts[ingredient.id]
            if (count == null) count = "0"
            holder.count.setText(count)
            holder.units.text = ingredient.unit_id
            holder.name.text = ingredient.name
        } else {
            holder.name.setText(R.string.no_ingredients_here)
        }
    }

    override fun getItemCount(): Int {
        return if (ingredients != null) ingredients!!.size else 0
    }

    fun setIngredients(ingredients: List<IngredientModel>?) {
        this.ingredients = ingredients
        notifyDataSetChanged()
    }

    inner class RecipeIngredientViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var count: TextInputEditText = itemView.findViewById(R.id.new_recipe_ingredient_count)
        var units: TextView = itemView.findViewById(R.id.new_recipe_ingredient_units)
        var name: TextView = itemView.findViewById(R.id.new_recipe_ingredient_name)
        var clear: TextView = itemView.findViewById(R.id.clear)
        var countListener: EditTextListener

        init {
            clear.setOnClickListener { v: View? -> count.setText("0") }
            countListener = EditTextListener(count)
            count.addTextChangedListener(countListener)
        }
    }

    inner class EditTextListener(private val countEdit: TextInputEditText) : TextWatcher {
        private var listPosition = 0
        fun updatePosition(position: Int) {
            listPosition = position
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (ingredients != null) {
                val ingredient = ingredients!![listPosition]
                var nCount: Double
                try {
                    nCount = s.toString().toDouble()
                } catch (e: Exception) {
                    countEdit.error = countEdit.context.getString(R.string.input_number)
                    nCount = 0.0
                }
                if (nCount != 0.0) {
                    counts[ingredient.id.toString()] = nCount.toString()
                } else {
                    counts.remove(ingredient.id)
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
    }
}