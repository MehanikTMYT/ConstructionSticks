package mrbysco.constructionstick.client;

import mrbysco.constructionstick.ConstructionStick;
import mrbysco.constructionstick.client.property.SelectStickUpgrade;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;

@EventBusSubscriber(modid = "constructionstick", value = Dist.CLIENT) // Fleet: explicit modid (multi-mod jar in AE Compendium)
public class ClientHandler {

	@SubscribeEvent
	public static void registerKeymapping(final RegisterKeyMappingsEvent event) {
		event.registerCategory(KeybindHandler.CATEGORY);
		event.register(KeybindHandler.KEY_CHANGE_RESTRICTION);
		event.register(KeybindHandler.KEY_CHANGE_UPGRADE);
		event.register(KeybindHandler.KEY_CHANGE_DIRECTION);
		event.register(KeybindHandler.KEY_OPEN_GUI);
		event.register(KeybindHandler.KEY_UNDO);
		event.register(KeybindHandler.KEY_SHOW_PREVIOUS);
	}

	@SubscribeEvent
	public static void registerModelProperties(RegisterSelectItemModelPropertyEvent event) {
		event.register(ConstructionStick.modLoc("stick_upgrade"), SelectStickUpgrade.TYPE);
	}
}
