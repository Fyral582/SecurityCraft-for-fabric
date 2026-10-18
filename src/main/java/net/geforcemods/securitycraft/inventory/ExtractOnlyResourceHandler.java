package net.geforcemods.securitycraft.inventory;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.FilteringStorage;

/**
 * A storage wrapper that only allows extracting from the wrapped storage
 */
public class ExtractOnlyResourceHandler<T> extends FilteringStorage<T> {
	public ExtractOnlyResourceHandler(Storage<T> delegate) {
		super(delegate);
	}

	@Override
	protected boolean canInsert(T resource) {
		return false;
	}
}
