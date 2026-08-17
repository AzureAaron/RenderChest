package net.azureaaron.renderchest.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.renderchest.impl.RenderStateKeys;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;

@Mixin(LevelExtractor.class)
class LevelExtractorMixin {
	@Inject(method = "extractVisibleEntities", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
	private void renderChest$markCustomGlowUsedThisFrame(CallbackInfo ci, @Local(name = "output") LevelRenderState output, @Local(name = "state") EntityRenderState state) {
		if (state.getDataOrDefault(RenderStateKeys.ENTITY_CUSTOM_GLOW_COLOUR, EntityRenderState.NO_OUTLINE) != EntityRenderState.NO_OUTLINE) {
			output.setData(RenderStateKeys.FRAME_USES_CUSTOM_GLOW, true);
		}
	}
}
