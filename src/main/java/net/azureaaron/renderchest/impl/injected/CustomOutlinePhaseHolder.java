package net.azureaaron.renderchest.impl.injected;

import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;

public interface CustomOutlinePhaseHolder {
	default SimpleFeatureRenderPhase renderChest$getCustomOutlinePhase() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
