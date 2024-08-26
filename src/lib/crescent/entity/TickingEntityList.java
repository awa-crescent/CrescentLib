package lib.crescent.entity;

import java.util.Collection;
import java.util.HashMap;

public abstract class TickingEntityList {
	public static HashMap<String, Collection<?>> entities;

	static {
		entities = new HashMap<>();
	}
}
