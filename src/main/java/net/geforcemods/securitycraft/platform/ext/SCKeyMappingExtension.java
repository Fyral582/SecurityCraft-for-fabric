package net.geforcemods.securitycraft.platform.ext;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

/**
 * Recreates the parts of NeoForge's IKeyMappingExtension that SecurityCraft uses. Injected into {@link KeyMapping}.
 */
public interface SCKeyMappingExtension {
	default boolean isActiveAndMatches(InputConstants.Key keyCode) {
		return keyCode != InputConstants.UNKNOWN && keyCode.equals(KeyBindingHelper.getBoundKeyOf((KeyMapping) this));
	}
}
