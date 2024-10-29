package org.example.sport_section.front.Views.Courts;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Booking_court;
import org.example.sport_section.Models.Court;
import org.example.sport_section.Models.CourtImage;
import org.example.sport_section.Models.User;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.CourtService.CourtService;
import org.example.sport_section.Services.ImageService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.ImageHelper;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Route("courts/info")
public class CourtPageInfo extends HorizontalLayout implements HasUrlParameter<String> {
    private final Div loadingSpinner = createLoadingSpinner();
    private final CourtService courtService;
    private final UserService userService;
    private final BookingCourtService bookingCourtService;
    private final ImageService imageService;
    private int courtId;

    @Autowired
    public CourtPageInfo(CourtService courtService, UserService userService,
                         BookingCourtService bookingCourtService, ImageService imageService) {
        this.imageService = imageService;
        this.courtService = courtService;
        this.userService = userService;
        this.bookingCourtService = bookingCourtService;
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (parameter != null) {
            this.courtId = Integer.parseInt(parameter);
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
        Div div = getDiv(courtId, email);
        Div mainContent = getVerticalMainContent();
        mainDiv.add(div, mainContent);
        mainDiv.setHeight("850px");
        mainDiv.setWidth("1200px");
        mainDiv.setPadding(true);
        mainDiv.getStyle().set("border-radius", "15px");
        mainDiv.getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)");
        add(mainDiv);
    }

    public Div getVerticalMainContent() {
        Div res = new Div();
        VerticalLayout vl = new VerticalLayout();
        //Court currentCourt = (Court) VaadinSession.getCurrent().getAttribute("court");
        Image courtImage = getImageForCourt(courtId);
        courtImage.getElement().getStyle().set("border-radius", "15px");
        courtImage.getElement().getStyle().set("box-shadow", "0 4px 8px rgba(0, 0, 0, 0.2)");
//        courtImage.getElement().getStyle().set("width", "100%");
//        courtImage.getElement().getStyle().set("height", "auto");
        courtImage.setWidth("700px");
        courtImage.setHeight("700px");
        remove(loadingSpinner);
        Text textTitle = new Text("Аренда корта");
        Div titleContainer = new Div(textTitle);
        titleContainer.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("text-decoration", "underline")
                .set("font-family", "Arial, sans-serif")
                .set("margin-bottom", "20px");
        vl.add(titleContainer, courtImage);
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
        final Integer[] selectedHour = new Integer[1];

        datePicker.setMin(LocalDate.now());  // Установить минимальную дату на сегодняшний день

        datePicker.addValueChangeListener(event -> {
            selectedDate[0] = event.getValue();
            if (selectedDate[0] != null) {
                List<Integer> aviableHours = getAviableHours(courtId, selectedDate[0]);

                UI.getCurrent().access(() -> {
                    timeLayout.removeAll();
                    timeLayout.add(new Text("Выберите доступное время для " + selectedDate[0]));
                    for (Integer hour : aviableHours) {
                        Button timeButton = new Button(hour + ":00");
                        timeButton.addClickListener(e -> {
                            selectedHour[0] = hour;
                            Notification.show("Выбрано " + hour + ":00",  3000, Notification.Position.MIDDLE);
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
                            Notification.show("Выполняется бронирование " + selectedDate[0] +  " " + selectedHour[0] + ":00", 3000, Notification.Position.MIDDLE);
                            System.out.println("email: " + email);
                            User user = getUser(email);
                            try {
                                bookCourt(selectedDate[0], selectedHour[0], courtId, user.getId());
                                Notification.show("Бронирование успешно создано!",  3000, Notification.Position.MIDDLE);
                                UI.getCurrent().navigate(HomePage.class);
                                timeDialog.close();
                            } catch (IllegalStateException e) {
                                UI.getCurrent().access(() -> {
                                    Notification.show("Корт уже забронирован на это время.",  3000, Notification.Position.MIDDLE);
                                    timeDialog.close();
                                });
                            } catch (SQLException e) {
                                //todo
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

    private Long bookCourt(LocalDate date, int hour, int courtId, int id) throws SQLException {
        Optional<Court> court = courtService.getCourtByIdAsync(courtId).join();
        if (court.isPresent()) {
            Booking_court bk = new Booking_court(court.get(), id, Date.valueOf(date), hour);
            return bookingCourtService.addBookingTimeForCourt(bk).join();
        }
        throw new SQLException();
    }

    private User getUser(String email) {
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
    private Image getImageForCourt(int courtId) {
        CourtImage imageData = imageService.getImageByCourtId(courtId).join();
        Image image = ImageHelper.createImageFromByteArray(imageData.getImage_data(), "описание");
        return image;
    }
}
