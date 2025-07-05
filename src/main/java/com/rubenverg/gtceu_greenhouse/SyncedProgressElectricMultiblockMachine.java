package com.rubenverg.gtceu_greenhouse;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import org.jetbrains.annotations.NotNull;

public class SyncedProgressElectricMultiblockMachine extends WorkableElectricMultiblockMachine implements ISyncedProgress {
	protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SyncedProgressElectricMultiblockMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

	@Persisted
	@DescSynced
	@RequireRerender
	private double progressPercent;

	public SyncedProgressElectricMultiblockMachine(IMachineBlockEntity holder, Object... args) {
		super(holder, args);
	}

	{
		subscribeServerTick(() -> {
			progressPercent = recipeLogic.getProgressPercent();
		});
	}

	@Override
	public @NotNull ManagedFieldHolder getFieldHolder() {
		return MANAGED_FIELD_HOLDER;
	}

	@Override
	public double getProgressPercent() {
		return progressPercent;
	}
}
