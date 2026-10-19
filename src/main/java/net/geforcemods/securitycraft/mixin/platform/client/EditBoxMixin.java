package net.geforcemods.securitycraft.mixin.platform.client;

import java.util.function.Predicate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.geforcemods.securitycraft.platform.ext.SCEditBoxExtension;
import net.minecraft.client.gui.components.EditBox;

/**
 * Adds a text filter to edit boxes: changes to the text that don't match the filter are rejected
 */
@Mixin(EditBox.class)
public class EditBoxMixin implements SCEditBoxExtension {
	@Shadow
	private String value;
	@Unique
	private Predicate<String> securitycraft$filter = s -> true;
	@Unique
	private String securitycraft$previousValue;

	@Override
	public void setFilter(Predicate<String> filter) {
		securitycraft$filter = filter;
	}

	@Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
	private void securitycraft$filterSetValue(String newValue, CallbackInfo ci) {
		if (!securitycraft$filter.test(newValue))
			ci.cancel();
	}

	@Inject(method = {"insertText", "deleteCharsToPos"}, at = @At("HEAD"))
	private void securitycraft$rememberValue(CallbackInfo ci) {
		securitycraft$previousValue = value;
	}

	@Inject(method = {"insertText", "deleteCharsToPos"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/EditBox;value:Ljava/lang/String;", opcode = org.objectweb.asm.Opcodes.PUTFIELD, shift = At.Shift.AFTER), cancellable = true)
	private void securitycraft$filterChange(CallbackInfo ci) {
		if (!securitycraft$filter.test(value)) {
			value = securitycraft$previousValue;
			ci.cancel();
		}
	}
}
