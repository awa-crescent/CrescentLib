package lib.crescent.nms;

import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;

public class Version {
	public static final Version this_version;
	private static final HashMap<String, Integer> ver_revision_num = new HashMap<>();

	public final String nms_version;// NMS版本，即craftbukkit的包名中的版本，例如1_21_R0
	public final int nms_major;
	public final int nms_minor;
	public final int nms_revision;

	static {
		// 主版本号数量
		ver_revision_num.put("major", 1);
		// 主版本号对应的此版本号数量
		ver_revision_num.put("1", 21);
		// 次版本号对应的修订版本号数量，不包含R0，R0不是稳定发行版
		ver_revision_num.put("1.8", 3);
		ver_revision_num.put("1.9", 2);
		ver_revision_num.put("1.10", 1);
		ver_revision_num.put("1.11", 1);
		ver_revision_num.put("1.12", 1);
		ver_revision_num.put("1.13", 2);
		ver_revision_num.put("1.14", 1);
		ver_revision_num.put("1.15", 1);
		ver_revision_num.put("1.16", 3);
		ver_revision_num.put("1.17", 1);
		ver_revision_num.put("1.18", 2);
		ver_revision_num.put("1.19", 3);
		ver_revision_num.put("1.20", 3);
		ver_revision_num.put("1.21", 3);// 1.21版本有3个稳定NMS版本和R0，供4个NMS版本
		this_version = Version.fromBukkitVersion(Bukkit.getBukkitVersion());
	}

	public Version(String nms_version) {
		this.nms_version = nms_version;
		String[] v = nms_version.split("_");
		nms_major = Integer.parseInt(v[0]);
		nms_minor = Integer.parseInt(v[1]);
		nms_revision = Integer.parseInt(v[2].replace("R", ""));
	}

	public Version(int nms_major, int nms_minor, int nms_revision) {
		this.nms_major = nms_major;
		this.nms_minor = nms_minor;
		this.nms_revision = nms_revision;
		nms_version = nms_major + "_" + nms_minor + "_R" + nms_revision;
	}

	public boolean newerThan(String nms_version) {
		return this.nms_version.compareToIgnoreCase(nms_version) > 0;
	}

	public boolean newerOrEqual(String nms_version) {
		return newerThan(nms_version) || equal(nms_version);
	}

	public boolean olderThan(String nms_version) {
		return this.nms_version.compareToIgnoreCase(nms_version) < 0;
	}

	public boolean olderOrEqual(String nms_version) {
		return olderThan(nms_version) || equal(nms_version);
	}

	public boolean equal(String nms_version) {
		return this.nms_version.compareToIgnoreCase(nms_version) == 0;
	}

	public boolean newerThan(Version version) {
		return this.nms_version.compareToIgnoreCase(version.nms_version) > 0;
	}

	public boolean newerOrEqual(Version version) {
		return newerThan(version) || equal(version);
	}

	public boolean olderThan(Version version) {
		return this.nms_version.compareToIgnoreCase(version.nms_version) < 0;
	}

	public boolean olderOrEqual(Version version) {
		return olderThan(version) || equal(version);
	}

	public boolean equal(Version version) {
		return this.nms_version.compareToIgnoreCase(version.nms_version) == 0;
	}

	/**
	 * 判断当前版本是否在指定两个版本之间，左闭右开，包含version1，不包含version2
	 * 
	 * @param version1
	 * @param version2
	 * @return
	 */
	public boolean between(String version1, String version2) {
		return newerOrEqual(version1) && olderThan(version2);
	}

	public boolean between(Version version1, Version version2) {
		return newerOrEqual(version1.nms_version) && olderThan(version2.nms_version);
	}

	/**
	 * 上一个版本
	 * 
	 * @return
	 */
	public Version previous() {
		if (nms_revision > 0)
			return new Version(nms_major, nms_minor, nms_revision - 1);
		else {
			int pre_minor = nms_minor - 1;
			if (nms_minor > 0)
				return new Version(nms_major, pre_minor, ver_revision_num.get(nms_major + "." + pre_minor));
			else {
				int pre_major = nms_major - 1;
				pre_minor = ver_revision_num.get(Integer.toString(pre_major));
				if (nms_major > 0)
					return new Version(pre_major, pre_major, ver_revision_num.get(pre_major + "." + pre_minor));
			}
		}
		return null;
	}

	/**
	 * 下一个版本
	 * 
	 * @return
	 */
	public Version next() {
		if (nms_revision < ver_revision_num.get(nms_major + "." + nms_minor))
			return new Version(nms_major, nms_minor, nms_revision + 1);
		else {
			int next_minor = nms_minor + 1;
			if (nms_minor < ver_revision_num.get(Integer.toString(nms_major)))
				return new Version(nms_major, next_minor, 0);
			else {
				int next_major = nms_major + 1;
				if (nms_major > ver_revision_num.get("major"))
					return new Version(next_major, 0, 0);
			}
		}
		return null;
	}

	public static Version fromBukkitVersion(String bukkit_ver) {
		return new Version(bukkitVersionToNMSVersion(bukkit_ver));
	}

	public static String bukkitVersionToNMSVersion(String bukkit_ver) {
		switch (bukkit_ver) {
		case "1.21-R0.1-SNAPSHOT":
			return "1_21_R0";
		case "1.21.1-R0.1-SNAPSHOT":
			return "1_21_R1";
		case "1.21.3-R0.1-SNAPSHOT":
			return "1_21_R2";
		case "1.21.4-R0.1-SNAPSHOT":
			return "1_21_R3";
		default:
			Bukkit.getLogger().log(Level.SEVERE, "Unknown Bukkit version: " + bukkit_ver);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Version v) {
			return (nms_major == v.nms_major) && (nms_major == v.nms_minor) && (nms_major == v.nms_revision);
		}
		return false;
	}

	@Override
	public String toString() {
		return nms_version;
	}
}
