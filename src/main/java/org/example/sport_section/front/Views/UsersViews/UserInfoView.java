package org.example.sport_section.front.Views.UsersViews;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Groups.Group;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.GroupService.GroupService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.CompletionException;
@PageTitle("Информация о пользователе")
@Route("admin/users/info")
public class UserInfoView extends VerticalLayout implements HasUrlParameter<Integer> {
    private int userId;
    private final UserService userService;
    private final GroupService groupService;
    @Autowired
    public UserInfoView(UserService userService, GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }
    @Override
    public void setParameter(BeforeEvent beforeEvent, Integer parameter) {
        if (parameter != null) {
            this.userId = parameter;
        } else {
            //todo
        }
        Optional<User> userOpt = userService.getUserAsync(userId).join();
        if (userOpt.isEmpty()) {
            //todo
        }
        List<Group> allGroups = groupService.getGroups().join();
        User user = userOpt.get();
        List<Booking_court> bookings = user.getBookings();
        Collections.sort(bookings, new Comparator<Booking_court>() {
            @Override
            public int compare(Booking_court booking1, Booking_court booking2) {
                return booking2.getDate().compareTo(booking1.getDate());
            }
        });

        FlexLayout container = new FlexLayout();
        container.setSizeFull();

        // Левая панель с информацией о пользователе
        VerticalLayout userInfoLayout = new VerticalLayout();
        userInfoLayout.add(new Span("Имя: " + user.getFirst_name()));
        userInfoLayout.add(new Span("Фамилия: " + user.getLast_name()));
        userInfoLayout.add(new Span("Телефон: " + user.getPhone()));
        userInfoLayout.add(new Span("Почта: " + user.getEmail()));
        userInfoLayout.add(new Text("Должности: "));
        userInfoLayout.add(new Text("Пользователь  "));
        if (user.getCoach() != null )userInfoLayout.add(new Text("Тренер  "));
        if (user.getAdmin() != null )userInfoLayout.add(new Text("Администратор  "));


        userInfoLayout.add(new Span("Группа: " + ((user.getGroup() == null) ? "Нет" : user.getGroup().getAllInfo())));
        userInfoLayout.add(new Span("Тренер в группе: " + ((user.getGroup() == null) ? "Нет" : user.getGroup().getCoach().getUser().getLast_name())));

        ComboBox<Group> groupComboBox = (user.getGroup() == null) ? new ComboBox<>("Записать в группу") : new ComboBox<>("Перезаписать в группу");
        groupComboBox.setItems(allGroups);
        groupComboBox.setItemLabelGenerator(Group::getAllInfo);
        userInfoLayout.add(groupComboBox);

        Button ok = new Button("OK");
        ok.addClickListener(e -> {
            Group group = groupComboBox.getValue();
            if (group == null) {
                Notification.show("Выберите группу для (пере)записи", 1000, Notification.Position.MIDDLE);
                return;
            }
            addIntoGroup(userId, group.getId());
        });
        userInfoLayout.add(ok);
        if (user.getGroup() != null) {
            Button deleteGroup = new Button("Выписать из группы");
            deleteGroup.addClickListener(e -> {
                confirm();
            });
            userInfoLayout.add(deleteGroup);
        }
        userInfoLayout.addClassName("user-info");
        Button back = new Button("Назад");
        back.addClickListener(e -> {
            UI.getCurrent().navigate(AllUsersView.class);
        });
        userInfoLayout.add(back);

        userInfoLayout.setWidth("30%");
        userInfoLayout.setHeightFull();

        // Правая панель с бронированиями
        Text text = new Text("Бронирования пользователя:");
        VerticalLayout vl = new VerticalLayout();
        vl.add(text);
        vl.setHeightFull();
        vl.setWidth("75%");
        if (bookings.isEmpty()) {
            setHeightFull();
            vl.add(new Text("Нет бронирований"));
        }
        if (bookings.size() < 5) {
            setHeightFull();
        }
        for (Booking_court booking : bookings) {
            vl.add(createCourtCard(booking));
        }

        // Добавляем в основной контейнер
        container.add(userInfoLayout, vl);
        add(container);

        // Стилирование
        setStyles();
    }

    private void setStyles() {
        getElement().getStyle().set("background-color", "#f0f0f0"); // общий фон

        getElement().executeJs(
                "const style = document.createElement('style');" +
                        "style.innerHTML = " +
                        "'.user-info{ background-color: white; padding: 15px; box-shadow: 0 0 10px rgba(0,0,0,0.1);}'" +
                        " + '.booking-grid{ background-color: white; padding: 15px; box-shadow: 0 0 10px rgba(0,0,0,0.1);}'" +
                        "document.head.appendChild(style);" +
                        "});"
        );

    }
    private HorizontalLayout createCourtCard(Booking_court booking) {
        HorizontalLayout card = new HorizontalLayout();
        card.getStyle().set("background-color", "#FFFFFF")
                .set("padding", "15px")
                .set("border-radius", "10px")
                .set("box-shadow", "0px 4px 8px rgba(0, 0, 0, 0.1)") // Увеличиваем тень для более выраженного эффекта
                .set("margin", "10px 0") // Добавляем отступы между карточками
                .set("color", "#333"); // Возможно, лучше выбрать немного более светлый цвет текста для контраста с фоном
        card.setWidth("90%");
        card.setHeight("95px"); // Делаем высоту автоматической, чтобы текст не обрезался
        card.setPadding(true); // Устанавливаем отступы внутри карточки
        card.setAlignItems(FlexComponent.Alignment.CENTER); // Центрируем элементы по вертикали

        Text date = new Text(booking.getDate().toString() + "  ");
        Text hour = new Text(booking.getTime() + ":00  ");
        Text name = new Text(booking.getCourt().getCourtName());
        card.add(date, hour, name);
        return card;
    }
    private void confirm() {
        // Создаём диалоговое окно
        Dialog dialog = new Dialog();

        Text text = new Text("Выписать пользователя из группы?");
        Button proveButton = new Button("Выписать");
        proveButton.getStyle().set("background-color", "lightgray");
        proveButton.getStyle().set("color", "white");
        proveButton.addClickListener(event -> {
            addIntoGroup(userId, null);
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
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

    public void addIntoGroup(int userId, Integer groupId) {
        try {
            UI.getCurrent().access(() -> {
                Notification.show("Выполняется", 1000, Notification.Position.MIDDLE);
            });
            userService.addUserIntoGroup(userId, groupId).join();
            UI.getCurrent().access(() -> {
                Notification.show("Успешно", 1000, Notification.Position.MIDDLE);
                UI.getCurrent().getPage().reload();
            });
        } catch (CompletionException ex) {
            UI.getCurrent().access( () -> {
                Notification.show("Ошибка: " + ex.getMessage(), 2000, Notification.Position.MIDDLE);
            });
        }
    }
}
