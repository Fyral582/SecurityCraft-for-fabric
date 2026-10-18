package net.geforcemods.securitycraft.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.fabricmc.loader.api.FabricLoader;
import net.geforcemods.securitycraft.SecurityCraft;

/**
 * On NeoForge, other mods register their implementations through InterModComms. On Fabric, mods can either call the register
 * methods of this class directly, or implement {@link Addon} and declare it as a "securitycraft" entrypoint in their
 * fabric.mod.json.
 */
public class SecurityCraftAPI {
	private static List<IExtractionBlock> registeredExtractionBlocks = new ArrayList<>();
	private static List<IPasscodeConvertible> registeredPasscodeConvertibles = new ArrayList<>();
	private static List<IAttackTargetCheck> registeredSentryAttackTargetChecks = new ArrayList<>();
	private static List<IDoorActivator> registeredDoorActivators = new ArrayList<>();
	public static final String IMC_EXTRACTION_BLOCK_MSG = "registerExtractionBlock";
	public static final String IMC_PASSCODE_CONVERTIBLE_MSG = "registerPasscodeConvertible";
	public static final String IMC_SENTRY_ATTACK_TARGET_MSG = "registerSentryAttackTargetCheck";
	public static final String IMC_DOOR_ACTIVATOR_MSG = "registerDoorActivator";
	private static boolean finished = false;

	private SecurityCraftAPI() {}

	public static void registerExtractionBlock(Supplier<? extends IExtractionBlock> extractionBlock) {
		checkNotFinished();
		registeredExtractionBlocks.add(extractionBlock.get());
	}

	public static void registerPasscodeConvertible(Supplier<? extends IPasscodeConvertible> passcodeConvertible) {
		checkNotFinished();
		registeredPasscodeConvertibles.add(passcodeConvertible.get());
	}

	public static void registerSentryAttackTargetCheck(Supplier<? extends IAttackTargetCheck> attackTargetCheck) {
		checkNotFinished();
		registeredSentryAttackTargetChecks.add(attackTargetCheck.get());
	}

	public static void registerDoorActivator(Supplier<? extends IDoorActivator> doorActivator) {
		checkNotFinished();
		registeredDoorActivators.add(doorActivator.get());
	}

	private static void checkNotFinished() {
		if (finished)
			throw new IllegalStateException("SecurityCraft's API registration has already finished. Register your implementations through the \"securitycraft\" entrypoint.");
	}

	/**
	 * Collects the registrations of all addons and freezes the registered implementations
	 */
	public static void finishRegistration() {
		for (Addon addon : FabricLoader.getInstance().getEntrypoints(SecurityCraft.MODID, Addon.class)) {
			addon.registerSecurityCraftContent();
		}

		finished = true;
		registeredExtractionBlocks = Collections.unmodifiableList(registeredExtractionBlocks);
		registeredPasscodeConvertibles = Collections.unmodifiableList(registeredPasscodeConvertibles);
		registeredSentryAttackTargetChecks = Collections.unmodifiableList(registeredSentryAttackTargetChecks);
		registeredDoorActivators = Collections.unmodifiableList(registeredDoorActivators);
	}

	public static List<IExtractionBlock> getRegisteredExtractionBlocks() {
		return registeredExtractionBlocks;
	}

	public static List<IPasscodeConvertible> getRegisteredPasscodeConvertibles() {
		return registeredPasscodeConvertibles;
	}

	public static List<IAttackTargetCheck> getRegisteredSentryAttackTargetChecks() {
		return registeredSentryAttackTargetChecks;
	}

	public static List<IDoorActivator> getRegisteredDoorActivators() {
		return registeredDoorActivators;
	}

	/**
	 * Entrypoint interface for mods that want to register implementations with SecurityCraft's API. Declare the implementing
	 * class as a "securitycraft" entrypoint in your fabric.mod.json.
	 */
	@FunctionalInterface
	public interface Addon {
		void registerSecurityCraftContent();
	}
}
