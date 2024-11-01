package org.example.sport_section.front.Views.Courts.UserBookings;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
@PageTitle("Добавить бронирование")
@Route("admin/addBooking")
public class AddBookingView extends VerticalLayout {
    private final UserService userService;
    private final BookingCourtService bookingCourtService;
    private final CourtService courtService;

    @Autowired
    public AddBookingView(UserService userService, BookingCourtService bookingCourtService, CourtService courtService) {
        this.userService = userService;
        this.bookingCourtService = bookingCourtService;
        this.courtService = courtService;
        List<User> allUsers = userService.getUsersAsync().join();
        List<Court> allCourts = courtService.getCourtsAsync().join();
        add(LoadContent(allUsers, allCourts));
        Button homeButton = new Button("На главную");
        homeButton.addClickListener(e -> {
            UI.getCurrent().navigate(HomePage.class);
        });
        homeButton.getStyle().set("background-color", "#F2F3F4")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");
        homeButton.setWidth("200px");
        homeButton.setHeight("65px");
        add(homeButton);

    }

    private VerticalLayout LoadContent(List<User> allUsers, List<Court> allCourts) {
        VerticalLayout content = new VerticalLayout();
        // Создание комбобоксов
        ComboBox<User> userComboBox = new ComboBox<>("Выберите пользователя");
        userComboBox.setItems(allUsers);
        userComboBox.setItemLabelGenerator(user -> user.getLast_name() + " (" + user.getEmail() + ", " + user.getPhone() + ")");

        ComboBox<Court> courtComboBox = new ComboBox<>("Выберите корт");
        courtComboBox.setItems(allCourts);
        courtComboBox.setItemLabelGenerator(Court::getCourtName);


        DatePicker datePicker = new DatePicker();
        Dialog timeDialog = new Dialog();
        VerticalLayout timeLayout = new VerticalLayout();

        final LocalDate[] selectedDate = new LocalDate[1];
        final Time[] selectedHour = new Time[1];
        datePicker.setMin(LocalDate.now());

        datePicker.addValueChangeListener(event -> {
            if (courtComboBox.getValue() == null) {
                Notification.show("Сначала выберите корт", 3000, Notification.Position.MIDDLE);
                return;
            }
            selectedDate[0] = event.getValue();
            if (selectedDate[0] != null) {
                List<Time> aviableHours = bookingCourtService.getAviavleTimeForCourtAsync(courtComboBox.getValue().getId(),
                        selectedDate[0]).join();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                UI.getCurrent().access(() -> {
                    timeLayout.removeAll();
                    timeLayout.add(new Text("Выберите доступное время для " + selectedDate[0]));
                    for (Time hour : aviableHours) {
                        Button timeButton = new Button(sdf.format(hour));
                        timeButton.addClickListener(e -> {
                            selectedHour[0] = hour;
                            Notification.show("Выбрано " + sdf.format(hour), 3000, Notification.Position.MIDDLE);
                            timeLayout.getChildren()
                                    .filter(component -> component instanceof Button)
                                    .map(component -> (Button) component)
                                    .forEach(button -> {
                                        button.getStyle().remove("background-color");
                                        button.getStyle().remove("color");
                                    });
                            // Установить стиль для выбранной кнопки
                            timeButton.getStyle().set("background-color", "#808080");
                            timeButton.getStyle().set("color", "white");
                        });
                        timeLayout.add(timeButton);
                    }
                    Button ok = new Button("OK");
                    ok.addClickListener(e -> {
                        if (selectedHour[0] == null) {
                            Notification.show("Выберите время", 3000, Notification.Position.MIDDLE);
                            return;
                        }
                        timeDialog.close();
                        Text time = new Text("Выбранное время: " + sdf.format(selectedHour[0]));
                        content.add(time);
                    });
                    timeLayout.add(ok);
                    timeDialog.add(timeLayout);
                    timeDialog.open();
                });
            }
        });

        Button processButton = new Button("Забронировать", e -> {
            User selectedUser = userComboBox.getValue();
            Court selectedCourt = courtComboBox.getValue();

            if (selectedUser == null || selectedCourt == null || selectedDate[0] == null || selectedHour[0] == null) {
                Notification.show("Пожалуйста, заполните все поля выбора.");
                return;
            }

            // Обработка выбранных значений
            try {
                bookCourt(selectedDate[0], selectedHour[0], selectedCourt.getId(), selectedUser);
                Notification.show("Бронирование успешно создано!",  3000, Notification.Position.MIDDLE);
                UI.getCurrent().navigate(HomePage.class);
                timeDialog.close();
            } catch (IllegalStateException ex) {
                UI.getCurrent().access(() -> {
                    Notification.show(ex.getMessage(),  3000, Notification.Position.MIDDLE);
                    timeDialog.close();
                });
            }
        });
        content.add(userComboBox, courtComboBox, datePicker, processButton);
        return content;
    }

    private void bookCourt(LocalDate date, Time hour, int courtId, User user) throws IllegalStateException {
        Optional<Court> court = courtService.getCourtByIdAsync(courtId).join();
        if (court.isPresent()) {
            Booking_court bk = new Booking_court(court.get(), user, Date.valueOf(date), hour);
            try {
                bookingCourtService.addBookingTimeForCourt(bk).join();
            } catch (CompletionException e) {
                throw new IllegalStateException("Ошибка: корт уже забронирован на это время");
            }
        } else {
            UI.getCurrent().access(() -> {
                throw new IllegalStateException("Ошибка: корт не существует или не доступен");
            });
        }
    }
}
