package mrbysco.constructionstick.integrations.emi.recipe;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Smithing upgrade for one concrete stick: template + stick + addition -> upgraded stick.
 */
public class ApplyUpgradeEMIRecipe implements EmiRecipe {
	private final List<EmiIngredient> inputs;
	private final EmiStack output;
	private final Identifier id;

	public ApplyUpgradeEMIRecipe(SmithingApplyUpgradeRecipe recipe, EmiStack base, EmiStack output, Identifier id) {
		this.inputs = List.of(EmiIngredient.of(recipe.templateIngredient().orElseThrow()), base,
				EmiIngredient.of(recipe.additionIngredient().orElseThrow()));
		this.output = output;
		this.id = id;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaEmiRecipeCategories.SMITHING;
	}

	@Override
	public @Nullable Identifier getId() {
		return this.id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return inputs;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return List.of(output);
	}

	@Override
	public int getDisplayWidth() {
		return 112;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 62, 1);
		widgets.addSlot(inputs.get(0), 0, 0);
		widgets.addSlot(inputs.get(1), 18, 0);
		widgets.addSlot(inputs.get(2), 36, 0);
		widgets.addSlot(output, 94, 0).recipeContext(this);
	}
}
