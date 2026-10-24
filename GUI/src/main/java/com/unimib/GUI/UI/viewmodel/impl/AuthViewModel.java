package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.model.repository.AuthRepository;
import com.unimib.GUI.utils.UserSession;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AuthViewModel extends BaseViewModel {

    private final AuthRepository repository;

    private final ObjectProperty<UIState<WorkerDTO>> userState =
            new SimpleObjectProperty<>();

    public AuthViewModel(UserSession userSession) {
        repository = new AuthRepository(userSession);
    }

    public void login(String email) {
        execute(
                () -> repository.login(email),
                userState
        );
    }

    public void signup(String name, String surname, String encodeImage) {
        execute(
                repository.signup(
                        new WorkerDTO(
                                null,
                                name,
                                surname,
                                null,
                                encodeImage,
                                null
                        )
                ),
                userState
        );
    }

    public ReadOnlyObjectProperty<UIState<WorkerDTO>> getUserState() {
        return userState;
    }
}