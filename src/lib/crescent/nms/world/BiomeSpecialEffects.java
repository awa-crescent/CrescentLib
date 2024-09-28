package lib.crescent.nms.world;

import java.util.Optional;

import javax.annotation.Nullable;

import lib.crescent.nms.NMSManipulator;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.biome.BiomeFog.GrassColor;
import net.minecraft.world.level.biome.BiomeParticles;
import net.minecraft.world.level.biome.CaveSound;
import net.minecraft.world.level.biome.CaveSoundSettings;

public class BiomeSpecialEffects {
	private static final String class_prefix = "net.minecraft.world.level.biome.BiomeSpecialEffects.";

	private static final void setBiomeSpecialEffectsIntValue(BiomeFog biome_effects, String name, int value) {
		NMSManipulator.setInt(biome_effects, class_prefix + name, value);
	}

	private static final void setBiomeSpecialEffectsObjectValue(BiomeFog biome_effects, String name, Object value) {
		NMSManipulator.setObject(biome_effects, class_prefix + name, value);
	}

	/**
	 * 设置群系雾的颜色，十六进制
	 * 
	 * @param biome_effects
	 * @param fogColor
	 */
	public static final void setFogColor(BiomeFog biome_effects, int fogColor) {
		setBiomeSpecialEffectsIntValue(biome_effects, "skyColor", fogColor);
	}

	public static final void setFogColor(BiomeBase biome, int fogColor) {
		setFogColor(Biome.getBiomeSpecialEffects(biome), fogColor);
	}

	/**
	 * 设置群系水的颜色，十六进制
	 * 
	 * @param biome_effects
	 * @param fogColor
	 */
	public static final void setWaterColor(BiomeFog biome_effects, int waterColor) {
		setBiomeSpecialEffectsIntValue(biome_effects, "waterColor", waterColor);
	}

	public static final void setWaterColor(BiomeBase biome, int fogColor) {
		setWaterColor(Biome.getBiomeSpecialEffects(biome), fogColor);
	}

	/**
	 * 设置群系水中雾的颜色，十六进制
	 * 
	 * @param biome_effects
	 * @param fogColor
	 */
	public static final void setWaterFogColor(BiomeFog biome_effects, int waterFogColor) {
		setBiomeSpecialEffectsIntValue(biome_effects, "waterFogColor", waterFogColor);
	}

	public static final void setWaterFogColor(BiomeBase biome, int fogColor) {
		setWaterFogColor(Biome.getBiomeSpecialEffects(biome), fogColor);
	}

	/**
	 * 设置群系天空颜色，十六进制
	 * 
	 * @param biome_effects
	 * @param skyColor
	 */
	public static final void setSkyColor(BiomeFog biome_effects, int skyColor) {
		setBiomeSpecialEffectsIntValue(biome_effects, "skyColor", skyColor);
	}

	public static final void setSkyColor(BiomeBase biome, int fogColor) {
		setSkyColor(Biome.getBiomeSpecialEffects(biome), fogColor);
	}

	/**
	 * 设置群系树叶颜色，十六进制
	 * 
	 * @param biome_effects
	 * @param skyColor
	 */
	public static final void setFoliageColorOverride(BiomeFog biome_effects, int foliageColorOverride) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "foliageColorOverride", Optional.of(foliageColorOverride));
	}

	public static final void setFoliageColorOverride(BiomeBase biome, int foliageColorOverride) {
		setFoliageColorOverride(Biome.getBiomeSpecialEffects(biome), foliageColorOverride);
	}

	/**
	 * 设置群系草的颜色，十六进制
	 * 
	 * @param biome_effects
	 * @param skyColor
	 */
	public static final void setGrassColorOverride(BiomeFog biome_effects, int grassColorOverride) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "grassColorOverride", Optional.of(grassColorOverride));
	}

	public static final void setGrassColorOverride(BiomeBase biome, int grassColorOverride) {
		setGrassColorOverride(Biome.getBiomeSpecialEffects(biome), grassColorOverride);
	}

	/**
	 * 设置草的颜色修饰方法
	 * 
	 * @param biome_effects
	 * @param biomefog_grasscolor
	 */
	public static final void setGrassColorModifier(BiomeFog biome_effects, GrassColor grassColorModifier) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "grassColorModifier", grassColorModifier);
	}

	public static final void setGrassColorModifier(BiomeBase biome, GrassColor grassColorModifier) {
		setGrassColorModifier(Biome.getBiomeSpecialEffects(biome), grassColorModifier);
	}

	/**
	 * 设置群系的粒子效果
	 * 
	 * @param biome_effects
	 * @param ambientParticleSettings
	 */
	public static final void setAmbientParticle(BiomeFog biome_effects, BiomeParticles ambientParticleSettings) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "ambientParticleSettings", Optional.of(ambientParticleSettings));
	}

	public static final void setAmbientParticle(BiomeBase biome, BiomeParticles ambientParticleSettings) {
		setAmbientParticle(Biome.getBiomeSpecialEffects(biome), ambientParticleSettings);
	}

	/**
	 * 设置群系的循环自然音效
	 * 
	 * @param biome_effects
	 * @param ambientLoopSoundEvent
	 */
	public static final void setAmbientLoopSound(BiomeFog biome_effects, Holder<SoundEffect> ambientLoopSoundEvent) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "ambientLoopSoundEvent", Optional.of(ambientLoopSoundEvent));
	}

	public static final void setAmbientLoopSound(BiomeBase biome, Holder<SoundEffect> ambientLoopSoundEvent) {
		setAmbientLoopSound(Biome.getBiomeSpecialEffects(biome), ambientLoopSoundEvent);
	}

	/**
	 * 设置群系的恐惧情绪音效（玩家在洞穴、黑暗中情绪值增加，到达100%时播放该音效）
	 * 
	 * @param biome_effects
	 * @param ambientMoodSettings
	 */
	public static final void setAmbientMoodSettings(BiomeFog biome_effects, CaveSoundSettings ambientMoodSettings) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "ambientMoodSettings", Optional.of(ambientMoodSettings));
	}

	public static final void setAmbientMoodSettings(BiomeBase biome, CaveSoundSettings ambientMoodSettings) {
		setAmbientMoodSettings(Biome.getBiomeSpecialEffects(biome), ambientMoodSettings);
	}

	/**
	 * 设置群系的额外音效
	 * 
	 * @param biome_effects
	 * @param ambientAdditionsSettings
	 */
	public static final void setAmbientAdditionsSound(BiomeFog biome_effects, CaveSound ambientAdditionsSettings) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "ambientAdditionsSettings", Optional.of(ambientAdditionsSettings));
	}

	public static final void setAmbientAdditionsSound(BiomeBase biome, CaveSound ambientAdditionsSettings) {
		setAmbientAdditionsSound(Biome.getBiomeSpecialEffects(biome), ambientAdditionsSettings);
	}

	/**
	 * 设置群系背景音乐
	 * 
	 * @param biome_effects
	 * @param backgroundMusic
	 */
	public static final void setBackgroundMusic(BiomeFog biome_effects, @Nullable Music backgroundMusic) {
		setBiomeSpecialEffectsObjectValue(biome_effects, "backgroundMusic", Optional.ofNullable(backgroundMusic));
	}

	public static final void setBackgroundMusic(BiomeBase biome, @Nullable Music backgroundMusic) {
		setBackgroundMusic(Biome.getBiomeSpecialEffects(biome), backgroundMusic);
	}
}
