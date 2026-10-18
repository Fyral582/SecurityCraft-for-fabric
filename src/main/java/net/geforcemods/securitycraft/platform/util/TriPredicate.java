package net.geforcemods.securitycraft.platform.util;

/**
 * Replacement for NeoForge's TriPredicate
 */
@FunctionalInterface
public interface TriPredicate<A, B, C> {
	boolean test(A a, B b, C c);
}
