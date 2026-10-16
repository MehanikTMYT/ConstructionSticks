package mrbysco.constructionstick.integrations.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.client.KeybindHandler;
import mrbysco.constructionstick.config.ConstructionConfig;
import mrbysco.constructionstick.config.ConstructionConfig.StickProperties;
import mrbysco.constructionstick.integrations.emi.recipe.ApplyUpgradeEMIRecipe;
import mrbysco.constructionstick.items.stick.ItemStick;
import mrbysco.constructionstick.items.template.ItemUpgradeTemplate;
import mrbysco.constructionstick.recipe.SmithingApplyUpgradeRecipe;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Native EMI integration (replaces the former JEI plugin): per-stick upgrade smithing recipes,
 * info pages for sticks and upgrade templates, and search aliases.
 */
@EmiEntrypoint
public class ConstructionStickEMIPlugin implements EmiPlugin {
	private static final String baseKey = ConstructionStick.MOD_ID + ".description.";
	private static final String baseKeyItem = "item." + ConstructionStick.MOD_ID + ".";

	@Override
	public void register(EmiRegistry registry) {
		registerUpgradeRecipes(registry);
		registerInfo(registry);

		Component alias = Component.literal("construction wand");
		for (DeferredItem<ItemStick> deferredStick : ModItems.STICKS) {
			registry.addAlias(EmiStack.of(deferredStick.get()), alias);
		}
	}

	/**
	 * EMI's vanilla plugin shows each upgrade recipe once with the generic result. Like the old JEI smithing
	 * extension, show one recipe per upgradeable stick with the actually assembled (upgraded) stick instead.
	 */
	private static void registerUpgradeRecipes(EmiRegistry registry) {
		if (registry.getRecipeMap() == null) {
			return;
		}
		Set<Identifier> replaced = new HashSet<>();
		for (RecipeHolder<?> holder : registry.getRecipeMap().byType(RecipeType.SMITHING)) {
			if (!(holder.value() instanceof SmithingApplyUpgradeRecipe recipe)) {
				continue;
			}
			Identifier id = holder.id().identifier();
			replaced.add(id);
			ItemStack template = firstItem(recipe.templateIngredient().orElseThrow());
			ItemStack addition = firstItem(recipe.additionIngredient().orElseThrow());
			recipe.baseIngredient().items().forEach(baseHolder -> {
				ItemStack base = new ItemStack(baseHolder);
				SmithingRecipeInput input = new SmithingRecipeInput(template, base, addition);
				if (!recipe.matches(input, null)) {
					return;
				}
				Identifier syntheticId = ConstructionStick.modLoc("/upgrade/" + id.getPath() + "/"
						+ BuiltInRegistries.ITEM.getKey(base.getItem()).getPath());
				registry.addRecipe(new ApplyUpgradeEMIRecipe(recipe, EmiStack.of(base), EmiStack.of(recipe.assemble(input)), syntheticId));
			});
		}
		registry.removeRecipes(r -> r.getCategory() == VanillaEmiRecipeCategories.SMITHING && replaced.contains(r.getId()));
	}

	private static ItemStack firstItem(net.minecraft.world.item.crafting.Ingredient ingredient) {
		return ingredient.items().findFirst().map(ItemStack::new).orElse(ItemStack.EMPTY);
	}

	private static void registerInfo(EmiRegistry registry) {
		MutableComponent stickChangeUpgradeKey = key(KeybindHandler.KEY_CHANGE_UPGRADE.getName());
		MutableComponent stickChangeRestrictionKey = key(KeybindHandler.KEY_CHANGE_RESTRICTION.getName());
		MutableComponent stickOpenGuiKey = key(KeybindHandler.KEY_OPEN_GUI.getName());
		MutableComponent stickUndoKey = key(KeybindHandler.KEY_UNDO.getName());
		MutableComponent stickShowPrevious = key(KeybindHandler.KEY_SHOW_PREVIOUS.getName());

		for (DeferredItem<ItemStick> deferredStick : ModItems.STICKS) {
			Item stick = deferredStick.get();
			StickProperties stickProperties = ConstructionConfig.getStickProperties(stick);
			Component durabilityComponent = Component.translatable(baseKey + "durability.limited", stickProperties.getDurability());
			Identifier itemId = BuiltInRegistries.ITEM.getKey(stick);
			registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(stick)), List.of(
					Component.translatable(baseKey + "stick",
							Component.translatable(baseKeyItem + itemId.getPath()),
							stickProperties.getLimit(), durabilityComponent, stickChangeRestrictionKey, stickOpenGuiKey,
							stickShowPrevious, stickUndoKey, stickOpenGuiKey)),
					ConstructionStick.modLoc("/info/" + itemId.getPath())));
		}

		for (DeferredItem<ItemUpgradeTemplate> templateSupplier : ModItems.TEMPLATES) {
			Item upgradeTemplate = templateSupplier.get();
			Identifier itemId = BuiltInRegistries.ITEM.getKey(upgradeTemplate);
			registry.addRecipe(new EmiInfoRecipe(List.<EmiIngredient>of(EmiStack.of(upgradeTemplate)), List.of(
					Component.translatable(baseKey + itemId.getPath()),
					Component.translatable(baseKey + "upgrade", stickChangeUpgradeKey)),
					ConstructionStick.modLoc("/info/" + itemId.getPath())));
		}
	}

	private static MutableComponent key(String name) {
		return Component.translatable(name).withStyle(ChatFormatting.BLUE);
	}
}
