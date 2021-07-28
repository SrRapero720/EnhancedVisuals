package team.creative.enhancedvisuals.api.type;

import com.mojang.blaze3d.shaders.Uniform;

import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualHandler;

public class VisualTypeSaturation extends VisualTypeShader {
    
    public VisualTypeSaturation(String name) {
        super(name, new ResourceLocation("shaders/post/desaturate.json"));
    }
    
    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void changeProperties(float intensity) {
        for (PostPass pass : postChain.getPasses()) {
            Uniform shaderuniform = pass.getEffect().getUniform("Saturation");
            
            if (shaderuniform != null)
                shaderuniform.set(intensity);
        }
    }
    
    @Override
    public boolean isVisible(VisualHandler handler, Visual visual) {
        return visual.getOpacity() != 1;
    }
}
