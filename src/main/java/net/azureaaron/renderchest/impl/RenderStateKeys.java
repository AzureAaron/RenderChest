package net.azureaaron.renderchest.impl;

import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

public final class RenderStateKeys {
	public static final RenderStateDataKey<Integer> ENTITY_CUSTOM_GLOW_COLOUR = RenderStateDataKey.create(() -> "Render Chest custom glow colour");
	public static final RenderStateDataKey<Boolean> FRAME_USES_CUSTOM_GLOW = RenderStateDataKey.create(() -> "Render Chest frame uses custom glow");

	private RenderStateKeys() {}
}
