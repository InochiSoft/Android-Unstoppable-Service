package com.inochi.unstopable.helper;

public final class Constants {
    public static class Default {
        public static final String APP = "com.inochi.unstopable";
        private static final int REQUEST = 1000;
    }

    public static final class Action {
        public static final String CLOSE_NOTIFY = Default.APP + ".action." + "CLOSE_NOTIFY";
        public static final String SHOW_NOTIFY = Default.APP + ".action." + "SHOW_NOTIFY";
        public static final String CREATE_DAILY = Default.APP + ".action." + "CREATE_DAILY";
        public static final String START_SERVICE = Default.APP + ".action." + "START_SERVICE";
    }

    public static final class Setting {
        public static final String NOTIF_ITEM = Default.APP + ".setting." + "NOTIF_ITEM";
    }

    public static final class Permission {
        public static final class Type {
            public static final int BOOT = Default.REQUEST + 1;
            public static final int WAKE_LOCK = Default.REQUEST + 2;
        }
    }

}
