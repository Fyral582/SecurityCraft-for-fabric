package net.geforcemods.securitycraft.platform.model;

import java.util.IdentityHashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

/**
 * Fabric replacement for NeoForge's ModelData. Instances are handed to models through Fabric's block entity render data.
 */
public final class ModelData {
	public static final ModelData EMPTY = new ModelData(Map.of());
	private final Map<ModelProperty<?>, Object> properties;

	private ModelData(Map<ModelProperty<?>, Object> properties) {
		this.properties = properties;
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public <T> T get(ModelProperty<T> property) {
		return (T) properties.get(property);
	}

	public boolean has(ModelProperty<?> property) {
		return properties.containsKey(property);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private final Map<ModelProperty<?>, Object> properties = new IdentityHashMap<>();

		private Builder() {}

		public <T> Builder with(ModelProperty<T> property, T value) {
			properties.put(property, value);
			return this;
		}

		public ModelData build() {
			return new ModelData(new IdentityHashMap<>(properties));
		}
	}
}
