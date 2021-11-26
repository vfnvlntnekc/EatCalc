package project.eatcalc.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import project.eatcalc.datastorage.Counts;
import project.eatcalc.datastorage.RecipeModel;

public class FragmentsViewModel extends ViewModel {
    public static final int RESULT_OK = -1;
    public static final int RECIPES = 100;
    public static final int INGREDIENTS = 101;
    public static final int ADD_RECIPE = 102;
    public static final int EDIT_RECIPE = 103;
    public static final int ADD_INGREDIENT = 104;
    public static final int RECIPE = 105;
    public static final int EDIT_INGREDIENTS = 106;
    public static final int RECIPE_MENU = 107;
    public static final int SETTINGS = 108;
    private MutableLiveData<Integer> lastFrag = new MutableLiveData<>();
    private MutableLiveData<String> toUpdateId = new MutableLiveData<>();
    private MutableLiveData<String> idIngredient = new MutableLiveData<>();
    private MutableLiveData<Integer> status = new MutableLiveData<>();
    private MutableLiveData<Counts> counts = new MutableLiveData<>();
    private MutableLiveData<RecipeModel> savedRecipe = new MutableLiveData<>();
    private MutableLiveData<Integer> recipeType = new MutableLiveData<>();

    public MutableLiveData<RecipeModel> getSavedRecipe() {
        return savedRecipe;
    }

    public void setSavedRecipe(RecipeModel savedRecipe) {
        this.savedRecipe.setValue(savedRecipe);
    }

    public LiveData<Integer> getLastFrag() {
        return lastFrag;
    }

    public void setLastFrag(int lastFrag) {
        this.lastFrag.setValue(lastFrag);
    }

    public LiveData<String> getIdIngredient() {
        return idIngredient;
    }

    public void setIdIngredient(String s) {
        this.idIngredient.setValue(s);
    }

    public LiveData<Counts> getCounts() {
        return counts;
    }

    public void setCounts(Counts counts) {
        this.counts.setValue(counts);
    }

    public void setStatus(int status) {
        this.status.setValue(status);
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public void setId(String name) {
        toUpdateId.setValue(name);
    }
    public LiveData<String> getId() {
        return toUpdateId;
    }

    public LiveData<Integer> getRecipeType() {
        return recipeType;
    }

    public void setRecipeType(int recipeType) {
        this.recipeType.setValue(recipeType);
    }
}
