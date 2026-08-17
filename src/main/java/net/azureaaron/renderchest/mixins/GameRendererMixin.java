package net.azureaaron.renderchest.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.azureaaron.renderchest.impl.GlowDepthTextureManager;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
class GameRendererMixin {
	@Inject(method = "close", at = @At("TAIL"))
	private void renderChest$onClose(CallbackInfo ci) {
		GlowDepthTextureManager.INSTANCE.close();
	}
}
