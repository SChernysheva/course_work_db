package org.example.sport_section.front.Views.Authorize;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("loginPage")
public class StartPage extends VerticalLayout{
    private final Div loadingSpinner = createLoadingSpinner();
    @Autowired
    private AuthenticationManager authenticationManager;


    public StartPage() {
        System.out.println("constructor");
        setClassName("login");
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
        if (authenticate(email, password)) {
            Notification.show("Успешный вход для " + email);
            toHomePage();
        } else {
            Notification.show("Неверный email или пароль!", 3000, Notification.Position.MIDDLE);
        }
    }
    private boolean authenticate(String username, String password) {
        try {
            Authentication token = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            return true;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return false;
        }
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

