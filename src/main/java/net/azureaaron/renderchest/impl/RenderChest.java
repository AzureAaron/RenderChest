package net.azureaaron.renderchest.impl;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.Identifier;

public final class RenderChest implements ClientModInitializer {
	public static final String MOD_ID = "render-chest";

	@Override
	public void onInitializeClient() {}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
