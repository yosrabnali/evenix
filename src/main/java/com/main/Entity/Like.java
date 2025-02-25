package com.main.Entity;

import java.util.Date;

public class Like {
    private Long id;
    private Long articleId;
    private Long userId;
    private long Reactiontype;
   /* private String createdAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }*/


    public long getReactiontype() {
        return Reactiontype;
    }

    public void setReactiontype(long reactiontype) {
        Reactiontype = reactiontype;
    }



    // Constructeur par défaut
    public Like() {}

    // Constructeur avec articleId et userId (pour l'ajout)
    public Like(Long articleId, Long userId ,long Reactiontype) {
        this.articleId = articleId;
        this.userId = userId;
        this.Reactiontype = Reactiontype;
    }

    // Constructeur complet
    public Like(Long id, Long articleId, Long userId,long Reactiontype) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.Reactiontype = Reactiontype;
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
                ", Reactiontype=" + Reactiontype +
                "}";

    }
}
