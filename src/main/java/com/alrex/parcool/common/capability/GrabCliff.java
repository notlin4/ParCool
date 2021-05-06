package com.alrex.parcool.common.capability;

import com.alrex.parcool.ParCoolConfig;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraftforge.common.util.LazyOptional;

public class GrabCliff implements IGrabCliff {
	private boolean grabbing = false;
	private int grabbingTime = 0;
	private int notGrabbingTime = 0;

	@Override
	public boolean canGrabCliff(ClientPlayerEntity player) {
		IStamina stamina;
		{
			LazyOptional<IStamina> staminaOptional = player.getCapability(IStamina.StaminaProvider.STAMINA_CAPABILITY);
			if (!staminaOptional.isPresent()) return false;
			stamina = staminaOptional.resolve().get();
		}
		double ySpeed = player.getMotion().y;
		return !stamina.isExhausted() && ySpeed < 0.2 && ParCoolConfig.CONFIG_CLIENT.canGrabCliff.get() && KeyBindings.getKeyGrabWall().isKeyDown() && player.getHeldItemMainhand().isEmpty() && player.getHeldItemOffhand().isEmpty() && WorldUtil.existsGrabbableWall(player);
	}

	@Override
	public boolean canJumpOnCliff(ClientPlayerEntity player) {
		return grabbing && ParCoolConfig.CONFIG_CLIENT.canGrabCliff.get() && grabbingTime > 3 && KeyRecorder.keyJumpState.isPressed();
	}

	@Override
	public boolean isGrabbing() {
		return grabbing;
	}

	@Override
	public void setGrabbing(boolean grabbing) {
		this.grabbing = grabbing;
	}


	@Override
	public int getGrabbingTime() {
		return grabbingTime;
	}

	@Override
	public int getNotGrabbingTime() {
		return notGrabbingTime;
	}

	@Override
	public void updateTime() {
		if (grabbing) {
			grabbingTime++;
			notGrabbingTime = 0;
		} else {
			notGrabbingTime++;
			grabbingTime = 0;
		}
	}

	@Override
	public int getStaminaConsumptionGrab() {
		return 4;
	}

	@Override
	public int getStaminaConsumptionClimbUp() {
		return 200;
	}
}
