package net.azureaaron.renderchest.test;

import java.awt.Color;

import net.azureaaron.renderchest.api.CustomGlowCallback;
import net.azureaaron.renderchest.api.GlowConstants;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.world.entity.Display.ItemDisplay;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.item.DyeColor;

public class RenderChestTestMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		CustomGlowCallback.EVENT.register((entity, _) ->
			switch (entity) {
				// Test model glow
				case Chicken chicken when chicken.isBaby() -> Color.YELLOW.getRGB();

				// Test item glow
				case ItemDisplay _ -> Color.RED.getRGB();

				// Test changing vanilla glow
				case Pig pig when pig.isBaby() -> DyeColor.ORANGE.getTextColor();

				// Test removing vanilla glow
				case Cow cow when cow.isBaby() -> GlowConstants.REMOVE_GLOW;

				default -> GlowConstants.NO_GLOW;
			}
		);
	}
}
