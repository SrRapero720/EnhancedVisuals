package team.creative.enhancedvisuals.client;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import team.creative.creativecore.common.config.premade.IntMinMax;
import team.creative.creativecore.common.config.premade.curve.Curve;
import team.creative.creativecore.common.config.premade.curve.DecimalCurve;
import team.creative.creativecore.common.util.type.HashMapList;
import team.creative.enhancedvisuals.EnhancedVisuals;
import team.creative.enhancedvisuals.api.Particle;
import team.creative.enhancedvisuals.api.Visual;
import team.creative.enhancedvisuals.api.VisualCategory;
import team.creative.enhancedvisuals.api.VisualHandler;
import team.creative.enhancedvisuals.api.type.VisualType;
import team.creative.enhancedvisuals.common.event.EVEvents;
import team.creative.enhancedvisuals.common.visual.VisualRegistry;

public class VisualManager {
    
    private static Minecraft mc = Minecraft.getInstance();
    public static Random rand = new Random();
    private static HashMapList<VisualCategory, Visual> visuals = new HashMapList<>();
    
    public static void onTick(@Nullable PlayerEntity player) {
        boolean areEyesInWater = player != null && EVEvents.areEyesInWater(player);
        
        synchronized (visuals) {
            for (Iterator<Visual> iterator = visuals.iterator(); iterator.hasNext();) {
                Visual visual = iterator.next();
                
                int factor = 1;
                if (areEyesInWater && visual.isAffectedByWater())
                    factor = EnhancedVisuals.CONFIG.waterSubstractFactor;
                
                for (int i = 0; i < factor; i++) {
                    if (!visual.tick()) {
                        visual.removeFromDisplay();
                        iterator.remove();
                        break;
                    }
                }
            }
            
            for (VisualHandler handler : VisualRegistry.handlers()) {
                if (handler.isEnabled(player))
                    handler.tick(player);
            }
        }
        
        if (player != null && !player.isAlive())
            VisualManager.clearParticles();
    }
    
    public static Collection<Visual> visuals(VisualCategory category) {
        return visuals.get(category);
    }
    
    public static void clearParticles() {
        synchronized (visuals) {
            visuals.removeKey(VisualCategory.particle);
        }
    }
    
    public static void add(Visual visual) {
        if (!visual.type.disabled) {
            visual.addToDisplay();
            visuals.add(visual.getCategory(), visual);
        }
    }
    
    public static Visual addVisualFadeOut(VisualType vt, VisualHandler handler, IntMinMax time) {
        return addVisualFadeOut(vt, handler, new DecimalCurve(0, 1, time.next(rand), 0));
    }
    
    public static Visual addVisualFadeOut(VisualType vt, VisualHandler handler, int time) {
        return addVisualFadeOut(vt, handler, new DecimalCurve(0, 1, time, 0));
    }
    
    public static Visual addVisualFadeOut(VisualType vt, VisualHandler handler, Curve curve) {
        Visual v = new Visual(vt, handler, curve, vt.getVariantAmount() > 1 ? rand.nextInt(vt.getVariantAmount()) : 0);
        add(v);
        return v;
    }
    
    public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, IntMinMax time, boolean rotate) {
        addParticlesFadeOut(vt, handler, count, new DecimalCurve(0, 1, time.next(rand), 0), rotate, null);
    }
    
    public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, IntMinMax time, boolean rotate, Color color) {
        addParticlesFadeOut(vt, handler, count, new DecimalCurve(0, 1, time.next(rand), 0), rotate, color);
    }
    
    public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, int time, boolean rotate) {
        addParticlesFadeOut(vt, handler, count, new DecimalCurve(0, 1, time, 0), rotate, null);
    }
    
    public static void addParticlesFadeOut(VisualType vt, VisualHandler handler, int count, Curve curve, boolean rotate, Color color) {
        if (vt.disabled)
            return;
        for (int i = 0; i < count; i++) {
            int screenWidth = mc.getMainWindow().getScaledWidth();
            int screenHeight = mc.getMainWindow().getScaledHeight();
            
            int width = vt.getWidth(screenWidth);
            int height = vt.getHeight(screenHeight);
            
            if (vt.scaleVariants()) {
                double scale = vt.randomScale(rand);
                width *= scale;
                height *= scale;
            }
            
            Particle particle = new Particle(vt, handler, curve, generateOffset(rand, screenWidth, width), generateOffset(rand, screenHeight, height), width, height, rotate ? rand
                    .nextFloat() * 360 : 0, rand.nextInt(vt.getVariantAmount()));
            particle.color = color;
            add(particle);
        }
    }
    
    public static int generateOffset(Random rand, int dimensionLength, int spacingBuffer) {
        int half = dimensionLength / 2;
        float multiplier = (float) (1 - Math.pow(rand.nextDouble(), 2));
        float textureCenterPosition = rand.nextInt(2) == 0 ? half + half * multiplier : half - half * multiplier;
        return (int) (textureCenterPosition - (spacingBuffer / 2.0F));
    }
    
}
