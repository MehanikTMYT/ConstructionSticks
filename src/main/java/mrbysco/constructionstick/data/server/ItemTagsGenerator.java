package mrbysco.constructionstick.data.server;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.basics.ModTags;
import mrbysco.constructionstick.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ItemTagsGenerator extends ItemTagsProvider {
	public ItemTagsGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, ConstructionStick.MOD_ID);
	}

	@Override
	protected void addTags(Provider provider) {
		this.tag(ModTags.CONSTRUCTION_STICKS).add(ModItems.STICK_WOODEN.get(), ModItems.STICK_COPPER.get(), ModItems.STICK_IRON.get(),
				ModItems.STICK_DIAMOND.get(), ModItems.STICK_NETHERITE.get());

		// fleet: optional entries — Sophisticated Backpacks is not part of every pack (missing ids break the tag)
		var blacklist = this.tag(ModTags.BLACKLISTED_INVENTORIES);
		blacklist.addOptional(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.BACKPACK.get());
		blacklist.addOptional(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.COPPER_BACKPACK.get());
		blacklist.addOptional(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.IRON_BACKPACK.get());
		blacklist.addOptional(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.GOLD_BACKPACK.get());
		blacklist.addOptional(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.DIAMOND_BACKPACK.get());
		blacklist.addOptional(net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.NETHERITE_BACKPACK.get());
	}
}
