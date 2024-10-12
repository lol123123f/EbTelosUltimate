package eb.mods.ebtelosultimate.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.TextureUtil;
import eb.mods.ebtelosultimate.client.EbTelosUltimateClient;
import java.awt.image.BufferedImage;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public class TextureHelper {
    private final Identifier textureId;
    private final String url;

    public int Height = 0;
    public int Width = 0;

    public TextureHelper(String url) {
        this.url = url;
        // Generate a unique identifier for this texture
        this.textureId = Identifier.of(EbTelosUltimateClient.modid, "external_texture_" + url.hashCode());
        // Download the image from the URL
        BufferedImage bufferedImage = ImageDownloader.downloadImage(url);

        if (bufferedImage != null) {
            // Upload the image as a Minecraft texture
            NativeImage nativeImage = convertToNativeImage(bufferedImage);
            MinecraftClient.getInstance().execute(() -> {
                NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
                Height = Objects.requireNonNull(texture.getImage()).getHeight();
                Width = Objects.requireNonNull(texture.getImage()).getWidth();
                // Register the texture with the TextureManager
                MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);
            });

        }
    }

    private NativeImage convertToNativeImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        NativeImage nativeImage = new NativeImage(width, height, true);

        // Copy the pixel data from the BufferedImage to NativeImage
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = bufferedImage.getRGB(x, y);  // Get pixel in ARGB format

                // Extract the alpha, red, green, and blue components
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                // Minecraft's NativeImage expects the color in ABGR format (Alpha-Blue-Green-Red)
                int abgr = (alpha << 24) | (blue << 16) | (green << 8) | red;

                // Set the pixel in the NativeImage
                nativeImage.setColor(x, y, abgr);
            }
        }
        return nativeImage;
    }

    public void bindTexture() {
        // Bind the custom texture to Minecraft's rendering system
        RenderSystem.setShaderTexture(0, textureId);
    }

    public Identifier getTexture() {
        return textureId;
    }
}
