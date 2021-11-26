package project.eatcalc.adapters

import android.content.Context
import project.eatcalc.datastorage.RecipeModel
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import project.eatcalc.R
import android.view.ViewGroup
import android.view.View
import java.util.Locale
import android.widget.ImageView
import android.widget.TextView

class RecipeAdapter(
    context: Context,
    private var recipes: List<RecipeModel>,
    private val listener: OnItemClicked
) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val recipeTypes = intArrayOf(
        R.drawable.ic_recipes, R.drawable.ic_breakfast, R.drawable.ic_soup,
        R.drawable.ic_second, R.drawable.ic_salad, R.drawable.ic_snack,
        R.drawable.ic_cake, R.drawable.ic_water
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeViewHolder {
        val itemView = inflater.inflate(R.layout.recipes_list_item, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.recipeType.setImageResource(recipeTypes[recipe.icon])
        holder.name.text = recipe.name
        holder.price.text = String.format(Locale.US, "%.2f %s", recipe.price, recipe.currencyId)
        holder.calories.text = String.format(Locale.US, "%.1f", recipe.calories)
        holder.proteins.text = String.format(Locale.US, "%.1f", recipe.proteins)
        holder.fats.text = String.format(Locale.US, "%.1f", recipe.fats)
        holder.carbs.text = String.format(Locale.US, "%.1f", recipe.carbohydrates)
        holder.itemView.rootView.setOnClickListener { view: View? ->
            listener.onItemClick(
                view,
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun getItem(position: Int): RecipeModel {
        return recipes[position]
    }

    fun setRecipes(recipes: List<RecipeModel>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recipeType: ImageView = itemView.findViewById(R.id.recipe_item_image)
        var name: TextView = itemView.findViewById(R.id.recipe_item_name)
        var price: TextView = itemView.findViewById(R.id.recipe_item_price)
        var calories: TextView = itemView.findViewById(R.id.recipe_item_calories)
        var proteins: TextView = itemView.findViewById(R.id.recipe_item_proteins)
        var fats: TextView = itemView.findViewById(R.id.recipe_item_fats)
        var carbs: TextView = itemView.findViewById(R.id.recipe_item_carbs)

    }
}