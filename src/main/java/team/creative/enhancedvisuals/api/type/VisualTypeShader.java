package team.creative.enhancedvisuals.api.type;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.enhancedvisuals.api.VisualCategory;
import team.creative.enhancedvisuals.client.render.EnhancedShaderGroup;
import team.creative.enhancedvisuals.common.visual.Visual;

public abstract class VisualTypeShader extends VisualType {
	
	public ResourceLocation location;
	
	public VisualTypeShader(ResourceLocation name, ResourceLocation location) {
		super(name, VisualCategory.shader);
		this.location = location;
	}
	
	public EnhancedShaderGroup shaderGroup = null;
	
	@Override
	@OnlyIn(value = Dist.CLIENT)
	public void loadResources(IResourceManager manager) {
		if (ShaderLinkHelper.getStaticShaderLinkHelper() != null) {
			Minecraft mc = Minecraft.getInstance();
			if (shaderGroup != null)
				shaderGroup.close();
			
			try {
				shaderGroup = new EnhancedShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), location);
				shaderGroup.createBindFramebuffers(mc.getFramebuffer().framebufferWidth, mc.getFramebuffer().framebufferHeight);
			} catch (JsonSyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	@OnlyIn(value = Dist.CLIENT)
	public int getVariantAmount() {
		return 0;
	}
	
	@Override
	@OnlyIn(value = Dist.CLIENT)
	public boolean supportsColor() {
		return false;
	}
	
	@Override
	@OnlyIn(value = Dist.CLIENT)
	public void resize(Framebuffer buffer) {
		if (shaderGroup != null)
			shaderGroup.createBindFramebuffers(buffer.framebufferWidth, buffer.framebufferHeight);
	}
	
	@Override
	public void render(Visual visual, TextureManager manager, int screenWidth, int screenHeight, float partialTicks) {
		if (shaderGroup == null)
			loadResources(Minecraft.getInstance().getResourceManager());
		
		if (shaderGroup != null) {
			changeProperties(visual.properties.opacity);
			shaderGroup.render(partialTicks);
		}
	}
	
	@OnlyIn(value = Dist.CLIENT)
	public abstract void changeProperties(float intensity);
	
}