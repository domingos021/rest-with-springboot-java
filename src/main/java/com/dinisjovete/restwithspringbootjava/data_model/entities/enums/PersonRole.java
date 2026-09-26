package com.dinisjovete.restwithspringbootjava.data_model.entities.enums;

// ============================================================================
// ENUM - USER ROLES (AUTHORIZATION LEVELS)
// ============================================================================
// Defines the authority levels available in the system.
// Used by Spring Security to enforce Role-Based Access Control (RBAC).
// ============================================================================

public enum PersonRole {

    /*
     * Full access: Can manage products, users, orders, and system settings.
     */
    ADMIN("ADMIN"),

    /*
     * Restricted access: Can view public products, manage their own profile,
     * and place orders.
     */
    CLIENT("CLIENT");

    private final String role;

    /*
     * Constructor mapping the role string representation.
     */
    PersonRole(String role) {
        this.role = role;
    }

    /*
     * Returns the string representation of the role.
     */
    public String getRole() {
        return role;
    }
}