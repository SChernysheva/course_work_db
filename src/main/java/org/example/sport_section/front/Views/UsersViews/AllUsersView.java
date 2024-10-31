package org.example.sport_section.front.Views.UsersViews;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletionException;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;

@Route("admin/allUsers")
public class AllUsersView extends HorizontalLayout {
    private final UserService userService;
    private final Div loadingSpinner = createLoadingSpinner();
    private List<User> allUsers;

    @Autowired
    public AllUsersView(UserService userService) {
        this.userService = userService;
        add(loadingSpinner);
        setPadding(false);
        getStyle().set("background-color", "#F2F3F4");
        getStyle().setHeight("auto");
        allUsers = userService.getUsersAsync().join();

        UI.getCurrent().access(() -> {
            addSidebar();
            remove(loadingSpinner);
            add(loadContent(allUsers)); // Передаем все пользователи
        });
    }

    private VerticalLayout loadContent(List<User> users) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle().set("background-color", "#F2F3F4");

        // Добавление поиска по пользователям
        TextField searchField = new TextField();
        searchField.setPlaceholder("Поиск по фамилии...");
        searchField.setWidth("80%");
        layout.add(searchField);
        searchField.addValueChangeListener(event -> {
            String filter = event.getValue().toLowerCase();
            List<User> filteredUsers = allUsers.stream()
                    .filter(user -> user.getLast_name().toLowerCase().contains(filter))
                    .toList();
            layout.removeAll();
            if (filteredUsers.isEmpty()) {
                layout.add(searchField);
                layout.add(new Text("Нет совпадающих пользователей"));
            } else {
                layout.add(searchField);
                for (var user : filteredUsers) {
                    layout.add(createUserCard(user));
                }
            }
        });


        if (users.isEmpty()) {
            layout.setHeightFull();
            Text text = new Text("пока нет пользователей");
            layout.add(text);
            setHeightFull();
            return layout;
        }
        if (users.size() < 5) {
            setHeightFull();
        }
        for (var user : users) {
            layout.add(createUserCard(user));
        }
        layout.setPadding(false);
        return layout;
    }

    private void addSidebar() {
        // Создаем и добавляем боковую панель
        VerticalLayout sidebar = createSidebarView(AllUsersView.class, UI.getCurrent());
        String email = SecurityUtils.getCurrentUserEmail();
        sidebar.add(createSidebarViewUser(email));
        sidebar.add(getExitButton());
        add(sidebar);
    }

    // Остальные методы...

    private Button getExitButton() {
        Button exitButton = new Button("Выйти");
        exitButton.addClickListener(e -> {
            exit();
        });
        return exitButton;
    }
    private void exit() {
        // Создаём диалоговое окно
        Dialog dialog = new Dialog();

        Text text = new Text("Вы точно хотите выйти?");
        Button proveButton = new Button("Выйти");
        proveButton.getStyle().set("background-color", "lightgray");
        proveButton.getStyle().set("color", "white");
        proveButton.addClickListener(event -> {
            SecurityUtils.deleteAuth();
            VaadinSession.getCurrent().close(); // Закройте текущую сессию
            // UI.getCurrent().navigate(StartPage.class);
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
            // Можем (необязательно) добавить логику возврата на домашнюю страницу
            UI.getCurrent().navigate(AllUsersView.class);
        });

        VerticalLayout layout = new VerticalLayout(text, proveButton, cancelButton);
        layout.setAlignItems(Alignment.CENTER); // Выравнивание по центру
        layout.setJustifyContentMode(JustifyContentMode.CENTER); // Вертикальное выравнивание по центру
        layout.setSizeFull(); // Занять всю доступную область

        dialog.add(layout); // Добавляем вёрстку в диалоговое окно
        dialog.setWidth("400px"); // Настройка ширины диалогового окна
        dialog.setHeight("200px"); // Настройка высоты диалогового окна
        dialog.open(); // Открываем диалоговое окно
    }
    private VerticalLayout createSidebarViewUser(String userEmail) {
        VerticalLayout sidebar = new VerticalLayout();
        // Создание контейнера с информацией о пользователе
        Div userContainer = new Div();
        userContainer.getStyle().set("background-color", "lightgray");
        userContainer.getStyle().set("padding", "5px");

        // Добавление текста с информацией о пользователе
        Span userText = new Span("Пользователь: " + userEmail);
        userContainer.add(userText);
        // Добавление контейнера с информацией о пользователе на боковую панель
        sidebar.add(userContainer);
        sidebar.setAlignItems(Alignment.CENTER); // Центрирование по вертикали
        sidebar.setWidth("200px"); // Задание ширины боковой панели

        return sidebar;
    }
    private HorizontalLayout createUserCard(User user) {
        HorizontalLayout card = new HorizontalLayout();
        card.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "15px")
                .set("border-radius", "10px")
                .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)") // Увеличиваем тень для более выраженного эффекта
                .set("margin", "10px 0") // Добавляем отступы между карточками
                .set("color", "#333"); // Возможно, лучше выбрать немного более светлый цвет текста для контраста с фоном
        card.setWidth("90%");
        card.setHeight("150px"); // Делаем высоту автоматической, чтобы текст не обрезался
        card.setPadding(true); // Устанавливаем отступы внутри карточки
        card.setAlignItems(FlexComponent.Alignment.CENTER); // Центрируем элементы по вертикали

        Text lastName = new Text(user.getLast_name() + "  ");
        Text firstName = new Text(user.getFirst_name() + "  ");
        Text email = new Text(user.getEmail() + "  ");
        Text phone = new Text(user.getPhone() + "  ");
        Button infoButton = new Button("Подробная информация");
        infoButton.addClickListener(e -> {
            goToUserInfo(user);
        });
        card.add(lastName, firstName, email, phone, infoButton);
        if (user.getAdmin() == null) {
            Button adminButton = new Button("Сделать администратором");
            adminButton.addClickListener(e -> {
                addAdmin(user);
            });
            card.add(adminButton);
        } else {
            Text text = new Text("Администратор");
            //text.getStyle().set("color", "red");
            card.add(text);
        }


        return card;
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
    public void goToUserInfo(User user) {
        UI.getCurrent().navigate("admin/users/info/" + user.getId());
    }
    private void addAdmin(User user) {
        // Создаём диалоговое окно
        Dialog dialog = new Dialog();

        Text text = new Text("Сделать пользователя " + user.getFirst_name() + " администратором?");
        Button proveButton = new Button("Да");
        proveButton.getStyle().set("background-color", "lightgray");
        proveButton.getStyle().set("color", "white");
        proveButton.addClickListener(event -> {
            UI.getCurrent().access( () -> {
                Notification.show("Выполняется...", 1000, Notification.Position.MIDDLE);
            });
            try {
                userService.addAdminAsync(user.getId()).join();
                UI.getCurrent().access( () -> {
                    Notification.show("Готово!", 1000, Notification.Position.MIDDLE);
                    dialog.close();
                });
            } catch (CompletionException ex) {
                UI.getCurrent().access( () -> {
                    Notification.show("Ошибка: пользователь уже администратор", 1000, Notification.Position.MIDDLE);
                    dialog.close();
                });
            }
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
            // Можем (необязательно) добавить логику возврата на домашнюю страницу
            UI.getCurrent().navigate(AllUsersView.class);
        });

        VerticalLayout layout = new VerticalLayout(text, proveButton, cancelButton);
        layout.setAlignItems(Alignment.CENTER); // Выравнивание по центру
        layout.setJustifyContentMode(JustifyContentMode.CENTER); // Вертикальное выравнивание по центру
        layout.setSizeFull(); // Занять всю доступную область

        dialog.add(layout); // Добавляем вёрстку в диалоговое окно
        dialog.setWidth("400px"); // Настройка ширины диалогового окна
        dialog.setHeight("200px"); // Настройка высоты диалогового окна
        dialog.open(); // Открываем диалоговое окно
    }
}
