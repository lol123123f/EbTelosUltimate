package eb.mods.ebtelosultimate.gui;

import com.mojang.authlib.properties.PropertyMap;
import eb.mods.ebtelosultimate.util.buttonWithToolTip;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import java.util.*;

public class PlayerInventoryGuiClassSelection extends LightweightGuiDescription {
    private final List<String> selected;
    public PlayerInventoryGuiClassSelection(List<String> selected, Map<String, Map> characters, String playerName) {
        this.selected = selected;


        WGridPanel root = new WGridPanel();
        root.setSize(90, 40);
        root.setInsets(Insets.ROOT_PANEL);
        //List<ItemStack> items = new ArrayList<>();

        int i = 0;
        for (String className : characters.keySet()) {
            ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
            ProfileComponent profileComponent = new ProfileComponent(Optional.of(playerName), Optional.empty(), new PropertyMap());
            skull.set(DataComponentTypes.PROFILE, profileComponent);
            WItem slot = new WItem(skull);
            buttonWithToolTip button = new buttonWithToolTip(className + " - Level: " + characters.get(className).get("level"), this::onButtonClicked);
            if (!(i % 2 == 0)) {
                i++;
            }
            root.add(slot, i, 0);
            root.add(button, i, 0);
            i++;
        }
        root.validate(this);
        setRootPanel(root);
    }

    private InputResult onButtonClicked(buttonWithToolTip btn) {
        selected.add(btn.getToolTip().split(" - ")[0]);
        return InputResult.PROCESSED;
    }



}
