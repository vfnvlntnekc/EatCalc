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

class IngredientAdapter(
    context: Context?,
    private var ingredients: List<IngredientModel>,
    private val listener: OnItemClicked
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        @JvmField
        val ingredientTypes = intArrayOf(
            R.drawable.ic_ingredient, R.drawable.ic_vegetables, R.drawable.ic_meat,
            R.drawable.ic_drinks, R.drawable.ic_salt
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView = inflater.inflate(R.layout.ingredients_list_item, parent, false)
        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredientModel = ingredients[position]
        holder.icon.setImageResource(ingredientTypes[ingredientModel.icon])
        holder.unitView.text = ingredientModel.unit
        holder.nameView.text = ingredientModel.name
        holder.priceView.text = String.format(Locale.US, "%.2f", ingredientModel.price)
        holder.currencyView.text = ingredientModel.currency
        holder.caloriesView.text = String.format(Locale.US, "%.2f", ingredientModel.calories)
        holder.proteinsView.text = String.format(Locale.US, "%.2f", ingredientModel.proteins)
        holder.fatsView.text = String.format(Locale.US, "%.2f", ingredientModel.fats)
        holder.carbsView.text = String.format(Locale.US, "%.2f", ingredientModel.carbohydrates)
        holder.itemView.rootView.setOnClickListener { view: View? ->
            listener.onItemClick(
                view,
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun getItem(position: Int): IngredientModel {
        return ingredients[position]
    }

    fun setIngredients(ingredients: List<IngredientModel>) {
        this.ingredients = ingredients
        notifyDataSetChanged()
    }

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val unitView: TextView = itemView.findViewById(R.id.item_unit)
        val nameView: TextView = itemView.findViewById(R.id.item_name)
        val priceView: TextView = itemView.findViewById(R.id.item_price)
        val currencyView: TextView = itemView.findViewById(R.id.item_currency)
        val caloriesView: TextView = itemView.findViewById(R.id.calories)
        val proteinsView: TextView = itemView.findViewById(R.id.proteins)
        val fatsView: TextView = itemView.findViewById(R.id.fats)
        val carbsView: TextView = itemView.findViewById(R.id.carbs)
        var icon: ImageView = itemView.findViewById(R.id.ingredient_photo)
    }
}