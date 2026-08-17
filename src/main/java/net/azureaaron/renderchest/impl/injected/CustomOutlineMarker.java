package net.azureaaron.renderchest.impl.injected;

public interface CustomOutlineMarker {
	/// @implNote Ensure that this flag is set after object initialization to ensure it doesn't get overwritten by the
	/// default constructor.
	default void renderChest$setHasCustomOutline() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}

	default boolean renderChest$hasCustomOutline() {
		throw new UnsupportedOperationException("Implemented via Mixin");
	}
}
