package project.eatcalc.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import project.eatcalc.R;
import project.eatcalc.adapters.EditIngredientsAdapter;
import project.eatcalc.datastorage.Counts;
import project.eatcalc.datastorage.DatabaseHelper;
import project.eatcalc.datastorage.IngredientModel;

public class EditIngredientsFragment extends Fragment {
    private String[] ingredientsIDs, ingredientsCountsStringArray;
    private EditIngredientsAdapter adapter;
    private RecyclerView ingredientsRecycler;
    private List<IngredientModel> ingredientsList;
    private DatabaseHelper databaseHelper;
    private NavController navController;
    private FragmentsViewModel viewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_edit_ingredients,container,false);
        navController = NavHostFragment.findNavController(this);
        databaseHelper = new DatabaseHelper(view.getContext());
        ingredientsRecycler = view.findViewById(R.id.edit_ingredients_list);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);
        viewModel.setLastFrag(FragmentsViewModel.EDIT_RECIPE);

        Counts counts = viewModel.getCounts().getValue();
        if (counts!=null) {
            ingredientsCountsStringArray = counts.countsNums.split(",");
            ingredientsIDs = counts.countsIds.split(",");
        }

        adapter = new EditIngredientsAdapter(view.getContext(), ingredientsIDs, ingredientsCountsStringArray);
        ingredientsRecycler.setAdapter(adapter);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
        ingredientsList = databaseHelper.getIngredients();
        adapter.setIngredients(ingredientsList);

        Button button_add_ingredient = view.findViewById(R.id.add_recipe_ingredient);
        button_add_ingredient.setOnClickListener(v -> {
            viewModel.setCounts(new Counts(adapter.getIngredientsIDs(), adapter.getIngredientsCounts()));
            navController.navigate(R.id.navigation_add_ingredient);
        });

        Button button_submit = view.findViewById(R.id.recipe_ingredients_save);
        button_submit.setOnClickListener(v -> {
            viewModel.setCounts(new Counts(adapter.getIngredientsIDs(), adapter.getIngredientsCounts()));
            navController.navigate(R.id.navigation_add_recipe);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Counts counts = viewModel.getCounts().getValue();
        if (counts!=null) {
            ingredientsCountsStringArray = counts.countsNums.split(",");
            ingredientsIDs = counts.countsIds.split(",");
        }
        EditIngredientsAdapter a = new EditIngredientsAdapter(getContext(), ingredientsIDs, ingredientsCountsStringArray);
        if(a!=null) {
            ingredientsList = new ArrayList<>(databaseHelper.getIngredients()); // TODO: to live data
            a.setIngredients(ingredientsList);
        }
    }

}
