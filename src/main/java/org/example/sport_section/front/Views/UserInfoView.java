package org.example.sport_section.front.Views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Booking_court;
import org.example.sport_section.Models.User;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.front.Views.AllUsers.AllUsersView;
import org.example.sport_section.front.Views.UserBookings.Bookings;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Route("admin/users/info")
public class UserInfoView extends VerticalLayout implements HasUrlParameter<Integer> {
    private int userId;
    private final UserService userService;
    @Autowired
    public UserInfoView(UserService userService) {
        this.userService = userService;
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
}
