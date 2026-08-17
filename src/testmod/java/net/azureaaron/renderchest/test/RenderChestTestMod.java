package net.azureaaron.renderchest.test;

import java.awt.Color;

import net.azureaaron.renderchest.api.CustomGlowCallback;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.animal.chicken.Chicken;

public class RenderChestTestMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CustomGlowCallback.EVENT.register((entity, _) ->
			switch (entity) {
				case Chicken chicken when chicken.isBaby() -> Color.YELLOW.getRGB();
				case ItemDisplay _ -> Color.RED.getRGB();
				default -> EntityRenderState.NO_OUTLINE;
			}
		);
	}
}
