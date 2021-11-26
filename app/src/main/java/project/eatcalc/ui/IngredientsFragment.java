package project.eatcalc.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import project.eatcalc.R;
import project.eatcalc.adapters.IngredientAdapter;
import project.eatcalc.datastorage.DatabaseHelper;
import project.eatcalc.datastorage.IngredientModel;
import project.eatcalc.swipe.SwipeController;
import project.eatcalc.swipe.SwipeControllerActions;

public class IngredientsFragment extends Fragment {
    private ArrayList<IngredientModel> ingredients;
    private RecyclerView recyclerView;
    private FloatingActionButton addIngredientButton;
    private DatabaseHelper databaseHelper;
    private NavController navController;
    private FragmentsViewModel viewModel;
    private IngredientAdapter adapter;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_ingredients_list, container, false);
        navController = NavHostFragment.findNavController(this);
        databaseHelper = new DatabaseHelper(view.getContext());
        addIngredientButton = view.findViewById(R.id.add_photo);
        ingredients = new ArrayList<>(databaseHelper.getIngredients());
        recyclerView = view.findViewById(R.id.ingredients_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new IngredientAdapter(view.getContext().getApplicationContext(), ingredients, (view1, position) -> {
            IngredientModel ingredient = adapter.getItem(position);
            viewModel.setId(ingredient.id);
            navController.navigate(R.id.navigation_add_ingredient);
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);
        viewModel.setLastFrag(FragmentsViewModel.INGREDIENTS);
        addIngredientButton.setOnClickListener(v -> {
            viewModel.setIdIngredient(null);
            navController.navigate(R.id.navigation_add_ingredient);
        });

        SwipeController swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onLeftClicked(int position) {
                IngredientModel toUpdate = adapter.getItem(position);
                viewModel.setId(toUpdate.id);
                navController.navigate(R.id.navigation_add_ingredient);
            }

            @Override
            public void onRightClicked(int position) {
                IngredientModel toDelete = adapter.getItem(position);

                new AlertDialog.Builder(getContext())
                    .setTitle(R.string.delete_ingredient_dialog_title)
                    .setMessage(R.string.delete_dialog_message)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .setNegativeButton(R.string.delete, (dialog, which) -> {
                        databaseHelper.deleteIngredient(toDelete.id);
                        ingredients.remove(position);
                        adapter.setIngredients(ingredients); // TODO: пересчёт рецептов
                    })
                .create().show();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        ingredients = new ArrayList<>(databaseHelper.getIngredients());
        IngredientAdapter a = (IngredientAdapter) recyclerView.getAdapter();
        if(a!=null) {
            a.setIngredients(ingredients);
        }
    }
}
