package org.makumba.parade.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {

    private long id;

    private String login;

    private String name;

    private String surname;

    private String nickname;

    private String email;

    private byte[] jpegPhoto;

    private Parade parade;

    private String cvsuser;

    private User mentor;

    @Column
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Column
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Column
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Id
    @GeneratedValue
    @Column(name = "user")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User() {

    }

    public User(String login, String name, String surname, String nickname, String email) {

        this.login = login;

        String loginName = "", loginSurname = "";
        if (login.indexOf(".") > -1) {
            loginName = login.substring(0, login.indexOf("."));
            loginSurname = login.substring(login.indexOf(".") + 1, login.length());
        }

        this.name = name;

        if (this.name == null || this.name.length() == 0) {
            this.name = loginName.substring(0, 1).toUpperCase() + loginName.substring(1);
        }

        this.surname = surname;

        if (this.surname == null || this.surname.length() == 0) {
            this.surname = loginSurname.substring(0, 1).toUpperCase() + loginSurname.substring(1);
        }

        this.nickname = nickname;

        if (this.nickname == null || this.nickname.length() == 0) {
            this.nickname = loginName;
        }

        this.email = email;
    }

    public static User getUnknownUser() {
        return new User("unknown", "unknown", "unknown", "unknown", "unknown@unknown.com");
    }

    @Column
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @ManyToOne
    @JoinColumn(name = "id_parade")
    public Parade getParade() {
        return parade;
    }

    public void setParade(Parade parade) {
        this.parade = parade;
    }

    @Column(columnDefinition = "longtext")
    public byte[] getJpegPhoto() {
        return jpegPhoto;
    }

    public void setJpegPhoto(byte[] jpegPhoto) {
        this.jpegPhoto = jpegPhoto;
    }

    @Column
    public String getCvsuser() {
        return cvsuser;
    }

    public void setCvsuser(String cvsuser) {
        this.cvsuser = cvsuser;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    @ManyToOne
    public User getMentor() {
        return mentor;
    }

}
