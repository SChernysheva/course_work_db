package org.example.sport_section.Views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.UserModelAuthorization;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route("register")
public class RegisterPage extends VerticalLayout {
    private final Div loadingSpinner = createLoadingSpinner();
    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public RegisterPage() {
        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        Button registerButton = new Button("Register", e -> register(usernameField.getValue(), passwordField.getValue()));
        add(usernameField, passwordField, registerButton);
    }

    private void register(String email, String password) {
        add(loadingSpinner);
        //получить пользователя
        CompletableFuture.supplyAsync(() -> getUserWithEmail(email), executor)
                .thenAcceptAsync(user -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        remove(loadingSpinner);
                        if (user == null) {
                            //захешировать текущий
                            addUser(email, password);
                            Notification.show("Успешно!", 3000, Notification.Position.MIDDLE);
                            toStartPage();
                        } else {
                            // Неправильный email или пароль
                            Notification.show("Пользователь с такой почтой уже существует", 3000, Notification.Position.MIDDLE);
                        }
                    }));
                });
    }
    private UserModelAuthorization getUserWithEmail(String email) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("http://localhost:8080/api/authorize/getUser" + "?email=" +  email, UserModelAuthorization.class);
    }
    private void toStartPage() {
        UI.getCurrent().navigate(StartPage.class);
    }
    private void addUser(String email, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/authorize/addUser" + "?email=" + email + "&password=" + password;
        restTemplate.postForObject(url, null, Void.class);
    }
    private Div createLoadingSpinner() {
        Div spinner = new Div();
        spinner.add(new Span("Loading..."));
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
