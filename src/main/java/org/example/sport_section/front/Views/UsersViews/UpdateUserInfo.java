package org.example.sport_section.front.Views.UsersViews;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.checkerframework.checker.units.qual.N;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Validators.UserValidator;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.validation.Validator;
import java.util.Optional;
import java.util.concurrent.CompletionException;

@Route("user/update")
public class UpdateUserInfo extends VerticalLayout implements HasUrlParameter<Integer> {

    private final Div loadingSpinner = createLoadingSpinner();
    private final UserService userService;
    private User user;

    @Autowired
    public UpdateUserInfo(UserService userService) {
        this.userService = userService;

        // Устанавливаем стили для VerticalLayout
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f0f0f0") // Светло-серый фон
                .set("padding", "20px")
                .set("height", "100vh");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer parameter) {
        if (parameter == null) {
            //todo
            return;
        }
        Integer userId = parameter;
        UI.getCurrent().access(() -> add(loadingSpinner));
        Optional<User> userOpt = userService.getUserAsync(userId).join();
        if (!userOpt.isPresent()) {
            //todo
            return;
        }
        user = userOpt.get();

        // Создаем поля и кнопки
        TextField emailField = new TextField("Email");
        emailField.setValue(user.getEmail());
        emailField.setWidth("300px"); // Устанавливаем ширину
        Text name = new Text(user.getFirst_name()+ "  ");
        Text lastName = new Text(user.getLast_name());
        TextField phone = new TextField("Номер телефона");
        phone.setValue(user.getPhone());
        phone.setWidth("300px"); // Устанавливаем ширину
        Button okButton = new Button("Применить изменения", e -> apply(emailField.getValue(), phone.getValue()));
        okButton.getStyle().set("background-color", "#888888")  // Серый фон кнопки
                .set("color", "#ffffff")               // Белый текст
                .set("border", "none")                 // Убираем рамку
                .set("padding", "10px 20px")           // Отступы
                .set("border-radius", "5px");          // Закругленные углы
        Button backButton = new Button("На главную", e ->
                UI.getCurrent().navigate(HomePage.class));
        backButton.getStyle().set("background-color", "#888888")  // Серый фон кнопки
                .set("color", "#ffffff")               // Белый текст
                .set("border", "none")                 // Убираем рамку
                .set("padding", "10px 20px")           // Отступы
                .set("border-radius", "5px");

        // Помещаем элементы в контейнер
        add(name, lastName, emailField, phone, okButton, backButton);

        UI.getCurrent().access(() -> remove(loadingSpinner));
    }

    private void apply(String email, String phone) {
        add(loadingSpinner);
        String oldEmail = user.getEmail();
        if (email.equals(user.getEmail()) && phone.equals(user.getPhone())) {
            remove(loadingSpinner);
            return;
        }
        if (!email.equals(user.getEmail())) {
            String resValidate = UserValidator.validateEmail(email);
            if (resValidate != null) {
                UI.getCurrent().access(() -> Notification.show("Ошибка: " + resValidate, 3000, Notification.Position.MIDDLE));
                remove(loadingSpinner);
                return;
            } else {
                user.setEmail(email);
            }
        }
        if (!phone.equals(user.getPhone())) {
            String resValidate = UserValidator.validatePhone(phone);
            if (resValidate != null) {
                UI.getCurrent().access(() -> Notification.show("Ошибка: " + resValidate, 3000, Notification.Position.MIDDLE));
                remove(loadingSpinner);
                return;
            } else {
                user.setPhone(phone);
            }
        }
        updateUser(user, oldEmail);
    }

    private Div createLoadingSpinner() {
        Div spinner = new Div();
        spinner.add(new Span("Loading..."));
        spinner.getStyle().set("display", "flex")
                .set("width", "100%")
                .set("height", "100%")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", "#666"); // Темно-серый цвет текста
        return spinner;
    }
    private void updateUser(User user, String oldEmail) {
        UI.getCurrent().access(() -> Notification.show("Выполняется", 1000, Notification.Position.MIDDLE));
        try {
            userService.updateUser(user.getId(), user, oldEmail).join();
            UI.getCurrent().access(() -> {
                Notification.show("Готово", 1000, Notification.Position.MIDDLE);
                UI.getCurrent().getPage().reload();
            });
        } catch (CompletionException ex) {
            UI.getCurrent().access( () -> {
                Notification.show("Ошибка: " + ex.getCause().getMessage(), 3000, Notification.Position.MIDDLE);
                remove(loadingSpinner);
            });
        }

    }
}

