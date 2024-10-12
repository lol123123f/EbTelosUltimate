package eb.mods.ebtelosultimate.command.ClassView;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import eb.mods.ebtelosultimate.gui.PlayerInventoryGui;
import eb.mods.ebtelosultimate.gui.PlayerInventoryGuiClassSelection;
import eb.mods.ebtelosultimate.gui.PlayerInventoryScreen;
import eb.mods.ebtelosultimate.util.WItemSlotWithImage;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassView {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
        dispatcher.register(ClientCommandManager.literal("cv").then(ClientCommandManager.argument("Player name", StringArgumentType.string()).executes(ClassView::run)));
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        CompletableFuture.runAsync(() ->  {
            openPlayerInventory(StringArgumentType.getString(context, "Player name"));
        });
        return 1;
    }

    public static void openPlayerInventory(String playerName) {
        List<SlotData> slots = generateSlots(playerName);
        MinecraftClient.getInstance().send(() -> {
            MinecraftClient.getInstance().setScreen(new PlayerInventoryScreen(new PlayerInventoryGui(slots))); // Open the GUI
        });
    }

    public static class SlotData{
        public WItemSlot slot;
        public int x;
        public int y;
        public SlotData(WItemSlot slot, int x, int y){
            this.slot = slot;
            this.x = x;
            this.y = y;
        }
    }

    private static List<SlotData> generateSlots(String playerName) {
        List<SlotData> slots = new ArrayList<>();

        // Armor and off-hand slots
        Map<String, Map> characters = new HashMap<>();
        AtomicInteger tasks_completed = new AtomicInteger();
        List<String> class_seleted = new ArrayList<>();

        CompletableFuture.runAsync(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.telosrealms.com/lookup/player/" + playerName))
                    .header("accept", "application/json").build();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    Map json = new Gson().fromJson(response.body(), Map.class); //Json parsing
                    if (json != null && json.containsKey("data")) {
                        if (json.get("data") == null) {
                            assert MinecraftClient.getInstance().player != null;
                            MinecraftClient.getInstance().player.sendMessage(Text.literal("Player not found 1"), false);
                            return;
                        }
                        Map<String, Object> data = (Map<String, Object>) json.get("data");
                        if (data.containsKey("characters") && data.get("characters") != null) {
                            Map<String, Object> charactersJson = (Map<String, Object>) data.get("characters");
                            if (charactersJson.get("all") == null) {
                                assert MinecraftClient.getInstance().player != null;
                                MinecraftClient.getInstance().player.sendMessage(Text.literal("Player not found 2"), false);
                                return;
                            }
                            List<Map<String, Object>> charactersJsonAll = (List<Map<String, Object>>) charactersJson.get("all");
                            for (Map<String, Object> entry : charactersJsonAll) {
                                String class_name = entry.get("type").toString().split(":")[1];
                                String class_level = String.valueOf((int)Float.parseFloat(entry.get("level").toString()));
                                if (entry.get("inventory") == null && entry.get("inventory") instanceof Map) {
                                    assert MinecraftClient.getInstance().player != null;
                                    MinecraftClient.getInstance().player.sendMessage(Text.literal("Inventory not found"), false);
                                    return;
                                }
                                Map<String, Object> inventory = (Map<String, Object>) entry.get("inventory");
                                if (inventory.get("armor") == null && inventory.get("armor") instanceof List) {
                                    assert MinecraftClient.getInstance().player != null;
                                    MinecraftClient.getInstance().player.sendMessage(Text.literal("armor not found aborting"), false);
                                    return;
                                }
                                if (inventory.get("extra") == null && inventory.get("extra") instanceof List) {
                                    assert MinecraftClient.getInstance().player != null;
                                    MinecraftClient.getInstance().player.sendMessage(Text.literal("extra not found aborting"), false);
                                    return;
                                }
                                List<Map<String, Object>> armor_map = new ArrayList<>((Collection<? extends Map<String, Object>>) inventory.get("armor"));
                                Collections.reverse(armor_map);
                                armor_map.addAll((Collection<? extends Map<String, Object>>) inventory.get("extra"));
                                List<Map<String, Object>> inventory_map = new ArrayList<>((Collection<? extends Map<String, Object>>) inventory.get("storage"));
                                List<Map<String, Object>> hotBar_map = new ArrayList<>();
                                for (int i = 0; i < 9; i++) {
                                    hotBar_map.add(inventory_map.getFirst());
                                    inventory_map.removeFirst();
                                }
                                int temp = 1;
                                while (characters.containsKey(class_name)) {
                                    class_name = class_name + " " + temp;
                                    temp++;
                                }
                                characters.put(class_name, new HashMap<>());
                                characters.get(class_name).put("level", class_level);
                                characters.get(class_name).put("armor", armor_map);
                                characters.get(class_name).put("hot_bar", hotBar_map);
                                characters.get(class_name).put("inventory", inventory_map);
                            }
                            MinecraftClient.getInstance().send(() -> {
                                MinecraftClient.getInstance().setScreen(new CottonClientScreen(new PlayerInventoryGuiClassSelection(class_seleted, characters, playerName)));
                            });
                            while (!(MinecraftClient.getInstance().currentScreen instanceof CottonClientScreen))
                            //Wait for the screen to open since the screen is opened on wait for the next tick
                            {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            while (class_seleted.isEmpty() && MinecraftClient.getInstance().currentScreen instanceof CottonClientScreen) {
                                CottonClientScreen screen = (CottonClientScreen) MinecraftClient.getInstance().currentScreen;
                                if (!(screen.getDescription() instanceof PlayerInventoryGuiClassSelection)){
                                    break;
                                }
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (class_seleted.isEmpty()) {
                                assert MinecraftClient.getInstance().player != null;
                                MutableText ExitText = Text.empty();
                                ExitText.append(Text.literal("No class selected"));
                                ExitText.fillStyle(Style.EMPTY.withColor(Formatting.RED));
                                MinecraftClient.getInstance().player.sendMessage(ExitText, false);
                                class_seleted.add("exitSignal");
                            }
                        } else {
                            assert MinecraftClient.getInstance().player != null;
                            MinecraftClient.getInstance().player.sendMessage(Text.literal("Player not found 3"), false);
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.sendMessage(Text.literal("Error getting player inventory"), false);
            }
        }).thenAccept(result -> {
            if (class_seleted.isEmpty()) {
                return;
            }
            String class_name = class_seleted.getFirst();
            Map<String, Object> player_class = characters.get(class_name);


            // armor slots
            List<Map<String, Object>> armor = (List<Map<String, Object>>) player_class.get("armor");
            for (int i = 0; i < 5; i++) {

                int finalI = i;
                CompletableFuture.runAsync(() -> {
                    if (armor.get(finalI).get("type") == null || armor.get(finalI).get("type").toString().equals("empty")) {
                        slots.add(new SlotData(MakeSlot(), finalI + 2, 0));
                    } else {
                        slots.add(new SlotData(MakeSlotWithImage("https://cdn.telosrealms.com/assets/item/" + armor.get(finalI).get("identifier") + ".png", "https://cdn.telosrealms.com/assets/ui/" + armor.get(finalI).get("identifier") + ".png"), finalI + 2, 0));
                    }
                }).thenAccept(result1 -> {
                    tasks_completed.getAndIncrement();
                });
            }

            // hot-bar slots
            List<Map<String, Object>> hotBar = (List<Map<String, Object>>) player_class.get("hot_bar");
            for (int i = 0; i < 9; i++) {
                int finalI = i;
                CompletableFuture.runAsync(() -> {
                    if (hotBar.get(finalI).get("type") == null || hotBar.get(finalI).get("type").toString().equals("empty")) {
                        slots.add(new SlotData(MakeSlot(), finalI, 2));
                    } else {
                        slots.add(new SlotData(MakeSlotWithImage("https://cdn.telosrealms.com/assets/item/" + hotBar.get(finalI).get("identifier") + ".png", "https://cdn.telosrealms.com/assets/ui/" + hotBar.get(finalI).get("identifier") + ".png"), finalI, 2));
                    }
                }).thenAccept(result1 -> {
                    tasks_completed.getAndIncrement();
                });
            }

            // inventory slots
            List<Map<String, Object>> inventory = (List<Map<String, Object>>) player_class.get("inventory");
            int x = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    x++;
                    int finalI = i;
                    int finalJ = j;
                    int finalX = x;
                    CompletableFuture.runAsync(() -> {
                        if (inventory.get(finalX-1).get("type") == null || inventory.get(finalX-1).get("type").toString().equals("empty")) {
                            slots.add(new SlotData(MakeSlot(), finalJ, finalI + 4));
                        } else {
                            slots.add(new SlotData(MakeSlotWithImage("https://cdn.telosrealms.com/assets/item/" + inventory.get(finalX-1).get("identifier") + ".png", "https://cdn.telosrealms.com/assets/ui/" + inventory.get(finalX-1).get("identifier") + ".png"), finalJ, finalI + 4));
                        }
                    }).thenAccept(result1 -> {
                        tasks_completed.getAndIncrement();
                    });
                    tasks_completed.getAndIncrement();
                }
            }
        });
        boolean flag = false;
        while (slots.size() < 41 && !flag) {
            try {
                if (!class_seleted.isEmpty() && Objects.equals(class_seleted.getFirst(), "exitSignal")){
                    flag = true;
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (Objects.equals(class_seleted.getFirst(), "exitSignal")) {
            return null;
        }
        return slots;
    }

    private static WItemSlot MakeSlot() {
        return WItemSlot.of(new Inventory() {


            @Override
            public void clear() {

            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public ItemStack getStack(int slot) {
                return null;
            }

            @Override
            public ItemStack removeStack(int slot, int amount) {
                return null;
            }

            @Override
            public ItemStack removeStack(int slot) {
                return null;
            }

            @Override
            public void setStack(int slot, ItemStack stack) {

            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return false;
            }
        }, 0);
    }

    private static String itemNameFromMap(Map<String, Object> item) {
        return item.get("identifier").toString();
    }

    public static WItemSlot MakeSlotWithImage(String imageUrl) {
        return new WItemSlotWithImage(imageUrl);
    }

    public static WItemSlot MakeSlotWithImage(String imageUrl, String imageUrlDescription) {
        return new WItemSlotWithImage(imageUrl, imageUrlDescription);
    }
}
