package org.eyeseetea.malariacare.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.HashMap;

/**
 * Created by nacho on 23/11/16.
 */

public class Permissions {

    public static final int FINE_LOCATION_REQUEST_CODE = 8;

    public static final int PHONE_STATE_REQUEST_CODE = 9;
    private static Permissions permissionsInstance;
    private static Activity activity;
    HashMap<Integer, Permission> permissions;

    public static Permissions getInstance(Activity callingActivity) {
        if (permissionsInstance == null) {
            activity = callingActivity;
            permissionsInstance = new Permissions();
            permissionsInstance.init();
        }
        return permissionsInstance;
    }

    /**
     * Remove from permissions Map the granted permission
     *
     * @return true on permission granted, false otherwise
     */
    public static boolean processAnswer(int requestCode,
            String permissions[], int[] grantResults) {
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionsInstance.removePermission(requestCode);
            return true;
        }
        return false;
    }

    public void init() {
        permissions = new HashMap<>();
        addPermission(new Permission(FINE_LOCATION_REQUEST_CODE,
                android.Manifest.permission.ACCESS_FINE_LOCATION));
        addPermission(new Permission(PHONE_STATE_REQUEST_CODE,
                android.Manifest.permission.READ_PHONE_STATE));
    }

    public void addPermission(Permission permission) {
        permissions.put(permission.getCode(), permission);
    }

    public void removePermission(int code) {
        permissions.remove(code);
    }

    public Permission getPermission(int code) {
        return permissions.get(code);
    }

    public void requestNextPermission() {
        if (permissions.size() > 0) {
            Integer code = (Integer) permissions.keySet().toArray()[0];
            Permission permission = getPermission(code);
            requestPermission(permission.getDefinition(), permission.getCode());
        }
    }

    public void requestPermission(String permission, int code) {
        ActivityCompat.requestPermissions(activity, new String[]{permission}, code);
    }

    public boolean areAllPermissionsGranted() {
        return permissions.isEmpty();
    }

    private class Permission {
        private int code;
        private String definition;

        public Permission(int code, String permission) {
            this.code = code;
            this.definition = permission;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDefinition() {
            return definition;
        }

        public void setDefinition(String definition) {
            this.definition = definition;
        }
    }
}
