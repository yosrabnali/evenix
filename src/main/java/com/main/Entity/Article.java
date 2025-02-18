package com.main.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Article {
    private Long id;
    private Long userId;
    private String titre;
    private String contenu;
    private String auteur;
    private LocalDateTime createdAt;
    private String image;
    private int likes;
    private List<Commentaire> commentaires = new ArrayList<>();

    // Constructeur par défaut
    public Article() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructeur pour l'ajout
    public Article(long userId, String titre, String contenu, String auteur) {
        this.userId = userId;
        this.titre = titre;
        this.contenu = contenu;
        this.auteur = auteur;
    }

    // Constructeur pour la modification
    public Article(long id, String titre, String contenu) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
    }

    // Constructeur pour la suppression
    public Article(long id) {
        this.id = id;
    }

    // Constructeur complet
    public Article(long id, long userId, String titre, String contenu, String auteur, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.titre = titre;
        this.contenu = contenu;
        this.auteur = auteur;
        this.createdAt = createdAt;
    }

    // Ajouter ce constructeur
    public Article(String auteur, String titre, String contenu, Long userId, String image) {
        this.auteur = auteur;
        this.titre = titre;
        this.contenu = contenu;
        this.userId = userId;
        this.image = image;
        this.createdAt = LocalDateTime.now();
        this.commentaires = new ArrayList<>();
        this.likes = 0;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void addCommentaire(Commentaire commentaire) {
        if (commentaires == null) commentaires = new ArrayList<>();
        commentaires.add(commentaire);
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", userId=" + userId +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", auteur='" + auteur + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
