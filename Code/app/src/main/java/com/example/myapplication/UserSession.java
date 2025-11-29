package com.example.myapplication;

public class UserSession {
    // default true for student mode; set to false for admin/organizer after login
    private static boolean student = true;

    public static boolean isStudent() {
        return student;
    }

    public static void setStudent(boolean isStudent) {
        student = isStudent;
    }
}
