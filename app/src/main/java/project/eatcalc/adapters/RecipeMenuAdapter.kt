package project.eatcalc.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import project.eatcalc.R
import android.widget.ImageView
import android.widget.TextView

class RecipeMenuAdapter(
    context: Context?,
    iconsIds: Array<Int>,
    menuItemNames: Array<String>,
    private val listener: OnItemClicked
) : RecyclerView.Adapter<RecipeMenuAdapter.RecipeViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val iconsIds: List<Int> = listOf(*iconsIds)
    private val menuItemNames: List<String> = listOf(*menuItemNames)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = inflater.inflate(R.layout.recipe_menu_item, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.recipeType.setImageResource(iconsIds[position])
        holder.name.text = menuItemNames[position]
        holder.itemView.rootView.setOnClickListener { view: View? ->
            listener.onItemClick(
                view,
                position
            )
        }
    }

    override fun getItemCount(): Int {
        return iconsIds.size
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recipeType: ImageView = itemView.findViewById(R.id.recipe_menu_image)
        var name: TextView = itemView.findViewById(R.id.recipe_menu_name)
    }
}