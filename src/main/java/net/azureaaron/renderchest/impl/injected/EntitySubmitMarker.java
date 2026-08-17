package net.azureaaron.renderchest.impl.injected;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

public interface EntitySubmitMarker {
	default @Nullable EntityRenderState renderChest$getEntityStateBeingSubmitted() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
