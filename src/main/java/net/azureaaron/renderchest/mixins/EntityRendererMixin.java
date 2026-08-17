package net.azureaaron.renderchest.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.renderchest.api.CustomGlowCallback;
import net.azureaaron.renderchest.impl.RenderStateKeys;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;

@Mixin(EntityRenderer.class)
class EntityRendererMixin {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void renderChest$customGlow(CallbackInfo ci, @Local(name = "entity") Entity entity, @Local(name = "state") EntityRenderState state) {
		int customGlowColour = CustomGlowCallback.EVENT.invoker().applyGlowColour(entity, state);

		if (customGlowColour != EntityRenderState.NO_OUTLINE) {
			// If the entity has the vanilla glow then change it's colour otherwise apply the custom glow
			if (entity.isCurrentlyGlowing()) {
				state.outlineColor = ARGB.opaque(customGlowColour);
			} else {
				state.setData(RenderStateKeys.ENTITY_CUSTOM_GLOW_COLOUR, ARGB.opaque(customGlowColour));
			}
		}
	}
}
