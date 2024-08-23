package lib.crescent.nms;

import java.util.function.UnaryOperator;

import org.bukkit.craftbukkit.v1_21_R1.util.CraftChatMessage;

import lib.crescent.utils.format.FormattingStyle;
import lib.crescent.utils.format.FormattingStyle.FormattingType;
import net.minecraft.core.IRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.IChatBaseComponent;

@Deprecated
public abstract class NMSEntry_v1_21_R1 {

	/**
	 * 创建NMS的文本组件，例如附魔描述
	 * 
	 * @param str   要添加颜色和样式的文本
	 * @param style 颜色和样式
	 * @return 应用了颜色和样式的IChatBaseComponent组件
	 */
	public static IChatBaseComponent getComponent(String str, FormattingStyle style) {
		switch (style.formatting_type) {
		case FormattingType.PREFIX:
			return IChatBaseComponent.literal(style.formatStringPrefix(str));
		case FormattingType.JSON:
			return CraftChatMessage.fromJSON(style.formatStringJSON(str));
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
	public static <T> DataComponentType<T> registerDataComponentType(String name, IRegistry<DataComponentType> register_type, UnaryOperator<DataComponentType.a<T>> unaryoperator) {
		return (DataComponentType) IRegistry.register(register_type, name, ((DataComponentType.a) unaryoperator.apply(DataComponentType.builder())).build());
	}
}
