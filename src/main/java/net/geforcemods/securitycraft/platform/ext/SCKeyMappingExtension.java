package net.geforcemods.securitycraft.platform.ext;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

/**
 * Recreates the parts of NeoForge's IKeyMappingExtension that SecurityCraft uses. Injected into {@link KeyMapping}.
 */
public interface SCKeyMappingExtension {
	default boolean isActiveAndMatches(InputConstants.Key keyCode) {
		return keyCode != InputConstants.UNKNOWN && keyCode.equals(KeyMappingHelper.getBoundKeyOf((KeyMapping) this));
	}
}
