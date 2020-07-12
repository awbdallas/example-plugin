package io.ryoung.heatmap;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.ImmutableList;
import com.google.inject.Provides;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Varbits;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
        name = "Bank Heatmap"
)
public class HeatmapPlugin extends Plugin {
    private static final List<Varbits> TAB_VARBITS = ImmutableList.of(
            Varbits.BANK_TAB_ONE_COUNT,
            Varbits.BANK_TAB_TWO_COUNT,
            Varbits.BANK_TAB_THREE_COUNT,
            Varbits.BANK_TAB_FOUR_COUNT,
            Varbits.BANK_TAB_FIVE_COUNT,
            Varbits.BANK_TAB_SIX_COUNT,
            Varbits.BANK_TAB_SEVEN_COUNT,
            Varbits.BANK_TAB_EIGHT_COUNT,
            Varbits.BANK_TAB_NINE_COUNT
    );

    @Inject
    private Client client;

    @Inject
    private HeatmapCalculation heatmapCalculation;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private HeatmapItemOverlay heatmapItemOverlay;

    @Inject
    private HeatmapConfig config;

    @Getter
    private HeatmapMode heatmapMode = HeatmapMode.NULL;

    @Provides
    HeatmapConfig provideHeatmapConfig(ConfigManager configManager) {
        return configManager.getConfig(HeatmapConfig.class);
    }

    @Override
    protected void startUp() {
        overlayManager.add(heatmapItemOverlay);
        log.info("Hit startup");
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(heatmapItemOverlay);
        heatmapMode = HeatmapMode.NULL;
    }

    @Subscribe
    public void onScriptCallbackEvent(ScriptCallbackEvent event) {
        if ("setBankTitle".equals(event.getEventName())) {
            Item[] items = getBankTabItems();
            heatmapItemOverlay.getHeatmapImages().invalidateAll();
            if (config.isUsingLogNormalization()) {
                heatmapCalculation.calculate(items, HeatmapCalculation.TYPE.NORMAL);
            } else {
                heatmapCalculation.calculate(items, HeatmapCalculation.TYPE.LOG);
            }

        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("heatmap")) {
            return;
        }
    }

    private Item[] getBankTabItems() {
        final ItemContainer container = client.getItemContainer(InventoryID.BANK);
        if (container == null) {
            return null;
        }

        final Item[] items = container.getItems();
        int currentTab = client.getVar(Varbits.CURRENT_BANK_TAB);

        if (currentTab > 0) {
            int startIndex = 0;

            for (int i = currentTab - 1; i > 0; i--) {
                startIndex += client.getVar(TAB_VARBITS.get(i - 1));
            }

            int itemCount = client.getVar(TAB_VARBITS.get(currentTab - 1));
            return Arrays.copyOfRange(items, startIndex, startIndex + itemCount);
        }

        return items;
    }

    HeatmapItem getHeatmapItem(int id) {
        return heatmapCalculation.getHeatmapItems().get(id);
    }

}