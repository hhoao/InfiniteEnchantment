package org.hhoa.mc.infinite_enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod("infinite_enchantment")
public class InfiniteEnchantment {

    private static final Logger LOGGER = LogManager.getLogger();

    public InfiniteEnchantment() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeft();
        ItemStack rightItem = event.getRight();

        Map<Enchantment, Integer> leftEnchantments = EnchantmentHelper.getEnchantments(leftItem);
        Map<Enchantment, Integer> rightEnchantments = EnchantmentHelper.getEnchantments(rightItem);

        if (leftEnchantments.isEmpty() || rightEnchantments.isEmpty()) {
            return;
        }

        Map<Enchantment, Integer> resultEnchantments = new HashMap<>(leftEnchantments);

        boolean attachMaxLevel = false;
        int totalLevel = 0;
        for (Map.Entry<Enchantment, Integer> enchantmentIntegerEntry : rightEnchantments.entrySet()) {
            Enchantment enchantment = enchantmentIntegerEntry.getKey();
            Integer rightLevel = enchantmentIntegerEntry.getValue();
            int leftLevel = leftEnchantments.getOrDefault(enchantment, 0);
            if (leftLevel > 0){
                int newLevel = leftLevel == rightLevel ? leftLevel + 1 : Math.max(leftLevel, rightLevel);
                resultEnchantments.put(enchantment, newLevel);
                if (newLevel > enchantment.getMaxLevel()) {
                    attachMaxLevel = true;
                }
                totalLevel += newLevel;
            } else {
                totalLevel += leftLevel;
            }
        }
        if (!attachMaxLevel) {
            return;
        }

        ItemStack result = leftItem.copy();
        EnchantmentHelper.setEnchantments(resultEnchantments, result);
        event.setOutput(result);
        event.setCost((int) (34 + Math.sqrt(totalLevel)));
    }


    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("InfiniteEnchantment setup!");
    }
}
