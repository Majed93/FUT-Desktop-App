package com.fut.desktop.app.parameters;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SearchParameterBase<T> {
    public String description;
    public T value;
    public T parent;
}
