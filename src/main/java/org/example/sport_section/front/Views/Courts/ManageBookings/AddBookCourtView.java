package org.example.sport_section.front.Views.Courts.ManageBookings;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Courts.Court;
import org.example.sport_section.Models.Images.CourtImage;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Services.ImageService.ImageService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.ImageHelper;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;

@PageTitle("Добавить бронирование")
@Route("courts/info")
public class AddBookCourtView extends HorizontalLayout implements HasUrlParameter<String> {
    private final Div loadingSpinner = createLoadingSpinner();
    private final CourtService courtService;
    private final UserService userService;
    private final BookingCourtService bookingCourtService;
    private final ImageService imageService;
    private Court court;

    @Autowired
    public AddBookCourtView(CourtService courtService, UserService userService,
                            BookingCourtService bookingCourtService, ImageService imageService) {
        this.imageService = imageService;
        this.courtService = courtService;
        this.userService = userService;
        this.bookingCourtService = bookingCourtService;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            int courtId = Integer.parseInt(parameter);
            this.court = courtService.getCourtByIdAsync(courtId).join().get();
            //here
        }
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        getStyle().set("background-color", "#CFCFCF");
        HorizontalLayout mainDiv = new HorizontalLayout();
        mainDiv.getStyle().set("background-color", "#E6E6E6");
        mainDiv.setJustifyContentMode(JustifyContentMode.CENTER);
        mainDiv.setAlignItems(Alignment.CENTER);
        add(loadingSpinner);
        //Court currentCourt = (Court) VaadinSession.getCurrent().getAttribute("court");
        String email = SecurityUtils.getCurrentUserEmail();
        Div div = getDiv(court.getId(), email);
        Div mainContent = getVerticalMainContent();
        mainDiv.add(div, mainContent);
        mainDiv.setHeight("850px");
        mainDiv.setWidth("1200px");
        mainDiv.setPadding(true);
        mainDiv.getStyle().set("border-radius", "15px");
        mainDiv.getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)");
        remove(loadingSpinner);
        add(mainDiv);
    }

    public Div getVerticalMainContent() {
        Div res = new Div();
        VerticalLayout vl = new VerticalLayout();
        Button homeButton = new Button("На главную");
        homeButton.getStyle().set("background-color", "#F2F3F4")
                .set("padding", "10px")
                .set("border-radius", "8px")
                .set("box-shadow", "0px 2px 4px rgba(0, 0, 0, 0.1)")
                .set("color", "black");
        homeButton.setWidth("200px");
        homeButton.setHeight("65px");
        homeButton.addClickListener(event -> {
            UI.getCurrent().navigate(HomePage.class);
        });
        Image courtImage = getImageForCourt(court);
        courtImage.getElement().getStyle().set("border-radius", "15px");
        courtImage.getElement().getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)");
        courtImage.setWidth("700px");
        courtImage.setHeight("600px");

        Text textTitle = new Text("Аренда корта");
        Div titleContainer = new Div(textTitle);
        titleContainer.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("text-decoration", "underline")
                .set("font-family", "Arial, sans-serif")
                .set("margin-bottom", "20px");
        vl.add(titleContainer, courtImage, homeButton);
        res.add(vl);
        return res;
    }

    public Div getDiv(int courtId, String email) {
        Div div = new Div();
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setAlignItems(Alignment.CENTER);
        horizontalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Text text = new Text("Выберите удобную дату и время");
        DatePicker datePicker = getDatePicker(courtId, email);
        horizontalLayout.add(text, datePicker);
        div.add(horizontalLayout);
        return div;
    }

    public DatePicker getDatePicker(int courtId, String email) {
        DatePicker datePicker = new DatePicker();
        datePicker.open();
        Dialog timeDialog = new Dialog();
        VerticalLayout timeLayout = new VerticalLayout();
        timeDialog.add(timeLayout);

        final LocalDate[] selectedDate = new LocalDate[1];
        final Time[] selectedHour = new Time[1];

        // Установка минимальной даты в DatePicker
        datePicker.setMin(LocalDate.now());

        datePicker.addValueChangeListener(event -> {
            selectedDate[0] = event.getValue();
            if (selectedDate[0] != null) {
                List<Time> aviableHours = getAviableHours(courtId, selectedDate[0]);

                UI.getCurrent().access(() -> {
                    timeLayout.removeAll();
                    timeLayout.add(new Text("Выберите доступное время для " + selectedDate[0]));
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    for (Time hour : aviableHours) {
                        Button timeButton = new Button(sdf.format(hour));
                        timeButton.addClickListener(e -> {
                            selectedHour[0] = hour;
                            Notification.show("Выбрано " + sdf.format(hour),  3000, Notification.Position.MIDDLE);
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

                    Button bookButton = new Button("Забронировать", bookEvent -> {
                        if (selectedDate[0] != null && selectedHour[0] != null) {
                            Notification.show("Выполняется бронирование " + selectedDate[0] +  " " + sdf.format(selectedHour[0]), 3000, Notification.Position.MIDDLE);
                            System.out.println("email: " + email);
                            Optional<User> userOpt = getUser(email);
                            if (!userOpt.isPresent()) {
                                //todo
                            }
                            User user = userOpt.get();
                            try {
                                bookCourt(selectedDate[0], selectedHour[0], courtId, user);
                                Notification.show("Бронирование успешно создано!",  3000, Notification.Position.MIDDLE);
                                UI.getCurrent().navigate(HomePage.class);
                                timeDialog.close();
                            } catch (IllegalStateException e) {
                                UI.getCurrent().access(() -> {
                                    Notification.show(e.getMessage(),  3000, Notification.Position.MIDDLE);
                                    timeDialog.close();
                                });
                            }
                        }
                    });
                    Button cancelButton = new Button("Отмена", bookEvent -> {
                        timeDialog.close();
                    });
                    timeLayout.add(bookButton);
                    timeLayout.add(cancelButton);
                    timeDialog.open();
                });
            } else {
                Notification.show("Выберите время", 3000, Notification.Position.MIDDLE);
            }
        });
        return datePicker;
    }

    private List<Time> getAviableHours(int courtId, LocalDate date) {
        //LocalDate localDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
        return bookingCourtService.getAviavleTimeForCourtAsync(courtId, date).join();
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

    private Optional<User> getUser(String email) {
        return userService.getUserAsync(email).join();
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
    private Image getImageForCourt(Court court) {
        CourtImage imageData = court.getCourtImage();
        if (imageData != null) {
            Image image = ImageHelper.createImageFromByteArray(imageData.getImage_data(), "описание");
            return image;
        }
        Image image = new Image();
        image.add("Изображения для корта пока нет");
        return image;
    }
}
