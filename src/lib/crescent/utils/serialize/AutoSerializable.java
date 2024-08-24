package lib.crescent.utils.serialize;

public interface AutoSerializable {
	/**
	 * 对象被拉取时需要执行的操作
	 */
	default public void onPull() {

	}

	default public void onPush() {

	}
}
