package net.azureaaron.renderchest.mixins;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.renderpearl.api.commands.RenderPass;

import net.azureaaron.renderchest.impl.injected.CustomOutlinePhaseExecutor;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.feature.FeatureFrameContext;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;

@Mixin(FeatureRenderDispatcher.PreparedFrame.class)
abstract class FeatureRenderDispatcherPreparedFrameMixin implements CustomOutlinePhaseExecutor {
	@Shadow
	private @Nullable FeatureFrameContext context;
	@Shadow
	private @Nullable SubmitNodeStorage submitNodeStorage;
	@Shadow
	private Map<FeatureRenderPhase<?>, List<?>> groupsByPhase;

	@Shadow
	public abstract void executePhase(FeatureRenderPhase<?> phase, FeatureFrameContext context, RenderPass renderPass);

	@Override
	public void renderChest$executeCustomOutline(RenderPass renderPass) {
		FeatureFrameContext context = Objects.requireNonNull(this.context);
		SubmitNodeStorage submitNodeStorage = Objects.requireNonNull(this.submitNodeStorage);

		for (SubmitNodeCollection collection : submitNodeStorage.getSubmitsPerOrder().values()) {
			this.executePhase(collection.renderChest$getCustomOutlinePhase(), context, renderPass);
		}
	}

	@ModifyExpressionValue(method = "hasAnyOutline", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"))
	private boolean renderChest$alsoHasAnyCustomOutline(boolean hasNoOutline, @Local(name = "collection") SubmitNodeCollection collection) {
		boolean hasNoCustomOutline = this.groupsByPhase.getOrDefault(collection.renderChest$getCustomOutlinePhase(), List.of()).isEmpty();

		return hasNoOutline && hasNoCustomOutline;
	}
}
