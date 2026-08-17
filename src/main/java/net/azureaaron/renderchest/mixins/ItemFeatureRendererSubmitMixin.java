package net.azureaaron.renderchest.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.azureaaron.renderchest.impl.injected.CustomOutlineMarker;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;

@Mixin(ItemFeatureRenderer.Submit.class)
class ItemFeatureRendererSubmitMixin implements CustomOutlineMarker {
	@Unique
	private boolean hasCustomGlow = false;

	@Override
	public void renderChest$setHasCustomOutline() {
		this.hasCustomGlow = true;
	}

	@Override
	public boolean renderChest$hasCustomOutline() {
		return this.hasCustomGlow;
	}
}
