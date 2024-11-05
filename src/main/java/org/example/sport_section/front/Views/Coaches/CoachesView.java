package org.example.sport_section.front.Views.Coaches;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.example.sport_section.Models.Courts.Booking_court;
import org.example.sport_section.Models.Groups.Group;
import org.example.sport_section.Models.Users.Coach;
import org.example.sport_section.Models.Users.User;
import org.example.sport_section.Services.CoachService.CoachService;
import org.example.sport_section.Services.CourtService.BookingCourtService;
import org.example.sport_section.Services.UserService.UserService;
import org.example.sport_section.Utils.Security.SecurityUtils;
import org.example.sport_section.front.Views.Courts.ManageBookings.AddBookingView;
import org.example.sport_section.front.Views.Courts.ManageBookings.ManageBookingsView;
import org.example.sport_section.front.Views.Home.HomePage;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.example.sport_section.front.Views.Sidebar.createSidebarView;


@Route("coaches")
public class CoachesView extends HorizontalLayout {
    private CoachService coachService;
    private UserService userService;
    private final Div loadingSpinner = createLoadingSpinner();
    private User user;

    @Autowired
    public CoachesView(CoachService coachService, UserService userService) {
        this.coachService = coachService;
        this.userService = userService;
        String email = SecurityUtils.getCurrentUserEmail();
        this.user = userService.getUserAsync(email).join().get(); //here
        List<Coach> coaches = coachService.getCoaches().join();
        getStyle().set("background-color", "#F2F3F4");
        getStyle().setHeight("auto");
        UI.getCurrent().access(() -> {
            add(createSidebarView(CoachesView.class, UI.getCurrent(), user));
            remove(loadingSpinner);
            add(loadContent(coaches));
        });
    }

    private VerticalLayout loadContent(List<Coach> coaches) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();
        layout.getStyle().set("background-color", "#F2F3F4");
        if (coaches.isEmpty()) {
            layout.setHeightFull();
            Text text = new Text("пока нет бронирований");
            layout.add(text);
            setHeightFull();
            return layout;
        }

        if (coaches.size() < 5) {
            setHeightFull();
        }
        for (var coach : coaches) {
            layout.add(createCoachCard(coach));
        }
        layout.setPadding(false);
        return layout;
    }

    private HorizontalLayout createCoachCard(Coach coach) {
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

        Text lastName = new Text(coach.getUser().getLast_name() + "  ");
        Text firstName = new Text(coach.getUser().getFirst_name() + "  ");
        Text email = new Text(coach.getUser().getEmail() + "  ");
        card.add(lastName, firstName, email);
        if (SecurityUtils.isAdminOrHigher()) {
            Text phone = new Text(coach.getUser().getPhone());
            card.add(phone);
            Button button = new Button("Показать группы тренера");
            button.addClickListener(e -> {
                getGroupsOfCoach(coach);
            });
            card.add(button);
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

    public void getGroupsOfCoach(Coach coach) {
        Dialog dialog = new Dialog();

        // Получаем список групп тренера
        List<Group> groups = coach.getGroups();

        // Создаем текстовое представление для диалогового окна
        StringBuilder text = new StringBuilder();
        text.append("Группы тренера ").append(coach.getUser().getLast_name()).append(":\n");

        if (groups.isEmpty()) {
            text.append("У тренера нет групп.");
        } else {
            for (Group group : groups) {
                text.append("- ").append(group.getName()).append("\n");
            }
        }

        // Текстовое поле для отображения информации
        TextArea textArea = new TextArea();
        textArea.setValue(text.toString());
        textArea.setReadOnly(true); // Делаем текстовое поле только для чтения
        textArea.setSizeFull(); // Занять всю доступную область в диалоговом окне

        Button cancelButton = new Button("OK");
        cancelButton.addClickListener(event -> {
            dialog.close(); // Закрываем диалоговое окно
        });

        VerticalLayout layout = new VerticalLayout(textArea, cancelButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER); // Выравнивание по центру
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Вертикальное выравнивание по центру
        layout.setSizeFull(); // Занять всю доступную область

        dialog.add(layout); // Добавляем вёрстку в диалоговое окно
        dialog.setWidth("400px"); // Настройка ширины диалогового окна
        dialog.setHeight("200px"); // Настройка высоты диалогового окна
        dialog.open(); // Открываем диалоговое окно
    }

}
