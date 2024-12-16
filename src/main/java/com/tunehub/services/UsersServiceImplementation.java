package com.tunehub.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tunehub.entities.Users;
import com.tunehub.repositories.UsersRepository;

@Service
public class UsersServiceImplementation implements UsersService {

    @Autowired
    UsersRepository repo;

    // In-memory storage for reset tokens
    private Map<String, String> resetTokens = new HashMap<>();

    @Override
    public void addUser(Users user) {
        repo.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return repo.findByEmail(email) != null;
    }

    @Override
    public boolean validateUser(String email, String password) {
        Users user = repo.findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }

    @Override
    public String getRole(String email) {
        Users user = repo.findByEmail(email);
        return user != null ? user.getRole() : null;
    }

    @Override
    public Users getUser(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public void updateUser(Users user) {
        repo.save(user);
    }

    @Override
    public Users getUserByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(Users user, String token) {
        resetTokens.put(token, user.getEmail());
    }

    @Override
    public Users getUserByPasswordResetToken(String token) {
        String email = resetTokens.get(token);
        return getUserByEmail(email);
    }

    @Override
    public void updatePassword(Users user, String password) {
        user.setPassword(password);
        repo.save(user);
    }
}