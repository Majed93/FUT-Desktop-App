package com.fut.desktop.app.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collection;

@Getter
@Setter
@JsonFormat
@AllArgsConstructor//(suppressConstructorProperties = true)
@NoArgsConstructor
public class UserAccountInfo implements Serializable {

    private static final long serialVersionUID = -5764934828793941435L;

    public Collection<Persona> personas;
}
