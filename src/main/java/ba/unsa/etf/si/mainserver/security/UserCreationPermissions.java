package ba.unsa.etf.si.mainserver.security;

import java.util.*;

public class UserCreationPermissions {
    public static Map<String, ArrayList<String>> permissions = new HashMap<String, ArrayList<String>>() {{
        put("ROLE_ADMIN",new ArrayList<>(
                Arrays.asList(
                        "ROLE_ADMIN",
                        "ROLE_MANAGER",
                        "ROLE_MERCHANT",
                        "ROLE_PRW",
                        "ROLE_WAREMAN",
                        "ROLE_CASHIER",
                        "ROLE_BARTENDER")));
        put("ROLE_MERCHANT",new ArrayList<>(
                Arrays.asList(
                        "ROLE_MANAGER",
                        "ROLE_MERCHANT",
                        "ROLE_PRW",
                        "ROLE_WAREMAN",
                        "ROLE_CASHIER",
                        "ROLE_BARTENDER")));
        put("ROLE_MANAGER",new ArrayList<>(
                Arrays.asList(
                        "ROLE_MANAGER",
                        "ROLE_PRW",
                        "ROLE_WAREMAN",
                        "ROLE_CASHIER",
                        "ROLE_BARTENDER")));
        put("ROLE_PRW",new ArrayList<>(
                Collections.emptyList()));
        put("ROLE_WAREMAN",new ArrayList<>(
                Collections.emptyList()));
        put("ROLE_CASHIER",new ArrayList<>(
                Collections.emptyList()));
        put("ROLE_BARTENDER",new ArrayList<>(
                Collections.emptyList()));

    }};
}

//
//    ROLE_ADMIN,
//            ROLE_MANAGER,
//            ROLE_MERCHANT,
//            ROLE_WAREMAN,
//            ROLE_PRW,
//            ROLE_CASHIER,
//            ROLE_BARTENDER
