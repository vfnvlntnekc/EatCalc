package project.eatcalc.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import project.eatcalc.ImagePickUpUtil;
import project.eatcalc.R;
import project.eatcalc.adapters.RecipeIngredientsAdapter;
import project.eatcalc.datastorage.Counts;
import project.eatcalc.datastorage.DatabaseHelper;
import project.eatcalc.datastorage.IngredientModel;
import project.eatcalc.datastorage.RecipeModel;
import project.eatcalc.validation.EmptyValidator;
import project.eatcalc.validation.PositiveValidator;
import project.eatcalc.validation.Validator;
import project.eatcalc.validation.ValidatorsComposer;

public class AddRecipeFragment extends Fragment {

    public static final int GET_GALLERY_IMAGE = 5;
    private ImageView recipePhoto;
    private Button addRecipePhoto;
    private Spinner recipeType;
    private TextInputEditText name, portionsNum, method;
    private String[] ingredientsCountsIDs, ingredientsCountsString;
    private double recipeMass = 0, recipePrice = 0, recipeCalories = 0, recipeProteins = 0, recipeFats = 0, recipeCarbs = 0;
    private boolean isError = false;
    private final List<IngredientModel> ingredients = new LinkedList<>();
    private RecyclerView ingredientsView;
    private RecipeIngredientsAdapter adapter;
    private DatabaseHelper databaseHelper;
    private String recipePhotoPath;
    private NavController navController;
    private FragmentsViewModel viewModel;
    private Thread calculating = null;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_recipe,container,false);
        databaseHelper = new DatabaseHelper(getContext());
        ImagePickUpUtil.verifyStoragePermissions(getActivity());

        navController = NavHostFragment.findNavController(this);

        viewModel = new ViewModelProvider(requireActivity()).get(FragmentsViewModel.class);
        int last = viewModel.getLastFrag().getValue();
        if(last==FragmentsViewModel.INGREDIENTS || last==FragmentsViewModel.ADD_INGREDIENT) { // TODO: добавить переход из профиля и из конвертера
            viewModel.setId(null);
            viewModel.setSavedRecipe(null);
        }
        viewModel.setLastFrag(FragmentsViewModel.ADD_RECIPE);

        Counts countsObj = viewModel.getCounts().getValue();
        if (countsObj!=null) {
            ingredientsCountsString = countsObj.countsNums.split(",");
            ingredientsCountsIDs = countsObj.countsIds.split(",");
        }

        name = view.findViewById(R.id.new_recipe_name);
        portionsNum = view.findViewById(R.id.new_recipe_portions_num);
        method = view.findViewById(R.id.new_recipe_method);
        recipePhoto = view.findViewById(R.id.recipe_photo);
        recipeType = view.findViewById(R.id.recipe_icon);

        RecipeModel savedRecipe = viewModel.getSavedRecipe().getValue();
        if((last==FragmentsViewModel.EDIT_RECIPE || last==FragmentsViewModel.ADD_INGREDIENT)&& savedRecipe!=null){
            name.setText(savedRecipe.name);
            portionsNum.setText(String.valueOf(savedRecipe.portions));
            method.setText(savedRecipe.method);
            recipePhotoPath = savedRecipe.photo;
            recipeType.setSelection(savedRecipe.icon);
        }

        ingredientsView = view.findViewById(R.id.add_recipe_ingredients);

        addRecipePhoto = view.findViewById(R.id.add_recipe_photo);
        addRecipePhoto.setOnClickListener((v)->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, getString(R.string.select_photo)), GET_GALLERY_IMAGE);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton button_add_ingredient = view.findViewById(R.id.add_recipe_ingredient);
        button_add_ingredient.setOnClickListener(v -> {
            if(ingredientsCountsIDs!=null) {
                viewModel.setCounts(new Counts(String.join(",", ingredientsCountsIDs), String.join(",", ingredientsCountsString)));
            }
            else {
                viewModel.setCounts(null);
            }
            RecipeModel savedRecipe = new RecipeModel();
            savedRecipe.name = name.getText().toString();
            int port;
            try {
               port = Integer.parseInt(portionsNum.getText().toString());
            } catch (Exception e)
            {
                port =0;
            }
            savedRecipe.portions = port;
            savedRecipe.method = method.getText().toString();
            savedRecipe.photo=recipePhotoPath;
            savedRecipe.icon = recipeType.getSelectedItemPosition();
            viewModel.setSavedRecipe(savedRecipe);
            navController.navigate(R.id.navigation_edit_ingredients);
        });


        Button button_submit = view.findViewById(R.id.new_recipe_save);
        button_submit.setOnClickListener(v -> {
            isError = false;

            int portionsCount;
            try {
                portionsCount = Integer.parseInt(portionsNum.getText().toString());
            } catch (Exception e) {
                portionsCount = 0;
                portionsNum.setError(getString(R.string.input_number));
                isError = true;
            }

            Validator<Double> positiveValidator = new PositiveValidator(getContext());
            if(!positiveValidator.isValid((double) portionsCount)) {
                portionsNum.setError(positiveValidator.getMessage());
                isError = true;
            }
            String recipeName = name.getText().toString();
            String cookingMethod = method.getText().toString();

            ValidatorsComposer<String> validation = new ValidatorsComposer<>(new EmptyValidator(getContext()));
            if(!validation.isValid(recipeName)) {
                name.setError(validation.getMessage());
                isError = true;
            }
            if(!validation.isValid(cookingMethod)) { // TODO: убрать проверку метода приготовки
                method.setError(validation.getMessage());
                isError = true;
            }
            if(ingredientsCountsIDs==null || ingredientsCountsIDs[0].trim().isEmpty() && ingredientsCountsIDs.length<2) {
                Toast.makeText(
                        view.getContext().getApplicationContext(),
                        view.getContext().getString(R.string.recipe_has_no_ingredients),
                        Toast.LENGTH_LONG
                ).show();
                isError= true;
            }
            if(!isError){
                String countIDs;
                String counts;
                if(ingredientsCountsIDs!=null) {
                    countIDs = String.join(",", ingredientsCountsIDs);
                    counts = String.join(",", ingredientsCountsString);
                }
                else {
                    countIDs = "";
                    counts = "";
                }
                if(calculating!=null) {
                    try {
                        calculating.join(); // дождаться выполнения вычислений
                    } catch (InterruptedException ignored) {
                        Log.v("EatCalc", "Calculating recipe was interrupted");
                        recipePrice=0;recipeCarbs=0;recipeProteins=0;recipeFats=0;recipeCalories=0;
                    }
                }
                recipeCalories/=portionsCount;
                recipeFats/=portionsCount;
                recipeProteins/=portionsCount;
                recipeCarbs/=portionsCount;
                int type = recipeType.getSelectedItemPosition();
                if(viewModel.getId().getValue()!=null){
                    String id = viewModel.getId().getValue();
                    databaseHelper.updateRecipe(id,recipeName,cookingMethod,portionsCount,
                            recipePrice,recipeMass,countIDs,counts,recipePhotoPath,
                            getString(R.string.rub),recipeCalories,recipeProteins,
                            recipeFats,recipeCarbs, type);
                }else {
                    databaseHelper.addRecipe(recipeName,cookingMethod,portionsCount,
                            recipePrice,recipeMass,countIDs,counts,recipePhotoPath,
                            getString(R.string.rub),recipeCalories,recipeProteins,
                            recipeFats,recipeCarbs, type);
                }
                viewModel.setId(null);
                viewModel.setCounts(null);
                viewModel.setSavedRecipe(null);
                navController.navigate(R.id.navigation_recipes);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("name", name.getText().toString());
        savedInstanceState.putString("portions", portionsNum.getText().toString());
        savedInstanceState.putString("photo", recipePhotoPath);
        savedInstanceState.putString("method", method.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            name.setText(savedInstanceState.getString("name"));
            portionsNum.setText(savedInstanceState.getString("portions"));
            method.setText(savedInstanceState.getString("method"));
            recipePhotoPath = savedInstanceState.getString("name");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        String recipeId = viewModel.getId().getValue();
        if(recipePhotoPath!=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(ImagePickUpUtil.getRealPathFromURI(getView().getContext(), Uri.parse(recipePhotoPath)), options);
            recipePhoto.setImageBitmap(bitmap);
        }
        calculating = new Thread(()-> {
            Counts modelCounts = viewModel.getCounts().getValue();
            if(recipeId!=null) { // update existing recipe
                RecipeModel recipe = databaseHelper.getRecipeById(recipeId);

                Bitmap bitmap = null;
                if (recipe.photo != null && !recipe.photo.isEmpty()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(ImagePickUpUtil.getRealPathFromURI(getView().getContext(), Uri.parse(recipe.photo)), options);
                }

                Bitmap finalBitmap = bitmap;
                getActivity().runOnUiThread(() -> {
                    name.setText(recipe.name);
                    portionsNum.setText(String.valueOf(recipe.portions));
                    method.setText(recipe.method);
                    recipeType.setSelection(recipe.icon);
                    if(finalBitmap !=null)
                        recipePhoto.setImageBitmap(finalBitmap);
                });

                ingredientsCountsString = recipe.ingredients_counts.split(",");
                ingredientsCountsIDs = recipe.ingredients.split(",");
            }
            ingredients.clear();
            if (modelCounts != null) {
                ingredientsCountsString = modelCounts.countsNums.split(",");
                ingredientsCountsIDs = modelCounts.countsIds.split(",");
            }
            recipePrice=0;recipeCarbs=0;recipeProteins=0;recipeFats=0;recipeCalories=0;
            if(ingredientsCountsIDs!=null){
                for(int i =0;i<ingredientsCountsIDs.length;++i) {
                    if(!ingredientsCountsIDs[i].trim().isEmpty()) {
                        IngredientModel ingredient = databaseHelper.getIngredientById(ingredientsCountsIDs[i]);
                        double count = Double.parseDouble(ingredientsCountsString[i]);
                        if (count != 0 && ingredient != null) {
                            ingredient.price *= count;
                            ingredient.count *= count;
                            recipePrice += ingredient.price;
                            recipeMass += ingredient.count*count;
                            recipeCalories += ingredient.calories*count;
                            recipeFats += ingredient.fats*count;
                            recipeProteins += ingredient.proteins*count;
                            recipeCarbs += ingredient.carbohydrates*count;
                            ingredients.add(ingredient); // TODO: воспользоваться конвертером единиц, чтобы найти массу рецепта
                        }
                        getActivity().runOnUiThread(() -> adapter.setIngredients(ingredients));
                    }
                }
            }
            adapter = new RecipeIngredientsAdapter(getContext(), ingredients);
            getActivity().runOnUiThread(() -> {
                ingredientsView.setLayoutManager(new LinearLayoutManager(getContext()) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                ingredientsView.setAdapter(adapter);
            });
        });
        calculating.start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState!=null) {
            String n = savedInstanceState.getString("name");
            if(n!=null)
                name.setText(n);
            String p = savedInstanceState.getString("portions");
            if(p!=null)
                portionsNum.setText(p);
            String m = savedInstanceState.getString("method");
            if(m!=null)
                method.setText(m);
            recipePhotoPath = savedInstanceState.getString("name");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode==GET_GALLERY_IMAGE)&&(data!=null)){
            ImagePickUpUtil.verifyStoragePermissions(getActivity());
            Uri selectedImage = data.getData();
            File file = new File(ImagePickUpUtil.getRealPathFromURI(getView().getContext(),selectedImage));

            File direct = new File(Environment.getExternalStorageDirectory().getPath() + "/EatCalc/pictures");

            if (!direct.exists()) {
                direct.mkdirs();
            }

            File fi = new File(direct, file.getName());
            if (fi.exists()) {
                fi.delete();
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(ImagePickUpUtil.getRealPathFromURI(getView().getContext(),selectedImage), options);
            //recipePhoto.setImageBitmap(bitmap);
            try {
                FileOutputStream out = new FileOutputStream(fi);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            recipePhotoPath = fi.getAbsolutePath();
        }
        else {
            if(ingredientsCountsIDs==null) {
                Toast.makeText(
                        getView().getContext().getApplicationContext(),
                        getView().getContext().getString(R.string.recipe_has_no_ingredients),
                        Toast.LENGTH_LONG
                ).show();
                isError= true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // TODO: предупреждение о несохраненном рецепте
        databaseHelper.close();
        viewModel.setSavedRecipe(null);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
