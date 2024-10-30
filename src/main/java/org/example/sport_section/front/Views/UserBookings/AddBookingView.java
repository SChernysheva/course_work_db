package org.example.sport_section.front.Views.UserBookings;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.sport_section.Models.Booking_court;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;

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
        final Integer[] selectedHour = new Integer[1];

        datePicker.addValueChangeListener(event -> {
            if (courtComboBox.getValue() == null) {
                Notification.show("Сначала выберите корт", 3000, Notification.Position.MIDDLE);
                return;
            }
            selectedDate[0] = event.getValue();
            if (selectedDate[0] != null) {
                List<Integer> aviableHours = getAviableHours(courtComboBox.getValue().getId(), selectedDate[0]);

                UI.getCurrent().access(() -> {
                    timeLayout.removeAll();
                    timeLayout.add(new Text("Выберите доступное время для " + selectedDate[0]));
                    for (Integer hour : aviableHours) {
                        Button timeButton = new Button(hour + ":00");
                        timeButton.addClickListener(e -> {
                            selectedHour[0] = hour;
                            Notification.show("Выбрано " + hour + ":00", 3000, Notification.Position.MIDDLE);
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
                        Text time = new Text("Выбранное время: " + selectedHour[0] + ":00");
                        content.add(time);
                    });
                    timeLayout.add(ok);
                    timeDialog.add(timeLayout);
                    timeDialog.open();
                });
            }
        });

        // Получение текущей даты в московском часовом поясе
        ZoneId moscowZoneId = ZoneId.of("Europe/Moscow");
        LocalDate currentDateInMoscow = ZonedDateTime.now(moscowZoneId).toLocalDate();

        // Установка минимальной даты в DatePicker
        datePicker.setMin(currentDateInMoscow);
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
                    Notification.show("Корт уже забронирован на это время.",  3000, Notification.Position.MIDDLE);
                    timeDialog.close();
                });
            } catch (SQLException ex) {
                //todo
            }
        });
        content.add(userComboBox, courtComboBox, datePicker, processButton);
        return content;
    }

    private List<Integer> getAviableHours(int courtId, LocalDate date) {
        List<Integer> availableHours = new ArrayList<>();
        if (LocalDate.now().isAfter(date)) {
            //todo
        }
        int startHour = 7;
        if (date.isEqual(LocalDate.now())) {
            LocalTime currentTime = LocalTime.now();
            startHour = Math.max(7, currentTime.getHour() + 1);
        }
        List<Integer> bookingHours = bookingCourtService.getBookingTimeForCourtAsync(courtId, date).join();
        for (int i = startHour; i <= 22; i++) {
            if (!bookingHours.contains(i)) {
                availableHours.add(i);
            }
        }
        return availableHours;
    }
    private Integer bookCourt(LocalDate date, int hour, int courtId, User user) throws SQLException {
        Optional<Court> court = courtService.getCourtByIdAsync(courtId).join();
        if (court.isPresent()) {
            Booking_court bk = new Booking_court(court.get(), user, Date.valueOf(date), hour);
            return bookingCourtService.addBookingTimeForCourt(bk).join();
        }
        throw new SQLException();
    }
}
