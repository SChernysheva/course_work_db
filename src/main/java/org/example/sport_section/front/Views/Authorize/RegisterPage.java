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
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.UserModelAuthorization;
import org.example.sport_section.Validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Route("register")
public class RegisterPage extends VerticalLayout {
    private final Div loadingSpinner = createLoadingSpinner();
    private final WebClient webClient;

    @Autowired
    public RegisterPage(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8080/api").build();
        TextField emailField = new TextField("Email");
        TextField name = new TextField("Имя");
        TextField lastName = new TextField("Фамилия");
        TextField phone = new TextField("Номер телефона");
        PasswordField passwordField = new PasswordField("Пароль");
        Button registerButton = new Button("Register", e -> register(lastName.getValue(),
                name.getValue(), emailField.getValue(), passwordField.getValue(), phone.getValue()));
        add(emailField, name, lastName, phone, passwordField, registerButton);
        UI.getCurrent().setPollInterval(100);
    }

    private void register(String lastName, String firstName, String email, String password, String phone) {
        add(loadingSpinner);
        String resValidate = validate(email, phone);
        if (resValidate != null) {
            Notification.show("Ошибка: " + resValidate, 3000, Notification.Position.MIDDLE);
            return;
        }
        //получить пользователя
        webClient.get().uri("/authorize/getUser" + "?email=" + email).retrieve()
                .bodyToMono(Optional.class).subscribe(user -> {
                    getUI().ifPresent(ui -> ui.access(() -> {
                        remove(loadingSpinner);
                        if (user.isEmpty()) {
                            addUser(firstName, lastName, email, password, phone);
                            Notification.show("Успешно!", 3000, Notification.Position.MIDDLE);
                            VaadinSession.getCurrent().close();
                            //toStartPage();
                        } else {
                            // Неправильный email или пароль
                            Notification.show("Пользователь с такой почтой уже существует", 3000, Notification.Position.MIDDLE);
                        }
                    }));
                });
    }

    private String validate(String email, String phone) {
        try {
            UserValidator.validateEmail(email);
            UserValidator.validatePhone(phone);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
        return null;
    }

    private void addUser(String firstName, String lastName, String email, String password, String phone) {
        String path = "/users/addUser";
        String url = String.format(path + "?email=%s&firstName=%s&lastName=%s&phone=%s", email, firstName, lastName, phone);
        System.out.println(url);
        webClient.post().uri(url).retrieve()
                .bodyToMono(Integer.class).subscribe(userId -> {
                    System.out.println("addUserAsyncId" + userId);
                    addUserAuthorize(email, password, userId);
                });
    }

    private void toStartPage() {
        UI.getCurrent().navigate(StartPage.class);
    }

    private void addUserAuthorize(String email, String password, long userId) {
        String url = "/authorize/addUser" + "?email=" + email + "&password=" + password + "&userId=" + userId;
        webClient.post().uri(url).retrieve().bodyToMono(Void.class).subscribe();
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
