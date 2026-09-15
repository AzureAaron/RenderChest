package net.azureaaron.renderchest.impl.injected;

import com.mojang.renderpearl.api.commands.RenderPass;

public interface CustomOutlinePhaseExecutor {
	default void renderChest$executeCustomOutline(RenderPass renderPass) {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
