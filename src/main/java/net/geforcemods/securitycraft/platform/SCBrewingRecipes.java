package net.geforcemods.securitycraft.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;

/**
 * Replacement for NeoForge's arbitrary brewing recipes (input item + ingredient item = output item). Vanilla's brewing logic
 * is hooked by PotionBrewingMixin and the brewing stand mixins.
 */
public final class SCBrewingRecipes {
	private static final List<Recipe> RECIPES = new ArrayList<>();

	private SCBrewingRecipes() {}

	public static void addRecipe(Predicate<ItemStack> input, Predicate<ItemStack> ingredient, Supplier<ItemStack> output) {
		RECIPES.add(new Recipe(input, ingredient, output));
	}

	public static boolean isIngredient(ItemStack stack) {
		for (Recipe recipe : RECIPES) {
			if (recipe.ingredient.test(stack))
				return true;
		}

		return false;
	}

	public static boolean isInput(ItemStack stack) {
		for (Recipe recipe : RECIPES) {
			if (recipe.input.test(stack))
				return true;
		}

		return false;
	}

	public static ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		if (input.isEmpty() || ingredient.isEmpty() || input.getCount() != 1)
			return ItemStack.EMPTY;

		for (Recipe recipe : RECIPES) {
			if (recipe.input.test(input) && recipe.ingredient.test(ingredient))
				return recipe.output.get();
		}

		return ItemStack.EMPTY;
	}

	private record Recipe(Predicate<ItemStack> input, Predicate<ItemStack> ingredient, Supplier<ItemStack> output) {}
}
