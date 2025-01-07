package lib.crescent.nms;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class Version {
	public static final Version this_version;

	public final String nms_version;// NMS版本，即craftbukkit的包名中的版本，例如1_21_R0
	public final int nms_major;
	public final int nms_minor;
	public final int nms_revision;

	static {
		this_version = Version.fromBukkitVersion(Bukkit.getBukkitVersion());
	}

	public Version(String nms_version) {
		this.nms_version = nms_version;
		String[] v = nms_version.split("_");
		nms_major = Integer.parseInt(v[0]);
		nms_minor = Integer.parseInt(v[1]);
		nms_revision = Integer.parseInt(v[2].replace("R", ""));
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
	public String toString() {
		return nms_version;
	}
}
