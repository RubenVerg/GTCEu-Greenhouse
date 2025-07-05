package com.rubenverg.gtceu_greenhouse;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(GreenhouseMod.MOD_ID)
public class GreenhouseMod {
	public static final String MOD_ID = "gtceu_greenhouse";

	private static final Logger LOGGER = LogUtils.getLogger();

	@SuppressWarnings("removal")
	public GreenhouseMod() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(this::commonSetup);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		LOGGER.info("Hii:3");
	}
}
