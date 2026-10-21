package net.geforcemods.securitycraft.entity.sentry;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;


public interface ISentryBulletContainer {
	public Storage<ItemVariant> getHandlerForSentry(Sentry sentry);
}
