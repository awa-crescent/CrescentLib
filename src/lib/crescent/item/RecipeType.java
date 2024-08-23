package lib.crescent.item;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;

public enum RecipeType {
	CRAFTING, FURNACE;

	public static HashMap<Material, FurnaceRecipe> furnace_recipes = null;

	static {
		loadRecipes();
	}

	public static void loadRecipes() {
		furnace_recipes = new HashMap<>();
		Iterator<Recipe> recipes = Bukkit.recipeIterator();
		while (recipes.hasNext()) {
			Recipe recipe = recipes.next();
			if (recipe instanceof FurnaceRecipe furnace_recipe) {
				furnace_recipes.put(furnace_recipe.getInput().getType(), furnace_recipe);
			}
		}
	}
}
