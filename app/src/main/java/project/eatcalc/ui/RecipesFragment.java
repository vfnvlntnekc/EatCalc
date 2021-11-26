package project.eatcalc.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import project.eatcalc.R;
import project.eatcalc.adapters.RecipeAdapter;
import project.eatcalc.datastorage.DatabaseHelper;
import project.eatcalc.datastorage.IngredientModel;
import project.eatcalc.datastorage.RecipeModel;
import project.eatcalc.swipe.SwipeController;
import project.eatcalc.swipe.SwipeControllerActions;

public class RecipesFragment extends Fragment {
    private ArrayList<RecipeModel> recipes;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton addNewRecipeButton, refreshButton;
    private DatabaseHelper databaseHelper;
    private NavController navController;
    private FragmentsViewModel viewModel;
    private boolean isRefreshed = false;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipes_list,container, false);
        databaseHelper     = new DatabaseHelper(view.getContext());
        addNewRecipeButton = view.findViewById(R.id.add_new_recipe);
        refreshButton      = view.findViewById(R.id.refresh);
        swipeRefreshLayout = view.findViewById(R.id.recipes_swipe_refresh);
        navController      = NavHostFragment.findNavController(this);
        viewModel          = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(()->{
            if(!isRefreshed) {
                isRefreshed = true;
                doRefresh();
            }
        });

        refreshButton.setOnClickListener(v->{
            if(!isRefreshed) {
                isRefreshed = true;
                doRefresh();
            }
        });

        addNewRecipeButton.setOnClickListener(v -> {
            viewModel.setId(null);
            viewModel.setCounts(null);
            navController.navigate(R.id.navigation_add_recipe);
        });

        return view;
    }

    private void doRefresh() {
        new Thread(()-> {
            getActivity().runOnUiThread(()-> swipeRefreshLayout.setRefreshing(true));
            int recipesType = viewModel.getRecipeType().getValue();
            recipes = recipesType==0?databaseHelper.getRecipes():databaseHelper.getRecipesFromType(recipesType);
            String[] ingredientsCountsString, ingredientsCountsIDs;
            RecipeModel curRecipe;
            for (RecipeModel recipe : recipes) {
                curRecipe = recipe;
                curRecipe.price = 0;
                curRecipe.carbohydrates = 0;
                curRecipe.calories = 0;
                curRecipe.fats = 0;
                curRecipe.proteins = 0;
                ingredientsCountsString = curRecipe.ingredients_counts.split(",");
                ingredientsCountsIDs = curRecipe.ingredients.split(",");
                for (int i = 0; i < ingredientsCountsIDs.length; ++i) {
                    if (!ingredientsCountsIDs[i].trim().isEmpty()) {
                        IngredientModel ingredient = databaseHelper.getIngredientById(ingredientsCountsIDs[i]);
                        double count = Double.parseDouble(ingredientsCountsString[i]);
                        if (count != 0 && ingredient != null) {
                            curRecipe.price += ingredient.price*count;
                            curRecipe.carbohydrates += ingredient.carbohydrates*count;
                            curRecipe.calories += ingredient.calories*count;
                            curRecipe.fats += ingredient.fats*count;
                            curRecipe.proteins += ingredient.proteins*count;
                        }
                    }
                }
                curRecipe.carbohydrates/=curRecipe.portions;
                curRecipe.calories/=curRecipe.portions;
                curRecipe.fats/=curRecipe.portions;
                curRecipe.proteins/=curRecipe.portions;
                databaseHelper.updateRecipe(curRecipe);
            }
            FragmentActivity activity = getActivity();
            if(activity!=null && recipeAdapter!=null)
                activity.runOnUiThread(()->{
                    recipeAdapter.notifyDataSetChanged();
                });
            getActivity().runOnUiThread(()-> swipeRefreshLayout.setRefreshing(false));
            isRefreshed = false;
        }).start();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.setLastFrag(FragmentsViewModel.RECIPES);
        displayRecipes(view);

        SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                RecipeModel toUpdate = recipeAdapter.getItem(position);
                viewModel.setId(toUpdate.id);
                navController.navigate(R.id.navigation_add_recipe);
            }

            @Override
            public void onRightClicked(int position) {
                RecipeModel toDelete = recipeAdapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.delete_recipe_dialog_title);
                builder.setMessage(R.string.delete_dialog_message);
                builder.setCancelable(true);
                builder.setPositiveButton(android.R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.setNegativeButton(R.string.delete, (dialog, which) -> {
                    databaseHelper.deleteRecipe(toDelete.id);
                    displayRecipes(view);
                });
                builder.create().show();
            }
        });
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeController);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        viewModel.getStatus().observe(getViewLifecycleOwner(), s->{
            if(s != FragmentsViewModel.RESULT_OK) {
                Toast.makeText(getContext().getApplicationContext(), getString(R.string.recipe_not_saved), Toast.LENGTH_LONG).show();
            }
        });

    }

    public void displayRecipes(View view) {
        int recipesType = viewModel.getRecipeType().getValue();
        recipes = recipesType==0?databaseHelper.getRecipes():databaseHelper.getRecipesFromType(recipesType);
        recyclerView = view.findViewById(R.id.recipes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recipeAdapter = new RecipeAdapter(view.getContext(), recipes, (view1, position) -> {
            RecipeModel recipe = recipeAdapter.getItem(position);
            viewModel.setId(recipe.id);
            navController.navigate(R.id.navigation_recipe);
        });
        recyclerView.setAdapter(recipeAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        int recipesType = viewModel.getRecipeType().getValue();
        recipes = recipesType==0?databaseHelper.getRecipes():databaseHelper.getRecipesFromType(recipesType);
        RecipeAdapter a = (RecipeAdapter) recyclerView.getAdapter();
        if(a!=null) {
            a.setRecipes(recipes);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(databaseHelper!=null)databaseHelper.close();
    }
}
