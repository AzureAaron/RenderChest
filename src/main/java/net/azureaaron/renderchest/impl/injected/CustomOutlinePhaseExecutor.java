package net.azureaaron.renderchest.impl.injected;

public interface CustomOutlinePhaseExecutor {
	default void renderChest$executeCustomOutline() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
