package com.unimib.GUI.UI.state;

public class UIState<T> {

    private final boolean loading;
    private final T data;
    private final String error;

    public UIState(boolean loading, T data, String error) {
        this.loading = loading;
        this.data = data;
        this.error = error;
    }

    public static <T> UIState<T> loading() {
        return new UIState<>(true, null, null);
    }

    public static <T> UIState<T> success(T data) {
        return new UIState<>(false, data, null);
    }

    public static <T> UIState<T> error(String message) {
        return new UIState<>(false, null, message);
    }

    public boolean isLoading() {
        return loading;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}