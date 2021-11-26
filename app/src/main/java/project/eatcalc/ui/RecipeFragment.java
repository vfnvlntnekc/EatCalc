package project.eatcalc.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import project.eatcalc.ImagePickUpUtil;
import project.eatcalc.R;
import project.eatcalc.adapters.RecipeIngredientsAdapter;
import project.eatcalc.datastorage.DatabaseHelper;
import project.eatcalc.datastorage.IngredientModel;
import project.eatcalc.datastorage.RecipeModel;

public class RecipeFragment extends Fragment {

    private TextView name, portionsNum, price, currency, cookingMethod, caloriesText, proteinsText, fatsText, carbsText;
    private ImageView recipePhoto;
    private DatabaseHelper databaseHelper;
    private List<IngredientModel> ingredients;
    private RecyclerView ingredientsView;
    private ConstraintLayout recipeLayout;
    private ProgressBar progressBar;
    private NavController navController;
    private FragmentsViewModel viewModel;
    private RecipeIngredientsAdapter adapter;
    private String recipePhotoPath;
    private boolean isExit = false;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_recipe, container, false);
        databaseHelper = new DatabaseHelper(getContext());
        navController = NavHostFragment.findNavController(this);
        name = view.findViewById(R.id.recipe_name);
        portionsNum = view.findViewById(R.id.portions_num);
        price = view.findViewById(R.id.recipe_price);
        currency = view.findViewById(R.id.recipe_currency);
        cookingMethod = view.findViewById(R.id.cooking_method);
        recipePhoto = view.findViewById(R.id.recipe_photo);
        caloriesText = view.findViewById(R.id.calories);
        proteinsText = view.findViewById(R.id.proteins);
        fatsText = view.findViewById(R.id.fats);
        carbsText = view.findViewById(R.id.carbs);
        ingredients = new ArrayList<>();
        ingredientsView = view.findViewById(R.id.recipe_ingredients);
        recipeLayout = view.findViewById(R.id.recipe_layout);
        progressBar = view.findViewById(R.id.recipe_progressbar);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);
        viewModel.setLastFrag(FragmentsViewModel.RECIPE);


        Button edit_button = view.findViewById(R.id.edit);
        edit_button.setOnClickListener(v -> {
            isExit=true;
            navController.navigate(R.id.navigation_add_recipe);
        });
        Button delete_button = view.findViewById(R.id.delete);
        delete_button.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.delete_recipe_dialog_title);
            builder.setMessage(R.string.delete_dialog_message);
            builder.setCancelable(true);
            builder.setPositiveButton(android.R.string.cancel, (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setNegativeButton(R.string.delete, (dialog, which) -> {
                databaseHelper.deleteRecipe(viewModel.getId().getValue());
                viewModel.setId(null);
                isExit=true;
                navController.navigate(R.id.navigation_recipes);
            });
            builder.create().show();
        });
        viewModel.getStatus().observe(getViewLifecycleOwner(), s ->{
            if(s != FragmentsViewModel.RESULT_OK) {
                Toast.makeText(
                        getContext().getApplicationContext(),
                        getString(R.string.recipe_not_saved),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isExit = false;

        Thread calculating = new Thread(() -> {
            String recipeId = viewModel.getId().getValue();
            if (recipeId==null) {
                return;
            }
            RecipeModel recipe = databaseHelper.getRecipeById(recipeId);
            String[] ingredientsCountsIDs = recipe.ingredients.split(",");
            String[] ingredientCountsString = recipe.ingredients_counts.split(",");
            ingredients.clear();
            for (int i = 0; i < ingredientsCountsIDs.length; ++i) {
                if (!ingredientsCountsIDs[i].trim().isEmpty()) {
                    IngredientModel ingredient = databaseHelper.getIngredientById(ingredientsCountsIDs[i]);
                    double count = Double.parseDouble(ingredientCountsString[i]);
                    if (count != 0 && ingredient != null) {
                        ingredient.count = count;
                        ingredient.price *= count;
                        ingredient.calories *= count;
                        ingredient.proteins *= count;
                        ingredient.fats *= count;
                        ingredient.carbohydrates *= count;
                        ingredients.add(ingredient);
                    }
                }
            }


            recipePhotoPath = recipe.photo;
            Bitmap bitmap = null;
            if (recipePhotoPath != null && !recipePhotoPath.isEmpty()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(ImagePickUpUtil.getRealPathFromURI(getContext(), Uri.parse(recipePhotoPath)), options);
            }
            Bitmap finalBitmap = bitmap;

            adapter = new RecipeIngredientsAdapter(getContext(), ingredients);
            LinearLayoutManager lm = new LinearLayoutManager(getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            String[] lines = recipe.method.split("\n");
            StringBuilder methodBuilder = new StringBuilder();
            for (String line : lines) {
                if (!TextUtils.isEmpty(line.trim())) {
                    methodBuilder.append("\n\u2022 ").append(line.trim());
                }
            }
            String markedMethod = methodBuilder.toString();

            getActivity().runOnUiThread(() -> {
                name.setText(recipe.name);
                portionsNum.setText(String.valueOf(recipe.portions));
                price.setText(String.format(Locale.US, "%.2f", recipe.price / recipe.portions)); // цена за 1 порцию
                currency.setText(recipe.currencyId); // TODO: добавить поддержку пересчёта валюты
                cookingMethod.setText(markedMethod);
                caloriesText.setText(String.format(Locale.US, "%.1f", recipe.calories / recipe.portions));
                proteinsText.setText(String.format(Locale.US, "%.1f", recipe.proteins / recipe.portions));
                fatsText.setText(String.format(Locale.US, "%.1f", recipe.fats / recipe.portions));
                carbsText.setText(String.format(Locale.US, "%.1f", recipe.carbohydrates / recipe.portions)); // КБЖУ за 1 порцию

                ingredientsView.setAdapter(adapter);
                ingredientsView.setLayoutManager(lm);
                recipePhoto.setImageBitmap(finalBitmap);

                progressBar.setVisibility(View.GONE);
                recipeLayout.setVisibility(View.VISIBLE);
            });
        });
        calculating.start();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(!isExit)
            viewModel.setId(null);
    }
}
