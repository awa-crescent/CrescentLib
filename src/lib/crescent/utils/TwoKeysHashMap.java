package lib.crescent.utils;

import java.util.HashMap;

public class TwoKeysHashMap<K1, K2, V> extends HashMap<KeyTuple<K1, K2>, V> {
	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 9215470728600536712L;

	public V get(K1 key_1, K2 key_2) {
		return get(new KeyTuple<K1, K2>(key_1, key_2));
	}

	public V put(K1 key_1, K2 key_2, V value) {
		return put(new KeyTuple<K1, K2>(key_1, key_2), value);
	}

	public boolean containsKey(K1 key_1, K2 key_2) {
		return containsKey(new KeyTuple<K1, K2>(key_1, key_2));
	}

	public V removeKeys(K1 key_1, K2 key_2) {
		return remove(new KeyTuple<K1, K2>(key_1, key_2));
	}
}