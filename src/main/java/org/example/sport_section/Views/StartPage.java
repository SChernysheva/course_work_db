package org.example.sport_section.Views;

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
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.User;
import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Utils.SecurityUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route("")
public class StartPage extends VerticalLayout implements BeforeEnterObserver {
    private final Div loadingSpinner = createLoadingSpinner();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public StartPage() {
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
        if (SecurityUtils.getCurrentUser() != null) {
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
        // получить пользователя
        CompletableFuture.supplyAsync(() -> getUser(email), executor)
                .thenAcceptAsync(user -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        remove(loadingSpinner);
                        if (user != null) {
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
        CompletableFuture.supplyAsync(() -> getHashPassword(email), executor)
                .thenAcceptAsync(expectedPassword -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        if (checkPasswords(hashPasswordUser, expectedPassword)) {
                            // Аутентификация успешна
                            Notification.show("Успешный вход для " + email);
                            User user = new User();
                            user.setEmail(email);
                            //VaadinSession.getCurrent().setAttribute(User.class, user);
                            toStartPage();
                        } else {
                            Notification.show("Неверный пароль!", 3000, Notification.Position.MIDDLE);
                        }
                    }));
                });
    }

    public String getHashPassword(String email) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://localhost:8080/api/authorize/getHashPassword?email=" + email, String.class);
    }

    private boolean checkPasswords(String passwordProvided, String passwordExpected) {
        return BCrypt.checkpw(passwordProvided, passwordExpected);
    }

    private void register() {
        UI.getCurrent().navigate(RegisterPage.class);
    }

    private void toStartPage() {
        UI.getCurrent().navigate(HomePage.class);
    }

    private UserModelAuthorization getUser(String email) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://localhost:8080/api/authorize/getUser?email=" + email, UserModelAuthorization.class);
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

