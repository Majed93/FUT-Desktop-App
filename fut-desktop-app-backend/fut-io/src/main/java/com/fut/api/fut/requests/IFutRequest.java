package com.fut.api.fut.requests;

import com.fut.desktop.app.exceptions.FutException;

import java.util.concurrent.Future;

public interface IFutRequest<T> {

    Future<T> PerformRequestAsync() throws FutException;
}
