package net.azureaaron.renderchest.api;

import net.minecraft.client.renderer.entity.state.EntityRenderState;

/// Contains constants used with {@link CustomGlowCallback}.
public final class GlowConstants {
	/// Indicates that the handler does not want to apply any glow to the given entity.
	public static final int NO_GLOW = EntityRenderState.NO_OUTLINE;
	/// Indicates that the handler wants to strip the entity of any glow it may have (custom or vanilla).
	public static final int REMOVE_GLOW = Integer.MAX_VALUE;

	private GlowConstants() {}
}
