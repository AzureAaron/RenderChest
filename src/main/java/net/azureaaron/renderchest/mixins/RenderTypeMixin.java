package net.azureaaron.renderchest.mixins;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.azureaaron.renderchest.impl.RenderChestPipelines;
import net.azureaaron.renderchest.impl.injected.CustomOutlineRenderTypeHolder;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

@Mixin(RenderType.class)
class RenderTypeMixin implements CustomOutlineRenderTypeHolder {
	@Unique
	private static final BiFunction<Identifier, Boolean, RenderType> CUSTOM_OUTLINE = Util.memoize(
			((texture, cullState) -> RenderType.create(
					"Render Chest custom outline",
					RenderSetup.builder(cullState ? RenderChestPipelines.CUSTOM_OUTLINE_CULL : RenderChestPipelines.CUSTOM_OUTLINE_NO_CULL)
					.withTexture("Sampler0", texture)
					.setOutputTarget(OutputTarget.OUTLINE_TARGET)
					.setOutline(RenderSetup.OutlineProperty.IS_OUTLINE)
					.createRenderSetup()
					))
			);
	@Unique
	private @Nullable Optional<RenderType> customOutline;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void renderChest$initCustomOutlineRenderType(CallbackInfo ci, @Local(name = "state") RenderSetup state) {
		RenderSetupAccessor accessor = (RenderSetupAccessor) (Object) state;
		this.customOutline = accessor.getOutlineProperty() == RenderSetup.OutlineProperty.AFFECTS_OUTLINE ? accessor.getTextures()
				.values()
				.stream()
				.findFirst()
				.map(texture -> CUSTOM_OUTLINE.apply(texture.location(), accessor.getPipeline().isCull()))
				: Optional.empty();
	}

	@Override
	public Optional<RenderType> renderChest$getCustomOutlineRenderType() {
		return Objects.requireNonNull(this.customOutline);
	}
}
