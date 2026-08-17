package net.azureaaron.renderchest.impl.injected;

import java.util.Optional;

import net.minecraft.client.renderer.rendertype.RenderType;

public interface CustomOutlineRenderTypeHolder {
	default Optional<RenderType> renderChest$getCustomOutlineRenderType() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
