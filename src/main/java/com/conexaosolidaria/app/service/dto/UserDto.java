package com.conexaosolidaria.app.service.dto;

import com.conexaosolidaria.app.domain.User;
import java.io.Serializable;

/**
 * A DTO representing a user, with only the public attributes.
 */
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    private String cpf;

    private String cnpj;

    private String tipoUser;

    public UserDto() {
        // Empty constructor needed for Jackson.
    }

    public UserDto(User user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
        this.cpf = user.getCpf();
        this.cnpj = user.getCnpj();
        this.tipoUser = user.getTipoUser();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTipoUser() {
        return tipoUser;
    }

    public void setTipoUser(String tipoUser) {
        this.tipoUser = tipoUser;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserDto{" +
            "id='" + id + '\'' +
            ", login='" + login + '\'' +
            ", cpf='" + cpf + '\'' +
            ", cnpj='" + cnpj + '\'' +
            ", tipoUser='" + tipoUser + '\'' +
            "}";
    }
}
