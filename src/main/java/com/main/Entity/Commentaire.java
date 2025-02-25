package com.main.Entity;

import java.sql.Date;


public class Commentaire {

    private Long id;
    private Long articleId;
    private Long userId;
    private String contenu;
    private Date createdAt;
    private String auteur;

    // Constructeur par défaut
    public Commentaire() {

    }

    // Constructeur avec paramètres pour l'ajout sans date
    public Commentaire(Long articleId, Long userId, String contenu ) {
        this.articleId = articleId;
        this.userId = userId;
        this.contenu = contenu;
    }

    // Constructeur avec l'ID et le contenu seulement
    public Commentaire(long id, String contenu) {
        this.id = id;
        this.contenu = contenu;
    }

    // Constructeur avec l'ID seulement
    public Commentaire(long id) {
        this.id = id;
    }

    // Constructeur avec tous les paramètres
    public Commentaire(Long articleId, String contenu, String auteur, long userId) {
        this.articleId = articleId;
        this.contenu = contenu;
        this.auteur = auteur;
        this.userId = userId;
       // this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Commentaire{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", userId=" + userId +
                ", contenu='" + contenu + '\'' +
                ", datePublication=" + createdAt +
                '}';
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }
}
