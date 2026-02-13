package dev.luca.mcbasics.api;

public final class Permission {

    private Permission() {}

    // Vanish permissions
    public static final String VANISH = "mcbasics.vanish";
    public static final String VANISH_SEE = "mcbasics.vanish.see";

    // Gamemode permissions
    public static final String GM = "mcbasics.gm";
    public static final String GM_OTHERS = "mcbasics.gm.others";
    public static final String GMC = "mcbasics.gmc";
    public static final String GMS = "mcbasics.gms";
    public static final String GMA = "mcbasics.gma";
    public static final String GMSP = "mcbasics.gmsp";

    // Other command permissions
    public static final String INVSEE = "mcbasics.invsee";
    public static final String TPHERE = "mcbasics.tphere";
    public static final String FEED = "mcbasics.feed";
    public static final String FEED_OTHERS = "mcbasics.feed.others";
    public static final String HEAL = "mcbasics.heal";
    public static final String HEAL_OTHERS = "mcbasics.heal.others";
    public static final String SPEED = "mcbasics.speed";
    public static final String SPEED_OTHERS = "mcbasics.speed.others";
    public static final String FLY = "mcbasics.fly";
    public static final String FLY_OTHERS = "mcbasics.fly.others";
    public static final String UNSAFEENCHANT = "mcbasics.unsafeenchant";
    public static final String ENCHANT = "mcbasics.enchant";
    public static final String ENDERCHEST = "mcbasics.enderchest";
    public static final String ENDERCHEST_OTHERS = "mcbasics.enderchest.others";
    public static final String SETDISPLAYNAME = "mcbasics.setdisplayname";
    public static final String SETLORE = "mcbasics.setlore";
    public static final String SUDO = "mcbasics.sudo";
    public static final String GOD = "mcbasics.god";
    public static final String GOD_OTHERS = "mcbasics.god.others";
    public static final String PING = "mcbasics.ping";
    public static final String PING_OTHERS = "mcbasics.ping.others";
    public static final String HELP = "mcbasics.help";
    public static final String ITEM = "mcbasics.item";

    // Wildcard permission
    public static final String ALL = "mcbasics.*";
}
