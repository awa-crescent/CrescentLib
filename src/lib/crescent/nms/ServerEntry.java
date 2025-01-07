package lib.crescent.nms;

import java.util.function.UnaryOperator;

import org.bukkit.Bukkit;

import lib.crescent.Manipulator;
import lib.crescent.utils.format.FormattingStyle;
import lib.crescent.utils.format.FormattingStyle.FormattingType;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;

public abstract class ServerEntry {
	public static final MinecraftServer server;
	public static final LayeredRegistryAccess<RegistryLayer> registries;
	public static final IRegistryCustom.Dimension registryAccess;// net.minecraft.core.RegistryAccess$Frozen

	static {
		server = (MinecraftServer) Manipulator.invoke(Bukkit.getServer(), "getServer", null);// 不论服务器版本多少均可获取到MinecraftServer对象
		registries = server.registries();
		registryAccess = server.registryAccess();
	}

	/**
	 * 创建NMS的文本组件，例如附魔描述
	 * 
	 * @param str   要添加颜色和样式的文本
	 * @param style 颜色和样式
	 * @return 应用了颜色和样式的IChatBaseComponent组件
	 */
	@SuppressWarnings("deprecation")
	public static final IChatBaseComponent getComponent(String str, FormattingStyle style) {
		switch (style.formatting_type) {
		case FormattingType.PREFIX:
			return IChatBaseComponent.literal(style.formatStringPrefix(str));
		case FormattingType.JSON:
			String json = style.formatStringJSON(str);
			if (json == null)
				return null;
			try {
				return IChatBaseComponent.ChatSerializer.fromJson(json, MinecraftServer.getDefaultRegistryAccess());
			} catch (RuntimeException ex) {
				return null;
			}
		}
		return null;
	}

	/**
	 * 注册数据组件，代码源自net.minecraft.world.item.enchantment.EnchantmentEffectComponents.register()
	 * 
	 * @param <T>
	 * @param name          数据组件名称
	 * @param register_type 注册类型，通过BuiltInRegistries获取
	 * @param unaryoperator 数据组件定义
	 * @return 返回注册好的数据组件
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T> DataComponentType<T> registerDataComponentType(String name, IRegistry<DataComponentType> register_type, UnaryOperator<DataComponentType.a<T>> unaryoperator) {
		return (DataComponentType) IRegistry.register(register_type, name, ((DataComponentType.a) unaryoperator.apply(DataComponentType.builder())).build());
	}
}
