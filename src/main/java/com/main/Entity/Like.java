package com.main.Entity;

public class Like {
    private Long id;
    private Long articleId;
    private Long userId;

    // Constructeur par défaut
    public Like() {}

    // Constructeur avec articleId et userId (pour l'ajout)
    public Like(Long articleId, Long userId) {
        this.articleId = articleId;
        this.userId = userId;
    }

    // Constructeur complet
    public Like(Long id, Long articleId, Long userId) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
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

    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", userId=" + userId +
                '}';
    }
}
