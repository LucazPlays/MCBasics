package dev.luca.mcbasics.api;

public final class Permission {

    private Permission() {}

    // Vanish permissions
    public static final String VANISH = "essentials.vanish";
    public static final String VANISH_SEE = "essentials.vanish.see";

    // Gamemode permissions
    public static final String GM = "essentials.gm";
    public static final String GM_OTHERS = "essentials.gm.others";
    public static final String GMC = "essentials.gmc";
    public static final String GMS = "essentials.gms";
    public static final String GMA = "essentials.gma";
    public static final String GMSP = "essentials.gmsp";

    // Other command permissions
    public static final String INVSEE = "essentials.invsee";
    public static final String TPHERE = "essentials.tphere";
    public static final String FEED = "essentials.feed";
    public static final String FEED_OTHERS = "essentials.feed.others";
    public static final String HEAL = "essentials.heal";
    public static final String HEAL_OTHERS = "essentials.heal.others";
    public static final String SPEED = "essentials.speed";
    public static final String SPEED_OTHERS = "essentials.speed.others";
    public static final String FLY = "essentials.fly";
    public static final String FLY_OTHERS = "essentials.fly.others";
    public static final String UNSAFEENCHANT = "essentials.unsafeenchant";

    // Wildcard permission
    public static final String ALL = "essentials.*";
}
