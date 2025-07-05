package com.rubenverg.gtceu_greenhouse.integration.kjs;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.gregtechceu.gtceu.integration.kjs.GTRegistryInfo;
import dev.latvian.mods.kubejs.KubeJSPlugin;

public class GreenhouseKubeJSPlugin extends KubeJSPlugin {
	@Override
	public void initStartup() {
		super.initStartup();
	}

	@Override
	public void init() {
		GTRegistryInfo.MACHINE.addType("greenhouse", GreenhouseMachineBuilder.class,
			id -> GreenhouseMachineBuilder.createGreenhouse(
				GTRegistration.REGISTRATE, id.getPath(),
				MetaMachineBlock::new, MetaMachineItem::new, MetaMachineBlockEntity::createBlockEntity),
			false);
	}
}
