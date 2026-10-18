package net.geforcemods.securitycraft.inventory;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;

/**
 * A storage wrapper that only allows inserting into the wrapped storage
 */
public class InsertOnlyResourceHandler<T> extends FilteringStorage<T> {
	public InsertOnlyResourceHandler(Storage<T> delegate) {
		super(delegate);
	}

	@Override
	protected boolean canExtract(T resource) {
		return false;
	}
}
