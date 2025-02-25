package com.main.Entity;

public enum Reactions {

        NON(0,"Like","#606266","/images/ic_like_outline.png"),
        LIKE(1,"Like","#056BE1","/images/like.png"),
        LOVE(2,"Love","#E12C4A","/images/ic_love_.png"),
        CARE(3,"Care","#EAA823","/images/ic_care.png"),
        HAHA(4,"Haha","#EAA823","/images/ic_haha.png"),
        WOW(5,"Wow","#EAA823","/images/ic_wow.png"),
        SAD(6,"Sad","#EAA823","/images/ic_sad.png"),
        ANGRY(7,"Angry","#DD6B0E","/img/ic_angry.png");

        private int id;
        private String name;
        private String color;
        private String imgSrc;

        Reactions(int id, String name, String color, String imgSrc) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.imgSrc = imgSrc;
        }


        public static Reactions fromId(int id) {
            for (Reactions reaction : values()) {
                if (reaction.getId() == id) {
                    return reaction;
                }
            }
            throw new IllegalArgumentException("No reaction found for id: " + id);
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getImgSrc() {
            return imgSrc;
        }

        public void setImgSrc(String imgSrc) {
            this.imgSrc = imgSrc;
        }

    }
