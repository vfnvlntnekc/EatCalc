package project.eatcalc.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import project.eatcalc.R;
import project.eatcalc.adapters.RecipeMenuAdapter;

public class RecipeMenuFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeMenuAdapter menuAdapter;
    private NavController navController;
    private FragmentsViewModel viewModel;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_menu_activity,container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);
        viewModel.setLastFrag(FragmentsViewModel.RECIPE_MENU);
        recyclerView = view.findViewById(R.id.listView);
        navController = NavHostFragment.findNavController(this);
        menuAdapter = new RecipeMenuAdapter(getContext(), new Integer[]{R.drawable.ic_recipes, R.drawable.ic_breakfast, R.drawable.ic_soup,
                R.drawable.ic_second, R.drawable.ic_salad, R.drawable.ic_snack,
                R.drawable.ic_cake, R.drawable.ic_water}, getActivity().getResources().getStringArray(R.array.recipeTypes),
                (view1, position) -> {
                    viewModel.setRecipeType(position);
                    navController.navigate(R.id.navigation_recipes);
                });

        recyclerView.setAdapter(menuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
