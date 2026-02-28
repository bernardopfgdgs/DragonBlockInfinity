package com.bernardo.dbi.form;

public enum Form {
    Base(BaseForm.ID);

    private final String id;

    Form(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}