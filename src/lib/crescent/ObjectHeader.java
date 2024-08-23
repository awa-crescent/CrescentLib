package lib.crescent;

//https://gist.github.com/arturmkrtchyan/43d6135e8a15798cc46c
public class ObjectHeader {
	// 32位JVM无OOP指针压缩
	public static final int HEADER_OFFSET_32 = 0;
	public static final int HEADER_LENGTH_32 = 64;

	public static final int MARKWORD_OFFSET_32 = HEADER_OFFSET_32;
	public static final int MARKWORD_LENGTH_32 = 32;
	public static final int KLASS_OFFSET_32 = MARKWORD_OFFSET_32 + MARKWORD_LENGTH_32;
	public static final int KLASS_LENGTH_32 = 32;

	public static final int IDENTITY_HASHCODE_OFFSET_32 = MARKWORD_OFFSET_32;
	public static final int IDENTITY_HASHCODE_LENGTH_32 = 25;
	public static final int AGE_OFFSET_32 = IDENTITY_HASHCODE_OFFSET_32 + IDENTITY_HASHCODE_LENGTH_32;
	public static final int AGE_LENGTH_32 = 4;
	public static final int BIASED_LOCK_OFFSET_32 = AGE_OFFSET_32 + AGE_LENGTH_32;
	public static final int BIASED_LOCK_LENGTH_32 = 1;
	public static final int LOCK_OFFSET_32 = BIASED_LOCK_OFFSET_32 + BIASED_LOCK_LENGTH_32;
	public static final int LOCK_LENGTH_32 = 2;

	public static final int THREAD_OFFSET_32 = MARKWORD_OFFSET_32;
	public static final int THREAD_LENGTH_32 = 23;
	public static final int EPOCH_OFFSET_32 = THREAD_OFFSET_32 + THREAD_LENGTH_32;
	public static final int EPOCH_LENGTH_32 = 2;

	public static final int PTR_TO_LOCK_RECORD_OFFSET_32 = MARKWORD_OFFSET_32;
	public static final int PTR_TO_LOCK_RECORD_LENGTH_32 = 30;

	public static final int PTR_TO_HEAVYWEIGHT_MONITOR_OFFSET_32 = MARKWORD_OFFSET_32;
	public static final int PTR_TO_HEAVYWEIGHT_MONITOR_LENGTH_32 = 30;

	// 64位JVM无OOP指针压缩
	public static final int HEADER_OFFSET_64 = 0;
	public static final int HEADER_LENGTH_64 = 128;

	public static final int MARKWORD_OFFSET_64 = HEADER_OFFSET_64;
	public static final int MARKWORD_LENGTH_64 = 64;
	public static final int KLASS_OFFSET_64 = MARKWORD_OFFSET_64 + MARKWORD_LENGTH_64;
	public static final int KLASS_LENGTH_64 = 64;

	public static final int UNUSED_1_NORMAL_OFFSET_64 = MARKWORD_OFFSET_64;
	public static final int UNUSED_1_NORMAL_LENGTH_64 = 25;
	public static final int IDENTITY_HASHCODE_OFFSET_64 = UNUSED_1_NORMAL_OFFSET_64 + UNUSED_1_NORMAL_LENGTH_64;
	public static final int IDENTITY_HASHCODE_LENGTH_64 = 31;
	public static final int UNUSED_2_NORMAL_OFFSET_64 = IDENTITY_HASHCODE_OFFSET_64 + IDENTITY_HASHCODE_LENGTH_64;
	public static final int UNUSED_2_NORMAL_LENGTH_64 = 1;
	public static final int AGE_OFFSET_64 = UNUSED_2_NORMAL_OFFSET_64 + UNUSED_2_NORMAL_LENGTH_64;
	public static final int AGE_LENGTH_64 = 4;
	public static final int BIASED_LOCK_OFFSET_64 = AGE_OFFSET_64 + AGE_LENGTH_64;
	public static final int BIASED_LOCK_LENGTH_64 = 1;
	public static final int LOCK_OFFSET_64 = BIASED_LOCK_OFFSET_64 + BIASED_LOCK_LENGTH_64;
	public static final int LOCK_LENGTH_64 = 2;

	public static final int THREAD_OFFSET_64 = MARKWORD_OFFSET_64;
	public static final int THREAD_LENGTH_64 = 54;
	public static final int EPOCH_OFFSET_64 = THREAD_OFFSET_64 + THREAD_LENGTH_64;
	public static final int EPOCH_LENGTH_64 = 2;
	public static final int UNUSED_1_BIASED_OFFSET_64 = EPOCH_OFFSET_64 + EPOCH_LENGTH_64;
	public static final int UNUSED_1_BIASED_LENGTH_64 = 1;

	public static final int PTR_TO_LOCK_RECORD_OFFSET_64 = MARKWORD_OFFSET_64;
	public static final int PTR_TO_LOCK_RECORD_LENGTH_64 = 62;

	public static final int PTR_TO_HEAVYWEIGHT_MONITOR_OFFSET_64 = MARKWORD_OFFSET_64;
	public static final int PTR_TO_HEAVYWEIGHT_MONITOR_LENGTH_64 = 62;

	// 64位JVM开启OOP指针压缩，JVM默认是开启的
	public static final int HEADER_OFFSET_64C = 0;
	public static final int HEADER_LENGTH_64C = 96;

	public static final int MARKWORD_OFFSET_64C = HEADER_OFFSET_64C;
	public static final int MARKWORD_LENGTH_64C = 64;
	public static final int KLASS_OFFSET_64C = MARKWORD_OFFSET_64C + MARKWORD_LENGTH_64C;
	public static final int KLASS_LENGTH_64C = 32;

	public static final int UNUSED_1_NORMAL_OFFSET_64C = MARKWORD_OFFSET_64C;
	public static final int UNUSED_1_NORMAL_LENGTH_64C = 25;
	public static final int IDENTITY_HASHCODE_OFFSET_64C = UNUSED_1_NORMAL_OFFSET_64C + UNUSED_1_NORMAL_LENGTH_64C;
	public static final int IDENTITY_HASHCODE_LENGTH_64C = 31;
	public static final int CMS_FREE_OFFSET_64C = IDENTITY_HASHCODE_OFFSET_64C + IDENTITY_HASHCODE_LENGTH_64C;
	public static final int CMS_FREE_LENGTH_64C = 1;
	public static final int AGE_OFFSET_64C = CMS_FREE_OFFSET_64C + CMS_FREE_LENGTH_64C;
	public static final int AGE_LENGTH_64C = 4;
	public static final int BIASED_LOCK_OFFSET_64C = AGE_OFFSET_64C + AGE_LENGTH_64C;
	public static final int BIASED_LOCK_LENGTH_64C = 1;
	public static final int LOCK_OFFSET_64C = BIASED_LOCK_OFFSET_64C + BIASED_LOCK_LENGTH_64C;
	public static final int LOCK_LENGTH_64C = 2;

	public static final int THREAD_OFFSET_64C = MARKWORD_OFFSET_64C;
	public static final int THREAD_LENGTH_64C = 54;
	public static final int EPOCH_OFFSET_64C = THREAD_OFFSET_64C + THREAD_LENGTH_64C;
	public static final int EPOCH_LENGTH_64C = 2;

	public static final int PTR_TO_LOCK_RECORD_OFFSET_64C = MARKWORD_OFFSET_64C;
	public static final int PTR_TO_LOCK_RECORD_LENGTH_64C = 62;

	public static final int PTR_TO_HEAVYWEIGHT_MONITOR_OFFSET_64C = MARKWORD_OFFSET_64C;
	public static final int PTR_TO_HEAVYWEIGHT_MONITOR_LENGTH_64C = 62;


}
