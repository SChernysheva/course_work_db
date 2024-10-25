package org.example.sport_section.front.Views.Authorize;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Utils.SecurityUtils;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.reactive.function.client.WebClient;

@Route("")
public class StartPage extends VerticalLayout implements BeforeEnterObserver {
    private final Div loadingSpinner = createLoadingSpinner();
    private final WebClient webClient;

    @Autowired
    public StartPage(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api").build();
        setClassName("login-layout");
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // создаем поля для ввода и кнопки
        TextField usernameField = new TextField("Email");
        PasswordField passwordField = new PasswordField("Password");
        Button loginButton = new Button("Войти", e -> login(usernameField.getValue(), passwordField.getValue()));
        Button registerButton = new Button("Зарегистрироваться", e -> register());

        // Устанавливаем стили для полей и кнопок
        applyStyles(usernameField, passwordField, loginButton, registerButton);

        // Добавляем элементы на страницу
        add(usernameField, passwordField, loginButton, registerButton);
        UI.getCurrent().setPollInterval(500);
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.getCurrentUserEmail() != null) {
            // Пользователь уже аутентифицирован
            event.forwardTo(HomePage.class);
        }
    }

    private void applyStyles(TextField usernameField, PasswordField passwordField, Button loginButton, Button registerButton) {
        usernameField.addClassName("auth-field");
        passwordField.addClassName("auth-field");
        loginButton.addClassName("auth-button");
        registerButton.addClassName("auth-button");

        // Выравнивание элементов по центру
        usernameField.setWidth("300px");
        passwordField.setWidth("300px");
        loginButton.setWidth("300px");
        registerButton.setWidth("300px");
    }

    private void login(String email, String password) {
        add(loadingSpinner);
        webClient.get().uri("/authorize/getUser?email=" + email).retrieve().bodyToMono(UserModelAuthorization.class)
                .subscribe(user -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                remove(loadingSpinner);
                if (user.getEmail() != null) {
                    // сравнить пароли
                    validatePasswords(password, email);
                } else {
                    // Неправильный email или пароль
                    Notification.show("Неверный email или пароль!", 3000, Notification.Position.MIDDLE);
                }
            }));
        });
    }

    private void validatePasswords(String hashPasswordUser, String email) {
        webClient.get().uri("/authorize/getHashPassword?email=" + email).retrieve().bodyToMono(String.class)
                .subscribe(expectedPassword -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        if (checkPasswords(hashPasswordUser, expectedPassword)) {
                            // Аутентификация успешна
                            Notification.show("Успешный вход для " + email);
                            SecurityUtils.saveUserInCurrentSession(email, hashPasswordUser);
                            VaadinSession.getCurrent().setAttribute("email", email);
                            toHomePage();
                        } else {
                            Notification.show("Неверный пароль!", 3000, Notification.Position.MIDDLE);
                        }
                    }));
                });
    }

    private boolean checkPasswords(String passwordProvided, String passwordExpected) {
        return BCrypt.checkpw(passwordProvided, passwordExpected);
    }

    private void register() {
        UI.getCurrent().navigate(RegisterPage.class);
    }

    private void toHomePage() {
        UI.getCurrent().navigate(HomePage.class);
    }

    private Div createLoadingSpinner() {
        Div spinner = new Div();
        spinner.add(new Span("Загрузка..."));
        spinner.getStyle().set("display", "flex");
        spinner.getStyle().set("width", "100%");
        spinner.getStyle().set("height", "100%");
        spinner.getStyle().set("align-items", "center");
        spinner.getStyle().set("justify-content", "center");
        spinner.getStyle().set("font-size", "24px");
        spinner.getStyle().set("font-weight", "bold");
        return spinner;
    }
}

