package io.wispforest.accessories_compat.common;

import io.wispforest.accessories_compat.AccessoriesCompatInit;
import io.wispforest.accessories_compat.trinkets.utils.SlotIdRedirect;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.SectionHeader;
import io.wispforest.owo.config.annotation.Sync;

import java.util.ArrayList;
import java.util.List;


@Modmenu(modId = AccessoriesCompatInit.MODID)
@Config(name = "accessories_compat", wrapperName = "AccessoriesCompatConfig")
public class AccessoriesCompatConfigModel {
    @SectionHeader("trinkets")
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public List<SlotIdRedirect> slotIdRedirects = new ArrayList<>(List.of(new SlotIdRedirect("charm/spell_book", "spellbook", 1)));

    public boolean dumpDataWhenNullEntries = false;

    @SectionHeader("curios")
    public boolean addPocketSlot = false;
    public boolean showPocketSlotInTooltip = false;
}
