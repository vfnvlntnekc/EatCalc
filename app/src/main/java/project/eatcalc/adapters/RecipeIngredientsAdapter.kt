package project.eatcalc.adapters

import android.content.Context
import project.eatcalc.datastorage.IngredientModel
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import project.eatcalc.R
import java.util.Locale
import android.widget.TextView
import android.widget.ImageView

class RecipeIngredientsAdapter(context: Context, private var ingredients: List<IngredientModel>) :
    RecyclerView.Adapter<RecipeIngredientsAdapter.IngredientViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView = inflater.inflate(R.layout.recipe_ingredient_item, parent, false)
        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.icon.setImageResource(IngredientAdapter.ingredientTypes[ingredient.icon])
        holder.count.text = ingredient.count.toString()
        holder.units.text = ingredient.unit_id
        holder.name.text = ingredient.name
        holder.price.text = String.format(Locale.US, "%.2f", ingredient.price)
        holder.currency.text = ingredient.currency_id
    }

    override fun getItemCount(): Int = ingredients.size

    fun setIngredients(ingredients: List<IngredientModel>) {
        this.ingredients = ingredients
        notifyDataSetChanged()
    }

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var count: TextView = itemView.findViewById(R.id.ingredient_count)
        var units: TextView = itemView.findViewById(R.id.ingredient_units)
        var name: TextView = itemView.findViewById(R.id.ingredient_name)
        var price: TextView = itemView.findViewById(R.id.ingredient_price)
        var currency: TextView = itemView.findViewById(R.id.ingredient_currency)
        var icon: ImageView = itemView.findViewById(R.id.ingredient_image)
    }
}