package com.unimib.GUI.UI.viewmodel.impl;

import com.unimib.GUI.UI.state.UIState;
import com.unimib.GUI.UI.viewmodel.BaseViewModel;
import com.unimib.GUI.model.dto.WorkerDTO;
import com.unimib.GUI.UI.repository.AuthRepository;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import static com.unimib.GUI.UI.view.utils.StringHelper.hashString;

public class AuthViewModel extends BaseViewModel {

    private final AuthRepository repository;

    private final ObjectProperty<UIState<Long>> loginState =
            new SimpleObjectProperty<>();

    private final ObjectProperty<UIState<WorkerDTO>> registrationState =
            new SimpleObjectProperty<>();

    public AuthViewModel() {
        repository = new AuthRepository();
    }

    public void login(String email) {
        execute(
                () -> repository.login(hashString(email)),
                loginState
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
                                encodeImage
                        )
                ),
                registrationState
        );
    }

    public ReadOnlyObjectProperty<UIState<Long>> getLoginState() {
        return loginState;
    }

    public ReadOnlyObjectProperty<UIState<WorkerDTO>> getRegistrationState() {
        return registrationState;
    }
}