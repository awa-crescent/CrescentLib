package lib.crescent.utils;

import java.util.Objects;

public class KeyTuple<K1, K2> {
	K1 key_1;
	K2 key_2;

	public KeyTuple(K1 key_1, K2 key_2) {
		this.key_1 = key_1;
		this.key_2 = key_2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		return (obj instanceof KeyTuple key_tuple) && this.key_1.equals(key_tuple.key_1) && this.key_2.equals(key_tuple.key_2);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(key_1) ^ Objects.hashCode(key_2);
	}
}