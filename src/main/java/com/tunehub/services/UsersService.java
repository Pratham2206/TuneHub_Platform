package com.tunehub.services;

import com.tunehub.entities.Users;

public interface UsersService {
    void addUser(Users user);

    boolean emailExists(String email);

    boolean validateUser(String email, String password);

    String getRole(String email);

    Users getUser(String email);

    void updateUser(Users user);

    Users getUserByEmail(String email);

    void createPasswordResetTokenForUser(Users user, String token);

    Users getUserByPasswordResetToken(String token);

    void updatePassword(Users user, String password);
}